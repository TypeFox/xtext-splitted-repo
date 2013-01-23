/*******************************************************************************
 * Copyright (c) 2012 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.xtext.xbase.scoping.batch;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.xtext.common.types.JvmDeclaredType;
import org.eclipse.xtext.common.types.JvmIdentifiableElement;
import org.eclipse.xtext.common.types.JvmMember;
import org.eclipse.xtext.common.types.JvmType;
import org.eclipse.xtext.common.types.JvmVisibility;
import org.eclipse.xtext.util.Strings;
import org.eclipse.xtext.xbase.typesystem.references.LightweightTypeReference;
import org.eclipse.xtext.xbase.typesystem.util.ContextualVisibilityHelper;

/**
 * @author Sebastian Zarnekow - Initial contribution and API
 */
@NonNullByDefault
public class FeatureScopeSessionWithContext extends AbstractNestedFeatureScopeSession {

	private ContextualVisibilityHelper visibilityHelper;

	public FeatureScopeSessionWithContext(AbstractFeatureScopeSession parent, LightweightTypeReference contextType) {
		super(parent);
		this.visibilityHelper = new ContextualVisibilityHelper(parent, contextType);
	}
	
	@Override
	public boolean isVisible(JvmMember member) {
		return visibilityHelper.isVisible(member);
	}
	
	@Override
	public boolean isVisible(JvmMember member, @Nullable JvmIdentifiableElement receiverFeature) {
		boolean result = isVisible(member);
		if (result && JvmVisibility.PROTECTED == member.getVisibility() && receiverFeature != null) {
			// TODO how does this work with inner types, e.g.
			/*
			 * class A {
			 *   class B {
			 *     {
			 *       A.super.toString
			 *     }
			 *   }
			 * }
			 */
			if (isThisOrSuper(receiverFeature)) {
				return true;
			}
			JvmType contextType = visibilityHelper.getRawContextType();
			if (contextType instanceof JvmDeclaredType) {
				String packageName = ((JvmDeclaredType) contextType).getPackageName();
				String memberPackageName = member.getDeclaringType().getPackageName();
				return Strings.equal(packageName, memberPackageName);
			}
			return false;
		}
		return result;
	}
	
	protected boolean isThisOrSuper(JvmIdentifiableElement receiverFeature) {
		return receiverFeature instanceof JvmType;
	}

}
