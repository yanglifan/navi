package com.github.navi.core;

import com.github.navi.core.exception.SelectStrategyCreationException;
import com.github.navi.core.strategy.ScoreSelectStrategy;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
		Annotation[] annotations = candidate.getClass().getAnnotations();
		Map<Annotation, MatcherDescription> matcherDescriptionMap = new HashMap<>();

		for (Annotation annotationOnCandidate : annotations) {
			processMatcherType(annotationOnCandidate, matcherDescriptionMap);
			processCompositeMatcherType(annotationOnCandidate, matcherDescriptionMap);
		}

		return matcherDescriptionMap;
	}

	private void processCompositeMatcherType(Annotation annotationOnCandidate,
			Map<Annotation, MatcherDescription> matcherDescriptionMap) {
		CompositeMatcherType compositeMatcherType =
				annotationOnCandidate.annotationType().getAnnotation(CompositeMatcherType.class);
		if (compositeMatcherType == null) {
			return;
		}

		Map<Annotation, MatcherDescription> subMatcherDescriptionMap
				= getMatcherAnnotations(annotationOnCandidate);

		subMatcherDescriptionMap = parseAliasProps(annotationOnCandidate, subMatcherDescriptionMap);

		matcherDescriptionMap.putAll(subMatcherDescriptionMap);
	}

	/**
	 * @param compositeMatcher      an annotation with {@link CompositeMatcherType}
	 * @param matcherDescriptionMap matcher annotations on this compositeMatcher
	 */
	private Map<Annotation, MatcherDescription> parseAliasProps(Annotation compositeMatcher,
			Map<Annotation, MatcherDescription> matcherDescriptionMap) {
		Map<Class<? extends Annotation>, MatcherDescription> aliasedMatcherAnnotationMap =
				new HashMap<>();

		Map<Class, Annotation> annotationMap = matcherDescriptionMap.keySet().stream()
				.collect(Collectors.toMap(Annotation::getClass, (a) -> a));

		Map<Class<? extends Annotation>, Map<String, String>> aliasPropValues = new HashMap<>();

		Method[] methods = compositeMatcher.annotationType().getMethods();
		for (Method aliasedPropMethod : methods) {
			AliasProp aliasProp = aliasedPropMethod.getAnnotation(AliasProp.class);
			if (aliasProp == null) {
				continue;
			}

			Class<? extends Annotation> aliasedAnnotation = aliasProp.annotation();
			Map<String, String> aliasedValues =
					aliasPropValues.computeIfAbsent(aliasedAnnotation, (a) -> new HashMap<>());

			String aliasedValue;
			try {
				aliasedValue = (String) aliasedPropMethod.invoke(compositeMatcher.annotationType());
			} catch (IllegalAccessException | InvocationTargetException | NullPointerException e) {
				throw new RuntimeException(e);
			}

			aliasedValues.put(aliasProp.value(), aliasedValue);
		}

		for (Map.Entry<Class<? extends Annotation>, Map<String, String>> entry
				: aliasPropValues.entrySet()) {
			Class<? extends Annotation> matcherType = entry.getKey();

			Annotation matcher = annotationMap.get(matcherType);
			Map<String, String> aliasedAttributes = entry.getValue();

			MatcherDescription matcherDescription =
					new MatcherDescription(matcher, aliasedAttributes);

			aliasedMatcherAnnotationMap.put(matcherType, matcherDescription);
		}

		return to(aliasedMatcherAnnotationMap, annotationMap);
	}

	private Map<Annotation, MatcherDescription> to(
			Map<Class<? extends Annotation>, MatcherDescription> matcherDescriptionMap,
			Map<Class, Annotation> annotationMap) {
		Map<Annotation, MatcherDescription> result = new HashMap<>();
		for (Map.Entry<Class<? extends Annotation>, MatcherDescription> entry :
				matcherDescriptionMap.entrySet()) {
			Annotation matcher = annotationMap.get(entry.getKey());
			result.put(matcher, entry.getValue());
		}
		return result;
	}

	private void processMatcherType(Annotation annotationOnCandidate,
			List<Annotation> allMatcherAnnotations) {
		MatcherType matcher =
				annotationOnCandidate.annotationType().getAnnotation(MatcherType.class);
		if (matcher == null) {
			return;
		}

		allMatcherAnnotations.add(annotationOnCandidate);
	}

	private void processMatcherType(Annotation annotationOnCandidate,
			Map<Annotation, MatcherDescription> matcherDescriptionMap) {
		MatcherType matcher =
				annotationOnCandidate.annotationType().getAnnotation(MatcherType.class);

		if (matcher == null) {
			return;
		}

		matcherDescriptionMap
				.put(annotationOnCandidate, new MatcherDescription(annotationOnCandidate));
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
