package com.github.navi.core.matcher;

import com.github.navi.core.Handler;
import com.github.navi.core.SimpleSelector;
import org.assertj.core.util.Sets;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class IntersectMatcherTests {
	@Test
	public void intersect_string() {
		Map<String, String> request = new HashMap<>();
		request.put("name", "stark, thor");

		SimpleSelector simpleSelector = new SimpleSelector();
		simpleSelector.registerCandidates(Handler.class, Sets.newLinkedHashSet(new IntersectHandler()));

		Handler handler = simpleSelector.select(request, Handler.class);

		assertThat(handler).isInstanceOf(IntersectHandler.class);
	}

	@Test
	public void intersect_collection() {
		Map<String, List<String>> request = new HashMap<>();
		request.put("name", Arrays.asList("stark", "thor"));

		SimpleSelector simpleSelector = new SimpleSelector();
		simpleSelector.registerCandidates(Handler.class, Sets.newLinkedHashSet(new IntersectHandler()));

		Handler handler = simpleSelector.select(request, Handler.class);

		assertThat(handler).isInstanceOf(IntersectHandler.class);
	}
}


@IntersectMatcher(propertyPath = "name", expectValue = {"stark", "rogers"})
class IntersectHandler implements Handler {

}
