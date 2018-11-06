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

package com.iqiyi.navi.core.policy;

import com.iqiyi.navi.core.Handler;
import com.iqiyi.navi.core.SimpleSelector;
import com.iqiyi.navi.core.matcher.EqualMatcher;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.assertThat;

public class ScoreSelectPolicyTest {
	@SuppressWarnings("unchecked")
	@Test
	public void select_highest_score_one() {
		SimpleSelector selector = new SimpleSelector(ScoreSelectPolicy.class);

		Set<ComparableHandler> handlers = new TreeSet<>();
		handlers.add(new CH2());
		handlers.add(new CH1());
		handlers.add(new CH3());

		selector.registerCandidates(ComparableHandler.class, handlers);

		Map<String, String> request = new HashMap<>();
		request.put("p1", "v1");
		request.put("p2", "v2");
		request.put("p3", "v3");

		ComparableHandler handler = selector.select(request, ComparableHandler.class);

		assertThat(handler).isInstanceOf(CH3.class);
	}

	@Test
	public void select_none_when_no_matcher() {
		SimpleSelector selector = new SimpleSelector();
		selector.registerCandidate(Handler.class, new NoAnnoHandler());

		Handler handler = selector.select(new Object(), Handler.class);
		assertThat(handler).isNull();
	}

	interface ComparableHandler<T> extends Comparable<T> {
		int order();
	}

	@EqualMatcher(property = "p1", value = "v1")
	class CH1 extends AbstractComparableHandler {
		@Override
		public int order() {
			return 1;
		}
	}

	@EqualMatcher(property = "p1", value = "v1")
	@EqualMatcher(property = "p2", value = "v2")
	class CH2 extends AbstractComparableHandler {
		@Override
		public int order() {
			return 2;
		}
	}

	@EqualMatcher(property = "p1", value = "v1")
	@EqualMatcher(property = "p2", value = "v2")
	@EqualMatcher(property = "p3", value = "v3")
	class CH3 extends AbstractComparableHandler {
		@Override
		public int order() {
			return 3;
		}
	}

	@SuppressWarnings("all")
	class NoAnnoHandler implements Handler {
	}

	abstract class AbstractComparableHandler implements ComparableHandler<ComparableHandler> {
		@Override
		public int compareTo(ComparableHandler o) {
			return this.order() - o.order();
		}
	}
}