package com.idc.webservice.service;

import com.alibaba.fastjson.JSONObject;
import org.springframework.http.HttpMethod;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

@WebService(name = "HlyService",targetNamespace = "http://service.webservice.idc.com")
public interface HlyService {

    @WebMethod
    String createHuilianyiData(String url, String jsonData, String methods);
}
