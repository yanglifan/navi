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

import com.iqiyi.navi.core.CompositeMatcherType;
import com.iqiyi.navi.core.Handler;
import com.iqiyi.navi.core.SimpleSelector;
import com.iqiyi.navi.core.alias.AliasFor;
import org.junit.Before;
import org.junit.Test;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class IntersectMatcherTest {
	private SimpleSelector selector;

	@Before
	public void setUp() {
		selector = new SimpleSelector();
	}

	@Test
	public void intersect_string() {
		// Given
		Map<String, String> request = new HashMap<>();
		request.put("name", "stark, thor");

		selector.registerCandidate(Handler.class, new IntersectHandler());

		// When
		Handler handler = selector.select(request, Handler.class);

		// Then
		assertThat(handler).isInstanceOf(IntersectHandler.class);
	}

	@Test
	public void intersect_collection() {
		// Given
		Map<String, List<String>> request = new HashMap<>();
		request.put("name", Arrays.asList("stark", "thor"));

		selector.registerCandidate(Handler.class, new IntersectHandler());

		// When
		Handler handler = selector.select(request, Handler.class);

		// Then
		assertThat(handler).isInstanceOf(IntersectHandler.class);
	}

	@Test
	public void alias_test() {
		// Given
		Map<String, List<String>> request = new HashMap<>();
		request.put("name", Arrays.asList("stark", "rogers"));

		selector.registerCandidate(Handler.class, new AliasIntersectHandler());

		// When
		Handler handler = selector.select(request, Handler.class);

		// Then
		assertThat(handler).isInstanceOf(AliasIntersectHandler.class);
	}

	@SuppressWarnings("unused")
	@Retention(RetentionPolicy.RUNTIME)
	@IntersectMatcher(property = "name")
	@CompositeMatcherType
	public @interface AliasIntersectMatcher {
		@AliasFor(annotationFor = IntersectMatcher.class, attributeFor = "value")
		String[] names();
	}

	@IntersectMatcher(property = "name", value = {"stark", "rogers"})
	private class IntersectHandler implements Handler {

	}

	@AliasIntersectMatcher(names = {"stark", "thor"})
	private class AliasIntersectHandler implements Handler {

	}
}