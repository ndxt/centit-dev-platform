package com.centit.locode.platform.all.config;

import com.centit.fileserver.task.FileOptTaskExecutor;
import com.centit.fileserver.utils.SystemTempFileUtils;
import com.centit.framework.common.SysParametersUtils;
import com.centit.framework.common.WebOptUtils;
import com.centit.framework.components.CodeRepositoryCache;
import com.centit.framework.components.OperationLogCenter;
import com.centit.framework.model.adapter.OperationLogWriter;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.support.algorithm.CollectionsOpt;
import com.centit.support.json.JSONOpt;
import com.centit.support.quartz.JavaBeanJob;
import com.centit.support.quartz.QuartzJobUtils;
import com.centit.workflow.service.impl.FlowTaskImpl;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.io.File;
import java.util.Random;

/**
 * Created by codefan on 17-7-6.
 */
public class InstantiationServiceBeanPostProcessor implements ApplicationListener<ContextRefreshedEvent> , ApplicationContextAware {

    @Autowired
    private OperationLogWriter operationLogManager;

    @Autowired
    protected PlatformEnvironment platformEnvironment;

    @Autowired
    protected SchedulerFactory schedulerFactory;

    @Autowired
    protected FileOptTaskExecutor fileOptTaskExecutor;

    @Autowired
    private FlowTaskImpl flowTaskImpl;

    @Value("${http.exception.notAsHttpError:false}")
    protected boolean httpExceptionNotAsHttpError;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        JSONOpt.fastjsonGlobalConfig();
        SystemTempFileUtils.setTempFileDirectory(
            SysParametersUtils.getTempHome() + File.separatorChar);

        CodeRepositoryCache.setPlatformEnvironment(platformEnvironment);
        CodeRepositoryCache.setAllCacheFreshPeriod(CodeRepositoryCache.CACHE_FRESH_PERIOD_SECONDS);
        WebOptUtils.setExceptionNotAsHttpError(httpExceptionNotAsHttpError);

        if (operationLogManager != null) {
            OperationLogCenter.registerOperationLogWriter(operationLogManager);
        }
        // 创建定时任务
        try {
            Random random = new Random();
            int second = random.nextInt(60);
            int minute = random.nextInt(9) +1;
            String cornExpress = String.valueOf(second) + " " +String.valueOf(minute) + "/10 8-19 * * ? *";

            Scheduler scheduler = schedulerFactory.getScheduler();
            QuartzJobUtils.registerJobType("bean", JavaBeanJob.class);

            QuartzJobUtils.createOrReplaceSimpleJob(scheduler, "fileOptJob",
                "default", "bean", 1800,
                CollectionsOpt.createHashMap("bean", fileOptTaskExecutor,
                    "beanName", "fileOptTaskExecutor",
                    "methodName", "doFileOptJob"));
            QuartzJobUtils.createOrReplaceCronJob(scheduler, "flowTaskJob",
                "default", "bean", cornExpress, //"0 0/5 * * * ? *",
                CollectionsOpt.createHashMap("bean", flowTaskImpl,
                    "beanName", "flowTaskImpl",
                    "methodName", "doFlowTimerJob"));
            scheduler.start();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

    }
}
