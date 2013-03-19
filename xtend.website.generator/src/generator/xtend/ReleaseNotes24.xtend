package generator.xtend

class ReleaseNotes24 extends AbstractXtendWebsite {

	override path() {
		"releasenotes_2_4.html"
	}
	
	override protected isPrettyPrint() {
		true
	}
	
	override contents() '''
		�headline('Release Notes - Version 2.4')�
		<div id="page">
	  <div class="inner">
	    <div class="container clearfix">
	    <h2>Xtend 2.4 Release Notes</h2>
	    <hr>
	    <div class="span1">&nbsp;</div>
	      <div class="span9">
	        <p>
	          This release includes almost <a href="https://bugs.eclipse.org/bugs/buglist.cgi?o5=anywordssubstr&f1=OP&f0=OP&resolution=FIXED&classification=Modeling&classification=Tools&f4=CP&v5=kepler&query_format=advanced&j1=OR&f3=CP&bug_status=RESOLVED&bug_status=VERIFIED&f5=flagtypes.name&component=Backlog&component=Common&component=Core&component=Releng&component=Website&component=Xtext&component=Xtext%20Backlog&product=TMF&product=Xtend&list_id=4768360">300 bug fixes and new features</a>.
	        </p>
	        <h2>Table of contents</h2>
	        <ul>
	          <li><a href="#android_development">Android Support</a>
	          	<ul>
	          		<li><a href="#android_debugging">Debugging</a>
	          		<li><a href="#android_maven">Maven Archetype</a>
	          	</ul>
	          <li><a href="#new_language_features">New Language Features</a>
	            <ul>
	            <li><a href="#active_annotations">Active Annotations</a>
	            <li><a href="#collection_literals">Collection Literals and Array Access</a>
	            <li><a href="#extension_values">Extension Values</a>
	            <li><a href="#interfaces_enums_annotations">Interfaces, Enumerations and Annotations</a>
	            <li><a href="#sam_types">SAM Type Conversion</a>
	            <li><a href="#new_operators">New Operators</a>
	            </ul>
	          </li>
	          <li><a href="#new_ide_features">New Editor Features</a>
	            <ul>
	            <li><a href="#organize_imports">Organize Imports</a>
	            <li><a href="#extract_method">Extract Method and Extract Local Variable</a>
	            <li><a href="#suppression_followup">Supression of Follow-Up Errors</a>
	            <li><a href="#optional_errors">Optional Errors and Warnings</a>
	            <li><a href="#quickfixes">New Quickfixes</a>
	            <li><a href="#content_assist">Improved Content Assist</a>
	            <li><a href="#formatter">Code Formatter</a>
	            <li><a href="#javadoc">JavaDoc</a>
	            <li><a href="#copy_qualifiedname">Copy Qualified Name</a>
	            </ul>
	          </li>
	        </ul>
	        <section id="android_development" style="padding-top: 68px; margin-top: -68px;">
	          <h2>Android Support</h2>
	          <p>As Xtend compiles to Java and does not have its own standard library but relies on the JDK, it is a 
	          great choice for Android application development. With version 2.4 the Android support has been 
	          further improved.
	          </p>
	          <h3 id="android_debugging">Debugging</h3>
	            <p>
	            Previously Xtend supported debugging through JSR-45 only, which works great in standard 
	            JVMs as it allows you to switch between Xtend and generated Java source during a debug 
	            session. The Dalvik VM unfortunately does not support JSR-45. Now you can configure 
	            the compiler to use either JSR-45 or install the debug information such that Android 
	            debugging simply works.
	            </p>
	          <h3 id="android_maven">Maven Archetype</h3>
	            <p>
	            There is also a Maven archetype to set up a working Android project easily. If you 
	            have installed Maven and the Android SDK the you only need the following command to 
	            get started:
	            </p>
	<pre class="prettyprint linenums">
	mvn archetype:generate -DarchetypeGroupId=org.eclipse.xtend \
	  -DarchetypeArtifactId=xtend-android-archetype \
	  -DarchetypeCatalog=http://build.eclipse.org/common/xtend/maven/
	</pre>
	        <p>
	        This will provide you with a fully configured Android/Xtend project.
	        </p>
	        </section>
	        <section id="new_language_features" style="padding-top: 68px; margin-top: -68px;">
	        <h2>New Language Features</h2>
	        <p>
	        The following new features have been added to the Xtend language.
	        </p>
	        <h3 id="active_annotations" >Active Annotations (Provisional API)</h3>
���	      <p>
���	      Java developers are often forced to write repetitive code, because the language 
���	      itself is not flexible enough. For instance, design patterns are not much more 
���	      than the documentation of a single concept and how you model that concept with 
���	      classes, interfaces and their members.
���	      </p>
���	      <p>
���	      To mitigate the effort of writing and maintainig redundant boiler plate code, 
���	      developers often reach out to extreme techniques such as byte code manipulation 
���	      (Spring Roo and Play! framework) or Java compiler hacking (Lombok). These 
���	      workarounds are not only complex but often cause other problems during development, 
���	      such as bad IDE integration and debugging issues. Other solutions to this kind 
���	      of problem include code generators and IDE wizards.
���	      </p>
	      <p>
	      Xtend's <em>Active Annotations</em> allow developers to use a simple annotation to tell 
	      that a certain pattern should be expanded to a specific piece of Java code. A simple example would be a JavaBeans 
	      property supporting the Observer pattern. Here you need a getter and a 
	      setter method for each field and also an observer list and the proper code to notify 
	      them about changes. In many software systems you have hundreds of these properties. 
	      Active Annotation allow you to define and automate the implementation of such patterns 
	      and idioms at a single point and 
	      let the compiler expand it on the fly. And all this based on lightweight, custom libraries. 
	      You do no longer have to write nor 
	      read the boiler plate code anymore. <a href="documentation.html#activeAnnotations">Read more...</a>
	      </p>
	      
	      <p>
	      Example: To compile the Xtend code on the left to the Java code on the right
	      </p>
	      <table class="table table-bordered table-condensed">
	      <tr>
	         <th>Xtend</th>
	         <th>Java</th>
	      </tr>
	      <tr>
	        <td>
	<pre class="prettyprint lang-xtend linenums">
	@Observable
	class Person {
	   String name
	} 
	</pre>
	    </td>
	        <td>
	<pre class="prettyprint lang-java linenums">
	public class Person {
	  private String name;
	  
	  public String getName() {
	    return this.name;
	  }
	  
	  public void setName(final String name) {
	    String _oldValue = this.name;
	    this.name = name;
	    _propertyChangeSupport.firePropertyChange(
	      "name", _oldValue, name);
	    
	  }
	  
	  private PropertyChangeSupport _propertyChangeSupport 
	      = new PropertyChangeSupport(this);
	  
	  // method addPropertyChangeListener
	  // method removePropertyChangeListener
	}
	</pre>
	        </td>
	      </tr>
	      </table>
	      <p>
	      you have to implement a simple active annotation <code class="prettyprint lang-xtend">@Observable</code> as
	      </p>
	          
	<pre class="prettyprint lang-xtend linenums">
	@Active(typeof(ObservableCompilationParticipant))
	annotation Observable {
	}
	
	class ObservableCompilationParticipant extends AbstractClassProcessor {
	
	  override doTransform(MutableClassDeclaration clazz, extension TransformationContext context) {
	    for (f : clazz.declaredFields) {
	      val fieldName = f.simpleName
	      val fieldType = f.type
	
	      clazz.addMethod('get' + fieldName.toFirstUpper) [
	        returnType = fieldType
	        body = ['��''return this.&laquo;fieldName&raquo;;''��']
	      ]
	
	      clazz.addMethod('set' + fieldName.toFirstUpper) [
	        addParameter(fieldName, fieldType)
	        body = ['��''
	          &laquo;fieldType&raquo; _oldValue = this.&laquo;fieldName&raquo;;
	          this.&laquo;fieldName&raquo; = &laquo;fieldName&raquo;;
	          _propertyChangeSupport.firePropertyChange("&laquo;fieldName&raquo;", _oldValue, 
	            &laquo;fieldName&raquo;);
	        ''��']
	      ]
	    }
	    
	    val changeSupportType = typeof(PropertyChangeSupport).newTypeReference
	    clazz.addField('_propertyChangeSupport') [
	      type = changeSupportType
	      initializer = [''��'new &laquo;toJavaCode(changeSupportType)&raquo;(this)''��']
	    ]
	    // add method addPropertyChangeListener
	    // add method removePropertyChangeListener
	  }
	}
	</pre>
	        <h3 id="collection_literals">Collection Literals and Arrays</h3>
	        <p>
	        Xtend now has literals for unmodifiable collections.
	        </p>
	<pre class="prettyprint lang-xtend linenums">
	val listOfWords = #["Hello", "Xtend"]
	val setOfWords  = #{"Hello", "Xtend"}
	val mapOfWords  = #{1->"Hello", 2->"Xtend"}
	</pre> 
	      <p>
	      Collections created with a literal are immutable. The list literal can be used to natively create arrays, too. 
	      If the target type is an array, it will compile to an array initializer.
	      </p>
	<pre class="prettyprint lang-xtend linenums">
	val String[] arrayOfWords = #["Hello", "Xtend"]
	</pre> 
	      <p>
	      In addition to literals for arrays you can now also easily access and modify arrays 
	      as well as create empty arrays of any size.
	      </p>
	<pre class="prettyprint lang-xtend linenums">
	val String[] arrayOfWords = newArrayOfSize(2)
	arrayOfWords.set(0, 'Hello')
	arrayOfWords.set(1, 'Xtend')
	</pre> 
	      <h3 id="interfaces_enums_annotations">Interfaces, Enums and Annotations</h3>
	      <p>
	      Interfaces, enumerations and annotation types can now be declared directly in Xtend. 
	      </p>
	<pre class="prettyprint lang-xtend linenums">
	interface Container<T> {
	  def T findChild((T)=>boolean matcher)
	}
	
	enum Color {
	  RED, GREEN, BLUE
	}
	
	@Retention(RetentionPolicy::RUNTIME)
	@Target(ElementType::TYPE)
	annotation DependsOn {
	  Class&lt;? extends Target&gt; value
	  val version = "2.4.0" // type 'String' inferred 
	}
	</pre>
	
	      <h3 id="extension_values">Extension Values</h3>
	      <p>
	      Extension methods allow to add new methods to existing types without modifying them. 
	      Consider the omnipresent class <code class="prettyprint lang-java">java.lang.String</code>.
	      If you have to parse a string to a number, you could always write
	      </p>
	<pre class="prettyprint lang-java linenums">
	Integer::parseInt('42')
	</pre>
	      <p>but what you actually think of is</p>
	<pre class="prettyprint lang-xtend linenums">
	'42'.parseInt
	</pre>
	      <p>To make that possible, you simply import the class <code class="prettyprint lang-java">Integer</code> as a static extension:</p>
	<pre class="prettyprint lang-xtend linenums">
	import static extension java.lang.Integer.*
	</pre>
	      <p>This enables to pass the base of the number as an argument, too:</p>
	<pre class="prettyprint lang-xtend linenums">
	'2A'.parseInt(16)
	</pre>
	      Extension methods are available in C# as well, but Xtend can do better. In Xtend, fields, 
	      parameters and local variables can provide extensions, too. In fact, not all
	      the useful functionality is available statically: if you now want to format the
	      parsed number to present it in the UI according to a localized pattern
	      from a <code class="prettyprint lang-java">ResourceBundle</code>,
	      you can simply use a <code class="prettyprint lang-java">Formatter</code> and 
	      write readable code that's easy to grasp:
	      </p>
	<pre class="prettyprint lang-xtend linenums">
	def getFormattedNumber(String number, extension ResourceBundle bundle) {
	  val localized = 'number_pattern'.getString
	  extension val formatter = new Formatter
	  localized.format(number.parseInt)
	}
	</pre>
	      <p>The above is equivalent to the slightly more bulky:</p>
	<pre class="prettyprint lang-xtend linenums">
	def getFormattedNumber(String number, ResourceBundle bundle) {
	  val localized = bundle.getString('number_pattern')
	  val formatter = new Formatter
	  formatter.format(localized, Integer::valueOf(number))
	}
	</pre>
	      <p>
	      Extensions are a handy mechanism to add layer specific 
	      functionality to classes: You could add UI related 
	      methods to your domain models via extensions just where they are needed, 
	      without introducing a UI dependency for other clients. <a href="documentation.html#extensionMethods">Read more...</a>
	      </p>
	
	      <h3 id="sam_types">SAM Type Conversion</h3>
	      <p>
	      Lambda expressions now work with abstract classes with a single abstract method 
	      (SAM types). For example, the <code class="prettyprint lang-java">AbstractIterator</code> 
	      from the Guava library has a single abstract method 
	      <code class="prettyprint lang-java">computeNext()</code>. A lambda can be used to implement that:
	      </p>
	<pre class="prettyprint lang-xtend linenums">
	val AbstractIterator&lt;Double&gt; infiniteRandomNumbers = [| Math::random]
	</pre>
	
	      <h3 id="new_operators">New Operators</h3>
	      <p>
	      Some new operators have been added. In addition to the usual <code>==</code> and <code>!=</code>
	      operators which map to <code>Object.equals(Object)</code>, the operators <code>===</code> and
	      <code>!==</code> respectively can be used to test for identity equality.
	<pre class="prettyprint lang-xtend linenums">
	if (myObject === otherObject) {
	  println("same objects")
	}
	</pre>
	      <p>
	        Also new exclusive range operators have been introduced. In order to iterate over a list and
	        work with the index you can write:</p>
	<pre class="prettyprint lang-xtend linenums">
	for (idx : 0 ..&lt; list.size) {
	  println("("+idx+") "+list.get(idx))
	}
	</pre>
	      <p>
	        Or if you want to iterate backwards :</p>
	<pre class="prettyprint lang-xtend linenums">
	for (idx : list.size &gt;.. 0) {
	  println("("+idx+") "+list.get(idx))
	}
	</pre>
	        </section>
	        <section id="new_ide_features" style="padding-top: 68px; margin-top: -68px;">
	        <h2>New IDE Features</h2>
			<p>Being an Eclipse project Xtend has always been designed with IDE integration in 
			mind. We are proud to announce that the editing support is now almost on par with 
			Java's and in some cases already even better. A user recently wrote in the newsgroup:
			</p>
			<blockquote>
	   		Tooling for Xtend is unlike any other language for the JVM after Java. The IDE support 
	   		is first class. It will take years for some languages to catch up. Some never will.
	   		</blockquote>
	   		<p>
			The following new IDE features improve the editing experience significantly:
			</p>
			<dl>
				<h3 id="organize_imports">Organize Imports</h3>
				<p>
				With the new release we have overhauled the <em>Organize imports</em> action. It 
				processes all kinds of imports, asks to resolve conflicts, and shortens qualified names
				automatically.
				</p>
				<img class="image_between_p" src="images/releasenotes/organize_imports.png"/>
	
				<h3 id="extract_method">Extract Method and Extract Local Variable</h3>
				<p>
				New refactorings have been added. You can now extract code into a new local variable
				</p>
				<img class="image_between_p" src="images/releasenotes/extract_local_variable.png"/>
				<p>
				or into a new method.
				</p>
				<img class="image_between_p" src="images/releasenotes/extract_method_refactoring.png"/>
	
				<h3 id="suppression_followup">Supression of Follow-Up Errors</h3>
				<p>
				Follow-up error markers are now suppressed and errors in general are much more local, 
				so it is very easy spot the problem immediately. In addition, the compiler generates 
				as much valid Java code as possible in order not to break dependent resources on changes.
				</p>
				<img class="image_between_p" src="images/releasenotes/follow_up_errors.png"/>
	
				<h3 id="optional_errors">Optional Errors and Warnings</h3>
				<p>The severity of optional compiler errors can be configured globally as well as 
				individually for a single project.</p>
				<img class="image_between_p" src="images/releasenotes/issue_severities.png"/>
	
				<h3 id="quickfixes">More Quickfixes</h3>
				<p>
				Xtend now offers to create missing elements. Fields, methods, extension methods and types 
				can be added with a quickfix, be it in Xtend or in Java classes.
				</p>
				<img class="image_between_p" src="images/releasenotes/quickfixes.png"/> 
			
				<h3 id="content_assist">Improved Content Assist</h3>
				<p>
				The content assist has become much smarter. It now proposes lambda brackets if the argument 
				takes a single function and it gives you hints on the possible parameter types when you 
				are working with overloaded methods.
				</p>
				<img class="image_between_p" src="images/releasenotes/content_assist.png"/>
			
				<h3 id="formatter">Formatter</h3>
				<p>
				A configurable formatter which pretty prints and indents code idiomatically is now available.
				</p>
				<img class="image_between_p" src="images/releasenotes/formatter.png"/>
				<img class="image_between_p" src="images/releasenotes/formatter_preferences.png"/>
				
				<h3 id="javadoc">JavaDoc</h3>
				<p>
				An Xtend editor now has validation and content assist within JavaDoc comments.
				</p>
				<img class="image_between_p" src="images/releasenotes/javadoc_content_assist.png"/>
				
				<h3 id="copy_qualifiedname">Copy Qualified Name</h3>
				<p>
				You can use <em>Copy qualifed name</em> in the editor and the outline view to copy the name
				of types, fields and methods into the clipboard.
				</p>
				<img class="image_between_p" src="images/releasenotes/copy_qualified_name.png"/>
			</dl>
			</section>
	
	        
	      </div>  
	    <div class="span1">&nbsp;</div>  
	  </div>
	</div>
	'''
	
}