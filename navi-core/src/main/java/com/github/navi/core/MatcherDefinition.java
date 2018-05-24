package com.github.navi.core;

import com.github.navi.core.alias.AliasAttribute;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Yang Lifan
 */
public class MatcherDefinition<A extends Annotation> {
	private static final String ALIAS_LABEL = "aliasLabel";

	private A matcher;
	private String aliasLabel;
	private Map<String, String[]> aliasedAttributes;
	private AliasAttribute aliasAttribute;

	public MatcherDefinition(A matcher) {
		this.matcher = matcher;
		this.aliasLabel = getAliasLabel(matcher);
		aliasedAttributes = new HashMap<>();
	}

	public AliasAttribute getAliasAttribute() {
		return aliasAttribute;
	}

	public void setAliasAttribute(AliasAttribute aliasAttribute) {
		this.aliasAttribute = aliasAttribute;
	}

	public String getAliasLabel() {
		return aliasLabel;
	}

	private String getAliasLabel(A matcher) {
		try {
			Method method = matcher.annotationType().getClass().getMethod(ALIAS_LABEL);
			return (String) method.invoke(matcher);
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException
				| ClassCastException e) {
			return "";
		}
	}

	public A getMatcher() {
		return matcher;
	}

	public Map<String, String[]> getAliasedAttributes() {
		return aliasedAttributes;
	}
}
