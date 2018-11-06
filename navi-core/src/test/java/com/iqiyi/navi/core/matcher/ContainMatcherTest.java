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

import com.iqiyi.navi.core.BaseTest;
import com.iqiyi.navi.core.Handler;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Yang Lifan
 */
public class ContainMatcherTest extends BaseTest {
	@Test
	public void simple() {
		// Given
		Map<String, String> req1 = new HashMap<>();
		req1.put("name", "stark");

		Map<String, String> req2 = new HashMap<>();
		req2.put("name", "starbucks");

		registerHandler(new ContainTestHandler());

		// When
		Handler h1 = selector.select(req1, Handler.class);
		Handler h2 = selector.select(req2, Handler.class);

		// Then
		assertThat(h1).isInstanceOf(ContainTestHandler.class);
		assertThat(h2).isInstanceOf(ContainTestHandler.class);
	}

	@ContainMatcher(property = "name", value = "sta")
	private class ContainTestHandler implements Handler {
	}
}
