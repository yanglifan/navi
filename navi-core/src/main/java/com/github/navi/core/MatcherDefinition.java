package com.github.navi.core;

import com.github.navi.core.alias.AliasAttributes;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Represent a matcher, contains the matcher annotation and other metadata, like alias attributes.
 * <p>
 * Alias attributes are not necessary, only when a basic matcher defined in a composite matcher,
 * then this basic matcher may contain some alias attributes.
 *
 * @author Yang Lifan
 */
public class MatcherDefinition<A extends Annotation> {
	private static final String ALIAS_LABEL = "aliasLabel";

	private A matcher;
	private String aliasLabel;
	private AliasAttributes aliasAttributes;
	private Map<String, String[]> aliasAttributes2;

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
