package com.github.navi.core.matcher;

import com.github.navi.core.MatchResult;
import com.github.navi.core.MatcherType;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Yang Lifan
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@MatcherType(processor = VersionMatcher.Processor.class)
public @interface VersionMatcher {
	String ALL_VERSIONS = "*";

	String propertyPath() default "version";

	String versionRange();

	class Processor extends OnePropertyMatcherProcessor<VersionMatcher> {
		@Override
		protected String getPropertyPath(VersionMatcher matcherAnnotation) {
			return matcherAnnotation.propertyPath();
		}

		@Override
		protected MatchResult doProcess(Object request, VersionMatcher matcherAnnotation) {
			String version = request.toString();
			if (StringUtils.isEmpty(version) || ALL_VERSIONS.equals(version)) {
				return MatchResult.NEUTRAL;
			}

			VersionRange versionRange = VersionRange.fromString(matcherAnnotation.versionRange());

			boolean within = versionRange.within(version);

			return within ? MatchResult.ACCEPT : MatchResult.REJECT;
		}
	}

	class VersionRange {
		private boolean isOpenLowerBound;
		private Version lowerBound;
		private boolean isOpenHigherBound;
		private Version higherVersion;

		private VersionRange(boolean isOpenLowerBound, Version lowerBound,
							 boolean isOpenHigherBound, Version higherVersion) {
			this.isOpenLowerBound = isOpenLowerBound;
			this.lowerBound = lowerBound;
			this.isOpenHigherBound = isOpenHigherBound;
			this.higherVersion = higherVersion;
		}

		static VersionRange fromString(String versionRangeString) {
			// TODO Validate
			String[] versions = versionRangeString.split(",");

			boolean isOpenLowerBound = versions[0].startsWith("(");
			String lowerBoundVersionValue = versions[0].substring(1);
			Version lowerVersion = new Version(lowerBoundVersionValue);

			boolean isOpenHigherBound = versions[1].endsWith(")");
			String higherBoundVersionValue = versions[1].substring(0, versions[1].length() - 1);
			Version higherVersion = new Version(higherBoundVersionValue);

			return new VersionRange(isOpenLowerBound, lowerVersion, isOpenHigherBound,
					higherVersion);
		}

		boolean within(String comparedVersionValue) {
			Version comparedVersion = new Version(comparedVersionValue);
			return isContentLowerBound(comparedVersion) && isContentHigherBound(comparedVersion);
		}

		private boolean isContentHigherBound(Version comparedVersion) {
			boolean isContentHigherBound;
			if (isOpenHigherBound) {
				isContentHigherBound = higherVersion.compareTo(comparedVersion) > 0;
			} else {
				isContentHigherBound = higherVersion.compareTo(comparedVersion) >= 0;
			}
			return isContentHigherBound;
		}

		private boolean isContentLowerBound(Version comparedVersion) {
			boolean isContentLowerBound;
			if (isOpenLowerBound) {
				isContentLowerBound = lowerBound.compareTo(comparedVersion) < 0;
			} else {
				isContentLowerBound = lowerBound.compareTo(comparedVersion) <= 0;
			}
			return isContentLowerBound;
		}
	}

	class Version implements Comparable<Version> {
		private boolean isAny;
		private int major;
		private int minor;
		private int patch;

		Version(String rawVersion) {
			if (ALL_VERSIONS.equals(rawVersion)) {
				this.isAny = true;
			} else {
				String[] versionArray = rawVersion.split("\\.");
				major = Integer.valueOf(versionArray[0]);
				minor = Integer.valueOf(versionArray[1]);
				patch = Integer.valueOf(versionArray[2]);

			}
		}

		@Override
		public int compareTo(Version o) {
			if (isAny) {
				return 0;
			}

			if (this.major != o.major) {
				return this.major - o.major;
			} else if (this.minor != o.minor) {
				return this.minor - o.minor;
			} else {
				return this.patch - o.patch;
			}
		}
	}
}
