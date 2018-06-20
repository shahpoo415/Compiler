package cop5556fa17;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import cop5556fa17.Scanner.Kind;
import cop5556fa17.Scanner.Token;
import cop5556fa17.TypeUtils.Type;
import cop5556fa17.AST.ASTNode;
import cop5556fa17.AST.ASTVisitor;
import cop5556fa17.AST.Declaration;
import cop5556fa17.AST.Declaration_Image;
import cop5556fa17.AST.Declaration_SourceSink;
import cop5556fa17.AST.Declaration_Variable;
import cop5556fa17.AST.Expression;
import cop5556fa17.AST.Expression_Binary;
import cop5556fa17.AST.Expression_BooleanLit;
import cop5556fa17.AST.Expression_Conditional;
import cop5556fa17.AST.Expression_FunctionAppWithExprArg;
import cop5556fa17.AST.Expression_FunctionAppWithIndexArg;
import cop5556fa17.AST.Expression_Ident;
import cop5556fa17.AST.Expression_IntLit;
import cop5556fa17.AST.Expression_PixelSelector;
import cop5556fa17.AST.Expression_PredefinedName;
import cop5556fa17.AST.Expression_Unary;
import cop5556fa17.AST.Index;
import cop5556fa17.AST.LHS;
import cop5556fa17.AST.Program;
import cop5556fa17.AST.Sink;
import cop5556fa17.AST.Sink_Ident;
import cop5556fa17.AST.Sink_SCREEN;
import cop5556fa17.AST.Source;
import cop5556fa17.AST.Source_CommandLineParam;
import cop5556fa17.AST.Source_Ident;
import cop5556fa17.AST.Source_StringLiteral;
import cop5556fa17.AST.Statement_Assign;
import cop5556fa17.AST.Statement_In;
import cop5556fa17.AST.Statement_Out;

public class TypeCheckVisitor implements ASTVisitor {
	 HashMap<String, Declaration> symbolTable = new HashMap<>();
	

		@SuppressWarnings("serial")
		public static class SemanticException extends Exception {
			Token t;

			public SemanticException(Token t, String message) {
				super("line " + t.line + " pos " + t.pos_in_line + ": "+  message);
				this.t = t;
			}

		}		
		

	
	/**
	 * The program name is only used for naming the class.  It does not rule out
	 * variables with the same name.  It is returned for convenience.
	 * 
	 * @throws Exception 
	 */
	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		for (ASTNode node: program.decsAndStatements) {
			node.visit(this, arg);
		}
		return program.name;
	}

	@Override
	public Object visitDeclaration_Variable(
			Declaration_Variable declaration_Variable, Object arg)
			throws Exception {
				
				declaration_Variable.declaration_type = TypeUtils.getType(declaration_Variable.type);
				Expression e = declaration_Variable.e;
				if(e != null)
				{
					e.visit(this, null);
					if(declaration_Variable.declaration_type != e.expression_type)
						throw new SemanticException(declaration_Variable.firstToken, "dec type is not equal to expression type");
				}
				if(!symbolTable.containsKey(declaration_Variable.name))
				{
					symbolTable.put(declaration_Variable.name, declaration_Variable);
					
				}
				else throw new SemanticException(declaration_Variable.firstToken, "symbol table already contains the name");
		return declaration_Variable;
	}

	@Override
	public Object visitExpression_Binary(Expression_Binary expression_Binary,
			Object arg) throws Exception {
		Expression e0 = expression_Binary.e0;
		Expression e1 = expression_Binary.e1;
		if(e0!=null)
			e0.visit(this, null);
		if(e1 != null)
			e1.visit(this, null);
		
		if(e0.expression_type == e1.expression_type)
		{
			if(expression_Binary.op == Kind.OP_EQ || expression_Binary.op == Kind.OP_NEQ)
				expression_Binary.expression_type = TypeUtils.Type.BOOLEAN;
			
			else if((expression_Binary.op == Kind.OP_GE|| expression_Binary.op == Kind.OP_GT || expression_Binary.op == Kind.OP_LE || expression_Binary.op == Kind.OP_LT) && (e0.expression_type == TypeUtils.Type.INTEGER))
				expression_Binary.expression_type = TypeUtils.Type.BOOLEAN;
			
			else if((expression_Binary.op == Kind.OP_AND || expression_Binary.op == Kind.OP_OR) && (e0.expression_type == TypeUtils.Type.INTEGER || e0.expression_type == TypeUtils.Type.BOOLEAN))
				expression_Binary.expression_type = e0.expression_type;
			
			else if((expression_Binary.op == Kind.OP_DIV || expression_Binary.op == Kind.OP_TIMES || expression_Binary.op == Kind.OP_MOD || expression_Binary.op == Kind.OP_PLUS || expression_Binary.op == Kind.OP_MINUS) && (e0.expression_type == TypeUtils.Type.INTEGER))
				expression_Binary.expression_type = TypeUtils.Type.INTEGER;
			else expression_Binary.expression_type = null;
		}
		else throw new SemanticException(expression_Binary.firstToken, "two expressions are not equal");
		if(expression_Binary.expression_type == null)
			throw new SemanticException(expression_Binary.firstToken, "expression binary is null");
		return expression_Binary;
	}

	@Override
	public Object visitExpression_Unary(Expression_Unary expression_Unary,
			Object arg) throws Exception {
				Expression e = expression_Unary.e;
				if(e!=null)
					e.visit(this, null);
				if((expression_Unary.op == Kind.OP_EXCL) && (e.expression_type == TypeUtils.Type.BOOLEAN || e.expression_type == TypeUtils.Type.INTEGER ))
					expression_Unary.expression_type = e.expression_type;
				else if ((expression_Unary.op == Kind.OP_PLUS || expression_Unary.op == Kind.OP_MINUS) && e.expression_type == TypeUtils.Type.INTEGER)
					expression_Unary.expression_type = TypeUtils.Type.INTEGER;
				else expression_Unary.expression_type = null;
				if(expression_Unary.expression_type == null)
					throw new SemanticException(expression_Unary.firstToken, "expression unary is null");
				return expression_Unary;
	}

	@Override
	public Object visitIndex(Index index, Object arg) throws Exception {
		Expression e0 = index.e0;
		Expression e1 = index.e1;
		
		if(e0 != null){
			e0.visit(this, null);
		}
		if(e1 != null){
			e1.visit(this, null);
		}
		if(e0.expression_type == Type.INTEGER && e1.expression_type ==Type.INTEGER){
			//
			if(e0.getClass() == Expression_PredefinedName.class && e1.getClass() == Expression_PredefinedName.class){
				Expression_PredefinedName ex0 = (Expression_PredefinedName)e0;
				Expression_PredefinedName ex1 = (Expression_PredefinedName)e1;
				index.setCartesian(!(ex0.kind == Kind.KW_r && ex1.kind == Kind.KW_a));
			}
			index.setCartesian(!(e0.firstToken.kind == Kind.KW_r && e1.firstToken.kind == Kind.KW_a));
			
		}else{
			throw new SemanticException(index.firstToken, "Required e0.type = INTEGER.");
		}
		
		//throw new UnsupportedOperationException();
		return index;
	}

	@Override
	public Object visitExpression_PixelSelector(
			Expression_PixelSelector expression_PixelSelector, Object arg)
			throws Exception {
				Index i = expression_PixelSelector.index;
				if(i!=null)
					i.visit(this, null);
				Declaration d = symbolTable.get(expression_PixelSelector.name);
				Type t = TypeUtils.getType(d.firstToken);
				if(t == TypeUtils.Type.IMAGE)
					expression_PixelSelector.expression_type = TypeUtils.Type.INTEGER;
				else if(i == null)
					expression_PixelSelector.expression_type = t;
				else expression_PixelSelector.expression_type = null;
				if(expression_PixelSelector.expression_type == null)
					throw new SemanticException(expression_PixelSelector.firstToken, "expression pixel selector is null");
		return expression_PixelSelector;
	}

	@Override
	public Object visitExpression_Conditional(
			Expression_Conditional expression_Conditional, Object arg)
			throws Exception {
		Expression e0 = expression_Conditional.condition;
		Expression e1 = expression_Conditional.trueExpression;
		Expression e2 = expression_Conditional.falseExpression;
		if(e0!=null)
			e0.visit(this, null);
		if(e1!=null)
			e1.visit(this, null);
		if(e2!=null)
			e2.visit(this, null);
		if(e0.expression_type == TypeUtils.Type.BOOLEAN && e1.expression_type == e2.expression_type)
			expression_Conditional.expression_type = e1.expression_type;
		else throw new SemanticException(expression_Conditional.firstToken, "expression condition is not boolean or the two expressions are not equal");
		
		return expression_Conditional;
	}

	@Override
	public Object visitDeclaration_Image(Declaration_Image declaration_Image,
			Object arg) throws Exception {
				Source s = declaration_Image.source;
				if(s!= null)
					s.visit(this, null);
				if(declaration_Image.xSize != null)
					declaration_Image.xSize.visit(this, null);
				if(declaration_Image.ySize != null)
					declaration_Image.ySize.visit(this, null);
				
				if(!symbolTable.containsKey(declaration_Image.name))
				{
					symbolTable.put(declaration_Image.name, declaration_Image);
					declaration_Image.declaration_type = TypeUtils.Type.IMAGE;
					
				}
				else throw new SemanticException(declaration_Image.firstToken, "in dec_image, symbol table already considers the name");
				if(declaration_Image.xSize != null)
				{
					if(declaration_Image.ySize!=null && declaration_Image.xSize.expression_type == TypeUtils.Type.INTEGER && declaration_Image.ySize.expression_type == TypeUtils.Type.INTEGER)
						return declaration_Image;
					else throw new SemanticException(declaration_Image.firstToken, "either ysize is null or both expressions are not integers");
				}
				return declaration_Image;
					
				
	}

	@Override
	public Object visitSource_StringLiteral(
			Source_StringLiteral source_StringLiteral, Object arg)
			throws Exception {
		URL u = null;
		try{
			u = new java.net.URL(source_StringLiteral.fileOrUrl);
		}
		catch(MalformedURLException e)
		{
			
		}
		if(u != null )
			source_StringLiteral.source_type = TypeUtils.Type.URL;
		else
			source_StringLiteral.source_type = TypeUtils.Type.FILE;
		//throw new UnsupportedOperationException();
		return source_StringLiteral;
	}

	@Override
	public Object visitSource_CommandLineParam(
			Source_CommandLineParam source_CommandLineParam, Object arg)
			throws Exception {
			Expression e = source_CommandLineParam.paramNum;
			if(e != null)
			{
				e.visit(this, null);
			}
			source_CommandLineParam.source_type = e.expression_type;
			if(e.expression_type != TypeUtils.Type.INTEGER)
				throw new SemanticException(source_CommandLineParam.firstToken, "command line param is not integer");
			else
				source_CommandLineParam.source_type = null;
		return source_CommandLineParam;
	}

	@Override
	public Object visitSource_Ident(Source_Ident source_Ident, Object arg)
			throws Exception {
		
				if(symbolTable.containsKey(source_Ident.name))
				{
					Declaration d = symbolTable.get(source_Ident.name);
					Type t = TypeUtils.getType(d.firstToken);
					source_Ident.source_type = t;
				}
				else throw new SemanticException(source_Ident.firstToken, "in source_ident, symbol already present in the table");
				if(source_Ident.source_type == TypeUtils.Type.FILE || source_Ident.source_type == TypeUtils.Type.URL)
					return source_Ident;
				else throw new SemanticException(source_Ident.firstToken, "type is not file or url");
		
	}

	@Override
	public Object visitDeclaration_SourceSink(
			Declaration_SourceSink declaration_SourceSink, Object arg)
			throws Exception {
				if(!symbolTable.containsKey(declaration_SourceSink.name))
				{
					symbolTable.put(declaration_SourceSink.name, declaration_SourceSink);
					Declaration d = symbolTable.get(declaration_SourceSink.name);
					Type t = TypeUtils.getType(d.firstToken);
					declaration_SourceSink.declaration_type = t;
				}
				else throw new SemanticException(declaration_SourceSink.firstToken, "in sorcesink_dec, symbol already present in the table");
				Source s = declaration_SourceSink.source;
				if(s != null)
					s.visit(this, null);
				if(s.source_type == declaration_SourceSink.declaration_type || s.source_type == null)
				{
					return declaration_SourceSink;
				}
				else
				{
					throw new SemanticException(declaration_SourceSink.firstToken, "source type is not equal to sourcesink type");
				}
		//return declaration_SourceSink;
	}

	@Override
	public Object visitExpression_IntLit(Expression_IntLit expression_IntLit,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
				expression_IntLit.expression_type = TypeUtils.Type.INTEGER;
		//throw new UnsupportedOperationException();
				return expression_IntLit;
	}

	@Override
	public Object visitExpression_FunctionAppWithExprArg(
			Expression_FunctionAppWithExprArg expression_FunctionAppWithExprArg,
			Object arg) throws Exception {
				Expression e = expression_FunctionAppWithExprArg.arg;
				if(e != null)
				{
					e.visit(this, null);
				}
				if(e.expression_type == TypeUtils.Type.INTEGER)
					{expression_FunctionAppWithExprArg.expressionFuncApp = TypeUtils.Type.INTEGER;
					expression_FunctionAppWithExprArg.expression_type = TypeUtils.Type.INTEGER;}
				else throw new SemanticException(expression_FunctionAppWithExprArg.firstToken, "expression is not integer");
					
				return expression_FunctionAppWithExprArg;
		
	}

	@Override
	public Object visitExpression_FunctionAppWithIndexArg(
			Expression_FunctionAppWithIndexArg expression_FunctionAppWithIndexArg,
			Object arg) throws Exception {
		Index e = expression_FunctionAppWithIndexArg.arg;
		if(e!=null)
		{
			e.visit(this, null);
		}
		
		expression_FunctionAppWithIndexArg.expressionFuncApp = TypeUtils.Type.INTEGER;
		expression_FunctionAppWithIndexArg.expression_type = TypeUtils.Type.INTEGER;
		return expression_FunctionAppWithIndexArg;
	}

	@Override
	public Object visitExpression_PredefinedName(
			Expression_PredefinedName expression_PredefinedName, Object arg)
			throws Exception {
		
		expression_PredefinedName.expression_type = TypeUtils.Type.INTEGER;
		
		return expression_PredefinedName;
	}

	@Override
	public Object visitStatement_Out(Statement_Out statement_Out, Object arg)
			throws Exception {
				Sink s = statement_Out.sink;
				if(s!= null)
					s.visit(this, null);
				Declaration d = symbolTable.get(statement_Out.name);
				if(d == null)
					throw new SemanticException(statement_Out.firstToken, "declaration is null");
				Type t = TypeUtils.getType(d.firstToken);
				statement_Out.dec = d;
				
				if(!(((t == TypeUtils.Type.INTEGER || t == TypeUtils.Type.BOOLEAN) && s.sink_type == TypeUtils.Type.SCREEN) || ((t == TypeUtils.Type.IMAGE) && (s.sink_type == TypeUtils.Type.FILE || s.sink_type == TypeUtils.Type.SCREEN))))
					throw new SemanticException(statement_Out.firstToken, "type mismatch");
		return statement_Out;
	}

	@Override
	public Object visitStatement_In(Statement_In statement_In, Object arg)
			throws Exception {
			Source s = statement_In.source;
			if(s!= null)
				s.visit(this, null);
			Declaration d = symbolTable.get(statement_In.name);
			//if(d == null)
				//throw new SemanticException(statement_In.firstToken, "declaration is null");
			Type t = TypeUtils.getType(d.firstToken);
			statement_In.dec = d;
			statement_In.statement_type = t;
			//if(s.source_type!= t)
				//throw new SemanticException(statement_In.firstToken, "source type is not equal to name type");
		return statement_In;
	}

	@Override
	public Object visitStatement_Assign(Statement_Assign statement_Assign,
			Object arg) throws Exception {
			LHS lhs = statement_Assign.lhs;
			Expression e = statement_Assign.e;
			if(e != null)
				e.visit(this, null);
			if(lhs != null)
			{
				lhs.visit(this, null);
				statement_Assign.isCartesian = lhs.isCartesian;

			}
			else statement_Assign.isCartesian = false;
			
			
			if(e.expression_type == lhs.lhs_type || (lhs.lhs_type == Type.IMAGE && e.expression_type == Type.INTEGER)){
				
			}
			else
			{
				throw new SemanticException(statement_Assign.firstToken, "expression and lhs type donot match");
			}
		return statement_Assign;
	}

	@Override
	public Object visitLHS(LHS lhs, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Index i = lhs.index;
		if(i!=null)
			i.visit(this, null);
		if(symbolTable.containsKey(lhs.name))
		{
			lhs.dec = symbolTable.get(lhs.name);
		}
		else throw new SemanticException(lhs.firstToken, "in lhs, symbol already present");
		if(i!=null)
		{
			lhs.isCartesian = i.isCartesian();
		}
		else
			lhs.isCartesian = false;
		lhs.lhs_type = lhs.dec.declaration_type;
		return lhs;
	}

	@Override
	public Object visitSink_SCREEN(Sink_SCREEN sink_SCREEN, Object arg)
			throws Exception {
	
		sink_SCREEN.sink_type = TypeUtils.Type.SCREEN;
		
		return sink_SCREEN;
	}

	@Override
	public Object visitSink_Ident(Sink_Ident sink_Ident, Object arg)
			throws Exception {
				if(symbolTable.containsKey(sink_Ident.name))
				{
					sink_Ident.sink_type = symbolTable.get(sink_Ident.name).declaration_type;
				}
				else throw new SemanticException(sink_Ident.firstToken, "in sink_ident, symbol already present");
				if(sink_Ident.sink_type != TypeUtils.Type.FILE)
					throw new SemanticException(sink_Ident.firstToken, "sink_ident is not file");
				return sink_Ident;
	}

	@Override
	public Object visitExpression_BooleanLit(
			Expression_BooleanLit expression_BooleanLit, Object arg)
			throws Exception {
		expression_BooleanLit.expression_type = TypeUtils.Type.BOOLEAN;
		return expression_BooleanLit;
	}

	@Override
	public Object visitExpression_Ident(Expression_Ident expression_Ident,
			Object arg) throws Exception {
				if(symbolTable.containsKey(expression_Ident.name))
				{
					
					Declaration d = symbolTable.get(expression_Ident.name);
					Type t = TypeUtils.getType(d.firstToken);
					expression_Ident.expression_type= t;
				}
				else throw new SemanticException(expression_Ident.firstToken, "in expression_ident, symbol not present");
		return expression_Ident;
	}

}
