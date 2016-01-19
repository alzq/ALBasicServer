package ALMySqlCommon.ALMySqlCommonObj.ALMySqlConnectionPool;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;

import ALBasicCommon.ALBasicCommonFun;

/********************
 * 数据库连接对象的信息存储对象
 * 
 * @author alzq.z
 * @email  zhuangfan@vip.163.com
 * @time   Jan 14, 2014 12:09:13 AM
 */
public class ALMySqlDBConnectionObj implements InvocationHandler
{
    private final static String CLOSE_METHOD_NAME = "close";
    
    /** 对应的数据库信息对象 */
    private _AALMySqlBaseDBObj _m_doDBObj;
    
    /** 获取连接的时间标记 */
    private long _m_lGetConnectionTimeTag;
    /** 连接对象 */
    private Connection _m_cConnection;
    
    public ALMySqlDBConnectionObj(_AALMySqlBaseDBObj _dbObj, Connection _conn)
    {
        _m_doDBObj = _dbObj;
        
        _m_lGetConnectionTimeTag = ALBasicCommonFun.getNowTimeMS();
        _m_cConnection = _conn;
    }
    
    /*************
     * 获取数据库信息对象
     * 
     * @author alzq.z
     * @time   Jan 14, 2014 12:14:53 AM
     */
    public _AALMySqlBaseDBObj getParentDBObj()
    {
        return _m_doDBObj;
    }
    
    /*******************
     * 获取连接的时间标记
     * 
     * @author alzq.z
     * @time   Jan 14, 2014 12:11:17 AM
     */
    public long getConnTimeTag()
    {
        return _m_lGetConnectionTimeTag;
    }
    
    /*****************
     * 返回一个挂了委托的Connection对象
     * 
     * @author alzq.z
     * @time   Jan 14, 2014 12:34:44 AM
     */
    public Connection getConnection()
    {
        if(null == _m_cConnection)
            return null;
        
        // 返回数据库连接conn的接管类，以便截住close方法
        Connection proxyConn = (Connection)(Proxy.newProxyInstance(_m_cConnection.getClass().getClassLoader()
                , new Class[] { Connection.class }, this));
        
        //刷新时间
        _m_lGetConnectionTimeTag = ALBasicCommonFun.getNowTimeMS();
        
        return proxyConn;
    }
    
    /****************
     * 获取连接对象
     * 
     * @author alzq.z
     * @time   Jan 14, 2014 12:10:59 AM
     */
    public Connection getConn()
    {
        long nowTime = ALBasicCommonFun.getNowTimeMS();
        
        try
        {
            if(null != _m_cConnection && !_m_cConnection.isClosed())
            {
                //检测最长重新连接时间
                if(nowTime - _m_lGetConnectionTimeTag <= getParentDBObj().getMaxConnectionKeepTimeMS())
                {
                    return getConnection();
                }
    
                //超出时间则关闭连接
                _m_cConnection.close();
                _m_cConnection = null;
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            try
            {
                //超出时间则关闭连接
                if(null != _m_cConnection)
                    _m_cConnection.close();
                _m_cConnection = null;
            }
            catch (Exception ex){}
            return null;
        } 

        //获取新的连接
        _m_cConnection = getParentDBObj()._getNewConnection();
        
        //返回空
        return getConnection();
    }

    /*********************
     * 调用委托对象的接口
     * 
     * @author alzq.z
     * @time   Jan 14, 2014 12:39:19 AM
     */
    @Override
    public Object invoke(Object _proxy, Method _method, Object[] _args)
            throws Throwable
    {
        Object obj = null;
        
        // 判断是否调用了close的方法，如果调用close方法则把连接置为无用状态
        if (CLOSE_METHOD_NAME.equals(_method.getName()))
        {
            //将缓冲节点插入缓冲池管理对象中
            getParentDBObj()._pushConnectionObj(this);
        }
        else
        {
            obj = _method.invoke(_m_cConnection, _args);
        }

        //刷新时间
        _m_lGetConnectionTimeTag = ALBasicCommonFun.getNowTimeMS();
        
        return obj;
    }
}
