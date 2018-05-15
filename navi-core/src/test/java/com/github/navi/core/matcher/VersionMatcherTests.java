package com.github.navi.core.matcher;

import com.github.navi.core.MatchResult;
import org.junit.Test;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Yang Lifan
 */
public class VersionMatcherTests {
    @Test
    public void main() {
        // given
        Map<String, String> versionedRequest = new HashMap<>();
        versionedRequest.put("version", "9.0.11");

        VersionMatcher versionMatcher = new VersionMatcher() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return null;
            }

            @Override
            public String propertyPath() {
                return "version";
            }

            @Override
            public String versionRange() {
                return "[9.0.0,9.1.0)";
            }
        };

        // when
        VersionMatcher.Processor processor = new VersionMatcher.Processor();
        MatchResult matchResult = processor.process(versionedRequest, versionMatcher);

        // then
        assertThat(matchResult).isEqualTo(MatchResult.ACCEPT);
    }
}
