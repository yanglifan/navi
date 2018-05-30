# Navi: Annotation Based Component Selector

[![](https://travis-ci.com/yanglifan/navi.svg?branch=master)](https://travis-ci.com/yanglifan/navi)
[![codecov](https://codecov.io/gh/yanglifan/navi/branch/master/graph/badge.svg)](https://codecov.io/gh/yanglifan/navi)
[![](https://maven-badges.herokuapp.com/maven-central/com.github.yanglifan.navi/navi-core/badge.svg)](http://search.maven.org/#search|gav|1|g:"com.github.yanglifan.navi"%20AND%20a:"navi-core")
[![](https://img.shields.io/badge/License-Apache%202-blue.svg)](LICENSE)

## Why?

The reason to create this project is to simplify the business system design and dev. 

Why this project can make these works to be more simple? Actually, at the beginning, most of business systems are very simple, may be just a CRUD system. But along with the business growth, the logic will become to be more and more complex. For one function, amount of branches will be generated:
1. Different client types (PC Web, H5 Web, Android, IOS, Phone, Pad, TV, XBox...)
1. Different client versions
1. Different countries and areas
1. Different types of users
1. Other business dimensions...

These different branches will have different logic, but still need to be integrated with the trunk logic. So how to organize the code? With if...else and switch? That would be a disaster for the maintainability and extendability.

With Navi, developers can implement the complex logic with elegant way.
 
## How?
 
```java
@EqualMatcher(property = "clientType", value = "android")
@VersionMatcher(range = "[1.0.0,2.0.0)")
public class AndroidV1OrderCreateHandler implements OrderCreateHandler {

}

OrderRequest request = new OrderRequest():
request.setClientType("android");
request.setVersion("1.5.0");

// this handler is AndroidOrderCreateHandler
public class OrderCreateService {
    public OrderCreateResponse createOrder(OrderRequest request) {
        // ...
        // Trunk Logic
        // ...
        
        // Branch Logic
        OrderCreateHandler handler = selector.select(request, OrderCreateHandler.class);
        handler.handle(request);
        
        // ...
        // Trunk Logic
        // ...
    }
}
```
