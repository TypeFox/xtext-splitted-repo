/*
 * generated by Xtext
 */
package org.eclipse.xtext.linking;

import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.naming.DefaultDeclarativeQualifiedNameProvider;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.scoping.ICaseInsensitivityHelper;

/**
 * Use this class to register components to be used within the IDE.
 */
public class IgnoreCaseNamespacesTestLanguageRuntimeModule extends org.eclipse.xtext.linking.AbstractIgnoreCaseNamespacesTestLanguageRuntimeModule {

	@Override
	public Class<? extends IQualifiedNameProvider> bindIQualifiedNameProvider() {
		return DefaultDeclarativeQualifiedNameProvider.class;
	}
	
	public ICaseInsensitivityHelper bindCaseInsensitivityHelper() {
		return new ICaseInsensitivityHelper() {
			public boolean isIgnoreCase(EReference reference) {
				return true;
			}
		};
	}
	
}