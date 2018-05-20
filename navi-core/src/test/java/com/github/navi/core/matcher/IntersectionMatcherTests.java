package com.github.navi.core.matcher;

import com.github.navi.core.Handler;
import com.github.navi.core.SimpleSelector;
import org.assertj.core.util.Sets;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class IntersectionMatcherTests {
	@Test
	public void intersect_string() {
		Map<String, String> request = new HashMap<>();
		request.put("name", "stark, thor");

		SimpleSelector simpleSelector = new SimpleSelector();
		simpleSelector.registerCandidates(Handler.class, Sets.newLinkedHashSet(new IntersectHandler()));

		Handler handler = simpleSelector.select(request, Handler.class);

		assertThat(handler).isInstanceOf(IntersectHandler.class);
	}
}


@IntersectionMatcher(propertyPath = "name", expectValue = {"stark", "rogers"})
class IntersectHandler implements Handler {

}
