package TestMain;

import ALBasicServer.ALBasicServer;
import ALBasicServer.ALServerSynTask.ALSynTaskManager;

public class TestMain {

	public static void main(String[] args) {
		
		ALSynTaskManager.getInstance().regTask(new StartTestLockTask());
		
		ALBasicServer.initBasicServer(1);
	}

}
