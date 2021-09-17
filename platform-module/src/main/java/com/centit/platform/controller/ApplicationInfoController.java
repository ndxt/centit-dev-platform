package com.centit.platform.controller;

import com.alibaba.fastjson.JSONObject;
import com.centit.framework.common.ResponseData;
import com.centit.framework.common.WebOptUtils;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.framework.model.basedata.IOsInfo;
import com.centit.platform.service.ApplicationInfoManager;
import com.centit.support.algorithm.StringBaseOpt;
import com.centit.support.common.ObjectException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author zhf
 */
@RestController
@RequestMapping(value = "applicationInfo")
@Api(value = "应用管理", tags = "应用管理")
public class ApplicationInfoController extends BaseController {
    @Autowired
    private ApplicationInfoManager applicationInfoManager;

    @ApiOperation(value = "新增应用")
    @PostMapping
    @WrapUpResponseBody
    public JSONObject createApplicationInfo(@RequestBody JSONObject osInfo) {
        return applicationInfoManager.createApplicationInfo(osInfo);
    }

    @ApiOperation(value = "修改应用")
    @PutMapping
    @WrapUpResponseBody
    public IOsInfo updateApplicationInfo(@RequestBody JSONObject osInfo) {
        return applicationInfoManager.updateApplicationInfo(osInfo);
    }

    @ApiOperation(value = "删除应用模块")
    @ApiImplicitParam(name = "applicationId", value = "图表ID")
    @DeleteMapping(value = "/{applicationId}")
    @WrapUpResponseBody
    public IOsInfo deleteApplicationInfo(@PathVariable String applicationId) {
        return applicationInfoManager.deleteApplicationInfo(applicationId);
    }

    @ApiOperation(value = "查询应用模块")
    @GetMapping(value = "/list/{topUnit}")
    @WrapUpResponseBody
    public List<? extends IOsInfo> listApplicationInfo(@PathVariable String topUnit) {
        return applicationInfoManager.listApplicationInfo(topUnit);
    }

    @ApiOperation(value = "查询单个应用模块")
    @GetMapping(value = "/{applicationId}")
    @WrapUpResponseBody
    public JSONObject getApplicationInfo(@PathVariable String applicationId) {
        return applicationInfoManager.getApplicationInfo(applicationId);
    }
}
