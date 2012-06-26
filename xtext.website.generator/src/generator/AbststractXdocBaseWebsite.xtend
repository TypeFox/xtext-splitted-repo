package generator

import org.eclipse.xtext.xdoc.xdoc.Document

abstract class AbstractXdocBaseWebsite extends AbstractWebsite {
	
	def protected Document getDocument()
	
	override javaScriptDocumentStart() '''
		�super.javaScriptDocumentStart()�
		�prettify�
	'''
	
	override protected isPrettyPrint() {
		true
	}
	
	def prettify() { 
		var result = ''''''
		if(document != null){
			val languages = document.langDefs
			if(languages.size > 0)
				// Do not override standard language definitions java & xml in prettify
				result = result + '''
				<script type="text/javascript">
					�FOR lang : languages�
					�IF !(lang.name.equalsIgnoreCase("java") || lang.name.equalsIgnoreCase("xml"))�
					registerLanguage('�FOR keyword : lang.keywords SEPARATOR "|"��keyword��ENDFOR�', '�lang.name.toLowerCase�');
					�ENDIF�
					�ENDFOR�
				</script>
				'''
		}
		return result
	}
}