package com.github.navi.spring;

import com.github.navi.core.matcher.EqualMatcher;
import com.github.navi.core.matcher.VersionMatcher;
import org.springframework.stereotype.Component;

/**
 * @author Yang Lifan
 */


@EqualMatcher(propertyPath = "platform", expectValue = "android")
@VersionMatcher(propertyPath = "clientVersion", versionRange = "[9.1.0,*]")
@Component
public class AndroidV910CouponHandler implements CouponHandler {
	@Override
	public void handleCoupon(CouponRequest couponRequest) {

	}
}
