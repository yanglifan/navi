package com.github.navi.core.matcher;

import com.github.navi.core.Handler;
import com.github.navi.core.MatchResult;
import com.github.navi.core.MatcherDefinition;
import com.github.navi.core.SimpleSelector;
import org.junit.Before;
import org.junit.Test;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class EqualMatcherTest {

	private EqualMatcher.Processor equalsMatcherProcessor = new EqualMatcher.Processor();

	private SimpleSelector selector;

	private EqualMatcher starkOrThor = new EqualMatcher() {
		@Override
		public String propertyPath() {
			return "username";
		}

		@Override
		public String[] expectValue() {
			return new String[]{"stark", "thor"};
		}

		@Override
		public Class<? extends Annotation> annotationType() {
			return null;
		}
	};

	@Before
	public void setUp() {
		selector = new SimpleSelector();
	}

	@Test
	public void simple() {
		TestRequest testRequest = new TestRequest("stark");
		MatchResult matchResult = equalsMatcherProcessor.process(testRequest, new MatcherDefinition<>(starkOrThor));
		assertThat(matchResult).isEqualTo(MatchResult.ACCEPT);
	}

	@Test
	public void match_multi_values() {
		Map<String, String> request = new HashMap<>();
		request.put("username", "stark");
		MatchResult matchResult = equalsMatcherProcessor.process(request, new MatcherDefinition<>(starkOrThor));
		assertThat(matchResult).isEqualTo(MatchResult.ACCEPT);

		request.put("username", "thor");
		matchResult = equalsMatcherProcessor.process(request, new MatcherDefinition<>(starkOrThor));
		assertThat(matchResult).isEqualTo(MatchResult.ACCEPT);

		request.put("username", "rogers");
		matchResult = equalsMatcherProcessor.process(request, new MatcherDefinition<>(starkOrThor));
		assertThat(matchResult).isEqualTo(MatchResult.REJECT);
	}

	@Test
	public void nested_prop() {
		// Given
		Map<String, Object> request = new HashMap<>();
		request.put("propLevel1", new TestRequest("stark"));

		EqualMatcher equalMatcher = new EqualMatcher() {
			@Override
			public String propertyPath() {
				return "propLevel1.username";
			}

			@Override
			public String[] expectValue() {
				return new String[]{"stark"};
			}

			@Override
			public Class<? extends Annotation> annotationType() {
				return null;
			}
		};

		// When
		MatchResult matchResult = equalsMatcherProcessor.process(request, new MatcherDefinition<>(equalMatcher));

		// Then
		assertThat(matchResult).isEqualTo(MatchResult.ACCEPT);
	}

	@Test
	public void repeatable() {
		// Given
		Map<String, String> mj1 = new HashMap<>();
		mj1.put("firstName", "michael");
		mj1.put("lastName", "jordan");

		Map<String, String> mj2 = new HashMap<>();
		mj2.put("firstName", "michael");
		mj2.put("lastName", "jackson");

		selector.registerCandidate(Handler.class, new MichaelJordan());

		// When
		Handler h1 = selector.select(mj1, Handler.class);
		Handler h2 = selector.select(mj2, Handler.class);

		// Then
		assertThat(h1).isInstanceOf(MichaelJordan.class);
		assertThat(h2).isNull();
	}

	@Test
	public void doMatchWithMapRequest() {
		Map<String, String> mapRequest = new HashMap<>();
		mapRequest.put("username", "stark");
		MatchResult matchResult = equalsMatcherProcessor.process(mapRequest,
				new MatcherDefinition<>(starkOrThor));
		assertThat(matchResult).isEqualTo(MatchResult.ACCEPT);
	}

	public class TestRequest {
		private String username;

		TestRequest(String username) {
			this.username = username;
		}

		/**
		 * Used via reflection.
		 */
		@SuppressWarnings("all")
		public String getUsername() {
			return username;
		}
	}

	@EqualMatcher(propertyPath = "firstName", expectValue = "michael")
	@EqualMatcher(propertyPath = "lastName", expectValue = "jordan")
	class MichaelJordan implements Handler {

	}
}