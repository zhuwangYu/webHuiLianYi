package com.idc.webservice.service;

import com.alibaba.fastjson.JSONObject;
import org.springframework.http.HttpMethod;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

@WebService(name = "DemoService",targetNamespace = "http://service.webservice.idc.com")
public interface DemoService {

    @WebMethod
    String createHuilianyiData(String url, String jsonData, String methods);
}
