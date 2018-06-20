package cop5556fa17;



import java.util.ArrayList;
import java.util.Arrays;
import cop5556fa17.AST.*;

import cop5556fa17.Scanner.Kind;
import cop5556fa17.Scanner.Token;
import cop5556fa17.Parser.SyntaxException;

import static cop5556fa17.Scanner.Kind.*;

public class Parser {

	@SuppressWarnings("serial")
	public class SyntaxException extends Exception {
		Token t;

		public SyntaxException(Token t, String message) {
			super(message);
			this.t = t;
		}

	}
	
	
	

	Scanner scanner;
	Token t;

	Parser(Scanner scanner) {
		this.scanner = scanner;
		t = scanner.nextToken();
	}

	/**
	 * Main method called by compiler to parser input.
	 * Checks for EOF
	 * 
	 * @throws SyntaxException
	 */
	public Program parse() throws SyntaxException {
		Program p = program();
		matchEOF();
		return p;
		
	}
	

	/**
	 * Program ::=  IDENTIFIER   ( Declaration SEMI | Statement SEMI )*   
	 * 
	 * Program is start symbol of our grammar.
	 * 
	 * @throws SyntaxException
	 */
	 Program program() throws SyntaxException {
		//TODO  implement this
		Token firstToken = t;
		
		ArrayList<ASTNode> dns = new ArrayList<>();
		if (t.kind == Kind.IDENTIFIER)
		{ 
			consume();
			if (scanner.hasTokens())
			{
				if( t.kind == Kind.KW_int || t.kind== Kind.KW_boolean || t.kind== Kind.KW_image || t.kind== Kind.KW_file || t.kind== Kind.KW_url || t.kind == Kind.IDENTIFIER )
				{
					while (t.kind == Kind.KW_int || t.kind== Kind.KW_boolean || t.kind== Kind.KW_image || t.kind== Kind.KW_file || t.kind== Kind.KW_url || t.kind == Kind.IDENTIFIER )
					{
						if(t.kind == Kind.KW_int || t.kind== Kind.KW_boolean || t.kind== Kind.KW_image || t.kind== Kind.KW_file || t.kind== Kind.KW_url )
						{
							dns.add(declaration());

						}
						
						else if (t.kind == Kind.IDENTIFIER)
						{
							dns.add(statement());
						}
						else throw new SyntaxException(t, "Expected declaration or statement at " + t.line + ":" + t.pos_in_line);
						if (t.kind == Kind.SEMI)
						{
							consume();
							//return new Program(firstToken, firstToken, dns);
						}
						else throw new SyntaxException(t, "Expected Semi at " + t.line + ":" + t.pos_in_line);
					}
				}
				else return new Program(firstToken, firstToken, dns);
			}
			else return new Program(firstToken, firstToken, dns);
		}
		else throw new SyntaxException(t, "Expected Identifier at " + t.line + ":" + t.pos_in_line);
		return new Program(firstToken, firstToken, dns);
	}
	
	Declaration declaration() throws SyntaxException {
		Token firstToken = t;
		Declaration d = null;
		//Declaration_Variable dv = null;
		if (t.kind == Kind.KW_int || t.kind== Kind.KW_boolean)
		{
			d = varDec();
			//d =  new Declaration(firstToken);
		}
		else if ( t.kind == KW_image)
		{
		
			 d= imageDec();
			//return new Declaration_Image(firstToken, e1, e2, firstToken.getText(), source);
		}
		else if (t.kind == Kind.KW_file || t.kind == Kind.KW_url)
		{
		
			d = sourceSinkDec();
		}
		else 
			throw new SyntaxException(t, "first of declaration EOL at " + t.line + ":" + t.pos_in_line);
		return d;
	}
	
	
	Declaration_Variable varDec() throws SyntaxException {
		Token firstToken = t;
		consume();
		Declaration_Variable dv = null;
		Expression e = null;
		Token name = t; 
		if (t.kind == Kind.IDENTIFIER)
		{
			
			consume();
		
			if (t.kind == Kind.OP_ASSIGN)
			{
				consume();
				e = expression();
				dv =  new Declaration_Variable(firstToken, firstToken, name, e);
				
			}
			else dv =  new Declaration_Variable(firstToken, firstToken, name, e);
			
		}
		
		else throw new SyntaxException(t, "Expected Identifier at " + t.line + ":" + t.pos_in_line);
		return dv;
			
	}
	
	Declaration_Image imageDec() throws SyntaxException{
		Declaration_Image di = null;
		Token firstToken = t;
		consume();
		Expression e1 = null;
		Expression e2 = null;
		Token name = null;
		Source s = null;
		if (t.kind == Kind.LSQUARE)
		{
			consume();
			
			e1 = expression();
			
			if (t.kind == Kind.COMMA)
			{
				consume();
				
				e2 = expression();
				
				if (t.kind == Kind.RSQUARE)
				{
					consume();
					
					if (t.kind == Kind.IDENTIFIER)
					{   name = t;
						consume();
						
						if (t.kind == Kind.OP_LARROW)
						{
							consume();
							 s = source();
							 di = new Declaration_Image(firstToken, e1, e2, name, s);
						}
						else di = new Declaration_Image(firstToken, e1, e2, name, s);
				}
				else throw new SyntaxException(t, "Expected Identifier at " + t.line + ":" + t.pos_in_line);
			}
			else throw new SyntaxException(t, "Expected ] at " + t.line + ":" + t.pos_in_line);
		}
		else throw new SyntaxException(t, "Expected , at " + t.line + ":" + t.pos_in_line);	
		
	}
		
		else if (t.kind == Kind.IDENTIFIER)
		{
			name = t;
			consume();
			
			if (t.kind == Kind.OP_LARROW)
			{
				consume();
				s = source();
				di = new Declaration_Image(firstToken, e1, e2, name, s);
			}
			else di = new Declaration_Image(firstToken, e1, e2, name, s);
		}
		
		else throw new SyntaxException(t, "Expected Identifier or [ at " + t.line + ":" + t.pos_in_line);
		return di;
	}
	
	
	Declaration_SourceSink sourceSinkDec() throws SyntaxException {
		Declaration_SourceSink dss = null;
		Token firstToken = t;
		consume();
		Source s = null;
		Token name = null;
		if (t.kind == Kind.IDENTIFIER)
		{
			name = t;
			consume();
			if (t.kind == Kind.OP_ASSIGN)
			{
				consume();
				s = source();
				dss = new Declaration_SourceSink(firstToken, firstToken, name, s);
			}
			else throw new SyntaxException(t, "Expected = at " + t.line + ":" + t.pos_in_line);
		}
		else throw new SyntaxException(t, "Expected Identifier at " + t.line + ":" + t.pos_in_line);
		return dss;
	}
	
	Source source() throws SyntaxException {
		Token firstToken = t;
		Source s = null;
		Expression e = null;
		if (t.kind == Kind.STRING_LITERAL)
			{
				String uf = t.getText();	
				consume();
				s= new Source_StringLiteral(firstToken, uf);
			}
		
		else if (t.kind == Kind.OP_AT)
			{
				consume();
				e = expression();
				s=new Source_CommandLineParam(firstToken, e);
			}
		else if (t.kind == Kind.IDENTIFIER)
		{
			consume();
			s = new Source_Ident(firstToken, firstToken);
		}
		
		else throw new SyntaxException(t, "Expected String literal or @ or Identifier at " + t.line + ":" + t.pos_in_line);
		return s;
	}
	
	Statement statement() throws SyntaxException {
		Token firstToken = t;
		consume();
		Statement_Out so = null;
		Statement_In si = null;
		Expression e = null;
		LHS lhs = null;
		Sink sn = null;
		Statement_Assign as = null;
		Index i = null;
		Source s = null;
		if (t.kind == Kind.OP_RARROW)
		{
			consume();
			 sn = sink();
			return new Statement_Out(firstToken, firstToken, sn);
		}
		else if (t.kind== Kind.OP_LARROW)
		{
			consume();
			s = source();
			return new Statement_In(firstToken, firstToken, s);
		}
		
		else if (t.kind == Kind.OP_ASSIGN)
		{
			consume();
			e = expression();
			lhs = new LHS(firstToken, firstToken, i);
			return new Statement_Assign(firstToken,lhs,e);
		}
		
		else if (t.kind == Kind.LSQUARE)
		{
			consume();
			i = lhsSelector();
			
			if (t.kind == Kind.RSQUARE)
			{
				consume();
				
				if (t.kind== Kind.OP_ASSIGN)
				{
					consume();
					e = expression();
					
					lhs = new LHS(firstToken, firstToken, i);
					return new Statement_Assign(firstToken,lhs,e);
				}
				else throw new SyntaxException(t, "Expected = at " + t.line + ":" + t.pos_in_line);
			}
			else throw new SyntaxException(t, "Expected ] at " + t.line + ":" + t.pos_in_line);
		}
		else throw new SyntaxException(t, "Expected -> or <- or = or [ at " + t.line + ":" + t.pos_in_line);
		//return new Statement(firstToken);
		
	}
	
	
	
	
	Sink sink() throws SyntaxException {
		Sink sn = null;
		Token firstToken = t;
		Token name = null;
		if (t.kind == Kind.IDENTIFIER)
		{
			name = t;
			consume();
			sn = new Sink_Ident(firstToken, name);
		}
		else if (t.kind == Kind.KW_SCREEN)
		{
			name = t;
			consume();
			sn = new Sink_SCREEN(firstToken);
		}
		else throw new SyntaxException(t, "Expected Identifier or Screen at " + t.line + ":" + t.pos_in_line);
		return sn;
		
	}
	
	
	
	
	Index lhsSelector() throws SyntaxException {
			Index i = null;
			Expression e0 = null;
			Expression e1 = null;
			if (t.kind == Kind.LSQUARE)
			{
				consume();
				
				if (t.kind == Kind.KW_x)
				{
					Token firstToken = t;
					e0 = new Expression_PredefinedName(firstToken, firstToken.kind);
					consume();
					if (t.kind == Kind.COMMA)
					{
						consume();
						
						if (t.kind == Kind.KW_y)
						{	Token first = t;
							consume();
							e1 = new Expression_PredefinedName(first, first.kind);
							i = new Index(firstToken, e0, e1);
						}
						else throw new SyntaxException(t, "Expected y at " + t.line + ":" + t.pos_in_line);
					}
					else throw new SyntaxException(t, "Expected , at " + t.line + ":" + t.pos_in_line);
				}
				else if (t.kind == Kind.KW_r)
				{	Token firstToken = t;
					consume();
					e0 = new Expression_PredefinedName(firstToken, firstToken.kind);
					if (t.kind == Kind.COMMA)
					{
						consume();
						
						if (t.kind == Kind.KW_a)
						{	Token first = t;
							consume();
							e1 = new Expression_PredefinedName(first, first.kind);
							i = new Index(firstToken, e0, e1);
						}
						else throw new SyntaxException(t, "Expected A at " + t.line + ":" + t.pos_in_line);
					}
					else throw new SyntaxException(t, "Expected , at " + t.line + ":" + t.pos_in_line);
				} 
				else throw new SyntaxException(t, "Expected x or r at " + t.line + ":" + t.pos_in_line);
				
				if (t.kind == Kind.RSQUARE)
				{
					consume();
				}
				else throw new SyntaxException(t, "Expected ] at " + t.line + ":" + t.pos_in_line);
			}
			else throw new SyntaxException(t, "Expected [ at " + t.line + ":" + t.pos_in_line);
			return i;
		}
	
	
	
	/**
	 * Expression ::=  OrExpression  OP_Q  Expression OP_COLON Expression    | OrExpression
	 * 
	 * Our test cases may invoke this routine directly to support incremental development.
	 * 
	 * @throws SyntaxException
	 */
	Expression expression() throws SyntaxException {
		//TODO implement this.
		Token firstToken = t;
		Expression_Conditional ec = null;
		Expression e0 = null;
		Expression e1 = null;
		Expression e2 = null;
		e0 = orExpression();
	
		if (t.kind == Kind.OP_Q)
		{
			consume();
			
			e1 = expression();
			
			if (t.kind == Kind.OP_COLON)
			{
				consume();
				
				e2 = expression();
				ec = new Expression_Conditional(firstToken, e0, e1, e2);
				return ec;
			}
			else throw new SyntaxException(t, "Expected : at " + t.line + ":" + t.pos_in_line);
		}
		
		else return e0;
		//return ec;
		
		
	}
	
	Expression orExpression() throws SyntaxException{
		
		Token firstToken = t;
		Expression e0 = null;
		Expression e1 = null;
		e0 = andExpression();
		
			if (t.kind == OP_OR)
			{
				while(t.kind == Kind.OP_OR)
				{
					Token op = t;
					consume();
					e1 = andExpression();
					e0 = new Expression_Binary(firstToken, e0, op, e1);
				}
			}
			else return e0;
			return e0;
	}
	
	Expression andExpression() throws SyntaxException{
			
		Token firstToken = t;
		Expression e0 = null;
		Expression e1 = null;
		Expression_Binary eb = null;	
		e0 = eqExpression();
			
				if (t.kind == OP_AND)
				{
					while(t.kind == Kind.OP_AND)
					{
						Token op = t;
						consume();
						e1 = eqExpression();
						e0 = new Expression_Binary(firstToken, e0, op, e1);
					}
				}
				else return e0;
				return e0;
		}
	
	Expression eqExpression() throws SyntaxException{
		
		Token firstToken = t;
		Expression e0 = null;
		Expression e1 = null;
		Expression_Binary eb = null;
		e0 = relExpression();
		
			if (t.kind == OP_EQ || t.kind == OP_NEQ)
			{
				while(t.kind == OP_EQ || t.kind == OP_NEQ)
				{
					Token op = t;
					consume();
					e1 = relExpression();
					e0 = new Expression_Binary(firstToken, e0, op, e1);
				}
			}
			else return e0;
			return e0;
	}
	
	Expression relExpression() throws SyntaxException{
		
		Token firstToken = t;
		Expression e0 = null;
		Expression e1 = null;
		Expression_Binary eb = null;
		e0 = addExpression();
		
			if (t.kind == OP_LT || t.kind == OP_LE || t.kind == OP_GT || t.kind == OP_GE)
			{
				while(t.kind == OP_LT || t.kind == OP_LE || t.kind == OP_GT || t.kind == OP_GE)
				{
					Token op = t;
					consume();
					e1 = addExpression();
					e0 = new Expression_Binary(firstToken, e0, op, e1);
				}
			}
			else return e0;
			return e0;
	}
	
	Expression addExpression() throws SyntaxException{
		
		Token firstToken = t;
		Expression e0 = null;
		Expression e1 = null;
		Expression_Binary eb = null;
		e0 = multExpression();
		
			if (t.kind == Kind.OP_PLUS || t.kind == Kind.OP_MINUS )
			{
				while(t.kind == Kind.OP_PLUS || t.kind == Kind.OP_MINUS)
				{
					Token op = t;
					consume();
					e1 = multExpression();
					e0 = new Expression_Binary(firstToken, e0, op, e1);
				}
			}
			else return e0;
			return e0;
	}
	
	Expression multExpression() throws SyntaxException{
		
		Token firstToken = t;
		Expression e0 = null;
		Expression e1 = null;
		Expression_Binary eb = null;
		e0 = unaryExpression();
		
			if (t.kind == Kind.OP_TIMES || t.kind == Kind.OP_DIV || t.kind == Kind.OP_MOD)
			{
				while(t.kind == Kind.OP_TIMES || t.kind == Kind.OP_DIV || t.kind == Kind.OP_MOD)
				{
					Token op = t;
					consume();
					e1 = unaryExpression();
					e0 = new Expression_Binary(firstToken, e0, op, e1);
				}
			}
			else return e0;
			return e0;
	}
	
	Expression unaryExpression() throws SyntaxException{
		Token firstToken = t;
		Expression eu = null;
		Expression e = null;
		
		if(t.kind == Kind.OP_PLUS)
		{
			Token op = t;
			consume();
			e = unaryExpression();
			eu = new Expression_Unary(firstToken, op, e);
		}
		else if(t.kind == Kind.OP_MINUS)
		{
			Token op = t;
			consume();
			e = unaryExpression();
			eu = new Expression_Unary(firstToken, op, e);
		}
		else  
			 eu = unaryExpressionNotPlusMinus();
		return eu;
	}
	
	Expression unaryExpressionNotPlusMinus() throws SyntaxException{
		Token firstToken = t;
		Expression_Unary eu = null;
		Expression e = null;
		Expression_PredefinedName ep = null;
		
		if (t.kind == Kind.OP_EXCL)
		{
			Token op = t;
			consume();
			e = unaryExpression();
			eu = new Expression_Unary(firstToken, op, e);
		}
		else if (t.kind == Kind.INTEGER_LITERAL || t.kind == Kind.LPAREN || t.kind == Kind.BOOLEAN_LITERAL || t.kind == Kind.KW_sin || t.kind == Kind.KW_cos || t.kind == Kind.KW_atan || t.kind == Kind.KW_abs || t.kind == Kind.KW_cart_x || t.kind == Kind.KW_cart_y || t.kind == KW_polar_a || t.kind == Kind.KW_polar_r )
		{
			Expression e1 = primary();
			return e1;
		}
		else if (t.kind == Kind.IDENTIFIER)
		{
			Expression e1 = identOrPixelSelectorExpression();
			return e1;
		}
		else if (t.kind == KW_x || t.kind == KW_y || t.kind == KW_r || t.kind == KW_a || t.kind == KW_X || t.kind == KW_Y || t.kind == KW_Z || t.kind == KW_A || t.kind ==KW_R || t.kind ==KW_DEF_X || t.kind ==KW_DEF_Y)
		{
			Token s = t;
			consume();
			return new Expression_PredefinedName(s, s.kind);
			
		}
		else throw new SyntaxException(t, "Expected ! or first of primary or identifier at " + t.line + ":" + t.pos_in_line);
		return eu;
	}
	
	
	Expression primary() throws SyntaxException{
		Expression e = null;
		Index i = null;
		
		if(t.kind == Kind.INTEGER_LITERAL)
		{
			Token firstToken = t;
			String a = t.getText();
			int value = Integer.parseInt(a);
			consume();
			return new Expression_IntLit(firstToken, value);
		}
		
		else if(t.kind == Kind.BOOLEAN_LITERAL)
		{
			Token firstToken = t;
			String a = t.getText();
			boolean value = Boolean.valueOf(a);
			consume();
			return new Expression_BooleanLit(firstToken, value);
		}
		else if (t.kind == Kind.KW_sin || t.kind == Kind.KW_cos || t.kind == Kind.KW_atan || t.kind == Kind.KW_abs || t.kind == Kind.KW_cart_x || t.kind == Kind.KW_cart_y || t.kind == KW_polar_a || t.kind == Kind.KW_polar_r)
		{
			Token firstToken = t;
			consume();
			if (t.kind == Kind.LPAREN)
			{
				consume();
				
				e = expression();
				
				if (t.kind == Kind.RPAREN)
				{
					consume();
					return new Expression_FunctionAppWithExprArg(firstToken, firstToken.kind, e);
				}
				else throw new SyntaxException(t, "Expected ) at " + t.line + ":" + t.pos_in_line);
			}
			else if (t.kind == Kind.LSQUARE)
			{
				
				consume();
				i = selector();
				
				if (t.kind == Kind.RSQUARE)
				{
					consume();
					return new Expression_FunctionAppWithIndexArg(firstToken, firstToken.kind, i);
				}
				else throw new SyntaxException(t, "Expected ] at " + t.line + ":" + t.pos_in_line);
			}
			else throw new SyntaxException(t, "Expected ( or [ at " + t.line + ":" + t.pos_in_line);
		}
		else if (t.kind == Kind.LPAREN)
		{
			consume();
			e = expression();
			
			if (t.kind == Kind.RPAREN)
			{
				consume();
			}
			else throw new SyntaxException(t, "Expected ) at " + t.line + ":" + t.pos_in_line);
		}
		else throw new SyntaxException(t, "Expected at " + t.line + ":" + t.pos_in_line);
		return e;
	}
	
	Index selector() throws SyntaxException{
		Expression e0 = null;
		Expression e1 = null;
		Token firstToken = t;
		e0 = expression();
		if (t.kind == Kind.COMMA)
		{
			consume();
		}
		else throw new SyntaxException(t, "Expected , at " + t.line + ":" + t.pos_in_line);
		e1 = expression();
		return new Index(firstToken, e0, e1);
	}
	
	Expression identOrPixelSelectorExpression() throws SyntaxException{
		Token firstToken = t;
		Expression_Ident ei = null;
		Expression_PixelSelector ep = null;
		Index i = null;
		
		consume();
		if (t.kind == Kind.LSQUARE)
		{
			consume();
			i = selector();
			
			if (t.kind == Kind.RSQUARE)
			{
				consume();
				return new Expression_PixelSelector(firstToken, firstToken, i);
			}
			else throw new SyntaxException(t, "Expected ] at " + t.line + ":" + t.pos_in_line);
		}
		else return new Expression_Ident(firstToken, firstToken);
		
	}

	/**
	 * Only for check at end of program. Does not "consume" EOF so no attempt to get
	 * nonexistent next Token.
	 * 
	 * @return
	 * @throws SyntaxException
	 */
	private Token matchEOF() throws SyntaxException {
		if (t.kind == EOF) {
			return t;
		}
		String message =  "Expected EOL at " + t.line + ":" + t.pos_in_line;
		throw new SyntaxException(t, message);
	}
	
	Token consume(){
		t= scanner.nextToken();
		return t;
	}
	
	void match(Kind kind){
		if(t.kind== kind){
			consume();
		}
	}

	
}
