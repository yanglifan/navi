package com.github.navi.core.processor;

import com.github.navi.core.MatchResult;
import com.github.navi.core.matcher.EqualsMatcher;
import org.junit.Test;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class EqualsMatcherProcessorTests {

    private EqualsMatcher.Processor equalsMatcherProcessor = new EqualsMatcher.Processor();

    private EqualsMatcher usernameEqualsStark = new EqualsMatcher() {
        @Override
        public String propertyPath() {
            return "username";
        }

        @Override
        public String expectValue() {
            return "stark";
        }

        @Override
        public Class<? extends Annotation> annotationType() {
            return null;
        }
    };


    @Test
    public void simpleMatch() {
        TestRequest testRequest = new TestRequest("stark");
        MatchResult matchResult = equalsMatcherProcessor.process(testRequest, usernameEqualsStark);
        assertThat(matchResult).isEqualTo(MatchResult.ACCEPT);
    }

    @Test
    public void doMatchWithMapRequest() {
        Map<String, String> mapRequest = new HashMap<>();
        mapRequest.put("username", "stark");
        MatchResult matchResult = equalsMatcherProcessor.process(mapRequest, usernameEqualsStark);
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
}