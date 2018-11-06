/*
 * Copyright 2018 Yang Lifan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.iqiyi.navi.core.matcher;

import com.iqiyi.navi.core.MatchResult;
import com.iqiyi.navi.core.MatcherType;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 1. If the version range is empty and the version from the request is not empty, then the result
 * will be {@link MatchResult.MatchType#REJECT}.
 *
 * @author Yang Lifan
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@MatcherType(processor = VersionMatcher.Processor.class)
public @interface VersionMatcher {
	String ALL_VERSIONS = "*";

	String property() default "version";

	String range() default "";

	class Processor extends OnePropertyMatcherProcessor<VersionMatcher> {
		@Override
		protected String getPropertyPath(VersionMatcher matcherAnnotation) {
			return matcherAnnotation.property();
		}

		@Override
		protected MatchResult doProcess(Object request, VersionMatcher matcher,
				String[] expectValues) {
			String versionValue = request.toString();
			String versionRangeValue = expectValues[0];

			if (emptyOrAll(versionValue)) {
				return MatchResult.neutral();
			}

			if (StringUtils.isEmpty(versionRangeValue)) {
				return MatchResult.reject();
			}

			VersionRange versionRange = VersionRange.fromString(versionRangeValue);

			boolean within = versionRange.within(versionValue);

			return within ? MatchResult.accept() : MatchResult.reject();
		}

		private boolean emptyOrAll(String version) {
			return StringUtils.isEmpty(version) || ALL_VERSIONS.equals(version);
		}

		@Override
		protected String[] getMatcherValue(VersionMatcher matcher) {
			return new String[]{matcher.range()};
		}

		@Override
		protected String aliasName() {
			return "range";
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
			if (higherVersion.isAny) {
				return true;
			}

			if (isOpenHigherBound) {
				return higherVersion.compareTo(comparedVersion) > 0;
			} else {
				return higherVersion.compareTo(comparedVersion) >= 0;
			}
		}

		private boolean isContentLowerBound(Version comparedVersion) {
			if (lowerBound.isAny) {
				return true;
			}

			if (isOpenLowerBound) {
				return lowerBound.compareTo(comparedVersion) < 0;
			} else {
				return lowerBound.compareTo(comparedVersion) <= 0;
			}
		}
	}

	class Version implements Comparable<Version> {
		private boolean isAny;
		private int major;
		private int minor;
		private int patch;

		Version(String rawVersion) {
			// TODO Check version format
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
