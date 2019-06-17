package TestMain;

import ALBasicServer.ALServerSynTask.ALSynTaskManager;
import ALBasicServer.ALTask._IALSynTask;

public class StartTestLockTask implements _IALSynTask
{
	@Override
	public void run() {
		for(int i = 0; i < 10; i++)
		{
			TestLockTask task = new TestLockTask("task_" + i);
			ALSynTaskManager.getInstance().regTask(task);
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
