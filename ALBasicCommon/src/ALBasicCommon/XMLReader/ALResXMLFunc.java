package ALBasicCommon.XMLReader;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ALBasicCommon.XMLReader.ReadFileFunc._AALXMLReadingIntListObj;
import ALBasicCommon.XMLReader.ReadFileFunc._AALXMLReadingLongListObj;
import ALBasicCommon.XMLReader.ReadFileFunc._AALXMLReadingObj;

/************************
 * XML文件读取相关函数类
 * 
 * @author alzq.z
 * @email  zhuangfan@vip.163.com
 * @time   Jun 26, 2013 11:55:33 PM
 */
public class ALResXMLFunc
{
    /*************************
     * 查询指定目录下的所有xml后缀名文件
     * 
     * @author alzq.z
     * @time   Jun 26, 2013 11:47:46 PM
     */
    public static ArrayList<File> findAllChildXMLFiles(String _rootFolderPath)
    {
        File rootFolder = new File(_rootFolderPath);

        return _findAllChildXMLFiles(rootFolder);
    }
    protected static ArrayList<File> _findAllChildXMLFiles(File _rootFolder)
    {
        if(null == _rootFolder)
        {
            return null;
        }
        
        File[] filesChildren = _rootFolder.listFiles();

        ArrayList<File> listFolder = new ArrayList<File>();
        ArrayList<File> listFiles = new ArrayList<File>();

        for (File file : filesChildren)
        {
            if (file.isDirectory())
            {
                //过滤一些以.开头的特殊文件，如SVN文件或Git文件
                if (!file.getName().startsWith("."))
                {
                    listFolder.addAll(_findAllChildXMLFiles(file));
                }
            }
            else if (file.getName().toLowerCase().endsWith(".xml"))
            {
                listFiles.add(file);
            }
            else
            {
                // 其他后缀文件不进行处理
            }
        }

        listFolder.addAll(listFiles);

        return listFolder;
    }
    
    /******************
     * 判断节点是否有效
     * 
     * @author alzq.z
     * @time   Jun 27, 2013 12:35:17 AM
     */
    public static boolean isNodeEnable(Node _node)
    {
        if(null == _node || _node.getNodeType() != Node.ELEMENT_NODE)
            return false;
        
        return true;
    }
    
    /**************************
     * 搜索XML文件，查询出所有指定路径的子节点队列，路径使用:进行分隔
     * 
     * @author alzq.z
     * @time   Jun 26, 2013 11:57:03 PM
     */
    public static ArrayList<ALXMLNodeInfo> findAllChildNode(File _xmlFile, String _nodePath)
    {
        ArrayList<ALXMLNodeInfo> retList = new ArrayList<ALXMLNodeInfo>();
        
        //解析路径
        String[] pathArr = _nodePath.split(":");
        
        try
        {
            //开始解析XML文件
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document doc = builder.parse(_xmlFile);
            
            Node rootNode = doc.getDocumentElement();
            
            if(null == rootNode)
                return retList;
            
            //节点名，比对第一个节点路径
            String rootNodeName = rootNode.getNodeName();
            if(!rootNodeName.equalsIgnoreCase(pathArr[0]))
                return retList;
            
            //递归搜索子节点
            if(pathArr.length <= 1)
            {
                //到底直接加入队列
                ALXMLNodeInfo info = new ALXMLNodeInfo();
                info.filePath = _xmlFile.getPath();
                info.node = rootNode;
                
                retList.add(info);
            }
            else
            {
                _findAllChildNode(rootNode, _xmlFile.getPath(), pathArr, 1, retList);
            }
        }
        catch(Exception ex)
        {
            System.out.println("Read File: " + _xmlFile.getPath() + " Error!");
            ex.printStackTrace(); 
        }
        
        return retList;
    }
    public static ArrayList<ALXMLNodeInfo> readXML(String _xmlFilePath, String _nodePath)
    {
        File xmlFile = new File(_xmlFilePath);
        //判断文件是否存在
        if(!xmlFile.exists())
            return null;
        
        //进行读取操作
        return findAllChildNode(xmlFile, _nodePath);
    }

    /********************
     * 从节点中进行详细的读取操作
     * 
     * @author alzq.z
     * @time   Jun 27, 2013 12:29:59 AM
     */
    public static <T> void readXML(ALXMLNodeInfo _nodeInfo, T _argObj, _AALXMLReadingObj<T> _readingObj)
    {
        if(null == _readingObj)
            return ;
        
        readXML(_nodeInfo.node, _argObj, _readingObj);
    }
    public static <T> void readXML(Node _rootNode, T _argObj, _AALXMLReadingObj<T> _readingObj)
    {
        if(null == _readingObj)
            return ;
        
        NodeList childList = _rootNode.getChildNodes();
        for(int i = 0; i < childList.getLength(); i++)
        {
            Node childNode = childList.item(i);
            
            if(!ALResXMLFunc.isNodeEnable(childNode))
                continue;
            
            String name = childNode.getNodeName();
            
            //尝试进行节点处理，未处理则进行后续的内容处理
            if(_readingObj.readingNode(_argObj, childNode, name))
                continue;
            
            String value = childNode.getTextContent();
            
            if(null == value || value.trim().isEmpty())
                continue;
            
            //读取具体的信息内容
            _readingObj.readingNodeValue(_argObj, name, value);
        }
    }
    
    
    /*************************
     * 根据节点路径，将所有符合的子节点对象都返回
     * 
     * @author alzq.z
     * @time   Aug 12, 2013 10:24:10 PM
     */
    protected static void _findAllChildNode(
            Node _rootNode, String _filePath, String[] _pathArr, int _pathDeep, ArrayList<ALXMLNodeInfo> _recNodeList)
    {
        if(null == _rootNode)
            return ;
        
        //遍历子节点操作
        NodeList childrenNodes = _rootNode.getChildNodes();
        for(int i = 0; i < childrenNodes.getLength(); i++)
        {
            Node childNode = childrenNodes.item(i);
            if(!isNodeEnable(childNode))
                continue;
            
            //比对节点名称与节点路径
            String nodeName = childNode.getNodeName();
            if(nodeName.equalsIgnoreCase(_pathArr[_pathDeep]))
            {
                int nextDeep = _pathDeep + 1;
                if(nextDeep >= _pathArr.length)
                {
                    //到底直接加入队列
                    ALXMLNodeInfo info = new ALXMLNodeInfo();
                    info.filePath = _filePath;
                    info.node = childNode;
                    
                    _recNodeList.add(info);
                }
                else
                {
                    //继续搜索下一层
                    _findAllChildNode(childNode, _filePath, _pathArr, nextDeep, _recNodeList);
                }
            }
        }
    }

    /********************
     * 读取指定标识作为名称的整形列表的对象
     * 
     * @author alzq.z
     * @time   Jun 27, 2013 12:29:59 AM
     */
    public static ArrayList<Integer> readIntList(Node _node, String _nodeName)
    {
        ArrayList<Integer> intList = new ArrayList<Integer>();
        
        ALResXMLFunc.readXML(
                _node
                , intList
                , new _AALXMLReadingIntListObj(_nodeName)
                    {
                        @Override
                        public boolean readingNode(ArrayList<Integer> _intList, Node _node, String _name)
                        {
                            return false;
                        }
                        
                        @Override
                        public void readingNodeValue(ArrayList<Integer> _intList, String _name, String _value)
                        {
                            if(_name.equalsIgnoreCase(getNodeName()))
                            {
                                _intList.add(Integer.parseInt(_value));
                            }
                        }
                    }
                );
        
        return intList;
    }
    public static ArrayList<Integer> readIntList(String _str)
    {
        if(null == _str || _str.isEmpty())
            return null;
        
        ArrayList<Integer> intList = new ArrayList<Integer>();
        
        String[] strs = _str.split(":");
        for(int i = 0; i < strs.length; i++)
        {
            if(strs[i].isEmpty())
                continue;
            
            intList.add(Integer.parseInt(strs[i]));
        }
        
        return intList;
    }
    public static ArrayList<Long> readLongList(Node _node, String _nodeName)
    {
        ArrayList<Long> longList = new ArrayList<Long>();
        
        ALResXMLFunc.readXML(
                _node
                , longList
                , new _AALXMLReadingLongListObj(_nodeName)
                    {
                        @Override
                        public boolean readingNode(ArrayList<Long> _longList, Node _node, String _name)
                        {
                            return false;
                        }
                        
                        @Override
                        public void readingNodeValue(ArrayList<Long> _longList, String _name, String _value)
                        {
                            if(_name.equalsIgnoreCase(getNodeName()))
                            {
                                _longList.add(Long.parseLong(_value));
                            }
                        }
                    }
                );
        
        return longList;
    }
    public static ArrayList<Long> readLongList(String _str)
    {
        if(null == _str || _str.isEmpty())
            return null;
        
        ArrayList<Long> longList = new ArrayList<Long>();
        
        String[] strs = _str.split(":");
        for(int i = 0; i < strs.length; i++)
        {
            if(strs[i].isEmpty())
                continue;
            
            longList.add(Long.parseLong(strs[i]));
        }
        
        return longList;
    }
}