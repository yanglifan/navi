package com.github.yanglifan.navi.core.alias;

import com.github.yanglifan.navi.core.MatcherDefinition;

import java.util.HashMap;
import java.util.Map;

/**
 * Store all alias attributes defined in a composite matcher.
 *
 * @author Yang Lifan
 */
public class AliasAttributesMapping {
	private Map<MatcherAliasAttributesKey, Map<String, String[]>> aliasAttributesMap =
			new HashMap<>();

	public void mergeInto(MatcherDefinition<?> matcherDefinition) {
		Map<String, String[]> aliasAttributes = aliasAttributesMap.get(matcherDefinition.key());
		if (aliasAttributes != null) {
			matcherDefinition.setAliasAttributes(aliasAttributes);
		}
	}

	public void add(AliasFor aliasFor, String[] aliasValue) {
		MatcherAliasAttributesKey key =
				new MatcherAliasAttributesKey(aliasFor.annotationFor(), aliasFor.label());
		Map<String, String[]> aliasAttributes =
				this.aliasAttributesMap.computeIfAbsent(key, k -> new HashMap<>());
		aliasAttributes.put(aliasFor.attributeFor(), aliasValue);
	}
}
