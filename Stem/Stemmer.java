package Stem;
/*
 *
 * Verb Stemming with the help of
 *
 * Word Net
 * &
 * Java word net library(jwnl)
 *
 * System Requirement:
 *                 Word net 2.0 installed in c:\program files\
  if verion 2.0 is not available or installed in other location
 then please change the dictionary_path from JWNLproperties.xml
 *
 *
 */

import net.didion.jwnl.*;
import net.didion.jwnl.data.*;
import net.didion.jwnl.dictionary.*;

import java.io.*;
/**
 *
 * @author Harsh Soni
 */
public class Stemmer {

	private Dictionary dic;
	private MorphologicalProcessor morph;
	private boolean IsInitialized = false;
        public void Unload ()
	{
		dic.close();
		Dictionary.uninstall();
		JWNL.shutdown();
	}
	public Stemmer ()
	{
//		AllWords = new HashMap ();

		try
		{
			JWNL.initialize(new FileInputStream
				("JWNLproperties.xml"));
			dic = Dictionary.getInstance();
			morph = dic.getMorphologicalProcessor();
			// ((AbstractCachingDictionary)dic).
			//	setCacheCapacity (10000);
			IsInitialized = true;
		}
		catch ( FileNotFoundException e )
		{
			System.out.println ( "Error initializing Stemmer:JWNLproperties.xml not found" );
		}
		catch ( JWNLException e )
		{
			System.out.println ( "Error initializing Stemmer: "
				+ e.toString() );
		}

	}
       /* stems a word with wordnet
	 * @param word word to stem
	 * @return the stemmed word or null if it was not found in WordNet
	 */
	public String doStem ( String word )
	{
		if ( !IsInitialized )
			return word;
		if ( word == null ) return word;
		if ( morph == null ) morph = dic.getMorphologicalProcessor();

		IndexWord w;
		try
		{
			w = morph.lookupBaseForm( POS.VERB, word );
			if ( w != null )
				return w.getLemma().toString ();
			w = morph.lookupBaseForm( POS.NOUN, word );
			if ( w != null )
				return w.getLemma().toString();
			w = morph.lookupBaseForm( POS.ADJECTIVE, word );
			if ( w != null )
				return w.getLemma().toString();
			w = morph.lookupBaseForm( POS.ADVERB, word );
			if ( w != null )
				return w.getLemma().toString();
		}
		catch ( JWNLException e )
		{
		}
		return word;
	}

    /**
     * @param args the command line arguments
     */

    
}