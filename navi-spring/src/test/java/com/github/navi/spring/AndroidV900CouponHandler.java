package com.github.navi.spring;

import com.github.navi.core.matcher.EqualsMatcher;
import com.github.navi.core.matcher.VersionMatcher;
import org.springframework.stereotype.Component;

/**
 * @author Yang Lifan
 */
@EqualsMatcher(propertyPath = "platform", expectValue = "android")
@VersionMatcher(propertyPath = "clientVersion", versionRange = "[9.0.0,9.1.0)")
@Component
public class AndroidV900CouponHandler implements CouponHandler {
	@Override
	public void handleCoupon(CouponRequest couponRequest) {

	}
}
