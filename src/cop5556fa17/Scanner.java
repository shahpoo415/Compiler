/* *
 * Scanner for the class project in COP5556 Programming Language Principles 
 * at the University of Florida, Fall 2017.
 * 
 * This software is solely for the educational benefit of students 
 * enrolled in the course during the Fall 2017 semester.  
 * 
 * This software, and any software derived from it,  may not be shared with others or posted to public web sites,
 * either during the course or afterwards.
 * 
 *  @Beverly A. Sanders, 2017
  */

package cop5556fa17;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;



public class Scanner {
	
	HashMap <String, Scanner.Kind> seperator;
	HashMap <String, Scanner.Kind> operator;
	HashMap <String, Scanner.Kind> keyword;
	HashMap <String, Scanner.Kind> bool1;
	HashMap <String, Scanner.Kind> bool2;
	
	@SuppressWarnings("serial")
	public static class LexicalException extends Exception {
		
		int pos;

		public LexicalException(String message, int pos) {
			super(message);
			this.pos = pos;
		}
		
		public int getPos() { return pos; }

	}

	public static enum Kind {
		IDENTIFIER, INTEGER_LITERAL, BOOLEAN_LITERAL, STRING_LITERAL, 
		KW_x/* x */, KW_X/* X */, KW_y/* y */, KW_Y/* Y */, KW_r/* r */, KW_R/* R */, KW_a/* a */, 
		KW_A/* A */, KW_Z/* Z */, KW_DEF_X/* DEF_X */, KW_DEF_Y/* DEF_Y */, KW_SCREEN/* SCREEN */, 
		KW_cart_x/* cart_x */, KW_cart_y/* cart_y */, KW_polar_a/* polar_a */, KW_polar_r/* polar_r */, 
		KW_abs/* abs */, KW_sin/* sin */, KW_cos/* cos */, KW_atan/* atan */, KW_log/* log */, 
		KW_image/* image */,  KW_int/* int */, 
		KW_boolean/* boolean */, KW_url/* url */, KW_file/* file */, OP_ASSIGN/* = */, OP_GT/* > */, OP_LT/* < */, 
		OP_EXCL/* ! */, OP_Q/* ? */, OP_COLON/* : */, OP_EQ/* == */, OP_NEQ/* != */, OP_GE/* >= */, OP_LE/* <= */, 
		OP_AND/* & */, OP_OR/* | */, OP_PLUS/* + */, OP_MINUS/* - */, OP_TIMES/* * */, OP_DIV/* / */, OP_MOD/* % */, 
		OP_POWER/* ** */, OP_AT/* @ */, OP_RARROW/* -> */, OP_LARROW/* <- */, LPAREN/* ( */, RPAREN/* ) */, 
		LSQUARE/* [ */, RSQUARE/* ] */, SEMI/* ; */, COMMA/* , */, EOF;
	}

	/** Class to represent Tokens. 
	 * 
	 * This is defined as a (non-static) inner class
	 * which means that each Token instance is associated with a specific 
	 * Scanner instance.  We use this when some token methods access the
	 * chars array in the associated Scanner.
	 * 
	 * 
	 * @author Beverly Sanders
	 *
	 */
	public class Token {
		public final Kind kind;
		public final int pos;
		public final int length;
		public final int line;
		public final int pos_in_line;

		public Token(Kind kind, int pos, int length, int line, int pos_in_line) {
			super();
			this.kind = kind;
			this.pos = pos;
			this.length = length;
			this.line = line;
			this.pos_in_line = pos_in_line;
		}

		public String getText() {
			if (kind == Kind.STRING_LITERAL) {
				return chars2String(chars, pos, length);
			}
			else return String.copyValueOf(chars, pos, length);
		}

		/**
		 * To get the text of a StringLiteral, we need to remove the
		 * enclosing " characters and convert escaped characters to
		 * the represented character.  For example the two characters \ t
		 * in the char array should be converted to a single tab character in
		 * the returned String
		 * 
		 * @param chars
		 * @param pos
		 * @param length
		 * @return
		 */
		private String chars2String(char[] chars, int pos, int length) {
			StringBuilder sb = new StringBuilder();
			for (int i = pos + 1; i < pos + length - 1; ++i) {// omit initial and final "
				char ch = chars[i];
				if (ch == '\\') { // handle escape
					i++;
					ch = chars[i];
					switch (ch) {
					case 'b':
						sb.append('\b');
						break;
					case 't':
						sb.append('\t');
						break;
					case 'f':
						sb.append('\f');
						break;
					case 'r':
						sb.append('\r'); //for completeness, line termination chars not allowed in String literals
						break;
					case 'n':
						sb.append('\n'); //for completeness, line termination chars not allowed in String literals
						break;
					case '\"':
						sb.append('\"');
						break;
					case '\'':
						sb.append('\'');
						break;
					case '\\':
						sb.append('\\');
						break;
					default:
						assert false;
						break;
					}
				} else {
					sb.append(ch);
				}
			}
			return sb.toString();
		}

		/**
		 * precondition:  This Token is an INTEGER_LITERAL
		 * 
		 * @returns the integer value represented by the token
		 */
		public int intVal() {
			assert kind == Kind.INTEGER_LITERAL;
			return Integer.valueOf(String.copyValueOf(chars, pos, length));
		}

		public String toString() {
			return "[" + kind + "," + String.copyValueOf(chars, pos, length)  + "," + pos + "," + length + "," + line + ","
					+ pos_in_line + "]";
		}

		/** 
		 * Since we overrode equals, we need to override hashCode.
		 * https://docs.oracle.com/javase/8/docs/api/java/lang/Object.html#equals-java.lang.Object-
		 * 
		 * Both the equals and hashCode method were generated by eclipse
		 * 
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((kind == null) ? 0 : kind.hashCode());
			result = prime * result + length;
			result = prime * result + line;
			result = prime * result + pos;
			result = prime * result + pos_in_line;
			return result;
		}

		/**
		 * Override equals method to return true if other object
		 * is the same class and all fields are equal.
		 * 
		 * Overriding this creates an obligation to override hashCode.
		 * 
		 * Both hashCode and equals were generated by eclipse.
		 * 
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Token other = (Token) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (kind != other.kind)
				return false;
			if (length != other.length)
				return false;
			if (line != other.line)
				return false;
			if (pos != other.pos)
				return false;
			if (pos_in_line != other.pos_in_line)
				return false;
			return true;
		}

		/**
		 * used in equals to get the Scanner object this Token is 
		 * associated with.
		 * @return
		 */
		private Scanner getOuterType() {
			return Scanner.this;
		}

	}

	/** 
	 * Extra character added to the end of the input characters to simplify the
	 * Scanner.  
	 */
	static final char EOFchar = 0;
	
	/**
	 * The list of tokens created by the scan method.
	 */
	final ArrayList<Token> tokens;
	
	/**
	 * An array of characters representing the input.  These are the characters
	 * from the input string plus and additional EOFchar at the end.
	 */
	final char[] chars;  



	
	/**
	 * position of the next token to be returned by a call to nextToken
	 */
	private int nextTokenPos = 0;

	Scanner(String inputString) {
		int numChars = inputString.length();
		this.chars = Arrays.copyOf(inputString.toCharArray(), numChars + 1); // input string terminated with null char
		chars[numChars] = EOFchar;
		tokens = new ArrayList<Token>();
		operator = new HashMap<>();
		seperator = new HashMap<>();
		keyword = new HashMap<>();
		bool1 = new HashMap<>();
		bool2 = new HashMap<>();
		
		seperator.put(";", Kind.SEMI);
		seperator.put(",", Kind.COMMA);
		seperator.put("(", Kind.LPAREN);
		seperator.put(")", Kind.RPAREN);
		seperator.put("[", Kind.LSQUARE);
		seperator.put("]", Kind.RSQUARE);
		
		operator.put("=", Kind.OP_ASSIGN);
		operator.put(">", Kind.OP_GT);
		operator.put("<", Kind.OP_LT);
		operator.put("*", Kind.OP_TIMES);
		operator.put("!", Kind.OP_EXCL);
		operator.put("?", Kind.OP_Q);
		operator.put(":", Kind.OP_COLON);
		operator.put("==", Kind.OP_EQ);
		operator.put("!=", Kind.OP_NEQ);
		operator.put(">=", Kind.OP_GE);
		operator.put("<=", Kind.OP_LE);
		operator.put("&", Kind.OP_AND);
		operator.put("|", Kind.OP_OR);
		operator.put("+", Kind.OP_PLUS);
		operator.put("-", Kind.OP_MINUS);
		operator.put("/", Kind.OP_DIV);
		operator.put("%", Kind.OP_MOD);
		operator.put("**", Kind.OP_POWER);
		operator.put("@", Kind.OP_AT);
		operator.put("->", Kind.OP_RARROW);
		operator.put("<-", Kind.OP_LARROW);
		
		keyword.put("x", Kind.KW_x);
		keyword.put("X", Kind.KW_X);
		keyword.put("y", Kind.KW_y);
		keyword.put("Y", Kind.KW_Y);
		keyword.put("r", Kind.KW_r);
		keyword.put("R", Kind.KW_R);
		keyword.put("a", Kind.KW_a);
		keyword.put("A", Kind.KW_A);
		keyword.put("Z", Kind.KW_Z);
		keyword.put("DEF_X", Kind.KW_DEF_X);
		keyword.put("DEF_Y", Kind.KW_DEF_Y);
		keyword.put("SCREEN", Kind.KW_SCREEN);
		keyword.put("cart_x", Kind.KW_cart_x);
		keyword.put("cart_y", Kind.KW_cart_y);
		keyword.put("polar_a", Kind.KW_polar_a);
		keyword.put("polar_r", Kind.KW_polar_r);
		keyword.put("abs", Kind.KW_abs);
		keyword.put("sin", Kind.KW_sin);
		keyword.put("cos", Kind.KW_cos);
		keyword.put("atan", Kind.KW_atan);
		keyword.put("log", Kind.KW_log);
		keyword.put("image", Kind.KW_image);
		keyword.put("int", Kind.KW_int);
		keyword.put("boolean", Kind.KW_boolean);
		keyword.put("url", Kind.KW_url);
		keyword.put("file", Kind.KW_file);
		
		bool1.put("true", Kind.BOOLEAN_LITERAL);
		bool2.put("false", Kind.BOOLEAN_LITERAL);
		
	}


	/**
	 * Method to scan the input and create a list of Tokens.
	 * 
	 * If an error is encountered during scanning, throw a LexicalException.
	 * 
	 * @return
	 * @throws LexicalException
	 */
	
	
	public Scanner scan() throws LexicalException {
		/* TODO  Replace this with a correct and complete implementation!!! */
		int pos = 0;
		int line = 1;
		int posInLine = 1;
		String string = String.valueOf(chars);
		string = string + " ";
		int len = string.length();
		char curr, next;
		
		for (int i = 0; i < len-2; i++,pos++)
		{
			curr=string.charAt(i);
			next = string.charAt(i+1);
			String temp=curr+"";
			
			if ((curr =='\r' && next == '\n') || curr == '\n')
			{
				if (curr =='\r')
				{
					i++;
					pos++;
				}
				
				line++;
				
				posInLine = 1;
				continue;
			}
			
			if (curr=='\r' && next!='\n')
			{
				posInLine = 1;
				line++;
				continue;
			}
			
			if(curr ==' ' || curr=='\t' || curr=='\f')
			{
				posInLine++;
				continue;
			}
			
			if(curr =='\b')
			{
				throw new LexicalException("\\b not allowed", pos);
			}
			
			if(curr =='\\')
			{
				throw new LexicalException("\\ not allowed", pos);
			}
			
			if(curr =='\'')
			{
				throw new LexicalException("\' not allowed", pos);
			}
			
				
			if (curr =='/' && next =='/')
			{
				i+=2;
				curr = string.charAt(i);
				pos+=2;
				
				while (curr !='\n' && i<chars.length && curr!='\r')
				{
					pos++;
					i++;
					curr = string.charAt(i);
				}
				
				i--;
				pos--;
				continue;
				
			}
			
			if (curr == '0')
			{
				tokens.add(new Token(Kind.INTEGER_LITERAL, pos, 1, line, posInLine));
				posInLine++;
				continue;
			}
			
			else if (Character.isDigit(curr))
			{
				int s = pos;
				int sp = posInLine;
				String no = "";
				
				while (i<chars.length && Character.isDigit(curr))
				{
					no+=curr;
					i++;
					pos++;
					posInLine++;
					curr = string.charAt(i);
				}
				i--;
				pos--;
				
				try
				{
					Integer.parseInt(no);
				}
				catch (Exception e)
				{
					throw new LexicalException("Number too large",s);
				}
				
				tokens.add(new Token(Kind.INTEGER_LITERAL, s, no.length(), line, sp));
				continue;
			}
			
			
			if (seperator.containsKey(temp))
			{
				tokens.add(new Token(seperator.get(temp), pos, 1, line, posInLine));
				posInLine++;
				continue;
			}
		
			String a = String.valueOf(curr);
			String z = String.valueOf(next);
			String c = a+z;
			
			if (operator.containsKey(c))
			{
				tokens.add(new Token(operator.get(c), pos, 2, line, posInLine));
				pos++;
				posInLine+=2;
				i++;
				continue;
			}
			
			if (operator.containsKey(a))
			{
				tokens.add(new Token(operator.get(a), pos, 1, line, posInLine));
				posInLine++;
				continue;
			}
			
			if( curr =='\"')
			{
				int start = pos;
				int start_posLine = posInLine;
				i++;
				pos++;
				posInLine++;
				curr = string.charAt(i);
				int flag = 0;
				
				while(curr != '\"' && i<chars.length-2)
				{StringBuilder sb = new StringBuilder();
					if (curr=='\\')
					{
						i++;
						pos++;
						curr = string.charAt(i);
						switch(curr)
						{
						case 'b':
							sb.append('\b');
							posInLine++;
							flag = 1;
							break;
						case 't':
							sb.append('\t');
							posInLine++;
							flag = 1;
							break;
						case 'f':
							sb.append('\f');
							posInLine++;
							flag = 1;
							break;
						case 'r':
							sb.append('\r');
							posInLine++;
							flag = 1;
							break;
						case 'n':
							sb.append('\n');
							posInLine++;
							flag = 1;
							break;	
						case '\'':
							sb.append('\'');
							posInLine++;
							flag = 1;
							break;
						case '\"':
							sb.append('\"');
							i++;
							pos++;
							posInLine++;
							curr=string.charAt(i);
							posInLine+=2;
							flag = 1;
							break;
						case '\\':
							sb.append('\\');
							i++;
							pos++;
							posInLine+=2;
							curr=string.charAt(i);
							flag = 1;
							break;
						default:
							throw new LexicalException("Not an escape sequence", pos);
				
						}
					} 
					
					else if(curr=='\n')
					{
						throw new LexicalException("String unclosed", pos);
					}
					
					else if(curr=='\r')
					{
						throw new LexicalException("String unclosed", pos);
					}
					
					else {
						
						i++;
						pos++;						
						posInLine++;      
						curr = string.charAt(i);
					}
					
				}
				
				posInLine++;
				
				
				
				if (curr!='\"')
				{
					if (flag == 1)
					throw new LexicalException("Unclosed String", pos);
					else
						throw new LexicalException("Unclosed String", pos+1);	
				}
				tokens.add(new Token(Kind.STRING_LITERAL, start, pos-start+1, line, start_posLine));
				continue;
			}
			
			
			if(Character.isAlphabetic(curr) || curr=='_' || curr=='$')
			{
				int start_pos = pos;
				int start_pil = posInLine;
				String ifkey = string.charAt(i)+ "";
				i++;
				pos++;
				posInLine++;
				curr = string.charAt(i);
				while((Character.isAlphabetic(curr) || curr=='_' || curr=='$' || Character.isDigit(curr)) && i<chars.length)
				{
					ifkey+= curr;
					if (curr=='\b')
					{
						throw new LexicalException("\\b is not allowed", pos);
					}
					i++;
					pos++;
					posInLine++;
					curr = string.charAt(i);
					if(curr == '\\')
					{
						throw new LexicalException("\\ is not allowed", pos);
					}
					
				}

				i--;
				pos--;
				
				if (bool1.containsKey(ifkey))
				{
					tokens.add(new Token(Kind.BOOLEAN_LITERAL, start_pos, 4, line, start_pil));
					continue;
				}
				
				else if (bool2.containsKey(ifkey))
				{
					tokens.add(new Token(Kind.BOOLEAN_LITERAL, start_pos, 5, line, start_pil));
					continue;
				}
				
				else if(keyword.containsKey(ifkey))
				{
					tokens.add(new Token(keyword.get(ifkey), start_pos, pos - start_pos + 1, line, start_pil));
					continue;
				}
				
				else{
				tokens.add(new Token(Kind.IDENTIFIER, start_pos, pos - start_pos + 1, line, start_pil));
				continue;}
			}
			
			if(true)
			{
				throw new LexicalException("Not a valid token, comment or white space", pos);
			}
		
		}
			
		
		tokens.add(new Token(Kind.EOF, pos, 0, line, posInLine));
		return this;
		
		
		}

	


	/**
	 * Returns true if the internal iterator has more Tokens
	 * 
	 * @return
	 */
	public boolean hasTokens() {
		return nextTokenPos < tokens.size();
	}

	/**
	 * Returns the next Token and updates the internal iterator so that
	 * the next call to nextToken will return the next token in the list.
	 * 
	 * It is the callers responsibility to ensure that there is another Token.
	 * 
	 * Precondition:  hasTokens()
	 * @return
	 */
	public Token nextToken() {
		return tokens.get(nextTokenPos++);
	}
	
	/**
	 * Returns the next Token, but does not update the internal iterator.
	 * This means that the next call to nextToken or peek will return the
	 * same Token as returned by this methods.
	 * 
	 * It is the callers responsibility to ensure that there is another Token.
	 * 
	 * Precondition:  hasTokens()
	 * 
	 * @return next Token.
	 */
	public Token peek() {
		return tokens.get(nextTokenPos);
	}
	
	
	/**
	 * Resets the internal iterator so that the next call to peek or nextToken
	 * will return the first Token.
	 */
	public void reset() {
		nextTokenPos = 0;
	}

	/**
	 * Returns a String representation of the list of Tokens 
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Tokens:\n");
		for (int i = 0; i < tokens.size(); i++) {
			sb.append(tokens.get(i)).append('\n');
		}
		return sb.toString();
	}

}
