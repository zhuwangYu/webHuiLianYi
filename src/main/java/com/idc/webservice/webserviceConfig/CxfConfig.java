package com.idc.webservice.webserviceConfig;

import com.idc.webservice.service.BpmService;
import com.idc.webservice.service.HlyService;
import com.idc.webservice.service.TestService;
import com.idc.webservice.service.impl.BpmServiceImpl;
import com.idc.webservice.service.impl.HlyServiceImpl;
import com.idc.webservice.service.impl.TestServiceImpl;
import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.xml.ws.Endpoint;

@Configuration
public class CxfConfig {
    @Bean
    /**ServletRegistrationBean disServlet(){
        return new ServletRegistrationBean( new CXFServlet(),"/erp2huilianyi/*");
    }*/
    ServletRegistrationBean disServlet(){
        return new ServletRegistrationBean( new CXFServlet(),"/erp2other/*");
    }

    @Bean(name = Bus.DEFAULT_BUS_ID)
    public SpringBus springBus(){
        return new SpringBus();
    }

    /**
     * 汇联易
     * @return
     */
    @Bean
    public HlyService demoService(){
        return new HlyServiceImpl();
    }

    @Bean
    public Endpoint hlyEndpoint(){
        EndpointImpl endpoint = new EndpointImpl(springBus(),demoService());
        endpoint.publish("/hly");
        return endpoint;
    }

    /**
     * BPM
     */
    @Bean
    public BpmService bpmService(){
        return new BpmServiceImpl();
    }
    @Bean
    public Endpoint bpmEndpoint(){
        EndpointImpl endpoint = new EndpointImpl(springBus(),bpmService());
        endpoint.publish("/bpm");
        return endpoint;
    }


    /**
     * 测试test
     * @return
     */
    @Bean
    public TestService testService(){
        return new TestServiceImpl();
    }
    @Bean
    public Endpoint testendpoint(){
        EndpointImpl endpoint= new EndpointImpl(springBus(),testService());
        endpoint.publish("/test");
        return endpoint;
    }



}
