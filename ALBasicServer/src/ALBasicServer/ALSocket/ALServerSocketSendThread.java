package ALBasicServer.ALSocket;

import ALBasicCommon.Thread._AALBasicThread;



/************************
 * 端口发送线程对象
 * @author alzq
 *
 */
public class ALServerSocketSendThread extends _AALBasicThread
{
    /** 线程是否退出 */
    private boolean _m_bThreadExit;
    
    public ALServerSocketSendThread()
    {
		super(true);
		
        _m_bThreadExit = false;
    }

    public void ExitThread()
    {
        _m_bThreadExit = true;
    }
    
    /******************
     * 线程结束时执行的函数
     * 
     * @author alzq.z
     * @time   Feb 20, 2013 10:48:09 PM
     */
    @Override
    protected void _onThreadStart()
    {
    }
    @Override
    protected void _onThreadEnd()
    {
    }
    
    /******************
     * 线程执行函数
     * 
     * @author alzq.z
     * @time   Feb 20, 2013 11:05:14 PM
     */
    protected void _run()
    {
        while(!_m_bThreadExit)
        {
            //循环获取对象发送
            ALBasicServerSocket socket = ALServerSendSocketMgr.getInstance().popSendSocket();
            
            if(null != socket)
            {
                //发送
                socket._realSendMessage();
            }
        }
    }
}
