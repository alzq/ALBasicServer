package ALBasicProtocolPack.BasicObj;

import java.nio.ByteBuffer;

import ALBasicProtocolPack._IALProtocolStructure;


/**********************
 * 协议处理类，使用模板方式定义，可直接生成对应的消息结构体并进行处理
 * 
 * @author alzq.z
 * @email  zhuangfan@vip.163.com
 * @time   Feb 19, 2013 10:56:34 AM
 */
public abstract class _AALBasicProtocolSubOrderDealer<T extends _IALProtocolStructure> extends _AALBasicProtocolSubOrderNoProtocolDealer<T>
{
    public _AALBasicProtocolSubOrderDealer()
    {
    }
    
	/*********************
	 * 消息带入的处理函数，将协议从字节中读取出并带入实际处理函数
	 * 
	 * @author alzq.z
	 * @time   Feb 19, 2013 10:52:19 AM
	 */
    @Override
	public void dealProtocol(_IALProtocolReceiver _receiver, ByteBuffer _msgBuffer)
	{
		T protocolObj = _createProtocolObj();
		protocolObj.readPackage(_msgBuffer);
		
		//处理数据，并返回结果
		_dealProtocol(_receiver, protocolObj);
	}
	
	/**********************
	 * 消息处理函数，直接带入对应的消息结构体
	 * 
	 * @author alzq.z
	 * @time   Feb 19, 2013 10:52:30 AM
	 */
	protected abstract void _dealProtocol(_IALProtocolReceiver _receiver, T _msg);
}
