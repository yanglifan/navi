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
import org.apache.commons.collections.CollectionUtils;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@MatcherType(processor = IntersectMatcher.Processor.class)
public @interface IntersectMatcher {
	String property();

	String[] value() default {};

	String separator() default ",";

	class Processor extends OnePropertyMatcherProcessor<IntersectMatcher> {

		@Override
		protected String getPropertyPath(IntersectMatcher matcherAnnotation) {
			return matcherAnnotation.property();
		}

		@Override
		protected MatchResult doProcess(Object property, IntersectMatcher matcher,
				String[] expectValues) {
			List<String> expectValueList = Arrays.asList(expectValues);

			boolean isIntersected;
			if (property instanceof String) {
				isIntersected = isIntersectOnString(property, expectValueList, matcher.separator());
			} else if (isStringCollection(property)) {
				isIntersected = isIntersectOnCollection(property, expectValueList);
			} else {
				isIntersected = false;
			}

			return isIntersected ? MatchResult.accept() : MatchResult.reject();
		}

		@Override
		protected String[] getMatcherValue(IntersectMatcher matcher) {
			return matcher.value();
		}

		@Override
		protected String aliasName() {
			return "value";
		}

		private boolean isStringCollection(Object property) {
			if (!(property instanceof Collection)) {
				return false;
			}

			Collection collection = (Collection) property;
			for (Object o : collection) {
				if (!String.class.isInstance(o)) {
					return false;
				}
			}

			return true;
		}

		private boolean isIntersectOnString(Object property, List<String> expectValueList,
				String separator) {
			if (!(property instanceof String)) {
				return false;
			}

			String propertyValue = (String) property;
			List<String> propertyValues = Arrays.asList(propertyValue.split(separator));
			return isIntersectOnCollection(propertyValues, expectValueList);
		}

		@SuppressWarnings("unchecked")
		private boolean isIntersectOnCollection(Object property, List<String> expectValueList) {
			Collection<String> stringCollection = (Collection<String>) property;
			stringCollection = trimStringCollection(stringCollection);
			Collection intersectResult =
					CollectionUtils.intersection(expectValueList, stringCollection);
			return !intersectResult.isEmpty();
		}

		private Collection<String> trimStringCollection(Collection<String> property) {
			Collection<String> collection = new ArrayList<>();
			for (String v : property) {
				collection.add(v.trim());
			}
			return collection;
		}
	}
}
