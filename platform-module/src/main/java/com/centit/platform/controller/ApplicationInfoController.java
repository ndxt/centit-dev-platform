package com.centit.platform.controller;

import com.alibaba.fastjson.JSONObject;
import com.centit.framework.common.WebOptUtils;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.model.basedata.IOsInfo;
import com.centit.platform.po.ApplicationInfo;
import com.centit.platform.service.ApplicationInfoManager;
import com.centit.framework.common.JsonResultUtils;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.framework.core.dao.PageQueryResult;
import com.centit.support.algorithm.BooleanBaseOpt;
import com.centit.support.algorithm.StringBaseOpt;
import com.centit.support.database.utils.PageDesc;
import com.centit.support.network.HtmlFormUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "applicationInfo")
@Api(value = "应用管理", tags = "应用管理")
public class ApplicationInfoController extends BaseController {
    @Autowired
    private ApplicationInfoManager applicationInfoManager;
    @Autowired
    private PlatformEnvironment platformEnvironment;

    @ApiOperation(value = "新增应用")
    @PostMapping
    @WrapUpResponseBody
    public IOsInfo createApplicationInfo(@RequestBody JSONObject osInfo) {
        return platformEnvironment.addOsInfo(osInfo);
    }

    @ApiOperation(value = "修改应用")
    @ApiImplicitParam(name = "applicationId", value = "应用ID")
    @PutMapping
    @WrapUpResponseBody
    public IOsInfo updateApplicationInfo(@RequestBody JSONObject osInfo) {
        return platformEnvironment.updateOsInfo(osInfo);
    }

    @ApiOperation(value = "删除应用模块")
    @ApiImplicitParam(name = "applicationId", value = "图表ID")
    @DeleteMapping(value = "/{applicationId}")
    @WrapUpResponseBody
    public IOsInfo deleteApplicationInfo(@PathVariable String applicationId) {
        return platformEnvironment.deleteOsInfo(applicationId);
    }

    @ApiOperation(value = "查询应用模块")
    @GetMapping
    @WrapUpResponseBody
    public List<? extends IOsInfo> listApplicationInfo(String topUnit) {
        List<? extends IOsInfo> osInfos= platformEnvironment.listOsInfos(topUnit);
        osInfos.removeIf(osInfo-> BooleanBaseOpt.castObjectToBoolean(osInfo.getIsDelete(),true));
        osInfos.sort(Comparator.comparing(IOsInfo::getLastModifyDate,Comparator.nullsFirst(Date::compareTo)).reversed());
        return osInfos;
    }

    @ApiOperation(value = "查询单个应用模块")
    @GetMapping(value = "/{applicationId}")
    @WrapUpResponseBody
    public IOsInfo getApplicationInfo(@PathVariable String applicationId) {
        return platformEnvironment.getOsInfo(applicationId);
    }
}
