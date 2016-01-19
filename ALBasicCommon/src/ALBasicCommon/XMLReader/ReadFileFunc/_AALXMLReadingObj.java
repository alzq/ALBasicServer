package ALBasicCommon.XMLReader.ReadFileFunc;

import org.w3c.dom.Node;

/*****************
 * 基本读取操作函数部分
 * 
 * @author alzq.z
 * @email  zhuangfan@vip.163.com
 * @time   Aug 12, 2013 10:22:21 PM
 */
public abstract class _AALXMLReadingObj<T>
{
    /*******************
     * 进行节点内容读取，返回是否进行了处理
     * 
     * @author alzq.z
     * @time   Aug 12, 2013 10:28:44 PM
     */
    public abstract boolean readingNode(T _argObj, Node _node, String _name);
    /*******************
     * 进行节点信息读取
     * 
     * @author alzq.z
     * @time   Aug 12, 2013 10:28:44 PM
     */
    public abstract void readingNodeValue(T _argObj, String _name, String _value);
}