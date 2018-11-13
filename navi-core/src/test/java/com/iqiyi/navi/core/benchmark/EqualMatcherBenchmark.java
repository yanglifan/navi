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

import com.iqiyi.navi.core.SimpleSelector;
import com.iqiyi.navi.core.matcher.EqualMatcher;
import com.iqiyi.navi.core.matcher.VersionMatcher;
import com.iqiyi.navi.core.policy.FirstMatchSelectPolicy;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Yang Lifan
 */
@Slf4j
public class EqualMatcherBenchmark extends BaseBenchmark {

	private static final int TEST_RETRY = 5;
	private static final int SLOW_TIMES = 25;

	private SimpleSelector selector;

	@Before
	public void setUp() {
		selector = new SimpleSelector();
	}

	@Ignore
	@Test
	public void doEqualMatchBenchmark() throws Exception {
		selector.registerCandidate(TestHandler.class, new HelloTestHandler());
		selector.registerCandidate(TestHandler.class, new Hello1TestHandler());
		selector.registerCandidate(TestHandler.class, new Hello2TestHandler());
		selector.registerCandidate(TestHandler.class, new Hello3TestHandler());
		selector.registerCandidate(TestHandler.class, new Hello4TestHandler());
		selector.registerCandidate(TestHandler.class, new Hello5TestHandler());
		selector.registerCandidate(TestHandler.class, new Hello6TestHandler());
		selector.registerCandidate(TestHandler.class, new Hello7TestHandler());
		selector.registerCandidate(TestHandler.class, new Hello8TestHandler());
		selector.registerCandidate(TestHandler.class, new Hello9TestHandler());

		long standardCost = doStandardEqualMatchBenchmark();

		long cost;
		long times = 0;
		for (int i = 0; i < TEST_RETRY; i++) {
			log.info("Try #{}", i);
			cost = doBenchmark(this::textIsHello, req -> selector.select(req, TestHandler.class));
			times = cost / standardCost;
			log.info("Slow times is {}", times);
			if (times < SLOW_TIMES) {
				// Pass
				return;
			}
		}

		throw new RuntimeException("Benchmark test failed. Slow times is " + times);
	}

	@Ignore
	@Test
	public void test_version_matcher() {
		doTestVersionMatcher();
	}

	private Map<String, String> textIsHello() {
		Map<String, String> req = new HashMap<>();
		req.put("text", "hello");
		return req;
	}

	private long doStandardEqualMatchBenchmark() throws InterruptedException {
		return doBenchmark(this::textIsHello, this::doSingleStandardBenchmark);
	}

	@SuppressWarnings("all")
	private void doSingleStandardBenchmark(Map<String, String> req) {
		if ("hello9".equals(req.get("text"))) {
			System.out.println("hello9");
		} else if ("hello8".equals(req.get("text"))) {
			System.out.println("hello8");
		} else if ("hello7".equals(req.get("text"))) {
			System.out.println("hello7");
		} else if ("hello6".equals(req.get("text"))) {
			System.out.println("hello6");
		} else if ("hello5".equals(req.get("text"))) {
			System.out.println("hello5");
		} else if ("hello4".equals(req.get("text"))) {
			System.out.println("hello4");
		} else if ("hello3".equals(req.get("text"))) {
			System.out.println("hello3");
		} else if ("hello2".equals(req.get("text"))) {
			System.out.println("hello2");
		} else if ("hello1".equals(req.get("text"))) {
			System.out.println("hello1");
		} else if ("hello".equals(req.get("text"))) {
			// Do nothing, just do select
		}
	}

	private void doTestVersionMatcher() {
		SimpleSelector selector = new SimpleSelector(FirstMatchSelectPolicy.class);

		selector.registerCandidate(TestHandler.class, new V1TestHandler());
		selector.registerCandidate(TestHandler.class, new V1_1TestHandler());
		selector.registerCandidate(TestHandler.class, new V1_2TestHandler());
		selector.registerCandidate(TestHandler.class, new V1_3TestHandler());
		selector.registerCandidate(TestHandler.class, new V1_4TestHandler());
		selector.registerCandidate(TestHandler.class, new V1_5TestHandler());
		selector.registerCandidate(TestHandler.class, new V1_6TestHandler());
		selector.registerCandidate(TestHandler.class, new V1_7TestHandler());
		selector.registerCandidate(TestHandler.class, new V1_8TestHandler());
		selector.registerCandidate(TestHandler.class, new V1_9TestHandler());

		Map<String, String> req = new HashMap<>();
		req.put("version", "1.0.0");
		selector.select(req, TestHandler.class);

		String baseVersion = "1.0.";
		long start = System.nanoTime();
		for (int i = 0; i < testCount; i++) {
			req.put("version", baseVersion + i);
			selector.select(req, TestHandler.class);
		}

		long total = System.nanoTime() - start;
		System.out.println("Per Version Match cost: " + total / testCount + "ns");
	}

	interface TestHandler {
	}

	@VersionMatcher(range = "[1.0.0,1.1.0)")
	private class V1TestHandler implements TestHandler {
	}

	@VersionMatcher(range = "[1.1.0,1.2.0)")
	private class V1_1TestHandler implements TestHandler {
	}

	@VersionMatcher(range = "[1.2.0,1.3.0)")
	private class V1_2TestHandler implements TestHandler {
	}

	@VersionMatcher(range = "[1.3.0,1.4.0)")
	private class V1_3TestHandler implements TestHandler {
	}

	@VersionMatcher(range = "[1.4.0,1.5.0)")
	private class V1_4TestHandler implements TestHandler {
	}

	@VersionMatcher(range = "[1.5.0,1.6.0)")
	private class V1_5TestHandler implements TestHandler {
	}

	@VersionMatcher(range = "[1.6.0,1.7.0)")
	private class V1_6TestHandler implements TestHandler {
	}

	@VersionMatcher(range = "[1.7.0,1.8.0)")
	private class V1_7TestHandler implements TestHandler {
	}

	@VersionMatcher(range = "[1.8.0,1.9.0)")
	private class V1_8TestHandler implements TestHandler {
	}

	@VersionMatcher(range = "[1.9.0,1.10.0)")
	private class V1_9TestHandler implements TestHandler {
	}

	@EqualMatcher(property = "text", value = "hello")
	private class HelloTestHandler implements TestHandler {
	}

	@EqualMatcher(property = "text", value = "hello1")
	private class Hello1TestHandler implements TestHandler {
	}

	@EqualMatcher(property = "text", value = "hello2")
	private class Hello2TestHandler implements TestHandler {
	}

	@EqualMatcher(property = "text", value = "hello3")
	private class Hello3TestHandler implements TestHandler {
	}

	@EqualMatcher(property = "text", value = "hello4")
	private class Hello4TestHandler implements TestHandler {
	}

	@EqualMatcher(property = "text", value = "hello5")
	private class Hello5TestHandler implements TestHandler {
	}

	@EqualMatcher(property = "text", value = "hello6")
	private class Hello6TestHandler implements TestHandler {
	}

	@EqualMatcher(property = "text", value = "hello7")
	private class Hello7TestHandler implements TestHandler {
	}

	@EqualMatcher(property = "text", value = "hello8")
	private class Hello8TestHandler implements TestHandler {
	}

	@EqualMatcher(property = "text", value = "hello9")
	private class Hello9TestHandler implements TestHandler {
	}
}

