package com.github.navi.core.matcher;

import com.github.navi.core.MatchResult;
import com.github.navi.core.MatcherProcessor;
import com.github.navi.core.MatcherType;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@MatcherType(processor = EqualsMatcher.Processor.class)
public @interface EqualsMatcher {
    String propertyPath();

    String expectValue();

    class Processor implements MatcherProcessor<EqualsMatcher> {
        @Override
        public MatchResult process(Object request, EqualsMatcher matcherAnnotation) {
            String paramPath = matcherAnnotation.propertyPath();
            Object property;
            try {
                property = BeanUtils.getProperty(request, paramPath);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                return MatchResult.REJECT;
            }


            boolean isEquals = matcherAnnotation.expectValue().equals(property.toString());

            return isEquals ? MatchResult.ACCEPT : MatchResult.REJECT;
        }
    }
}
