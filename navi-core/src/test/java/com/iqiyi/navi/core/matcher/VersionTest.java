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
public class VersionTest extends BaseTest {
	@Test
	public void simple_match() {
		// Given
		registerHandler(new SimpleVersionHandler());

		Map<String, String> request = new HashMap<>();
		request.put("version", "1.5.0");

		// When
		Handler handler = selector.select(request, Handler.class);

		// Then
		assertThat(handler).isInstanceOf(SimpleVersionHandler.class);
	}

	@Test
	public void upper_bound_is_all_version() {
		// Given
		Map<String, String> v9_0_11_Request = new HashMap<>();
		v9_0_11_Request.put("version", "9.0.11");

		Map<String, String> v9_1_0_Request = new HashMap<>();
		v9_1_0_Request.put("version", "9.1.0");

		Map<String, String> v9_1_20_Request = new HashMap<>();
		v9_1_20_Request.put("version", "9.1.20");

		registerHandler(new UpperAllHandler());

		// When
		Handler v9_0_11_Result = selector.select(v9_0_11_Request, Handler.class);
		Handler v9_1_0_Result = selector.select(v9_1_0_Request, Handler.class);
		Handler v9_1_20_Result = selector.select(v9_1_20_Request, Handler.class);

		// Then
		assertThat(v9_0_11_Result).isNull();
		assertThat(v9_1_0_Result).isInstanceOf(UpperAllHandler.class);
		assertThat(v9_1_20_Result).isInstanceOf(UpperAllHandler.class);
	}

	@Test
	public void upper_bound_is_all_version2() {
		// Given
		Map<String, String> v9_0_11_Request = new HashMap<>();
		v9_0_11_Request.put("version", "9.0.11");

		Map<String, String> v9_1_0_Request = new HashMap<>();
		v9_1_0_Request.put("version", "9.1.0");

		Map<String, String> v9_1_20_Request = new HashMap<>();
		v9_1_20_Request.put("version", "9.1.20");

		registerHandler(new UpperAll2Handler());

		// When
		Handler v9_0_11_Result = selector.select(v9_0_11_Request, Handler.class);
		Handler v9_1_0_Result = selector.select(v9_1_0_Request, Handler.class);
		Handler v9_1_20_Result = selector.select(v9_1_20_Request, Handler.class);

		// Then
		assertThat(v9_0_11_Result).isNull();
		assertThat(v9_1_0_Result).isInstanceOf(UpperAll2Handler.class);
		assertThat(v9_1_20_Result).isInstanceOf(UpperAll2Handler.class);
	}

	@Test
	public void lower_bound_is_all_version() {
		// Given
		Map<String, String> v9_0_11_Request = new HashMap<>();
		v9_0_11_Request.put("version", "9.0.11");

		Map<String, String> v9_1_0_Request = new HashMap<>();
		v9_1_0_Request.put("version", "9.1.0");

		Map<String, String> v9_1_20_Request = new HashMap<>();
		v9_1_20_Request.put("version", "9.1.20");

		registerHandler(new LowerAllHandler());
		registerHandler(new UpperAllHandler());

		// When
		Handler v9_0_11_Result = selector.select(v9_0_11_Request, Handler.class);
		Handler v9_1_0_Result = selector.select(v9_1_0_Request, Handler.class);
		Handler v9_1_20_Result = selector.select(v9_1_20_Request, Handler.class);

		// Then
		assertThat(v9_0_11_Result).isInstanceOf(LowerAllHandler.class);
		assertThat(v9_1_0_Result).isInstanceOf(UpperAllHandler.class);
		assertThat(v9_1_20_Result).isInstanceOf(UpperAllHandler.class);
	}

	@Test
	public void version_range_empty() {
		// Given
		Map<String, String> request = new HashMap<>();
		request.put("version", "1.0.0");

		registerHandler(new EmptyVersionRangeHandler());

		// When
		Handler handler = selector.select(request, Handler.class);

		// Then
		assertThat(handler).isNull();
	}

	@VersionMatcher(range = "[9.1.0,*]")
	private class UpperAllHandler implements Handler {
	}

	@VersionMatcher(range = "[9.1.0,*)")
	private class UpperAll2Handler implements Handler {
	}

	@VersionMatcher(range = "[1.0.0,2.0.0)")
	private class SimpleVersionHandler implements Handler {
	}

	@VersionMatcher(range = "[*,9.1.0)")
	private class LowerAllHandler implements Handler {
	}

	@VersionMatcher
	private class EmptyVersionRangeHandler implements Handler {
	}
}
