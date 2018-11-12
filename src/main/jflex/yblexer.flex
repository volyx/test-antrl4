package pw.ohm.yb.lexer;

// import com.intellij.lexer.LexerBase;
// import com.intellij.psi.tree.IElementType;
// import pw.ohm.yb.YbTokens;

%%

%public
%class YbLexer
// %extends LexerBase
%unicode
%function advanceImpl
// %type IElementType
%type YbTokens
%eof{  return;
%eof}

WHITE_SPACE=[ \n\r\t\f]+
SINGLE_LINE_COMMENT="/""/"[^\r\n]*

STRING_LITERAL=\"([^\\\"\r\n]|{ESCAPE_SEQUENCE})*(\"|\\)?
ESCAPE_SEQUENCE=\\[^\r\n]

IDENTIFIER_PART=[:jletter:] | [:jletterdigit:] | "-" | "."
IDENTIFIER={IDENTIFIER_PART}*

%%


<YYINITIAL>
{

	"#"                       { return YbTokens.SHARP; }

	"@"                       { return YbTokens.AT; }

	"="                       { return YbTokens.EQ; }

	"{"                       { return YbTokens.LBRACE; }

	"}"                       { return YbTokens.RBRACE; }

	"("                       { return YbTokens.LPAR; }

	")"                       { return YbTokens.RPAR; }

	"["                       { return YbTokens.LBRACKET; }

	"]"                       { return YbTokens.RBRACKET; }

	","                       { return YbTokens.COMMA; }

	"."                       { return YbTokens.DOT; }

	":"                       { return YbTokens.COLON; }

	{SINGLE_LINE_COMMENT}     { return YbTokens.COMMENT; }

	{STRING_LITERAL}          { return YbTokens.STRING; }

	{IDENTIFIER}              { return YbTokens.IDENTIFIER; }

	{WHITE_SPACE}             { return YbTokens.WHITE_SPACE; }

	.                         { return YbTokens.BAD_CHARACTER; }
}
