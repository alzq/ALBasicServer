package ALBasicClient;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;

import ALServerLog.ALServerLog;
import BasicServer.S2C_BasicClientVerifyResult;

public class ALBasicClientSocket
{
    /** 对应于服务端的ID */
    private long _m_iClientID;
    /** 对应的处理对�*/
    private _AALBasicClientListener _m_clClient;
    /** 是否正在登录 */
    private boolean _m_bLoginIng;
    /** 是否登录成功 */
    private boolean _m_bLoged;
    /** 连接服务器的IP，端�*/
    private String _m_sServerIP;
    private int _m_iServerPort;
    /** 连接的端口对�*/
    private SocketChannel _m_scSocket;
    
    /** 发送队列锁 */
    private ReentrantLock _m_rSendListMutex;
    /** 需要发送的消息队列 */
    private LinkedList<ByteBuffer> _m_lSendBufferList;
    
    /** 缓存读取字节的位置，长度根据配置设置 */
    private int _m_sBufferLen;
    private ByteBuffer _m_bByteBuffer;
    
    public ALBasicClientSocket(_AALBasicClientListener _client, String _serverIP, int _serverPort)
    {
        _m_iClientID = 0;
        _m_sServerIP = _serverIP;
        _m_iServerPort = _serverPort;
        _m_scSocket = null;
        
        _m_clClient = _client;
        
        _m_rSendListMutex = new ReentrantLock();
        _m_lSendBufferList = new LinkedList<ByteBuffer>();
        
        _m_bLoginIng = false;
        _m_bLoged = false;
        _m_sBufferLen = 0;
        _m_bByteBuffer = ByteBuffer.allocate(ALBasicClientConf.getInstance().getRecBufferLen());
        _m_bByteBuffer.clear();
    }
    
    public long getID() {return _m_iClientID;}
    public boolean getIsLoginIng() {return _m_bLoginIng;}
    public _AALBasicClientListener getClient() {return _m_clClient;}
    
    /**************
     * 判断是否正在连接状�
     * 
     * @author alzq.z
     * @time   Mar 17, 2013 10:52:53 PM
     */
    public boolean getIsConnecting()
    {
        if(_m_bLoginIng || _m_bLoged)
            return true;
        
        return false;
    }
    
    /******************
     * 登录操作
     * 
     * @author alzq.z
     * @time   Feb 19, 2013 9:57:25 PM
     */
    public void login(int _clientType, String _userName, String _userPassword, String _customMsg)
    {
        if(_m_bLoginIng || _m_bLoged)
            return ;

        _m_bLoginIng = true;

        ALBasicClientRecThread recThread = 
                new ALBasicClientRecThread(_clientType, _userName, _userPassword, _customMsg, this, _m_sServerIP, _m_iServerPort);
        recThread.start();
    }
    
    /****************
     * 断开连接
     */
    public void logout()
    {
        _logout();
    }
    
    /********************
     * 将消息添加到发送队列，等待发�
     * 
     * @author alzq.z
     * @time   Feb 19, 2013 9:57:33 PM
     */
    public void send(ByteBuffer _buf)
    {
        if(null == _m_scSocket || null == _buf || _buf.remaining() == 0)
            return ;
        
        boolean needAddToSendList = false;
        _lockBuf();
        
        //判断当前队列是否有剩余协议，表明是否需要将socket添加到对应发送队列中
        if(_m_lSendBufferList.isEmpty())
            needAddToSendList = true;
        
        //先插入长度数据，后插入实际数�
        ByteBuffer lenthBuffer = ByteBuffer.allocate(4);
        lenthBuffer.putInt(_buf.remaining());
        lenthBuffer.flip();
        
        _m_lSendBufferList.add(lenthBuffer);
        _m_lSendBufferList.add(_buf);
        
        _unlockBuf();
        
        if(needAddToSendList)
            ALBasicSendingClientManager.getInstance().addSendSocket(this);
    }
    
    /****************
     * 对数据添加临时头的发送方�
     * 
     * @author alzq.z
     * @time   Feb 19, 2013 9:59:13 PM
     */
    public void send(ByteBuffer _tmpHeader, ByteBuffer _buf)
    {
        if(null == _m_scSocket || null == _buf || _buf.remaining() == 0)
            return ;
        
        boolean needAddToSendList = false;
        _lockBuf();
        
        //判断当前队列是否有剩余协议，表明是否需要将socket添加到对应发送队列中
        if(_m_lSendBufferList.isEmpty())
            needAddToSendList = true;
        
        //先插入长度数据，后插入实际数�
        ByteBuffer lenthBuffer = ByteBuffer.allocate(4);
        lenthBuffer.putInt(_buf.remaining() + _tmpHeader.remaining());
        lenthBuffer.flip();
        
        _m_lSendBufferList.add(lenthBuffer);
        _m_lSendBufferList.add(_tmpHeader);
        _m_lSendBufferList.add(_buf);
        
        _unlockBuf();
        
        if(needAddToSendList)
            ALBasicSendingClientManager.getInstance().addSendSocket(this);
    }
    
    /**********************
     * 实际的发送函数，尝试发送尽量多的消息，并判断是否有剩余消息需要发�br>
     * 发送完成后判断是否有剩余消息，并在计划队列中添加节�br>
     * 
     * @author alzq.z
     * @time   Feb 19, 2013 9:59:24 PM
     */
    protected void _realSendMessage()
    {
        if(null == _m_scSocket)
            return ;
        
        if(!_m_scSocket.isConnected())
        {
            ALBasicSendingClientManager.getInstance().addSendSocket(this);
            return ;
        }

        boolean needAddToSendList = false;
        _lockBuf();

        while(!_m_lSendBufferList.isEmpty())
        {
            //Socket 允许写入操作�
            ByteBuffer buf = _m_lSendBufferList.getFirst();

            if(buf.remaining() <= 0)
            {
                ALServerLog.Error("try to send a null buffer");
                ALServerLog.Error("Wrong buffer:");
                for(int i = 0; i < buf.limit(); i++)
                {
                    ALServerLog.Error(buf.get(i) + " ");
                }
            }
            
            try {
                _m_scSocket.write(buf);
                
                //判断写入后对应数据的读取指针位置
                if(buf.remaining() <= 0)
                    _m_lSendBufferList.pop();
                else
                    break;
            }
            catch (IOException e)
            {
                ALServerLog.Error("Client Socket send message error! socket id[" + getID() + "]");
                e.printStackTrace();
                break;
            }
        }
        
        //当需要发送队列不为空时，继续添加发送节�
        if(!_m_lSendBufferList.isEmpty())
            needAddToSendList = true;
        
        _unlockBuf();
        
        if(needAddToSendList)
            ALBasicSendingClientManager.getInstance().addSendSocket(this);
    }
    
    /*********************
     * 接收函数中将接收到的字节放入消息中，根据Socket之前收的残留信息进行一并处理�
     * 
     * @author alzq.z
     * @time   Feb 19, 2013 10:00:23 PM
     */
    protected void _socketReceivingMessage(ByteBuffer _buf)
    {
        //将数据放入缓冲中
        try
        {
            _m_bByteBuffer.put(_buf);
        }
        catch (BufferOverflowException e)
        {
            ALServerLog.Error("_socketReceivingMessage length is too long, Socket Buffer need more!");
            _m_bByteBuffer.put(_buf.array(), 0, _m_bByteBuffer.remaining());
            //放置缓冲区读取指�
            _buf.position(_m_bByteBuffer.remaining());
        }
        
        if(0 == _m_sBufferLen)
        {
            //尚未读取长度�
            if(_m_bByteBuffer.position() >= 4)
            {
                //当缓冲中字节大于2时可获取对应的消息长�
                _m_sBufferLen = _m_bByteBuffer.getInt(0);
            }
        }
        
        //当长度有效则判断是否到达消息末尾
        int bufLen = _m_bByteBuffer.position();
        int startPos = 0;
        while(0 != _m_sBufferLen && bufLen >= startPos + _m_sBufferLen + 4)
        {
            //到达消息末尾，将消息取出，并清除缓存消息
            ByteBuffer message = ByteBuffer.allocate(_m_sBufferLen);
            message.put(_m_bByteBuffer.array(), startPos + 4, _m_sBufferLen);
            message.flip();

            startPos = startPos + _m_sBufferLen + 4;
            
            //添加消息
            if(_m_bLoged)
            {
                _m_clClient.receiveMes(message);
            }
            else
            {
                //处理登录操作
                _checkLoginMes(message);
            }
            
            //根据长度设置对应消息长度
            if(bufLen - startPos > 4)
            {
                //当缓冲中字节大于2时可获取对应的消息长�
                _m_sBufferLen = _m_bByteBuffer.getInt(startPos);
            }
            else
            {
                _m_sBufferLen = 0;
                break;
            }
        }

        //当数据经过了拷贝则将剩余数据拷贝放入缓存
        if(startPos != 0)
        {
        	ByteBuffer tmpBuf = ByteBuffer.allocate(bufLen - startPos);
        	tmpBuf.put(_m_bByteBuffer.array(), startPos, bufLen - startPos);
        	tmpBuf.flip();
        	
        	_m_bByteBuffer.clear();
        	_m_bByteBuffer.put(tmpBuf);
        }

        //如原先缓存数据未完全放入，此时将剩余数据放入
        if(_buf.remaining() > 0)
        {
            _m_bByteBuffer.put(_buf);
        }
    }

    protected SocketChannel _getSocketChannel() throws Exception
    {
        if(null == _m_scSocket)
            _m_scSocket = SocketChannel.open();
            
        return _m_scSocket;
    }
    
    /*************
     * 未登录情况下对返回信息进行处�
     * 
     * @author alzq.z
     * @time   Feb 19, 2013 10:02:04 PM
     */
    protected void _checkLoginMes(ByteBuffer _mes)
    {
        _m_bLoginIng = false;
        
        try
        {
            S2C_BasicClientVerifyResult msg = new S2C_BasicClientVerifyResult();
            msg.readPackage(_mes);
            
            //获取ID
            _m_iClientID = msg.getSocketID();

            //直接当作登录成功处理，未成功将直接断开
            _m_bLoged = true;
            _m_clClient.LoginSuc(msg.getCustomRetMsg());
        }
        catch (Exception e)
        {
            _logout();
        }
    }
    
    /*************
     * 断开连接
     * 
     * @author alzq.z
     * @time   Feb 19, 2013 10:07:34 PM
     */
    protected void _logout()
    {
        if(null != _m_scSocket)
        {
            try
            {
                _m_scSocket.close();
            }
            catch (IOException e){}
        }
        
        _clearLoginValidate();
    }
    
    /***********
     * 清空连接相关变量
     * 
     * @author alzq.z
     * @time   Feb 19, 2013 10:07:39 PM
     */
    protected void _clearLoginValidate()
    {
        if(_m_bLoged)
        {
            //已经登录了为退出操�
            _m_clClient.Disconnect();
        }
        else if(_m_bLoginIng)
        {
            //正在登录为连接失败操�
            _m_clClient.ConnectFail();
        }
        else
        {
            //其他情况为登录失败操�
            _m_clClient.LoginFail();
        }
            
        _m_bLoged = false;
        _m_bLoginIng = false;
        _m_scSocket = null;
    }
    
    protected void _lockBuf()
    {
        _m_rSendListMutex.lock();
    }
    protected void _unlockBuf()
    {
        _m_rSendListMutex.unlock();
    }
}
