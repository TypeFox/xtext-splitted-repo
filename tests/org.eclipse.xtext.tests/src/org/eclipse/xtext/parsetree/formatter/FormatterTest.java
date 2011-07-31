package org.eclipse.xtext.parsetree.formatter;

import java.io.IOException;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.IGrammarAccess;
import org.eclipse.xtext.ParserRule;
import org.eclipse.xtext.formatting.INodeModelFormatter.IFormattedRegion;
import org.eclipse.xtext.formatting.impl.AbstractTokenStream;
import org.eclipse.xtext.junit.AbstractXtextTests;
import org.eclipse.xtext.junit.util.ParseHelper;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.parsetree.formatter.formattertestlanguage.Decl;
import org.eclipse.xtext.parsetree.formatter.formattertestlanguage.FormattertestlanguageFactory;
import org.eclipse.xtext.parsetree.formatter.formattertestlanguage.TestLinewrapMinMax;
import org.eclipse.xtext.parsetree.reconstr.ITokenStream;
import org.eclipse.xtext.resource.SaveOptions;
import org.eclipse.xtext.serializer.acceptor.ISemanticSequenceAcceptor;
import org.eclipse.xtext.serializer.acceptor.ISyntacticSequenceAcceptor;
import org.eclipse.xtext.serializer.acceptor.TokenStreamSequenceAdapter;
import org.eclipse.xtext.serializer.diagnostic.ISerializationDiagnostic;
import org.eclipse.xtext.serializer.sequencer.IHiddenTokenSequencer;
import org.eclipse.xtext.serializer.sequencer.ISemanticSequencer;
import org.eclipse.xtext.serializer.sequencer.ISyntacticSequencer;

public class FormatterTest extends AbstractXtextTests {

	protected static class TokenBuffer extends AbstractTokenStream {

		private StringBuffer buf = new StringBuffer();

		@Override
		public void writeHidden(EObject grammarElement, String value) throws IOException {
			buf.append("Hidden   " + grammarElement.eClass().getName() + ": '" + value + "'\n");
		}

		@Override
		public void writeSemantic(EObject grammarElement, String value) throws IOException {
			buf.append("Semantic " + grammarElement.eClass().getName() + ": '" + value + "'\n");
		}

		@Override
		public void init(ParserRule startRule) {
			buf.append("Start '" + startRule.getName() + "'\n");
		}

		@Override
		public String toString() {
			return buf.toString();
		}

	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		with(FormatterTestLanguageStandaloneSetup.class);
	}

	// test formatting based on the ParseTreeConstructor
	private void assertFormattedPTC(String expected, String model) throws Exception {
		EObject m = getModel(model);
		String res = getSerializer().serialize(m, SaveOptions.newBuilder().format().getOptions());
		assertEquals(expected, res);
	}

	private void assertPreserved(String model) throws Exception {
		EObject m = getModel(model);
		String res = getSerializer().serialize(m, SaveOptions.newBuilder().getOptions());
		assertEquals(model, res);
	}

	// test formatting based on the NodeModel
	private void assertFormattedNM(String expected, String model, int offset, int lengt) throws Exception {
		ICompositeNode node = NodeModelUtils.getNode(getModel(model)).getRootNode();
		// System.out.println(EmfFormatter.objToStr(node));
		IFormattedRegion r = getNodeModelFormatter().format(node, offset, lengt);
		String actual = model.substring(0, r.getOffset()) + r.getFormattedText()
				+ model.substring(r.getLength() + r.getOffset());
		assertEquals(expected, actual);
	}

	protected void serializeToTokenBuffer(String model, ITokenStream out) throws Exception {
		EObject semanticObject = get(ParseHelper.class).parse(model);
		ISerializationDiagnostic.Acceptor errors = ISerializationDiagnostic.EXCEPTION_THROWING_ACCEPTOR;
		ISemanticSequencer semantic = get(ISemanticSequencer.class);
		ISyntacticSequencer syntactic = get(ISyntacticSequencer.class);
		IHiddenTokenSequencer hidden = get(IHiddenTokenSequencer.class);
		TokenStreamSequenceAdapter tokenstream = new TokenStreamSequenceAdapter(out, errors);
		semantic.init((ISemanticSequenceAcceptor) syntactic, errors);
		EObject context = get(IGrammarAccess.class).getGrammar().getRules().get(0);
		syntactic.init(context, semanticObject, (ISyntacticSequenceAcceptor) hidden, errors);
		hidden.init(context, semanticObject, tokenstream, errors);
		tokenstream.init(context);
		semantic.createSequence(context, semanticObject);
	}

	private void assertEqualTokenStreams(String modelString) throws Exception {
		// disabled for now since the new serializer appends/prepends whitespace 
		// to serialized regions and the old one doesn't.
		//		EObject model = getModel(modelString);
		//		//		IParseTreeConstructor ptc = get(IParseTreeConstructor.class);
		//		INodeModelStreamer nms = get(INodeModelStreamer.class);
		//		TokenBuffer ptcTb = new TokenBuffer();
		//		TokenBuffer nmsTb = new TokenBuffer();
		//		//		ptc.serializeSubtree(model, ptcTb);
		//		serializeToTokenBuffer(modelString, ptcTb);
		//		nms.feedTokenStream(nmsTb, NodeModelUtils.getNode(model).getRootNode(), 0, modelString.length());
		//		assertEquals(ptcTb.toString(), nmsTb.toString());
	}

	public void testLinewrap() throws Exception {
		final String model = "test linewrap float val; int x; double y;";
		final String expected = "test linewrap\nfloat val;\nint x;\ndouble y;";
		assertFormattedPTC(expected, model);
		assertFormattedNM(expected, model, 0, model.length());
		assertEqualTokenStreams(model);
		assertPreserved(model);
	}

	public void testKeepComments() throws Exception {
		// final String model = "test linewrap float val; int x; double y;";
		final String model = "// begincomment \ntest linewrap// comment1\n" + "float val;//comment2\n" + "int x;"
				+ "double y; //yoyoyo!\n// endcomment.";
		final String exp = "// begincomment \ntest linewrap // comment1\n" + "float val; //comment2\n" + "int x;\n"
				+ "double y; //yoyoyo!\n// endcomment.";
		assertFormattedPTC(exp, model);
		assertFormattedNM(exp, model, 0, model.length());
		assertEqualTokenStreams(model);
		assertPreserved(model);
	}

	public void testIndentation() throws Exception {
		final String model = "test indentation { float val; double y; indentation { int x; } }";
		final String expected = "test indentation {\n	float val;\n	double y;\n	indentation {\n		int x;\n	}\n}";
		assertFormattedPTC(expected, model);
		assertFormattedNM(expected, model, 0, model.length());
		assertEqualTokenStreams(model);
		assertPreserved(model);
	}

	public void testAssociation() throws Exception {
		final String model = "test indentation { var = [0,1,2,3,4]; }";
		final String expected = "test indentation {\n	var=[ 0, 1, 2, 3, 4 ];\n}";
		assertFormattedPTC(expected, model);
		assertFormattedNM(expected, model, 0, model.length());
		assertEqualTokenStreams(model);
		assertPreserved(model);
	}

	public void testIndentationAndComments() throws Exception {
		final String model = "test /* xxx */ indentation { float val; // some float\n double /* oo */ y; indentation { // some block\n int x; // xxx\n } } // final comment";
		final String expected = "test /* xxx */ indentation {\n	float val; // some float\n	double /* oo */ y;\n	indentation { // some block\n		int x; // xxx\n	}\n} // final comment";
		assertFormattedPTC(expected, model);
		assertFormattedNM(expected, model, 0, model.length());
		assertEqualTokenStreams(model);
		assertPreserved(model);
	}

	public void testIndentationAndLineWrap() throws Exception {
		final String model = "test indentation { void func(x:int,y:int,s:javalangString, foo:javasqlDate, blupp:mylongtype,  msads:adshdjkhsakdasdkslajdlsask, x:x, a:b, c:d ); }";
		final String expected = "test indentation {\n	void func(x:int, y:int,\n\t		s:javalangString,\n\t		foo:javasqlDate,\n\t		blupp:mylongtype,\n\t		msads:adshdjkhsakdasdkslajdlsask,\n\t		x:x, a:b, c:d);\n}";
		assertFormattedPTC(expected, model);
		assertFormattedNM(expected, model, 0, model.length());
		assertEqualTokenStreams(model);
		assertPreserved(model);
	}

	public void testBetween1() throws Exception {
		final String model = "test indentation { indentation { x x; }; }";
		final String expected = "test indentation {\n	indentation {\n		x x;\n	};\n}";
		assertFormattedPTC(expected, model);
		assertFormattedNM(expected, model, 0, model.length());
		assertEqualTokenStreams(model);
		assertPreserved(model);
	}

	public void testBetween2() throws Exception {
		final String model = "test indentation { indentation { x x; } }";
		final String expected = "test indentation {\n	indentation {\n		x x;\n	}\n}";
		assertFormattedPTC(expected, model);
		assertFormattedNM(expected, model, 0, model.length());
		assertEqualTokenStreams(model);
		assertPreserved(model);
	}

	public void testLinewrapDatatypeRule() throws Exception {
		final String model = "test linewrap fqn ab; fqn xx.yy.zz;";
		final String expected = "test linewrap\nfqn\nab;\nfqn\nxx.yy.zz;";
		assertFormattedPTC(expected, model);
		assertFormattedNM(expected, model, 0, model.length());
		assertEqualTokenStreams(model);
		assertPreserved(model);
	}

	public void testLinewrapDatatypeRulePartial1() throws Exception {
		final String model = "test linewrap fqn ab . xx .yy   .zz;";
		final String expected = "test linewrap fqn ab.xx.yy.zz;";
		assertFormattedNM(expected, model, 22, 2);
	}

	public void testLinewrapDatatypeRulePartial2() throws Exception {
		final String model = "test linewrap fqn ab . xx .yy   .zz;fqn xxx;";
		final String expected = "test linewrap fqn\nab.xx.yy.zz;fqn xxx;";
		assertFormattedNM(expected, model, 15, 10);
	}

	public void testLinewrapDatatypeRulePartial3() throws Exception {
		final String model = "test linewrap fqn ab . xx .yy   .zz;fqn xxx;";
		final String expected = "test linewrap fqn ab.xx.yy.zz;\nfqn xxx;";
		assertFormattedNM(expected, model, 25, 12);
	}

	public void testFormatSegment1() throws Exception {
		final String model = "test\nindentation {\n    indentation  {  x  x  ;  }  }";
		final String expected = "test\nindentation {\n    indentation {\n    	x x;\n    }  }";
		assertFormattedNM(expected, model, 30, 18);
	}

	public void testFormatSegment2() throws Exception {
		final String model = "test       indentation {\n    indentation  {  x  x  ;  }  }";
		//		final String expected = "test\nindentation {\n    indentation {\n    	x x;\n    }  }";
		assertFormattedNM(model, model, 7, 10);
	}

	public void testFormatSegment3() throws Exception {
		final String model = "     test       indentation {\n    indentation  {  x  x  ;  }  }";
		final String expected = "test indentation {\n	indentation {\n		x x;\n	}\n}";
		assertFormattedNM(expected, model, 0, model.length());
	}

	public void testLinewrapDatatypeRuleRef1() throws Exception {
		final String model = "test linewrap fqn ab  .cd .ef; fqnref ab. cd. ef;";
		final String expected = "test linewrap\nfqn\nab.cd.ef;\nfqnref\nab.cd.ef;";
		//		assertFormattedPTC(expected, model);
		assertFormattedNM(expected, model, 0, model.length());
		//		assertEqualTokenStreams(model);
	}

	public void testLinewrapDatatypeRuleRef2() throws Exception {
		final String model = "test linewrap fqn ab.cd.ef; fqnref ab.cd.ef;";
		final String expected = "test linewrap\nfqn\nab.cd.ef;\nfqnref\nab.cd.ef;";
		assertFormattedPTC(expected, model);
		assertFormattedNM(expected, model, 0, model.length());
		assertEqualTokenStreams(model);
		assertPreserved(model);
	}

	public void testLinewrapDatatypeRuleComments() throws Exception {
		final String model = "test linewrap/* 1 */fqn/* 2 */ab.cd.ef/* 3 */;/* 4 */fqnref/* 5 */ab.cd.ef/* 6 */;/* 7 */";
		final String expected = "test linewrap /* 1 */ fqn\n\t/* 2 */\nab.cd.ef /* 3 */; /* 4 */\n\tfqnref /* 5 */ ab.cd.ef\n\t/* 6 */; /* 7 */";
		assertFormattedPTC(expected, model);
		assertFormattedNM(expected, model, 0, model.length());
		assertEqualTokenStreams(model);
		assertPreserved(model);
	}

	public void testEnumeration() throws Exception {
		final String model = "test linewrap enum lit1,lit2,lit3,lit1;";
		final String expected = "test linewrap\nenum lit1 ,\nlit2,\nlit3,\nlit1;";
		assertFormattedPTC(expected, model);
		assertFormattedNM(expected, model, 0, model.length());
		assertEqualTokenStreams(model);
		assertPreserved(model);
	}

	// see https://bugs.eclipse.org/bugs/show_bug.cgi?id=312559
	public void testSuppressedWhitespace() throws Exception {
		final String model = "test linewrap `f%<b>%a` post;";
		final String expected = "test linewrap\n`f%< b >%a` post;";
		assertFormattedPTC(expected, model);
		assertFormattedNM(expected, model, 0, model.length());
		assertEqualTokenStreams(model);
		assertPreserved(model);
	}

	public void testSuppressedLinewrap() throws Exception {
		final String model = "test linewrap\n`foo%abcd%foo%< b\n\t>%abcd%foo%abcd%foo%abcd%"
				+ "foo%abcd%foo%abcd%foo%abcd%foo%abcd%foo%abcd%foo%xx%foo%abcd%foo%abcd%"
				+ "foo%abcd%foo%<\n\tb >%foo%abcd` post;";
		assertFormattedPTC(model, model);
		assertFormattedNM(model, model, 0, model.length());
		assertEqualTokenStreams(model);
		assertPreserved(model);
	}

	public void testLinewrapMin() throws Exception {
		final String model = "test wrapminmax foo bar;";
		final String expected = "test wrapminmax\n\nfoo bar;";
		assertFormattedPTC(expected, model);
		assertFormattedNM(expected, model, 0, model.length());
		assertEqualTokenStreams(model);
		assertPreserved(model);
	}

	public void testLinewrapMax() throws Exception {
		final String model = "test wrapminmax\n\n\n\n\n\n\n\n\n\n\n\n\nfoo bar;";
		final String expected = "test wrapminmax\n\n\n\n\nfoo bar;";
		assertFormattedPTC(expected, model);
		assertFormattedNM(expected, model, 0, model.length());
		assertEqualTokenStreams(model);
		assertPreserved(model);
	}

	public void testLinewrapKeep() throws Exception {
		final String model = "test wrapminmax\n\n\n\nfoo bar;";
		assertFormattedPTC(model, model);
		assertFormattedNM(model, model, 0, model.length());
		assertEqualTokenStreams(model);
		assertPreserved(model);
	}

	public void testLinewrapDefault() throws Exception {
		FormattertestlanguageFactory f = FormattertestlanguageFactory.eINSTANCE;
		TestLinewrapMinMax m = f.createTestLinewrapMinMax();
		Decl d = f.createDecl();
		d.getType().add("xxx");
		d.getName().add("yyy");
		m.getItems().add(d);
		String actual = getSerializer().serialize(m, SaveOptions.newBuilder().format().getOptions());
		final String expected = "test wrapminmax\n\n\nxxx yyy;";
		assertEquals(expected, actual);
	}

	public void testSpace() throws Exception {
		final String model = "test linewrap space foo;";
		final String expected = "test linewrap\nspace     foo;";
		assertFormattedPTC(expected, model);
		assertFormattedNM(expected, model, 0, model.length());
		assertEqualTokenStreams(model);
		assertPreserved(model);
	}

	public void testDatatypeRules() throws Exception {
		final String model = "test linewrap datatypes abc kw1 bcd def kw3;";
		final String expected = "test linewrap\ndatatypes abc\nkw1\nbcd\ndef\nkw3;";
		assertFormattedPTC(expected, model);
		assertFormattedNM(expected, model, 0, model.length());
		assertEqualTokenStreams(model);
		assertPreserved(model);
	}

	public void testWrappingdatatype1() throws Exception {
		final String model = "test wrappingdt foo kw1";
		final String expected = "test wrappingdt foo kw1";
		assertFormattedPTC(expected, model);
		assertFormattedNM(expected, model, 0, model.length());
		assertEqualTokenStreams(model);
		assertPreserved(model);
	}

	public void testWrappingdatatype2() throws Exception {
		final String model = "test wrappingdt foo bar kw1";
		final String expected = "test wrappingdt foo bar kw1";
		assertFormattedPTC(expected, model);
		assertFormattedNM(expected, model, 0, model.length());
		assertEqualTokenStreams(model);
		assertPreserved(model);
	}

	public void testWrappingdatatype3() throws Exception {
		final String model = "test wrappingdt f\nb kw1";
		final String expected = "test wrappingdt f\nb kw1";
		assertFormattedPTC(expected, model);
		assertFormattedNM(expected, model, 0, model.length());
		assertEqualTokenStreams(model);
		assertPreserved(model);
	}

}
