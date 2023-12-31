package com.centit.locode.platform.config;

import com.centit.framework.components.CodeRepositoryCache;
import com.centit.framework.components.OperationLogCenter;
import com.centit.framework.model.adapter.MessageSender;
import com.centit.framework.model.adapter.NotificationCenter;
import com.centit.framework.model.adapter.OperationLogWriter;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.support.json.JSONOpt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;

/**
 *
 * @author codefan
 * @date 17-7-6
 */
@Component
public class InstantiationServiceBeanPostProcessor implements ApplicationListener<ContextRefreshedEvent>
{

    private final NotificationCenter notificationCenter;

    private final OperationLogWriter optLogManager;

    private final MessageSender innerMessageManager;

    @Autowired
    protected PlatformEnvironment platformEnvironment;

    @Autowired
    public InstantiationServiceBeanPostProcessor(NotificationCenter notificationCenter, OperationLogWriter optLogManager, MessageSender innerMessageManager) {
        this.notificationCenter = notificationCenter;
        this.optLogManager = optLogManager;
        this.innerMessageManager = innerMessageManager;
    }

    @Override
    public void onApplicationEvent(@Nullable ContextRefreshedEvent event)
    {
        JSONOpt.fastjsonGlobalConfig();
        CodeRepositoryCache.setPlatformEnvironment(platformEnvironment);
        if(innerMessageManager!=null) {
            notificationCenter.registerMessageSender("innerMsg", innerMessageManager);
        }
        if(optLogManager!=null) {
            OperationLogCenter.registerOperationLogWriter(optLogManager);
        }
    }

}
