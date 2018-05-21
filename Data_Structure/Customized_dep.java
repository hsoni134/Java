/*
 * 
 */

package Data_Structure;

/**
 * @author Harsh Soni
 *
 * A customized dependencies class for storing
 * dependency in well form format.
 * The object of this class is stored in LinkedList in Knowledge class
 * 
 */
public class Customized_dep {
    private String dep_name;
    private String word1;
    private int pos1;
    private int pos2;
    private String tag1;
    private String word2;
    private String tag2;
    public Customized_dep(String d,String w1,String t1,String n1,String w2,String t2,String n2)
    {
        dep_name=d;
        word1=w1;
        tag1=t1;
        pos1=Integer.parseInt(n1);
        word2=w2;
        tag2=t2;
        pos2=Integer.parseInt(n2);
      }
    public String getDep()
    {
        return dep_name;
    }
    public String getWord1()
    {
        return word1;
    }
    public String getTag1()
    {
        return tag1;
    }
    public String getWord2()
    {
        return word2;
    }        
    public String getTag2()
    {
        return tag2;
    }
    public int getPos1()
    {
        return pos1;
    }
    public int getPos2()
    {
        return pos2;
    }
  
}
