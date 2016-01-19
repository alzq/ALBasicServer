package ALMySqlCommon.ALMySqlCommonObj;

import ALMySqlCommon.ALMySqlCommonObj.ALMySqlConnectionPool._AALMySqlBaseDBObj;

/******************
 * 数据库版本更新操作对象中用于更新数据库版本的操作对象接口类
 * 
 * @author alzq.z
 * @email  zhuangfan@vip.163.com
 * @time   Apr 7, 2013 10:11:27 PM
 */
public interface _IALMySqlDBUpdateObj
{
    /******************
     * 获取本操作对象升级的源版本号
     * 
     * @author alzq.z
     * @time   Apr 7, 2013 10:13:42 PM
     */
    public abstract String getUpdateSrcVersion();
    /******************
     * 获取本操作对象升级的目标版本号
     * 
     * @author alzq.z
     * @time   Apr 7, 2013 10:13:42 PM
     */
    public abstract String getUpdateTarVersion();
    /*********************
     * 执行具体的升级操作函数
     * 
     * @author alzq.z
     * @time   Apr 7, 2013 10:16:11 PM
     */
    public abstract boolean update(_AALMySqlBaseDBObj _dbObj);
}
