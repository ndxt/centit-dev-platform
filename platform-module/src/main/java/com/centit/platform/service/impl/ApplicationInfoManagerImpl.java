package com.centit.platform.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.centit.fileserver.common.FileLibrary;
import com.centit.fileserver.common.OperateFileLibrary;
import com.centit.framework.common.ResponseData;
import com.centit.framework.common.WebOptUtils;
import com.centit.framework.filter.RequestThreadLocal;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.model.basedata.IOptInfo;
import com.centit.framework.model.basedata.IOsInfo;
import com.centit.framework.system.po.OptInfo;
import com.centit.framework.system.po.OsInfo;
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

import java.util.*;

/**
 * @author zhf
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ApplicationInfoManagerImpl implements ApplicationInfoManager {
    @Autowired
    private PlatformEnvironment platformEnvironment;
    @Autowired(required = false)
    private OperateFileLibrary operateFileLibrary;
    @Autowired
    private WorkGroupDao workGroupDao;
    @Autowired
    private WorkGroupManager workGroupManager;
    private final static String FILE_TYPE_ITEM = "I";
    private final static String OPTINFO_INTOOLBAR_NO = "N";
    private final static String OPTINFO_OPTTYPE_COMMON = "O";
    private final static String OPTINFO_FORMCODE_ITEM = "I";
    private final static String OPTINFO_FORMCODE_COMMON = "C";
    private final static String OPTINFO_FORMCODE_PAGEENTER = "A";
    private final static String WORKGROUP_ROLECODE_LEADER = "组长";
    private IOsInfo iOsInfo;
    private FileLibrary fileLibrary;
    private List<IOptInfo> optInfos = new ArrayList<>();
    private List<WorkGroup> workGroup = new ArrayList<>();

    @Override
    public JSONObject createApplicationInfo(OsInfo osInfo) {
        createOsInfoAndOther(osInfo);
        return assemblyApplicationInfo();
    }

    @Override
    public List<? extends IOsInfo> listApplicationInfo(String topUnit, Map<String, Object> parameters) {
        List<? extends IOsInfo> osInfos = platformEnvironment.listOsInfos(topUnit);
        osInfos.removeIf(osInfo -> BooleanBaseOpt.castObjectToBoolean(osInfo.isDeleted(), true));
        if (parameters.containsKey("osName")){//因为前面接口本身就不支持条件查询，只能大致模拟迷糊查询
            osInfos.removeIf(osInfo -> !osInfo.getOsName().contains((String)parameters.get("osName")));
        }
        if (parameters.containsKey("sortValue") && parameters.get("sortValue").equals("ASC")){
            osInfos.sort(Comparator.comparing(IOsInfo::getLastModifyDate, Comparator.nullsFirst(Date::compareTo)));
        }else {
            osInfos.sort(Comparator.comparing(IOsInfo::getLastModifyDate, Comparator.nullsFirst(Date::compareTo)).reversed());
        }
        return osInfos;
    }

    @Override
    public JSONObject getApplicationInfo(String applicationId) {
        iOsInfo = platformEnvironment.getOsInfo(applicationId);
        fileLibrary = operateFileLibrary.getFileLibrary(applicationId);
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
    public IOsInfo updateApplicationInfo(OsInfo osInfo) {
        return platformEnvironment.updateOsInfo(osInfo);
    }

    private void createOsInfoAndOther(OsInfo osInfo) {
        OsInfo assemblyOsInfo = assemblyOsInfo(osInfo);
        iOsInfo = platformEnvironment.addOsInfo(assemblyOsInfo);
        createWorkGroup();
        createFileLibrary();
        createOptInfos();
        updateOsInfo();
    }

    private void createWorkGroup() {
        workGroup.clear();
        workGroup.add(assemblyWorkGroupInfo());
        workGroupManager.batchWorkGroup(workGroup);
    }

    private void createFileLibrary() {
        FileLibrary assemblyLibrary = assemblyFileLibraryInfo();
        fileLibrary = operateFileLibrary.insertFileLibrary(assemblyLibrary);
    }

    private void createOptInfos() {
        optInfos.clear();
        createParentMenu();
        creatSubMenuAndAddOptList(OPTINFO_FORMCODE_COMMON);
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
        if (StringBaseOpt.isNvl(osInfo.getCreated())) {
            osInfo.setCreated(loginUser);
        }
        if (StringBaseOpt.isNvl(osInfo.getTopUnit())) {
            osInfo.setTopUnit(topUnit);
        }
        osInfo.setOsType(IOsInfo.OSTYPE_LOCODE);
        osInfo.setOsId(null);
        osInfo.setDeleted(false);
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

    private FileLibrary assemblyFileLibraryInfo() {
        FileLibrary fileLibrary = new FileLibrary();
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
        return result;
    }

    private OptInfo assemblySubMenuInfo(String type) {
        OptInfo result = new OptInfo();
        result.setIsInToolbar(OPTINFO_INTOOLBAR_NO);
        result.setPreOptId(iOsInfo.getOsId());
        result.setOptUrl("");
        switch (type) {
            case OPTINFO_FORMCODE_COMMON:
                result.setOptName("通用模块");
                result.setFormCode(OPTINFO_FORMCODE_COMMON);
                break;
            case OPTINFO_FORMCODE_PAGEENTER:
                result.setOptName("应用入口页面");
                result.setFormCode(OPTINFO_FORMCODE_PAGEENTER);
                break;
            default:
        }
        return result;
    }

}
