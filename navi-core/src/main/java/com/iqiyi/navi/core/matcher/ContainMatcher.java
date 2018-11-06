/*
 * Copyright 2018 Yang Lifan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.iqiyi.navi.core.matcher;

import com.iqiyi.navi.core.MatchResult;
import com.iqiyi.navi.core.MatcherType;

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
