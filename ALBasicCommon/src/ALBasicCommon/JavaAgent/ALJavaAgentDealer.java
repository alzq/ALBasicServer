package ALBasicCommon.JavaAgent;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;

import ALJavaAgent.ALJavaAgentMain;

/****************
 * 加载java agent动态变更库的相关处理对象
 * 
 * @author alzq.z
 * @email  zhuangfan@vip.163.com
 * @time   2019年3月16日 下午1:50:32
 */
public class ALJavaAgentDealer
{
    private static VirtualMachine g_VM;
    private static String g_sPid;

    /************
     * 优先初始化static成员变量
     */
    static
    {
        try
        {
            //获取当前进程pid
            String name = ManagementFactory.getRuntimeMXBean().getName();
            g_sPid = name.split("@")[0];
            System.out.println("Current pid: " + g_sPid);
        }
        catch (Exception e)
        {
            System.out.printf("ALJavaAgentDealer Init failed!! Exception: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    /*******************
     * 进行类重载的处理函数，带入对应的重载库目录，以及需要重载的类路径
     * 
     * @param _agentJarPath 带入加载的ALJavaAgent的Jar包绝对路径，包用已经打好的示例包即可
     * @param _rootPath 需要重载的新类的包文件根路径，用于与相对路径拼凑获得实际的class文件路径
     * @param _classArr 需要重载的class文件类路径队列
     * @author alzq.z
     * @time   2019年3月16日 下午2:41:10
     */
    public static boolean hotAttachClass(String _agentJarPath, String _rootPath, String[] _classArr)
            throws ClassNotFoundException, IOException, UnmodifiableClassException, AttachNotSupportedException, AgentLoadException, AgentInitializationException
    {
        //进行环境初始化
        if (!_init(_agentJarPath))
        {
            System.out.println("Init JavaAgent Env failed !!!");
            return false;
        }
        
        try
        {
            //逐个构造需要重定义的数据
            List<ClassDefinition> classDefList = new ArrayList<ClassDefinition>();
            for (String className : _classArr)
            {
                Class<?> c = Class.forName(className);
                //拼凑实际需要加载的class路径
                String classPath = _rootPath + className.replace(".", "/") + ".class";
                System.out.println("Redefined class: " + classPath);
                try
                {
                    //读取class文件
                    FileInputStream is = new FileInputStream(classPath);
                    byte[] bytesFromFile = new byte[is.available()];
                    is.read(bytesFromFile);
                    is.close();
                    
                    ClassDefinition classDefinition = new ClassDefinition(c, bytesFromFile);
                    //放入替换数据结构中
                    classDefList.add(classDefinition);
                }
                catch (Exception e)
                {
                    System.out.println("Redefine class: " + classPath + " failed !!! Exception: " + e.getMessage());
                    return false;
                }

                System.out.println("Redefined Inited Done class: " + classPath);
            }
            
            //执行实际的重定义事件
            ClassDefinition[] dealArr = new ClassDefinition[classDefList.size()];
            classDefList.toArray(dealArr);
            //执行
            ALJavaAgentMain.getInstrumentation().redefineClasses(dealArr);
        }
        catch (Exception e)
        {
            System.out.println("HotAttachClass failed!!!" + e.getMessage());
            e.printStackTrace();
            
            return false;
        }
        finally
        {
            //释放注入状态
            _discard();
        }
        
        //参会成功
        return true;
    }

    /****************
     * 初始化函数，返回是否初始化成功。函数内获取java的虚拟机环境
     * 
     * @author alzq.z
     * @time   2019年3月16日 下午1:42:25
     */
    private static boolean _init(String _agentJarPath)
            throws IOException, AttachNotSupportedException, AgentLoadException, AgentInitializationException
    {
        // 虚拟机加载
        try
        {
            System.out.printf("Start attach to pid： {}", g_sPid);
            g_VM = VirtualMachine.attach(g_sPid);
            
            //模拟一个指定目录，用以获取java的环境目录
            System.out.println("Start loadAgent: " + _agentJarPath);
            g_VM.loadAgent(_agentJarPath);
            
            //获取Instrumentation对象，用于重载类对象，通过加载对应jar包，再通过jar包中的对应类获取Instrumentation
            Instrumentation instrumentation = ALJavaAgentMain.getInstrumentation();
            
            //检测返回的
            if (null == instrumentation)
            {
                System.out.println("initInstrumentation must not be null");
                return false;
            }
            
            return true;
        }
        catch (Exception e)
        {
            System.out.println("init VirtualMachine.attach failed !! " + e.getMessage());
            e.printStackTrace();
            
            return false;
        }
    }

    /******************
     * 资源释放函数
     * 
     * @author alzq.z
     * @time   2019年3月16日 下午2:50:51
     */
    private static void _discard() throws IOException
    {
        if (g_VM != null)
        {
            g_VM.detach();
        }
    }
}
