/*
 * Method class represents the data structure of method (or function)
 * including
 *
 * --------------------------------------
 * |    method name,                    |
 * |    method argument,                |
 * |    and return type of the method   |
 * --------------------------------------
 *
 * Note:
 *  For each method there exist only one unique name of a method
 *
 *  For each method there may exist multiple argument and return type which is seperated by ","(comma)
 *
 *
 *
 */

package Data_Structure;

/**
 *
 * @author Harsh Soni
 */
public class Method {
    String name;
    String argument;
    String return_v;
    public Method()
    {
        name="";
        argument="";
        return_v="";
    }
    public void setArgument(String arg)
    {
        if(argument.equals(""))
            argument=arg;
        else
            argument+=", "+arg;
    }
    public void setName(String nm)
    {
        name=nm;
    }
    public void setReturn_v(String ret)
    {
        if(return_v.equals(""))
                return_v=ret;
        else
            return_v+=", "+ret;
    }
    public String getArgument()
    {
        return argument;
    }
    public String getName()
    {
        return name;
    }
    public String getReturn_v()
    {
        return return_v;
    }
}