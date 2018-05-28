package com.github.yanglifan.navi.core.matcher;

import com.github.yanglifan.navi.core.MatchResult;
import com.github.yanglifan.navi.core.MatcherDefinition;
import com.github.yanglifan.navi.core.MatcherProcessor;
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