

import java.util.List;


public class CompilationEngine
 {

	private List<Lexical> tokens;

	private List<String> output;

	private int count = -1;

	private Lexical currentToken;

	public CompilationEngine(List<Lexical> tokens, List<String> output)
	{
		this.tokens = tokens;
		this.output = output;
	}

	// 'class' className '{' classVarDec* subroutineDec* '}'
	public void compileClass() {
		output.add("<class>");

		// 'class'
		goNext();
		eat(currentToken);

		// className
		goNext();
		eat(currentToken);

		// '{'
		goNext();
		eat(currentToken);

		goNext();
		while (isClassVarDec(currentToken) || isClassSubroutineDec(currentToken)) {
			if (isClassVarDec(currentToken)) 
			{
				goBack();
				compileClassVarDec();

			} else if (isClassSubroutineDec(currentToken)) 
			{
				goBack();
				compileSubroutineDec();
			}

			goNext();
		}

		// '}'
		eat(currentToken);

		output.add("</class>");
	}

	private boolean isClassVarDec(Lexical token) {
		return token.getLecical().equals("static") || token.getLecical().equals("field");
	}

	private boolean isClassSubroutineDec(Lexical token) {
		return token.getLecical().equals("constructor") || token.getLecical().equals("function") ||
				token.getLecical().equals("method");
	}

	// ( 'static' | 'field' ) type varName ( ',' varName)* ';'
	private void compileClassVarDec() 
	{
		output.add("<classVarDec>");

		// 'static' | 'field'
		goNext();
		eat(currentToken);

		// type
		goNext();
		eat(currentToken);

		// varName
		goNext();
		eat(currentToken);

		goNext();
		while (currentToken.getLecical().equals(",")) 
		{
			// ','
			eat(currentToken);

			// varName
			goNext();
			eat(currentToken);

			goNext();
		}

		// ';'
		eat(currentToken);

		output.add("</classVarDec>");
	}

	// ( 'constructor' | 'function' | 'method' ) ( 'void' | type ) subroutineName '(' parameterList ')' subroutineBody
	private void compileSubroutineDec() {
		output.add("<subroutineDec>");

		// 'constructor' | 'function' | 'method'
		goNext();
		eat(currentToken);

		// 'void' | type
		goNext();
		eat(currentToken);

		// subroutineName
		goNext();
		eat(currentToken);

		// '('
		goNext();
		eat(currentToken);

		// parameterList
		compileParameterList();

		// ')'
		goNext();
		eat(currentToken);

		// subroutineBody
		compileSubroutineBody();

		output.add("</subroutineDec>");
	}

	// ((type varName) ( ',' type varName)*)?
	private void compileParameterList() 
	{
		output.add("<parameterList>");

		goNext();
		if (!currentToken.getLecical().equals(")")) 
		{
			// type
			eat(currentToken);

			// varName
			goNext();
			eat(currentToken);

			goNext();
			while (currentToken.getLecical().equals(",")) {
				// ','
				eat(currentToken);

				// type
				goNext();
				eat(currentToken);

				// varName
				goNext();
				eat(currentToken);

				goNext();
			}
		}

		goBack();

		output.add("</parameterList>");
	}

	// '{' varDec* statements '}'
	private void compileSubroutineBody() {
		output.add("<subroutineBody>");

		// '{'
		goNext();
		eat(currentToken);

		// varDec*
		goNext();
		while (isVarDec(currentToken)) {
			goBack();
			compileVarDec();
			goNext();
		}

		// statements
		goBack();
		compileStatements();

		// '}'
		goNext();
		eat(currentToken);

		output.add("</subroutineBody>");
	}

	private boolean isVarDec(Lexical token) {
		return token.getLecical().equals("var");
	}

	private boolean isStatement(Lexical token) {
		return token.getLecical().equals("let") ||
				token.getLecical().equals("if") ||
				token.getLecical().equals("while") ||
				token.getLecical().equals("do") ||
				token.getLecical().equals("return");
	}

	// 'var' type varName ( ',' varName)* ';'
	private void compileVarDec() {
		output.add("<varDec>");

		// 'var'
		goNext();
		eat(currentToken);

		// type
		goNext();
		eat(currentToken);

		// varName
		goNext();
		eat(currentToken);

		goNext();
		while (currentToken.getLecical().equals(",")) {
			// ','
			eat(currentToken);

			// varName
			goNext();
			eat(currentToken);

			goNext();
		}

		// ';'
		eat(currentToken);

		output.add("</varDec>");
	}

	// statement*
	private void compileStatements() {
		output.add("<statements>");

		goNext();
		while (isStatement(currentToken)) {
			goBack();
			compileStatement();
			goNext();
		}

		goBack();

		output.add("</statements>");
	}

	private void compileStatement() {
		goNext();

		if (currentToken.getLecical().equals("let")) {
			goBack();
			compileLetStatement();

		} else if (currentToken.getLecical().equals("if")) {
			goBack();
			compileIfStatement();

		} else if (currentToken.getLecical().equals("while")) {
			goBack();
			compileWhileStatement();

		} else if (currentToken.getLecical().equals("do")) {
			goBack();
			compileDoStatement();

		} else if (currentToken.getLecical().equals("return")) {
			goBack();
			compileReturnStatement();
		}
	}

	// 'let' varName ('[' expression ']')? '=' expression ';'
	private void compileLetStatement() {
		output.add("<letStatement>");

		// 'let'
		goNext();
		eat(currentToken);

		// varName
		goNext();
		eat(currentToken);

		goNext();
		if (currentToken.getLecical().equals("[")) {
			// '['
			eat(currentToken);

			// expression
			compileExpression();

			// ']'
			goNext();
			eat(currentToken);

			goNext();
		}

		// '='
		eat(currentToken);

		// expression
		compileExpression();

		// ';'
		goNext();
		eat(currentToken);

		output.add("</letStatement>");
	}

	// 'if' '(' expression ')' '{' statements '}' ( 'else' '{' statements '}' )?
	private void compileIfStatement() {
		output.add("<ifStatement>");

		// 'if'
		goNext();
		eat(currentToken);

		// '('
		goNext();
		eat(currentToken);

		// expression
		compileExpression();

		// ')'
		goNext();
		eat(currentToken);

		// '{'
		goNext();
		eat(currentToken);

		// statements
		compileStatements();

		// '}'
		goNext();
		eat(currentToken);

		goNext();
		if (currentToken.getLecical().equals("else")) {
			// 'else'
			eat(currentToken);

			// '{'
			goNext();
			eat(currentToken);

			// statements
			compileStatements();

			// '}'
			goNext();
			eat(currentToken);

		} else {
			goBack();
		}

		output.add("</ifStatement>");
	}

	// 'while' '(' expression ')' '{' statements '}'
	private void compileWhileStatement() {
		output.add("<whileStatement>");

		// 'while'
		goNext();
		eat(currentToken);

		// '('
		goNext();
		eat(currentToken);

		// expression
		compileExpression();

		// ')'
		goNext();
		eat(currentToken);

		// '{'
		goNext();
		eat(currentToken);

		// statements
		compileStatements();

		// '}'
		goNext();
		eat(currentToken);

		output.add("</whileStatement>");
	}

	// 'do' subroutineCall ';'
	private void compileDoStatement() {
		output.add("<doStatement>");

		// 'do'
		goNext();
		eat(currentToken);

		// subroutineCall
		compileSubroutineCall();

		// ';'
		goNext();
		eat(currentToken);

		output.add("</doStatement>");
	}

	// 'return' expression? ';'
	private void compileReturnStatement() {
		output.add("<returnStatement>");

		// 'return'
		goNext();
		eat(currentToken);

		goNext();
		if (!currentToken.getLecical().equals(";")) {
			// expression
			goBack();
			compileExpression();

			goNext();
		}

		// ';'
		eat(currentToken);

		output.add("</returnStatement>");
	}

	// term (op term)*
	private void compileExpression() {
		output.add("<expression>");

		// term
		compileTerm();

		goNext();
		while (isOp(currentToken)) {
			// op
			eat(currentToken);

			// term
			compileTerm();
			goNext();
		}

		goBack();

		output.add("</expression>");
	}

	// integerConstant | stringConstant | keywordConstant | varName | varName '[' expression ']' |
	// subroutineCall | '(' expression ')' | unaryOp term
	private void compileTerm() {
		output.add("<term>");

		goNext();
		if (currentToken.getType() == Lexical.Type.integerConstant) {
			// Case: integerConstant
			eat(currentToken);

		} else if (currentToken.getType() == Lexical.Type.stringConstant) {
			// Case: stringConstant
			eat(currentToken);

		} else if (isKeywordConstant(currentToken)) {
			// Case: keywordConstant
			eat(currentToken);

		} else if (isUnaryOp(currentToken)){
			// Case: unaryOp term

			// unaryOp
			eat(currentToken);

			// term
			compileTerm();

		} else if (currentToken.getLecical().equals("(")) {
			// Case: '(' expression ')'

			// '('
			eat(currentToken);

			// expression
			compileExpression();

			// ')'
			goNext();
			eat(currentToken);

		} else {
			goNext();
			if (currentToken.getLecical().equals("[")) {
				// Case: varName '[' expression ']'
				goBack();

				// varName
				eat(currentToken);

				// '['
				goNext();
				eat(currentToken);

				// expression
				compileExpression();

				// ']'
				goNext();
				eat(currentToken);

			} else if (currentToken.getLecical().equals("(") || currentToken.getLecical().equals(".")) {
				// Case: subroutineCall
				goBack(); goBack(); // Go to begin to compile subroutineCall
				compileSubroutineCall();

			} else {
				// Case: varName
				goBack();

				// varName
				eat(currentToken);
			}
		}

		output.add("</term>");
	}

	// subroutineName '(' expressionList ')' | (className | varName) '.' subroutineName '(' expressionList ')'
	private void compileSubroutineCall() {
		goNext();

		// subroutineName | (className | varName)
		eat(currentToken);

		goNext();
		if (currentToken.getLecical().equals("(")) { // Case: subroutineName '(' expressionList ')'
			// '('
			eat(currentToken);

			// expressionList
			compileExpressionList();

			// ')'
			goNext();
			eat(currentToken);

		} else if (currentToken.getLecical().equals(".")) { // Case: (className | varName) '.' subroutineName '(' expressionList ')'
			// '.'
			eat(currentToken);

			// subroutineName
			goNext();
			eat(currentToken);

			// '('
			goNext();
			eat(currentToken);

			// expressionList
			compileExpressionList();

			// ')'
			goNext();
			eat(currentToken);
		}
	}

	// (expression ( ',' expression)* )?
	private void compileExpressionList() {
		output.add("<expressionList>");

		goNext();
		if (!currentToken.getLecical().equals(")")) {
			goBack();

			// expression
			compileExpression();

			goNext();
			while (currentToken.getLecical().equals(",")) {
				// ','
				eat(currentToken);

				// expression
				compileExpression();

				goNext();
			}

			goBack();

		} else {
			goBack();
		}

		output.add("</expressionList>");
	}

	private boolean isOp(Lexical token) {
		return token.getLecical().equals("+") ||
				token.getLecical().equals("-") ||
				token.getLecical().equals("*") ||
				token.getLecical().equals("/") ||
				token.getLecical().equals("&") ||
				token.getLecical().equals("|") ||
				token.getLecical().equals("<") ||
				token.getLecical().equals(">") ||
				token.getLecical().equals("=");
	}

	private boolean isUnaryOp(Lexical token) {
		return token.getLecical().equals("-") || token.getLecical().equals("~");
	}

	private boolean isKeywordConstant(Lexical token) {
		return token.getLecical().equals("true") ||
				token.getLecical().equals("false") ||
				token.getLecical().equals("null") ||
				token.getLecical().equals("this");
	}

	private void eat(Lexical token) {
		output.add(token.toString());
	}

	private void goNext() {
		count++;
		currentToken = tokens.get(count);
	}

	private void goBack() {
		count--;
		currentToken = tokens.get(count);
	}
}