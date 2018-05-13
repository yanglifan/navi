package com.github.guide.core;

import java.lang.annotation.Annotation;

/**
 * @author Yang Lifan
 */
public interface IndicatorProcessor<A extends Annotation> {
    ProcessResult process(Object request, A indicatorAnnotation);
}
