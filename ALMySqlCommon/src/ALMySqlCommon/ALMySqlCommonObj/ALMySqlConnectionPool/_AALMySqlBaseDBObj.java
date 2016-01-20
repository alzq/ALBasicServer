package ALMySqlCommon.ALMySqlCommonObj.ALMySqlConnectionPool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;

import ALMySqlCommon.ALMySqlCommonObj._IALMySqlDBTableCreateObj;
import ALMySqlCommon.ALMySqlCommonObj._IALMySqlDBUpdateObj;
import ALMySqlCommon.ALMySqlDBExcutor.ALMySqlDBExcutor;
import ALMySqlCommon.ALMysqlBasicObj.bo.ALMySql_VersionBO;
import ALMySqlCommon.ALMysqlBasicObj.op.ALMySql_VersionOP;
import ALServerLog.ALServerLog;

/********************
 * 基础的DL数据库对象，抽象类，需要实现部分操作接口，如检查更新，创建等
 * 
 * @author alzq.z
 * @email  zhuangfan@vip.163.com
 * @time   Apr 7, 2013 12:40:00 AM
 */
public abstract class _AALMySqlBaseDBObj
{
    public static final String DRIVER_STR = "com.mysql.jdbc.Driver";
    
    /** 创建对象的管理队列 */
    private ArrayList<_IALMySqlDBTableCreateObj> _m_lCreateObjList;
    /** 保存版本号与升级操作对象的映射关系 */
    private Hashtable<String, _IALMySqlDBUpdateObj> _m_htUpdateObjTable;
    /** 数据库主机连接相关信息 */
    private String _m_sDBServerHost;
    private String _m_sDBName;
    private String _m_sUserName;
    private String _m_sPassword;
    /** 数据库最长保持连接时间MS */
    private long _m_lMaxConnectionKeepTimeMS;
    /** 数据库最大连接数量 */
    private int _m_iMaxConnectionCount;
    
    /** 连接操作锁对象 */
    private ReentrantLock _m_conMutex;
    /** 连接队列 */
    private LinkedList<ALMySqlDBConnectionObj> _m_lConnectionList;
    
    public _AALMySqlBaseDBObj(String _host, String _dbName, String _userName, String _password)
    {
        _m_lCreateObjList = new ArrayList<_IALMySqlDBTableCreateObj>();
        _m_htUpdateObjTable = new Hashtable<String, _IALMySqlDBUpdateObj>();
        
        _m_sDBServerHost = _host;
        _m_sDBName = _dbName;
        _m_sUserName = _userName;
        _m_sPassword = _password;
        _m_lMaxConnectionKeepTimeMS = 60000;
        _m_iMaxConnectionCount = 3;

        _m_conMutex = new ReentrantLock();
        _m_lConnectionList = new LinkedList<ALMySqlDBConnectionObj>();
    }
    public _AALMySqlBaseDBObj(String _host, String _dbName, String _userName, String _password
            , long _maxConnectionKeepTimeMS, int _maxConnectionCount)
    {
        _m_lCreateObjList = new ArrayList<_IALMySqlDBTableCreateObj>();
        _m_htUpdateObjTable = new Hashtable<String, _IALMySqlDBUpdateObj>();
        
        _m_sDBServerHost = _host;
        _m_sDBName = _dbName;
        _m_sUserName = _userName;
        _m_sPassword = _password;
        _m_lMaxConnectionKeepTimeMS = _maxConnectionKeepTimeMS;
        _m_iMaxConnectionCount = _maxConnectionCount;

        _m_conMutex = new ReentrantLock();
        _m_lConnectionList = new LinkedList<ALMySqlDBConnectionObj>();
    }

    public String getDBName() {return _m_sDBName;}
    public long getMaxConnectionKeepTimeMS() {return _m_lMaxConnectionKeepTimeMS;}
    public int getMaxConnectionCount() {return _m_iMaxConnectionCount;}
    
    /******************
     * 初始化数据库连接
     * 
     * @author alzq.z
     * @time   Apr 7, 2013 10:40:22 PM
     */
    public boolean initDBCon()
    {
        _m_conMutex.lock();
        
        try
        {
            //创建足够数量的连接个数
            for(int i = 0; i < _m_iMaxConnectionCount; i++)
            {
                //创建连接对象
                Connection conn = _getNewConnection();
                //加入队列
                _m_lConnectionList.add(new ALMySqlDBConnectionObj(this, conn));
            }
            
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
        finally
        {
            _m_conMutex.unlock();
        }
    }
    
    /***************
     * 从连接池中获取连接对象，最多重试10次
     * 
     * @author alzq.z
     * @time   Jan 14, 2014 12:54:52 AM
     */
    public Connection getConn()
    {
        int tryTime = 0;
        do
        {
            tryTime++;
            
            _m_conMutex.lock();
            
            try
            {
                if(!_m_lConnectionList.isEmpty())
                {
                    //尝试获取缓冲池队列节点
                    ALMySqlDBConnectionObj connectionObj = _m_lConnectionList.pollFirst();
                    
                    return connectionObj.getConn();
                }
            }
            finally
            {
                _m_conMutex.unlock();
            }
            
            //等待100毫秒后重新获取
            try{
                Thread.sleep(100);
            }catch(Exception ex){}
        }
        while(tryTime < 10);
        
        //超出次数，返回空
        return null;
    }
    
    /****************
     * 将缓冲池对象重新放入队列
     * 
     * @author alzq.z
     * @time   Jan 14, 2014 12:59:19 AM
     */
    protected void _pushConnectionObj(ALMySqlDBConnectionObj _connObj)
    {
        _m_conMutex.lock();
        
        try
        {
            _m_lConnectionList.add(_connObj);
        }
        finally
        {
            _m_conMutex.unlock();
        }
    }
    
    /*****************
     * 尝试获取新的连接对象
     * 
     * @author alzq.z
     * @time   Jan 13, 2014 10:53:18 PM
     */
    protected Connection _getNewConnection()
    {
        //开始尝试最多3次重新获取连接
        int tryTime = 0;
        do
        {
            tryTime++;
            
            try
            {
                String driver = "com.mysql.jdbc.Driver";
                Class.forName(driver);
                
                //获取连接的数据库名
                String urlStr = "jdbc:mysql://" + _m_sDBServerHost + "/" + _m_sDBName + "?useUnicode=true&characterEncoding=UTF8";
                
                //创建连接
                Connection conn = DriverManager.getConnection(urlStr, _m_sUserName, _m_sPassword);
                
                return conn;
            }
            catch(SQLException e)
            {
                e.printStackTrace();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            
            //到这里表示出错，等待100毫秒
            try
            {
                Thread.sleep(100);
            }
            catch (InterruptedException e){ }
        }
        while(tryTime < 3);
        
        //返回空
        return null;
    }
    
    /*****************
     * 注册一个数据库版本升级对象
     * 
     * @author alzq.z
     * @time   Apr 7, 2013 10:36:35 PM
     */
    public void regUpdateObj(_IALMySqlDBUpdateObj _updateObj)
    {
        //判断映射表中是否存在已经有的操作对象，有则返回失败
        if(_m_htUpdateObjTable.containsKey(_updateObj.getUpdateSrcVersion()))
        {
            ALServerLog.Fatal(_updateObj.getUpdateSrcVersion() + " has already get a DB update Obj!");
            return ;
        }
        
        //注册到映射表中
        _m_htUpdateObjTable.put(_updateObj.getUpdateSrcVersion(), _updateObj);
    }
    
    /********************
     * 注册创建处理对象到队列中
     * 
     * @author alzq.z
     * @time   Apr 28, 2013 12:31:20 AM
     */
    public void regCreateObj(_IALMySqlDBTableCreateObj _createObj)
    {
        _m_lCreateObjList.add(_createObj);
    }
    
    /**********************
     * 根据已经注册的升级操作对象以及最新版本号进行版本检查并创建版本对象
     * 
     * @author alzq.z
     * @time   Apr 7, 2013 10:00:37 PM
     */
    public boolean checkVersion()
    {
        //获取当前版本信息
        String dbVersion = ALMySql_VersionOP.getVersionInfo(this);

        //无数据，表明当前数据库仍然未创建，因此需要进行创建
        if(null == dbVersion)
        {
            //创建表
            if(!_createCurVersionDb())
            {
                //当创建数据库失败
                ALServerLog.Fatal("DB: " + _m_sDBName + " Create DB Fail!");
                return false;
            }
            
            //创建版本表
            _createVersionTable();
            //插入版本数据
            ALMySql_VersionBO bo = new ALMySql_VersionBO();
            bo.setVersion(getCurVersion());
            
            ALMySqlDBExcutor.InsertBO(this, bo);
            
            return true;
        }
        else if(!dbVersion.equals(getCurVersion()))
        {
            //当版本号不符时尝试升级
            while(!dbVersion.equals(getCurVersion()))
            {
                //获取对应的升级处理对象
                _IALMySqlDBUpdateObj updateObj = _m_htUpdateObjTable.get(dbVersion);
                
                //当查询不到升级对象，则表示处理失败
                if(null == updateObj)
                {
                    ALServerLog.Fatal("Can not find update Object for version: " + dbVersion);
                    return false;
                }
                
                //进行处理操作
                if(!updateObj.update(this))
                {
                    ALServerLog.Fatal("Update DB from: " + dbVersion + " to: " + updateObj.getUpdateTarVersion() + " Fail!");
                    return false;
                }
                
                //更新版本号
                ALMySql_VersionOP.setVersionInfo(this, updateObj.getUpdateTarVersion());
                
                //设置版本号为新版本号
                dbVersion = updateObj.getUpdateTarVersion();
            }
            
            //当出了循环表示已经切换到最新版本了
            return true;
        }
        else
        {
            //版本号相同直接返回true
            return true;
        }
    }
    
    /****************
     * 创建最新版本的直接操作对象
     * 
     * @author alzq.z
     * @time   Apr 7, 2013 10:00:07 PM
     */
    protected final boolean _createCurVersionDb()
    {
        //逐个执行创建操作
        for(int i = 0; i < _m_lCreateObjList.size(); i++)
        {
            _IALMySqlDBTableCreateObj createObj = _m_lCreateObjList.get(i);
            
            //执行创建操作
            if(!createObj.create(this))
            {
                ALServerLog.Fatal(createObj.toString() + " Create Object Deal Fail!");
                return false;
            }
        }
        
        return true;
    }
    
    /********************
     * 创建版本表
     * 
     * @author alzq.z
     * @time   Jan 14, 2014 1:33:57 AM
     */
    protected void _createVersionTable()
    {
        ALMySqlDBExcutor.execute(
                "CREATE TABLE `info_version`(`id` INT(11) AUTO_INCREMENT PRIMARY KEY, `version` VARCHAR(256));", this);
    }
    
    /****************
     * 获取当前最新版本的版本信息对象
     * 
     * @author alzq.z
     * @time   Apr 7, 2013 10:00:07 PM
     */
    public abstract String getCurVersion();
}
