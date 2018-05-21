/*
 * This class will create the knowledge structure derived from text as an xml file
 *
 * The xml file will be like
 *
 * -----------------------------------------------------------------
 * |    <entity name='...' parent/instanc_of='...'>                |
 * |        <Attribute_value name='...'>                           |
 * |        <possess entity='...'>                                 |
 * |        <method name='...' arguments='...' return_value='...'> |
 * |    </entity>                                                  |
 * -----------------------------------------------------------------
 *
 */
package Represent;
import Data_Structure.Method;
import Data_Structure.Entity;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;


/**
 * @author Harsh Soni
 */
public class Represent_xml
{
    ArrayList<Method> m;        //Storing the list of methods with its argument and return values
    ArrayList<String> i;        //Storing the "parent class" if it is inherited otherwie stores the class name of the object
    ArrayList<String> p;        //Stores the possess entities
    ArrayList<String> v;        //Stores the attribute values
    private String filename;    //It consist name of xml file that is to be generated
    BufferedWriter bf;          //To write in file
    private String useIterator(
            ArrayList<String> al    //String Arraylist that needs to be traverse
            )
    {
        /*
         *  This method is used to iterate the arraylist of string
         *  it can able to traverse all the element of possess, values and inherited class
         *  It returns the String consisting of all the element seperated by space
         */
        String tmp="";
        Iterator<String> it=al.iterator();
        while(it.hasNext())
        {
            if(tmp.equals(""))
                tmp=it.next();
            else
                tmp+=", "+it.next();
        }
        return tmp;
    }
    public Represent_xml() throws IOException
            //Constructor that initialize the data member
    {
        i=new ArrayList<String>();
        p=new ArrayList<String>();
        v=new ArrayList<String>();
        m=new ArrayList<Method>();
        filename="output.xml";
        bf=new BufferedWriter(new FileWriter(filename,true));// "true" stands for appending file
    }
    public void do_represent(
            LinkedList<Entity> ent
            ) throws IOException
    {
        /*
         * This method will traverse the LinkedList of Entity class
         * for each traversal it will check the type of information contained in the object
         * It seperate each entity and for each entity it stores the information(name of method,possess, attribute value,parent)
         * in the individual ArrayList
         * after entertaining a complete entity it will clear the data structure in order to rebuild for new entity
         */
        while(ent.size()!=0)
        {
            String ent_name=ent.get(0).getName();
            System.out.println("For Entity:"+ ent_name);
            for(int index=0;index<ent.size();index++)
            {
                Entity e=ent.get(index);
                if(e.getName().toUpperCase().equals(ent_name.toUpperCase()))
                {
                    System.out.println("First time enters "+e.getName());
                    if(e.getType()=='i')
                    {
                        if(!contain(i,e.getP_or_i()))
                            i.add(e.getP_or_i());
                        ent.remove(index--);
                    }
                    if(e.getType()=='v')
                    {
                        if(!contain(v,e.getValue()))
                            v.add(e.getValue());
                        ent.remove(index--);
                    }
                    if(e.getType()=='p')
                    {
                        if(!contain(p,e.getPos()))
                            p.add(e.getPos());
                        ent.remove(index--);
                    }
                    //Complex if...else machanism to entertain "method" of entity
                    if(e.getType()=='m')
                    {
                        if(hasMethod(e.getMethod()))
                        {
                            if(e.getReturn_value().equals(""))
                            {
                                if(e.getArgument().equals(""))
                                {
                                    ent.remove(index--);
                                }
                                else
                                {
                                    if(hasArgument(e.getMethod(),e.getArgument()))
                                    {
                                        ent.remove(index--);
                                    }
                                    else
                                    {
                                        Method mt=getMethod(e.getMethod());
                                        mt.setArgument(e.getArgument());
                                        m.set(getMethodIndex(e.getMethod()), mt);   //Replacing existing Method
                                                                        //object with the updated argument
                                        ent.remove(index--);
                                    }

                                }
                            }
                            else
                            {
                                if(e.getArgument().equals(""))
                                {
                                    if(hasReturn(e.getMethod(),e.getReturn_value()))
                                    {
                                        ent.remove(index--);
                                    }
                                    else
                                    {
                                        Method mt=getMethod(e.getMethod());
                                        mt.setReturn_v(e.getReturn_value());
                                        m.set(getMethodIndex(e.getMethod()), mt);   //Replacing existing Method
                                                                        //object with the updated argument
                                        ent.remove(index--);
                                    }
                                }
                                else
                                {
                                    if(hasReturn(e.getMethod(), e.getReturn_value()))
                                    {
                                        if(hasArgument(e.getMethod(), e.getArgument()))
                                        {
                                            ent.remove(index--);
                                        }
                                        else
                                        {
                                            Method mt=getMethod(e.getMethod());
                                            mt.setArgument(e.getArgument());
                                            m.set(getMethodIndex(e.getMethod()), mt);
                                            ent.remove(index--);
                                        }

                                    }
                                    else
                                    {
                                        if(hasArgument(e.getMethod(),e.getArgument()))
                                        {
                                            Method mt=getMethod(e.getMethod());
                                            mt.setArgument(e.getArgument());
                                            mt.setReturn_v(e.getReturn_value());
                                            m.set(getMethodIndex(e.getMethod()), mt);
                                            ent.remove(index--);
                                        }
                                        else
                                        {
                                            Method mt=getMethod(e.getMethod());
                                            mt.setReturn_v(e.getReturn_value());
                                            m.set(getMethodIndex(e.getMethod()), mt);
                                            ent.remove(index--);
                                        }
                                    }
                                }
                            }

                        }
                        else
                        {
                            Method mt=new Method();
                            mt.setName(e.getMethod());
                            if(!e.getArgument().equals(""))
                            {
                                mt.setArgument(e.getArgument());
                            }
                            if(!e.getReturn_value().equals(""))
                            {
                                mt.setReturn_v(e.getReturn_value());
                            }
                            m.add(mt);
                            ent.remove(index--);
                        }
                    }
                }
            }
            //A whole entity is entertained now representing into xml file
            createXML(ent_name.toUpperCase());  //Writing the entity information into the xml file
            i.clear();
            m.clear();
            v.clear();
            p.clear();
        }
        end();  //closing buffered reader
    }
    public void createXML(
            String entnm
            ) throws IOException
    {
        System.out.println("Again called for xml");
        Iterator<Method> met=m.iterator();
        String parent=useIterator(i);
        String values=useIterator(v);
        String possess=useIterator(p);
        bf.write("<Entity name='"+entnm+"'");
        if(!parent.equals(""))
        {
            bf.write(" parent/instanc_of='"+parent+"'");
        }
        bf.write(">\n");
        if(!values.equals(""))
        {
            bf.write("\t<Attribute_values name='"+values+"' />\n");
        }
        if(!possess.equals(""))
        {
            bf.write("\t<Possess entity='"+possess+"' />\n");
        }
        String mnm,mag,mrv;
        while(met.hasNext())
        {
            Method mm=met.next();
            mnm=mm.getName();
            mag=mm.getArgument();
            mrv=mm.getReturn_v();
            if(!mnm.equals(""))
            {
                bf.write("\t<Method name='"+mnm+"'");
                if(!mag.equals(""))
                {
                    bf.write(" arguments='"+mag+"'");
                }
                if(!mrv.equals(""))
                {
                    bf.write(" return_value='"+mrv+"'");
                }
                bf.write(" />\n");
            }
        }
        bf.write("</Entity>\n");
    }
    private boolean hasArgument(String mthd,String arg)
    {
        for(int index=0;index<m.size();index++)
        {
            if(m.get(index).getName().equals(mthd)&&m.get(index).getArgument().contains(arg))
                return true;
        }
        return false;
    }
    private boolean hasReturn(String mthd,String ret)
    {
        for(int index=0;index<m.size();index++)
        {
            if(m.get(index).getName().equals(mthd)&&m.get(index).getReturn_v().contains(ret))
                return true;
        }
        return false;
    }
    private Method getMethod(String mname)
    {
        Method m1;
        for(int index=0;index<m.size();index++)
        {
            m1=m.get(index);
            if(m1.getName().equals(mname))
                return m1;
        }
        return null;
    }
    private boolean hasMethod(String mname)
    {
        Method m1;
        for(int index=0;index<m.size();index++)
        {
            m1=m.get(index);
            if(m1.getName().equals(mname))
                return true;
        }
        return false;
    }
    private int getMethodIndex(String mname)
    {
        Method m1;
        for(int index=0;index<m.size();index++)
        {
            m1=m.get(index);
            if(m1.getName().equals(mname))
                return index;
        }
        return -1;
    }
    private boolean contain(ArrayList<String> al,String match)
    {
        for(int j=0;j<al.size();j++)
        {
            if(al.get(j).equals(match))
                return true;
        }
        return false;
    }
    
    public void end() throws IOException
    {
        bf.close();
    }
    
}