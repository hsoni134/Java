/*
 
 */

package Knowledge_extract;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author Harsh Soni
 */
public class Sentence {

    public void CreateFile(String text,String file) throws FileNotFoundException, IOException
    {
        BufferedWriter bf=new BufferedWriter(new FileWriter("sp/"+file));
        try
        {
           bf.write(text);
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
        finally
        {
        bf.close();
        }
    }
    public int parse(String infile,String outfile) 
    {

       try
        {
                System.out.println("Parsing Started.....");
                String pth="sp/stanford-parser.jar edu.stanford.nlp.parser.lexparser.LexicalizedParser";
		String inp="sp/"+infile;
		String op="sp/"+outfile;
		String str="cmd /c java -mx200m -classpath "+pth+"  -retainTMPSubcategories -outputFormat \"wordsAndTags,typedDependencies\" sp/englishPCFG.ser.gz "+inp+" > "+op;
		Process p1=Runtime.getRuntime().exec(str);
                return p1.waitFor();
		
        }
        catch(Exception e)
                {
            System.out.println(e);
            }
      
        return 0;
        }

    }


