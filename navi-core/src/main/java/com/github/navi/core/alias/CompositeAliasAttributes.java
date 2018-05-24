package com.github.navi.core.alias;

import com.github.navi.core.AliasFor;
import com.github.navi.core.MatcherDefinition;

import java.util.HashMap;
import java.util.Map;

/**
 * Store all alias attributes defined in a composite matcher.
 *
 * @author Yang Lifan
 */
public class CompositeAliasAttributes {
	private Map<String, AliasAttributes> aliasAttributeMap = new HashMap<>();

	public void mergeInto(MatcherDefinition<?> matcherDefinition) {
		String key = KeyBuilder.build(matcherDefinition.getMatcher().annotationType(),
				matcherDefinition.getAliasLabel());
		AliasAttributes aliasAttributes = aliasAttributeMap.get(key);
		if (aliasAttributes != null) {
			matcherDefinition.setAliasAttributes(aliasAttributes);
		}
	}

	public void add(AliasFor aliasFor, String[] aliasValue) {
		String key = KeyBuilder.build(aliasFor.annotationFor(), aliasFor.label());
		AliasAttributes aliasAttributes =
				this.aliasAttributeMap.computeIfAbsent(key,
						k -> new AliasAttributes());
		aliasAttributes.addAttribute(aliasFor.attributeFor(), aliasValue);
	}
}
