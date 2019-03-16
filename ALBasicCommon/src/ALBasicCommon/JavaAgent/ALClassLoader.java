package ALBasicCommon.JavaAgent;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**************
 * 远程加载类的处理对象
 * 
 * @author alzq.z
 * @email  zhuangfan@vip.163.com
 * @time   2019年3月16日 下午2:53:03
 */
public class ALClassLoader extends URLClassLoader
{

    /***************
     * 加载并运行jar包文件中指定类的指定函数，函数必须是不带参数的
     * 
     * @param _jarFileFullPath 带入jar包文件的绝对路径
     * @param _className 需要加载的类对象名称，最好带绝对路径
     * @param _methodName 函数名
     * @author alzq.z
     * @time   2019年3月16日 下午2:51:42
     */
    public static boolean execJarMethod(String _jarFileFullPath, String _className, String _methodName)
    {
        URL[] urls = new URL[] {};
        ALClassLoader classLoader = new ALClassLoader(urls, null);
        try
        {
            File jarFile = new File(_jarFileFullPath);
            if (!jarFile.exists())
            {
                System.out.println(_jarFileFullPath + " not exists.");
                return false;
            }
            
            //加载jar文件
            classLoader.addJar(jarFile.toURI().toURL());
            Class<?> clazz = null;
            try
            {
                clazz = classLoader.loadClass(_className);
            }
            catch (Exception e)
            {
                System.out.println("Load class: " + _className + " Failed!!! err: " + e.getMessage());
                return false;
            }
            
            Method method = null;
            try
            {
                method = clazz.getDeclaredMethod(_methodName);
            }
            catch (Exception e)
            {
                System.out.println("Can not find class: " + _className + " Method: " + _methodName + " Reflection!!! err: " + e.getMessage());
                return false;
            }
            try
            {
                method.invoke(null);
            }
            catch (Exception e)
            {
                System.out.printf("Class:{} Method:{} Invoke Failed", _className, _methodName, e);
                return false;
            }
            
            System.out.printf("Invoke Class:{} Method:{} Success!", _className, _methodName);
            return true;
        }
        catch (Exception e)
        {
            System.out.printf("Class:{} Method:{} Invoke Failed", _className, _methodName, e);
            return false;
        }
        finally
        {
            try
            {
                classLoader.close();
            }
            catch (Exception e2)
            {
            }
        }
    }
    
    public ALClassLoader(URL[] urls)
    {
        super(urls);
    }

    public ALClassLoader(URL[] urls, ClassLoader parent)
    {
        super(urls, parent);
    }

    public void addJar(URL url)
    {
        this.addURL(url);
    }
}
