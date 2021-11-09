package com.centit.platform.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.framework.common.WebOptUtils;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.model.basedata.IOptMethod;
import com.centit.framework.model.basedata.IOsInfo;
import com.centit.framework.system.po.OsInfo;
import com.centit.metaform.service.MetaFormModelDraftManager;
import com.centit.metaform.service.MetaFormModelManager;
import com.centit.platform.service.ApplicationInfoManager;
import com.centit.support.database.utils.PageDesc;
import com.centit.workflow.po.FlowInfo;
import com.centit.workflow.service.FlowDefine;
import com.centit.workflow.service.FlowOptService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zhf
 */
@RestController
@RequestMapping(value = "applicationInfo")
@Api(value = "应用管理", tags = "应用管理")
public class ApplicationInfoController extends BaseController {
    @Autowired
    private ApplicationInfoManager applicationInfoManager;

    @Autowired
    private MetaFormModelManager metaFormModelManager;

    @Autowired
    private MetaFormModelDraftManager metaFormModelDraftManager;

    @Autowired
    private PlatformEnvironment platformEnvironment;

    @Autowired(required = false)
    FlowDefine  flowDefine;

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

    @ApiOperation(value = "业务模块删除按钮")
    @PostMapping(value = "/businessDelete")
    @WrapUpResponseBody
    public JSONObject businessDelete(String optId,  String topUnit) {
        JSONObject jsonObject = new JSONObject();
        PageDesc pageDesc = new PageDesc();
        pageDesc.setPageNo(1);
        pageDesc.setPageSize(10);
        Map<String, Object> metaFormParam = new HashMap<>();
        metaFormParam.put("optId",optId);
        //页面数据
        JSONArray metaFormModelList = metaFormModelManager.listFormModeAsJson(null, metaFormParam, pageDesc);
        JSONArray metaFormModelDraftList = metaFormModelDraftManager.listFormModeAsJson(null, metaFormParam, pageDesc);
        if (!metaFormModelList.isEmpty() || !metaFormModelDraftList.isEmpty()){
            jsonObject.put("msg","页面存在数据，无法删除，请先移除！");
            return jsonObject;
        }
        //接口数据
        List<? extends IOptMethod> iOptMethods = platformEnvironment.listAllOptMethod(topUnit);
        List<String> optIds = iOptMethods.stream().map(iOptMethod -> iOptMethod.getOptId()).collect(Collectors.toList());
        if (optIds.contains(optId)){
            jsonObject.put("msg","接口存在数据，无法删除，请先移除！");
            return jsonObject;
        }
        //流程数据
        List<FlowInfo> flowInfos = flowDefine.listFlowsByOptId(optId);
        if (!flowInfos.isEmpty()){
            jsonObject.put("msg","流程存在数据，无法删除，请先移除！");
            return jsonObject;
        }
        //删除业务模块
        boolean result = platformEnvironment.deleteOptInfoByOptId(optId);
        jsonObject.put("msg",result);
        return jsonObject;
    }
}
