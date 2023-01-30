package com.idc.webservice.service;

import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService(name = "TestService",targetNamespace = "http://service.webservice.idc.com")

public interface TestService {
    @WebMethod
    String Test();
}
