package ALBasicProtocolPack.ProtocolDealer;

import ALBasicProtocolPack._IALProtocolStructure;
import ALBasicProtocolPack.BasicObj._IALProtocolReceiver;


/**********************
 * 协议处理类，使用模板方式定义，可直接生成对应的消息结构体并进行处理
 * 
 * @author alzq.z
 * @email  zhuangfan@vip.163.com
 * @time   Feb 19, 2013 10:56:34 AM
 */
public abstract class _AALProtocolSubOrderDealer<T extends _IALProtocolStructure>
{
    /** 实例化对象，用于获取信息使用 */
    private T basicInfoObj;
    
    public _AALProtocolSubOrderDealer()
    {
        basicInfoObj = _createProtocolObj();
    }
    
	/**********************
	 * 消息处理函数，直接带入对应的消息结构体
	 * 
	 * @author alzq.z
	 * @time   Feb 19, 2013 10:52:30 AM
	 */
	@SuppressWarnings("unchecked")
	public void dealProtocol(_IALProtocolReceiver _receiver, _IALProtocolStructure _msg)
	{
		_dealProtocol(_receiver, (T)_msg);
	}
	
	/**********************
	 * 消息处理函数，直接带入对应的消息结构体
	 * 
	 * @author alzq.z
	 * @time   Feb 19, 2013 10:52:30 AM
	 */
	protected abstract void _dealProtocol(_IALProtocolReceiver _receiver, T _msg);

    /************
     * 自动根据处理的消息对象获取本处理对象处理的协议主，副协议号
     * 
     * @author alzq.z
     * @time   Feb 19, 2013 11:36:41 AM
     */
    public byte getMainOrder() {return basicInfoObj.getMainOrder();}
    public byte getSubOrder() {return basicInfoObj.getSubOrder();}
    
    /**********************
     * 创建消息结构体用于读取字节，并转化为消息对象
     * 
     * @author alzq.z
     * @time   Feb 19, 2013 10:52:25 AM
     */
    protected abstract T _createProtocolObj();
}
