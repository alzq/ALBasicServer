package ALBasicProtocolPack.ProtocolDealer;

import ALBasicCommon.ALBasicCommonFun;
import ALBasicProtocolPack._IALProtocolStructure;
import ALBasicProtocolPack.BasicObj._IALProtocolReceiver;
import ALServerLog.ALServerLog;


@SuppressWarnings("rawtypes")
public abstract class _AALProtocolMainOrderDealer
{
	private byte mainOrder;
	private _AALProtocolSubOrderDealer[] dealArray = null;
    
	/*****************
	 * 带入主协议号以及处理的协议最大的子协议号，进行协议处理队列的初始化
	 * 
	 * @author alzq.z
	 * @time   Feb 19, 2013 10:52:55 AM
	 */
    public _AALProtocolMainOrderDealer(byte _mainOrder, int _protocolMaxTypeNum)
    {
        mainOrder = _mainOrder;
        dealArray = new _AALProtocolSubOrderDealer[_protocolMaxTypeNum + 1];
    }
    
    /************
     * 获取主协议号
     * 
     * @author alzq.z
     * @time   Feb 19, 2013 11:38:46 AM
     */
    public byte getMainOrder() {return mainOrder;}
    
    /****************
     * 设置处理对象，直接设置到数组中保证处理速度
     * 
     * @author alzq.z
     * @time   Feb 19, 2013 10:52:50 AM
     */
    public void regDealer(_AALProtocolSubOrderDealer _dealer)
    {
        if(null == dealArray)
            return ;
        
        if(_dealer.getMainOrder() != mainOrder)
        {
            //主协议号不匹配，提示警告
            ALServerLog.Fatal(mainOrder + " doesn't match the dealer's(" + _dealer.getClass().toString() + ") main order: " + _dealer.getMainOrder() + "!");
        }
        
        int subOrder = ALBasicCommonFun.byte2int(_dealer.getSubOrder());
        if(subOrder >= dealArray.length)
        {
        	ALServerLog.Fatal(mainOrder + " Protocol dispather don't have " + subOrder + " size list to save the dealer obj!");
            return ;
        }
        
        dealArray[subOrder] = _dealer;
    }
    public void regDealer(byte _subOrder, _AALProtocolSubOrderDealer _dealer)
    {
        if(null == dealArray)
            return ;
        
        int subOrder = ALBasicCommonFun.byte2int(_subOrder);
        if(subOrder >= dealArray.length)
        {
            ALServerLog.Fatal(mainOrder + " Protocol dispather don't have " + subOrder + " size list to save the dealer obj!");
            return ;
        }
        
        dealArray[subOrder] = _dealer;
    }
    
    /****************
     * 强制设置指定对象为处理对象
     * 
     * @author alzq.z
     * @time   Feb 19, 2013 10:52:50 AM
     */
    public void setSubDealer(int _subOrderID, _AALProtocolSubOrderDealer _dealer)
    {
        if(null == dealArray)
            return ;
        
        if(_subOrderID >= dealArray.length)
        {
            ALServerLog.Fatal(mainOrder + " Protocol dispather don't have " + _subOrderID + " size list to save the dealer obj!");
            return ;
        }
        
        dealArray[_subOrderID] = _dealer;
    }
    
   /********************
    * 获取子处理对象信息
    * @company Isg @author alzq.zhf
    * 2014年11月15日 下午12:08:17
    */
   public _AALProtocolSubOrderDealer getSubDealer(byte _subOrder)
   {
       int iIndex = ALBasicCommonFun.byte2int(_subOrder);
       //编号超出数组大小，直接返回失败
       if(iIndex >= dealArray.length)
           return null;

       return dealArray[iIndex];
   }
    
	/**********************
	 * 根据协议编号分发协议并进行处理
	 * 
	 * @author alzq.z
	 * @time   Feb 19, 2013 10:52:46 AM
	 */
	public boolean dispathProtocol(_IALProtocolReceiver _receiver, _IALProtocolStructure _protocol)
	{
        if(null == _protocol)
            return false;

        //获取对应处理对象
        _AALProtocolSubOrderDealer dealer = getSubDealer(_protocol.getSubOrder());

        //判断处理对象是否有效
        if(null == dealer)
            return false;
        
        //处理对象
        dealer.dealProtocol(_receiver, _protocol);
        //返回处理成功
        return true;
	}
}
