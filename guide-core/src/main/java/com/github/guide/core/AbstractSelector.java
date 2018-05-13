package com.github.guide.core;

import java.lang.annotation.Annotation;

/**
 * @author Yang Lifan
 */
public abstract class AbstractSelector implements Selector {

    public <T> T select(Object request, Class<T> beanClass) {
        Iterable<T> candidates = findBeansByType(beanClass);
        for (T candidate : candidates) {
            Annotation[] annotations = candidate.getClass().getAnnotations();
            for (Annotation annotation : annotations) {
                MatcherType matcherType =
                        annotation.getClass().getAnnotation(MatcherType.class);
                if (matcherType == null) {
                    continue;
                }

                MatcherProcessor<Annotation> indicatorProcessor =
                        getIndicatorProcessor(matcherType.processor());

                if (indicatorProcessor == null) {
                    throw new NullPointerException("Cannot find the indicator processor");
                }

                indicatorProcessor.process(request, annotation);
            }
        }
        return null;
    }

    protected abstract MatcherProcessor<Annotation> getIndicatorProcessor(
            Class<? extends MatcherProcessor> processorClass);

    protected abstract <T> Iterable<T> findBeansByType(Class<T> beanClass);
}
