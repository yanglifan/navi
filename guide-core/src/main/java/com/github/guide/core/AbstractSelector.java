package com.github.guide.core;

import java.lang.annotation.Annotation;

/**
 * @author Yang Lifan
 */
public abstract class AbstractSelector implements Selector {

    public <T> T select(Object request, Class<T> beanClass) {
        Iterable<T> candidates = findBeansByType(beanClass);
        SelectStrategy<T> selectStrategy = getSelectStrategy();
        for (T candidate : candidates) {
            Annotation[] annotations = candidate.getClass().getAnnotations();
            for (Annotation annotation : annotations) {
                MatcherType matcherType =
                        annotation.getClass().getAnnotation(MatcherType.class);
                if (matcherType == null) {
                    continue;
                }

                MatcherProcessor<Annotation> matcherProcessor =
                        getMatcherProcessor(matcherType.processor());

                if (matcherProcessor == null) {
                    throw new NullPointerException("Cannot find the indicator processor");
                }

                MatchResult matchResult = matcherProcessor.process(request, annotation);
                selectStrategy.addMatchResult(matchResult);
            }
        }
        return selectStrategy.getWinner();
    }

    protected abstract MatcherProcessor<Annotation> getMatcherProcessor(
            Class<? extends MatcherProcessor> processorClass);

    protected abstract <T> Iterable<T> findBeansByType(Class<T> beanClass);

    protected <T> SelectStrategy<T> getSelectStrategy() {
        return new ScoreSelectStrategy<T>();
    }
}
