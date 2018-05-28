package com.github.yanglifan.navi.spring;

import com.github.yanglifan.navi.core.matcher.EqualMatcher;
import com.github.yanglifan.navi.core.matcher.VersionMatcher;
import org.springframework.stereotype.Component;

/**
 * @author Yang Lifan
 */
@EqualMatcher(propertyPath = "platform", expectValue = "android")
@VersionMatcher(propertyPath = "clientVersion", versionRange = "[9.0.0,9.1.0)")
@Component
public class AndroidV900CouponHandler implements CouponHandler {
	@Override
	public void handleCoupon(CouponRequest couponRequest) {

	}
}
