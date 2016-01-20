package ALMySqlCommon.ALMySqlCommonObj;

import ALMySqlCommon.ALMySqlCommonObj.ALMySqlConnectionPool._AALMySqlBaseDBObj;

/****************************
 * 数据库版本更新操作对象中用于创建基本表对象的操作对象接口类
 * 
 * @author alzq.z
 * @time   Feb 19, 2013 10:51:53 AM
 */
public interface _IALMySqlDBTableCreateObj
{
    /*********************
     * 执行具体的创建操作函数
     * 
     * @author alzq.z
     * @time   Apr 7, 2013 10:16:11 PM
     */
    public abstract boolean create(_AALMySqlBaseDBObj _dbObj);
}
