package com.github.navi.core;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Yang Lifan
 */
public class MatcherDefinition<A extends Annotation> {
	private A matcher;
	private Map<String, String[]> aliasedAttributes;

	public MatcherDefinition(A matcher) {
		this.matcher = matcher;
		aliasedAttributes = new HashMap<>();
	}

	public A getMatcher() {
		return matcher;
	}

	public Map<String, String[]> getAliasedAttributes() {
		return aliasedAttributes;
	}
}
