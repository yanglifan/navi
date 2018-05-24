package com.github.navi.core.matcher;

import com.github.navi.core.Handler;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Yang Lifan
 */
public class VersionMatcherTest extends BaseMatcherTest {
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

	@VersionMatcher(versionRange = "[9.1.0,*]")
	private class UpperAllHandler implements Handler {

	}

	@VersionMatcher(versionRange = "[1.0.0,2.0.0)")
	private class SimpleVersionHandler implements Handler {
	}

	@VersionMatcher(versionRange = "[*,9.1.0)")
	private class LowerAllHandler implements Handler {

	}
}
