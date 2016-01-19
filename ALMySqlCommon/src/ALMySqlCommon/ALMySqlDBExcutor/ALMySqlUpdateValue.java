package ALMySqlCommon.ALMySqlDBExcutor;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import ALBasicCommon.ALBasicCommonFun;

/*****************
 * 操作数据库update语句时操作的数据信息
 * 
 * @author alzq.z
 * @email  zhuangfan@vip.163.com
 * @time   Jan 13, 2014 11:37:24 PM
 */
public class ALMySqlUpdateValue
{
    /******************
     * 创建一个操作的数据值对象信息
     * 
     * @author alzq.z
     * @time   Jan 13, 2014 11:40:28 PM
     */
    public static ALMySqlUpdateValue createObj(String _fieldName, String _valueStr)
    {
        return new ALMySqlUpdateValue().addValueObj(_fieldName, _valueStr);
    }
    public static ALMySqlUpdateValue createObj(String _fieldName, int _valueInt)
    {
        return new ALMySqlUpdateValue().addValueObj(_fieldName, _valueInt);
    }
    public static ALMySqlUpdateValue createObj(String _fieldName, long _valueLong)
    {
        return new ALMySqlUpdateValue().addValueObj(_fieldName, _valueLong);
    }
    public static ALMySqlUpdateValue createObj(String _fieldName, byte[] _valueBinary)
    {
        return new ALMySqlUpdateValue().addValueObj(_fieldName, _valueBinary);
    }
    public static ALMySqlUpdateValue createObj(String _fieldName, ByteBuffer _valueBinary)
    {
        return new ALMySqlUpdateValue().addValueObj(_fieldName, _valueBinary);
    }
    
    protected class ALMySqlDBUpdateValueObj
    {
        /** 字段名 */
        private String _m_sFieldName;
        /** 值对应的字符串 */
        private String _m_sValueStr;
        
        public ALMySqlDBUpdateValueObj(String _fieldName, String _valueStr)
        {
            _m_sFieldName = _fieldName;
            _m_sValueStr = _valueStr;
        }
    }
    
    private ArrayList<ALMySqlDBUpdateValueObj> _m_lUpdateValueObjList;
    
    public ALMySqlUpdateValue()
    {
        _m_lUpdateValueObjList = new ArrayList<ALMySqlDBUpdateValueObj>();
    }
    
    /*****************
     * 添加一个对应设置值信息
     * 
     * @author alzq.z
     * @time   Jan 13, 2014 11:47:13 PM
     */
    public ALMySqlUpdateValue addValueObj(String _fieldName, String _valueStr)
    {
        _m_lUpdateValueObjList.add(new ALMySqlDBUpdateValueObj(_fieldName, _valueStr));
        
        return this;
    }
    public ALMySqlUpdateValue addValueObj(String _fieldName, int _valueInt)
    {
        _m_lUpdateValueObjList.add(new ALMySqlDBUpdateValueObj(_fieldName, Integer.toString(_valueInt)));
        
        return this;
    }
    public ALMySqlUpdateValue addValueObj(String _fieldName, long _valueLong)
    {
        _m_lUpdateValueObjList.add(new ALMySqlDBUpdateValueObj(_fieldName, Long.toString(_valueLong)));
        
        return this;
    }
    public ALMySqlUpdateValue addValueObj(String _fieldName, float _valueFloat)
    {
        _m_lUpdateValueObjList.add(new ALMySqlDBUpdateValueObj(_fieldName, Float.toString(_valueFloat)));
        
        return this;
    }
    public ALMySqlUpdateValue addValueObj(String _fieldName, double _valueDouble)
    {
        _m_lUpdateValueObjList.add(new ALMySqlDBUpdateValueObj(_fieldName, Double.toString(_valueDouble)));
        
        return this;
    }
    public ALMySqlUpdateValue addValueObj(String _fieldName, byte[] _valueBinary)
    {
        _m_lUpdateValueObjList.add(new ALMySqlDBUpdateValueObj(_fieldName, "0x" + ALBasicCommonFun.getHexString(_valueBinary)));
        
        return this;
    }
    public ALMySqlUpdateValue addValueObj(String _fieldName, ByteBuffer _valueBinary)
    {
        _m_lUpdateValueObjList.add(new ALMySqlDBUpdateValueObj(_fieldName, "0x" + ALBasicCommonFun.getHexString(_valueBinary)));
        
        return this;
    }
    
    /******************
     * 获取Set的字符串信息
     * 
     * @author alzq.z
     * @time   Jan 13, 2014 11:47:40 PM
     */
    public String getSetStr()
    {
        StringBuilder builder = new StringBuilder();
        
        for(int i = 0; i < _m_lUpdateValueObjList.size(); i++)
        {
            ALMySqlDBUpdateValueObj valueObj = _m_lUpdateValueObjList.get(i);
            if(null == valueObj)
                continue;
            
            if(i > 0)
                builder.append(",");
            
            //拼凑字符串
            builder.append(valueObj._m_sFieldName).append("=").append(valueObj._m_sValueStr);
        }
        
        return builder.toString();
    }
}
