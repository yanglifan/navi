package com.github.navi.core;

import com.github.navi.core.alias.AliasAttributes;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Yang Lifan
 */
public class MatcherDefinition<A extends Annotation> {
	private static final String ALIAS_LABEL = "aliasLabel";

	private A matcher;
	private String aliasLabel;
	private AliasAttributes aliasAttributes;

	MatcherDefinition(A matcher) {
		this.matcher = matcher;
		this.aliasLabel = getAliasLabel(matcher);
	}

	public String[] getAliasAttribute(String aliasAttributeName) {
		if (aliasAttributes == null) {
			return null;
		}

		return aliasAttributes.getValues().get(aliasAttributeName);
	}

	public void setAliasAttributes(AliasAttributes aliasAttributes) {
		this.aliasAttributes = aliasAttributes;
	}

	public String getAliasLabel() {
		return aliasLabel;
	}

	private String getAliasLabel(A matcher) {
		try {
			Class<? extends Annotation> annotationType = matcher.annotationType();
			Method method = annotationType.getMethod(ALIAS_LABEL);
			return (String) method.invoke(matcher);
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException
				| ClassCastException e) {
			return "";
		}
	}

	public A getMatcher() {
		return matcher;
	}
}
