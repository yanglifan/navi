package com.github.navi.core;

import org.apache.commons.beanutils.BeanUtils;

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

	@Override
	public MatchResult process(Object request, A matcherAnnotation) {
		String propPath = getPropertyPath(matcherAnnotation);
		List<String> properties = getProperties(matcherAnnotation, propPath);

		Object finalReq = request;
		try {
			for (String prop : properties) {
				finalReq = BeanUtils.getProperty(finalReq, prop);
			}
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			return MatchResult.REJECT;
		}

		return doProcess(finalReq, matcherAnnotation);
	}

	private List<String> getProperties(A matcherAnnotation, String propPath) {
		if (propPath.contains(PROPERTY_SEPARATOR)) {
			String[] propArray = getPropertyPath(matcherAnnotation).split(".");
			return Arrays.asList(propArray);
		} else {
			return Collections.singletonList(propPath);
		}
	}

	protected abstract String getPropertyPath(A matcherAnnotation);

	protected abstract MatchResult doProcess(Object request, A matcherAnnotation);
}