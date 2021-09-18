package com.centit.platform.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.fileserver.common.OperateFileLibrary;
import com.centit.framework.common.ResponseData;
import com.centit.framework.common.WebOptUtils;
import com.centit.framework.filter.RequestThreadLocal;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.model.basedata.IOptInfo;
import com.centit.framework.model.basedata.IOsInfo;
import com.centit.platform.service.ApplicationInfoManager;
import com.centit.support.algorithm.BooleanBaseOpt;
import com.centit.support.algorithm.StringBaseOpt;
import com.centit.support.common.ObjectException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * @author zhf
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ApplicationInfoManagerImpl implements ApplicationInfoManager {
    @Autowired
    private PlatformEnvironment platformEnvironment;
    @Autowired
    private OperateFileLibrary fileStore;
    private final static String FILE_TYPE_ITEM = "I";
    private final static String OSINFO_CREATED_NAME = "created";
    private final static String OSINFO_TOPUNIT_NAME = "topUnit";
    private final static String OPTINFO_INTOOLBAR_NO = "N";
    private final static String OPTINFO_FORMCODE_ITEM = "I";
    private final static String OPTINFO_FORMCODE_COMMON = "C";
    private final static String OPTINFO_FORMCODE_PAGEENTER = "A";
    private final static String WORKGROUP_ROLECODE_LEADER = "组长";
    private IOsInfo iOsInfo;
    private JSONObject fileLibraryInfo;
    private List<IOptInfo> optInfos = new ArrayList<>();


    @Override
    public JSONObject createApplicationInfo(JSONObject osInfo) {
        createOsInfoAndOther(osInfo);
        return assemblyApplicationInfo();
    }

    @Override
    public List<? extends IOsInfo> listApplicationInfo(String topUnit) {
        List<? extends IOsInfo> osInfos = platformEnvironment.listOsInfos(topUnit);
        osInfos.removeIf(osInfo -> BooleanBaseOpt.castObjectToBoolean(osInfo.getIsDelete(), true));
        osInfos.sort(Comparator.comparing(IOsInfo::getLastModifyDate, Comparator.nullsFirst(Date::compareTo)).reversed());
        return osInfos;
    }

    @Override
    public JSONObject getApplicationInfo(String applicationId) {
        iOsInfo = platformEnvironment.getOsInfo(applicationId);
        fileLibraryInfo = fileStore.getFileLibrary(applicationId);
        if (notHaveAuth()) {
            throw new ObjectException(ResponseData.HTTP_NON_AUTHORITATIVE_INFORMATION, "您没有权限");
        }
        optInfos = (List<IOptInfo>) platformEnvironment.listMenuOptInfosUnderOsId(applicationId);
        return assemblyApplicationInfo();
    }

    private boolean notHaveAuth() {
        String loginUser = WebOptUtils.getCurrentUserCode(
            RequestThreadLocal.getLocalThreadWrapperRequest());
        if (StringBaseOpt.isNvl(loginUser)) {
            loginUser = WebOptUtils.getRequestFirstOneParameter(
                RequestThreadLocal.getLocalThreadWrapperRequest(), "userCode");
        }
        if(StringBaseOpt.isNvl(loginUser)){
            return true;
        }
        JSONArray workGroups = fileLibraryInfo.getJSONArray("workGroups");
        for (int i = 0; i < workGroups.size(); i++) {
            JSONObject user = workGroups.getJSONObject(i).getJSONObject("workGroupParameter");
            if (user.getString("userCode").equals(loginUser)) {
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
    public IOsInfo updateApplicationInfo(JSONObject osInfo) {
        return platformEnvironment.updateOsInfo(osInfo);
    }

    private void createOsInfoAndOther(JSONObject osInfo) {
        JSONObject result = assemblyOsInfo(osInfo);
        iOsInfo = platformEnvironment.addOsInfo(result);
        createFileLibrary();
        createOptInfos();
    }

    private void createFileLibrary() {
        JSONObject fileLibrary = assemblyFileLibraryInfo();
        fileLibraryInfo = fileStore.insertFileLibrary(fileLibrary);
    }

    private void createOptInfos() {
        optInfos.clear();
        createParentMenu();
        creatSubMenuAndAddOptList(OPTINFO_FORMCODE_COMMON);
        creatSubMenuAndAddOptList(OPTINFO_FORMCODE_PAGEENTER);
    }

    private JSONObject assemblyApplicationInfo() {
        JSONObject result = new JSONObject();
        result.put("osInfo", iOsInfo);
        result.put("fileLibrary", fileLibraryInfo);
        result.put("submenu", optInfos);
        return result;
    }

    private JSONObject assemblyOsInfo(JSONObject osInfo) {
        String loginUser = WebOptUtils.getCurrentUserCode(
            RequestThreadLocal.getLocalThreadWrapperRequest());
        if (StringBaseOpt.isNvl(loginUser) && StringBaseOpt.isNvl(osInfo.getString(OSINFO_CREATED_NAME))) {
            throw new ObjectException(ResponseData.ERROR_USER_LOGIN_ERROR,
                "没有登录的用户");
        }
        String topUnit = WebOptUtils.getCurrentTopUnit(
            RequestThreadLocal.getLocalThreadWrapperRequest());
        if (StringBaseOpt.isNvl(topUnit) && StringBaseOpt.isNvl(osInfo.getString(OSINFO_TOPUNIT_NAME))) {
            throw new ObjectException(ResponseData.ERROR_USER_LOGIN_ERROR,
                "没有所属租户");
        }
        if (StringBaseOpt.isNvl(osInfo.getString(OSINFO_CREATED_NAME))) {
            osInfo.put(OSINFO_CREATED_NAME, loginUser);
        }
        if (StringBaseOpt.isNvl(osInfo.getString(OSINFO_TOPUNIT_NAME))) {
            osInfo.put(OSINFO_TOPUNIT_NAME, topUnit);
        }
        osInfo.put("osType", IOsInfo.OSTYPE_LOCODE);
        return osInfo;
    }

    private JSONObject assemblyFileLibraryInfo() {
        JSONObject fileLibrary = new JSONObject();
        fileLibrary.put("libraryId", iOsInfo.getOsId());
        fileLibrary.put("libraryName", iOsInfo.getOsName());
        fileLibrary.put("libraryType", FILE_TYPE_ITEM);
        fileLibrary.put("createUser", iOsInfo.getCreated());
        JSONArray teamUsers = new JSONArray();
        JSONObject teamUser = new JSONObject();
        JSONObject teamUserPrimary = new JSONObject();
        teamUserPrimary.put("groupId", iOsInfo.getOsId());
        teamUserPrimary.put("userCode", iOsInfo.getCreated());
        teamUserPrimary.put("roleCode", WORKGROUP_ROLECODE_LEADER);
        teamUser.put("workGroupParameter", teamUserPrimary);
        teamUser.put("creator", iOsInfo.getCreated());
        teamUsers.add(teamUser);
        fileLibrary.put("workGroups", teamUsers);
        return fileLibrary;
    }

    private void createParentMenu() {
        JSONObject result = assemblyParentMenuInfo();
        platformEnvironment.addOptInfo(result);
    }

    private void creatSubMenuAndAddOptList(String type) {
        JSONObject result = assemblySubMenuInfo(type);
        IOptInfo optInfo = platformEnvironment.addOptInfo(result);
        optInfos.add(optInfo);
    }

    private JSONObject assemblyParentMenuInfo() {
        JSONObject result = new JSONObject();
        result.put("optId", iOsInfo.getOsId());
        result.put("optName", iOsInfo.getOsName());
        result.put("isInToolbar", OPTINFO_INTOOLBAR_NO);
        result.put("formCode", OPTINFO_FORMCODE_ITEM);
        result.put("optUrl", "");
        return result;
    }

    private JSONObject assemblySubMenuInfo(String type) {
        JSONObject result = new JSONObject();
        result.put("isInToolbar", OPTINFO_INTOOLBAR_NO);
        result.put("preOptId", iOsInfo.getOsId());
        result.put("optUrl", "");
        switch (type) {
            case OPTINFO_FORMCODE_COMMON:
                result.put("optName", "通用业务");
                result.put("formCode", OPTINFO_FORMCODE_COMMON);
                break;
            case OPTINFO_FORMCODE_PAGEENTER:
                result.put("optName", "应用入口页面");
                result.put("formCode", OPTINFO_FORMCODE_PAGEENTER);
                break;
            default:
        }
        return result;
    }

}
