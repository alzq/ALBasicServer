package TestMain;

import ALBasicServer.ALBasicMutex.MutexAtom;
import ALBasicServer.ALServerSynTask.ALSynTaskManager;
import ALBasicServer.ALTask._IALSynTask;
import ALServerLog.ALServerLog;

public class TestLockTask implements _IALSynTask
{
	private static MutexAtom _mutex = new MutexAtom();
	
	private String _m_sName;
	
	public TestLockTask(String _name)
	{
		_m_sName = _name;
	}

	@Override
	public void run() {
		_mutex.lock();
		try
		{
			Thread.sleep(1000);
			
			ALServerLog.Fatal("lock: " + _m_sName);
			
			ALSynTaskManager.getInstance().regTask(this);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			_mutex.lock();
		}
	}

}
