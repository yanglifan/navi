package com.github.navi.core;

import java.lang.annotation.Annotation;

/**
 * @author Yang Lifan
 */
public abstract class AbstractSelector implements Selector {

    public <T> T select(Object request, Class<T> candidateType) {
        Iterable<T> candidates = findCandidatesByType(candidateType);

        SelectStrategy<T> selectStrategy = getSelectStrategy();

        for (T candidate : candidates) {
            Annotation[] annotations = candidate.getClass().getAnnotations();

            for (Annotation annotation : annotations) {
                MatchResult<T> matchResult = doMatch(request, annotation);

                if (matchResult == null) {
                    continue;
                }

                selectStrategy.addMatchResult(matchResult);
            }

            selectStrategy.addCandidate(candidate);
        }

        return selectStrategy.getWinner();
    }

    private <T> MatchResult<T> doMatch(Object request, Annotation annotation) {
        MatcherType matcherType =
                annotation.annotationType().getAnnotation(MatcherType.class);
        if (matcherType == null) {
            return null;
        }

        MatcherProcessor<Annotation> matcherProcessor =
                getMatcherProcessor(matcherType.processor());

        if (matcherProcessor == null) {
            throw new NullPointerException("Cannot find the matcher processor");
        }

        return matcherProcessor.process(request, annotation);
    }

    protected abstract MatcherProcessor<Annotation> getMatcherProcessor(
            Class<? extends MatcherProcessor> processorClass);

    protected abstract <T> Iterable<T> findCandidatesByType(Class<T> beanClass);

    protected <T> SelectStrategy<T> getSelectStrategy() {
        return new ScoreSelectStrategy<T>();
    }
}
