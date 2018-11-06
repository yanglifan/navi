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

package com.iqiyi.navi.core.benchmark;

import com.iqiyi.navi.core.Handler;
import com.iqiyi.navi.core.PlatformMatcher;
import com.iqiyi.navi.core.SimpleSelector;
import com.iqiyi.navi.core.policy.FirstMatchSelectPolicy;
import org.junit.Ignore;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Yang Lifan
 */
@SuppressWarnings("all")
public class CompositeMatcherBenchmark extends BaseBenchmark {
	private static final int SLOW_TIMES = 30;

	@Ignore
	@Test
	public void benchmark() throws Exception {
		SimpleSelector selector = new SimpleSelector(FirstMatchSelectPolicy.class);
		selector.registerCandidate(Handler.class, new AndroidHandler());
		selector.registerCandidate(Handler.class, new AHandler());
		selector.registerCandidate(Handler.class, new BHandler());
		selector.registerCandidate(Handler.class, new CHandler());
		selector.registerCandidate(Handler.class, new DHandler());

		long cost = doBenchmark(this::platformIsAndroid,
				req -> selector.select(req, Handler.class));
		System.out.println(cost);
		long standard = doBenchmark(this::platformIsAndroid, this::standardBenchmark);
		System.out.println(standard);
		long times = cost / standard;
		assertThat(times).isLessThan(SLOW_TIMES);
	}

	@SuppressWarnings("all")
	private void standardBenchmark(Map<String, String> request) {
		String platform = request.get("platform");
		if ("a".equals(platform)) {
			// do a
		} else if ("b".equals(platform)) {
			// do b
		} else if ("c".equals(platform)) {
			// do c
		} else if ("d".equals(platform)) {
			// do d
		} else if ("android".equals(platform)) {
			// do android
		}
	}

	private Map<String, String> platformIsAndroid() {
		Map<String, String> request = new HashMap<>();
		request.put("platform", "android");
		return request;
	}


	@PlatformMatcher(platform = "android")
	class AndroidHandler implements Handler {
	}

	@PlatformMatcher(platform = "a")
	class AHandler implements Handler {
	}

	@PlatformMatcher(platform = "b")
	class BHandler implements Handler {
	}

	@PlatformMatcher(platform = "c")
	class CHandler implements Handler {
	}

	@PlatformMatcher(platform = "d")
	public class DHandler implements Handler {
	}
}
