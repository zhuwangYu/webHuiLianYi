package com.idc.webservice.service;

import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService(name = "BpmService",targetNamespace = "http://service.webservice.idc.com")
public interface BpmService {
    @WebMethod
    String bpmCreateData();
}
