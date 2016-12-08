package PreProcessData;

/**
 * This is for INFSCI 2140 in 2015
 * 
 * TextTokenizer can split a sequence of text into individual word tokens.
 */
public class WordTokenizer {
	//you can add essential private methods or variables
	private char[] texts;
	private int marker = 0;
	// YOU MUST IMPLEMENT THIS METHOD
	public WordTokenizer( char[] texts ) {
		// this constructor will tokenize the input texts (usually it is a char array for a whole document)
		this.texts=texts;
	}
	
	// YOU MUST IMPLEMENT THIS METHOD
	public char[] nextWord() {
		// read and return the next word of the document
		// or return null if it is the end of the document
		StringBuilder s=new StringBuilder();
		int i = marker;
		for (;i<texts.length;i++){
			if(checkT(texts[i])){
				s.append(texts[i]);
			}
			else if(i<texts.length-1&&texts[i]=='\''&&texts[i-1]=='n'&&texts[i+1]=='t'&&s.length()>0){
				marker=i+2;
				s.deleteCharAt(s.length()-1);
				return s.toString().toCharArray();
			}
			else {
				marker=i+1;
				if(s.length()>0){
					return s.toString().toCharArray();
				}
			}
		}
		if(s.length()==0){
			return null;
		}
		else {
			marker= i;
			return s.toString().toCharArray();
		}
	}
		
	private boolean checkT(char c){
		if(Character.isDigit(c)||Character.isLetter(c)){
			return true;
		}
		else return false;
	}
	
}
