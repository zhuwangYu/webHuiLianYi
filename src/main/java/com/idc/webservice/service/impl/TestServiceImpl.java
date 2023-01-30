package com.idc.webservice.service.impl;

import com.idc.webservice.service.TestService;

import javax.jws.WebService;

@WebService(serviceName = "TestService",targetNamespace = "http://service.webservice.idc.com",
        endpointInterface = "com.idc.webservice.service.TestService" )
public class TestServiceImpl implements TestService {
    @Override
    public String Test() {
        return "test";
    }
}
