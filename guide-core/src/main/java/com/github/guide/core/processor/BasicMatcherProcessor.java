package com.github.guide.core.processor;

import com.github.guide.core.BasicMatcher;
import com.github.guide.core.MatcherProcessor;
import com.github.guide.core.MatchResult;

/**
 * @author Yang Lifan
 */
public class BasicMatcherProcessor implements MatcherProcessor<BasicMatcher> {
    @Override
    public MatchResult process(Object request, BasicMatcher indicatorAnnotation) {
        return null;
    }
}
