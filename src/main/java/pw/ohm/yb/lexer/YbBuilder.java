package pw.ohm.yb.lexer;

import java.io.IOException;
import java.io.UncheckedIOException;

public class YbBuilder {

	private final YbLexer ybLexer;
	private YbTokens ybTokens;
	private String error;

	public YbBuilder(YbLexer ybLexer) {

		this.ybLexer = ybLexer;
		this.advanceLexer();
	}

	public void advanceLexer() {
		try {
			ybTokens = ybLexer.advanceImpl();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public YbTokens getTokenType() {
		return ybTokens;
	}

	public String getTokenText() {
		return ybLexer.yytext();
	}

	public void error(String error) {
		this.error = error;
		System.err.println(error);
	}

	public void remapCurrentToken(YbTokens newElementType) {
		this.ybTokens = newElementType;
	}

	public Marker mark() {
		try {
			YbTokens ybTokens = ybLexer.advanceImpl();
			while (ybTokens == YbTokens.WHITE_SPACE && ybTokens != null) {
				ybTokens = ybLexer.advanceImpl();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new Marker() {
			@Override
			public void done(YbTokens root) {

			}

			@Override
			public void drop() {

			}

			@Override
			public void error(String expected_type) {

			}
		};
	}

	public boolean eof() {
		return ybTokens == null;
	}

	public interface Marker {
		void done(YbTokens root);

		void drop();

		void error(String expected_type);
	}
}
