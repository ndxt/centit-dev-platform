package com.centit.platform.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.fileserver.common.FileLibraryInfo;
import com.centit.fileserver.common.OperateFileLibrary;
import com.centit.framework.common.ResponseData;
import com.centit.framework.common.WebOptUtils;
import com.centit.framework.components.CodeRepositoryUtil;
import com.centit.framework.filter.RequestThreadLocal;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.model.basedata.IOptInfo;
import com.centit.framework.model.basedata.IOsInfo;
import com.centit.framework.system.po.OptInfo;
import com.centit.framework.system.po.OsInfo;
import com.centit.platform.service.ApplicationInfoManager;
import com.centit.product.adapter.api.MetadataManageService;
import com.centit.product.adapter.api.WorkGroupManager;
import com.centit.product.adapter.po.WorkGroup;
import com.centit.product.adapter.po.WorkGroupParameter;
import com.centit.support.algorithm.BooleanBaseOpt;
import com.centit.support.algorithm.CollectionsOpt;
import com.centit.support.algorithm.StringBaseOpt;
import com.centit.support.common.ObjectException;
import com.centit.tenant.dubbo.adapter.TenantManageService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author zhf
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ApplicationInfoManagerImpl implements ApplicationInfoManager {
    @Autowired
    private PlatformEnvironment platformEnvironment;
    @Autowired
    private OperateFileLibrary operateFileLibrary;
    @Autowired
    private WorkGroupManager workGroupManager;

    @Autowired
    private MetadataManageService metadataManageService;

    @Autowired
    private TenantManageService tenantManageService;

    private final static String FILE_TYPE_ITEM = "I";
    private final static String OPTINFO_INTOOLBAR_NO = "N";
    private final static String OPTINFO_OPTTYPE_COMMON = "O";
    private final static String OPTINFO_FORMCODE_ITEM = "I";
    public final static String OPTINFO_FORMCODE_PAGEENTER = "A";
    private final static String WORKGROUP_ROLECODE_LEADER = "组长";
    private IOsInfo iOsInfo;
    private FileLibraryInfo fileLibrary;
    private List<IOptInfo> optInfos = new ArrayList<>();
    private List<WorkGroup> workGroup = new ArrayList<>();

    @Override
    public JSONObject createApplicationInfo(OsInfo osInfo) {
        //验证应用数量是否达到限制
        checkOsNumberLimitIsOver(WebOptUtils.getCurrentTopUnit(RequestThreadLocal.getLocalThreadWrapperRequest()));
        createOsInfoAndOther(osInfo);
        return assemblyApplicationInfo();
    }

    @Override
    public JSONArray listApplicationInfo(String topUnit, Map<String, Object> parameters) {
        List<? extends IOsInfo> osInfos = platformEnvironment.listOsInfos(topUnit);
        osInfos.removeIf(osInfo -> BooleanBaseOpt.castObjectToBoolean(osInfo.isDeleted(), true));
        if (parameters.containsKey("osName")&& StringUtils.isNotBlank((String)parameters.get("osName"))){//因为前面接口本身就不支持条件查询，只能大致模拟迷糊查询
            osInfos.removeIf(osInfo -> !osInfo.getOsName().contains((String)parameters.get("osName")));
        }
        if (parameters.containsKey("sortValue") && parameters.get("sortValue").equals("ASC")){
            osInfos.sort(Comparator.comparing(IOsInfo::getLastModifyDate, Comparator.nullsFirst(Date::compareTo)));
        }else {
            osInfos.sort(Comparator.comparing(IOsInfo::getLastModifyDate, Comparator.nullsFirst(Date::compareTo)).reversed());
        }
        JSONArray jsonArray = new JSONArray();
        for (IOsInfo osInfo : osInfos) {
            JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(osInfo), JSONObject.class);
            Map map = new HashMap();
            map.put("groupId",osInfo.getOsId());
            map.put("roleCode","组长");
            List<WorkGroup> workGroup = workGroupManager.listWorkGroup(map,null);
            if (workGroup!=null&&workGroup.size()>0){
                String userName = CodeRepositoryUtil.getUserName(topUnit, workGroup.get(0).getWorkGroupParameter().getUserCode());
                jsonObject.put("createUserName",userName);
                jsonObject.put("userCode",workGroup.get(0).getWorkGroupParameter().getUserCode());
            }
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }

    @Override
    public JSONObject getApplicationInfo(String applicationId,String topUnit) {
        iOsInfo = platformEnvironment.getOsInfo(applicationId);
        if (!topUnit.equals(iOsInfo.getTopUnit())){
            throw new ObjectException(ResponseData.HTTP_NON_AUTHORITATIVE_INFORMATION, "您没有权限");
        }
        fileLibrary = operateFileLibrary.getFileLibrary(applicationId);
        Map map = new HashMap();
        map.put("groupId",applicationId);
        List<WorkGroup>   workGroup = workGroupManager.listWorkGroup(map,null);
        if (notHaveAuth(workGroup)) {
            throw new ObjectException(ResponseData.HTTP_NON_AUTHORITATIVE_INFORMATION, "您没有权限");
        }
        optInfos = (List<IOptInfo>) platformEnvironment.listMenuOptInfosUnderOsId(applicationId);
        return assemblyApplicationInfo();
    }

    private boolean notHaveAuth(List<WorkGroup>   workGroups) {
        String loginUser = WebOptUtils.getCurrentUserCode(
            RequestThreadLocal.getLocalThreadWrapperRequest());
        if (StringBaseOpt.isNvl(loginUser)) {
            loginUser = WebOptUtils.getRequestFirstOneParameter(
                RequestThreadLocal.getLocalThreadWrapperRequest(), "userCode");
        }
        if (StringBaseOpt.isNvl(loginUser)) {
            return true;
        }
        for (WorkGroup workGroup : workGroups) {
            if (workGroup.getWorkGroupParameter().getUserCode().equals(loginUser)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public IOsInfo deleteApplicationInfo(String applicationId) {
        return platformEnvironment.deleteOsInfo(applicationId);
    }

    @Override
    public IOsInfo updateApplicationInfo(OsInfo osInfo) {
        return platformEnvironment.updateOsInfo(osInfo);
    }

    @Override
    public JSONObject getResourceInfo(Map<String, Object> parameters) {

        String topUnit = MapUtils.getString(parameters, "topUnit");
        String sourceType = MapUtils.getString(parameters, "sourceType","D");

        JSONObject tenantInfo = tenantManageService.getTenantInfoByTopUnit(topUnit);
        int dataBaseCount = metadataManageService.countDataBase(CollectionsOpt.createHashMap("topUnit", topUnit, "sourceType", sourceType));
        int unitCount = platformEnvironment.countUnitByTopUnit(topUnit);
        int userCount = platformEnvironment.countUserByTopUnit(topUnit);
        List<? extends IOsInfo> osInfos = platformEnvironment.listOsInfos(topUnit);
        int osCount = CollectionUtils.sizeIsEmpty(osInfos) ? 0 : osInfos.size();

        tenantInfo.put("databaseCount",dataBaseCount);
        tenantInfo.put("unitCount",unitCount);
        tenantInfo.put("userCount",userCount);
        tenantInfo.put("osCount",osCount);
        return tenantInfo;
    }

    private void createOsInfoAndOther(OsInfo osInfo) {
        //获取工作组组长code
        String leaderCode = osInfo.getCreated();
        OsInfo assemblyOsInfo = assemblyOsInfo(osInfo);
        iOsInfo = platformEnvironment.addOsInfo(assemblyOsInfo);
        createWorkGroup(leaderCode);
        createFileLibrary();
        createOptInfos();
        updateOsInfo();
    }

    private void createWorkGroup(String leaderCode) {
        workGroup.clear();
        workGroup.add(assemblyWorkGroupInfo(leaderCode));
        workGroupManager.batchWorkGroup(workGroup);
    }

    private void createFileLibrary() {
        FileLibraryInfo assemblyLibrary = assemblyFileLibraryInfo();
        fileLibrary = operateFileLibrary.insertFileLibrary(assemblyLibrary);
    }

    private void createOptInfos() {
        optInfos.clear();
        createParentMenu();
        creatSubMenuAndAddOptList(CodeRepositoryUtil.OPT_INFO_FORM_CODE_COMMON);
        creatSubMenuAndAddOptList(OPTINFO_FORMCODE_PAGEENTER);
    }

    private void updateOsInfo() {
        iOsInfo.setRelOptId(iOsInfo.getOsId());
        platformEnvironment.updateOsInfo(iOsInfo);
    }

    private JSONObject assemblyApplicationInfo() {
        JSONObject result = new JSONObject();
        result.put("osInfo", iOsInfo);
        result.put("workGroup", workGroup);
        result.put("fileLibrary", fileLibrary);
        result.put("submenu", optInfos);
        return result;
    }

    private OsInfo assemblyOsInfo(OsInfo osInfo) {
        String loginUser = WebOptUtils.getCurrentUserCode(
            RequestThreadLocal.getLocalThreadWrapperRequest());
        if (StringBaseOpt.isNvl(loginUser) && StringBaseOpt.isNvl(osInfo.getCreated())) {
            throw new ObjectException(ResponseData.ERROR_USER_LOGIN_ERROR,
                "没有登录的用户");
        }
        String topUnit = WebOptUtils.getCurrentTopUnit(
            RequestThreadLocal.getLocalThreadWrapperRequest());
        if (StringBaseOpt.isNvl(topUnit) && StringBaseOpt.isNvl(osInfo.getTopUnit())) {
            throw new ObjectException(ResponseData.ERROR_USER_LOGIN_ERROR,
                "没有所属租户");
        }
        osInfo.setCreated(loginUser);
        if (StringBaseOpt.isNvl(osInfo.getTopUnit())) {
            osInfo.setTopUnit(topUnit);
        }
        osInfo.setOsType(IOsInfo.OSTYPE_LOCODE);
        osInfo.setOsId(null);
        osInfo.setDeleted(false);
        return osInfo;
    }

    private WorkGroup assemblyWorkGroupInfo(String leaderCode) {
        WorkGroup workGroup = new WorkGroup();
        workGroup.setCreator(iOsInfo.getCreated());
        WorkGroupParameter workGroupParameter = new WorkGroupParameter();
        workGroupParameter.setRoleCode(WORKGROUP_ROLECODE_LEADER);
        workGroupParameter.setGroupId(iOsInfo.getOsId());
        workGroupParameter.setUserCode(StringUtils.isBlank(leaderCode)?iOsInfo.getCreated():leaderCode);
        workGroup.setWorkGroupParameter(workGroupParameter);
        return workGroup;
    }

    private FileLibraryInfo assemblyFileLibraryInfo() {
        FileLibraryInfo fileLibrary = new FileLibraryInfo();
        fileLibrary.setLibraryId(iOsInfo.getOsId());
        fileLibrary.setLibraryName(iOsInfo.getOsName());
        fileLibrary.setLibraryType(FILE_TYPE_ITEM);
        fileLibrary.setCreateUser(iOsInfo.getCreated());
        return fileLibrary;
    }

    private void createParentMenu() {
        OptInfo result = assemblyParentMenuInfo();
        platformEnvironment.addOptInfo(result);
    }

    private void creatSubMenuAndAddOptList(String type) {
        OptInfo result = assemblySubMenuInfo(type);
        IOptInfo optInfo = platformEnvironment.addOptInfo(result);
        optInfos.add(optInfo);
    }

    private OptInfo assemblyParentMenuInfo() {
        OptInfo result = new OptInfo();
        result.setOptId(iOsInfo.getOsId());
        result.setOptName(iOsInfo.getOsName());
        result.setIsInToolbar(OPTINFO_INTOOLBAR_NO);
        result.setFormCode(OPTINFO_FORMCODE_ITEM);
        result.setOptUrl("");
        result.setOptType(OPTINFO_OPTTYPE_COMMON);
        result.setTopOptId(iOsInfo.getOsId());
        return result;
    }

    private OptInfo assemblySubMenuInfo(String type) {
        OptInfo result = new OptInfo();
        result.setIsInToolbar(OPTINFO_INTOOLBAR_NO);
        result.setPreOptId(iOsInfo.getOsId());
        result.setTopOptId(iOsInfo.getOsId());
        result.setOptUrl("");
        switch (type) {
            case CodeRepositoryUtil.OPT_INFO_FORM_CODE_COMMON:
                result.setOptName("通用模块");
                result.setFormCode(CodeRepositoryUtil.OPT_INFO_FORM_CODE_COMMON);
                break;
            case OPTINFO_FORMCODE_PAGEENTER:
                result.setOptName("应用入口页面");
                result.setFormCode(OPTINFO_FORMCODE_PAGEENTER);
                break;
            default:
        }
        return result;
    }

    /**
     * 验证租户下的应用数量是否达到最大限制
     * @param topUnit 租户code
     */
    private void checkOsNumberLimitIsOver(String topUnit) {
        if (StringUtils.isBlank(topUnit)){
            throw new ObjectException("topUnit不能为空!");
        }
        JSONObject tenantInfo = tenantManageService.getTenantInfoByTopUnit(topUnit);
        if (null == tenantInfo){
            throw new ObjectException("租户信息有误!");
        }
        List<? extends IOsInfo> osInfos = platformEnvironment.listOsInfos(topUnit);
        int osCount = CollectionUtils.sizeIsEmpty(osInfos) ? 0 : osInfos.size();
        if (osCount>=tenantInfo.getIntValue("osNumberLimit")){
            throw new ObjectException("应用个数达到最大限制!");
        }
    }
}
