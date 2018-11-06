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

import com.iqiyi.navi.core.Handler;
import com.iqiyi.navi.core.SimpleSelector;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TODO Add basic test
 * 2. multivalue
 * 3. nested
 */
public class EqualMatcherTest {

	private SimpleSelector selector;

	@Before
	public void setUp() {
		selector = new SimpleSelector();
	}

	@Test
	public void simple() {
		// Given
		Map<String, String> request = new HashMap<>();
		request.put("name", "stark");

		selector.registerCandidate(Handler.class, new SimpleEqualHandler());

		// When
		Handler handler = selector.select(request, Handler.class);

		// Then
		assertThat(handler).isInstanceOf(SimpleEqualHandler.class);
	}

	@Test
	public void multi_expect_values() {
		// Given
		Map<String, String> request = new HashMap<>();
		request.put("name", "stark");

		selector.registerCandidate(Handler.class, new MultiEqualHandler());

		// When
		Handler handler = selector.select(request, Handler.class);

		// Then
		assertThat(handler).isInstanceOf(MultiEqualHandler.class);
	}

	@Test
	public void repeatable() {
		// Given
		Map<String, String> mj1 = new HashMap<>();
		mj1.put("firstName", "michael");
		mj1.put("lastName", "jordan");

		Map<String, String> mj2 = new HashMap<>();
		mj2.put("firstName", "michael");
		mj2.put("lastName", "jackson");

		selector.registerCandidate(Handler.class, new MichaelJordan());

		// When
		Handler h1 = selector.select(mj1, Handler.class);
		Handler h2 = selector.select(mj2, Handler.class);

		// Then
		assertThat(h1).isInstanceOf(MichaelJordan.class);
		assertThat(h2).isNull();
	}

	@EqualMatcher(property = "name", value = "stark")
	private class SimpleEqualHandler implements Handler {
	}

	@EqualMatcher(property = "name", value = {"stark", "thor"})
	private class MultiEqualHandler implements Handler {
	}

	@EqualMatcher(property = "firstName", value = "michael")
	@EqualMatcher(property = "lastName", value = "jordan")
	private class MichaelJordan implements Handler {
	}
}