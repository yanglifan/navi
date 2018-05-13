package com.github.guide.core.processor;

import com.github.guide.core.EqualsMatcher;
import com.github.guide.core.MatchResult;
import com.github.guide.core.MatcherProcessor;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;

/**
 * @author Yang Lifan
 */
public class EqualsMatcherProcessor implements MatcherProcessor<EqualsMatcher> {
    @Override
    public <T> MatchResult<T> process(Object request, EqualsMatcher matcherAnnotation) {
        String paramPath = matcherAnnotation.propertyPath();
        Object property;
        try {
            property = BeanUtils.getProperty(request, paramPath);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            return MatchResult.reject();
        }


        boolean isEquals = matcherAnnotation.expectValue().equals(property.toString());

        return isEquals ? MatchResult.accept() : MatchResult.reject();
    }
}
