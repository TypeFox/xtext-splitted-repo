package generator

import com.google.inject.Binder
import com.google.inject.Guice
import com.google.inject.Module
import org.eclipse.xtext.xdoc.XdocRuntimeModule
import org.eclipse.xtext.xdoc.XdocStandaloneSetup
import xdocgen.DocumentLoad
import com.google.inject.Inject
import bootstrap.Body
import bootstrap.PostProcessor
import java.io.File
import org.eclipse.xtext.xdoc.xdoc.Document
import bootstrap.HtmlExtensions
import org.eclipse.xtext.xdoc.xdoc.ImageRef
import org.eclipse.emf.ecore.util.EcoreUtil
import static extension com.google.common.io.Files.*
import org.eclipse.xtext.nodemodel.util.NodeModelUtils

class Documentation extends AbstractWebsite {
	
	new() {
		doc = docLoader.loadDocument(xdocDocumentRootFolder)
	}
	
	override getStandaloneSetup() {
		new DocumentationSetup
	}
	
	def getXdocDocumentRootFolder() {
		'../docs/org.eclipse.xtext.doc.xdoc/xdoc'
	}

	override path() {
		"documentation.html"
	}
	
	val Document doc
	
	@Inject DocumentLoad docLoader
	@Inject extension Body
	@Inject extension HtmlExtensions
	@Inject PostProcessor processor
	
	override website() {
		processor.postProcess(super.website())
	}
	
	override generateTo(File targetDir) {
		super.generateTo(targetDir)
		copyImages(doc, targetDir)
	}
	
	def copyImages(Document doc, File targetDir) {
		val iter = EcoreUtil::getAllContents(doc.eResource.resourceSet, true)
		iter.filter(typeof(ImageRef)).forEach[
			val source = new File(eResource.URI.trimSegments(1).toFileString, path)
			if (!source.exists)
				throw new IllegalStateException("Referenced Image "+source.canonicalPath+" does not exist in "+eResource.URI.lastSegment+" line "+NodeModelUtils::getNode(it).startLine)
			val target = new File(targetDir, path)
			println(target.canonicalPath)
			
			source.newInputStreamSupplier.copy(target)
		]
	}
	
	override contents() '''
		<!--Container-->
		<div id="header_wrapper" class="container" >
			<div class="inner">
				<div class="container">
					<div class="page-heading"><h1>Documentation</h1></div>
				</div> <!-- /.container -->
			</div> <!-- /inner -->
		</div>
		�doc.menu�
		<div id="page">  
			<div class="inner">
				�doc.body�
			</div>
		</div>
	'''
	
	def menu(Document doc) '''
		<div id="outline-container">
			<ul id="outline">
				�FOR chapter : doc.chapters�
					<li><a href="#�chapter.href�">�chapter.title.toHtml�</a>
					�FOR section : chapter.subSections BEFORE '<ul>' AFTER '</ul>'�
						<li><a href="#�section.href�">�section.title.toHtml�</a></li>
					�ENDFOR�
					</li>
				�ENDFOR�
				�FOR part : doc.parts�
					<li>&nbsp;</li>
					<li>�part.title.toHtml�</li>
					�FOR chapter : part.chapters�
						<li><a href="#�chapter.href�">�chapter.title.toHtml�</a>
						�FOR section : chapter.subSections BEFORE '<ul>' AFTER '</ul>'�
							<li><a href="#�section.href�">�section.title.toHtml�</a></li>
						�ENDFOR�
						</li>
					�ENDFOR�
				�ENDFOR�
			</ul>
		</div>
	'''
	
	def protected getDocument() {
		doc
	}
}

class DocumentationSetup extends XdocStandaloneSetup implements Module {
	
	override createInjector() {
		val module = new XdocRuntimeModule
		Guice::createInjector(module, this)
	}
	
	override configure(Binder binder) {
		
	}
	
}