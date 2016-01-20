package ALMySqlCommon.ALMySqlCommonObj;

import java.sql.ResultSet;
import java.util.List;

/************************
 * 数据库中数据集的对象基类
 * 
 * @author alzq.z
 * @time   Feb 19, 2013 10:51:53 AM
 */
public interface _IALMySqlBaseDBBO
{
    /**
     * 本方法从ResultSet中提取相关信息，并添加到List中<br>
     * 主要用于JDBC的访问，一般根据自行设定好的顺序进行数据提取<br>
     * 也可以使用字段名，不过字段名消耗较多资源<br>
     * 需要在子类别中重定义<br>
     */
    @SuppressWarnings("rawtypes")
    public void getFromResultSet(ResultSet rs, List list) throws Exception;
    /**************
     * 用于选取数据的数据字段拼凑字符串
     */
    public String getSelectItemsName();
    /***************
     * 获取对应的数据库表名
     */
    public String getTbName();
    /***************
     * 用于获取插入数据时的字段列表
     */
    public String getInsertItemsName();
    /***************
     * 用于获取插入数据时的Value字符串
     */
    public String getInsertValuesStr();
    /***************
     * 用于设置每条记录的主键ID
     */
    public void setId(long _id);
}
