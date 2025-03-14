package com.centit.locode.platform.all.config;

import com.centit.framework.config.WebConfig;
import com.centit.support.file.PropertiesReader;
import org.springframework.web.WebApplicationInitializer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Properties;

/**
 * Created by zou_wy on 2017/3/29.
 */

public class WebInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        String [] servletUrlPatterns = {"/system/*","/metadata/*","/metaform/*", "/workflow/*",
                "/dbdesign/*","/platform/*","/dde/*","/oa/*","/fileserver/*","/help/*","/task/*","/im/*"};
        WebConfig.registerSpringConfig(servletContext, ServiceConfig.class);
        WebConfig.registerServletConfig(servletContext, "system",
                "/system/*",
                FrameworkSpringMvcConfig.class, SwaggerConfig.class);

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

        WebConfig.registerServletConfig(servletContext, "dbdesign",
                "/dbdesign/*",
                DBDesignSpringMvcConfig.class, SwaggerConfig.class);

        WebConfig.registerServletConfig(servletContext, "dde",
            "/dde/*",
            DdeSpringMvcConfig.class, SwaggerConfig.class);
        WebConfig.registerServletConfig(servletContext, "oa",
            "/oa/*",
            OaComponentSpringMvcConfig.class, SwaggerConfig.class);

        WebConfig.registerServletConfig(servletContext, "fileserver",
            "/fileserver/*",
            FileServerSpringMvcConfig.class,SwaggerConfig.class);
        WebConfig.registerServletConfig(servletContext, "help",
            "/help/*",
            WorkOrderSpringMvcConfig.class, SwaggerConfig.class);
        WebConfig.registerServletConfig(servletContext, "task",
            "/task/*",
            TaskSpringMvcConfig.class, SwaggerConfig.class);
        WebConfig.registerServletConfig(servletContext, "im",
            "/im/*",
            IMSpringMvcConfig.class,SwaggerConfig.class);

        WebConfig.registerRequestContextListener(servletContext);
        WebConfig.registerSingleSignOutHttpSessionListener(servletContext);
        WebConfig.registerCharacterEncodingFilter(servletContext, servletUrlPatterns);
        WebConfig.registerHttpPutFormContentFilter(servletContext, servletUrlPatterns);
        WebConfig.registerHiddenHttpMethodFilter(servletContext, servletUrlPatterns);
        WebConfig.registerRequestThreadLocalFilter(servletContext);
        WebConfig.registerAssertUserLoginFilter(servletContext, new String[] {
            "/platform/*", "/dde/packetDraft/*", "/dde/query/*",
            "/dde/taskLog/*", "/metadata/*", "/dbdesign/*", "/workflow/*"
        });
        WebConfig.registerSpringSecurityFilter(servletContext, servletUrlPatterns);

        Properties properties = PropertiesReader.getClassPathProperties("/system.properties");
        String jdbcUrl = properties.getProperty("jdbc.url");

        if(jdbcUrl !=null && jdbcUrl.startsWith("jdbc:h2")){
            WebConfig.initializeH2Console(servletContext);
        }
    }
}
