package cop5556fa17.AST;

import cop5556fa17.Scanner.Token;
import cop5556fa17.TypeUtils.Type;

public abstract class Expression_FunctionApp extends Expression {
	
	public Type expressionFuncApp;

	public Expression_FunctionApp(Token firstToken) {
		super(firstToken);
		
	}

}
