# A Spring EJB autowiring library for Java

![build status](https://github.com/tnaskali/spring-ejb-autowiring/actions/workflows/maven.yml/badge.svg)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=tnaskali_spring-ejb-autowiring&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=tnaskali_spring-ejb-autowiring)

Before version 5, the Spring Framework included an abstract java EE interceptor that could be used to perform application context initialization and autowiring on EJBs. In version 5, the interceptor was removed from the Spring Framework without offering an alternative but [encouraging former users to implement their own custom interceptor](https://jira.spring.io/browse/SPR-16821).

This library consists of a template for an autowiring interceptor whose implementation is based on Spring's original ```org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor``` as well as a template for a Singleton EJB responsible for holding the shared ApplicationContext instance(s).
