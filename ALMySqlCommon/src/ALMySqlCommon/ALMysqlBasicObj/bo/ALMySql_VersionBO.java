package ALMySqlCommon.ALMysqlBasicObj.bo;

import java.sql.ResultSet;
import java.util.List;

import ALMySqlCommon.ALMySqlCommonObj._IALMySqlBaseDBBO;

/********************************
 * 基本库版本表对象内容
 * 
 * @author alzq.z
 * @time   Feb 19, 2013 10:51:53 AM
 */
public class ALMySql_VersionBO implements _IALMySqlBaseDBBO
{
    private String _m_sVersionStr;
    
    public ALMySql_VersionBO()
    {
        _m_sVersionStr = "";
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void getFromResultSet(ResultSet rs, List list) throws Exception
    {
        ALMySql_VersionBO bo = new ALMySql_VersionBO();
        
        bo._m_sVersionStr = rs.getString(1);
        
        list.add(bo);
    }

    @Override
    public String getSelectItemsName()
    {
        return "`version`";
    }

    @Override
    public String getTbName()
    {
        return "`info_version`";
    }

    @Override
    public String getInsertItemsName()
    {
        return "`version`";
    }

    @Override
    public String getInsertValuesStr()
    {
        return "'" + _m_sVersionStr + "'";
    }

    @Override
    public void setId(long _id)
    {
    }
    
    public String getVersion() {return _m_sVersionStr;}
    
    public void setVersion(String _version) {_m_sVersionStr = _version;}
}
