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
                IndicatorType indicatorType =
                        annotation.getClass().getAnnotation(IndicatorType.class);
                if (indicatorType == null) {
                    continue;
                }

                IndicatorProcessor<Annotation> indicatorProcessor =
                        getIndicatorProcessor(indicatorType.processor());

                if (indicatorProcessor == null) {
                    throw new NullPointerException("Cannot find the indicator processor");
                }

                indicatorProcessor.process(request, annotation);
            }
        }
        return null;
    }

    protected abstract IndicatorProcessor<Annotation> getIndicatorProcessor(
            Class<? extends IndicatorProcessor> processorClass);

    protected abstract <T> Iterable<T> findBeansByType(Class<T> beanClass);
}
