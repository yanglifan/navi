package com.github.navi.core;

import com.github.navi.core.matcher.EqualsMatcher;
import com.github.navi.core.matcher.VersionMatcher;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@VersionMatcher(versionRange = "[1.0.0,2.0.0)")
@EqualsMatcher(propertyPath = "platform", expectValue = "android")
@CompositeMatcher
@interface AndroidV1Matcher {

}

interface Handler {
}

@AndroidV1Matcher
class AndroidV1Handler implements Handler {

}

/**
 * @author Yang Lifan
 */
public class CompositeMatcherTests {
	@Test
	public void main() {
		// Given
		Set<Handler> handlers = new HashSet<>();
		handlers.add(new AndroidV1Handler());
		SimpleSelector selector = new SimpleSelector();
		selector.registerCandidates(Handler.class, handlers);

		Map<String, String> request = new HashMap<>();
		request.put("platform", "android");
		request.put("version", "1.0.0");

		// When
		Handler androidV1Handler = selector.select(request, Handler.class);

		// Then
		assertThat(androidV1Handler).isInstanceOf(AndroidV1Handler.class);
	}
}
