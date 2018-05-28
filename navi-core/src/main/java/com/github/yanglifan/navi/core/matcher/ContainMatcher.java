package com.github.yanglifan.navi.core.matcher;

import com.github.yanglifan.navi.core.MatchResult;
import com.github.yanglifan.navi.core.MatcherType;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Yang Lifan
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@MatcherType(processor = ContainMatcher.Processor.class)
public @interface ContainMatcher {
	String property();

	String[] value() default "";

	String aliasLabel() default "";

	class Processor extends OnePropertyMatcherProcessor<ContainMatcher> {

		@Override
		protected MatchResult doProcess(Object request, ContainMatcher matcher,
				String[] expectValues) {
			for (String expectValue : expectValues) {
				if (request.toString().contains(expectValue)) {
					return MatchResult.accept();
				}
			}

			return MatchResult.reject();
		}

		@Override
		protected String getPropertyPath(ContainMatcher matcherAnnotation) {
			return matcherAnnotation.property();
		}

		@Override
		protected String[] getMatcherValue(ContainMatcher matcher) {
			return matcher.value();
		}

		@Override
		protected String aliasName() {
			return "value";
		}
	}
}
