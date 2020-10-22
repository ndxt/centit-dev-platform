package com.centit.bdbclient;

import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.security.model.CentitPasswordEncoder;
import com.centit.framework.security.model.CentitUserDetailsService;
import com.centit.framework.staticsystem.service.impl.UserDetailsServiceImpl;
import com.centit.support.algorithm.BooleanBaseOpt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

@PropertySource("classpath:system.properties")
public class AppSystemBeanConfig implements EnvironmentAware{

    private Environment env;

    @Autowired
    @Override
    public void setEnvironment(final Environment environment) {
        if(environment!=null) {
            this.env = environment;
        }
    }

    @Autowired
    private CentitPasswordEncoder passwordEncoder;

    @Bean
    public AutowiredAnnotationBeanPostProcessor autowiredAnnotationBeanPostProcessor() {
        return new AutowiredAnnotationBeanPostProcessor();
    }

    @Bean
    @Lazy(value = false)
    public PlatformEnvironment platformEnvironment() {
        Boolean ipEnable = env.getProperty("centit.ip.enable", Boolean.class);
        if(null!=ipEnable && ipEnable){
            IPClientPlatformEnvironment ipPlatformEnvironment = new IPClientPlatformEnvironment();
            ipPlatformEnvironment.setTopOptId(env.getProperty("centit.ip.topoptid"));
            ipPlatformEnvironment.createPlatAppSession(
                env.getProperty("centit.ip.home"),
                BooleanBaseOpt.castObjectToBoolean(env.getProperty("centit.ip.auth.enable"),false),
                env.getProperty("centit.ip.auth.usercode"),
                env.getProperty("centit.ip.auth.password"));
            //ipPlatformEnvironment.setPasswordEncoder(passwordEncoder);
            return ipPlatformEnvironment;
        }else {
            JsonPlatformEnvironment jsonPlatformEnvironment = new JsonPlatformEnvironment();
            jsonPlatformEnvironment.setAppHome(env.getProperty("app.home"));
            jsonPlatformEnvironment.setPasswordEncoder(passwordEncoder);
            return jsonPlatformEnvironment;
        }

    }

    @Bean
    public CentitUserDetailsService centitUserDetailsService(@Autowired PlatformEnvironment platformEnvironment) {
        UserDetailsServiceImpl userDetailsService = new UserDetailsServiceImpl();
        userDetailsService.setPlatformEnvironment(platformEnvironment);
        return userDetailsService;
    }

    @Bean
    public HttpSessionCsrfTokenRepository csrfTokenRepository() {
        return new HttpSessionCsrfTokenRepository();
    }


}
