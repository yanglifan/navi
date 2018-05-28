package com.github.yanglifan.navi.core;

import com.github.yanglifan.navi.core.matcher.EqualMatcher;
import com.github.yanglifan.navi.core.matcher.VersionMatcher;
import org.junit.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@VersionMatcher(versionRange = "[1.0.0,2.0.0)")
@EqualMatcher(propertyPath = "platform", expectValue = "android")
@CompositeMatcherType
@interface AndroidV1Matcher {

}

@AndroidV1Matcher
class AndroidV1Handler implements Handler {

}

/**
 * @author Yang Lifan
 */
public class CompositeMatcherTest {
	@Test
	public void main() {
		// Given
		Set<Handler> handlers = new HashSet<>();
		handlers.add(new AndroidV1Handler());
		SimpleSelector selector = new SimpleSelector();
		selector.registerCandidates(Handler.class, handlers);

		Map<String, String> request1 = new HashMap<>();
		request1.put("platform", "android");
		request1.put("version", "1.0.0");


		Map<String, String> request2 = new HashMap<>();
		request2.put("platform", "android");
		request2.put("version", "2.0.0");

		// When
		Handler androidV1Handler = selector.select(request1, Handler.class);
		Handler androidV2Handler = selector.select(request2, Handler.class);

		// Then
		assertThat(androidV1Handler).isInstanceOf(AndroidV1Handler.class);
		assertThat(androidV2Handler).isNull();
	}
}
