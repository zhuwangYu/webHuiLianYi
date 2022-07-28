package com.idc.webservice.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.idc.webservice.service.DemoService;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.jws.WebService;

@WebService(serviceName = "DemoService",targetNamespace = "http://service.webservice.idc.com",
        endpointInterface = "com.idc.webservice.service.DemoService" )
public class DemoServiceImpl implements DemoService {
    private final String baseUrl="https://apistage.huilianyi.com/gateway/";
    private final int MAX_TIME_OUT = 1000*120;
    private final String clientId = "bf221508-973c-47e3-ae17-a448593ee0f5";
    private final String securet = "YWRjZTkyMTEtMDRlZS00YzZlLTgzMmItMTNkNmZjY2JhN2Jj";
    private String getHuilianyiToken(String url){
//        String clientId = "bf221508-973c-47e3-ae17-a448593ee0f5";//需要替换
//        String securet = "YWRjZTkyMTEtMDRlZS00YzZlLTgzMmItMTNkNmZjY2JhN2Jj";//需要替换
        String finUrl = baseUrl.concat(url);//需要替换
        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setConnectionRequestTimeout(MAX_TIME_OUT);
        httpRequestFactory.setConnectTimeout(MAX_TIME_OUT);
        httpRequestFactory.setReadTimeout(MAX_TIME_OUT);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(httpRequestFactory);

        HttpHeaders httpHeaders = new HttpHeaders();
        String authStr = clientId.concat(":").concat(securet);
        String authStrEnc = new String(Base64.encodeBase64(authStr.getBytes()));
        httpHeaders.set("Authorization", "Basic ".concat(authStrEnc));

        MultiValueMap<String, String> formData = new LinkedMultiValueMap();
        formData.add("grant_type", "client_credentials");
        formData.add("scope", "write");

        HttpEntity httpEntity =  new HttpEntity(formData, httpHeaders);
        ResponseEntity<String> exchange = restTemplate.exchange(finUrl, HttpMethod.POST, httpEntity, String.class);
        String respone = exchange.getBody();
        return respone;
//        System.out.println(respone);
    }

    @Override
    public String createHuilianyiData(String url, String jsonData,String methods) {
        String dataUrl = baseUrl.concat(url);
        HttpMethod method = HttpMethod.POST;
        if (methods==null){
            method = HttpMethod.POST;
        }
        if (methods!=null&&methods.equals("GET")){
            method = HttpMethod.GET;
        }
        if (methods!=null&&methods.equals("DELETE")){
            method=HttpMethod.DELETE;
        }
        if (methods!=null&&methods.equals("PUT")){
            method=HttpMethod.PUT;
        }

        String response = getHuilianyiToken("oauth/token");
        JSONObject object = JSONObject.parseObject(response);
        Object obj = object.get("access_token");
        String accessToken = (String) obj;

        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setConnectionRequestTimeout(MAX_TIME_OUT);
        httpRequestFactory.setConnectTimeout(MAX_TIME_OUT);
        httpRequestFactory.setReadTimeout(MAX_TIME_OUT);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(httpRequestFactory);

        JSONObject jsonObject = (JSONObject) JSON.parse(jsonData);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "Bearer  ".concat(accessToken));
        httpHeaders.set("Content-Type","application/json");
        //jsonObject.toJSONString(),
        HttpEntity httpEntity = new HttpEntity(jsonObject.toJSONString(),httpHeaders);
        ResponseEntity<String> exchange = restTemplate.exchange(dataUrl,method, httpEntity, String.class);
        String respone = exchange.getBody();
        return respone;
    }
}
