package com.centit.locode.platform.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.centit.framework.common.JsonResultUtils;
import com.centit.framework.common.WebOptUtils;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.framework.core.dao.PageQueryResult;
import com.centit.locode.platform.po.ApplicationResources;
import com.centit.locode.platform.service.ApplicationResourcesService;
import com.centit.product.metadata.service.SourceInfoManager;
import com.centit.product.oa.team.utils.ResourceBaseController;
import com.centit.support.database.utils.PageDesc;
import io.swagger.annotations.Api;
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
 * @author tian_y
 */
@Api(value = "应用资源关联", tags = "应用资源关联")
@RestController
@RequestMapping(value = "/appResources")
public class ApplicationResourcesController extends ResourceBaseController  {

    @Autowired
    private ApplicationResourcesService applicationResourcesService;

    @Autowired
    private SourceInfoManager databaseInfoMag;

    @ApiOperation(value = "新增关联信息")
    @PostMapping()
    @WrapUpResponseBody
    public void createApplicationDictionary(@RequestBody JSONObject jsonObject, HttpServletResponse response){
        String osId = jsonObject.getString("osId");
        JSONArray dataBaseIds = jsonObject.getJSONArray("dataBaseIds");
        String pushUser = jsonObject.getString("pushUser");
        String dataBaseId = jsonObject.getString("dataBaseId");
        if (dataBaseIds != null && !dataBaseIds.isEmpty()) {
            for(int i = 0; i < dataBaseIds.size(); i++){
                ApplicationResources app = new ApplicationResources();
                app.setOsId(osId);
                app.setDataBaseId(dataBaseIds.get(i) + "");
                app.setPushUser(pushUser);
                if(dataBaseId != null && !"".equals(dataBaseId) && dataBaseId.equals(dataBaseIds.get(i) + "")){
                    //默认关系数据库--是
                    app.setIsUsed("1");
                }else{
                    //默认关系数据库--否
                    app.setIsUsed("0");
                }
                applicationResourcesService.createApplicationResources(app);
            }
            JsonResultUtils.writeSingleDataJson(dataBaseIds,response);
        }
    }

    @ApiOperation(value = "修改关联信息")
    @PutMapping()
    @WrapUpResponseBody
    public void update(@RequestBody ApplicationResources applicationResources){
        String osId = applicationResources.getOsId();
        String isUsed = applicationResources.getIsUsed();
        Map<String, Object> propertiesMap =  new HashMap<>();
        if(StringUtils.isNotBlank(osId)){
            propertiesMap.put("osId", osId);
        }
        if(StringUtils.isNotBlank(isUsed)){
            propertiesMap.put("isUsed", isUsed);
        }
        //如果修改默认关系数据库，则修改当前应用下默认关系库为0
        if(isUsed != null && "1".equals(isUsed)){
            List<ApplicationResources> list = applicationResourcesService.listObjectsByProperty(propertiesMap);
            if(list != null && list.size() > 0){
                for(ApplicationResources app : list){
                    app.setIsUsed("0");
                    applicationResourcesService.updateApplicationResources(app);
                }
            }
        }
        applicationResourcesService.updateApplicationResources(applicationResources);
    }

    @ApiOperation(value = "删除关联信息")
    @DeleteMapping(value = "/{id}")
    @WrapUpResponseBody
    public void deleteById(@PathVariable String id){
        applicationResourcesService.deleteApplicationResources(id);
    }

    @ApiOperation(value = "查询关联信息列表")
    @GetMapping("/list")
    @WrapUpResponseBody
    public PageQueryResult list(HttpServletRequest request, PageDesc pageDesc){
        Map<String, Object> propertiesMap = BaseController.collectRequestParameters(request);
        List<ApplicationResources> list = applicationResourcesService.listObjectsByProperty(propertiesMap);
        String topUnit = WebOptUtils.getCurrentTopUnit(request);
        if(StringUtils.isNotBlank(topUnit)){
            propertiesMap.put("topUnit", topUnit);
        }

        propertiesMap.remove("osId");

        if (list!=null && list.size()>0){
            Map<String, String> ids = new HashMap<>();
            list.stream().forEach(applicationResources -> ids.put(applicationResources.getId(),applicationResources.getDataBaseId()));
            List<String> dataBaseCode = list.stream().map(applicationResources -> applicationResources.getDataBaseId()).collect(Collectors.toList());
            propertiesMap.put("databaseCodes",dataBaseCode);
            JSONArray sourceInfos = databaseInfoMag.listDatabaseAsJson(propertiesMap, pageDesc);
            JSONArray jsonArray = new JSONArray();
            ids.forEach((key,value)->{
                sourceInfos.stream().forEach(sourceInfo->{
                    JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(sourceInfo), JSONObject.class);
                    if (jsonObject.getString("databaseCode").equals(ids.get(key))){
                        ApplicationResources po = applicationResourcesService.getApplicationResources(key);
                        jsonObject.put("id",key);
                        jsonObject.put("dataBaseId", jsonObject.getString("databaseCode"));
                        jsonObject.put("pushUser", po.getPushUser());
                        jsonObject.put("pushTime", po.getPushTime());
                        jsonArray.add(jsonObject);
                    }
                });
            });
            return PageQueryResult.createJSONArrayResult(jsonArray, pageDesc, new Class[]{ApplicationResources.class});

        }
        return PageQueryResult.createResult(new JSONArray(), pageDesc);
    }

    @ApiOperation(value = "查询单个关联信息")
    @GetMapping(value = "/{id}")
    @WrapUpResponseBody
    public ApplicationResources getHistory(@PathVariable String id) {
        return applicationResourcesService.getApplicationResources(id);
    }

    @ApiOperation(value = "删除资源管理以及关联信息")
    @DeleteMapping(value = "/sourceInfo/{id}")
    @WrapUpResponseBody
    public void deleteSourceInfo(@PathVariable String id){
        applicationResourcesService.deleteSourceInfo(id);
    }

}
