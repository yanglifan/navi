package com.github.yanglifan.navi.spring;

import com.github.yanglifan.navi.core.matcher.EqualMatcher;
import com.github.yanglifan.navi.core.matcher.VersionMatcher;
import org.springframework.stereotype.Component;

/**
 * @author Yang Lifan
 */


@EqualMatcher(property = "platform", value = "android")
@VersionMatcher(property = "clientVersion", range = "[9.1.0,*]")
@Component
public class AndroidV910CouponHandler implements CouponHandler {
	@Override
	public void handleCoupon(CouponRequest couponRequest) {

	}
}
