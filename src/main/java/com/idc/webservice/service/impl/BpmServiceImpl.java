package com.idc.webservice.service.impl;

import com.actionsoft.bpms.api.OpenApiClient;
import com.actionsoft.bpms.api.common.ApiResponse;
import com.actionsoft.sdk.service.model.TaskQueryModel;
import com.actionsoft.sdk.service.response.MapResponse;
import com.actionsoft.sdk.service.response.StringArrayResponse;
import com.actionsoft.sdk.service.response.StringResponse;
import com.actionsoft.sdk.service.response.process.ProcessInstResponse;
import com.actionsoft.sdk.service.response.task.TaskInstGetResponse;
import com.idc.webservice.service.BpmService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jws.WebService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@WebService(serviceName = "BpmService",targetNamespace = "http://service.webservice.idc.com",
        endpointInterface = "com.idc.webservice.service.BpmService")
public class BpmServiceImpl implements BpmService {
    Logger logger = LoggerFactory.getLogger(BpmServiceImpl.class);
    public static String apiServer = "http://localhost:8088/portal/api";
    //公钥
    public static String accessKey = "idcbpm";
    //私钥
    public static String secret = "IDCBPM";
    @Override
    public String bpmCreateData() {
        logger.info("test");
        String res = null;
        String apiMethod = "process.create";
        Map<String,Object> args = new HashMap<String,Object>();
        //模型ID
        args.put("processDefId","obj_39da8931ed40436f8af61610c56c51a2");
        args.put("uid","admin");
        args.put("title","BPM测试--peter");
        OpenApiClient client = new OpenApiClient(apiServer,accessKey,secret);
        //流程创建
        ProcessInstResponse response = client.exec(apiMethod,args,ProcessInstResponse.class);
        //流程创建成功处理
        if (response.getResult().equals("ok")){
            //创建成功，获取相应信息
            String processInstId = response.getData().getId();//流程实例序号
            System.out.println("processInstId="+processInstId);
            logger.info(response.getMsg());
            //启动流程
            apiMethod="process.start";
            args=new HashMap<String,Object>();
            args.put("processInstId",processInstId);
            client = new OpenApiClient(apiServer,accessKey,secret);
            ApiResponse processStartRes = client.exec(apiMethod,args,ApiResponse.class);
            //流程启动成功处理
            if (processStartRes.getResult().equals("ok")){
                System.out.println("启动流程成功");
                //绑定数据
                apiMethod="bo.create";
                args = new HashMap<String,Object>();
                args.put("boName","BO_EU_IDC_DEMO");//单头表名

                Map<String,Object> recordData = new HashMap<>();
                recordData.put("DH","111");
                recordData.put("XM","222");
                recordData.put("XB","333");

                args.put("recordData",recordData);
                args.put("bindId",processInstId);
                client= new OpenApiClient(apiServer,accessKey,secret);
                StringResponse boMainCreateRes = client.exec(apiMethod,args,StringResponse.class);
                //主表数据创建成功处理
                if (boMainCreateRes.getResult().equals("ok")){
                    System.out.println("主表数据创建成功");
                    //创建单身数据
                    apiMethod = "bo.creates";
                    args = new HashMap<String,Object>();
                    args.put("boName","BO_EU_IDC_DEMO_DTL");//单身表名
                    List<Map<String,Object>> recordDatas = new ArrayList<>();
                    Map<String,Object> recordData1 = new HashMap<>();
                    recordData1.put("xx","xxxxx");
                    recordData1.put("ss","sssss");
                    recordDatas.add(recordData1);
                    Map<String,Object> recordData2 = new HashMap<>();
                    recordData2.put("xx","xxxxx");
                    recordData2.put("ss","sssss");
                    recordDatas.add(recordData2);

                    args.put("recordDatas",recordDatas);
                    args.put("bindId",processInstId);
                    client = new OpenApiClient(apiServer,accessKey,secret);
                    StringArrayResponse boDataCreateRes= client.exec(apiMethod,args,StringArrayResponse.class);
                    //单身创建成功
                    if (boDataCreateRes.getResult().equals("ok")){
                        //查询任务
                        apiMethod ="task.query";
                        args = new HashMap<String,Object>();

                        TaskQueryModel taskQueryModel = new TaskQueryModel();
                        taskQueryModel.setProcessInstId(processInstId);

                        args.put("tqm",taskQueryModel);

                        client = new OpenApiClient(apiServer,accessKey,secret);
                        TaskInstGetResponse taskQueryRes = client.exec(apiMethod,args,TaskInstGetResponse.class);
                        //查询成功
                        if (taskQueryRes.getResult().equals("ok")){
                            String taskInstId = taskQueryRes.getData().getId();
                            System.out.println(("任务查询id"+taskInstId));

                            apiMethod="task.complete";
                            args = new HashMap<String,Object>();
                            args.put("taskInstId", taskInstId);
                            args.put("uid","admin" );
                            client = new OpenApiClient(apiServer, accessKey, secret);
                            MapResponse commitTaskRes = client.exec(apiMethod, args, MapResponse.class);
                            if (commitTaskRes.getResult().equals("ok")){
                                return "恭喜你，任务提交成功";
                            }
                        }else {
                            res = taskQueryRes.getMsg();
                        }
                    }else {
                        res = boDataCreateRes.getMsg();
                    }
                }else{
                    res = boMainCreateRes.getMsg();
                }
            }else {
                res = processStartRes.getMsg();
            }
        }else {
            res=response.getMsg();
        }
        return res;
    }
}
