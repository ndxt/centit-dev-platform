package com.centit.project.demo.config;

import com.centit.framework.config.WebConfig;
import com.centit.support.file.PropertiesReader;
import com.centit.support.json.JSONOpt;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Properties;

/**
 * Created by zou_wy on 2017/3/29.
 */

public abstract class WebInitializerConfig  {
        public static void setupWebServices(ServletContext servletContext) throws ServletException {
        String [] servletUrlPatterns = {"/system/*","/metadata/*","/metaform/*", "/workflow/*",
                "/dbdesign/*","/platform/*","/dde/*","/oa/*","/fileserver/*","/help/*","/task/*","/im/*"};
        WebConfig.registerSpringConfig(servletContext, ServiceConfig.class);


        WebConfig.registerServletConfig(servletContext, "metadata",
                "/metadata/*",
                MetaDataSpringMvcConfig.class, SwaggerConfig.class);

        WebConfig.registerServletConfig(servletContext, "workflow",
            "/workflow/*",
            WorkflowSpringMvcConfig.class, SwaggerConfig.class);

        WebConfig.registerServletConfig(servletContext, "platform",
                "/platform/*",
                PlatformSpringMvcConfig.class, SwaggerConfig.class);
        WebConfig.registerServletConfig(servletContext, "metaform",
                "/metaform/*",
                MetaformSpringMvcConfig.class, SwaggerConfig.class);


        WebConfig.registerServletConfig(servletContext, "dde",
            "/dde/*",
            DdeSpringMvcConfig.class, SwaggerConfig.class);
        WebConfig.registerServletConfig(servletContext, "oa",
            "/oa/*",
            OaComponentSpringMvcConfig.class, SwaggerConfig.class);

        WebConfig.registerServletConfig(servletContext, "fileserver",
            "/fileserver/*",
            FileServerSpringMvcConfig.class,SwaggerConfig.class);

        WebConfig.registerServletConfig(servletContext, "task",
            "/task/*",
            TaskSpringMvcConfig.class, SwaggerConfig.class);
        WebConfig.registerServletConfig(servletContext, "im",
            "/im/*",
            IMSpringMvcConfig.class,SwaggerConfig.class);

        //dubbo hessian协议使用
       /* ServletRegistration.Dynamic dubbo = servletContext.addServlet("dubbo", DispatcherServlet.class);
        dubbo.addMapping("/*");*/

        WebConfig.registerRequestContextListener(servletContext);
        WebConfig.registerSingleSignOutHttpSessionListener(servletContext);
        WebConfig.registerCharacterEncodingFilter(servletContext, servletUrlPatterns);
        WebConfig.registerHttpPutFormContentFilter(servletContext, servletUrlPatterns);
        WebConfig.registerHiddenHttpMethodFilter(servletContext, servletUrlPatterns);
        WebConfig.registerRequestThreadLocalFilter(servletContext);
        WebConfig.registerSpringSecurityFilter(servletContext, servletUrlPatterns);

        Properties properties = PropertiesReader.getClassPathProperties("/system.properties");
        String jdbcUrl = properties.getProperty("jdbc.url");

        if(jdbcUrl.startsWith("jdbc:h2")){
            WebConfig.initializeH2Console(servletContext);
        }

        JSONOpt.fastjsonGlobalConfig();
    }
}
