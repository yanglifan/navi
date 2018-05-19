package com.github.navi.core;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

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
		List<Annotation> annotations = getMatcherAnnotations(candidate);

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

	private List<Annotation> getMatcherAnnotations(Object candidate) {
		Annotation[] annotations = candidate.getClass().getAnnotations();
		List<Annotation> allMatcherAnnotations = new ArrayList<>();

		for (Annotation annotation : annotations) {
			MatcherType matcher = annotation.annotationType().getAnnotation(MatcherType.class);
			if (matcher != null) {
				allMatcherAnnotations.add(annotation);
				continue;
			}

			CompositeMatcher compositeMatcher = annotation.annotationType().getAnnotation(CompositeMatcher.class);
			if (compositeMatcher != null) {
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

	protected abstract MatcherProcessor<Annotation> getMatcherProcessor(
			Class<? extends MatcherProcessor> processorClass);

	protected abstract <T> Iterable<T> findCandidatesByType(Class<T> beanClass);

	protected <T> SelectStrategy<T> createSelectStrategy() {
		return new ScoreSelectStrategy<>();
	}
}
