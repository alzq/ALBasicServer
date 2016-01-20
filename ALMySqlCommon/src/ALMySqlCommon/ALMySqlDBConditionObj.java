package ALMySqlCommon;

import java.util.Iterator;
import java.util.List;

import ALMySqlCommon.ALMySqlCommonObj._IALMySqlBaseDBBO;

/**
 * SQL语句选择条件对象<br>
 * 能组成等于，大于，小于，大于等于，小于等于，like，in，条件嵌套的条件<br>
 * @author alzq
 *
 */
public class ALMySqlDBConditionObj
{
    private StringBuffer condition;
    private String tablesName;
    private String itemsName;
    private String orderItemsName;
    private String orderType;
    private String groupItemsName;
    private int countTopLvCondition;
    private int countAllCondition;
    
    public ALMySqlDBConditionObj(_IALMySqlBaseDBBO _bo){
        condition=new StringBuffer();
        tablesName=_bo.getTbName();
        itemsName="";
        orderItemsName="";
        orderType="";
        countTopLvCondition=0;
        countAllCondition=0;
    }
    
    public ALMySqlDBConditionObj(){
        condition=new StringBuffer();
        tablesName="";
        itemsName="";
        orderItemsName="";
        orderType="";
        groupItemsName="";
        countTopLvCondition=0;
        countAllCondition=0;
    }
    
    public void delAllConditions()
    {
        condition=new StringBuffer();
        itemsName="";
        orderItemsName="";
        orderType="";
        groupItemsName="";
        countTopLvCondition=0;
        countAllCondition=0;
    }
    
    public int getCountAllCondition() {
        return countAllCondition;
    }

    public int getCountTopLvCondition() {
        return countTopLvCondition;
    }

    public String getTablesName() {
        return tablesName;
    }

    public void setTablesName(String tablesName) {
        this.tablesName = tablesName;
    }

    public String getItemsName() {
        return itemsName;
    }

    public void setItemsName(String itemsName) {
        this.itemsName = itemsName;
    }
    
    /**
     * 获取实际的条件字符串
     * @return
     */
    public String getCondition(){
        String orderS="";
        if(orderItemsName!=null && !orderItemsName.trim().equals(""))
        {
            orderS=" order by "+orderItemsName;
            if(orderType!=null && !orderType.trim().equals(""))
            {
                orderS+=" "+orderType;
            }
        }
        String groupS="";
        if(groupItemsName!=null && !groupItemsName.trim().equals(""))
        {
            orderS=" group by "+groupItemsName;
        }
        return condition.toString()+orderS+groupS;
    }
    
    /**
     * 获取实际的条件字符串与表名-hibernate
     * @return
     */
    public String getConditionAndTables(){
        String con=getCondition();
        if(con.equals("") || con.trim().substring(0,6).equals("order ") || con.trim().substring(0,6).equals("group "))
        {
            return "from "+tablesName+" "+con;
        }
        return "from "+tablesName+" where "+getCondition();
    }
    
    /**
     * 获取SQL语句
     * @return
     */
    public String getSQL(){
        return "select "+itemsName+" "+getConditionAndTables()+";";
    }
    
    /**
     * 添加条件语句
     * @param itemName
     * @param value
     * @return
     */
    private boolean addSQL(String sql,String operate){
        try{
            if(condition.length()>0){
                condition.append(operate+" ");
            }
            condition.append(sql);
            countTopLvCondition++;
            countAllCondition++;
            return true;
        }catch(Exception e){
            System.out.println("Conditions addConditions Error!---alzq.baseClass.Conditions:");
            System.out.println(sql);
            return false;
        }
    }
    

    /**
     * 添加相等条件
     * @param itemName
     * @param value
     * @return
     */
    public boolean addAndEquals(String itemName,Object value){
        return addSQL(itemName+"='"+String.valueOf(value)+"' ","and");
    }
    public boolean addAndEquals(String itemName,int value){
        return addSQL(itemName+"='"+String.valueOf(value)+"' ","and");
    }
    public boolean addAndEqualsItem(String itemName,String value){
        return addSQL(itemName+"="+String.valueOf(value)+" ","and");
    }

    /**
     * 添加不相等条件
     * @param itemName
     * @param value
     * @return
     */
    public boolean addAndNotEquals(String itemName,Object value){
        return addSQL(itemName+"!='"+String.valueOf(value)+"' ","and");
    }
    public boolean addAndNotEquals(String itemName,int value){
        return addSQL(itemName+"!='"+String.valueOf(value)+"' ","and");
    }
    public boolean addAndNotEqualsItem(String itemName,String value){
        return addSQL(itemName+"!="+String.valueOf(value)+" ","and");
    }

    /**
     * 添加Like
     * @param itemName
     * @param value
     * @return
     */
    public boolean addAndLike(String itemName,String likeStr){
        return addSQL(itemName+" like '"+likeStr+"' ","and");
    }

    /**
     * 添加数字比较
     * @param itemName
     * @param operator
     * @param value
     * @return
     */
    private boolean addAndNumberJudge(String itemName,String operator,Object value){
        return addSQL(itemName+" "+operator+" "+String.valueOf(value)+" ","and");
    }

    /**
     * 添加数字小于条件
     * @param itemName
     * @param value
     * @return
     */
    public boolean addAndSmallThan(String itemName,int value){
        return addAndNumberJudge(itemName,"<",String.valueOf(value));
    }
    public boolean addSmallThan(String itemName,double value){
        return addAndNumberJudge(itemName,"<",String.valueOf(value));
    }
    public boolean addSmallThan(String itemName,float value){
        return addAndNumberJudge(itemName,"<",String.valueOf(value));
    }

    /**
     * 添加数字大于条件
     * @param itemName
     * @param value
     * @return
     */
    public boolean addAndLardgeThan(String itemName,int value){
        return addAndNumberJudge(itemName,">",String.valueOf(value));
    }
    public boolean addAndLardgeThan(String itemName,double value){
        return addAndNumberJudge(itemName,">",String.valueOf(value));
    }
    public boolean addAndLardgeThan(String itemName,float value){
        return addAndNumberJudge(itemName,">",String.valueOf(value));
    }

    /**
     * 添加数字小于等于条件
     * @param itemName
     * @param value
     * @return
     */
    public boolean addAndSmallAndEquals(String itemName,int value){
        return addAndNumberJudge(itemName,"<=",String.valueOf(value));
    }
    public boolean addAndSmallAndEquals(String itemName,double value){
        return addAndNumberJudge(itemName,"<=",String.valueOf(value));
    }
    public boolean addAndSmallAndEquals(String itemName,float value){
        return addAndNumberJudge(itemName,"<=",String.valueOf(value));
    }
    
    /**
     * 添加数字大于等于条件
     * @param itemName
     * @param value
     * @return
     */
    public boolean addAndLardgeAndEquals(String itemName,int value){
        return addAndNumberJudge(itemName,">=",String.valueOf(value));
    }
    public boolean addAndLardgeAndEquals(String itemName,double value){
        return addAndNumberJudge(itemName,">=",String.valueOf(value));
    }
    public boolean addAndLardgeAndEquals(String itemName,float value){
        return addAndNumberJudge(itemName,">=",String.valueOf(value));
    }
    
    /**
     * 添加其他比较
     * @param itemName
     * @param operator
     * @param value
     * @return
     */
    private boolean addAndJudge(String itemName,String operator,Object value){
        return addSQL(itemName+" "+operator+" '"+String.valueOf(value)+"' ","and");
    }

    public boolean addAndSmallThan(String itemName,Object value){
        return addAndJudge(itemName,"<",value);
    }
    public boolean addAndLardgeThan(String itemName,Object value){
        return addAndJudge(itemName,">",value);
    }
    public boolean addAndSmallAndEquals(String itemName,Object value){
        return addAndJudge(itemName,"<=",value);
    }
    public boolean addAndLardgeAndEquals(String itemName,Object value){
        return addAndJudge(itemName,">=",value);
    }
    
    /**
     * 数据库中字段与字段进行比较
     * @param itemName
     * @param item
     * @return
     */
    private boolean addAndItemJudge(String itemName,String operator,String item){
        return addSQL(itemName+" "+operator+" "+item+" ","and");
    }

    public boolean addAndSmallThanItem(String itemName,String item){
        return addAndItemJudge(itemName,"<",item);
    }
    public boolean addAndLardgeThanItem(String itemName,String item){
        return addAndItemJudge(itemName,">",item);
    }
    public boolean addAndSmallAndEqualsItem(String itemName,String item){
        return addAndItemJudge(itemName,"<=",item);
    }
    public boolean addAndLardgeAndEqualsItem(String itemName,String item){
        return addAndItemJudge(itemName,">=",item);
    }

    /**
     * 添加in条件
     * @param itemName
     * @param list
     * @return
     */
    @SuppressWarnings("rawtypes")
    public boolean addAndInList(String itemName,List list){
        try{
            if(condition.length()>0){
                condition.append("and ");
            }
            condition.append(itemName+" in ");
            
            condition.append("(");
            for(Iterator iter=list.iterator();iter.hasNext();)
            {
                condition.append("'"+iter.next().toString()+"'");
                if(iter.hasNext()){condition.append(",");}
            }
            condition.append(")");
            
            condition.append(" ");
            countTopLvCondition++;
            countAllCondition++;
            return true;
        }catch(Exception e){
            System.out.println("Conditions addInList Error!---alzq.baseClass.Conditions:");
            return false;
        }
    }

    /**
     * 添加is null条件
     * @param itemName
     * @return
     */
    public boolean addAndIsNull(String itemName)
    {
        return addSQL(itemName+" is null ","and");
    }

    /**
     * 添加is not null条件
     * @param itemName
     * @return
     */
    public boolean addAndIsNotNull(String itemName)
    {
        return addSQL(itemName+" is not null ","and");
    }

    /**
     * 添加条件嵌套
     * @param con
     * @return
     */
    public boolean addAndCondition(ALMySqlDBConditionObj con){
        try{
            if(condition.length()>0){
                condition.append("and ");
            }
            condition.append("( ");
            condition.append(con.getCondition());
            condition.append(") ");
            countTopLvCondition++;
            countAllCondition+=con.getCountAllCondition();
            return true;
        }catch(Exception e){
            System.out.println("Conditions addCondition Error!---alzq.baseClass.Conditions:");
            return false;
        }
    }
    


    /**
     * 添加相等条件-or
     * @param itemName
     * @param value
     * @return
     */
    public boolean addOrEquals(String itemName,Object value){
        return addSQL(itemName+"='"+String.valueOf(value)+"' ","or");
    }
    public boolean addOrEquals(String itemName,int value){
        return addSQL(itemName+"='"+String.valueOf(value)+"' ","or");
    }
    public boolean addOrEqualsItem(String itemName,String value){
        return addSQL(itemName+"="+String.valueOf(value)+" ","or");
    }

    /**
     * 添加不相等条件-or
     * @param itemName
     * @param value
     * @return
     */
    public boolean addOrNotEquals(String itemName,Object value){
        return addSQL(itemName+"!='"+String.valueOf(value)+"' ","or");
    }
    public boolean addOrNotEquals(String itemName,int value){
        return addSQL(itemName+"!='"+String.valueOf(value)+"' ","or");
    }
    public boolean addOrNotEqualsItem(String itemName,String value){
        return addSQL(itemName+"!="+String.valueOf(value)+" ","or");
    }

    /**
     * 添加Like--or
     * @param itemName
     * @param value
     * @return
     */
    public boolean addOrLike(String itemName,String likeStr){
        return addSQL(itemName+" like '"+likeStr+"' ","or");
    }

    /**
     * 添加数字比较--or
     * @param itemName
     * @param operator
     * @param value
     * @return
     */
    private boolean addOrNumberJudge(String itemName,String operator,Object value){
        return addSQL(itemName+" "+operator+" "+String.valueOf(value)+" ","or");
    }

    /**
     * 添加数字小于条件--or
     * @param itemName
     * @param value
     * @return
     */
    public boolean addOrSmallThan(String itemName,int value){
        return addOrNumberJudge(itemName,"<",String.valueOf(value));
    }
    public boolean addOrSmallThan(String itemName,double value){
        return addOrNumberJudge(itemName,"<",String.valueOf(value));
    }
    public boolean addOrSmallThan(String itemName,float value){
        return addOrNumberJudge(itemName,"<",String.valueOf(value));
    }

    /**
     * 添加数字大于条件--or
     * @param itemName
     * @param value
     * @return
     */
    public boolean addOrLardgeThan(String itemName,int value){
        return addOrNumberJudge(itemName,">",String.valueOf(value));
    }
    public boolean addOrLardgeThan(String itemName,double value){
        return addOrNumberJudge(itemName,">",String.valueOf(value));
    }
    public boolean addOrLardgeThan(String itemName,float value){
        return addOrNumberJudge(itemName,">",String.valueOf(value));
    }

    /**
     * 添加数字小于等于条件--or
     * @param itemName
     * @param value
     * @return
     */
    public boolean addOrSmallAndEquals(String itemName,int value){
        return addOrNumberJudge(itemName,"<=",String.valueOf(value));
    }
    public boolean addOrSmallAndEquals(String itemName,double value){
        return addOrNumberJudge(itemName,"<=",String.valueOf(value));
    }
    public boolean addOrSmallAndEquals(String itemName,float value){
        return addOrNumberJudge(itemName,"<=",String.valueOf(value));
    }
    
    /**
     * 添加数字大于等于条件--or
     * @param itemName
     * @param value
     * @return
     */
    public boolean addOrLardgeAndEquals(String itemName,int value){
        return addOrNumberJudge(itemName,">=",String.valueOf(value));
    }
    public boolean addOrLardgeAndEquals(String itemName,double value){
        return addOrNumberJudge(itemName,">=",String.valueOf(value));
    }
    public boolean addOrLardgeAndEquals(String itemName,float value){
        return addOrNumberJudge(itemName,">=",String.valueOf(value));
    }
    
    /**
     * 添加其他比较--or
     * @param itemName
     * @param operator
     * @param value
     * @return
     */
    private boolean addOrJudge(String itemName,String operator,Object value){
        return addSQL(itemName+" "+operator+" '"+String.valueOf(value)+"' ","or");
    }

    public boolean addOrSmallThan(String itemName,Object value){
        return addOrJudge(itemName,"<",value);
    }
    public boolean addOrLardgeThan(String itemName,Object value){
        return addOrJudge(itemName,">",value);
    }
    public boolean addOrSmallAndEquals(String itemName,Object value){
        return addOrJudge(itemName,"<=",value);
    }
    public boolean addOrLardgeAndEquals(String itemName,Object value){
        return addOrJudge(itemName,">=",value);
    }
    
    /**
     * 数据库中字段与字段比较--or
     * @param itemName
     * @param operator
     * @param item
     * @return
     */
    private boolean addOrItemJudge(String itemName,String operator,String item){
        return addSQL(itemName+" "+operator+" "+item+" ","or");
    }

    public boolean addOrSmallThanItem(String itemName,String item){
        return addOrItemJudge(itemName,"<",item);
    }
    public boolean addOrLardgeThanItem(String itemName,String item){
        return addOrItemJudge(itemName,">",item);
    }
    public boolean addOrSmallAndEqualsItem(String itemName,String item){
        return addOrItemJudge(itemName,"<=",item);
    }
    public boolean addOrLardgeAndEqualsItem(String itemName,String item){
        return addOrItemJudge(itemName,">=",item);
    }

    /**
     * 添加in条件--or
     * @param itemName
     * @param list
     * @return
     */
    @SuppressWarnings("rawtypes")
    public boolean addOrInList(String itemName,List list){
        try{
            if(condition.length()>0){
                condition.append("or ");
            }
            condition.append(itemName+" in ");
            
            condition.append("(");
            for(Iterator iter=list.iterator();iter.hasNext();)
            {
                condition.append("'"+iter.next().toString()+"'");
                if(iter.hasNext()){condition.append(",");}
            }
            condition.append(")");
            
            condition.append(" ");
            countTopLvCondition++;
            countAllCondition++;
            return true;
        }catch(Exception e){
            System.out.println("Conditions addOrInList Error!---alzq.baseClass.Conditions:");
            return false;
        }
    }

    /**
     * 添加is null条件
     * @param itemName
     * @return
     */
    public boolean addOrIsNull(String itemName)
    {
        return addSQL(itemName+" is null ","or");
    }

    /**
     * 添加is not null条件
     * @param itemName
     * @return
     */
    public boolean addOrIsNotNull(String itemName)
    {
        return addSQL(itemName+" is not null ","or");
    }

    /**
     * 添加条件嵌套--or
     * @param con
     * @return
     */
    public boolean addOrCondition(ALMySqlDBConditionObj con){
        try{
            if(condition.length()>0){
                condition.append("or ");
            }
            condition.append("( ");
            condition.append(con.getCondition());
            condition.append(") ");
            countTopLvCondition++;
            countAllCondition+=con.getCountAllCondition();
            return true;
        }catch(Exception e){
            System.out.println("Conditions addOrCondition Error!---alzq.baseClass.Conditions:");
            return false;
        }
    }

    public String getOrderItemsName() {
        return orderItemsName;
    }

    public void setOrderItemsName(String orderItemsName) {
        this.orderItemsName = orderItemsName;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        if(orderType.toLowerCase().equals("asc") || orderType.toLowerCase().equals("desc"))
        {
            this.orderType = orderType;
        }
    }

    public String getGroupItemsName() {
        return groupItemsName;
    }

    public void setGroupItemsName(String groupItemsName) {
        this.groupItemsName = groupItemsName;
    }
}
