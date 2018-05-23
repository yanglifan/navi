package com.github.navi.core.matcher;

import com.github.navi.core.MatchResult;
import com.github.navi.core.MatcherDescription;
import com.github.navi.core.MatcherProcessor;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Yang Lifan
 */
public abstract class OnePropertyMatcherProcessor<A extends Annotation>
		implements MatcherProcessor<A> {

	private static final String PROPERTY_SEPARATOR = ".";
	private static final String PROPERTY_SEPARATOR_REGEX = "\\.";

	@Override
	public MatchResult process(Object request, MatcherDescription<A> matcherDescription) {
		String propPath = getPropertyPath(matcherDescription.getMatcher());
		List<String> properties = toPropertyList(propPath);

		Object valueFromRequest = request;
		try {
			valueFromRequest = getValueFromRequest(properties, valueFromRequest);
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			return MatchResult.REJECT;
		}

		String[] expectValues = getExpectValues(matcherDescription);

		return doProcess(valueFromRequest, matcherDescription.getMatcher(), expectValues);
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

	private String[] getExpectValues(MatcherDescription<A> matcherDescription) {
		String[] aliasedValues = getAliasedValue(matcherDescription.getAliasedAttributes());
		if (aliasedValues == null) {
			return getMatcherValue(matcherDescription.getMatcher());
		} else {
			return aliasedValues;
		}
	}

	private String[] getAliasedValue(Map<String, String> aliasedAttributes) {
		String aliasValue = aliasedAttributes.get(aliasName());
		if (StringUtils.isEmpty(aliasValue)) {
			return null;
		}

		return new String[]{aliasValue};
	}

	protected abstract String getPropertyPath(A matcherAnnotation);

	protected abstract MatchResult doProcess(Object request, A matcher, String[] expectValues);

	protected abstract String[] getMatcherValue(A matcher);

	protected abstract String aliasName();
}