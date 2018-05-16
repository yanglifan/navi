package com.github.navi.core;

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
	public MatchResult process(Object request, A matcherAnnotation) {
		String propPath = getPropertyPath(matcherAnnotation);
		List<String> properties = toPropertyList(propPath);

		Object result = request;
		try {
			result = getFinalResult(properties, result);
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			return MatchResult.REJECT;
		}

		return doProcess(result, matcherAnnotation);
	}

	private Object getFinalResult(List<String> properties, Object result) throws IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		for (String prop : properties) {
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

	protected abstract String getPropertyPath(A matcherAnnotation);

	protected abstract MatchResult doProcess(Object request, A matcherAnnotation);
}