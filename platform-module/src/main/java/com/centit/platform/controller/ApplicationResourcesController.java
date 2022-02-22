package com.centit.platform.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.framework.common.JsonResultUtils;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.framework.core.dao.PageQueryResult;
import com.centit.platform.po.ApplicationResources;
import com.centit.platform.service.ApplicationResourcesService;
import com.centit.product.adapter.po.SourceInfo;
import com.centit.product.metadata.service.SourceInfoManager;
import com.centit.support.database.utils.PageDesc;
import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author tian_y
 */
@Api(value = "应用资源关联", tags = "应用资源关联")
@RestController
@RequestMapping(value = "/appResources")
public class ApplicationResourcesController extends BaseController  {

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
        List<ApplicationResources> list = applicationResourcesService.listApplicationResources(BaseController.collectRequestParameters(request), pageDesc);
        List<String> dataBaseCode = list.stream().map(applicationResources -> applicationResources.getDataBaseId()).collect(Collectors.toList());
        List<SourceInfo> sourceInfos = databaseInfoMag.listDatabase();
        sourceInfos.removeIf(sourceInfo ->!dataBaseCode.contains(sourceInfo.getDatabaseCode()));
        if (StringUtils.isNotBlank(request.getParameter("sourceType"))){
            sourceInfos.removeIf(sourceInfo -> !sourceInfo.getSourceType().equals(request.getParameter("sourceType")));
        }
        return PageQueryResult.createJSONArrayResult(JSONArray.parseArray(JSON.toJSONString(sourceInfos)), pageDesc, new Class[]{SourceInfo.class});
        /*List<Map<String, Object>> resultList = new ArrayList();
        SourceInfo sourceInfo = new SourceInfo();
        if (list != null && list.size() > 0) {
            for(ApplicationResources applicationResources : list){
                String dataBaseId = applicationResources.getDataBaseId();
                sourceInfo = databaseInfoMag.getObjectById(dataBaseId);
                Map<String, Object> resultMap = new HashMap<>();
                if(sourceInfo != null){
                    resultMap.put("id",applicationResources.getId());
                    resultMap.put("osId",applicationResources.getOsId());
                    resultMap.put("dataBaseId", dataBaseId);
                    resultMap.put("isUsed",applicationResources.getIsUsed());
                    resultMap.put("pushTime",applicationResources.getPushTime());
                    resultMap.put("pushUser",applicationResources.getPushUser());
                    resultMap.put("databaseName", sourceInfo.getDatabaseName());
                    resultMap.put("databaseUrl", sourceInfo.getDatabaseUrl());
                    resultMap.put("createTime", sourceInfo.getCreateTime());
                    resultMap.put("sourceType", sourceInfo.getSourceType());
                    resultList.add(resultMap);
                }else{
                    //未查询到资源信息，说明资源已经被删除，删除关联信息
                    applicationResourcesService.deleteApplicationResources(applicationResources.getId());
                }
            }
        }
        return PageQueryResult.createJSONArrayResult(JSONArray.parseArray(JSON.toJSONString(resultList)), pageDesc, new Class[]{ApplicationResources.class});*/
    }

    @ApiOperation(value = "查询单个关联信息")
    @GetMapping(value = "/{id}")
    @WrapUpResponseBody
    public ApplicationResources getHistory(@PathVariable String id) {
        return applicationResourcesService.getApplicationResources(id);
    }
}
