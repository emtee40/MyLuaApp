// Generated from D:/android studio project/MyLuaApp2/LuaAnalyzer/src/main/java/com/dingyi/lua/analyzer/parser\Lua.g4 by ANTLR 4.9.1
package com.dingyi.lua.analyzer.parser;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class LuaLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.9.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, T__16=17, 
		T__17=18, T__18=19, T__19=20, T__20=21, T__21=22, T__22=23, T__23=24, 
		T__24=25, T__25=26, T__26=27, T__27=28, T__28=29, T__29=30, T__30=31, 
		T__31=32, T__32=33, T__33=34, T__34=35, T__35=36, T__36=37, T__37=38, 
		T__38=39, T__39=40, T__40=41, T__41=42, T__42=43, T__43=44, T__44=45, 
		T__45=46, T__46=47, T__47=48, T__48=49, T__49=50, T__50=51, T__51=52, 
		T__52=53, T__53=54, T__54=55, T__55=56, T__56=57, T__57=58, T__58=59, 
		T__59=60, T__60=61, T__61=62, T__62=63, T__63=64, NAME=65, NORMALSTRING=66, 
		CHARSTRING=67, LONGSTRING=68, INT=69, HEX=70, FLOAT=71, HEX_FLOAT=72, 
		COMMENT=73, LINE_COMMENT=74, WS=75, SHEBANG=76;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "T__8", 
			"T__9", "T__10", "T__11", "T__12", "T__13", "T__14", "T__15", "T__16", 
			"T__17", "T__18", "T__19", "T__20", "T__21", "T__22", "T__23", "T__24", 
			"T__25", "T__26", "T__27", "T__28", "T__29", "T__30", "T__31", "T__32", 
			"T__33", "T__34", "T__35", "T__36", "T__37", "T__38", "T__39", "T__40", 
			"T__41", "T__42", "T__43", "T__44", "T__45", "T__46", "T__47", "T__48", 
			"T__49", "T__50", "T__51", "T__52", "T__53", "T__54", "T__55", "T__56", 
			"T__57", "T__58", "T__59", "T__60", "T__61", "T__62", "T__63", "NAME", 
			"NORMALSTRING", "CHARSTRING", "LONGSTRING", "NESTED_STR", "INT", "HEX", 
			"FLOAT", "HEX_FLOAT", "ExponentPart", "HexExponentPart", "EscapeSequence", 
			"DecimalEscape", "HexEscape", "UtfEscape", "Digit", "HexDigit", "COMMENT", 
			"LINE_COMMENT", "WS", "SHEBANG"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "';'", "'='", "'break'", "'continue'", "'goto'", "'do'", "'end'", 
			"'while'", "'repeat'", "'until'", "'if'", "'then'", "'for'", "','", "'in'", 
			"'function'", "'local'", "'$'", "'switch'", "'when'", "'else'", "'lambda'", 
			"'defer'", "'elseif'", "'default'", "'case'", "':'", "'<'", "'>'", "'return'", 
			"'::'", "'@'", "'.'", "'nil'", "'false'", "'true'", "'...'", "'('", "')'", 
			"'['", "']'", "'{'", "'}'", "'or'", "'and'", "'<='", "'>='", "'~='", 
			"'=='", "'..'", "'+'", "'-'", "'*'", "'/'", "'%'", "'//'", "'&'", "'|'", 
			"'~'", "'<<'", "'>>'", "'not'", "'#'", "'^'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, "NAME", "NORMALSTRING", "CHARSTRING", "LONGSTRING", 
			"INT", "HEX", "FLOAT", "HEX_FLOAT", "COMMENT", "LINE_COMMENT", "WS", 
			"SHEBANG"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public LuaLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Lua.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2N\u02a0\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="+
		"\4>\t>\4?\t?\4@\t@\4A\tA\4B\tB\4C\tC\4D\tD\4E\tE\4F\tF\4G\tG\4H\tH\4I"+
		"\tI\4J\tJ\4K\tK\4L\tL\4M\tM\4N\tN\4O\tO\4P\tP\4Q\tQ\4R\tR\4S\tS\4T\tT"+
		"\4U\tU\4V\tV\3\2\3\2\3\3\3\3\3\4\3\4\3\4\3\4\3\4\3\4\3\5\3\5\3\5\3\5\3"+
		"\5\3\5\3\5\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\t"+
		"\3\t\3\t\3\t\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\13\3\13\3\13\3\13\3"+
		"\13\3\13\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\16\3\16\3\16\3\16\3\17\3\17"+
		"\3\20\3\20\3\20\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\22\3\22"+
		"\3\22\3\22\3\22\3\22\3\23\3\23\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\25"+
		"\3\25\3\25\3\25\3\25\3\26\3\26\3\26\3\26\3\26\3\27\3\27\3\27\3\27\3\27"+
		"\3\27\3\27\3\30\3\30\3\30\3\30\3\30\3\30\3\31\3\31\3\31\3\31\3\31\3\31"+
		"\3\31\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\33\3\33\3\33\3\33\3\33"+
		"\3\34\3\34\3\35\3\35\3\36\3\36\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3 \3"+
		" \3 \3!\3!\3\"\3\"\3#\3#\3#\3#\3$\3$\3$\3$\3$\3$\3%\3%\3%\3%\3%\3&\3&"+
		"\3&\3&\3\'\3\'\3(\3(\3)\3)\3*\3*\3+\3+\3,\3,\3-\3-\3-\3.\3.\3.\3.\3/\3"+
		"/\3/\3\60\3\60\3\60\3\61\3\61\3\61\3\62\3\62\3\62\3\63\3\63\3\63\3\64"+
		"\3\64\3\65\3\65\3\66\3\66\3\67\3\67\38\38\39\39\39\3:\3:\3;\3;\3<\3<\3"+
		"=\3=\3=\3>\3>\3>\3?\3?\3?\3?\3@\3@\3A\3A\3B\3B\7B\u01a0\nB\fB\16B\u01a3"+
		"\13B\3C\3C\3C\7C\u01a8\nC\fC\16C\u01ab\13C\3C\3C\3D\3D\3D\7D\u01b2\nD"+
		"\fD\16D\u01b5\13D\3D\3D\3E\3E\3E\3E\3F\3F\3F\3F\3F\3F\7F\u01c3\nF\fF\16"+
		"F\u01c6\13F\3F\5F\u01c9\nF\3G\6G\u01cc\nG\rG\16G\u01cd\3H\3H\3H\6H\u01d3"+
		"\nH\rH\16H\u01d4\3I\6I\u01d8\nI\rI\16I\u01d9\3I\3I\7I\u01de\nI\fI\16I"+
		"\u01e1\13I\3I\5I\u01e4\nI\3I\3I\6I\u01e8\nI\rI\16I\u01e9\3I\5I\u01ed\n"+
		"I\3I\6I\u01f0\nI\rI\16I\u01f1\3I\3I\5I\u01f6\nI\3J\3J\3J\6J\u01fb\nJ\r"+
		"J\16J\u01fc\3J\3J\7J\u0201\nJ\fJ\16J\u0204\13J\3J\5J\u0207\nJ\3J\3J\3"+
		"J\3J\6J\u020d\nJ\rJ\16J\u020e\3J\5J\u0212\nJ\3J\3J\3J\6J\u0217\nJ\rJ\16"+
		"J\u0218\3J\3J\5J\u021d\nJ\3K\3K\5K\u0221\nK\3K\6K\u0224\nK\rK\16K\u0225"+
		"\3L\3L\5L\u022a\nL\3L\6L\u022d\nL\rL\16L\u022e\3M\3M\3M\3M\5M\u0235\n"+
		"M\3M\3M\3M\3M\5M\u023b\nM\3N\3N\3N\3N\3N\3N\3N\3N\3N\3N\3N\5N\u0248\n"+
		"N\3O\3O\3O\3O\3O\3P\3P\3P\3P\3P\6P\u0254\nP\rP\16P\u0255\3P\3P\3Q\3Q\3"+
		"R\3R\3S\3S\3S\3S\3S\3S\3S\3S\3S\3T\3T\3T\3T\3T\3T\7T\u026d\nT\fT\16T\u0270"+
		"\13T\3T\3T\7T\u0274\nT\fT\16T\u0277\13T\3T\3T\7T\u027b\nT\fT\16T\u027e"+
		"\13T\3T\3T\7T\u0282\nT\fT\16T\u0285\13T\5T\u0287\nT\3T\3T\3T\5T\u028c"+
		"\nT\3T\3T\3U\6U\u0291\nU\rU\16U\u0292\3U\3U\3V\3V\3V\7V\u029a\nV\fV\16"+
		"V\u029d\13V\3V\3V\3\u01c4\2W\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13"+
		"\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25)\26+\27-\30/\31\61"+
		"\32\63\33\65\34\67\359\36;\37= ?!A\"C#E$G%I&K\'M(O)Q*S+U,W-Y.[/]\60_\61"+
		"a\62c\63e\64g\65i\66k\67m8o9q:s;u<w=y>{?}@\177A\u0081B\u0083C\u0085D\u0087"+
		"E\u0089F\u008b\2\u008dG\u008fH\u0091I\u0093J\u0095\2\u0097\2\u0099\2\u009b"+
		"\2\u009d\2\u009f\2\u00a1\2\u00a3\2\u00a5K\u00a7L\u00a9M\u00abN\3\2\23"+
		"\5\2C\\aac|\6\2\62;C\\aac|\4\2$$^^\4\2))^^\4\2ZZzz\4\2GGgg\4\2--//\4\2"+
		"RRrr\f\2$$))^^cdhhppttvvxx||\3\2\62\64\3\2\62;\5\2\62;CHch\6\2\f\f\17"+
		"\17??]]\4\2\f\f\17\17\5\2\f\f\17\17]]\4\3\f\f\17\17\5\2\13\f\16\17\"\""+
		"\2\u02c5\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2"+
		"\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27"+
		"\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2"+
		"\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2"+
		"\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2"+
		"\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2\2\2\2E\3\2\2\2"+
		"\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2\2\2M\3\2\2\2\2O\3\2\2\2\2Q\3\2\2\2\2S"+
		"\3\2\2\2\2U\3\2\2\2\2W\3\2\2\2\2Y\3\2\2\2\2[\3\2\2\2\2]\3\2\2\2\2_\3\2"+
		"\2\2\2a\3\2\2\2\2c\3\2\2\2\2e\3\2\2\2\2g\3\2\2\2\2i\3\2\2\2\2k\3\2\2\2"+
		"\2m\3\2\2\2\2o\3\2\2\2\2q\3\2\2\2\2s\3\2\2\2\2u\3\2\2\2\2w\3\2\2\2\2y"+
		"\3\2\2\2\2{\3\2\2\2\2}\3\2\2\2\2\177\3\2\2\2\2\u0081\3\2\2\2\2\u0083\3"+
		"\2\2\2\2\u0085\3\2\2\2\2\u0087\3\2\2\2\2\u0089\3\2\2\2\2\u008d\3\2\2\2"+
		"\2\u008f\3\2\2\2\2\u0091\3\2\2\2\2\u0093\3\2\2\2\2\u00a5\3\2\2\2\2\u00a7"+
		"\3\2\2\2\2\u00a9\3\2\2\2\2\u00ab\3\2\2\2\3\u00ad\3\2\2\2\5\u00af\3\2\2"+
		"\2\7\u00b1\3\2\2\2\t\u00b7\3\2\2\2\13\u00c0\3\2\2\2\r\u00c5\3\2\2\2\17"+
		"\u00c8\3\2\2\2\21\u00cc\3\2\2\2\23\u00d2\3\2\2\2\25\u00d9\3\2\2\2\27\u00df"+
		"\3\2\2\2\31\u00e2\3\2\2\2\33\u00e7\3\2\2\2\35\u00eb\3\2\2\2\37\u00ed\3"+
		"\2\2\2!\u00f0\3\2\2\2#\u00f9\3\2\2\2%\u00ff\3\2\2\2\'\u0101\3\2\2\2)\u0108"+
		"\3\2\2\2+\u010d\3\2\2\2-\u0112\3\2\2\2/\u0119\3\2\2\2\61\u011f\3\2\2\2"+
		"\63\u0126\3\2\2\2\65\u012e\3\2\2\2\67\u0133\3\2\2\29\u0135\3\2\2\2;\u0137"+
		"\3\2\2\2=\u0139\3\2\2\2?\u0140\3\2\2\2A\u0143\3\2\2\2C\u0145\3\2\2\2E"+
		"\u0147\3\2\2\2G\u014b\3\2\2\2I\u0151\3\2\2\2K\u0156\3\2\2\2M\u015a\3\2"+
		"\2\2O\u015c\3\2\2\2Q\u015e\3\2\2\2S\u0160\3\2\2\2U\u0162\3\2\2\2W\u0164"+
		"\3\2\2\2Y\u0166\3\2\2\2[\u0169\3\2\2\2]\u016d\3\2\2\2_\u0170\3\2\2\2a"+
		"\u0173\3\2\2\2c\u0176\3\2\2\2e\u0179\3\2\2\2g\u017c\3\2\2\2i\u017e\3\2"+
		"\2\2k\u0180\3\2\2\2m\u0182\3\2\2\2o\u0184\3\2\2\2q\u0186\3\2\2\2s\u0189"+
		"\3\2\2\2u\u018b\3\2\2\2w\u018d\3\2\2\2y\u018f\3\2\2\2{\u0192\3\2\2\2}"+
		"\u0195\3\2\2\2\177\u0199\3\2\2\2\u0081\u019b\3\2\2\2\u0083\u019d\3\2\2"+
		"\2\u0085\u01a4\3\2\2\2\u0087\u01ae\3\2\2\2\u0089\u01b8\3\2\2\2\u008b\u01c8"+
		"\3\2\2\2\u008d\u01cb\3\2\2\2\u008f\u01cf\3\2\2\2\u0091\u01f5\3\2\2\2\u0093"+
		"\u021c\3\2\2\2\u0095\u021e\3\2\2\2\u0097\u0227\3\2\2\2\u0099\u023a\3\2"+
		"\2\2\u009b\u0247\3\2\2\2\u009d\u0249\3\2\2\2\u009f\u024e\3\2\2\2\u00a1"+
		"\u0259\3\2\2\2\u00a3\u025b\3\2\2\2\u00a5\u025d\3\2\2\2\u00a7\u0266\3\2"+
		"\2\2\u00a9\u0290\3\2\2\2\u00ab\u0296\3\2\2\2\u00ad\u00ae\7=\2\2\u00ae"+
		"\4\3\2\2\2\u00af\u00b0\7?\2\2\u00b0\6\3\2\2\2\u00b1\u00b2\7d\2\2\u00b2"+
		"\u00b3\7t\2\2\u00b3\u00b4\7g\2\2\u00b4\u00b5\7c\2\2\u00b5\u00b6\7m\2\2"+
		"\u00b6\b\3\2\2\2\u00b7\u00b8\7e\2\2\u00b8\u00b9\7q\2\2\u00b9\u00ba\7p"+
		"\2\2\u00ba\u00bb\7v\2\2\u00bb\u00bc\7k\2\2\u00bc\u00bd\7p\2\2\u00bd\u00be"+
		"\7w\2\2\u00be\u00bf\7g\2\2\u00bf\n\3\2\2\2\u00c0\u00c1\7i\2\2\u00c1\u00c2"+
		"\7q\2\2\u00c2\u00c3\7v\2\2\u00c3\u00c4\7q\2\2\u00c4\f\3\2\2\2\u00c5\u00c6"+
		"\7f\2\2\u00c6\u00c7\7q\2\2\u00c7\16\3\2\2\2\u00c8\u00c9\7g\2\2\u00c9\u00ca"+
		"\7p\2\2\u00ca\u00cb\7f\2\2\u00cb\20\3\2\2\2\u00cc\u00cd\7y\2\2\u00cd\u00ce"+
		"\7j\2\2\u00ce\u00cf\7k\2\2\u00cf\u00d0\7n\2\2\u00d0\u00d1\7g\2\2\u00d1"+
		"\22\3\2\2\2\u00d2\u00d3\7t\2\2\u00d3\u00d4\7g\2\2\u00d4\u00d5\7r\2\2\u00d5"+
		"\u00d6\7g\2\2\u00d6\u00d7\7c\2\2\u00d7\u00d8\7v\2\2\u00d8\24\3\2\2\2\u00d9"+
		"\u00da\7w\2\2\u00da\u00db\7p\2\2\u00db\u00dc\7v\2\2\u00dc\u00dd\7k\2\2"+
		"\u00dd\u00de\7n\2\2\u00de\26\3\2\2\2\u00df\u00e0\7k\2\2\u00e0\u00e1\7"+
		"h\2\2\u00e1\30\3\2\2\2\u00e2\u00e3\7v\2\2\u00e3\u00e4\7j\2\2\u00e4\u00e5"+
		"\7g\2\2\u00e5\u00e6\7p\2\2\u00e6\32\3\2\2\2\u00e7\u00e8\7h\2\2\u00e8\u00e9"+
		"\7q\2\2\u00e9\u00ea\7t\2\2\u00ea\34\3\2\2\2\u00eb\u00ec\7.\2\2\u00ec\36"+
		"\3\2\2\2\u00ed\u00ee\7k\2\2\u00ee\u00ef\7p\2\2\u00ef \3\2\2\2\u00f0\u00f1"+
		"\7h\2\2\u00f1\u00f2\7w\2\2\u00f2\u00f3\7p\2\2\u00f3\u00f4\7e\2\2\u00f4"+
		"\u00f5\7v\2\2\u00f5\u00f6\7k\2\2\u00f6\u00f7\7q\2\2\u00f7\u00f8\7p\2\2"+
		"\u00f8\"\3\2\2\2\u00f9\u00fa\7n\2\2\u00fa\u00fb\7q\2\2\u00fb\u00fc\7e"+
		"\2\2\u00fc\u00fd\7c\2\2\u00fd\u00fe\7n\2\2\u00fe$\3\2\2\2\u00ff\u0100"+
		"\7&\2\2\u0100&\3\2\2\2\u0101\u0102\7u\2\2\u0102\u0103\7y\2\2\u0103\u0104"+
		"\7k\2\2\u0104\u0105\7v\2\2\u0105\u0106\7e\2\2\u0106\u0107\7j\2\2\u0107"+
		"(\3\2\2\2\u0108\u0109\7y\2\2\u0109\u010a\7j\2\2\u010a\u010b\7g\2\2\u010b"+
		"\u010c\7p\2\2\u010c*\3\2\2\2\u010d\u010e\7g\2\2\u010e\u010f\7n\2\2\u010f"+
		"\u0110\7u\2\2\u0110\u0111\7g\2\2\u0111,\3\2\2\2\u0112\u0113\7n\2\2\u0113"+
		"\u0114\7c\2\2\u0114\u0115\7o\2\2\u0115\u0116\7d\2\2\u0116\u0117\7f\2\2"+
		"\u0117\u0118\7c\2\2\u0118.\3\2\2\2\u0119\u011a\7f\2\2\u011a\u011b\7g\2"+
		"\2\u011b\u011c\7h\2\2\u011c\u011d\7g\2\2\u011d\u011e\7t\2\2\u011e\60\3"+
		"\2\2\2\u011f\u0120\7g\2\2\u0120\u0121\7n\2\2\u0121\u0122\7u\2\2\u0122"+
		"\u0123\7g\2\2\u0123\u0124\7k\2\2\u0124\u0125\7h\2\2\u0125\62\3\2\2\2\u0126"+
		"\u0127\7f\2\2\u0127\u0128\7g\2\2\u0128\u0129\7h\2\2\u0129\u012a\7c\2\2"+
		"\u012a\u012b\7w\2\2\u012b\u012c\7n\2\2\u012c\u012d\7v\2\2\u012d\64\3\2"+
		"\2\2\u012e\u012f\7e\2\2\u012f\u0130\7c\2\2\u0130\u0131\7u\2\2\u0131\u0132"+
		"\7g\2\2\u0132\66\3\2\2\2\u0133\u0134\7<\2\2\u01348\3\2\2\2\u0135\u0136"+
		"\7>\2\2\u0136:\3\2\2\2\u0137\u0138\7@\2\2\u0138<\3\2\2\2\u0139\u013a\7"+
		"t\2\2\u013a\u013b\7g\2\2\u013b\u013c\7v\2\2\u013c\u013d\7w\2\2\u013d\u013e"+
		"\7t\2\2\u013e\u013f\7p\2\2\u013f>\3\2\2\2\u0140\u0141\7<\2\2\u0141\u0142"+
		"\7<\2\2\u0142@\3\2\2\2\u0143\u0144\7B\2\2\u0144B\3\2\2\2\u0145\u0146\7"+
		"\60\2\2\u0146D\3\2\2\2\u0147\u0148\7p\2\2\u0148\u0149\7k\2\2\u0149\u014a"+
		"\7n\2\2\u014aF\3\2\2\2\u014b\u014c\7h\2\2\u014c\u014d\7c\2\2\u014d\u014e"+
		"\7n\2\2\u014e\u014f\7u\2\2\u014f\u0150\7g\2\2\u0150H\3\2\2\2\u0151\u0152"+
		"\7v\2\2\u0152\u0153\7t\2\2\u0153\u0154\7w\2\2\u0154\u0155\7g\2\2\u0155"+
		"J\3\2\2\2\u0156\u0157\7\60\2\2\u0157\u0158\7\60\2\2\u0158\u0159\7\60\2"+
		"\2\u0159L\3\2\2\2\u015a\u015b\7*\2\2\u015bN\3\2\2\2\u015c\u015d\7+\2\2"+
		"\u015dP\3\2\2\2\u015e\u015f\7]\2\2\u015fR\3\2\2\2\u0160\u0161\7_\2\2\u0161"+
		"T\3\2\2\2\u0162\u0163\7}\2\2\u0163V\3\2\2\2\u0164\u0165\7\177\2\2\u0165"+
		"X\3\2\2\2\u0166\u0167\7q\2\2\u0167\u0168\7t\2\2\u0168Z\3\2\2\2\u0169\u016a"+
		"\7c\2\2\u016a\u016b\7p\2\2\u016b\u016c\7f\2\2\u016c\\\3\2\2\2\u016d\u016e"+
		"\7>\2\2\u016e\u016f\7?\2\2\u016f^\3\2\2\2\u0170\u0171\7@\2\2\u0171\u0172"+
		"\7?\2\2\u0172`\3\2\2\2\u0173\u0174\7\u0080\2\2\u0174\u0175\7?\2\2\u0175"+
		"b\3\2\2\2\u0176\u0177\7?\2\2\u0177\u0178\7?\2\2\u0178d\3\2\2\2\u0179\u017a"+
		"\7\60\2\2\u017a\u017b\7\60\2\2\u017bf\3\2\2\2\u017c\u017d\7-\2\2\u017d"+
		"h\3\2\2\2\u017e\u017f\7/\2\2\u017fj\3\2\2\2\u0180\u0181\7,\2\2\u0181l"+
		"\3\2\2\2\u0182\u0183\7\61\2\2\u0183n\3\2\2\2\u0184\u0185\7\'\2\2\u0185"+
		"p\3\2\2\2\u0186\u0187\7\61\2\2\u0187\u0188\7\61\2\2\u0188r\3\2\2\2\u0189"+
		"\u018a\7(\2\2\u018at\3\2\2\2\u018b\u018c\7~\2\2\u018cv\3\2\2\2\u018d\u018e"+
		"\7\u0080\2\2\u018ex\3\2\2\2\u018f\u0190\7>\2\2\u0190\u0191\7>\2\2\u0191"+
		"z\3\2\2\2\u0192\u0193\7@\2\2\u0193\u0194\7@\2\2\u0194|\3\2\2\2\u0195\u0196"+
		"\7p\2\2\u0196\u0197\7q\2\2\u0197\u0198\7v\2\2\u0198~\3\2\2\2\u0199\u019a"+
		"\7%\2\2\u019a\u0080\3\2\2\2\u019b\u019c\7`\2\2\u019c\u0082\3\2\2\2\u019d"+
		"\u01a1\t\2\2\2\u019e\u01a0\t\3\2\2\u019f\u019e\3\2\2\2\u01a0\u01a3\3\2"+
		"\2\2\u01a1\u019f\3\2\2\2\u01a1\u01a2\3\2\2\2\u01a2\u0084\3\2\2\2\u01a3"+
		"\u01a1\3\2\2\2\u01a4\u01a9\7$\2\2\u01a5\u01a8\5\u0099M\2\u01a6\u01a8\n"+
		"\4\2\2\u01a7\u01a5\3\2\2\2\u01a7\u01a6\3\2\2\2\u01a8\u01ab\3\2\2\2\u01a9"+
		"\u01a7\3\2\2\2\u01a9\u01aa\3\2\2\2\u01aa\u01ac\3\2\2\2\u01ab\u01a9\3\2"+
		"\2\2\u01ac\u01ad\7$\2\2\u01ad\u0086\3\2\2\2\u01ae\u01b3\7)\2\2\u01af\u01b2"+
		"\5\u0099M\2\u01b0\u01b2\n\5\2\2\u01b1\u01af\3\2\2\2\u01b1\u01b0\3\2\2"+
		"\2\u01b2\u01b5\3\2\2\2\u01b3\u01b1\3\2\2\2\u01b3\u01b4\3\2\2\2\u01b4\u01b6"+
		"\3\2\2\2\u01b5\u01b3\3\2\2\2\u01b6\u01b7\7)\2\2\u01b7\u0088\3\2\2\2\u01b8"+
		"\u01b9\7]\2\2\u01b9\u01ba\5\u008bF\2\u01ba\u01bb\7_\2\2\u01bb\u008a\3"+
		"\2\2\2\u01bc\u01bd\7?\2\2\u01bd\u01be\5\u008bF\2\u01be\u01bf\7?\2\2\u01bf"+
		"\u01c9\3\2\2\2\u01c0\u01c4\7]\2\2\u01c1\u01c3\13\2\2\2\u01c2\u01c1\3\2"+
		"\2\2\u01c3\u01c6\3\2\2\2\u01c4\u01c5\3\2\2\2\u01c4\u01c2\3\2\2\2\u01c5"+
		"\u01c7\3\2\2\2\u01c6\u01c4\3\2\2\2\u01c7\u01c9\7_\2\2\u01c8\u01bc\3\2"+
		"\2\2\u01c8\u01c0\3\2\2\2\u01c9\u008c\3\2\2\2\u01ca\u01cc\5\u00a1Q\2\u01cb"+
		"\u01ca\3\2\2\2\u01cc\u01cd\3\2\2\2\u01cd\u01cb\3\2\2\2\u01cd\u01ce\3\2"+
		"\2\2\u01ce\u008e\3\2\2\2\u01cf\u01d0\7\62\2\2\u01d0\u01d2\t\6\2\2\u01d1"+
		"\u01d3\5\u00a3R\2\u01d2\u01d1\3\2\2\2\u01d3\u01d4\3\2\2\2\u01d4\u01d2"+
		"\3\2\2\2\u01d4\u01d5\3\2\2\2\u01d5\u0090\3\2\2\2\u01d6\u01d8\5\u00a1Q"+
		"\2\u01d7\u01d6\3\2\2\2\u01d8\u01d9\3\2\2\2\u01d9\u01d7\3\2\2\2\u01d9\u01da"+
		"\3\2\2\2\u01da\u01db\3\2\2\2\u01db\u01df\7\60\2\2\u01dc\u01de\5\u00a1"+
		"Q\2\u01dd\u01dc\3\2\2\2\u01de\u01e1\3\2\2\2\u01df\u01dd\3\2\2\2\u01df"+
		"\u01e0\3\2\2\2\u01e0\u01e3\3\2\2\2\u01e1\u01df\3\2\2\2\u01e2\u01e4\5\u0095"+
		"K\2\u01e3\u01e2\3\2\2\2\u01e3\u01e4\3\2\2\2\u01e4\u01f6\3\2\2\2\u01e5"+
		"\u01e7\7\60\2\2\u01e6\u01e8\5\u00a1Q\2\u01e7\u01e6\3\2\2\2\u01e8\u01e9"+
		"\3\2\2\2\u01e9\u01e7\3\2\2\2\u01e9\u01ea\3\2\2\2\u01ea\u01ec\3\2\2\2\u01eb"+
		"\u01ed\5\u0095K\2\u01ec\u01eb\3\2\2\2\u01ec\u01ed\3\2\2\2\u01ed\u01f6"+
		"\3\2\2\2\u01ee\u01f0\5\u00a1Q\2\u01ef\u01ee\3\2\2\2\u01f0\u01f1\3\2\2"+
		"\2\u01f1\u01ef\3\2\2\2\u01f1\u01f2\3\2\2\2\u01f2\u01f3\3\2\2\2\u01f3\u01f4"+
		"\5\u0095K\2\u01f4\u01f6\3\2\2\2\u01f5\u01d7\3\2\2\2\u01f5\u01e5\3\2\2"+
		"\2\u01f5\u01ef\3\2\2\2\u01f6\u0092\3\2\2\2\u01f7\u01f8\7\62\2\2\u01f8"+
		"\u01fa\t\6\2\2\u01f9\u01fb\5\u00a3R\2\u01fa\u01f9\3\2\2\2\u01fb\u01fc"+
		"\3\2\2\2\u01fc\u01fa\3\2\2\2\u01fc\u01fd\3\2\2\2\u01fd\u01fe\3\2\2\2\u01fe"+
		"\u0202\7\60\2\2\u01ff\u0201\5\u00a3R\2\u0200\u01ff\3\2\2\2\u0201\u0204"+
		"\3\2\2\2\u0202\u0200\3\2\2\2\u0202\u0203\3\2\2\2\u0203\u0206\3\2\2\2\u0204"+
		"\u0202\3\2\2\2\u0205\u0207\5\u0097L\2\u0206\u0205\3\2\2\2\u0206\u0207"+
		"\3\2\2\2\u0207\u021d\3\2\2\2\u0208\u0209\7\62\2\2\u0209\u020a\t\6\2\2"+
		"\u020a\u020c\7\60\2\2\u020b\u020d\5\u00a3R\2\u020c\u020b\3\2\2\2\u020d"+
		"\u020e\3\2\2\2\u020e\u020c\3\2\2\2\u020e\u020f\3\2\2\2\u020f\u0211\3\2"+
		"\2\2\u0210\u0212\5\u0097L\2\u0211\u0210\3\2\2\2\u0211\u0212\3\2\2\2\u0212"+
		"\u021d\3\2\2\2\u0213\u0214\7\62\2\2\u0214\u0216\t\6\2\2\u0215\u0217\5"+
		"\u00a3R\2\u0216\u0215\3\2\2\2\u0217\u0218\3\2\2\2\u0218\u0216\3\2\2\2"+
		"\u0218\u0219\3\2\2\2\u0219\u021a\3\2\2\2\u021a\u021b\5\u0097L\2\u021b"+
		"\u021d\3\2\2\2\u021c\u01f7\3\2\2\2\u021c\u0208\3\2\2\2\u021c\u0213\3\2"+
		"\2\2\u021d\u0094\3\2\2\2\u021e\u0220\t\7\2\2\u021f\u0221\t\b\2\2\u0220"+
		"\u021f\3\2\2\2\u0220\u0221\3\2\2\2\u0221\u0223\3\2\2\2\u0222\u0224\5\u00a1"+
		"Q\2\u0223\u0222\3\2\2\2\u0224\u0225\3\2\2\2\u0225\u0223\3\2\2\2\u0225"+
		"\u0226\3\2\2\2\u0226\u0096\3\2\2\2\u0227\u0229\t\t\2\2\u0228\u022a\t\b"+
		"\2\2\u0229\u0228\3\2\2\2\u0229\u022a\3\2\2\2\u022a\u022c\3\2\2\2\u022b"+
		"\u022d\5\u00a1Q\2\u022c\u022b\3\2\2\2\u022d\u022e\3\2\2\2\u022e\u022c"+
		"\3\2\2\2\u022e\u022f\3\2\2\2\u022f\u0098\3\2\2\2\u0230\u0231\7^\2\2\u0231"+
		"\u023b\t\n\2\2\u0232\u0234\7^\2\2\u0233\u0235\7\17\2\2\u0234\u0233\3\2"+
		"\2\2\u0234\u0235\3\2\2\2\u0235\u0236\3\2\2\2\u0236\u023b\7\f\2\2\u0237"+
		"\u023b\5\u009bN\2\u0238\u023b\5\u009dO\2\u0239\u023b\5\u009fP\2\u023a"+
		"\u0230\3\2\2\2\u023a\u0232\3\2\2\2\u023a\u0237\3\2\2\2\u023a\u0238\3\2"+
		"\2\2\u023a\u0239\3\2\2\2\u023b\u009a\3\2\2\2\u023c\u023d\7^\2\2\u023d"+
		"\u0248\5\u00a1Q\2\u023e\u023f\7^\2\2\u023f\u0240\5\u00a1Q\2\u0240\u0241"+
		"\5\u00a1Q\2\u0241\u0248\3\2\2\2\u0242\u0243\7^\2\2\u0243\u0244\t\13\2"+
		"\2\u0244\u0245\5\u00a1Q\2\u0245\u0246\5\u00a1Q\2\u0246\u0248\3\2\2\2\u0247"+
		"\u023c\3\2\2\2\u0247\u023e\3\2\2\2\u0247\u0242\3\2\2\2\u0248\u009c\3\2"+
		"\2\2\u0249\u024a\7^\2\2\u024a\u024b\7z\2\2\u024b\u024c\5\u00a3R\2\u024c"+
		"\u024d\5\u00a3R\2\u024d\u009e\3\2\2\2\u024e\u024f\7^\2\2\u024f\u0250\7"+
		"w\2\2\u0250\u0251\7}\2\2\u0251\u0253\3\2\2\2\u0252\u0254\5\u00a3R\2\u0253"+
		"\u0252\3\2\2\2\u0254\u0255\3\2\2\2\u0255\u0253\3\2\2\2\u0255\u0256\3\2"+
		"\2\2\u0256\u0257\3\2\2\2\u0257\u0258\7\177\2\2\u0258\u00a0\3\2\2\2\u0259"+
		"\u025a\t\f\2\2\u025a\u00a2\3\2\2\2\u025b\u025c\t\r\2\2\u025c\u00a4\3\2"+
		"\2\2\u025d\u025e\7/\2\2\u025e\u025f\7/\2\2\u025f\u0260\7]\2\2\u0260\u0261"+
		"\3\2\2\2\u0261\u0262\5\u008bF\2\u0262\u0263\7_\2\2\u0263\u0264\3\2\2\2"+
		"\u0264\u0265\bS\2\2\u0265\u00a6\3\2\2\2\u0266\u0267\7/\2\2\u0267\u0268"+
		"\7/\2\2\u0268\u0286\3\2\2\2\u0269\u0287\3\2\2\2\u026a\u026e\7]\2\2\u026b"+
		"\u026d\7?\2\2\u026c\u026b\3\2\2\2\u026d\u0270\3\2\2\2\u026e\u026c\3\2"+
		"\2\2\u026e\u026f\3\2\2\2\u026f\u0287\3\2\2\2\u0270\u026e\3\2\2\2\u0271"+
		"\u0275\7]\2\2\u0272\u0274\7?\2\2\u0273\u0272\3\2\2\2\u0274\u0277\3\2\2"+
		"\2\u0275\u0273\3\2\2\2\u0275\u0276\3\2\2\2\u0276\u0278\3\2\2\2\u0277\u0275"+
		"\3\2\2\2\u0278\u027c\n\16\2\2\u0279\u027b\n\17\2\2\u027a\u0279\3\2\2\2"+
		"\u027b\u027e\3\2\2\2\u027c\u027a\3\2\2\2\u027c\u027d\3\2\2\2\u027d\u0287"+
		"\3\2\2\2\u027e\u027c\3\2\2\2\u027f\u0283\n\20\2\2\u0280\u0282\n\17\2\2"+
		"\u0281\u0280\3\2\2\2\u0282\u0285\3\2\2\2\u0283\u0281\3\2\2\2\u0283\u0284"+
		"\3\2\2\2\u0284\u0287\3\2\2\2\u0285\u0283\3\2\2\2\u0286\u0269\3\2\2\2\u0286"+
		"\u026a\3\2\2\2\u0286\u0271\3\2\2\2\u0286\u027f\3\2\2\2\u0287\u028b\3\2"+
		"\2\2\u0288\u0289\7\17\2\2\u0289\u028c\7\f\2\2\u028a\u028c\t\21\2\2\u028b"+
		"\u0288\3\2\2\2\u028b\u028a\3\2\2\2\u028c\u028d\3\2\2\2\u028d\u028e\bT"+
		"\2\2\u028e\u00a8\3\2\2\2\u028f\u0291\t\22\2\2\u0290\u028f\3\2\2\2\u0291"+
		"\u0292\3\2\2\2\u0292\u0290\3\2\2\2\u0292\u0293\3\2\2\2\u0293\u0294\3\2"+
		"\2\2\u0294\u0295\bU\3\2\u0295\u00aa\3\2\2\2\u0296\u0297\7%\2\2\u0297\u029b"+
		"\7#\2\2\u0298\u029a\n\17\2\2\u0299\u0298\3\2\2\2\u029a\u029d\3\2\2\2\u029b"+
		"\u0299\3\2\2\2\u029b\u029c\3\2\2\2\u029c\u029e\3\2\2\2\u029d\u029b\3\2"+
		"\2\2\u029e\u029f\bV\2\2\u029f\u00ac\3\2\2\2*\2\u01a1\u01a7\u01a9\u01b1"+
		"\u01b3\u01c4\u01c8\u01cd\u01d4\u01d9\u01df\u01e3\u01e9\u01ec\u01f1\u01f5"+
		"\u01fc\u0202\u0206\u020e\u0211\u0218\u021c\u0220\u0225\u0229\u022e\u0234"+
		"\u023a\u0247\u0255\u026e\u0275\u027c\u0283\u0286\u028b\u0292\u029b\4\2"+
		"\3\2\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}