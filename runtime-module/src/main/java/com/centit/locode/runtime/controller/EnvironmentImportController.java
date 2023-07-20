package com.centit.locode.runtime.controller;

import com.centit.framework.common.ResponseData;
import com.centit.framework.common.WebOptUtils;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.framework.security.model.CentitUserDetails;
import com.centit.locode.runtime.service.EnvironmentImportManager;
import com.centit.support.common.ObjectException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.SQLException;

/**
 * @author codefan@sina.com
 */
@Controller
@RequestMapping(value = "/exchange")
@Api(value = "应用资源导入导出", tags = "runtime环境导入导出")
public class EnvironmentImportController extends BaseController {

    @Autowired
    private EnvironmentImportManager environmentImportManager;

    @ApiOperation(value = "导入环境数据")
    @ApiImplicitParam(name = "importType", type = "query",
        value = "导入类别:dictionary，file，database，flow, fileAndStore")
    @PutMapping(value = "/import")
    @WrapUpResponseBody
    public void importApplication(String importType, HttpServletRequest request) throws IOException, SQLException {
        CentitUserDetails ud = WebOptUtils.getCurrentUserDetails(request);
        if(ud == null){
            throw new ObjectException(ResponseData.ERROR_USER_NOT_LOGIN, "用户没有登录，没有对应的权限！");
        }
        environmentImportManager.importEnvironment(importType, ud);
    }


    @ApiOperation(value = "测试接口是否正确启动")
    @PutMapping(value = "/test")
    @WrapUpResponseBody
    public String testInterface() {
        return "Hello from locode runtime!";
    }
}
