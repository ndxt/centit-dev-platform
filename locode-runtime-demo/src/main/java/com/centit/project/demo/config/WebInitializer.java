package com.centit.project.demo.config;

import com.centit.framework.config.SystemSpringMvcConfig;
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
        String [] servletUrlPatterns = {"/system/*","/metadata/*","/metaform/*",
                "/dbdesign/*","/runtime/*","/dde/*"};
              //, "/workflow/*", "/oa/*","/fileserver/*","/help/*","/task/*","/im/*"};
        WebConfig.registerSpringConfig(servletContext, ServiceConfig.class);

        WebConfig.registerServletConfig(servletContext, "system",
                "/system/*",
                SystemSpringMvcConfig.class, SwaggerConfig.class);

        WebConfig.registerServletConfig(servletContext, "metadata",
                "/metadata/*",
                MetaDataSpringMvcConfig.class, SwaggerConfig.class);

        WebConfig.registerServletConfig(servletContext, "platform",
                "/runtime/*",
                RuntimeSpringMvcConfig.class, SwaggerConfig.class);

        WebConfig.registerServletConfig(servletContext, "metaform",
                "/metaform/*",
                MetaformSpringMvcConfig.class, SwaggerConfig.class);


        WebConfig.registerServletConfig(servletContext, "dde",
            "/dde/*",
            DdeSpringMvcConfig.class, SwaggerConfig.class);


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

    }
}
