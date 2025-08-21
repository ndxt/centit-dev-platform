package com.centit.locode.platform.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.centit.fileserver.common.FileLibraryInfo;
import com.centit.fileserver.common.OperateFileLibrary;
import com.centit.framework.common.ResponseData;
import com.centit.framework.components.CodeRepositoryUtil;
import com.centit.framework.jdbc.dao.DatabaseOptUtils;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.model.basedata.OptInfo;
import com.centit.framework.model.basedata.OsInfo;
import com.centit.framework.model.basedata.WorkGroup;
import com.centit.framework.model.basedata.WorkGroupParameter;
import com.centit.locode.platform.dao.ApplicationDictionaryDao;
import com.centit.locode.platform.service.ApplicationInfoManager;
import com.centit.product.metadata.api.MetadataManageService;
import com.centit.support.algorithm.BooleanBaseOpt;
import com.centit.support.algorithm.CollectionsOpt;
import com.centit.support.algorithm.UuidOpt;
import com.centit.support.common.ObjectException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

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
    private MetadataManageService metadataManageService;

    private final static String FILE_TYPE_ITEM = "I";

    @Autowired
    private ApplicationDictionaryDao applicationDictionaryDao;

    @Override
    public JSONObject createApplicationInfo(OsInfo osInfo) {
        //验证应用数量是否达到限制
        checkOsNumberLimitIsOver(osInfo.getTopUnit());
        //获取工作组组长code
        String leaderCode = osInfo.getCreated();
        OsInfo assemblyOsInfo = assemblyOsInfo(osInfo);
        // 如果主键为空 生成主键
        String osId = StringUtils.isBlank(osInfo.getOsId()) ? UuidOpt.getUuidAsString22() : osInfo.getOsId();
        assemblyOsInfo.setOsId(osId);
        assemblyOsInfo.setRelOptId(osId);
        assemblyOsInfo = platformEnvironment.addOsInfo(assemblyOsInfo);
        WorkGroup wg = createWorkGroup(assemblyOsInfo, leaderCode);
        FileLibraryInfo fileLibrary = createFileLibrary(assemblyOsInfo);
        List<OptInfo> optInfos = createOptInfos(assemblyOsInfo);
        return assemblyApplicationInfo(assemblyOsInfo, CollectionsOpt.createList(wg), fileLibrary, optInfos);
    }

    @Override
    public JSONArray listApplicationInfo(String topUnit, String userCode, Map<String, Object> parameters) {
        List<OsInfo> osInfos = platformEnvironment.listOsInfos(topUnit);
        osInfos.removeIf(osInfo -> BooleanBaseOpt.castObjectToBoolean(osInfo.isDeleted(), true));
        if (parameters.containsKey("osName") && StringUtils.isNotBlank((String) parameters.get("osName"))) {
            //因为前面接口本身就不支持条件查询，只能大致模拟迷糊查询
            osInfos.removeIf(osInfo -> !osInfo.getOsName().contains((String) parameters.get("osName")));
        }
        if (parameters.containsKey("sortValue") && parameters.get("sortValue").equals("ASC")) {
            osInfos.sort(Comparator.comparing(OsInfo::getLastModifyDate, Comparator.nullsFirst(Date::compareTo)));
        } else {
            osInfos.sort(Comparator.comparing(OsInfo::getLastModifyDate, Comparator.nullsFirst(Date::compareTo)).reversed());
        }

        if (parameters.get("involved") != null) {
            List<WorkGroup> workGroups = platformEnvironment.listWorkGroup(null, userCode, null);
            List<String> osId = workGroups.stream().map(WorkGroup::getGroupId).collect(Collectors.toList());
            osInfos.removeIf(osInfo -> !osId.contains(osInfo.getOsId()));
        }

        JSONArray jsonArray = new JSONArray();
        for (OsInfo osInfo : osInfos) {
            JSONObject jsonObject = JSONObject.from(osInfo);
            //JSON.parseObject(JSON.toJSONStringWithDateFormat(osInfo,JSON.DEFFAULT_DATE_FORMAT), JSONObject.class);
            List<WorkGroup> workGroup = platformEnvironment.listWorkGroup(osInfo.getOsId(), null, "组长");
            if (workGroup != null && workGroup.size() > 0) {
                String userName = CodeRepositoryUtil.getUserName(topUnit, workGroup.get(0).getUserCode());
                jsonObject.put("createUserName", userName);
                jsonObject.put("userCode", workGroup.get(0).getUserCode());
            }
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }

    @Override
    public JSONObject getApplicationInfo(String applicationId, String topUnit, String loginUser, boolean checkAuth) {
        OsInfo osInfo = platformEnvironment.getOsInfo(applicationId);
        if (osInfo == null) {
            return null;
        }
        if (checkAuth && !topUnit.equals(osInfo.getTopUnit())) {
            throw new ObjectException(ResponseData.HTTP_NON_AUTHORITATIVE_INFORMATION, "您没有权限");
        }
        FileLibraryInfo fileLibrary = operateFileLibrary.getFileLibrary(topUnit, applicationId);
        List<WorkGroup> workGroup = null;
        if (checkAuth) {
            workGroup = platformEnvironment.listWorkGroup(applicationId, null, null);
            if (notHaveAuth(workGroup, loginUser)) {
                throw new ObjectException(ResponseData.HTTP_NON_AUTHORITATIVE_INFORMATION, "您没有权限");
            }
        }
        List<OptInfo> optInfos = platformEnvironment.listMenuOptInfosUnderOsId(applicationId);

        return assemblyApplicationInfo(osInfo, workGroup, fileLibrary, optInfos);
    }

    private boolean notHaveAuth(List<WorkGroup> workGroups, String loginUser) {
        if (StringUtils.isBlank(loginUser)) {
            return true;
        }
        for (WorkGroup workGroup : workGroups) {
            if (workGroup.getUserCode().equals(loginUser)) {
                return false;
            }
        }
        return true;
    }

    @Override
    @Transactional
    public OsInfo deleteApplicationInfo(String applicationId) {
        Object[] params = {applicationId};

        try {
            OsInfo osInfo = platformEnvironment.getOsInfo(applicationId);
            String sql;

            // 删除关联数据包参数草稿
            sql = "delete from q_data_packet_param_draft where packet_id in (" +
                "select packet_id from q_data_packet_draft where OS_ID=?)";
            DatabaseOptUtils.doExecuteSql(applicationDictionaryDao, sql, params);

            // 删除数据包草稿
            sql = "delete from q_data_packet_draft where os_id=?";
            DatabaseOptUtils.doExecuteSql(applicationDictionaryDao, sql, params);

            // 删除正式数据包参数
            sql = "delete from q_data_packet_param where packet_id in (" +
                "select packet_id from q_data_packet where OS_ID=?)";
            DatabaseOptUtils.doExecuteSql(applicationDictionaryDao, sql, params);

            // 删除正式数据包
            sql = "delete from q_data_packet where os_id=?";
            DatabaseOptUtils.doExecuteSql(applicationDictionaryDao, sql, params);

            // 删除表单模型草稿
            sql = "delete from m_meta_form_model_draft where os_id=?";
            DatabaseOptUtils.doExecuteSql(applicationDictionaryDao, sql, params);

            // 删除正式表单模型
            sql = "delete from m_meta_form_model where os_id=?";
            DatabaseOptUtils.doExecuteSql(applicationDictionaryDao, sql, params);

            // 删除角色权限
            sql = "delete from F_ROLEPOWER where OPT_CODE in " +
                "(select a.OPT_CODE from f_optdef a join f_optinfo b on a.opt_id=b.opt_id where b.os_id=?)";
            DatabaseOptUtils.doExecuteSql(applicationDictionaryDao, sql, params);

            // 删除操作定义
            sql = "delete from f_optdef where opt_id in (select opt_id from f_optinfo where os_id=?)";
            DatabaseOptUtils.doExecuteSql(applicationDictionaryDao, sql, params);

            // 删除流程变量定义
            sql = "delete from wf_opt_variable_define where opt_id in " +
                "(select opt_id from f_optinfo where top_opt_id=?)";
            DatabaseOptUtils.doExecuteSql(applicationDictionaryDao, sql, params);

            // 删除流程团队角色
            sql = "delete from wf_opt_team_role where opt_id in " +
                "(select opt_id from f_optinfo where top_opt_id=?)";
            DatabaseOptUtils.doExecuteSql(applicationDictionaryDao, sql, params);

            // 删除操作信息
            sql = "delete from f_optinfo where os_id=?";
            DatabaseOptUtils.doExecuteSql(applicationDictionaryDao, sql, params);

            // 删除用户角色
            sql = "delete from f_userrole where role_code in (select role_code from f_roleinfo where os_id=?)";
            DatabaseOptUtils.doExecuteSql(applicationDictionaryDao, sql, params);

            // 删除角色信息
            sql = "delete from f_roleinfo where os_id=?";
            DatabaseOptUtils.doExecuteSql(applicationDictionaryDao, sql, params);

            // 删除流程节点
            sql = "delete from wf_node where flow_code in(" +
                "select flow_code from wf_flow_define where OS_ID=? )";
            DatabaseOptUtils.doExecuteSql(applicationDictionaryDao, sql, params);

            // 删除流程转移
            sql = "delete from wf_transition where flow_code in(" +
                "select flow_code from wf_flow_define where OS_ID=? )";
            DatabaseOptUtils.doExecuteSql(applicationDictionaryDao, sql, params);

            // 删除流程定义
            sql = "delete from wf_flow_define where os_id=?";
            DatabaseOptUtils.doExecuteSql(applicationDictionaryDao, sql, params);

            // 删除应用字典
            sql = "delete from m_application_dictionary where os_id=?";
            DatabaseOptUtils.doExecuteSql(applicationDictionaryDao, sql, params);

            // 删除应用资源
            sql = "delete from m_application_resources where os_id=?";
            DatabaseOptUtils.doExecuteSql(applicationDictionaryDao, sql, params);

            // 删除表与操作关系
            sql = "delete from f_table_opt_relation where os_id=?";
            DatabaseOptUtils.doExecuteSql(applicationDictionaryDao, sql, params);

            // 删除文件库信息
            sql = "delete from file_library_info where library_id=?";
            DatabaseOptUtils.doExecuteSql(applicationDictionaryDao, sql, params);

            // 删除工作组
            sql = "delete from work_group where group_id=?";
            DatabaseOptUtils.doExecuteSql(applicationDictionaryDao, sql, params);

            // 删除历史版本
            sql = "delete from history_version where os_id=?";
            DatabaseOptUtils.doExecuteSql(applicationDictionaryDao, sql, params);

            // 删除帮助文档
            sql = "delete from f_help_doc where os_id=?";
            DatabaseOptUtils.doExecuteSql(applicationDictionaryDao, sql, params);

            // 最后删除 OS 信息
            sql = "delete from f_os_info where os_id=?";
            DatabaseOptUtils.doExecuteSql(applicationDictionaryDao, sql, params);

            return osInfo;

        } catch (Exception e) {
            // 记录错误日志
            throw new RuntimeException("删除 applicationId=" + applicationId + " 时发生错误", e);
        }
    }


    @Override
    public OsInfo updateApplicationInfo(OsInfo osInfo) {
        return platformEnvironment.updateOsInfo(osInfo);
    }

    @Override
    public JSONObject getResourceInfo(Map<String, Object> parameters) {

        String topUnit = MapUtils.getString(parameters, "topUnit");

        JSONObject tenantInfo = platformEnvironment.getTenantInfoByTopUnit(topUnit);
        int dataBaseCount = metadataManageService.countDataBase(CollectionsOpt.createHashMap("topUnit", topUnit));
        tenantInfo.put("databaseCount", dataBaseCount);
        return tenantInfo;
    }

    private WorkGroup createWorkGroup(OsInfo osInfo, String leaderCode) {
        WorkGroup wf = assemblyWorkGroupInfo(osInfo, leaderCode);
        platformEnvironment.batchSaveWorkGroup(CollectionsOpt.createList(wf));
        return wf;
    }

    private FileLibraryInfo createFileLibrary(OsInfo osInfo) {
        FileLibraryInfo assemblyLibrary = assemblyFileLibraryInfo(osInfo);
        return operateFileLibrary.insertFileLibrary(assemblyLibrary);
    }

    private List<OptInfo> createOptInfos(OsInfo osInfo) {
        List<OptInfo> optInfos = new ArrayList<>();
        createParentMenu(osInfo);
        OptInfo optInfo = creatSubMenuAndAddOptInfo(osInfo, OptInfo.OPT_INFO_FORM_CODE_COMMON, OptInfo.OPT_INFO_FORM_CODE_COMMON_NAME);
        optInfos.add(optInfo);
        optInfo = creatSubMenuAndAddOptInfo(osInfo, OptInfo.OPT_INFO_FORM_CODE_PAGE_ENTER, OptInfo.OPT_INFO_FORM_CODE_PAGE_ENTER_NAME);
        optInfos.add(optInfo);
        return optInfos;
    }


    private JSONObject assemblyApplicationInfo(OsInfo osInfo, List<WorkGroup> workGroup,
                                               FileLibraryInfo fileLibrary, List<OptInfo> optInfos) {
        JSONObject result = new JSONObject();
        result.put("osInfo", osInfo);
        result.put("workGroup", workGroup);
        result.put("fileLibrary", fileLibrary);
        result.put("submenu", optInfos);
        return result;
    }

    private OsInfo assemblyOsInfo(OsInfo osInfo) {
        if (StringUtils.isBlank(osInfo.getCreated())) {
            throw new ObjectException(ResponseData.ERROR_USER_LOGIN_ERROR,
                "没有登录的用户");
        }
        if (StringUtils.isBlank(osInfo.getTopUnit())) {
            throw new ObjectException(ResponseData.ERROR_USER_LOGIN_ERROR,
                "没有所属租户");
        }
        osInfo.setOsType(OsInfo.OSTYPE_LOCODE);
        osInfo.setDeleted(false);
        return osInfo;
    }

    private WorkGroup assemblyWorkGroupInfo(OsInfo osInfo, String leaderCode) {
        WorkGroup workGroup = new WorkGroup();
        workGroup.setCreator(osInfo.getCreated());
        WorkGroupParameter workGroupParameter = new WorkGroupParameter();
        workGroupParameter.setRoleCode("组长");// TenantConstant.WORKGROUP_ROLE_CODE_LEADER);
        workGroupParameter.setGroupId(osInfo.getOsId());
        workGroupParameter.setUserCode(StringUtils.isBlank(leaderCode) ? osInfo.getCreated() : leaderCode);
        workGroup.setWorkGroupParameter(workGroupParameter);
        return workGroup;
    }

    private FileLibraryInfo assemblyFileLibraryInfo(OsInfo osInfo) {
        FileLibraryInfo fileLibrary = new FileLibraryInfo();
        fileLibrary.setLibraryId(osInfo.getOsId());
        fileLibrary.setLibraryName(osInfo.getOsName());
        fileLibrary.setLibraryType(FILE_TYPE_ITEM);
        fileLibrary.setCreateUser(osInfo.getCreated());
        String topUnit = osInfo.getTopUnit();
        fileLibrary.setOwnUnit(topUnit);
        return fileLibrary;
    }

    private OptInfo createParentMenu(OsInfo osInfo) {
        OptInfo result = new OptInfo();
        result.setOsId(osInfo.getOsId());
        result.setOptId(osInfo.getRelOptId());
        result.setPreOptId("0");
        result.setOptName(osInfo.getOsName());
        result.setIsInToolbar(OptInfo.OPT_INFO_IN_TOOLBAR_NO);
        result.setFormCode(OptInfo.OPT_INFO_FORM_CODE_ITEM);
        result.setOptUrl("");
        result.setOptType(OptInfo.OPT_INFO_OPT_TYPE_COMMON);
        result.setTopOptId(osInfo.getRelOptId());
        return platformEnvironment.addOptInfo(result);
    }

    private OptInfo creatSubMenuAndAddOptInfo(OsInfo osInfo, String type, String optName) {
        OptInfo result = new OptInfo();
        result.setIsInToolbar(OptInfo.OPT_INFO_IN_TOOLBAR_NO);
        result.setPreOptId(osInfo.getRelOptId());
        result.setTopOptId(osInfo.getRelOptId());
        result.setOsId(osInfo.getOsId());
        result.setOptUrl("");
        result.setFormCode(type);
        result.setOptName(optName);
        return platformEnvironment.addOptInfo(result);
    }

    /**
     * 验证租户下的应用数量是否达到最大限制
     *
     * @param topUnit 租户code
     */
    private void checkOsNumberLimitIsOver(String topUnit) {
        if (StringUtils.isBlank(topUnit)) {
            throw new ObjectException(ResponseData.ERROR_USER_NOT_LOGIN, "topUnit不能为空!");
        }
        JSONObject tenantInfo = platformEnvironment.getTenantInfoByTopUnit(topUnit);
        if (null == tenantInfo) {
            throw new ObjectException("租户信息有误!");
        }
        List<OsInfo> osInfos = platformEnvironment.listOsInfos(topUnit);
        int osCount = CollectionUtils.sizeIsEmpty(osInfos) ? 0 : osInfos.size();
        if (osCount >= tenantInfo.getIntValue("osNumberLimit")) {
            throw new ObjectException("应用个数达到最大限制!");
        }
    }
}
