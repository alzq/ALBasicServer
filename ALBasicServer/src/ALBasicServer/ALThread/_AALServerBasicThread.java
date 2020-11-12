package ALBasicServer.ALThread;

import ALBasicCommon.Thread._AALBasicThread;
import ALBasicServer.ALBasicServerConf;


/************************
 * 服务器部分的线程基类处理，在结束的时候会自动释放锁
 * @author alzq
 *
 */
public abstract class _AALServerBasicThread extends _AALBasicThread
{
    /** 本线程对应锁信息的存储结构体 */
    private ALThreadMutexMgr _m_tmrThreadMutexMgr;

    public _AALServerBasicThread()
    {
		super();

        _m_tmrThreadMutexMgr  = null;
    }
    public _AALServerBasicThread(boolean _isSyn)
    {
		super(_isSyn);

        _m_tmrThreadMutexMgr  = null;
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
        if(ALBasicServerConf.getInstance().getCheckMutex())
        {
            //获得当前线程ID
            long threadID = Thread.currentThread().getId();
            _m_tmrThreadMutexMgr = ALThreadManager.getInstance().regThread(threadID);
            
            //注册失败直接返回，不进行线程体操作
            if(null == _m_tmrThreadMutexMgr)
                return ;
        }
    }
    @Override
    protected void _onThreadEnd()
    {
        //当有进行锁检测时需要尝试释放所有注册锁，避免异常的操作导致锁未释放
        if(ALBasicServerConf.getInstance().getCheckMutex())
            _m_tmrThreadMutexMgr.releaseAllMutex();
    }
}
