/*******************************************************************************
 * Copyright (c) 2012 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.xtext.xbase.tests.compiler.output;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IStorage;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.xtext.builder.trace.AbstractTrace;
import org.eclipse.xtext.common.types.JvmTypeReference;
import org.eclipse.xtext.generator.trace.AbstractTraceRegion;
import org.eclipse.xtext.generator.trace.ILocationInResource;
import org.eclipse.xtext.generator.trace.ITrace;
import org.eclipse.xtext.resource.ILocationInFileProvider;
import org.eclipse.xtext.util.TextRegion;
import org.eclipse.xtext.xbase.XExpression;
import org.eclipse.xtext.xbase.compiler.ImportManager;
import org.eclipse.xtext.xbase.compiler.XbaseCompiler;
import org.eclipse.xtext.xbase.compiler.output.TreeAppendable;
import org.eclipse.xtext.xbase.tests.AbstractXbaseTestCase;
import org.eclipse.xtext.xbase.typing.ITypeProvider;
import org.junit.Ignore;
import org.junit.Test;

import com.google.inject.Inject;

/**
 * @author Sebastian Zarnekow - Initial contribution and API
 */
public class CompilerTraceTest extends AbstractXbaseTestCase {
	
	public static class SimpleTrace extends AbstractTrace {

		private final AbstractTraceRegion root;

		public SimpleTrace(AbstractTraceRegion root) {
			this.root = root;
		}
		
		@Override
		protected AbstractTraceRegion doGetRootTraceRegion() {
			return root;
		}
		
		@Override
		@NonNull
		public IProject getLocalProject() {
			throw new UnsupportedOperationException();
		}
		
		@Override
		@NonNull
		public IStorage getLocalStorage() {
			throw new UnsupportedOperationException();
		}
		
	}
	
	@Inject
	private ITypeProvider typeProvider;
	
	@Inject
	private ILocationInFileProvider locationProvider;
	
	@Test 
	public void testNullLiteral() throws Exception {
		assertTrace("\nreturn #null#;", "#null#");
	}
	
	@Test 
	public void testStringLiteral() throws Exception {
		assertTrace("\nreturn #\"fo\\no\"#;", "#'fo\no'#");
	}
	
	@Test
	public void testStringLiteral_02() throws Exception {
		assertTrace("\nreturn \"f#o\\n#o\";", "#'fo\no'#");
	}
	
	@Test
	public void testFeatureCall_01() throws Exception {
		assertTrace( 
				"\nArrayList<Object> _newArrayList = CollectionLiterals.<Object>newA#rray#List();\n" + 
				"return _newArrayList;", 
				"#newArrayList#");
	}
	
	@Test
	public void testFeatureCall_02() throws Exception {
		assertTrace( 
				"\nArrayList<Object> _newArrayList = CollectionLiterals.<Object>newA#rrayList(#);\n" + 
				"return _newArrayList;", 
				"#newArrayList()#");
	}
	
	@Test
	public void testFeatureCall_03() throws Exception {
		assertTrace( 
				"\nArrayList<Object> _newArrayList = CollectionLiterals.<Object>newA#rrayList#();\n" + 
				"return _newArrayList;", 
				"#newArrayList#()");
	}
	
	@Test
	public void testFeatureCall_04() throws Exception {
		assertTrace( 
				"\nArrayList<String> _newArrayList = CollectionLiterals.<String>newA#rrayList#();\n" + 
				"return _newArrayList;", 
				"<String>#newArrayList#()");
	}
	
	@Test
	public void testFeatureCall_05() throws Exception {
		assertTrace( 
				"\nArrayList<String> _newArrayList = CollectionLiterals.<Stri#ng>newArrayList#();\n" + 
				"return _newArrayList;", 
				"#<String>newArrayList()#");
	}
	
	@Test
	public void testFeatureCall_06() throws Exception {
		assertTrace( 
				"\nArrayList<String> _newArrayList = CollectionLiterals.<St#ri#ng>newArrayList();\n" + 
				"return _newArrayList;", 
				"<#String#>newArrayList()");
	}
	
	@Test
	public void testFeatureCall_07() throws Exception {
		assertTrace( 
				"\nArrayList<String> _newArrayList = CollectionLiterals.<String>newA#rrayList(\"a#\");\n" + 
				"return _newArrayList;", 
				"#newArrayList('a')#");
	}
	
	@Test
	public void testFeatureCall_08() throws Exception {
		assertTrace( 
				"\nArrayList<String> _newArrayList = CollectionLiterals.<String>ne#wArrayList(#);\n" + 
				"return _newArrayList;", 
				"<String>#newArrayList()#");
	}
	
	@Test
	public void testFeatureCall_09() throws Exception {
		assertTrace( 
				"\nArrayList<String> _newArrayList = CollectionLiterals.<String>#newArrayList()#;\n" + 
				"return _newArrayList;", 
				"<String>#newArrayList()#");
	}
	
	@Test
	public void testFeatureCall_10() throws Exception {
		assertTrace( 
				"\nArrayList<String> _newArrayList = CollectionLiterals.<String>newArrayList();\n" + 
				"return _ne#wArray#List;", 
				"{ <String>#newArrayList#(); }");
	}
	
	@Test
	public void testFeatureCall_11() throws Exception {
		assertTrace( 
				"\nArrayList<String> _newArrayList = CollectionLiterals.<Stri#ng>newAr#rayList();\n" + 
				"return _newArrayList;", 
				"{ #<String>newArrayList()#; }");
	}
	
	@Test
	public void testFeatureCall_12() throws Exception {
		assertTrace( 
				"\nArrayList<String> _newA#r#rayList = CollectionLiterals.<String>newArrayList();\n" + 
				"return _newArrayList;", 
				"{ #<String>newArrayList()#; }");
	}
	
	@Test
	public void testStaticFeatureCall_01() throws Exception {
		assertTrace( 
				"\nreturn String.CASE_INS#EN#SITIVE_ORDER;", 
				"String::#CASE_INSENSITIVE_ORDER#");
	}
	
	@Test
	public void testStaticFeatureCall_02() throws Exception {
		assertTrace( 
				"\nreturn St#r#ing.CASE_INSENSITIVE_ORDER;", 
				"#String#::CASE_INSENSITIVE_ORDER");
	}
	
	@Test
	public void testStaticFeatureCall_03() throws Exception {
		assertTrace( 
				"\nreturn Str#ing.CASE_INSEN#SITIVE_ORDER;", 
				"#String::CASE_INSENSITIVE_ORDER#");
	}
	
	@Test
	public void testStaticFeatureCall_04() throws Exception {
		assertTrace( 
				"\n" + 
				"List<?> _emptyList = Collections.em#ptyL#ist();\n" + 
				"return _emptyList;", 
				"java::util::Collections::#emptyList#");
	}
	
	@Test
	public void testStaticFeatureCall_05() throws Exception {
		assertTrace( 
				"\n" + 
				"List<Object> _emptyList = Co#lle#ctions.<Object>emptyList();\n" + 
				"return _emptyList;", 
				"#java::util::Collections#::<Object>emptyList");
	}
	
	@Test
	public void testStaticFeatureCall_06() throws Exception {
		assertTrace( 
				"\n" + 
				"List<String> _singletonList = Collections.<S#tri#ng>singletonList(\"value\");\n" + 
				"return _singletonList;", 
				"java::util::Collections::<#String#>singletonList('value')");
	}
	
	@Test
	public void testStaticFeatureCall_07() throws Exception {
		assertTrace( 
				"\n" + 
				"List<String> _singletonList = Co#llections.<Stri#ng>singletonList(\"value\");\n" + 
				"return _singletonList;", 
				"#java::util::Collections::<String>singletonList('value')#");
	}
	
	@Test
	public void testStaticFeatureCall_08() throws Exception {
		assertTrace( 
				"\n" + 
				"List<String> _singletonList = Collections.<Stri#ng>singletonList(\"va#lue\");\n" + 
				"return _singletonList;", 
				"java::util::Collections::#<String>singletonList('value')#");
	}
	
	@Test
	public void testConstructorCall_01() throws Exception {
		assertTrace( 
				"\nString _string = new S#tri#ng();\n" +
				"return _string;", 
				"new #String#");
	}
	
	@Test
	public void testConstructorCall_02() throws Exception {
		assertTrace( 
				"\nString _string = n#e#w String();\n" +
				"return _string;", 
				"#new String#()");
	}
	
	@Test
	public void testConstructorCall_03() throws Exception {
		assertTrace( 
				"\nString _string = new Str#ing(\"Ar#gs\");\n" +
				"return _string;", 
				"#new String('Args')#");
	}
	
	@Test
	public void testConstructorCall_04() throws Exception {
		assertTrace( 
				"\nArrayList<?> _arrayList = new Array#List<#Object>();\n" +
				"return _arrayList;", 
				"new #java.util.ArrayList#");
	}
	
	@Test
	@Ignore("Constructor types loose track to the constructor call")
	public void testConstructorCall_05() throws Exception {
		assertTrace( 
				"\nArrayList<String> _arrayList = new ArrayList<S#trin#g>(5);\n" +
				"return _arrayList;", 
				"new java.util.ArrayList<S#tri#ng>(5)");
	}
	
	@Test
	public void testConstructorCall_06() throws Exception {
		assertTrace( 
				"\nArrayList<String> _arrayList = new Arr#ayLi#st<String>(5);\n" +
				"return _arrayList;", 
				"new #java.util.ArrayList#<String>(5)");
	}
	
	@Test
	public void testConstructorCall_07() throws Exception {
		assertTrace( 
				"\nArrayList<String> _a#rra#yList = new ArrayList<String>();\n" +
				"return _arrayList;", 
				"#new java.util.ArrayList<String>#");
	}
	
	@Test
	public void testConstructorCall_08() throws Exception {
		assertTrace( 
				"\nArrayList<String> _a#rra#yList = new ArrayList<String>(5);\n" +
				"return _arrayList;", 
				"#new java.util.ArrayList<String>(5)#");
	}
	
	@Test
	public void testConstructorCall_09() throws Exception {
		assertTrace( 
				"\nArrayList<?> _arrayList = new Array#List<#Object>(5);\n" +
				"return _arrayList;", 
				"((new #java.util.ArrayList#(5)))");
	}
	
	@Test
	public void testConstructorCall_10() throws Exception {
		assertTrace( 
				"\nArrayList<String> _a#rra#yList = new ArrayList<String>(5);\n" +
				"return _arrayList;", 
				"((#new java.util.ArrayList<String>(5)#))");
	}
	
	private static final Pattern p = Pattern.compile("([^#]*)#([^#]*)#([^#]*)", Pattern.DOTALL);
	
	@SuppressWarnings("null")
	protected void assertTrace(final String javaCodeWithMarker, String xbaseCodeWithMarker) throws Exception {
		xbaseCodeWithMarker = " " + xbaseCodeWithMarker + " ";
		Matcher xbaseMatcher = p.matcher(xbaseCodeWithMarker);
		assertTrue(xbaseMatcher.matches());
		String xbaseGroup1 = xbaseMatcher.group(1);
		String xbaseGroup2 = xbaseMatcher.group(2);
		String xbaseGroup3 = xbaseMatcher.group(3);
		String actualCode = xbaseGroup1 + xbaseGroup2 + xbaseGroup3; 
		XExpression model = expression(actualCode,true);
		TreeAppendable appendable = new TreeAppendable(new ImportManager(true), locationProvider, model, "  ", "\n");
		XbaseCompiler compiler = get(XbaseCompiler.class);
		JvmTypeReference returnType = typeProvider.getCommonReturnType(model, true);
		compiler.compile(model, appendable, returnType);
		String compiledJavaCode = appendable.getContent();
		Matcher javaMatcher = p.matcher(javaCodeWithMarker);
		assertTrue(javaMatcher.matches());
		String javaGroup1 = javaMatcher.group(1);
		String javaGroup2 = javaMatcher.group(2);
		String javaGroup3 = javaMatcher.group(3);
		String actualExpectation = javaGroup1 + javaGroup2 + javaGroup3;
		assertEquals(actualExpectation, compiledJavaCode);
		ITrace trace = new SimpleTrace(appendable.getTraceRegion());
		ILocationInResource location = trace.getBestAssociatedLocation(new TextRegion(javaGroup1.length(), javaGroup2.length()));
		assertEquals(new TextRegion(xbaseGroup1.length(), xbaseGroup2.length()), location.getTextRegion());
	}
}
