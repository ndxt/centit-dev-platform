package com.centit.platform.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
    @PostMapping(value = "/businessDelete")
    @WrapUpResponseBody
    public JSONObject businessDelete(String optId,  HttpServletRequest request) {
        String topUnit = WebOptUtils.getCurrentTopUnit(request);
        JSONObject jsonObject = new JSONObject();
        PageDesc pageDesc = new PageDesc();
        pageDesc.setPageNo(1);
        pageDesc.setPageSize(10);
        Map<String, Object> metaFormParam = new HashMap<>();
        metaFormParam.put("optId",optId);
        //页面数据
        JSONArray metaFormModelList = metaFormModelManager.listFormModeAsJson(null, metaFormParam, pageDesc);
        System.out.println(JSON.toJSONString(metaFormModelList));
        System.out.println("metaFormModelList返回结果："+metaFormModelList.size());
        JSONArray metaFormModelDraftList = metaFormModelDraftManager.listFormModeAsJson(null, metaFormParam, pageDesc);
        System.out.println(JSON.toJSONString(metaFormModelList));
        System.out.println("metaFormModelDraftList返回结果："+metaFormModelDraftList.size());
        if (!metaFormModelList.isEmpty() || !metaFormModelDraftList.isEmpty()){
            jsonObject.put("msg","页面存在数据，无法删除，请先移除！");
            return jsonObject;
        }
        //接口数据
        List<? extends IOptMethod> iOptMethods = CodeRepositoryUtil.getOptMethodByOptID(topUnit,optId);
        System.out.println(JSON.toJSONString(iOptMethods));
        System.out.println("iOptMethods返回结果："+iOptMethods.size());
        if (iOptMethods.size()>1){
            jsonObject.put("msg","接口存在数据，无法删除，请先移除！");
            return jsonObject;
        }
        //流程数据
        List<FlowInfo> flowInfos = flowDefine.listFlowsByOptId(optId);
        System.out.println(JSON.toJSONString(flowInfos));
        System.out.println("flowInfos返回结果："+flowInfos.size());
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
