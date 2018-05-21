/*
 * 
 */

package Knowledge_extract;


import Rules.Rules;
import Data_Structure.Customized_dep;
import Data_Structure.Tagset;
import Data_Structure.Entity;
import Represent.Represent_xml;
import Stem.Stemmer;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.StringTokenizer;


/**
 *
 * @author Harsh Soni
 */
public class Knowledge {

Stemmer stemmer;    /* we create stemmer object here because
                     * loading and unloading task of stemming is very costly
                     * so once the stemmer gets loaded it will be only unloaded after
                     * executing each sentence.
                */
String text;
LinkedList <Entity> entity;
LinkedList <Tagset> atw;         //contains the tag"t" and word "w"
LinkedList <Customized_dep> acd; //Stores Customized_dep for more information
                                //see Customized_dep.java
    public Knowledge(String txt)
    {
        entity=new LinkedList<Entity>();
        acd=new LinkedList<Customized_dep>();
        atw=new LinkedList<Tagset>();
        text=txt;
        
    }
 public void extract(int type)
    {
        int state=0;
        String in="input.txt";
        String out="output.txt";
        Sentence sentence=new Sentence();
        
        try {
            if(type==2)
            {
                sentence.CreateFile(text, in);
                state=sentence.parse(in,out);
            }
            else
            {
                System.out.println("Writing into output file");
                BufferedWriter bf=new BufferedWriter(new FileWriter("sp/"+out));
                bf.write(text);
                bf.close();
            }
           
            if(state==0)
            {
                System.out.println("Parser finished Execution");
                stemmer=new Stemmer();  //initializing Stemmer and connecting with word net
                doProcess(out);
            }
        } catch (FileNotFoundException ex) {
          } catch (IOException ex) {
          }
 }
private void doProcess(String file) throws FileNotFoundException, IOException
    {
                System.out.println("In do process....");
                LinkedList <Entity> temp=new LinkedList<Entity>();
                Rules rl=new Rules();
       		FileReader fr=new FileReader("sp/" +file);
		BufferedReader br=new BufferedReader(fr);
                String line;
                int linecounter=0;
                int flag=1;

                while((line=br.readLine())!=null)
                {
                    if(line.equals("")&& linecounter%2==0)
                    {
                        flag=0;
                        linecounter++;
                        continue;
                    }
                    if(line.equals("") && linecounter % 2 != 0)
                    {

                        flag=1;
                        linecounter++;
                        continue;
                    }
                    if(flag==1)
                    {
                        /*
                         * Checking if the tags and depndencies
                         * is for new sentence
                         */
                        if(linecounter!=0)
                        {
                            /*
                             * Matching the rules which is defined in
                             * rule class
                             * for the previous sentence
                             */
                             temp=rl.match(acd,atw,stemmer);
                             for(int i=0;i<temp.size();i++) //copying the sentence knowledge into
                                                            //the knowledge structure
                                     entity.add(temp.get(i));
                             
                        /*
                         * Now clearing LinkedList to rebuild with the new sentence
                         */
                            atw.clear();
                            acd.clear();
                        }
                        createTagset(line);
                    }
                else{
                        createDep(line);
                    }
                }
                 temp=rl.match(acd,atw,stemmer);
                 for(int i=0;i<temp.size();i++) //copying the sentence knowledge into
                                                //the knowledge structure
                                     entity.add(temp.get(i));

                 for(int i=0;i<entity.size();i++)
                 {
                     System.out.println(entity.get(i));
                 }
                 stemmer.Unload();
                 Represent_xml rx=new Represent_xml();
                 rx.do_represent(entity);
                System.out.println("finished execution.");
    }
    public void createTagset(String line)
    {
        StringTokenizer space=new StringTokenizer(line," ");
        while(space.hasMoreTokens())
        {
            StringTokenizer tags=new StringTokenizer(space.nextToken(),"/");
            Tagset ts=new Tagset(tags.nextToken(),tags.nextToken());
            atw.add(ts);
        }
     }
    public boolean isInteger(String s)
    {
         try
        {
            int d = Integer.parseInt(s);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
            return true;
    }
    public void createDep(String line) throws IOException
    {
        /*
         *
         * A sample of dependency
          nsubj(word1-1,word2-4)
         *
         */
        StringTokenizer coma=new StringTokenizer(line,",");
                StringTokenizer part1=new StringTokenizer(coma.nextToken(),"(-");
                    String dep=part1.nextToken();
                    String word1=part1.nextToken();
                    String num=part1.nextToken();   
                    /*(It may possible that
                     * a word is like "co-curricular"
                     * which will violate the rule for tokenizing the word
                     * by delemiter "-"
                     * so we will check that if word is not a number means
                     * it is continious word)
                       * */
                    while(!isInteger(num))
                    {
                        word1+="-"+num;
                        num=part1.nextToken();
                    }
                    StringTokenizer part2=new StringTokenizer(coma.nextToken(),"-)");
                    String word2=part2.nextToken();
                    String num2=part2.nextToken();
                    while(!isInteger(num2))
                    {
                        word2+="-"+num2;
                        num2=part2.nextToken();
                    }
       Tagset word1ts=atw.get(Integer.parseInt(num)-1);
       Tagset word2ts=atw.get(Integer.parseInt(num2)-1);
       String word1t=word1ts.gettag();
       String word2t=word2ts.gettag();
       createfinaldep(dep,word1.trim(),word1t.trim(),num,word2.trim(),word2t.trim(),num2);
            
    }
   public void createfinaldep( 
           String dep,
           String word1,
           String word1t,
           String num1,
           String word2,
           String word2t,
           String num2
           )
    {
     /*
      * First Storing dependency nsubj into LinkedList
      */
       int flag=0;
        if(dep.equals("nsubj"))
        {
          if(word2t.startsWith("NN"))
          {
             if(word1t.startsWith("VB")||word1t.startsWith("JJ")||word1t.startsWith("NN"))
               {
                    flag=1;
               }
            }
        }
        else if(dep.equals("nn")||
                dep.equals("prt")||
                dep.equals("agent")||
                dep.equals("conj_and")||
                dep.equals("neg")||
                dep.equals("poss"))
        {
                    flag=1;
        }
        
        else if(dep.equals("cop"))
        {
            if((word1t.startsWith("NN")||word1t.startsWith("JJ"))&& word2t.startsWith("VB"))
            {
                    flag=1;
            }
        }
        else if(dep.equals("dobj"))
        {
            if(word1t.startsWith("VB")&&word2t.startsWith("NN")){
                    flag=1;
            }
        }
        else if(dep.startsWith("prep"))
        {
                if(word2t.startsWith("NN")&&(word1t.startsWith("VB")||word1t.startsWith("NN")))
                {
                    flag=1;
                }
        }
        else if(dep.equals("infmod"))
        {
               if(word1t.startsWith("NN")&&(word2t.startsWith("NN")||word2t.startsWith("VB")))
               {
                    flag=1;
                }
        }
        else if(dep.equals("nsubjpass"))
        {
                if(word1t.startsWith("VB")&&word2t.startsWith("NN"))
                {
                    flag=1;
            }
        }
        else if(dep.equals("partmod"))
        {
            if(word1t.startsWith("NN")&&word2t.startsWith("VB"))
            {
                    flag=1;
            }
        }
        else if(dep.equals("xcomp"))
        {
            if(word1t.startsWith("VB")&& word2t.startsWith("VB"))
            {
                flag=1;
            }
        }
        else if(dep.equals("advmod"))
        {
            if(word1t.startsWith("VB")&& word2t.startsWith("RB"))
            {
                    flag=1;
            }
        }
        else if(dep.equals("acomp"))
        {
                if(word1t.startsWith("VB")&&word2t.startsWith("JJ"))
                {
                    flag=1;
               }
        }
        else if(dep.equals("amod"))
        {
              if(word1t.startsWith("NN")&&(word2t.startsWith("JJ")||word2t.startsWith("VB")))
                  //note: It may possible that a adjective could be verb in some case
                  //e.g. "worried man"
              {
                    flag=1;
            }
        }
        if(flag==1)
        {
                    Customized_dep cd=new Customized_dep(dep,word1,word1t,num1,word2,word2t,num2);
                    acd.add(cd);
        }
     }

    /*public int parse(String infile,String outfile)
    {

       try
        {
           System.out.println("Parsing Started.....");
/*                String pth="sp/stanford-parser.jar edu.stanford.nlp.parser.lexparser.LexicalizedParser";
		String inp="sp/"+infile;
		String op="sp/"+outfile;
		String str="cmd /c java -mx200m -classpath "+pth+"  -retainTMPSubcategories -outputFormat \"wordsAndTags,typedDependencies\" sp/englishPCFG.ser.gz "+inp+" > "+op;
		Process p1=Runtime.getRuntime().exec("exe.bat",null,new File("."));
                p1.waitFor();
        }
        catch(Exception e)
                {
            System.out.println(e);
            }

        return 0;
        }
        */
}