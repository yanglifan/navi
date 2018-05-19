package com.github.navi.core;

import java.lang.annotation.Annotation;

/**
 * @author Yang Lifan
 */
public abstract class AbstractSelector implements Selector {

	public <T> T select(Object request, Class<T> candidateType) {
		Iterable<T> candidates = findCandidatesByType(candidateType);

		SelectStrategy<T> selectStrategy = createSelectStrategy();

		for (T candidate : candidates) {
			doMatch(request, candidate, selectStrategy);
		}

		return selectStrategy.getWinner();
	}

	private <T> void doMatch(Object request, T candidate, SelectStrategy<T> selectStrategy) {
		Annotation[] annotations = candidate.getClass().getAnnotations();

		for (Annotation annotation : annotations) {
			MatchResult matchResult = doMatch(request, annotation);

			if (matchResult == null) {
				continue;
			}

			if (matchResult == MatchResult.REJECT) {
				return;
			}

			selectStrategy.addMatchResult(matchResult);
		}

		selectStrategy.addCandidate(candidate);
	}

	private MatchResult doMatch(Object request, Annotation annotation) {
		MatcherType matcherType = annotation.annotationType().getAnnotation(MatcherType.class);
		CompositeMatcher compositeMatcher = annotation.annotationType().getAnnotation(CompositeMatcher.class);

		if (matcherType == null || compositeMatcher == null) {
			return null;
		}

		if (matcherType != null) {
			return doMatchWithMatcher(request, annotation, matcherType);
		} else {
			return doMatchWithCompositeMatcher(request, annotation, compositeMatcher);
		}
	}

	private MatchResult doMatchWithCompositeMatcher(Object request, Annotation annotation,
													CompositeMatcher compositeMatcher) {
		return null;
	}

	private MatchResult doMatchWithMatcher(Object request, Annotation annotation, MatcherType matcherType) {
		MatcherProcessor<Annotation> matcherProcessor = getMatcherProcessor(matcherType.processor());

		if (matcherProcessor == null) {
			throw new NullPointerException("Cannot find the matcher processor");
		}

		return matcherProcessor.process(request, annotation);
	}

	protected abstract MatcherProcessor<Annotation> getMatcherProcessor(
			Class<? extends MatcherProcessor> processorClass);

	protected abstract <T> Iterable<T> findCandidatesByType(Class<T> beanClass);

	protected <T> SelectStrategy<T> createSelectStrategy() {
		return new ScoreSelectStrategy<>();
	}
}
