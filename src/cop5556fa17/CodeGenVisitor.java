package cop5556fa17;

import java.util.ArrayList;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import cop5556fa17.Scanner.Kind;
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
import cop5556fa17.AST.Statement_In;
import cop5556fa17.AST.Statement_Out;
import cop5556fa17.AST.Statement_Assign;
//import cop5556fa17.image.ImageFrame;
//import cop5556fa17.image.ImageSupport;

public class CodeGenVisitor implements ASTVisitor, Opcodes {

	/**
	 * All methods and variable static.
	 */


	/**
	 * @param DEVEL
	 *            used as parameter to genPrint and genPrintTOS
	 * @param GRADE
	 *            used as parameter to genPrint and genPrintTOS
	 * @param sourceFileName
	 *            name of source file, may be null.
	 */
	public CodeGenVisitor(boolean DEVEL, boolean GRADE, String sourceFileName) {
		super();
		this.DEVEL = DEVEL;
		this.GRADE = GRADE;
		this.sourceFileName = sourceFileName;
	}

	ClassWriter cw;
	String className;
	String classDesc;
	String sourceFileName;

	MethodVisitor mv; // visitor of method currently under construction

	/** Indicates whether genPrint and genPrintTOS should generate code. */
	final boolean DEVEL;
	final boolean GRADE;
	


	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		className = program.name;  
		classDesc = "L" + className + ";";
		String sourceFileName = (String) arg;
		cw.visit(52, ACC_PUBLIC + ACC_SUPER, className, null, "java/lang/Object", null);
		cw.visitSource(sourceFileName, null);
		// create main method
		mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
		// initialize
		mv.visitCode();		
		//add label before first instruction
		Label mainStart = new Label();
		mv.visitLabel(mainStart);		
		
		
		cw.visitField(ACC_STATIC, "x", "I", null, new Integer(0)).visitEnd();
		cw.visitField(ACC_STATIC, "y", "I", null, new Integer(0)).visitEnd();
		cw.visitField(ACC_STATIC, "X", "I", null, new Integer(0)).visitEnd();
		cw.visitField(ACC_STATIC, "Y", "I", null, new Integer(0)).visitEnd();
		cw.visitField(ACC_STATIC, "r", "I", null, new Integer(0)).visitEnd();
		cw.visitField(ACC_STATIC, "a", "I", null, new Integer(0)).visitEnd();
		cw.visitField(ACC_STATIC, "R", "I", null, new Integer(0)).visitEnd();
		cw.visitField(ACC_STATIC, "A", "I", null, new Integer(0)).visitEnd();
		cw.visitField(ACC_STATIC, "DEF_X", "I", null, new Integer(256)).visitEnd();
		cw.visitField(ACC_STATIC, "DEF_Y", "I", null, new Integer(256)).visitEnd();
		cw.visitField(ACC_STATIC, "Z", "I", null, new Integer(16777215)).visitEnd();
		
		// if GRADE, generates code to add string to log
		//CodeGenUtils.genLog(GRADE, mv, "entering main");

		// visit decs and statements to add field to class
		//  and instructions to main method, respectivley
		ArrayList<ASTNode> decsAndStatements = program.decsAndStatements;
		for (ASTNode node : decsAndStatements) {
			node.visit(this, arg);
		}

		//generates code to add string to log
		//CodeGenUtils.genLog(GRADE, mv, "leaving main");
		
		//adds the required (by the JVM) return statement to main
		mv.visitInsn(RETURN);
		
		//adds label at end of code
		Label mainEnd = new Label();
		mv.visitLabel(mainEnd);
		
		//handles parameters and local variables of main. Right now, only args
		mv.visitLocalVariable("args", "[Ljava/lang/String;", null, mainStart, mainEnd, 0);

		//Sets max stack size and number of local vars.
		//Because we use ClassWriter.COMPUTE_FRAMES as a parameter in the constructor,
		//asm will calculate this itself and the parameters are ignored.
		//If you have trouble with failures in this routine, it may be useful
		//to temporarily set the parameter in the ClassWriter constructor to 0.
		//The generated classfile will not be correct, but you will at least be
		//able to see what is in it.
		mv.visitMaxs(0, 0);
		
		//terminate construction of main method
		mv.visitEnd();
		
		//terminate class construction
		cw.visitEnd();

		//generate classfile as byte array and return
		return cw.toByteArray();
	}

	@Override
	public Object visitDeclaration_Variable(Declaration_Variable declaration_Variable, Object arg) throws Exception {
	
		Expression e = declaration_Variable.e;
		String fname = declaration_Variable.name;
		Object init = null;
		FieldVisitor f = null;
		if(declaration_Variable.declaration_type == Type.INTEGER)
		{
			init = new Integer(0);
			f = cw.visitField(ACC_STATIC, fname, "I", null, init);
			
		}
		if(declaration_Variable.declaration_type == Type.BOOLEAN)
		{
			init = new Boolean(false);
			f = cw.visitField(ACC_STATIC, fname, "Z", null, init);
			
		}
		f.visitEnd();
		if(e!=null)
		{
			e.visit(this, null);
			if(declaration_Variable.declaration_type == Type.INTEGER)
				mv.visitFieldInsn(PUTSTATIC, className, fname, "I");
			if(declaration_Variable.declaration_type == Type.BOOLEAN)
				mv.visitFieldInsn(PUTSTATIC, className, fname, "Z");
		}
		//throw new UnsupportedOperationException();
		return null;
	}

	@Override
	public Object visitExpression_Binary(Expression_Binary expression_Binary, Object arg) throws Exception {
	
		Expression e0 = expression_Binary.e0;
		Expression e1 = expression_Binary.e1;
		if(e0 != null)
			e0.visit(this, null);
		if(e1 != null)
			e1.visit(this, null);
		Label lab = new Label();
		Kind op = expression_Binary.op;
		if((e0.expression_type == TypeUtils.Type.INTEGER && e1.expression_type == TypeUtils.Type.INTEGER)|| (e0.expression_type == TypeUtils.Type.BOOLEAN && e1.expression_type == TypeUtils.Type.BOOLEAN))
		{
			if(op == Kind.OP_PLUS)
				mv.visitInsn(IADD);
			if(op == Kind.OP_MINUS)
				mv.visitInsn(ISUB);
			if(op == Kind.OP_TIMES)
				mv.visitInsn(IMUL);
			if(op == Kind.OP_DIV)
				mv.visitInsn(IDIV);
			if(op == Kind.OP_MOD)
				mv.visitInsn(IREM);
			if(op == Kind.OP_AND)
				mv.visitInsn(IAND);
			if(op == Kind.OP_OR)
				mv.visitInsn(IOR);
			if(op == Kind.OP_NEQ)
			{
				mv.visitJumpInsn(IF_ICMPNE, lab);
				mv.visitLdcInsn(false);
			}
			if(op == Kind.OP_EQ)
			{
				mv.visitJumpInsn(IF_ICMPEQ, lab);
				mv.visitLdcInsn(false);
			}
			if(op == Kind.OP_LE)
			{
				mv.visitJumpInsn(IF_ICMPLE, lab);
				mv.visitLdcInsn(false);
			}
			if(op == Kind.OP_GE)
			{
				mv.visitJumpInsn(IF_ICMPGE, lab);
				mv.visitLdcInsn(false);
			}
			if(op == Kind.OP_LT)
			{
				mv.visitJumpInsn(IF_ICMPLT, lab);
				mv.visitLdcInsn(false);
			}
			if(op == Kind.OP_GT)
			{
				mv.visitJumpInsn(IF_ICMPGT, lab);
				mv.visitLdcInsn(false);
			}
			
		}
		Label end = new Label();
		mv.visitJumpInsn(GOTO, end);
		mv.visitLabel(lab);
		mv.visitLdcInsn(true);
		mv.visitLabel(end);
		//throw new UnsupportedOperationException();
		//CodeGenUtils.genLogTOS(GRADE, mv, expression_Binary.expression_type);
		return null;
	}

	@Override
	public Object visitExpression_Unary(Expression_Unary expression_Unary, Object arg) throws Exception {
	
		//throw new UnsupportedOperationException();
		Expression e = expression_Unary.e;
		Kind kind = expression_Unary.op;
		if(e!=null)
			e.visit(this, null);
		if(kind == Kind.OP_MINUS)
			mv.visitInsn(INEG);
		else if (kind == Kind.OP_EXCL)
		{
			if(e.expression_type == TypeUtils.Type.INTEGER)
			{
				Integer max = INTEGER.MAX_VALUE;
				mv.visitLdcInsn(max);
				mv.visitInsn(IXOR);
			}
			else if(e.expression_type == TypeUtils.Type.BOOLEAN)
			{
				mv.visitInsn(ICONST_1);
				mv.visitInsn(IXOR);
			}
		}
		//CodeGenUtils.genLogTOS(GRADE, mv, expression_Unary.expression_type);
		return null;
	}

	// generate code to leave the two values on the stack
	@Override
	public Object visitIndex(Index index, Object arg) throws Exception {
		
		Expression e0 = index.e0;
		Expression e1 = index.e1;
		
		e0.visit(this, null);
		e1.visit(this, null);
		if(index.isCartesian)
			return null;
		else
		{
			mv.visitInsn(DUP2);
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "cart_x", RuntimeFunctions.cart_xSig, false);
			mv.visitInsn(DUP_X2);
			mv.visitInsn(POP);
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "cart_y", RuntimeFunctions.cart_ySig, false);
			
			
		}
		return null;
	}

	@Override
	public Object visitExpression_PixelSelector(Expression_PixelSelector expression_PixelSelector, Object arg)
			throws Exception {
		
		mv.visitFieldInsn(GETSTATIC, className, expression_PixelSelector.name, ImageSupport.ImageDesc);
		Index i = expression_PixelSelector.index;
		i.visit(this, null);
		mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "getPixel", ImageSupport.getPixelSig, false);
		return null;
	}

	@Override
	public Object visitExpression_Conditional(Expression_Conditional expression_Conditional, Object arg)
			throws Exception {
		
		Expression e0 = expression_Conditional.trueExpression;
		Expression e1 = expression_Conditional.condition;
		Expression e2 = expression_Conditional.falseExpression;
		if(e1 != null)
			e1.visit(this, null);
		Label lab = new Label();
		mv.visitJumpInsn(IFEQ, lab);
		e0.visit(this, null);
		Label end = new Label();
		mv.visitJumpInsn(GOTO, end);
		mv.visitLabel(lab);
		if(e2 != null)
			e2.visit(this, null);
		mv.visitLabel(end);
		return null;
//		CodeGenUtils.genLogTOS(GRADE, mv, expression_Conditional.trueExpression.getType());
//		return null;
	}


	@Override
	public Object visitDeclaration_Image(Declaration_Image declaration_Image, Object arg) throws Exception {
	
		FieldVisitor f = null;
		f = cw.visitField(ACC_STATIC, declaration_Image.name, ImageSupport.ImageDesc, null, null);
		f.visitEnd();
		Source s = declaration_Image.source;
		if(s!= null)
		{
			s.visit(this, null);
			
			Expression e0 = declaration_Image.xSize;
			Expression e1 = declaration_Image.ySize;
			
			if(e0 != null && e1 != null)
			{
				e0.visit(this, null);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
				e1.visit(this, null);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
			}
			else
			{
				mv.visitInsn(ACONST_NULL);
				mv.visitInsn(ACONST_NULL);
			}
			mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "readImage", ImageSupport.readImageSig, false);
		}
		else
		{
			Expression e0 = declaration_Image.xSize;
			Expression e1 = declaration_Image.ySize;
			if(e0 != null && e1 != null)
			{
				e0.visit(this, null);
				e1.visit(this, null);
				
			}
			else
			{
				mv.visitLdcInsn(256);
				mv.visitLdcInsn(256);
				
			}
			mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "makeImage", ImageSupport.makeImageSig, false);
		}
		mv.visitFieldInsn(PUTSTATIC, className, declaration_Image.name, ImageSupport.ImageDesc);	
		return null;
		//throw new UnsupportedOperationException();
	}
	
  
	@Override
	public Object visitSource_StringLiteral(Source_StringLiteral source_StringLiteral, Object arg) throws Exception {
		
		mv.visitLdcInsn(new String(source_StringLiteral.fileOrUrl));
		//CodeGenUtils.genLogTOS(GRADE, mv, Type.);
		return null;
		//throw new UnsupportedOperationException();
	}

	

	@Override
	public Object visitSource_CommandLineParam(Source_CommandLineParam source_CommandLineParam, Object arg)
			throws Exception {
		
		Expression e = source_CommandLineParam.paramNum;
		mv.visitVarInsn(ALOAD, 0);
		if(e != null)
			e.visit(this, null);
		mv.visitInsn(AALOAD);
		return null;
		
	}

	@Override
	public Object visitSource_Ident(Source_Ident source_Ident, Object arg) throws Exception {
	

		mv.visitFieldInsn(GETSTATIC, className, source_Ident.name, ImageSupport.StringDesc);
		//CodeGenUtils.genLogTOS(GRADE, mv, Type.BOOLEAN);
		return null;
		//throw new UnsupportedOperationException();
	}


	@Override
	public Object visitDeclaration_SourceSink(Declaration_SourceSink declaration_SourceSink, Object arg)
			throws Exception {
		
		FieldVisitor f = null;
		f = cw.visitField(ACC_STATIC, declaration_SourceSink.name, ImageSupport.StringDesc, null, null);
		f.visitEnd();
		Source s = declaration_SourceSink.source;
		if(s!= null)
		{
			s.visit(this, null);
		}
		mv.visitFieldInsn(PUTSTATIC, className, declaration_SourceSink.name, ImageSupport.StringDesc);	
		return null;
		//throw new UnsupportedOperationException();
	}
	


	@Override
	public Object visitExpression_IntLit(Expression_IntLit expression_IntLit, Object arg) throws Exception {
		
		mv.visitLdcInsn(new Integer(expression_IntLit.value));
		//throw new UnsupportedOperationException();
	//CodeGenUtils.genLogTOS(GRADE, mv, Type.INTEGER);
	return null;
	}

	@Override
	public Object visitExpression_FunctionAppWithExprArg(
			Expression_FunctionAppWithExprArg expression_FunctionAppWithExprArg, Object arg) throws Exception {
		
		Expression e = expression_FunctionAppWithExprArg.arg;
		
			Kind k = expression_FunctionAppWithExprArg.function;
			e.visit(this, null);
			if(k == Kind.KW_abs)
				mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "abs", RuntimeFunctions.absSig, false);
			else if (k == Kind.KW_log)
				mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "log", RuntimeFunctions.logSig, false);
		
		
		return null;
	}

	@Override
	public Object visitExpression_FunctionAppWithIndexArg(
			Expression_FunctionAppWithIndexArg expression_FunctionAppWithIndexArg, Object arg) throws Exception {
		
		Expression e0 = expression_FunctionAppWithIndexArg.arg.e0;
		Expression e1 = expression_FunctionAppWithIndexArg.arg.e1;
		e0.visit(this, null);
		e1.visit(this, null);
		Kind k = expression_FunctionAppWithIndexArg.function;
		if (k == Kind.KW_cart_x)
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "cart_x", RuntimeFunctions.cart_xSig, false);
		else if (k == Kind.KW_cart_y)
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "cart_y", RuntimeFunctions.cart_ySig, false);
		else if (k == Kind.KW_polar_a)
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "polar_a", RuntimeFunctions.polar_aSig, false);
		else if (k == Kind.KW_polar_r)
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "polar_r", RuntimeFunctions.polar_rSig, false);
			
		return null;
	}

	@Override
	public Object visitExpression_PredefinedName(Expression_PredefinedName expression_PredefinedName, Object arg)
			throws Exception {
		// TODO HW6
		 if (expression_PredefinedName.kind == Kind.KW_x)
			 mv.visitFieldInsn(GETSTATIC, className, "x", "I");

		 else if (expression_PredefinedName.kind == Kind.KW_y)
			 mv.visitFieldInsn(GETSTATIC, className, "y", "I");
		 
		 else if (expression_PredefinedName.kind == Kind.KW_r)
		 {
			 mv.visitFieldInsn(GETSTATIC, className, "x", "I");
			 mv.visitFieldInsn(GETSTATIC, className, "y", "I");
			 mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "polar_r", RuntimeFunctions.polar_rSig, false);
		 }
		 else if (expression_PredefinedName.kind == Kind.KW_a)
		 {
			 mv.visitFieldInsn(GETSTATIC, className, "x", "I");
			 mv.visitFieldInsn(GETSTATIC, className, "y", "I");
			 mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "polar_a", RuntimeFunctions.polar_aSig, false);
		 }
			 
		 else if (expression_PredefinedName.kind == Kind.KW_X)
		 {
			 //mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "getX", ImageSupport.getXSig, false);
			 mv.visitFieldInsn(GETSTATIC, className, "X", "I");
		 }
		 
		 else if (expression_PredefinedName.kind == Kind.KW_Y)
		 {
			 //mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "getY", ImageSupport.getYSig, false);
			 mv.visitFieldInsn(GETSTATIC, className, "Y", "I");
		 }
		
		 else if (expression_PredefinedName.kind == Kind.KW_Z)
			 mv.visitLdcInsn(16777215);
		
		 else if (expression_PredefinedName.kind == Kind.KW_R)
		 {
			 //mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "getX", ImageSupport.getXSig, false);
				//mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "getY", ImageSupport.getYSig, false);
			 mv.visitFieldInsn(GETSTATIC, className, "X", "I");
			 mv.visitFieldInsn(GETSTATIC, className, "Y", "I");
				mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "polar_r", RuntimeFunctions.polar_rSig, false);
		 }
		 else if (expression_PredefinedName.kind == Kind.KW_A)
		 {
			 mv.visitInsn(ICONST_0);
				//mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "getY", ImageSupport.getYSig, false);
			 	mv.visitFieldInsn(GETSTATIC, className, "y", "I");
			 	 
				mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "polar_a", RuntimeFunctions.polar_aSig, false);
		 }
		 else if (expression_PredefinedName.kind == Kind.KW_DEF_X)
			 //mv.visitLdcInsn(256);
			 mv.visitFieldInsn(GETSTATIC, className, "DEF_X", "I");
		 else if (expression_PredefinedName.kind == Kind.KW_DEF_Y)
			 //mv.visitLdcInsn(256);
		 mv.visitFieldInsn(GETSTATIC, className, "DEF_Y", "I");
		 else throw new UnsupportedOperationException();
		
		
	
	return null;
	}

	/** For Integers and booleans, the only "sink"is the screen, so generate code to print to console.
	 * For Images, load the Image onto the stack and visit the Sink which will generate the code to handle the image.
	 */
	@Override
	public Object visitStatement_Out(Statement_Out statement_Out, Object arg) throws Exception {
		
		Sink s = statement_Out.sink;
		if(statement_Out.dec.declaration_type == TypeUtils.Type.INTEGER)
		{
			mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
			mv.visitFieldInsn(GETSTATIC, className, statement_Out.name, "I");
			// CodeGenUtils.genPrint(DEVEL, mv, "state_out***************************");
			CodeGenUtils.genLogTOS(GRADE, mv, statement_Out.dec.declaration_type);
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V", false);
		}
		else if(statement_Out.dec.declaration_type == TypeUtils.Type.BOOLEAN)
		{
			mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
			mv.visitFieldInsn(GETSTATIC, className, statement_Out.name, "Z");
			// CodeGenUtils.genPrint(DEVEL, mv, "state_out bool***************************");
			CodeGenUtils.genLogTOS(GRADE, mv, statement_Out.dec.declaration_type);
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Z)V", false);
		}
		else if(statement_Out.dec.declaration_type == TypeUtils.Type.IMAGE)
		{
			mv.visitFieldInsn(GETSTATIC, className, statement_Out.name, ImageSupport.ImageDesc);
			CodeGenUtils.genLogTOS(GRADE, mv, statement_Out.dec.declaration_type);
			s.visit(this, null);
			
		}
		return null;
	}

	/**
	 * Visit source to load rhs, which will be a String, onto the stack
	 * 
	 *  In HW5, you only need to handle INTEGER and BOOLEAN
	 *  Use java.lang.Integer.parseInt or java.lang.Boolean.parseBoolean 
	 *  to convert String to actual type. 
	 *  
	 *  
	 */
	@Override
	public Object visitStatement_In(Statement_In statement_In, Object arg) throws Exception {
	
		Source s=statement_In.source;	
		String name=statement_In.name;
		
		
		if(s!=null)
			s.visit(this, null);

		if(statement_In.dec.declaration_type==Type.INTEGER)
			{
				
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "parseInt", "(Ljava/lang/String;)I", false);
				mv.visitFieldInsn(PUTSTATIC, className, name,"I");												
			}
		
		else if(statement_In.dec.declaration_type==Type.BOOLEAN)
			{
			
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "parseBoolean", "(Ljava/lang/String;)Z", false);
				//mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Boolean", "<init>", "(Ljava/lang/String;)V", false);
				mv.visitFieldInsn(PUTSTATIC, className, name,"Z");												
				
			}
		else if (statement_In.dec.declaration_type==Type.IMAGE)
		{	
			Declaration_Image dec = (Declaration_Image) statement_In.dec;
			Expression e0 = dec.xSize;
			Expression e1 = dec.ySize;
			
			if(e0 != null && e1 != null)
			{
				mv.visitFieldInsn(GETSTATIC, className, statement_In.name, ImageSupport.ImageDesc);
				mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "getX", ImageSupport.getXSig, false);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
				mv.visitFieldInsn(GETSTATIC, className, statement_In.name, ImageSupport.ImageDesc);
				mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "getY", ImageSupport.getYSig, false);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
			}
			else
			{
				mv.visitInsn(ACONST_NULL);
				mv.visitInsn(ACONST_NULL);
			}
			mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "readImage", ImageSupport.readImageSig, false);
			mv.visitFieldInsn(PUTSTATIC, className, name, ImageSupport.ImageDesc);
		}

		return null;
		//throw new UnsupportedOperationException();
	}

	
	/**
	 * In HW5, only handle INTEGER and BOOLEAN types.
	 */
	@Override
	public Object visitStatement_Assign(Statement_Assign statement_Assign, Object arg) throws Exception {
		
		Type t = statement_Assign.lhs.lhs_type;
		LHS lhs = statement_Assign.lhs;
		if(t == Type.INTEGER || t==Type.BOOLEAN){
			Expression e = statement_Assign.e;
			if (e!=null)
				e.visit(this, null);
			
			if(lhs!=null)
			{
				lhs.visit(this, null);
			}
		}
		else if(t == Type.IMAGE){
			mv.visitFieldInsn(GETSTATIC, className, lhs.name, ImageSupport.ImageDesc);
			mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "getY", ImageSupport.getYSig,false);
			mv.visitFieldInsn(PUTSTATIC, className, "Y","I");
			mv.visitFieldInsn(GETSTATIC, className, lhs.name, ImageSupport.ImageDesc);
			mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "getX", ImageSupport.getXSig,false);
			mv.visitFieldInsn(PUTSTATIC, className, "X","I");	
			mv.visitInsn(ICONST_0);
			mv.visitInsn(DUP);
			Label start_label = new Label();
			mv.visitLabel(start_label);
			mv.visitFieldInsn(PUTSTATIC, className,"y", "I");
			mv.visitFieldInsn(GETSTATIC, className, "Y", "I");
			Label end_label = new Label();
			mv.visitJumpInsn(IF_ICMPGE, end_label);
			mv.visitLdcInsn(0);
			mv.visitInsn(DUP);
			Label x_label = new Label();
			mv.visitLabel(x_label);
			mv.visitFieldInsn(PUTSTATIC, className,"x", "I");
			mv.visitFieldInsn(GETSTATIC, className, "X","I");		
			Label y_label = new Label();
			mv.visitJumpInsn(IF_ICMPGE, y_label);
			statement_Assign.e.visit(this, arg);
			statement_Assign.lhs.visit(this, arg);
			mv.visitFieldInsn(GETSTATIC, className,"x", "I");
			mv.visitLdcInsn(1);
			mv.visitInsn(IADD);
			mv.visitInsn(DUP);
			mv.visitJumpInsn(GOTO, x_label);
			mv.visitLabel(y_label);
			mv.visitFieldInsn(GETSTATIC, className,"y", "I");
			mv.visitInsn(ICONST_1);
			mv.visitInsn(IADD);
			mv.visitInsn(DUP);
			mv.visitJumpInsn(GOTO, start_label);
			mv.visitLabel(end_label);
			}
		return null;
	}

	/**
	 * In HW5, only handle INTEGER and BOOLEAN types.
	 */
	@Override
	public Object visitLHS(LHS lhs, Object arg) throws Exception {
		
		Type t = lhs.lhs_type;
		if(t == Type.INTEGER)
			mv.visitFieldInsn(PUTSTATIC, className, lhs.name, "I");
		if(t == Type.BOOLEAN)
			mv.visitFieldInsn(PUTSTATIC, className, lhs.name, "Z");
		if(t == Type.IMAGE)
		{
			mv.visitFieldInsn(GETSTATIC, className, lhs.name, ImageSupport.ImageDesc);
			mv.visitFieldInsn(GETSTATIC, className, "x", "I");
			mv.visitFieldInsn(GETSTATIC, className, "y", "I");
			mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "setPixel", ImageSupport.setPixelSig, false);
			
		}
		return null;
	}
	

	@Override
	public Object visitSink_SCREEN(Sink_SCREEN sink_SCREEN, Object arg) throws Exception {
		
		mv.visitMethodInsn(INVOKESTATIC, ImageFrame.className, "makeFrame", ImageSupport.makeFrameSig, false);
		mv.visitInsn(POP);
		return null;
		
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitSink_Ident(Sink_Ident sink_Ident, Object arg) throws Exception {
		
		mv.visitFieldInsn(GETSTATIC, className, sink_Ident.name, ImageSupport.StringDesc);
		mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "write", ImageSupport.writeSig, false);
		return null;
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpression_BooleanLit(Expression_BooleanLit expression_BooleanLit, Object arg) throws Exception {
	
		mv.visitLdcInsn(new Boolean(expression_BooleanLit.value));
		//CodeGenUtils.genLogTOS(GRADE, mv, Type.BOOLEAN);
		return null;
		//throw new UnsupportedOperationException();
		
	}

	@Override
	public Object visitExpression_Ident(Expression_Ident expression_Ident,
			Object arg) throws Exception {
		
		if(expression_Ident.expression_type == TypeUtils.Type.INTEGER)
			mv.visitFieldInsn(GETSTATIC, className, expression_Ident.name, "I");
		else if (expression_Ident.expression_type == TypeUtils.Type.BOOLEAN)
			mv.visitFieldInsn(GETSTATIC, className, expression_Ident.name, "Z");
		
	//CodeGenUtils.genLogTOS(GRADE, mv, expression_Ident.expression_type);
	return null;
	}

}
