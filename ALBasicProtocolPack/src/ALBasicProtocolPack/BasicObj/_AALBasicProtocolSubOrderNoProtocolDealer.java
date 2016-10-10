package ALBasicProtocolPack.BasicObj;

import java.nio.ByteBuffer;


/**********************
 * 协议处理类，使用模板方式定义，可直接生成对应的消息结构体并进行处理
 * 
 * @author alzq.z
 * @email  zhuangfan@vip.163.com
 * @time   Feb 19, 2013 10:56:34 AM
 */
public abstract class _AALBasicProtocolSubOrderNoProtocolDealer
{
    public _AALBasicProtocolSubOrderNoProtocolDealer()
    {
    }
    
	/*********************
	 * 消息带入的处理函数，将协议从字节中读取出并带入实际处理函数
	 * 
	 * @author alzq.z
	 * @time   Feb 19, 2013 10:52:19 AM
	 */
	public abstract void dealProtocol(_IALProtocolReceiver _receiver, ByteBuffer _msgBuffer);
}
