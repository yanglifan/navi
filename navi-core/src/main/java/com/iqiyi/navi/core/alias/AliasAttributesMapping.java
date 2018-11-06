/*
 * Copyright 2018 Yang Lifan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.iqiyi.navi.core.alias;

import com.iqiyi.navi.core.MatcherDefinition;

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
