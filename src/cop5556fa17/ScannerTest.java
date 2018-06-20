/**
 * /**
 * JUunit tests for the Scanner for the class project in COP5556 Programming Language Principles 
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

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556fa17.Scanner.Kind;
import cop5556fa17.Scanner.LexicalException;
import cop5556fa17.Scanner.Token;

import static cop5556fa17.Scanner.Kind.*;

public class ScannerTest {

	//set Junit to be able to catch exceptions
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	
	//To make it easy to print objects and turn this output on and off
	static final boolean doPrint = true;
	private void show(Object input) {
		if (doPrint) {
			System.out.println(input.toString());
		}
	}

	
	
	Token checkNextIsEOF(Scanner scanner) {
		Scanner.Token token = scanner.nextToken();
		assertEquals(Scanner.Kind.EOF, token.kind);
		assertFalse(scanner.hasTokens());
		return token;
	}


		Token checkNext(Scanner scanner, Scanner.Kind kind, int pos, int length, int line, int pos_in_line) {
		Token t = scanner.nextToken();
		assertEquals(scanner.new Token(kind, pos, length, line, pos_in_line), t);
		return t;
	}

	
	Token check(Scanner scanner, Scanner.Kind kind, int length) {
		Token t = scanner.nextToken();
		assertEquals(kind, t.kind);
		assertEquals(length, t.length);
		return t;
	}

	/**
	 * Simple test case with a (legal) empty program
	 *   
	 * @throws LexicalException
	 */
	@Test
	public void testEmpty() throws LexicalException {
		String input = "";  //The input is the empty string.  This is legal
		show(input);        //Display the input 
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		checkNextIsEOF(scanner);  //Check that the only token is the EOF token.
	}
	
	/**
	 * Test illustrating how to put a new line in the input program and how to
	 * check content of tokens.
	 * 
	 * Because we are using a Java String literal for input, we use \n for the
	 * end of line character. (We should also be able to handle \n, \r, and \r\n
	 * properly.)
	 * 
	 * Note that if we were reading the input from a file, as we will want to do 
	 * later, the end of line character would be inserted by the text editor.
	 * Showing the input will let you check your input is what you think it is.
	 * 
	 * @throws LexicalException
	 */
	@Test
	public void testSemi() throws LexicalException {
		String input = ";==\n1234;\r\nabc,\rboolean **** //true\na\"\\f\"false ([])";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		//checkNext(scanner, Kind.BOOLEAN_LITERAL, 0, 4, 1,1);
		checkNext(scanner, SEMI, 0, 1, 1, 1);
		checkNext(scanner, Kind.OP_EQ, 1, 2, 1, 2);
		checkNext(scanner, Kind.INTEGER_LITERAL, 4, 4, 2, 1);
		checkNext(scanner, SEMI, 8, 1, 2, 5);
		checkNext(scanner, Kind.IDENTIFIER, 11, 3, 3, 1);
		checkNext(scanner, COMMA, 14, 1, 3, 4);
		checkNext(scanner, Kind.KW_boolean, 16, 7, 4, 1);
		checkNext(scanner, Kind.OP_POWER, 24, 2, 4, 9);
		checkNext(scanner, Kind.OP_POWER, 26, 2, 4, 11);
		checkNext(scanner, Kind.KW_a, 36, 1, 5, 1);
		checkNext(scanner, Kind.STRING_LITERAL, 37, 4, 5, 2);
		checkNext(scanner, Kind.BOOLEAN_LITERAL, 41, 5, 5,6);
		checkNext(scanner, Kind.LPAREN, 47, 1, 5,12);
		checkNext(scanner, Kind.LSQUARE, 48, 1, 5,13);
		checkNext(scanner, Kind.RSQUARE, 49, 1, 5,14);
		checkNext(scanner, Kind.RPAREN, 50, 1, 5,15);
		
		checkNextIsEOF(scanner);
	}
	
	@Test
	public void testSem() throws LexicalException {
		String input = "//abcdef\rdef";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, Kind.IDENTIFIER, 9, 3, 2, 1);
		//checkNext(scanner, Kind.OP_DIV, 2, 1, 1, 3);
		//checkNext(scanner, Kind.IDENTIFIER, 3, 1, 1, 4);
		checkNextIsEOF(scanner);
	}

	
	@Test
	public void testSem1() throws LexicalException {
		String input = "\"111111111111111111~@#1111111111111111\"";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, Kind.STRING_LITERAL, 0, 39, 1, 1);
		//checkNext(scanner, Kind.OP_DIV, 2, 1, 1, 3);
		//checkNext(scanner, Kind.IDENTIFIER, 3, 1, 1, 4);
		checkNextIsEOF(scanner);
	}

	
	@Test
	public void testSe() throws LexicalException {
		String input = "012345)\"\tab c \"";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, Kind.INTEGER_LITERAL, 0, 1, 1, 1);
		checkNext(scanner, Kind.INTEGER_LITERAL, 1, 5, 1, 2);
		checkNext(scanner, Kind.RPAREN, 6, 1, 1, 7);
		checkNext(scanner, Kind.STRING_LITERAL, 7, 8, 1, 8);
		checkNextIsEOF(scanner);
	}
	
	@Test
	public void testS() throws LexicalException {
		String input = "//abc\n\r abc";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		//checkNext(scanner, Kind.INTEGER_LITERAL, 1, 1, 1, 2);
		//checkNext(scanner, Kind.INTEGER_LITERAL, 2, 2, 1, 3);
		checkNext(scanner, Kind.IDENTIFIER, 8, 3, 3, 2);
		checkNextIsEOF(scanner);
	}
	
	@Test
	public void testSt() throws LexicalException {
		String input = "\" \\'\" 0 _$atv$_0";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		//checkNext(scanner, Kind.INTEGER_LITERAL, 1, 1, 1, 2);
		//checkNext(scanner, Kind.INTEGER_LITERAL, 2, 2, 1, 3);
		checkNext(scanner, Kind.STRING_LITERAL, 0, 5, 1, 1);
		checkNext(scanner, Kind.INTEGER_LITERAL, 6, 1, 1, 7);
		checkNext(scanner, Kind.IDENTIFIER, 8, 8, 1, 9);
		checkNextIsEOF(scanner);
	}

	
	@Test
	public void testSi() throws LexicalException {
		String input = "\"\\f\\t\\\\\" 1Abc a0_$bc\r (123) !==== a True;";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, STRING_LITERAL, 0, 8, 1, 1);
		checkNext(scanner, Kind.INTEGER_LITERAL, 9, 1, 1, 10);
		checkNext(scanner, Kind.IDENTIFIER, 10, 3, 1, 11);
		checkNext(scanner, Kind.IDENTIFIER, 14, 6, 1, 15);
		checkNext(scanner, Kind.LPAREN, 22, 1, 2, 2);
		checkNext(scanner, Kind.INTEGER_LITERAL, 23, 3, 2, 3);
		checkNext(scanner, Kind.RPAREN, 26, 1, 2, 6);
		checkNext(scanner, Kind.OP_NEQ, 28, 2, 2, 8);
		checkNext(scanner, Kind.OP_EQ, 30, 2, 2, 10);
		checkNext(scanner, Kind.OP_ASSIGN, 32, 1, 2, 12);
		checkNext(scanner, Kind.KW_a, 34, 1, 2, 14);
		checkNext(scanner, Kind.IDENTIFIER, 36, 4, 2, 16);
		checkNext(scanner, Kind.SEMI, 40, 1, 2, 20);
		checkNextIsEOF(scanner);
	}
	
	@Test
	public void fail() throws LexicalException {
		String input = "abcbb\b";
		show(input);
		thrown.expect(LexicalException.class);  //Tell JUnit to expect a LexicalException
		try {
			new Scanner(input).scan();
		} catch (LexicalException e) {  //
			show(e);
			assertEquals(5,e.getPos());
			throw e;
		}
	}
	
	@Test
	public void fail1() throws LexicalException {
		String input = "\"\\f\\t\\r\\b\\n\\\"\\";
		show(input);
		thrown.expect(LexicalException.class);  //Tell JUnit to expect a LexicalException
		try {
			new Scanner(input).scan();
		} catch (LexicalException e) {  //
			show(e);
			assertEquals(13,e.getPos());
			throw e;
		}
	}
	
	/**
	 * This example shows how to test that your scanner is behaving when the
	 * input is illegal.  In this case, we are giving it a String literal
	 * that is missing the closing ".  
	 * 
	 * Note that the outer pair of quotation marks delineate the String literal
	 * in this test program that provides the input to our Scanner.  The quotation
	 * mark that is actually included in the input must be escaped, \".
	 * 
	 * The example shows catching the exception that is thrown by the scanner,
	 * looking at it, and checking its contents before rethrowing it.  If caught
	 * but not rethrown, then JUnit won't get the exception and the test will fail.  
	 * 
	 * The test will work without putting the try-catch block around 
	 * new Scanner(input).scan(); but then you won't be able to check 
	 * or display the thrown exception.
	 * 
	 * @throws LexicalException
	 *//*
*/	@Test
	public void failUnclosedStringLiteral() throws LexicalException {
		String input = "\" greetings  ";
		show(input);
		thrown.expect(LexicalException.class);  //Tell JUnit to expect a LexicalException
		try {
			new Scanner(input).scan();
		} catch (LexicalException e) {  //
			show(e);
			assertEquals(13,e.getPos());
			throw e;
		}
	}
}
