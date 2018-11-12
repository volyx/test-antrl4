package pw.ohm.yb.lexer;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class YbParser {
	private static Map<String, YbTokens> ourSoftKeywords = new HashMap<String, YbTokens>();

	static {
		ourSoftKeywords.put("itemBuilder", YbTokens.ITEM_BUILDER_KEYWORD);
		ourSoftKeywords.put("queryBuilder", YbTokens.QUERY_BUILDER_KEYWORD);
		ourSoftKeywords.put("int", YbTokens.INT_KEYWORD);
		ourSoftKeywords.put("long", YbTokens.LONG_KEYWORD);
		ourSoftKeywords.put("boolInt", YbTokens.BOOL_INT_KEYWORD);
		ourSoftKeywords.put("map", YbTokens.MAP_KEYWORD);
		ourSoftKeywords.put("string", YbTokens.STRING_KEYWORD);
		ourSoftKeywords.put("double", YbTokens.DOUBLE_KEYWORD);
	}

	//	@Override
	public void parse(YbTokens root, YbBuilder builder) {
//		builder.setDebugMode(true);

		YbBuilder.Marker mark = builder.mark();
		while (!builder.eof()) {
			YbTokens start = getTokenOf(builder, YbTokens.BUILDER_TOKENS);

			if (start == YbTokens.ITEM_BUILDER_KEYWORD || start == YbTokens.QUERY_BUILDER_KEYWORD) {
				parseBuilder(builder);
			} else if (builder.getTokenType() == YbTokens.AT) {
				parseVariable(builder);
			} else {
				builder.error("Unexpected token. Current: " + builder.getTokenType().name());
				builder.advanceLexer();
			}
		}
		mark.done(root);

//		return builder.getTreeBuilt();
	}

	private static void parseVariable(YbBuilder builder) {
		YbBuilder.Marker mark = builder.mark();

		builder.advanceLexer(); //@

		if (builder.getTokenType() == YbTokens.IDENTIFIER) {
			builder.remapCurrentToken(YbTokens.VARIABLE_NAME);
			builder.advanceLexer();
		} else {
			builder.error("Expected name");
		}

		expect(builder, YbTokens.EQ, "'=' expected");

		if (builder.getTokenType() == YbTokens.IDENTIFIER) {
			boolean first = true;
			while (!builder.eof()) {
				if (first) {
					first = false;
				} else {
					expect(builder, YbTokens.DOT, "',' expected");
				}

				if (builder.getTokenType() == YbTokens.IDENTIFIER) {
					builder.advanceLexer();
				}

				if (builder.getTokenType() != YbTokens.DOT) {
					break;
				}
			}
		} else if (builder.getTokenType() == YbTokens.STRING) {
			builder.advanceLexer();
		} else {
			builder.error("Expected value");
		}
		mark.done(YbTokens.VARIABLE);
	}

	private static void parseBuilder(YbBuilder builder) {
		YbBuilder.Marker mark = builder.mark();

		builder.advanceLexer();

		expect(builder, YbTokens.IDENTIFIER, "Expected name");

		if (expect(builder, YbTokens.LBRACE, "'{' expected")) {
			while (!builder.eof()) {
				if (builder.getTokenType() == YbTokens.RBRACE) {
					break;
				}

				if (builder.getTokenType() == YbTokens.AT) {
					parseVariable(builder);
					continue;
				}

				if (!parseParameter(builder) && builder.getTokenType() != YbTokens.RBRACE) {
					break;
				}
			}

			expect(builder, YbTokens.RBRACE, "'}' expected");
		}
		mark.done(YbTokens.BUILDER);
	}

	private static boolean parseParameter(YbBuilder builder) {
		YbBuilder.Marker mark = builder.mark();

		boolean attributes = false;
		while (builder.getTokenType() == YbTokens.SHARP) {
			attributes = true;
			YbBuilder.Marker attribute = builder.mark();

			builder.advanceLexer();

			expect(builder, YbTokens.IDENTIFIER, "Expected attribute name");

			if (builder.getTokenType() == YbTokens.LPAR) {
				YbBuilder.Marker parameterList = builder.mark();

				builder.advanceLexer();

				boolean first = true;
				while (!builder.eof()) {
					if (builder.getTokenType() == YbTokens.RPAR) {
						break;
					}

					if (first) {
						first = false;
					} else {
						expect(builder, YbTokens.COMMA, "',' expected");
					}

					if (!parseExpression(builder) && builder.getTokenType() != YbTokens.RPAR && builder.getTokenType() != YbTokens.COMMA) {
						break;
					}
				}

				expect(builder, YbTokens.RPAR, "')' expected");

				parameterList.done(YbTokens.ATTRIBUTE_PARAMETER_LIST);
			}

			attribute.done(YbTokens.ATTRIBUTE);
		}

		YbTokens typeToken = getTokenOf(builder, EnumSet.of(YbTokens.INT_KEYWORD, YbTokens.DOUBLE_KEYWORD, YbTokens.LONG_KEYWORD, YbTokens.BOOL_INT_KEYWORD, YbTokens.MAP_KEYWORD, YbTokens.STRING_KEYWORD));
		if (typeToken != null || builder.getTokenType() == YbTokens.LBRACKET || builder.getTokenType() == YbTokens.STRING) {
			YbBuilder.Marker typeMarker = builder.mark();
			if (builder.getTokenType() == YbTokens.LBRACKET) {
				builder.advanceLexer();

				boolean first = true;
				while (!builder.eof()) {
					if (builder.getTokenType() == YbTokens.RBRACKET) {
						break;
					}

					if (first) {
						first = false;
					} else {
						expect(builder, YbTokens.COMMA, "',' expected");
					}

					if (builder.getTokenType() == YbTokens.IDENTIFIER) {
						builder.advanceLexer();
					} else if (builder.getTokenType() != YbTokens.RBRACKET && builder.getTokenType() != YbTokens.COMMA) {
						break;
					}
				}

				expect(builder, YbTokens.RBRACKET, "']' expected");
				typeMarker.done(YbTokens.ENUM_TYPE);
			} else if (builder.getTokenType() == YbTokens.STRING) {
				builder.advanceLexer();
				typeMarker.done(YbTokens.USER_TYPE);
			} else {
				builder.advanceLexer();
				typeMarker.done(YbTokens.PRIMITIVE_TYPE);
			}

			expect(builder, YbTokens.IDENTIFIER, "Expected name");

			mark.done(YbTokens.PARAMETER);
			return true;
		} else {
			if (attributes) {
				mark.error("Expected type");
				return true;
			} else {
				mark.drop();
				return false;
			}
		}
	}

	private static boolean parseExpression(YbBuilder builder) {
		YbTokens tokenType = builder.getTokenType();
		YbBuilder.Marker mark = builder.mark();
		if (tokenType == YbTokens.IDENTIFIER) {
			builder.advanceLexer();
			mark.done(YbTokens.REFERENCE);
			return true;
		} else if (tokenType == YbTokens.STRING) {
			builder.advanceLexer();
			mark.done(YbTokens.CONSTANT);
			return true;
		} else {
			mark.drop();
			return false;
		}
	}

	private static boolean expect(YbBuilder builder, YbTokens elementType, String message) {
		if (builder.getTokenType() == elementType) {
			builder.advanceLexer();
			return true;
		} else {

			if (message != null) {
				builder.error(message);
			}
			return false;
		}
	}

	public static YbTokens getTokenOf(YbBuilder builder, Set<YbTokens> tokenSet) {
		YbTokens tokenType = builder.getTokenType();
		if (tokenType == YbTokens.IDENTIFIER) {
			System.out.println(builder.getTokenText());
			YbTokens newElementType = ourSoftKeywords.get(builder.getTokenText());
			if (newElementType != null && tokenSet.contains(newElementType)) {
				builder.remapCurrentToken(newElementType);
				return newElementType;
			}
		}
		return null;
	}
}
