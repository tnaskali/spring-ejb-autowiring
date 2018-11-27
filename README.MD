# A Spring EJB autowiring library for Java

[![Build Status](https://travis-ci.org/tnaskali/spring-ejb-autowiring.svg?branch=master)](https://travis-ci.org/tnaskali/spring-ejb-autowiring)

Before version 5, the Spring Framework included an abstract java EE interceptor that could be used to perform application context initialization and autowiring on EJBs. In version 5, the interceptor was removed from the Spring Framework without offering an alternative but [encouraging former users to implement their own custom interceptor](https://jira.spring.io/browse/SPR-16821).

This library consists of a template for an autowiring interceptor whose implementation is based on Spring's original ```org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor``` as well as a template for a Singleton EJB responsible for holding the shared ApplicationContext instance(s).
