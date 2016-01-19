package ALMySqlCommon.ALMysqlBasicObj.op;

import java.util.ArrayList;

import ALMySqlCommon.ALMySqlCommonObj.ALMySqlConnectionPool._AALMySqlBaseDBObj;
import ALMySqlCommon.ALMySqlDBExcutor.ALMySqlDBExcutor;
import ALMySqlCommon.ALMysqlBasicObj.bo.ALMySql_VersionBO;

/**************
 * 版本信息操作对象
 * 
 * @author alzq.z
 * @email  zhuangfan@vip.163.com
 * @time   Jan 14, 2014 1:21:22 AM
 */
public class ALMySql_VersionOP
{
    private static ALMySql_VersionBO bo = new ALMySql_VersionBO();
    
    /*************
     * 获取对应数据库的版本信息
     * 
     * @author alzq.z
     * @time   Jan 14, 2014 1:21:55 AM
     */
    @SuppressWarnings("unchecked")
    public static String getVersionInfo(_AALMySqlBaseDBObj _dbObj)
    {
        //当表不存在直接返回
        if(!ALMySqlDBExcutor.isTableExist(_dbObj, bo.getTbName()))
            return null;
        
        ArrayList<ALMySql_VersionBO> resList = 
                (ArrayList<ALMySql_VersionBO>)ALMySqlDBExcutor.executeQuery("SELECT `version` FROM `info_version`;", _dbObj, bo);
        
        if(null == resList || resList.isEmpty())
            return null;
        
        return resList.get(0).getVersion();
    }
    
    /*************
     * 设置当前版本号
     * 
     * @author alzq.z
     * @time   Jan 14, 2014 1:21:55 AM
     */
    public static void setVersionInfo(_AALMySqlBaseDBObj _dbObj, String _version)
    {
        ALMySqlDBExcutor.executeUpdate("UPDATE `info_version` SET `version` = '" + _version + "';", _dbObj);
    }
}
