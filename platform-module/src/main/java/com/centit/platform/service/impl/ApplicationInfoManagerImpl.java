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
    private IOsInfo iOsInfo;
    private JSONObject fileLibraryInfo;
    private List<? extends IOptInfo> menuFunsByUser;


    @Override
    public JSONObject createApplicationInfo(JSONObject osInfo) {
        iOsInfo = createOsInfo(osInfo);
        fileLibraryInfo = insertFileLibrary(iOsInfo);
        //创建模块
        return assemblyJsonObject();
    }

    private IOsInfo createOsInfo(JSONObject osInfo) {
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
        return platformEnvironment.addOsInfo(osInfo);
    }

    private JSONObject insertFileLibrary(IOsInfo iOsInfo) {
        JSONObject result = new JSONObject();
        result.put("libraryId", iOsInfo.getOsId());
        result.put("libraryName", iOsInfo.getOsName());
        result.put("libraryType", FILE_TYPE_ITEM);
        result.put("createUser", iOsInfo.getCreated());
        JSONArray jsonArray = new JSONArray();
        JSONObject teamUser = new JSONObject();
        teamUser.put("libraryId", iOsInfo.getOsId());
        teamUser.put("accessUsercode", iOsInfo.getCreated());
        jsonArray.add(teamUser);
        result.put("fileLibraryAccesss", jsonArray);
        return fileStore.insertFileLibrary(result);
    }

    private JSONObject assemblyJsonObject() {
        JSONObject result = new JSONObject();
        result.put("osInfo", iOsInfo);
        result.put("fileLibrary", fileLibraryInfo);
        result.put("submenu", menuFunsByUser);
        return result;
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
        String loginUser = WebOptUtils.getCurrentUserCode(
            RequestThreadLocal.getLocalThreadWrapperRequest());
        menuFunsByUser = platformEnvironment
            .listUserMenuOptInfosUnderSuperOptId(loginUser, applicationId, false);
        return assemblyJsonObject();
    }

    @Override
    public IOsInfo deleteApplicationInfo(String applicationId) {
        return platformEnvironment.deleteOsInfo(applicationId);
    }

    @Override
    public IOsInfo updateApplicationInfo(JSONObject osInfo) {
        return platformEnvironment.updateOsInfo(osInfo);
    }
}
