package ALMySqlCommon.ALMySqlDBExcutor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import ALMySqlCommon.ALMySqlDBConditionObj;
import ALMySqlCommon.ALMySqlCommonObj._IALMySqlBaseDBBO;
import ALMySqlCommon.ALMySqlCommonObj.ALMySqlConnectionPool._AALMySqlBaseDBObj;

/*********************
 * 数据库操作对象
 * 
 * @author alzq.z
 * @email  zhuangfan@vip.163.com
 * @time   Jan 13, 2014 11:31:25 PM
 */
public class ALMySqlDBExcutor
{
    private static byte[] emptyBinary = new byte[0];
    
    /**
     * 插入BO对象函数
     */
    public static boolean InsertBO(_AALMySqlBaseDBObj _dbObj, _IALMySqlBaseDBBO _bo)
    {
        Connection con = null;
        PreparedStatement stmt = null;
        String SQL = null;
        long id = 0;
        boolean dealRes = false;

        try
        {
            con = _dbObj.getConn();

            //获取插入的执行字符串
            SQL = GetInsertSql(_bo);

            stmt = con.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
            dealRes = (stmt.executeUpdate() > 0);

            //获取ID
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next())
                id = rs.getLong(1);
            
            rs.close();
        }
        catch (Exception ex)
        {
            System.out.print("SQL:'" + SQL + "' error!!!\n");
            ex.printStackTrace();
        }
        finally
        {
            try
            {
                stmt.close();
            }
            catch (Exception e){ }
            try
            {
                con.close();
            }
            catch (Exception e){ }
        }

        _bo.setId(id);
        
        return dealRes;
    }
    public static boolean InsertBO(_AALMySqlBaseDBObj _dbObj, _IALMySqlBaseDBBO _bo, ArrayList<byte[]> _binaryList)
    {
        Connection con = null;
        PreparedStatement stmt = null;
        String SQL = null;
        long id = 0;

        try
        {
            con = _dbObj.getConn();

            SQL = GetInsertSql(_bo);

            stmt = con.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
            
            for(int i = 0; i < _binaryList.size(); i++)
            {
                byte[] binaryData = _binaryList.get(i);
                if(null == binaryData)
                    binaryData = emptyBinary;
                
                stmt.setBytes(i + 1, binaryData);
            }
            
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next())
                id = rs.getLong(1);
            
            rs.close();
        }
        catch (Exception ex)
        {
            System.out.print("SQL:'" + SQL + "' error!!!\n");
            ex.printStackTrace();
        }
        finally
        {
            try
            {
                stmt.close();
            }
            catch (Exception e){ }
            try
            {
                con.close();
            }
            catch (Exception e){ }
        }

        if (id > 0)
        {
            _bo.setId(id);
            return true;
        }
        else
        {
            return false;
        }
    }
    public static int ReplaceBO(_AALMySqlBaseDBObj _dbObj, _IALMySqlBaseDBBO _bo)
    {
        Connection con = null;
        Statement stmt = null;
        String SQL = null;
        int resultCount = 0;

        try
        {
            con = _dbObj.getConn();
            stmt = con.createStatement();

            SQL = GetReplaceSql(_bo);

            resultCount = stmt.executeUpdate(SQL);
        }
        catch (Exception ex)
        {
            System.out.print("SQL:'" + SQL + "' error!!!\n");
            ex.printStackTrace();
        }
        finally
        {
            try
            {
                stmt.close();
            }
            catch (Exception e){ }
            try
            {
                con.close();
            }
            catch (Exception e){ }
        }

        return resultCount;
    }

    /*********************
     * 根据条件获取对应的数据对象的数量
     * 
     * @author alzq.z
     * @time   Jan 13, 2014 11:59:18 PM
     */
    public static long getCountByCondition(_AALMySqlBaseDBObj _dbObj,
            ALMySqlDBConditionObj _conditionObj, _IALMySqlBaseDBBO _bo)
    {
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        String SQL = null;
        try
        {
            con = _dbObj.getConn();
            stmt = con.createStatement();

            //设置表名
            _conditionObj.setTablesName(_bo.getTbName());
            _conditionObj.setItemsName(" Count(*) ");
            SQL = _conditionObj.getSQL();

            //获取数据
            rs = stmt.executeQuery(SQL);
            if (rs.next())
            {
                //获取第一个字段
                return rs.getLong(1);
            }
        }
        catch (Exception ex)
        {
            System.out.print("SQL:'" + SQL + "' error!!!\n");
            ex.printStackTrace();
        }
        finally
        {
            try
            {
                rs.close();
            }
            catch (Exception e){ }
            try
            {
                stmt.close();
            }
            catch (Exception e){ }
            try
            {
                con.close();
            }
            catch (Exception e){ }
        }

        return 0;
    }

    /*********************
     * 根据条件获取对应的数据对象队列
     * 
     * @author alzq.z
     * @time   Jan 13, 2014 11:59:18 PM
     */
    @SuppressWarnings({ "rawtypes" })
    public static List getListByCondition(_AALMySqlBaseDBObj _dbObj,
            ALMySqlDBConditionObj _conditionObj, _IALMySqlBaseDBBO _bo)
    {
        List res = new ArrayList<_IALMySqlBaseDBBO>();
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        String SQL = null;
        try
        {
            con = _dbObj.getConn();
            stmt = con.createStatement();

            //设置表名
            _conditionObj.setTablesName(_bo.getTbName());
            _conditionObj.setItemsName(_bo.getSelectItemsName());
            SQL = _conditionObj.getSQL();

            rs = stmt.executeQuery(SQL);
            while (rs.next())
            {
                _bo.getFromResultSet(rs, res);
            }
        }
        catch (Exception ex)
        {
            System.out.print("SQL:'" + SQL + "' error!!!\n");
            ex.printStackTrace();
        }
        finally
        {
            try
            {
                rs.close();
            }
            catch (Exception e){ }
            try
            {
                stmt.close();
            }
            catch (Exception e){ }
            try
            {
                con.close();
            }
            catch (Exception e){ }
        }

        return res;
    }

    /**
     * 根据Condition进行UPDATE
     * 
     * @param condition
     * @param firstLine
     * @param endLine
     * @return
     * @throws Exception
     */
    public static int updateByCondition(_AALMySqlBaseDBObj _dbObj,
            ALMySqlDBConditionObj _conditionObj, ALMySqlUpdateValue _updateValue)
    {
        return updateByCondition(_dbObj, _conditionObj, _updateValue.getSetStr());
    }
    public static int updateByCondition(_AALMySqlBaseDBObj _dbObj,
            ALMySqlDBConditionObj _conditionObj, String _updateStr)
    {
        Connection con = null;
        Statement stmt = null;
        String SQL = null;

        int resultCount = 0;

        try
        {
            con = _dbObj.getConn();
            stmt = con.createStatement();

            SQL = GetUpdateSql(_conditionObj, _updateStr);

            resultCount = stmt.executeUpdate(SQL);
        }
        catch (Exception ex)
        {
            System.out.print("SQL:'" + SQL + "' error!!!\n");
            ex.printStackTrace();
        }
        finally
        {
            try
            {
                stmt.close();
            }
            catch (Exception e){ }
            try
            {
                con.close();
            }
            catch (Exception e){ }
        }

        return resultCount;
    }
    public static int updateByCondition(_AALMySqlBaseDBObj _dbObj,
            ALMySqlDBConditionObj _conditionObj, String _updateStr, ArrayList<byte[]> _binaryList)
    {
        Connection con = null;
        PreparedStatement stmt = null;
        String SQL = null;

        int resultCount = 0;

        try
        {
            con = _dbObj.getConn();

            SQL = GetUpdateSql(_conditionObj, _updateStr);

            stmt = con.prepareStatement(SQL);
            
            for(int i = 0; i < _binaryList.size(); i++)
            {
                byte[] binaryData = _binaryList.get(i);
                if(null == binaryData)
                    binaryData = emptyBinary;
                
                stmt.setBytes(i + 1, binaryData);
            }
            
            resultCount = stmt.executeUpdate();
        }
        catch (Exception ex)
        {
            System.out.print("SQL:'" + SQL + "' error!!!\n");
            ex.printStackTrace();
        }
        finally
        {
            try
            {
                stmt.close();
            }
            catch (Exception e){ }
            try
            {
                con.close();
            }
            catch (Exception e){ }
        }

        return resultCount;
    }

    /**
     * 根据Condition进行delete
     * 
     * @param condition
     * @param firstLine
     * @param endLine
     * @return
     * @throws Exception
     */
    public static int delByCondition(_AALMySqlBaseDBObj _dbObj,
            ALMySqlDBConditionObj _conditionObj)
    {
        Connection con = null;
        Statement stmt = null;
        String SQL = null;

        int resultCount = 0;

        try
        {
            con = _dbObj.getConn();
            stmt = con.createStatement();

            SQL = "delete " + _conditionObj.getConditionAndTables() + ";";

            resultCount = stmt.executeUpdate(SQL);
        }
        catch (Exception ex)
        {
            System.out.print("SQL:'" + SQL + "' error!!!\n");
            ex.printStackTrace();
        }
        finally
        {
            try
            {
                stmt.close();
            }
            catch (Exception e){ }
            try
            {
                con.close();
            }
            catch (Exception e){ }
        }

        return resultCount;
    }

    /*********************
     * 判断对应的表是否存在
     * 
     * @author alzq.z
     * @time   Jan 13, 2014 11:59:18 PM
     */
    public static boolean isTableExist(_AALMySqlBaseDBObj _dbObj, String _tableName)
    {
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        String SQL = null;
        try
        {
            con = _dbObj.getConn();
            stmt = con.createStatement();

            //设置表名
            SQL = "SELECT `TABLE_NAME` from `INFORMATION_SCHEMA`.`TABLES` WHERE `TABLE_SCHEMA`='" 
                        + _dbObj.getDBName() + "' AND `TABLE_NAME`='" + _tableName.replace("`", "") + "';";

            //获取数据
            rs = stmt.executeQuery(SQL);
            if (rs.next())
            {
                return true;
            }
        }
        catch (Exception ex)
        {
            System.out.print("SQL:'" + SQL + "' error!!!\n");
            ex.printStackTrace();
        }
        finally
        {
            try
            {
                rs.close();
            }
            catch (Exception e){ }
            try
            {
                stmt.close();
            }
            catch (Exception e){ }
            try
            {
                con.close();
            }
            catch (Exception e){ }
        }
        
        return false;
    }

    /**************
     * 获取update的操作语句
     * 
     * @author alzq.z
     * @time   Jan 13, 2014 11:35:45 PM
     */
    public static String GetUpdateSql(ALMySqlDBConditionObj _conditionObj, String _updateStr)
    {
        StringBuilder builder = new StringBuilder();
        
        builder.append("update ")
            .append(_conditionObj.getTablesName())
            .append(" set ")
            .append(_updateStr);

        String conditionString = _conditionObj.getCondition();
        if (conditionString.trim().length() > 0)
        {
            builder.append(" where ")
                .append(_conditionObj.getCondition());
        }
        
        builder.append(";");
        
        return builder.toString();
    }

    /*****************
     * 获取Insert语句的具体字符串
     * 
     * @author alzq.z
     * @time   Jan 13, 2014 11:34:07 PM
     */
    public static String GetInsertSql(_IALMySqlBaseDBBO _bo)
    {
        StringBuilder builder = new StringBuilder();
        
        builder.append("insert into ")
            .append(_bo.getTbName())
            .append("(")
            .append(_bo.getInsertItemsName())
            .append(") values (")
            .append(_bo.getInsertValuesStr())
            .append(");");
        
        return builder.toString();
    }

    /*******************
     * 获取Replace语句的具体字符串
     * 
     * @author alzq.z
     * @time   Jan 13, 2014 11:34:25 PM
     */
    public static String GetReplaceSql(_IALMySqlBaseDBBO _bo)
    {
        StringBuilder builder = new StringBuilder();
        
        builder.append("replace into ")
            .append(_bo.getTbName())
            .append("(")
            .append(_bo.getInsertItemsName())
            .append(") values (")
            .append(_bo.getInsertValuesStr())
            .append(");");
        
        return builder.toString();
    }
    
    
    public static boolean execute(String sql, _AALMySqlBaseDBObj _dbObj)
    {
        Connection conn = null;
        Statement stmt = null;

        try
        {
            conn = _dbObj.getConn();
            stmt = conn.createStatement();
            stmt.execute(sql);
        }
        catch (SQLException e)
        {
            System.out.println("SQL Error [" + sql + "]");
            e.printStackTrace();
            
            return false;
        }
        finally
        {
            try
            {
                if ( null != stmt )
                    stmt.close();
            }
            catch (Exception e){ }
            
            try
            {
                if (null != conn)
                    conn.close();
            }
            catch (Exception e){ } 
        }
        
        return true;
    }    
    /***********************
     * do some sql operation<br>
     * and return the count of the data have been operate<br>
     * 
     * @param sql
     * @param dbHost
     * @param dbName
     * @param dbUser
     * @param dbPass
     * @return
     * @throws SQLException
     */
    public static int executeUpdate(String sql, _AALMySqlBaseDBObj _dbObj)
    {
        int dealCount = 0;
        Connection conn = null;
        Statement stmt = null;
        try
        {
            conn = _dbObj.getConn();
            stmt = conn.createStatement();
            dealCount = stmt.executeUpdate(sql);
        }
        catch (SQLException e)
        {
            System.out.println("SQL Error [" + sql + "]");
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if ( null != stmt )
                    stmt.close();
            }
            catch (Exception e){ }
            
            try
            {
                if (null != conn)
                    conn.close();
            }
            catch (Exception e){ } 
        }
        return dealCount;
    }

    public static int executeInsert(String sql, _AALMySqlBaseDBObj _dbObj)
            throws SQLException
    {
        int lastID = 0;

        Connection conn = null;
        PreparedStatement stmt = null;
        try
        {
            conn = _dbObj.getConn();
            stmt = conn.prepareStatement(sql,
                    Statement.RETURN_GENERATED_KEYS);
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();

            if (rs.next())
            {
                lastID = rs.getInt(1);
            }
            
            rs.close();
        }
        catch (SQLException e)
        {
            System.out.println("SQL Error [" + sql + "]");
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if ( null != stmt )
                    stmt.close();
            }
            catch (Exception e){ }
            
            try
            {
                if (null != conn)
                    conn.close();
            }
            catch (Exception e){ } 
        }

        return lastID;
    }
    
    /**
     * 获取ID列表集合
     * @param sql
     * @param connectionInfo
     * @return
     */
    public static List<Integer> getFirstNumList(String sql, _AALMySqlBaseDBObj _dbObj)
    {
        List<Integer> lstID = new ArrayList<Integer>();
        
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try
        {
            conn = _dbObj.getConn();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            while (rs.next())
            {
                lstID.add(rs.getInt(1));
            }
            
            rs.close();
        }
        catch (SQLException e)
        {
            System.out.println("SQL Error [" + sql + "]");
            e.printStackTrace();
        }
        catch (Exception e) 
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if ( null != stmt )
                    stmt.close();
            }
            catch (Exception e){ }
            
            try
            {
                if (null != conn)
                    conn.close();
            }
            catch (Exception e){ }
            
        }
        
        return lstID;
    }
    
    @SuppressWarnings("rawtypes")
    public static List executeQuery(String _sql, _AALMySqlBaseDBObj _dbObj, _IALMySqlBaseDBBO _bo)
    {
        List listResult = new ArrayList<_IALMySqlBaseDBBO>();
        
        Statement stmt = null;
        ResultSet rs = null;
        Connection conn = null;
        
        try
        {
            conn = _dbObj.getConn();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(_sql);
            
            while(rs.next())
            {
                _bo.getFromResultSet(rs, listResult);
            }
            
            rs.close();
        }
        catch (SQLException e)
        {
            System.out.println("SQL Error [" + _sql + "]");
            e.printStackTrace();
        }
        catch (ClassNotFoundException e)
        {
            System.out.println("DB Connection Error");
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if ( null != stmt )
                    stmt.close();
            }
            catch (Exception e){ }
            
            try
            {
                if (null != conn)
                    conn.close();
            }
            catch (Exception e){ }
            
        }
        
        return listResult;
    }
    
    public static void setUTF8(_AALMySqlBaseDBObj _dbObj)
    {
        String sql = "SET NAMES 'utf8';";
        Connection conn = null;
        Statement stmt = null;
        try
        {
            conn = _dbObj.getConn();
            stmt = conn.createStatement();
            stmt.executeUpdate(sql);
        }
        catch (SQLException e)
        {
            System.out.println("SQL Error [" + sql + "]");
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if ( null != stmt )
                    stmt.close();
            }
            catch (Exception e){ }
            
            try
            {
                if (null != conn)
                    conn.close();
            }
            catch (Exception e){ }
        }
    }
}
