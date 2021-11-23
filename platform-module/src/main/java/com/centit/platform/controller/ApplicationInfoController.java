package com.centit.platform.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.framework.common.ResponseData;
import com.centit.framework.common.WebOptUtils;
import com.centit.framework.components.CodeRepositoryUtil;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.model.basedata.IOptMethod;
import com.centit.framework.model.basedata.IOsInfo;
import com.centit.framework.system.po.OsInfo;
import com.centit.metaform.dubbo.adapter.MetaFormModelDraftManager;
import com.centit.metaform.dubbo.adapter.MetaFormModelManager;
import com.centit.platform.service.ApplicationInfoManager;
import com.centit.support.database.utils.PageDesc;
import com.centit.workflow.po.FlowInfo;
import com.centit.workflow.service.FlowDefine;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
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

    @Autowired
    private MetaFormModelManager metaFormModelManager;

    @Autowired
    private MetaFormModelDraftManager metaFormModelDraftManager;

    @Autowired
    private PlatformEnvironment platformEnvironment;

    @Autowired
    private FlowDefine flowDefine;

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
    public JSONArray listApplicationInfo(HttpServletRequest request, HttpServletResponse response) {
        if (!WebOptUtils.isTenantTopUnit(request)){
            return null;
        }
        String topUnit = WebOptUtils.getCurrentTopUnit(request);
        Map<String, Object> parameters = BaseController.collectRequestParameters(request);
        return applicationInfoManager.listApplicationInfo(topUnit,parameters);
    }

    @ApiOperation(value = "查询单个应用模块")
    @GetMapping(value = "/{applicationId}")
    @WrapUpResponseBody
    public JSONObject getApplicationInfo(@PathVariable String applicationId) {
        return applicationInfoManager.getApplicationInfo(applicationId);
    }

    @ApiOperation(value = "业务模块删除按钮")
    @DeleteMapping(value = "/businessDelete/{optId}")
    @WrapUpResponseBody
    public ResponseData businessDelete(@PathVariable String optId,  HttpServletRequest request) {
        String topUnit = WebOptUtils.getCurrentTopUnit(request);
        Map<String, Object> metaFormParam = new HashMap<>();
        metaFormParam.put("optId",optId);
        //页面数据
        JSONArray metaFormModelList = metaFormModelManager.listFormModeAsJson(null, metaFormParam, null);
        JSONArray metaFormModelDraftList = metaFormModelDraftManager.listFormModeAsJson(null, metaFormParam, null);
        if (!metaFormModelList.isEmpty() || !metaFormModelDraftList.isEmpty()){
            return ResponseData.makeErrorMessage("页面存在数据，无法删除，请先移除！");
        }
        //接口数据
        List<? extends IOptMethod> iOptMethods = CodeRepositoryUtil.getOptMethodByOptID(topUnit,optId);
        if (iOptMethods.size()>1){
            return ResponseData.makeErrorMessage("接口存在数据，无法删除，请先移除！");
        }
        //流程数据
        List<FlowInfo> flowInfos = flowDefine.listFlowsByOptId(optId);
        if (!flowInfos.isEmpty()){
            return ResponseData.makeErrorMessage("流程存在数据，无法删除，请先移除！");
        }
        //删除业务模块
        boolean result = platformEnvironment.deleteOptInfoByOptId(optId);
        return ResponseData.makeSuccessResponse(result+"");
    }
}
