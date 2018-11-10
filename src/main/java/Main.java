import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.*;

import java.io.IOException;

public class Main {

	public static void main(String[] args) throws IOException {
//		new Main().run();
		new Main().log();

		System.out.println("done");

	}

	private void log() throws IOException {
		final CharStream charStream = CharStreams.fromFileName("/Users/volyx/Projects/test-antrl4/src/main/resources/log.txt");
		final LogLexer logLexer = new LogLexer(charStream);
		final LogParser logParser = new LogParser(new CommonTokenStream(logLexer));
		final ParseTree tree = logParser.log();
		ParseTreeWalker walker = new ParseTreeWalker();
		ParseTreeListener listener = new LogListener() {
			@Override
			public void enterLog(LogParser.LogContext ctx) {
//				System.out.println("Main.enterLog " + ctx.getText());
			}

			@Override
			public void exitLog(LogParser.LogContext ctx) {
//				System.out.println("Main.exitLog " + ctx.getText());
			}

			@Override
			public void enterEntry(LogParser.EntryContext ctx) {
				System.out.println("Main.enterEntry " + ctx.getText());
			}

			@Override
			public void exitEntry(LogParser.EntryContext ctx) {
				System.out.println("Main.exitEntry " + ctx.getText());
			}

			@Override
			public void enterTimestamp(LogParser.TimestampContext ctx) {
				System.out.println("Main.enterTimestamp " + ctx.getText());
			}

			@Override
			public void exitTimestamp(LogParser.TimestampContext ctx) {
				System.out.println("Main.exitTimestamp " + ctx.getText());
			}

			@Override
			public void enterLevel(LogParser.LevelContext ctx) {
				System.out.println("Main.enterLevel " + ctx.getText());
			}

			@Override
			public void exitLevel(LogParser.LevelContext ctx) {
				System.out.println("Main.exitLevel " + ctx.getText());
			}

			@Override
			public void enterMessage(LogParser.MessageContext ctx) {
				System.out.println("Main.enterMessage " + ctx.getText());
			}

			@Override
			public void exitMessage(LogParser.MessageContext ctx) {
				System.out.println("Main.exitMessage " + ctx.getText());
			}

			@Override
			public void visitTerminal(TerminalNode node) {
				System.out.println("Main.visitTerminal " + node.getText());
			}

			@Override
			public void visitErrorNode(ErrorNode node) {
				System.out.println("Main.visitErrorNode " + node.getText());
			}

			@Override
			public void enterEveryRule(ParserRuleContext ctx) {
//				System.out.println("Main.enterEveryRule " + ctx.getText());
			}

			@Override
			public void exitEveryRule(ParserRuleContext ctx) {
//				System.out.println("Main.exitEveryRule " + ctx.getText());
			}
		};
		walker.walk(listener, tree);



	}

	private void run() throws IOException {
		final CharStream charStream = CharStreams.fromFileName("/Users/volyx/Projects/test-antrl4/src/main/resources/test.txt");
		HelloLexer lexer = new HelloLexer(charStream);
		CommonTokenStream commonTokenStream = new CommonTokenStream(lexer);
		HelloParser parser = new HelloParser(commonTokenStream);

		ParseTree tree = parser.r();

//		System.out.println(parser.toString());

		ParseTreeWalker walker = new ParseTreeWalker();

		final HelloListener listener = new HelloListener() {
			@Override
			public void visitTerminal(TerminalNode node) {
				System.out.println("visitTerminal " + node.getSymbol().getType());
				System.out.println("visitTerminal " + node.getText());
			}

			@Override
			public void visitErrorNode(ErrorNode node) {
				System.out.println("visitErrorNode");
			}

			@Override
			public void enterEveryRule(ParserRuleContext ctx) {
				System.out.println("enterEveryRule");
			}

			@Override
			public void exitEveryRule(ParserRuleContext ctx) {
				System.out.println("exitEveryRule");
			}

			@Override
			public void enterR(HelloParser.RContext ctx) {
				System.out.println("enterR");
			}

			@Override
			public void exitR(HelloParser.RContext ctx) {
				System.out.println("exitR");

			}
		};
		walker.walk(listener, tree);
	}
}
