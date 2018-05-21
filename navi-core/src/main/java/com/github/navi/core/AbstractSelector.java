package com.github.navi.core;

import com.github.navi.core.exception.SelectStrategyCreationException;
import com.github.navi.core.strategy.ScoreSelectStrategy;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

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
		List<Annotation> annotations = getMatcherAnnotations(candidate);

		for (Annotation annotation : annotations) {
			MatchResult matchResult = doMatch(request, annotation);

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

	private List<Annotation> getMatcherAnnotations(Object candidate) {
		Annotation[] annotations = candidate.getClass().getAnnotations();
		List<Annotation> allMatcherAnnotations = new ArrayList<>();

		for (Annotation annotation : annotations) {
			MatcherType matcher = annotation.annotationType().getAnnotation(MatcherType.class);
			if (matcher != null) {
				allMatcherAnnotations.add(annotation);
				continue;
			}

			CompositeMatcherType compositeMatcherType =
					annotation.annotationType().getAnnotation(CompositeMatcherType.class);
			if (compositeMatcherType != null) {
				List<Annotation> matcherAnnotations = getMatcherAnnotations(annotation);
				if (!matcherAnnotations.isEmpty()) {
					allMatcherAnnotations.addAll(matcherAnnotations);
				}
			}
		}

		return allMatcherAnnotations;
	}

	private MatchResult doMatch(Object request, Annotation matcherAnnotation) {
		MatcherType matcherType = matcherAnnotation.annotationType().getAnnotation(MatcherType.class);

		MatcherProcessor<Annotation> matcherProcessor = getMatcherProcessor(matcherType.processor());

		if (matcherProcessor == null) {
			throw new NullPointerException("Cannot find the matcher processor");
		}

		return matcherProcessor.process(request, matcherAnnotation);
	}

	protected abstract MatcherProcessor getMatcherProcessor(Class<? extends MatcherProcessor> processorClass);

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
