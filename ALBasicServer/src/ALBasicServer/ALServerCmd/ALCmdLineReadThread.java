package ALBasicServer.ALServerCmd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import ALBasicServer.ALThread._AALServerBasicThread;

/**********************
 * 命令行的读取线程
 * 
 * @author alzq.z
 * @email  zhuangfan@vip.163.com
 * @time   Oct 5, 2013 12:15:14 PM
 */
public class ALCmdLineReadThread extends _AALServerBasicThread
{
    public ALCmdLineReadThread() 
    {
		super(false);
	}

	/******************
     * 线程执行函数
     * 
     * @author alzq.z
     * @time   Feb 20, 2013 11:05:54 PM
     */
    @Override
    protected void _run()
    {
        //获取读取对象
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String cmd = null;
        
        while(true)
        {
            try
            {
                cmd = reader.readLine();
            } 
            catch (IOException e)
            {
                e.printStackTrace();
                return ;
            }
            
            //添加处理对象
            if(null == cmd || cmd.isEmpty())
            {
                try
                {
                    Thread.sleep(100);
                } 
                catch (Exception e)
                {}
            }
            else
            {
                ALCmdDealerManager.getInstance().addCmd(cmd);
            }
        }
    }
}
