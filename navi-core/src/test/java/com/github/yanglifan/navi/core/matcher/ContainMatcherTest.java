package com.github.yanglifan.navi.core.matcher;

import com.github.yanglifan.navi.core.BaseTest;
import com.github.yanglifan.navi.core.Handler;
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
