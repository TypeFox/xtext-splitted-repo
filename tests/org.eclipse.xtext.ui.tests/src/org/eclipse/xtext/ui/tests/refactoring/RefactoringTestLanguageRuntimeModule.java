/*
 * generated by Xtext
 */
package org.eclipse.xtext.ui.tests.refactoring;

import org.eclipse.xtext.resource.IFragmentProvider;
import org.eclipse.xtext.ui.tests.refactoring.resource.RefactoringTestLanguageFragmentProvider;

/**
 * Use this class to register components to be used at runtime / without the Equinox extension registry.
 */
public class RefactoringTestLanguageRuntimeModule extends org.eclipse.xtext.ui.tests.refactoring.AbstractRefactoringTestLanguageRuntimeModule {

	@Override
	public Class<? extends IFragmentProvider> bindIFragmentProvider() {
		return RefactoringTestLanguageFragmentProvider.class;
	}

}