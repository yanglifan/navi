package com.github.navi.core.alias;

import com.github.navi.core.AliasFor;
import com.github.navi.core.MatcherDefinition;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

/**
 * Internal store the alias attributes in a key:value format.
 *
 * Key -> matcher class simple name + alias label
 * @author Yang Lifan
 */
public class AliasAttributes {
	private Map<String, AliasAttribute> aliasAttributeMap = new HashMap<>();

	public void mergeInto(MatcherDefinition<?> matcherDefinition) {
		String key = KeyBuilder.build(matcherDefinition.getMatcher().annotationType(),
				matcherDefinition.getAliasLabel());
		AliasAttribute aliasAttribute = aliasAttributeMap.get(key);
		if (aliasAttribute != null) {
			matcherDefinition.setAliasAttribute(aliasAttribute);
		}
	}

	public AliasAttribute get(MatcherDefinition<?> matcherDefinition) {
		Annotation matcher = matcherDefinition.getMatcher();
		String label = matcherDefinition.getAliasLabel();
		String key = KeyBuilder.build(matcher.annotationType(), label);
		return aliasAttributeMap.get(key);
	}

	public void add(AliasFor aliasFor, String[] aliasValue) {
		String key = KeyBuilder.build(aliasFor.annotationFor(), aliasFor.label());
		// TODO If existed?

		this.aliasAttributeMap.put(key, new AliasAttribute(aliasFor, aliasValue));
	}
}
