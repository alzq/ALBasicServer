package ALBasicServer.ALSocket;

import ALBasicServer.ALTask.IALSynTask;

public class SynDisconnectTask implements IALSynTask
{
    private ALBasicServerSocket _m_csSocket;
    
    public SynDisconnectTask(ALBasicServerSocket _socket)
    {
        _m_csSocket = _socket;
    }
    
    @Override
    public void run()
    {
        if(null == _m_csSocket)
            return ;
        
        ALBasicServerSocketListener listener = _m_csSocket.getListener();
        
        if(null == listener)
            return ;
        
        //设置Socket为NULL
        listener.setSocket(null);
        
        listener.disconnect();
    }

}
