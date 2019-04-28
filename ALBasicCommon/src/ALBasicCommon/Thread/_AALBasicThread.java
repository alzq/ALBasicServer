package ALBasicCommon.Thread;

import ALBasicCommon.ALBasicEnum.ThreadStat;

/********************
 * 本函数包中基本的线程类对象
 * 
 * @author alzq.z
 * @email  zhuangfan@vip.163.com
 * @time   Feb 20, 2013 10:44:51 PM
 */
public abstract class _AALBasicThread extends Thread
{
	//线程Id
	private long _m_lThreadId;
	
    /** 线程状态 */
    private ThreadStat _m_eThreadStat;
    /** 是否需要即时响应任务 */
    private boolean _m_bIsImdThread;
    
    public _AALBasicThread(boolean _isSyn)
    {
        _m_eThreadStat = ThreadStat.INIT;
        _m_bIsImdThread = _isSyn;
    }
    
    public long getThreadId() {return _m_lThreadId;}
    public ThreadStat getThreadStat() {return _m_eThreadStat;}
    public boolean isImd() {return _m_bIsImdThread;}
    
    /******************
     * 线程执行函数
     * 
     * @author alzq.z
     * @time   Feb 20, 2013 10:46:17 PM
     */
    public final void run()
    {
        _m_eThreadStat = ThreadStat.RUNNING;
        
        //获得当前线程ID
        _m_lThreadId = Thread.currentThread().getId();
        ALBasicThreadMgr.getInstance().regThread(this);
        
        _run();

        //注销自己
        ALBasicThreadMgr.getInstance().unregThread(this);
        _m_eThreadStat = ThreadStat.STOP;
    }
    
    /****************
     * 需要重载的线程执行体
     * 
     * @author alzq.z
     * @time   Feb 20, 2013 10:46:45 PM
     */
    protected abstract void _run();
}
