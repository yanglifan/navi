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

package com.github.yanglifan.navi.core;

import com.github.yanglifan.navi.core.matcher.EqualMatcher;
import com.github.yanglifan.navi.core.matcher.VersionMatcher;
import com.github.yanglifan.navi.core.policy.FirstMatchSelectPolicy;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * This tests will not run with Maven.
 *
 * @author Yang Lifan
 */
public class PerfTests {

	private static final int TEST_COUNT = 1000000;

	@Test
	public void test_version_matcher() {
		doTestVersionMatcher(false);
	}

	@Test
	public void test_version_matcher_cached() {
		doTestVersionMatcher(true);
	}

	@Test
	public void test_equals_normal() {
		Map<String, String> req = new HashMap<>();
		req.put("text", "hello");

		long start = System.nanoTime();
		for (int i = 0; i < TEST_COUNT; i++) {
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
				// do nothing
			}
		}
		long total = System.nanoTime() - start;
		System.out.println("Per test_equals_normal cost: " + total / TEST_COUNT + "ns");
	}

	@Test
	public void test_equals_matcher() {
		doEqualMatcherTest(false);
	}

	@Test
	public void test_equals_matcher_cached() {
		doEqualMatcherTest(true);
	}

	private void doTestVersionMatcher(boolean needCache) {
		SimpleSelector selector = new SimpleSelector(FirstMatchSelectPolicy.class);
		selector.setEnableCache(needCache);

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
		for (int i = 0; i < TEST_COUNT; i++) {
			req.put("version", baseVersion + i);
			selector.select(req, TestHandler.class);
		}

		long total = System.nanoTime() - start;
		System.out.println("Per Version Match cost: " + total / TEST_COUNT + "ns");
	}

	private void doEqualMatcherTest(boolean cache) {
		SimpleSelector selector = new SimpleSelector();
		selector.setEnableCache(cache);

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

		Map<String, String> req = new HashMap<>();
		req.put("text", "hello");
		selector.select(req, TestHandler.class);

		long start = System.nanoTime();
		for (int i = 0; i < TEST_COUNT; i++) {
			selector.select(req, TestHandler.class);
		}

		long total = System.nanoTime() - start;
		System.out.println("Per Equals Match cost: " + total / TEST_COUNT + "ns");
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

