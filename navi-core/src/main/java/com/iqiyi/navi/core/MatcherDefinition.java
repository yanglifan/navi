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

package com.iqiyi.navi.core;

import com.iqiyi.navi.core.alias.MatcherAliasAttributesKey;

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
	private Map<String, String[]> aliasAttributes;

	private volatile MatcherProcessor<A> processor;

	MatcherDefinition(A matcher) {
		this.matcher = matcher;
		this.aliasLabel = getAliasLabel(matcher);
	}

	public MatcherAliasAttributesKey key() {
		Class<? extends Annotation> matcherType = matcher.annotationType();
		return new MatcherAliasAttributesKey(matcherType, aliasLabel);
	}

	public String[] getAliasAttribute(String aliasAttributeName) {
		if (aliasAttributes == null) {
			return null;
		}

		return aliasAttributes.get(aliasAttributeName);
	}

	public void setAliasAttributes(Map<String, String[]> aliasAttributes) {
		this.aliasAttributes = aliasAttributes;
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

	MatcherProcessor<A> getProcessor() {
		return processor;
	}

	void setProcessor(MatcherProcessor<A> cachedProcessor) {
		this.processor = cachedProcessor;
	}
}
