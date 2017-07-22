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
    public static ALMySqlUpdateValue createObj(String _fieldName, float _valueFloat)
    {
        return new ALMySqlUpdateValue().addValueObj(_fieldName, _valueFloat);
    }
    public static ALMySqlUpdateValue createObj(String _fieldName, double _valueDouble)
    {
        return new ALMySqlUpdateValue().addValueObj(_fieldName, _valueDouble);
    }
    public static ALMySqlUpdateValue createObj(String _fieldName, byte[] _valueBinary)
    {
        return new ALMySqlUpdateValue().addValueObj(_fieldName, _valueBinary);
    }
    public static ALMySqlUpdateValue createObj(String _fieldName, ByteBuffer _valueBinary)
    {
        return new ALMySqlUpdateValue().addValueObj(_fieldName, _valueBinary);
    }
    public static ALMySqlUpdateValue createSQLObj(String _fieldName, String _sqlStr)
    {
        return new ALMySqlUpdateValue().addValueObj(_fieldName, _sqlStr);
    }

    protected abstract class _AALMySqlDBUpdateValueObj
    {
        /** 字段名 */
        protected String _m_sFieldName;
        /** 值对应的字符串 */
        protected String _m_sValueStr;
        
        public _AALMySqlDBUpdateValueObj(String _fieldName, String _valueStr)
        {
            _m_sFieldName = _fieldName;
            _m_sValueStr = _valueStr;
        }
        
        /** 拼凑功能字符串 */
        public abstract void appendValueStr(StringBuilder _builder);
    }
    protected class ALMySqlDBUpdateValueObj extends _AALMySqlDBUpdateValueObj
    {
        public ALMySqlDBUpdateValueObj(String _fieldName, String _valueStr)
        {
            super(_fieldName, _valueStr);
        }

        public void appendValueStr(StringBuilder _builder)
        {
            if(null == _builder)
                return;
            
            _builder.append("'").append(_m_sValueStr).append("'");
        }
    }
    protected class ALMySqlDBUpdateSQLValueObj extends _AALMySqlDBUpdateValueObj
    {
        public ALMySqlDBUpdateSQLValueObj(String _fieldName, String _valueStr)
        {
            super(_fieldName, _valueStr);
        }

        public void appendValueStr(StringBuilder _builder)
        {
            if(null == _builder)
                return;
            
            _builder.append(_m_sValueStr);
        }
    }
    
    private ArrayList<_AALMySqlDBUpdateValueObj> _m_lUpdateValueObjList;
    
    public ALMySqlUpdateValue()
    {
        _m_lUpdateValueObjList = new ArrayList<_AALMySqlDBUpdateValueObj>();
    }
    
    /*****************
     * 添加一个对应设置值信息
     * 
     * @author alzq.z
     * @time   Jan 13, 2014 11:47:13 PM
     */
    public ALMySqlUpdateValue addValueObj(String _fieldName, boolean _valueBoolean)
    {
        _m_lUpdateValueObjList.add(new ALMySqlDBUpdateValueObj(_fieldName, Boolean.toString(_valueBoolean)));
        
        return this;
    }
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
    public ALMySqlUpdateValue addSQLValueObj(String _fieldName, String _sqlStr)
    {
        _m_lUpdateValueObjList.add(new ALMySqlDBUpdateSQLValueObj(_fieldName, _sqlStr));
        
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
            _AALMySqlDBUpdateValueObj valueObj = _m_lUpdateValueObjList.get(i);
            if(null == valueObj)
                continue;
            
            if(i > 0)
                builder.append(",");
            
            //拼凑字符串
            builder.append(valueObj._m_sFieldName).append("=");
            valueObj.appendValueStr(builder);
        }
        
        return builder.toString();
    }
}
