package com.idc.webservice.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.idc.webservice.service.HlyService;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.jws.WebService;

@WebService(serviceName = "HlyService",targetNamespace = "http://service.webservice.idc.com",
        endpointInterface = "com.idc.webservice.service.HlyService")
public class HlyServiceImpl implements HlyService {
    Logger logger = LoggerFactory.getLogger(HlyServiceImpl.class);

//    private final String baseUrl="https://apistage.huilianyi.com/gateway/";
    private static final String baseUrl="https://api.huilianyi.com/gateway/";
    private static final int MAX_TIME_OUT = 1000*120;
    private static final String clientId = "ccf8e85b-2d30-457a-861b-90514317368c";
    private static final String securet = "YjIwZmJiYTItYzAzYi00Y2E0LWFjN2ItZWVmODQ4MzIyNTIx";
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
//        System.out.println(exchange);
        if (exchange.getStatusCodeValue()!=200||exchange.getBody().isEmpty()){
            logger.error("token生成失败"+" return:"+exchange);
        }
        String response = exchange.getBody();
        return response;
//        System.out.println(respone);
    }

    @Override
    public String createHuilianyiData(String url, String jsonData,String methods) {
        String dataUrl = baseUrl.concat(url);
        HttpMethod method = null;
        if (methods==null||methods.equals("POST")){
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
        if (response==null){
            return "token获取失败";
        }
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
        HttpEntity httpEntity = null;
        if (methods.equals("POST")) {
            httpEntity = new HttpEntity(jsonObject.toJSONString(), httpHeaders);
        }
        if (methods.equals("GET")){
            httpEntity = new HttpEntity(null,httpHeaders);
        }
        ResponseEntity<String> exchange = restTemplate.exchange(dataUrl,method, httpEntity, String.class);
        if (exchange.getBody().isEmpty()){
            logger.error("汇联易的接口调用失败: "+" url:"+dataUrl+"  return："+exchange);
            return "error";
        }
        String responseData = exchange.getBody();
        JSONObject jsonObject1 = JSON.parseObject(responseData);
        if (jsonObject1.getString("errorCode").equals("0000")){
            if (url.equals("budget-service/api/open/budget/occupy")){
                String data = jsonObject1.getString("data");
                JSONObject json = JSON.parseObject(data);
                String messageLevel = json.getString("messageLevel");
                if (messageLevel.equals("NO_MESSAGE")||messageLevel.equals("COMPEL")){
                    logger.info("url:"+baseUrl.concat(url)+" 调用成功  data:"+jsonData+"  method:"+methods+" return: "+exchange);
                    return "success";
                }else {
                    logger.error("url:"+baseUrl.concat(url)+" 调用失败  data:"+jsonData+"  method:"+methods+" return: "+exchange);
                    return json.getString("errorMessage");
                }
            }else {
                logger.info("url:"+baseUrl.concat(url)+" 调用成功  data:"+jsonData+"  method:"+methods+" return: "+exchange);
                return "success";
            }

        }else {
            logger.error("url:"+baseUrl.concat(url)+" 调用失败  data:"+jsonData+"  method:"+methods+" return: "+exchange);
            return jsonObject1.getString("message");
        }


    }
}
