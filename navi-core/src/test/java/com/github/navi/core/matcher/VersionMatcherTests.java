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
    private VersionMatcher.Processor processor = new VersionMatcher.Processor();

    @Test
    public void main() {
        // given
        Map<String, String> v9_0_0_Request = new HashMap<>();
        v9_0_0_Request.put("version", "9.0.0");

        Map<String, String> v9_0_11_Request = new HashMap<>();
        v9_0_11_Request.put("version", "9.0.11");

        Map<String, String> v9_1_0_Request = new HashMap<>();
        v9_1_0_Request.put("version", "9.1.0");

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
        MatchResult v9_0_0_Result = processor.process(v9_0_0_Request, versionMatcher);
        MatchResult v9_0_11_Result = processor.process(v9_0_11_Request, versionMatcher);
        MatchResult v9_1_0_Result = processor.process(v9_1_0_Request, versionMatcher);

        // then
        assertThat(v9_0_0_Result).isEqualTo(MatchResult.ACCEPT);
        assertThat(v9_0_11_Result).isEqualTo(MatchResult.ACCEPT);
        assertThat(v9_1_0_Result).isEqualTo(MatchResult.REJECT);
    }

    @Test
    public void upper_bound_is_all_version() {
        // given
        Map<String, String> v9_0_11_Request = new HashMap<>();
        v9_0_11_Request.put("version", "9.0.11");

        Map<String, String> v9_1_0_Request = new HashMap<>();
        v9_1_0_Request.put("version", "9.1.0");

        Map<String, String> v9_1_20_Request = new HashMap<>();
        v9_1_20_Request.put("version", "9.1.20");

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
                return "[9.1.0,*]";
            }
        };

        // when
        MatchResult v9_0_11_Result = processor.process(v9_0_11_Request, versionMatcher);
        MatchResult v9_1_0_Result = processor.process(v9_1_0_Request, versionMatcher);
        MatchResult v9_1_20_Result = processor.process(v9_1_20_Request, versionMatcher);

        // then
        assertThat(v9_0_11_Result).isEqualTo(MatchResult.REJECT);
        assertThat(v9_1_0_Result).isEqualTo(MatchResult.ACCEPT);
        assertThat(v9_1_20_Result).isEqualTo(MatchResult.ACCEPT);
    }

    @Test
    public void lower_bound_is_all_version() {
        // given
        Map<String, String> v9_0_11_Request = new HashMap<>();
        v9_0_11_Request.put("version", "9.0.11");

        Map<String, String> v9_1_0_Request = new HashMap<>();
        v9_1_0_Request.put("version", "9.1.0");

        Map<String, String> v9_1_20_Request = new HashMap<>();
        v9_1_20_Request.put("version", "9.1.20");

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
                return "[*,9.1.0)";
            }
        };

        // when
        MatchResult v9_0_11_Result = processor.process(v9_0_11_Request, versionMatcher);
        MatchResult v9_1_0_Result = processor.process(v9_1_0_Request, versionMatcher);
        MatchResult v9_1_20_Result = processor.process(v9_1_20_Request, versionMatcher);

        // then
        assertThat(v9_0_11_Result).isEqualTo(MatchResult.ACCEPT);
        assertThat(v9_1_0_Result).isEqualTo(MatchResult.REJECT);
        assertThat(v9_1_20_Result).isEqualTo(MatchResult.REJECT);
    }
}
