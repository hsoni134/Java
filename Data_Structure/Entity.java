/*
 This class provides data structure to store information about an entity.
 */

package Data_Structure;

/**
 *
 * @author Harsh Soni
 */
public class Entity {
    private char type;
    /*
     *  type indicates which kind of knowledge does this entity class posses
     * type 'v'= value (adjectives) of subject
     * type 'p'= posssess (noun) of subject
     * type 'm'= method(verb) argument(noun) return_value(adjectives) of subject
     * type 'i'= inhertance/instance_of (noun) of subject
     */
    private String p_or_i;
    private String name;
    private String pos;
    private String value;
    private String method;
    private String argument;
    private String return_value;
    public Entity(char type,String name,String pos,String value,String method, String argument,String return_value,String p_or_i)
    {

        this.type=type;
        this.name=name;
        this.pos=pos;
        this.p_or_i=p_or_i;
        this.value=value;
        this.method=method;
        this.argument=argument;
        this.return_value=return_value;
    }
    @Override
    public String toString()
    {
        return "Knowledge type: "+type+" Entity: "+name+" pos: "+pos+" value: "+ value+" method: "+method+" argument:"+ argument+" return value: "+return_value+" parent/instance :"+p_or_i;
    }
    public char getType()
    {
        return type;
    }
    public String getP_or_i()
    {
        return p_or_i;
    }
    public String getReturn_value()
    {
        return return_value;
    }
    public String getName()
    {
        return name;
    }
    public String getPos()
    {
        return pos;
    }
    public String getValue()
    {
        return value;
    }
    public String getMethod()
    {
        return method;
    }
    public String getArgument()
    {
        return argument;
    }
}
