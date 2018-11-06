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

import com.iqiyi.navi.core.alias.AliasAttributesMapping;
import com.iqiyi.navi.core.alias.AliasFor;
import com.iqiyi.navi.core.exception.InvalidMatcherException;
import com.iqiyi.navi.core.exception.SelectPolicyCreationException;
import com.iqiyi.navi.core.policy.DefaultRejectPolicy;
import com.iqiyi.navi.core.policy.FirstMatchSelectPolicy;
import com.iqiyi.navi.core.util.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author Yang Lifan
 */
public abstract class AbstractSelector implements Selector {
	protected Class<? extends SelectPolicy> defaultSelectPolicyClass;

	private RejectPolicy rejectPolicy = new DefaultRejectPolicy();

	private ConcurrentMap<Object, List<MatcherDefinition<?>>> matcherDefinitionsMap =
			new ConcurrentHashMap<>();

	public AbstractSelector() {
		this(FirstMatchSelectPolicy.class);
	}

	public AbstractSelector(Class<? extends SelectPolicy> defaultSelectPolicyClass) {
		this.defaultSelectPolicyClass = defaultSelectPolicyClass;
	}

	public <T> T select(Object request, Class<T> candidateType) {
		return this.select(request, candidateType, createSelectPolicy());
	}

	@Override
	public <T> T select(Object request, Class<T> candidateType, SelectPolicy<T> selectPolicy) {
		Iterable<T> candidates = findCandidatesByType(candidateType);

		for (T candidate : candidates) {
			T result = doMatch(request, candidate, selectPolicy);

			/*
			 * To fit the quick match mode: If there is a result, return it. With other modes,
			 * doMatch method will return null, get the result via selectPolicy.getResult().
			 */
			if (result != null) {
				return result;
			}
		}

		return selectPolicy.getResult();
	}

	private <T> T doMatch(Object request, T candidate, SelectPolicy<T> selectPolicy) {
		List<MatcherDefinition<?>> matcherDefinitions = readMatcherDefinitions(candidate);

		if (matcherDefinitions.isEmpty()) {
			return null;
		}

		for (MatcherDefinition<?> matcherDefinition : matcherDefinitions) {
			MatchResult matchResult = doMatch(request, matcherDefinition);

			if (matchResult == null) {
				continue;
			}

			if (matchResult.getType() == MatchResult.MatchType.REJECT) {
				rejectPolicy.reject(candidate, matchResult);
				return null;
			}

			selectPolicy.addMatchResult(matchResult);
		}

		return selectPolicy.addCandidateAndGetResult(candidate);
	}

	private List<MatcherDefinition<?>> readMatcherDefinitions(Object candidate) {
		return matcherDefinitionsMap.computeIfAbsent(candidate, this::doReadMatcherDefinitions);
	}

	private List<MatcherDefinition<?>> doReadMatcherDefinitions(Object candidate) {
		List<MatcherDefinition<?>> definitions = new ArrayList<>();
		readMatcherDefinitions(candidate, definitions);
		return definitions;
	}

	private void readMatcherDefinitions(Object candidate,
			List<MatcherDefinition<?>> matcherDefinitions) {
		List<Annotation> annotations = getTopLevelMatcherAnnotation(candidate);

		for (Annotation annotation : annotations) {
			readDefinitionFromMatcher(annotation, matcherDefinitions);
			readDefinitionFromMatcherContainer(annotation, matcherDefinitions);
			readDefinitionFromCompositeMatcher(annotation, matcherDefinitions);
		}
	}

	private void readDefinitionFromMatcherContainer(Annotation annotation,
			List<MatcherDefinition<?>> matcherDefinitions) {
		if (!AnnotationUtils.annotatedBy(annotation, MatcherContainer.class)) {
			return;
		}

		readMatcherDefinitions(annotation, matcherDefinitions);
	}

	private List<Annotation> getTopLevelMatcherAnnotation(Object candidate) {
		Annotation[] annotationArray;
		if (candidate instanceof Annotation) {
			annotationArray = getAnnotationsFromAnnotation((Annotation) candidate);
		} else {
			annotationArray = candidate.getClass().getAnnotations();
		}

		return Arrays.stream(annotationArray)
				.filter(isMatcher())
				.collect(Collectors.toList());
	}

	private Annotation[] getAnnotationsFromAnnotation(Annotation annotation) {
		if (AnnotationUtils.annotatedBy(annotation, MatcherContainer.class)) {
			try {
				Method valueMethod = annotation.getClass().getMethod("value");
				return (Annotation[]) valueMethod.invoke(annotation);
			} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
				throw new InvalidMatcherException(annotation + " is not a matcher container");
			}
		} else {
			return annotation.annotationType().getAnnotations();
		}
	}

	private Predicate<Annotation> isMatcher() {
		return a -> AnnotationUtils.annotatedBy(a, MatcherType.class)
				|| AnnotationUtils.annotatedBy(a, CompositeMatcherType.class)
				|| AnnotationUtils.annotatedBy(a, MatcherContainer.class);
	}

	private void readDefinitionFromCompositeMatcher(Annotation annotation,
			List<MatcherDefinition<?>> matcherDefinitions) {
		if (notCompositeMatcher(annotation)) {
			return;
		}

		readMatcherDefinitions(annotation, matcherDefinitions);

		AliasAttributesMapping aliasAttributesMapping = readAliasedAttributes(annotation);

		mergeAliasAttributes(matcherDefinitions, aliasAttributesMapping);
	}

	private boolean notCompositeMatcher(Annotation annotation) {
		return !AnnotationUtils.annotatedBy(annotation, CompositeMatcherType.class);
	}

	private void mergeAliasAttributes(List<MatcherDefinition<?>> matcherDefinitions,
			AliasAttributesMapping aliasAttributesMapping) {
		for (MatcherDefinition<?> matcherDefinition : matcherDefinitions) {
			aliasAttributesMapping.mergeInto(matcherDefinition);
		}
	}

	private AliasAttributesMapping readAliasedAttributes(Annotation compositeMatcher) {
		AliasAttributesMapping aliasAttributesMapping = new AliasAttributesMapping();
		Method[] methods = compositeMatcher.annotationType().getMethods();
		for (Method aliasAttrMethod : methods) {
			AliasFor aliasFor = aliasAttrMethod.getAnnotation(AliasFor.class);
			if (aliasFor == null) {
				continue;
			}

			String[] aliasValue = getAliasValue(compositeMatcher, aliasAttrMethod);

			aliasAttributesMapping.add(aliasFor, aliasValue);
		}
		return aliasAttributesMapping;
	}

	private String[] getAliasValue(Annotation compositeMatcher, Method aliasedPropMethod) {
		try {
			Object value = aliasedPropMethod.invoke(compositeMatcher);
			if (value instanceof String) {
				return new String[]{(String) value};
			}

			if (value instanceof String[]) {
				return (String[]) value;
			}

			throw new RuntimeException("Unsupported type");
		} catch (IllegalAccessException | InvocationTargetException | NullPointerException e) {
			throw new RuntimeException(e);
		}
	}

	private void readDefinitionFromMatcher(Annotation annotation,
			List<MatcherDefinition<?>> matcherDefinitions) {
		MatcherType matcher = annotation.annotationType().getAnnotation(MatcherType.class);

		if (matcher == null) {
			return;
		}

		matcherDefinitions.add(new MatcherDefinition<>(annotation));
	}

	@SuppressWarnings("unchecked")
	private MatchResult doMatch(Object request, MatcherDefinition matcherDefinition) {
		MatcherProcessor matcherProcessor = matcherDefinition.getProcessor();

		if (matcherProcessor == null) {
			MatcherType matcherType = matcherDefinition.getMatcher().annotationType()
					.getAnnotation(MatcherType.class);
			matcherProcessor = getMatcherProcessor(matcherType.processor());
			matcherDefinition.setProcessor(matcherProcessor);
		}

		if (matcherProcessor == null) {
			throw new NullPointerException("Cannot find the matcher processor");
		}

		return matcherProcessor.process(request, matcherDefinition);
	}

	protected abstract MatcherProcessor getMatcherProcessor(
			Class<? extends MatcherProcessor> processorClass);

	protected abstract <T> Iterable<T> findCandidatesByType(Class<T> beanClass);

	@SuppressWarnings("unchecked")
	protected <T> SelectPolicy<T> createSelectPolicy() {
		try {
			return defaultSelectPolicyClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new SelectPolicyCreationException(e);
		}
	}

	public void setRejectPolicy(RejectPolicy rejectPolicy) {
		this.rejectPolicy = rejectPolicy;
	}
}
