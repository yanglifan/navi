package com.github.navi.core;

import com.github.navi.core.exception.SelectStrategyCreationException;
import com.github.navi.core.strategy.ScoreSelectStrategy;
import com.github.navi.core.utils.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Yang Lifan
 */
public abstract class AbstractSelector implements Selector {
	protected Class<? extends SelectStrategy> defaultSelectStrategyClass;

	public AbstractSelector() {
		this(ScoreSelectStrategy.class);
	}

	public AbstractSelector(Class<? extends SelectStrategy> defaultSelectStrategyClass) {
		this.defaultSelectStrategyClass = defaultSelectStrategyClass;
	}

	public <T> T select(Object request, Class<T> candidateType) {
		return this.select(request, candidateType, createSelectStrategy());
	}

	@Override
	public <T> T select(Object request, Class<T> candidateType, SelectStrategy<T> selectStrategy) {
		Iterable<T> candidates = findCandidatesByType(candidateType);

		for (T candidate : candidates) {
			T result = doMatch(request, candidate, selectStrategy);

			if (result != null) {
				return result;
			}
		}

		return selectStrategy.getResult();
	}

	private <T> T doMatch(Object request, T candidate, SelectStrategy<T> selectStrategy) {
		Map<Annotation, MatcherDescription> matcherDescriptions = getMatcherAnnotations(candidate);

		for (Map.Entry<Annotation, MatcherDescription> matcherDescriptionEntry
				: matcherDescriptions.entrySet()) {
			MatchResult matchResult = doMatch(request, matcherDescriptionEntry.getValue());

			if (matchResult == null) {
				continue;
			}

			if (matchResult == MatchResult.REJECT) {
				return null;
			}

			selectStrategy.addMatchResult(matchResult);
		}

		return selectStrategy.addCandidate(candidate);
	}

	private Map<Annotation, MatcherDescription> getMatcherAnnotations(Object candidate) {
		Annotation[] annotations = getAnnotations(candidate);

		Map<Annotation, MatcherDescription> matcherDescriptions = new HashMap<>();

		for (Annotation annotationOnCandidate : annotations) {
			processMatcherType(annotationOnCandidate, matcherDescriptions);
			processCompositeMatcherType(annotationOnCandidate, matcherDescriptions);
		}

		return matcherDescriptions;
	}

	private Annotation[] getAnnotations(Object candidate) {
		if (candidate instanceof Annotation) {
			return ((Annotation) candidate).annotationType().getAnnotations();
		} else {
			return candidate.getClass().getAnnotations();
		}
	}

	private void processCompositeMatcherType(Annotation annotation,
			Map<Annotation, MatcherDescription> matcherDescriptions) {
		if (notCompositeMatcher(annotation)) {
			return;
		}

		Annotation[] subAnnotations = annotation.annotationType().getAnnotations();
		for (Annotation subAnnotation : subAnnotations) {
			processMatcherType(subAnnotation, matcherDescriptions);
		}

		merge(matcherDescriptions, aliasedAttributes(annotation));
	}

	private boolean notCompositeMatcher(Annotation annotationOnCandidate) {
		CompositeMatcherType compositeMatcherType =
				annotationOnCandidate.annotationType().getAnnotation(CompositeMatcherType.class);
		return compositeMatcherType == null;
	}

	private void merge(Map<Annotation, MatcherDescription> matcherDescriptions,
			Map<Class<? extends Annotation>, Map<String, String>> allAliasedAttributes) {
		for (Map.Entry<Annotation, MatcherDescription> entry : matcherDescriptions.entrySet()) {
			Annotation matcher = entry.getKey();
			Class<? extends Annotation> matcherType = AnnotationUtils.toClass(matcher);
			Map<String, String> aliasedAttributes = allAliasedAttributes.get(matcherType);
			if (aliasedAttributes != null && !aliasedAttributes.isEmpty()) {
				entry.getValue().getAliasedAttributes().putAll(aliasedAttributes);
			}
		}
	}

	private Map<Class<? extends Annotation>, Map<String, String>> aliasedAttributes(
			Annotation compositeMatcher) {
		Map<Class<? extends Annotation>, Map<String, String>> aliasAttributes = new HashMap<>();
		Method[] methods = compositeMatcher.annotationType().getMethods();
		for (Method aliasedPropMethod : methods) {
			AliasAttribute aliasAttribute = aliasedPropMethod.getAnnotation(AliasAttribute.class);
			if (aliasAttribute == null) {
				continue;
			}

			String aliasedValue = getAliasedValue(compositeMatcher, aliasedPropMethod);

			Map<String, String> aliasedAttributes =
					createAliasedAttributesIfAbsent(aliasAttributes, aliasAttribute);
			aliasedAttributes.put(aliasAttribute.attributeFor(), aliasedValue);
		}
		return aliasAttributes;
	}

	private String getAliasedValue(Annotation compositeMatcher, Method aliasedPropMethod) {
		String aliasedValue;
		try {
			aliasedValue = (String) aliasedPropMethod.invoke(compositeMatcher);
		} catch (IllegalAccessException | InvocationTargetException | NullPointerException e) {
			throw new RuntimeException(e);
		}
		return aliasedValue;
	}

	private Map<String, String> createAliasedAttributesIfAbsent(Map<Class<? extends Annotation>,
			Map<String, String>> aliasAttributes, AliasAttribute aliasAttribute) {
		Class<? extends Annotation> aliasedAnnotation = aliasAttribute.annotationFor();
		return aliasAttributes.computeIfAbsent(aliasedAnnotation, (a) -> new HashMap<>());
	}

	private void processMatcherType(Annotation annotationOnCandidate,
			Map<Annotation, MatcherDescription> matcherDescriptionMap) {
		MatcherType matcher =
				annotationOnCandidate.annotationType().getAnnotation(MatcherType.class);

		if (matcher == null) {
			return;
		}

		matcherDescriptionMap
				.put(annotationOnCandidate, new MatcherDescription<>(annotationOnCandidate));
	}

	@SuppressWarnings("unchecked")
	private MatchResult doMatch(Object request, MatcherDescription matcherDescription) {
		MatcherType matcherType =
				matcherDescription.getMatcher().annotationType().getAnnotation(MatcherType.class);

		MatcherProcessor matcherProcessor = getMatcherProcessor(matcherType.processor());

		if (matcherProcessor == null) {
			throw new NullPointerException("Cannot find the matcher processor");
		}

		return matcherProcessor.process(request, matcherDescription);
	}

	protected abstract MatcherProcessor getMatcherProcessor(
			Class<? extends MatcherProcessor> processorClass);

	protected abstract <T> Iterable<T> findCandidatesByType(Class<T> beanClass);

	@SuppressWarnings("unchecked")
	protected <T> SelectStrategy<T> createSelectStrategy() {
		try {
			return defaultSelectStrategyClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new SelectStrategyCreationException(e);
		}
	}
}
