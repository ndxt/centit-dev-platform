package com.centit.project.demo.config;

import com.centit.fileserver.utils.SystemTempFileUtils;
import com.centit.framework.common.SysParametersUtils;
import com.centit.framework.common.WebOptUtils;
import com.centit.framework.components.CodeRepositoryCache;
import com.centit.framework.components.OperationLogCenter;
import com.centit.framework.model.adapter.NotificationCenter;
import com.centit.framework.model.adapter.OperationLogWriter;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.support.json.JSONOpt;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.io.File;

/**
 * Created by codefan on 17-7-6.
 */
public class InstantiationServiceBeanPostProcessor implements ApplicationListener<ContextRefreshedEvent> , ApplicationContextAware {

    @Autowired
    protected NotificationCenter notificationCenter;

    @Autowired
    private OperationLogWriter operationLogManager;

    @Autowired
    protected PlatformEnvironment platformEnvironment;

    @Autowired
    protected CodeRepositoryCache.EvictCacheExtOpt osInfoManager;

    @Value("${http.exception.notAsHttpError:true}")
    protected boolean httpExceptionNotAsHttpError;

    @Value("${app.support.tenant:true}")
    protected boolean supportTenant;

    private ApplicationContext applicationContext;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        JSONOpt.fastjsonGlobalConfig();
        SystemTempFileUtils.setTempFileDirectory(
            SysParametersUtils.getTempHome() + File.separatorChar);

        CodeRepositoryCache.setPlatformEnvironment(platformEnvironment);
        CodeRepositoryCache.setAllCacheFreshPeriod(CodeRepositoryCache.CACHE_FRESH_PERIOD_SECONDS);
        WebOptUtils.setExceptionNotAsHttpError(httpExceptionNotAsHttpError);
        WebOptUtils.setIsTenant(supportTenant);

        if (operationLogManager != null) {
            OperationLogCenter.registerOperationLogWriter(operationLogManager);
        }

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext=applicationContext;
    }
}
