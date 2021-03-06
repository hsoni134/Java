package Rules;
import Data_Structure.Customized_dep;
import Data_Structure.Tagset;
import Data_Structure.Entity;
import Stem.Stemmer;
import java.util.HashMap;
import java.util.LinkedList;
/**
 *
 * This is the core part of this application which contains
 * well defined rules that will be applied to the dependencies generated by Stanford parser
 * 
 * @author Harsh Soni
 *
 */
public class Rules {

    /*
     * nounphrase is a hashmap which has an Integer key
     * Integer key represent the position of a noun in a sentence
     * and String will represent noun phrase
     */
    Stemmer stemmer;
    HashMap<Integer,String> noun_phrase;
    LinkedList<Entity> ent;
    public void create_noun_phrase(LinkedList<Customized_dep> cd)
    {
        noun_phrase =new HashMap<Integer, String>();
        String rootnoun="";
        Customized_dep nn;
        int flag=0;
        int key = 0;
        for(int i=0;i<cd.size();i++)
            {
                    nn=cd.get(i);
                    if(nn.getDep().equals("nn"))
                    {
                        if(flag==0) //creating new key for the nounphrase and inserting first noun
                        {
                            key=nn.getPos1();
                            flag=1;
                            rootnoun=nn.getWord1();
                            noun_phrase.put(new Integer(key), nn.getWord2());
                        }
                        else    //Appending existing nounphrase with the new noun.
                        {
                            String old=noun_phrase.get(key);
                             noun_phrase.put(new Integer(key), old+" "+nn.getWord2());
                        }

                    }
                    else
                    {
    
                        flag=0;
                        if(!rootnoun.equals(""))    //Attaching rootnoun to end of the nounphrase
                        {
                            String old=noun_phrase.get(key);
                            noun_phrase.put(key, old+" "+rootnoun);
                            rootnoun="";
                        }
                }
            }
        if(!rootnoun.equals(""))    //if no dependency is there but need to attach the root verb at the
                                    //end of the nounphrase if missing
        {
                            String old=noun_phrase.get(key);
                            noun_phrase.put(key, old+" "+rootnoun);
        }
 /*       System.out.println("Noun phrases");
        Set set=nounphrase.entrySet();
        Iterator itrt=set.iterator();
        while(itrt.hasNext())
        {
            Map.Entry me=(Map.Entry)itrt.next();
            System.out.println(" Key "+ me.getKey()+" Value: "+me.getValue());
        }
  * 
  */
    }
    public void handle_adjective(LinkedList<Customized_dep> cd)
    {
        for(int i=0;i<cd.size();i++)
        {
            Customized_dep amod=cd.get(i);
            if(amod.getDep().equals("amod"))
            {
                String entity=amod.getWord1();
                if(noun_phrase.containsKey(amod.getPos1()))
                    entity=noun_phrase.get(amod.getPos1());
                String neg="";
                if(isneg(amod.getPos1(),cd,i))
                {
                    neg="not";
                }
                ent.add(new Entity('v',entity,"",neg+" "+amod.getWord2(),"","","",""));
                System.out.println(entity+" has value "+neg+" "+amod.getWord2());
            }
        }
    }
    public LinkedList<Entity> match(
            LinkedList<Customized_dep> acd,
            LinkedList<Tagset> ts,
            Stemmer stemmer)
    {
        this.stemmer=stemmer;
        create_noun_phrase(acd);
        ent=new LinkedList<Entity>();
        print_sentence(ts);
        Customized_dep cd;
        int size=acd.size();
        for(int i=0;i<size;i++)
        {
            cd=acd.get(i);
            if(cd.getDep().equals("poss"))
            {
                   size=handlepos(acd,i);
            }
            else if(cd.getDep().equals("nsubj"))
            {
                int oldsize=size;
                if(cd.getTag1().startsWith("NN")&&cd.getTag1().startsWith("NN")||cd.getTag1().startsWith("JJ"))
                {
                    size=handlecop(acd,i);
                }
                else if(cd.getTag1().startsWith("VB")&&cd.getTag2().startsWith("NN"))
                {
                    boolean exec=false;
                    exec=handledobjvn(acd,i);
                    exec=handleprepvn(acd,i);
                    size=handleacomp(acd,i);
                        if(!exec)
                                size=handlensubj(acd,i);                        
                }
                
            }
            else if(cd.getDep().equals("infmod") )
            {
                    size=handledobjnv(acd,i);
            }
            else if(cd.getDep().equals("nsubjpass"))
            {
                    size=handleagentvn(acd,i);

            }
            else if(cd.getDep().equals("partmod"))
            {
                int oldsize=size;
                size=handledobjnv(acd,i);
                if(oldsize==size)
                {
                    size=handleprepnv(acd,i);
                }
            }
        }
        handle_adjective(acd);
        return ent;
    }
    /*
     * handlecop method for handling copula with nominal subject
     */
    public int handlepos(LinkedList<Customized_dep> cd,int i)
    {
        Customized_dep pos=cd.get(i);
        String noun=pos.getWord2();
        if(noun_phrase.containsKey(pos.getPos2()))
            noun=noun_phrase.get(pos.getPos2());
        ent.add(new Entity('p',noun,pos.getWord1(),"","","","",""));
        System.out.println(noun+" posses "+pos.getWord1());
        return cd.size();
    }
    public int handlensubj(LinkedList<Customized_dep> cd,int i)
    {
        Customized_dep nsubj=cd.get(i);
        String verb=stemmer.doStem(nsubj.getWord1());
        if(verb.equals("be")||verb.equals("have")||verb.equals("possess")
                ||verb.equals("contain")||verb.equals("own"))
            return cd.size();
        String entity=nsubj.getWord2();
        if(noun_phrase.containsKey(nsubj.getPos2()))
        {
            entity=noun_phrase.get(nsubj.getPos2());
        }
        ent.add(new Entity('m', entity,"","",verb,"","",""));
        System.out.println(entity+" has method "+verb);
        return cd.size();
    }
public int handleacomp(LinkedList<Customized_dep> cd, int i)
{
    Customized_dep dep=cd.get(i);
    Customized_dep acomp;
    for(
    int index=i+1;
    index<cd.size();
    index++)
    {
        acomp=cd.get(index);
        if(acomp.getDep().startsWith("acomp")&&acomp.getPos1()==dep.getPos1())
        {
            String entity=dep.getWord2();
            if(noun_phrase.containsKey(dep.getPos2()))
            {
                entity=noun_phrase.get(dep.getPos2());
            }
            String verb=dep.getWord1();
            stemmer.doStem(verb);
            ent.add(new Entity('m',entity,"","",verb,"",acomp.getWord2(),""));
            System.out.println(entity+" has method "+verb+" which returns "+ acomp.getWord2());
            cd.remove(index--);
            //index-- is required because we are deleting the element from linked list
            //and the next element is at position index so (-- and then ++)will be represent index
        }
    }
    return cd.size();
}
public boolean handledobjvn(LinkedList<Customized_dep> cd, int i)
{
    Customized_dep dep=cd.get(i);
    Customized_dep dobj;
    int flag=0;
    for(
    int index=i+1;
    index<cd.size();
    index++)
    {
        dobj=cd.get(index);
        if(dobj.getDep().equals("dobj")&&dobj.getPos1()==dep.getPos1())
        {
            String entity1=dep.getWord2(),entity2=dobj.getWord2();
            if(noun_phrase.containsKey(dep.getPos2()))
                    entity1=noun_phrase.get(dep.getPos2());
            if(noun_phrase.containsKey(dobj.getPos2()))
                    entity2=noun_phrase.get(dobj.getPos2());
             entity2+=(getPrepnn(cd, index, dobj.getPos2()));   //Associate dobj with preposition
             String verb=stemmer.doStem(dobj.getWord1().toLowerCase());
           if(verb.equals("have")||verb.equals("possess")
                ||verb.equals("contain")||verb.equals("own"))
           {
               if(!isneg(dobj.getPos1(), cd, i))
               {
                   ent.add(new Entity('p', entity1, entity2,"","","","",""));
                   System.out.println(entity1+" possess "+entity2);
               }
               else
               {
                   ent.add(new Entity('p', entity1,"not "+entity2,"","","","",""));
                    System.out.println(entity1+ " do not possess "+entity2);
               }
           }
           else
           {
                 if(isneg(dobj.getPos1(),cd,i))
                 {
                ent.add(new Entity('m',entity1,"","",verb,"not "+entity2,"",""));
                System.out.println(entity1+" has method "+verb+" which has"
                        + " argment not "+entity2);
                 }
                else
                {

                ent.add(new Entity('m',entity1,"","",verb,entity2,"",""));
                System.out.println(entity1+" has method "+verb+" which has"
                        + " argment "+entity2);
                }

           }
            flag=1;
        }
    }
    if(flag==1)
        return true;
    return false;
}
public boolean handleprepvn(LinkedList<Customized_dep> cd,int i)
{
    Customized_dep dep=cd.get(i);
    Customized_dep prep;
    int flag=0;
    for(
    int index=i+1;
    index<cd.size();
    index++)
    {
        prep=cd.get(index);
        if(prep.getDep().startsWith("prep")&&prep.getPos1()==dep.getPos1())
        {
            String entity1=dep.getWord2(),entity2=prep.getWord2();
            if(noun_phrase.containsKey(dep.getPos2()))
                    entity1=noun_phrase.get(dep.getPos2());
            if(noun_phrase.containsKey(prep.getPos2()))
                    entity2=noun_phrase.get(prep.getPos2());
            String verb=stemmer.doStem(dep.getWord1());
            if(!verb.equals("have")||verb.equals("own")||verb.equals("possess")||verb.equals("contain"))
            {
            if(isneg(prep.getPos1(),cd,i))
            {
                ent.add(new Entity('m',entity1,"","",verb,"not "+entity2,"",""));
                System.out.println(entity1+" has method "+verb+" which has argument not "+ entity2);
            }
            else
            {
                ent.add(new Entity('m',entity1,"","",verb,entity2,"",""));
                System.out.println(entity1+" has method "+verb+" which has argument "+ entity2);

            }
            flag=1;
            }
            
        }
    }
    if (flag==1)
            return true;
    return false;
}
public String getPrepnn(LinkedList<Customized_dep> cd,int i,int pos)
    {
    Customized_dep prep;
        for(int index=0;index<cd.size();index++)
        {
            prep=cd.get(index);
            if(prep.getDep().startsWith("prep")&&prep.getPos1()==pos)
            {
                int index_to_start=0;
                if(prep.getDep().startsWith("prepc"))
                    index_to_start++;
                index_to_start+=5;
                String preposition=prep.getDep().substring(index_to_start);
                String noun=prep.getWord2();
                if(noun_phrase.containsKey(prep.getPos2()))
                {
                    noun=noun_phrase.get(prep.getPos2());
                }
                return " "+preposition+" "+noun;
            }
        }
    return "";
}
public int handleprepnv(LinkedList<Customized_dep> cd, int i)
{
    Customized_dep dep=cd.get(i);
    Customized_dep prep;
    for(
    int index=i+1;
    index<cd.size();
    index++)
    {
        prep=cd.get(index);
        if(prep.getDep().startsWith("prep")&&prep.getPos1()==dep.getPos2())
        {
            String entity1=dep.getWord1(),entity2=dep.getWord2();
            if(noun_phrase.containsKey(dep.getPos1()))
                entity1=noun_phrase.get(dep.getPos1());
            if(noun_phrase.containsKey(prep.getPos2()))
                entity2=noun_phrase.get(prep.getPos2());
            String verb=stemmer.doStem(dep.getWord2());
            ent.add(new Entity('m', entity1,"","",verb, entity2,"",""));
            System.out.println(entity1+" has method "+verb+" which has argument "+ entity2);
            cd.remove(index--);
        }
    }
    return cd.size();
}
public int handlecop(LinkedList<Customized_dep> cd,int i)
    {
        Customized_dep cd1=cd.get(i);
        Customized_dep cd2;
        for (int j = i + 1; j < cd.size(); j++)
            {
                cd2=cd.get(j);
                if(cd2.getDep().equals("cop")&&cd1.getPos1()==cd2.getPos1())
                {
                    if(cd1.getTag1().startsWith("JJ"))
                    {
                        String entity1=cd1.getWord2();
                        if(noun_phrase.containsKey(cd1.getPos2()))
                                entity1=noun_phrase.get(cd1.getPos2());
                        ent.add(new Entity('v',entity1,"",cd1.getWord1(),"","","",""));
                        System.out.println(entity1+" has value "+cd1.getWord1());
                    }
                    else
                    {
                        String adjective=getamod(cd,j,cd2.getWord1());
                        if(!isneg(cd2.getPos1(),cd,j)) //checking if it is negative sentence
                        {
                            String entity1=cd1.getWord2(),entity2=cd1.getWord1();
                            if(noun_phrase.containsKey(cd1.getPos2()))
                                entity1=noun_phrase.get(cd1.getPos2());
                            if(noun_phrase.containsKey(cd1.getPos1()))
                                entity2=noun_phrase.get(cd1.getPos1());
                            entity2+=getPrepnn(cd, j, cd1.getPos1());
                            if(adjective.equals(""))
                            {
                                ent.add(new Entity('i',entity1,"","","","","",entity2));
                                System.out.println(entity1+" is derrived from "+ entity2);
                            }
                            else
                            {
                                ent.add(new Entity('i',entity1,"","","","","",entity2));
                            System.out.println(entity1+" is derrived from "+ entity2 +" whose value is "+adjective);
                            }
                        }
                        else
                        {
                            if(!adjective.equals("")) //it has adjective which modify noun
                            {
                                String entity1=cd1.getWord2(),entity2=cd1.getWord1();
                                if(noun_phrase.containsKey(cd1.getPos2()))
                                    entity1=noun_phrase.get(cd1.getPos2());
                                if(noun_phrase.containsKey(cd1.getPos1()))
                                    entity2=noun_phrase.get(cd1.getPos1());
                                entity2+=getPrepnn(cd, j, cd1.getPos1());
                                ent.add(new Entity('i', entity1,"","","","","", entity2));
                                ent.add(new Entity('v', entity2,"","not "+adjective,"","","",""));
                                System.out.println(entity1+" is derrived from "+ entity2+" whose value is not "+ adjective);
                           }
                        }
                    }
                    cd.remove(i--);
                    //we are not removing cop because it may possible
                    //that some othere noun is related with the same noun.
                    //for example "famer and doctor are man."
                    break;
                }
            }
               return cd.size();
    }

/*
 handleagent() is for handling passive sentence
 */
public int handleagentvn(LinkedList<Customized_dep> cd,int index)
{
    Customized_dep nsubjpass =cd.get(index);
    Customized_dep agent;
    for(
        int j=index+1;
        j<cd.size();
        j++
        )
        {
            agent=cd.get(j);
            if(agent.getDep().equals("agent")&&nsubjpass.getPos1()==agent.getPos1())
            {
            String entity1=agent.getWord2();
            String entity2=nsubjpass.getWord2();
            if(noun_phrase.containsKey(agent.getPos2()))
                entity1=noun_phrase.get(agent.getPos2());
            if(noun_phrase.containsKey(nsubjpass.getPos2()))
                entity2=noun_phrase.get(nsubjpass.getPos2());
            String verb=stemmer.doStem(agent.getWord1());
            if(isneg(agent.getPos1(),cd,index))
            {
                ent.add(new Entity('m', entity1, "","",verb,"not "+ entity2,"",""));
                System.out.println(entity1+" has method "+verb+" which has argument not "+ entity2);
            }
            else
            {
                ent.add(new Entity('m', entity1,"","",verb, entity2,"",""));
                System.out.println(entity1+" has method "+verb+" which has argument "+ entity2);
            }


            cd.remove(j--);//removing dependency agent from the Linked list
            }
        }
    return cd.size();
}
/*
 * This handledobjnv method is used to match the dobj which is relate with
 * the dependency partmod and infomd
 * 
 */
public int handledobjnv(LinkedList<Customized_dep> cd,int index)
    {
        Customized_dep dep=cd.get(index);
        Customized_dep dobj;
        int depindex=index;
        for(int j=index+1;j<cd.size();j++)
        {
            dobj=cd.get(j);
            if((dobj.getDep().equals("dobj"))&& (dobj.getPos1()==dep.getPos2()))
            {
                String entity1=dep.getWord1();
                String entity2=dobj.getWord2();
                String verb=stemmer.doStem(dobj.getWord1());
                if(noun_phrase.containsKey(dep.getPos1()))
                    //checking whether an subject is nounphrase
                {
                    entity1=noun_phrase.get(dep.getPos1());
                }
                if(noun_phrase.containsKey(dobj.getPos2()))
                {
                    //checking wheather an object is nounphrase
                    entity2=noun_phrase.get(dobj.getPos2());
                }
                if(isneg(dobj.getPos1(),cd,0)||isneg(dep.getPos1(),cd,0))//0 is passed because the neg is available before this dependencies
                    //so it will start checking from the first index.
                {
                    ent.add(new Entity('m', entity1,"","", verb, "not " + entity2,"", ""));
                    System.out.println(entity1+" has method "+verb+" which has argument not "+entity2);
                }
                else
                {
                    ent.add(new Entity('m', entity1,"","", verb,entity2,"",""));
                    System.out.println(entity1+" has method "+verb+" which has argument "+entity2);
                }
         
                cd.remove(depindex);
                cd.remove(--j);         //removing dobj dependency from the linked list
            }
        }
        return cd.size();
    }

public boolean isneg(int pos,LinkedList<Customized_dep> cd,int index)
    {
        Customized_dep cd1;
        for(int i=index+1;i<cd.size();i++)
        {
            cd1=cd.get(i);
            if(cd1.getDep().equals("neg")&& cd1.getPos1()==pos)
                return true;
        }
        return false;
    }
public String getamod(LinkedList<Customized_dep> cd,
        int index,
        String noun)
{
    Customized_dep cd1;
    for(int j=index+1;j<cd.size();j++)
    {
        cd1=cd.get(j);
        if(cd1.getDep().equals("amod")&&cd1.getWord1().equals(noun))
        {
            return cd1.getWord2();
        }
    }
    return "";
}
    public void print_sentence(LinkedList<Tagset> ts)
    {
        System.out.println("Knowledge Extracted from Sentence: ");
        Tagset t;
        for(int i=0;i<ts.size();i++)
        {
            t=ts.get(i);
            System.out.print(" "+t);
        }
        System.out.println("");
        System.out.println("------------------------");
     }
}