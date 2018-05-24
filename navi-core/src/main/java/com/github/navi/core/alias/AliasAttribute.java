package com.github.navi.core.alias;

import com.github.navi.core.AliasFor;

import java.lang.annotation.Annotation;

/**
 * @author Yang Lifan
 */
public class AliasAttribute {
	private Class<? extends Annotation> matcherType;
	private String label;
	private String attribute;
	private String[] value;

	public AliasAttribute(AliasFor aliasFor, String[] value) {
		this.matcherType = aliasFor.annotationFor();
		this.label = aliasFor.label();
		this.attribute = aliasFor.attributeFor();
		this.value = value;
	}
}
