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

package com.iqiyi.navi.core.matcher;

import com.iqiyi.navi.core.MatchResult;
import com.iqiyi.navi.core.MatcherDefinition;
import com.iqiyi.navi.core.MatcherProcessor;
import org.apache.commons.beanutils.PropertyUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Yang Lifan
 */
public abstract class OnePropertyMatcherProcessor<A extends Annotation>
		implements MatcherProcessor<A> {

	private static final String PROPERTY_SEPARATOR = ".";
	private static final String PROPERTY_SEPARATOR_REGEX = "\\.";

	@Override
	public MatchResult process(Object request, MatcherDefinition<A> matcherDefinition) {
		String propPath = getPropertyPath(matcherDefinition.getMatcher());
		List<String> properties = toPropertyList(propPath);

		Object valueFromRequest = request;
		try {
			valueFromRequest = getValueFromRequest(properties, valueFromRequest);
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			return MatchResult.reject(e);
		}

		if (valueFromRequest == null) {
			return MatchResult.reject();
		}

		String[] expectValues = getExpectValues(matcherDefinition);

		return doProcess(valueFromRequest, matcherDefinition.getMatcher(), expectValues);
	}

	private Object getValueFromRequest(List<String> properties, Object result)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		for (String prop : properties) {
			// TODO Do null assert
			result = PropertyUtils.getProperty(result, prop);
		}
		return result;
	}

	private List<String> toPropertyList(String propPath) {
		if (propPath.contains(PROPERTY_SEPARATOR)) {
			String[] propArray = propPath.split(PROPERTY_SEPARATOR_REGEX);
			return Arrays.asList(propArray);
		} else {
			return Collections.singletonList(propPath);
		}
	}

	private String[] getExpectValues(MatcherDefinition<A> matcherDefinition) {
		String[] aliasedValues = matcherDefinition.getAliasAttribute(aliasName());
		if (aliasedValues == null) {
			return getMatcherValue(matcherDefinition.getMatcher());
		} else {
			return aliasedValues;
		}
	}

	protected abstract String getPropertyPath(A matcherAnnotation);

	protected abstract MatchResult doProcess(Object request, A matcher, String[] expectValues);

	protected abstract String[] getMatcherValue(A matcher);

	protected abstract String aliasName();
}