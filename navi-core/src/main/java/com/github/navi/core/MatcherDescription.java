package com.github.navi.core;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * @author Yang Lifan
 */
public class MatcherDescription<A extends Annotation> {
	private A matcher;
	private Map<String, String> aliasedAttributes;

	public MatcherDescription(A matcher) {
		this(matcher, null);
	}

	MatcherDescription(A matcher, Map<String, String> aliasedAttributes) {
		this.matcher = matcher;
		this.aliasedAttributes = aliasedAttributes;
	}

	public A getMatcher() {
		return matcher;
	}

	public Map<String, String> getAliasedAttributes() {
		return aliasedAttributes;
	}
}
