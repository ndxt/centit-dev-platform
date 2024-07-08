package com.centit.locode.platform.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpResponseBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@RestController
@RequestMapping(value = "applicationInfo")
@Api(value = "应用管理", tags = "应用管理")
public class ApplicationInfoController extends BaseController {

    @Value("${app.home:./}")
    private String appHome;

    private JSONObject loadApplicationInfo(){
        String appInfoFile = appHome + File.separator + "config" +
            File.separator +  "application.json";
        try {
            return JSON.parseObject(new FileInputStream(appInfoFile));
        } catch (IOException e) {
            return null;
        }
    }

    @ApiOperation(value = "查询单个应用模块")
    @GetMapping(value = "/{applicationId}")
    @WrapUpResponseBody
    public JSONObject getApplicationInfo(@PathVariable String applicationId) {
        return loadApplicationInfo();
    }

    @ApiOperation(value = "查询单个应用模块")
    @GetMapping(value = "no-auth/{applicationId}")
    @WrapUpResponseBody
    public JSONObject getApplicationInfoNoAuth(@PathVariable String applicationId) {
        return  loadApplicationInfo();
    }
}
