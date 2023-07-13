package com.centit.platform.controller;

import com.centit.framework.common.WebOptUtils;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.framework.security.model.CentitUserDetails;
import com.centit.platform.service.ApplicationExchangeManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author codefan@sina.com
 */
@Controller
@RequestMapping(value = "/exchange")
@Api(value = "应用资源导入导出", tags = "runtime环境导入导出")
public class ApplicationExchangeController extends BaseController {

    @Autowired
    private ApplicationExchangeManager applicationExchangeManager;

    @ApiOperation(value = "导出应用路径")
    @GetMapping(value = "/export/{osId}")
    @WrapUpResponseBody
    public void exportApplication(@PathVariable String osId, HttpServletRequest request) throws IOException {
        CentitUserDetails ud = WebOptUtils.getCurrentUserDetails(request);
        applicationExchangeManager.exportApplication(osId, ud);

    }

}
