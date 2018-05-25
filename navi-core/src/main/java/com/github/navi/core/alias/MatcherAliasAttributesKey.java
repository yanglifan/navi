package com.github.navi.core.alias;

import java.lang.annotation.Annotation;
import java.util.Objects;

/**
 * @author Yang Lifan
 */
public class MatcherAliasAttributesKey {
	private Class<? extends Annotation> matcherType;
	private String aliasLabel;

	public MatcherAliasAttributesKey(Class<? extends Annotation> matcherType, String aliasLabel) {
		this.matcherType = matcherType;
		this.aliasLabel = aliasLabel;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		MatcherAliasAttributesKey that = (MatcherAliasAttributesKey) o;
		return Objects.equals(matcherType, that.matcherType) &&
				Objects.equals(aliasLabel, that.aliasLabel);
	}

	@Override
	public int hashCode() {

		return Objects.hash(matcherType, aliasLabel);
	}
}
