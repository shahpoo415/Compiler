package cop5556fa17.AST;

import cop5556fa17.Scanner.Token;
import cop5556fa17.TypeUtils.Type;

public abstract class Sink extends ASTNode {
	public Type sink_type;
	
	public Sink(Token firstToken) {
		super(firstToken);
	}
	

}
