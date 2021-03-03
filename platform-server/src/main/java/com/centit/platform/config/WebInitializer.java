package com.centit.platform.config;

import com.centit.framework.config.SystemSpringMvcConfig;
import com.centit.framework.config.WebConfig;
import org.springframework.web.WebApplicationInitializer;

import javax.annotation.Nonnull;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 *
 * @author zou_wy
 * @date 2017/3/29
 */

public class WebInitializer implements WebApplicationInitializer {


    @Override
    public void onStartup(@Nonnull ServletContext servletContext) throws ServletException {
        WebConfig.registerSpringConfig(servletContext, ServiceConfig.class);

        String [] servletUrlPatterns = {"/system/*","/platform/*","/oa/*"};
        WebConfig.registerServletConfig(servletContext, "system",
            "/system/*",
            SystemSpringMvcConfig.class, SwaggerConfig.class);
        WebConfig.registerServletConfig(servletContext, "platform",
            "/platform/*",
            PlatformSpringMvcConfig.class,SwaggerConfig.class);
        WebConfig.registerServletConfig(servletContext, "oa",
            "/oa/*",
            OaComponentSpringMvcConfig.class, SwaggerConfig.class);
        WebConfig.registerRequestContextListener(servletContext);
        WebConfig.registerSingleSignOutHttpSessionListener(servletContext);
        WebConfig.registerCharacterEncodingFilter(servletContext, servletUrlPatterns);
        WebConfig.registerHttpPutFormContentFilter(servletContext, servletUrlPatterns);
        WebConfig.registerHiddenHttpMethodFilter(servletContext, servletUrlPatterns);
        WebConfig.registerRequestThreadLocalFilter(servletContext);
        WebConfig.registerSpringSecurityFilter(servletContext, servletUrlPatterns);
    }


}
