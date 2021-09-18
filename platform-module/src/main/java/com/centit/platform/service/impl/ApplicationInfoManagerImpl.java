package com.centit.platform.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.centit.fileserver.common.IFileLibrary;
import com.centit.fileserver.common.OperateFileLibrary;
import com.centit.framework.common.ResponseData;
import com.centit.framework.common.WebOptUtils;
import com.centit.framework.filter.RequestThreadLocal;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.model.basedata.IOptInfo;
import com.centit.framework.model.basedata.IOsInfo;
import com.centit.platform.service.ApplicationInfoManager;
import com.centit.product.dao.WorkGroupDao;
import com.centit.product.po.WorkGroup;
import com.centit.product.po.WorkGroupParameter;
import com.centit.product.service.WorkGroupManager;
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
    @Autowired
    private WorkGroupDao workGroupDao;
    @Autowired
    private WorkGroupManager workGroupManager;
    private final static String FILE_TYPE_ITEM = "I";
    private final static String OPTINFO_INTOOLBAR_NO = "N";
    private final static String OPTINFO_FORMCODE_ITEM = "I";
    private final static String OPTINFO_FORMCODE_COMMON = "C";
    private final static String OPTINFO_FORMCODE_PAGEENTER = "A";
    private final static String WORKGROUP_ROLECODE_LEADER = "组长";
    private IOsInfo iOsInfo;
    private IFileLibrary fileLibrary;
    private List<IOptInfo> optInfos = new ArrayList<>();
    private List<WorkGroup> workGroup = new ArrayList<>();

    @Override
    public JSONObject createApplicationInfo(IOsInfo osInfo) {
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
        fileLibrary = fileStore.getFileLibrary(applicationId);
        workGroup = workGroupDao.listObjectsByProperty("groupId", applicationId);
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
        if (StringBaseOpt.isNvl(loginUser)) {
            return true;
        }
        for (WorkGroup workGroup : workGroup) {
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
    public IOsInfo updateApplicationInfo(IOsInfo osInfo) {
        return platformEnvironment.updateOsInfo(osInfo);
    }

    private void createOsInfoAndOther(IOsInfo osInfo) {
        IOsInfo assemblyOsInfo = assemblyOsInfo(osInfo);
        iOsInfo = platformEnvironment.addOsInfo(assemblyOsInfo);
        createWorkGroup();
        createFileLibrary();
        createOptInfos();
    }

    private void createWorkGroup() {
        workGroup.clear();
        workGroup.add(assemblyWorkGroupInfo());
        workGroupManager.batchWorkGroup(workGroup);
    }

    private void createFileLibrary() {
        fileLibrary = assemblyFileLibraryInfo();
        fileStore.insertFileLibrary(fileLibrary);
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
        result.put("workGroup", workGroup);
        result.put("fileLibrary", fileLibrary);
        result.put("submenu", optInfos);
        return result;
    }

    private IOsInfo assemblyOsInfo(IOsInfo osInfo) {
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
        if (StringBaseOpt.isNvl(osInfo.getCreated())) {
            osInfo.setCreated(loginUser);
        }
        if (StringBaseOpt.isNvl(osInfo.getTopUnit())) {
            osInfo.setTopUnit(topUnit);
        }
        osInfo.setOsType(IOsInfo.OSTYPE_LOCODE);
        osInfo.setOsId(null);
        return osInfo;
    }

    private WorkGroup assemblyWorkGroupInfo() {
        WorkGroup workGroup = new WorkGroup();
        workGroup.setCreator(iOsInfo.getCreated());
        WorkGroupParameter workGroupParameter = new WorkGroupParameter();
        workGroupParameter.setRoleCode(WORKGROUP_ROLECODE_LEADER);
        workGroupParameter.setGroupId(iOsInfo.getOsId());
        workGroupParameter.setUserCode(iOsInfo.getCreated());
        workGroup.setWorkGroupParameter(workGroupParameter);
        return workGroup;
    }

    private IFileLibrary assemblyFileLibraryInfo() {
        IFileLibrary fileLibrary = fileStore.getInstance();
        fileLibrary.setLibraryId(iOsInfo.getOsId());
        fileLibrary.setLibraryName(iOsInfo.getOsName());
        fileLibrary.setLibraryType(FILE_TYPE_ITEM);
        fileLibrary.setCreateUser(iOsInfo.getCreated());
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
