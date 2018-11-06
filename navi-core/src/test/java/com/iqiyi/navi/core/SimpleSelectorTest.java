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

package com.iqiyi.navi.core;

import com.iqiyi.navi.core.matcher.EqualMatcher;
import com.iqiyi.navi.core.matcher.VersionMatcher;
import com.iqiyi.navi.core.policy.FirstMatchSelectPolicy;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class SimpleSelectorTest {
	@Test
	public void no_winner_when_no_matcher_annotation() {
		// Given
		SimpleSelector simpleSelector = new SimpleSelector();
		Set<Handler> handlers = new HashSet<>();
		handlers.add(new NoAnno1Handler());
		handlers.add(new NoAnno2Handler());
		simpleSelector.registerCandidates(Handler.class, handlers);

		// When
		Handler winner = simpleSelector.select(new Object(), Handler.class);

		// Then
		assertThat(winner).isNull();
	}

	@Test
	public void no_winner_when_all_reject() {
		// Given
		SimpleSelector simpleSelector = new SimpleSelector();
		Set<Handler> handlers = new HashSet<>();
		handlers.add(new StarkHandler());
		simpleSelector.registerCandidates(Handler.class, handlers);

		Map<String, String> req = new HashMap<>();
		req.put("username", "thor");
		req.put("version", "1.0.0");

		// When
		Handler winner = simpleSelector.select(req, Handler.class);

		// Then
		assertThat(winner).isNull();
	}

	@Test
	public void test_first_match_policy() {
		// Given
		SimpleSelector simpleSelector = new SimpleSelector();
		Set<Handler> handlers = new HashSet<>();
		handlers.add(new StarkHandler());
		simpleSelector.registerCandidates(Handler.class, handlers);

		Map<String, String> req = new HashMap<>();
		req.put("username", "stark");
		req.put("version", "1.0.0");

		// When
		Handler winner = simpleSelector.select(req, Handler.class, new FirstMatchSelectPolicy<>());

		// Then
		assertThat(winner).isInstanceOf(StarkHandler.class);
	}

	@Test
	public void test_alias_prop() {
		Map<String, String> request = new HashMap<>();
		request.put("platform", "android");

		SimpleSelector selector = new SimpleSelector();
		selector.registerCandidate(Handler.class, new WithAliasPropHandler());

		Handler handler = selector.select(request, Handler.class);

		assertThat(handler).isInstanceOf(WithAliasPropHandler.class);
	}

	private class NoAnno1Handler implements Handler {

	}

	private class NoAnno2Handler implements Handler {

	}

	@EqualMatcher(property = "username", value = "stark")
	@VersionMatcher(range = "[1.0.0,2.0.0)")
	private class StarkHandler implements Handler {

	}

	@PlatformMatcher(platform = "android")
	private class WithAliasPropHandler implements Handler {

	}
}