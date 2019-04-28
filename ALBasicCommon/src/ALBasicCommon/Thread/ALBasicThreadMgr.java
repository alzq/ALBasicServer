package ALBasicCommon.Thread;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

/********************
 * 基础线程管理器
 * 
 * @author alzq.z
 * @email  zhuangfan@vip.163.com
 * @time   Feb 20, 2013 10:44:51 PM
 */
public class ALBasicThreadMgr extends Thread
{
	private static ALBasicThreadMgr _g_instance = new ALBasicThreadMgr();
	public static ALBasicThreadMgr getInstance()
	{
		if(null == _g_instance)
			_g_instance = new ALBasicThreadMgr();
		
		return _g_instance;
	}
	
	/********
	 * 获取当前线程数据
	 * @param _thread
	 * @param _threadId
	 */
	public static _AALBasicThread getCurThreadInfo()
	{
        long curThreadId = Thread.currentThread().getId();
		return getInstance().getThread(curThreadId);
	}
	
	//线程管理器
	private HashMap<Long, _AALBasicThread> _m_hsThreadTable;
	private ReentrantLock _m_mutex;
	
	protected ALBasicThreadMgr()
	{
		_m_hsThreadTable = new HashMap<Long, _AALBasicThread>();
		_m_mutex = new ReentrantLock();
	}

	protected void _lock() {_m_mutex.lock();}
	protected void _unlock() {_m_mutex.unlock();}
	
	/********
	 * 注册线程
	 * @param _thread
	 * @param _threadId
	 */
	public void regThread(_AALBasicThread _thread)
	{
		if(null == _thread)
			return ;
		
		_lock();
		
		try
		{
			_m_hsThreadTable.put(_thread.getThreadId(), _thread);
		}
		finally
		{
			_unlock();
		}
	}
	
	/********
	 * 注册线程
	 * @param _thread
	 * @param _threadId
	 */
	public void unregThread(_AALBasicThread _thread)
	{
		if(null == _thread)
			return ;
		
		_lock();
		
		try
		{
			_m_hsThreadTable.remove(_thread.getThreadId());
		}
		finally
		{
			_unlock();
		}
	}
	
	/********
	 * 获取线程信息对象
	 * @param _thread
	 * @param _threadId
	 */
	public _AALBasicThread getThread(long _threadId)
	{
		_lock();
		
		try
		{
			return _m_hsThreadTable.get(_threadId);
		}
		finally
		{
			_unlock();
		}
	}
}
