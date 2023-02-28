package com.centit.platform.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.centit.framework.common.JsonResultUtils;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.framework.core.dao.PageQueryResult;
import com.centit.platform.po.ApplicationRule;
import com.centit.platform.service.ApplicationRuleService;
import com.centit.product.adapter.po.DataCheckRule;
import com.centit.product.metadata.service.DataCheckRuleService;
import com.centit.support.algorithm.CollectionsOpt;
import com.centit.support.database.utils.PageDesc;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author tian_y
 */
@Api(value = "应用校验规则关联", tags = "应用校验规则关联")
@RestController
@RequestMapping(value = "/appRule")
public class ApplicationRuleController extends BaseController {

    @Autowired
    private ApplicationRuleService applicationRuleService;

    @Autowired
    private DataCheckRuleService dataCheckRuleService;

    @ApiOperation(value = "新增关联信息")
    @PostMapping()
    @WrapUpResponseBody
    public void createApplicationRule(@RequestBody JSONObject jsonObject, HttpServletResponse response) {
        String osId = jsonObject.getString("osId");
        JSONArray ruleIds = jsonObject.getJSONArray("ruleIds");
        String pushUser = jsonObject.getString("pushUser");
        if (ruleIds != null && !ruleIds.isEmpty()) {
            for (int i = 0; i < ruleIds.size(); i++) {
                ApplicationRule app = new ApplicationRule();
                app.setOsId(osId);
                app.setRuleId(ruleIds.get(i) + "");
                app.setPushUser(pushUser);
                applicationRuleService.createApplicationRule(app);
            }
        }
        JsonResultUtils.writeSingleDataJson(ruleIds, response);
    }

    @ApiOperation(value = "修改关联信息")
    @PutMapping()
    @WrapUpResponseBody
    public void update(@RequestBody ApplicationRule applicationRule) {
        applicationRuleService.updateApplicationRule(applicationRule);
    }

    @ApiOperation(value = "删除关联信息")
    @DeleteMapping(value = "/{id}")
    @WrapUpResponseBody
    public void deleteById(@PathVariable String id) {
        applicationRuleService.deleteApplicationRule(id);
    }

    @ApiOperation(value = "查询关联信息列表")
    @GetMapping("/list")
    @WrapUpResponseBody
    public PageQueryResult list(HttpServletRequest request, PageDesc pageDesc) {
        Map<String, Object> map = BaseController.collectRequestParameters(request);
        List<ApplicationRule> list = applicationRuleService.listApplicationRule(map, pageDesc);
        List<Map<String, Object>> resultList = new ArrayList();
        if (list != null && list.size() > 0) {
            for(ApplicationRule applicationRule : list) {
                String ruleId = applicationRule.getRuleId();
                DataCheckRule dataCheckRule = dataCheckRuleService.getObjectById(ruleId);
                if(dataCheckRule != null){
                    Map<String, Object> resultMap = CollectionsOpt.objectToMap(dataCheckRule);
                    resultMap.put("id", applicationRule.getId());
                    resultList.add(resultMap);
                }
            }
        }
        return PageQueryResult.createJSONArrayResult(JSONArray.parseArray(JSON.toJSONString(resultList)), pageDesc, new Class[]{DataCheckRule.class});
    }

    @ApiOperation(value = "查询单个关联信息")
    @GetMapping(value = "/{id}")
    @WrapUpResponseBody
    public ApplicationRule getHistory(@PathVariable String id) {
        return applicationRuleService.getApplicationRule(id);
    }

    @ApiOperation(value = "删除校验规则以及关联信息")
    @DeleteMapping(value = "/checkRule/{id}")
    @WrapUpResponseBody
    public void deleteCheckRule(@PathVariable String id) {
        applicationRuleService.deleteCheckRule(id);
    }
}
