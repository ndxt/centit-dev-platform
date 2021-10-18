package com.centit.platform.controller;

import com.alibaba.fastjson.JSONObject;
import com.centit.framework.common.WebOptUtils;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.framework.model.basedata.IOsInfo;
import com.centit.framework.system.po.OsInfo;
import com.centit.platform.service.ApplicationInfoManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

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
    public JSONObject createApplicationInfo(@RequestBody OsInfo osInfo) {
        return applicationInfoManager.createApplicationInfo(osInfo);
    }

    @ApiOperation(value = "修改应用")
    @PutMapping
    @WrapUpResponseBody
    public IOsInfo updateApplicationInfo(@RequestBody OsInfo osInfo) {
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
    @GetMapping(value = "/list")
    @WrapUpResponseBody
    public List<? extends IOsInfo> listApplicationInfo(HttpServletRequest request, HttpServletResponse response) {
        String topUnit = WebOptUtils.getCurrentTopUnit(request);
        Map<String, Object> parameters = BaseController.collectRequestParameters(request);
        if (StringUtils.isBlank(topUnit)){
            return null;
        }
        return applicationInfoManager.listApplicationInfo(topUnit,parameters);
    }

    @ApiOperation(value = "查询单个应用模块")
    @GetMapping(value = "/{applicationId}")
    @WrapUpResponseBody
    public JSONObject getApplicationInfo(@PathVariable String applicationId) {
        return applicationInfoManager.getApplicationInfo(applicationId);
    }
}
