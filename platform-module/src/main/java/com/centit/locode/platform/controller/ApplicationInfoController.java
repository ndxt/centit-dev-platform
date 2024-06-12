package com.centit.locode.platform.controller;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.centit.framework.common.ResponseData;
import com.centit.framework.common.WebOptUtils;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.model.basedata.OptInfo;
import com.centit.framework.model.basedata.OptMethod;
import com.centit.framework.model.basedata.OsInfo;
import com.centit.locode.platform.service.ApplicationInfoManager;
import com.centit.metaform.service.MetaFormModelDraftManager;
import com.centit.metaform.service.MetaFormModelManager;
import com.centit.product.metadata.api.MetadataManageService;
import com.centit.support.common.ObjectException;
import com.centit.workflow.service.FlowDefine;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
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
    MetadataManageService metadataManageService;

    @Autowired
    private PlatformEnvironment platformEnvironment;

    @Autowired
    private FlowDefine flowDefine;

    @ApiOperation(value = "新增应用")
    @PostMapping
    @WrapUpResponseBody
    public JSONObject createApplicationInfo(@RequestBody OsInfo osInfo, HttpServletRequest httpServletRequest) {
        if(StringUtils.isBlank(osInfo.getTopUnit())){
            osInfo.setTopUnit(WebOptUtils.getCurrentTopUnit(httpServletRequest));
        }
        if(StringUtils.isBlank(osInfo.getCreated())){
            osInfo.setCreated(WebOptUtils.getCurrentUserCode(httpServletRequest));
        }
        return applicationInfoManager.createApplicationInfo(osInfo);
    }

    @ApiOperation(value = "修改应用")
    @PutMapping
    @WrapUpResponseBody
    @Transactional(rollbackFor = Exception.class)
    public OsInfo updateApplicationInfo(@RequestBody OsInfo osInfo) {
        OptInfo optInfo = new OptInfo();
        optInfo.setOptId(osInfo.getOsId());
        optInfo.setOptName(osInfo.getOsName());
        platformEnvironment.updateOptInfo(optInfo);
        return applicationInfoManager.updateApplicationInfo(osInfo);
    }

    @ApiOperation(value = "删除应用模块")
    @ApiImplicitParam(name = "applicationId", value = "图表ID")
    @DeleteMapping(value = "/{applicationId}")
    @WrapUpResponseBody
    public OsInfo deleteApplicationInfo(@PathVariable String applicationId) {
        //TODO 添加是否是研发人员验证

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
        return applicationInfoManager.listApplicationInfo(topUnit, parameters);
    }

    @ApiOperation(value = "查询单个应用模块")
    @GetMapping(value = "/{applicationId}")
    @WrapUpResponseBody
    public JSONObject getApplicationInfo(@PathVariable String applicationId, HttpServletRequest request) {
        if (!WebOptUtils.isTenantTopUnit(request)){
            return null;
        }
        String topUnit = WebOptUtils.getCurrentTopUnit(request);
        String loginUser = WebOptUtils.getCurrentUserCode(request);
        if(StringUtils.isBlank(loginUser)){
                loginUser = request.getParameter("userCode");
        }
        return applicationInfoManager.getApplicationInfo(applicationId,topUnit, loginUser,true);
    }

    @ApiOperation(value = "查询单个应用模块")
    @GetMapping(value = "no-auth/{applicationId}")
    @WrapUpResponseBody
    public JSONObject getApplicationInfoNoAuth(@PathVariable String applicationId, HttpServletRequest request) {
        String topUnit = WebOptUtils.getCurrentTopUnit(request);
        String loginUser = WebOptUtils.getCurrentUserCode(request);
        return applicationInfoManager.getApplicationInfo(applicationId,topUnit, loginUser,false);
    }

    @ApiOperation(value = "业务模块删除按钮")
    @DeleteMapping(value = "/businessDelete/{optId}")
    @WrapUpResponseBody
    public ResponseData businessDelete(@PathVariable String optId,  HttpServletRequest request) {
        String loginUser = WebOptUtils.getCurrentUserCode(request);
        if (StringUtils.isBlank(loginUser)) {
            loginUser = WebOptUtils.getRequestFirstOneParameter(request, "userCode");
        }
        if (StringUtils.isBlank(loginUser)){
            throw new ObjectException(ResponseData.HTTP_MOVE_TEMPORARILY, "您未登录，请先登录！");
        }
        String topUnit = WebOptUtils.getCurrentTopUnit(request);
        Map<String, Object> metaFormParam = new HashMap<>();
        metaFormParam.put("optId",optId);
        metaFormParam.put("isValid","F");
        //页面数据
        JSONArray metaFormModelList = metaFormModelManager.listFormModeAsJson(null, metaFormParam, null);
        JSONArray metaFormModelDraftList = metaFormModelDraftManager.listFormModeAsJson(null, metaFormParam, null);
        if (!metaFormModelList.isEmpty() || !metaFormModelDraftList.isEmpty()){
            return ResponseData.makeErrorMessage("页面存在数据，无法删除，请先移除！");
        }
        //接口数据
        List<OptMethod> iOptMethods = platformEnvironment.listAllOptMethod(topUnit);
        iOptMethods.removeIf(iOptMethod -> !iOptMethod.getOptId().equals(optId));
        if (iOptMethods.size()>1){
            return ResponseData.makeErrorMessage("接口存在数据，无法删除，请先移除！");
        }
        //流程数据
        JSONArray flowInfos = flowDefine.listFlowsByOptId(optId);
        if (!flowInfos.isEmpty()){
            return ResponseData.makeErrorMessage("流程存在数据，无法删除，请先移除！");
        }
        //移除关联数据
        metadataManageService.deleteMetaOptRelationByOptId(optId);
        //删除业务模块
        boolean result = platformEnvironment.deleteOptInfoByOptId(optId);
        return ResponseData.makeSuccessResponse(result+"");
    }

    @ApiOperation(value = "查询资源的详情和使用情况")
    @GetMapping(value = "/resourceInfo")
    @WrapUpResponseBody
    public JSONObject resourceInfo(HttpServletRequest request, HttpServletResponse response) {
        if (StringUtils.isBlank(WebOptUtils.getCurrentUserCode(request))){
            throw new ObjectException(ResponseData.ERROR_USER_NOT_LOGIN,"您未登录");
        }
        Map<String, Object> parameters = collectRequestParameters(request);
        if (StringUtils.isBlank(MapUtils.getString(parameters,"topUnit"))){
            throw new ObjectException("topUnit不能为空!");
        }
        return applicationInfoManager.getResourceInfo(parameters);
    }
}
