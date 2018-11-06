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
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.List;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Repeatable(EqualMatchers.class)
@MatcherType(processor = EqualMatcher.Processor.class)
public @interface EqualMatcher {
	String property();

	String[] value() default "";

	String aliasLabel() default "";

	class Processor extends OnePropertyMatcherProcessor<EqualMatcher> {

		@Override
		protected MatchResult doProcess(Object request, EqualMatcher matcher,
				String[] expectValues) {
			List<String> expectValueList = Arrays.asList(expectValues);
			boolean isContains = expectValueList.contains(request.toString());
			return isContains ? MatchResult.accept() : MatchResult.reject();
		}

		@Override
		protected String getPropertyPath(EqualMatcher matcherAnnotation) {
			return matcherAnnotation.property();
		}

		@Override
		protected String[] getMatcherValue(EqualMatcher matcher) {
			return matcher.value();
		}

		@Override
		protected String aliasName() {
			return "value";
		}
	}
}
