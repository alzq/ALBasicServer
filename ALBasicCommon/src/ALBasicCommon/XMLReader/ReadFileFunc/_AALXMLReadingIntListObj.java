package ALBasicCommon.XMLReader.ReadFileFunc;

import java.util.ArrayList;


/*****************
 * 基本读取操作函数部分
 * 
 * @author alzq.z
 * @email  zhuangfan@vip.163.com
 * @time   Aug 12, 2013 10:22:21 PM
 */
public abstract class _AALXMLReadingIntListObj extends _AALXMLReadingObj<ArrayList<Integer>>
{
    /** 标识名称 */
    private String _m_sNodeName;
    
    public _AALXMLReadingIntListObj(String _nodeName)
    {
        _m_sNodeName = _nodeName;
    }
    
    public String getNodeName() {return _m_sNodeName;}
}