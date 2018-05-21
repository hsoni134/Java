/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Data_Structure;

/**
 *
 * @author Harsh Soni
 */
public class Tagset {
    private String word;
    private String tag;

    public Tagset(String w,String t)
    {
        word=w;
        tag=t;
    }
    @Override
    public String toString()
    {
        return word;
    }
    public String gettag()
    {
        return tag;
    }
}
