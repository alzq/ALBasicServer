package ALBasicServer.ALServerSynTask;

import java.util.LinkedList;

import ALBasicCommon.Thread._AALBasicThread;
import ALBasicServer.ALTask._IALSynTask;

public class ALSynTimingTaskCheckThread extends _AALBasicThread
{
    /** 线程是否退出 */
    private boolean _m_bThreadExit;
    private int _m_iCheckTime;
    
    public ALSynTimingTaskCheckThread()
    {
		super(true);
		
        _m_bThreadExit = false;
        _m_iCheckTime = ALSynTaskManager.getInstance().getTaskCheckTime();
    }
    
    public void exitThread()
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
     * @time   Feb 20, 2013 11:05:35 PM
     */
    @Override
    protected void _run()
    {
        //用于存放需要处理的任务的队列
        LinkedList<_IALSynTask> popTaskList = new LinkedList<_IALSynTask>();
        
        while(!_m_bThreadExit)
        {
            //获取需要处理的所有任务队列的队列
            ALSynTaskManager.getInstance()._popTimerTask(popTaskList);

            //逐个任务的插入到当前任务队列中
            ALSynTaskManager.getInstance()._registerTaskList(popTaskList);
            
            //休眠指定精度时间
            try {
                sleep(_m_iCheckTime);
            } catch (InterruptedException e) {}
        }
    }
}
