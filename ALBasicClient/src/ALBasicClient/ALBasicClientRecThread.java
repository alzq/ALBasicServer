package ALBasicClient;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import ALBasicCommon.Thread._AALBasicThread;
import ALServerLog.ALServerLog;
import BasicServer.C2S_BasicClientVerifyInfo;

public class ALBasicClientRecThread extends _AALBasicThread
{
    /** 客户端对象类型 */
    private int _m_iClientType;
    /** 登录验证信息 */
    private String _m_sUserName;
    private String _m_sUserPassword;
    /** 登录时发送的自定义信息 */
    private String _m_sCustomMsg;
    /** 线程是否退出 */
    private boolean _m_bThreadExit;
    private ALBasicClientSocket _m_scSocket;
    private String _m_sServerIP;
    private int _m_iServerPort;
    
    public ALBasicClientRecThread(int _clientType, String _userName, String _userPassword, String _customMsg, ALBasicClientSocket _socket, String _serverIP, int _serverPort)
    {
    	super(true);
    	
        _m_iClientType = _clientType;
        _m_sUserName = _userName;
        _m_sUserPassword = _userPassword;
        _m_sCustomMsg = _customMsg;
        _m_bThreadExit = false;
        
        _m_scSocket = _socket;
        _m_sServerIP = _serverIP;
        _m_iServerPort = _serverPort;
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
     * @time   Feb 20, 2013 11:03:08 PM
     */
    @Override
    protected void _run()
    {
        try
        {
            if(!_m_scSocket.initSocket(_m_sServerIP, _m_iServerPort))
            	return ;
            
            //发送登录请求
            C2S_BasicClientVerifyInfo msg = new C2S_BasicClientVerifyInfo();
            msg.setClientType(_m_iClientType);
            msg.setUserName(_m_sUserName);
            msg.setUserPassword(_m_sUserPassword);
            if(null == _m_sCustomMsg)
                msg.setCustomMsg("");
            else
                msg.setCustomMsg(_m_sCustomMsg);
            
            _m_scSocket.send(msg.makePackage());
        }
        catch (Exception ex)
        {
            _m_scSocket._logout();
            return ;
        }
        
        //创建接收BUF
        ByteBuffer recBuffer = ByteBuffer.allocate(ALBasicClientConf.getInstance().getRecBufferLen());

        //开启循环处理Socket事件
        while(!_m_bThreadExit)
        {
            try
            {
	            //循环获取到的事件
	            Set<SelectionKey> readyKeySet = _m_scSocket.selectKeys();
	            //返回空表示里面处理select有问题，但是不一定需要断开连接
	            if(null == readyKeySet)
	            	continue;
	            
	            Iterator<SelectionKey> iter = readyKeySet.iterator();
	            while(iter.hasNext())
	            {
	                SelectionKey key = (SelectionKey)iter.next();
	                //移除已经取出的事件
	                iter.remove();
	                
	                if(key.isReadable())
	                {
	                    //收到数据
	                    SocketChannel socketChannel = (SocketChannel)key.channel();
	
	                    //读取数据
	                    try
	                    {
	                        recBuffer.clear();
	                        //此时进行读取，当读取的包大小超过存储区域则可能导致数据读取不全
	                        int recLen = socketChannel.read(recBuffer);
	                        //设置限制，并将读取位置归0
	                        recBuffer.flip();
	                        
	                        if(recLen < 0)
	                        {
	                            _m_scSocket._logout();
	                            return ;
	                        }
	                        
	                        if(recLen > 0)
	                        {
	                            //处理消息读取
	                            _m_scSocket._socketReceivingMessage(recBuffer);
	                        }
	                    }
	                    catch (IOException e)
	                    {
	                        _m_scSocket._logout();
	                        return ;
	                    }
	                    catch (Exception e)
	                    {
	                        ALServerLog.Fatal("Client port read buffer error!!");
	                        e.printStackTrace();
	                    }
	                }
	            }
            }
            catch (Exception e) {
            	if(!_m_bThreadExit)
            	{
	            	ALServerLog.Fatal("Rec Thread Read Error!");
					e.printStackTrace();
            	}
			}
        }
    }
}
