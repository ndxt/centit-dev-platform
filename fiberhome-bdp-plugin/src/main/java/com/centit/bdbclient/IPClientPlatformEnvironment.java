package com.centit.bdbclient;

import com.alibaba.fastjson.JSONArray;
import com.centit.framework.appclient.AppSession;
import com.centit.framework.appclient.HttpReceiveJSON;
import com.centit.framework.appclient.RestfulHttpRequest;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.model.basedata.*;
import com.centit.framework.security.model.CentitUserDetails;
import com.centit.framework.security.model.JsonCentitUserDetails;
import com.centit.framework.staticsystem.po.*;
import com.centit.framework.system.dao.DataCatalogDao;
import com.centit.framework.system.dao.DataDictionaryDao;
import com.centit.framework.system.po.DataCatalog;
import com.centit.framework.system.po.DataDictionary;
import com.centit.support.algorithm.StringRegularOpt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 集成平台客户端业务配置新, 所有的访问需要添加一个cache策略
 * @author codefan
 *
 */
public class IPClientPlatformEnvironment implements PlatformEnvironment {

    private Logger logger = LoggerFactory.getLogger(IPClientPlatformEnvironment.class);

    private String topOptId;

    public IPClientPlatformEnvironment() {

    }

    public void setTopOptId(String topOptId) {
        this.topOptId = topOptId;
    }

    private AppSession appSession;

    public AppSession getPlatAppSession() {
        return this.appSession;
    }

    public void createPlatAppSession(String appServerUrl,boolean needAuthenticated,String userCode,String password){
        appSession = new AppSession(appServerUrl,needAuthenticated,userCode,password);
    }

    @Autowired
    private DataDictionaryDao dataDictionaryDao;

    @Autowired
    private DataCatalogDao dataCatalogDao;

    @Override
    public UserSetting getUserSetting(String userCode, String paramCode) {
        HttpReceiveJSON resJson = RestfulHttpRequest.getResponseData(
            appSession,
            "/platform/usersetting/"+userCode+"/"+paramCode);

        if(resJson==null)
            return null;
        return resJson.getDataAsObject(UserSetting.class);
    }

    /**
     * 获取全部个人设置
     *
     * @param userCode 用户编码
     * @return 个人设置列表
     */
    @Override
    public List<? extends IUserSetting> listUserSettings(String userCode) {
        return RestfulHttpRequest.getResponseObjectList(
            appSession,
            "/platform/usersettings/"+userCode+"/"+topOptId,
            UserSetting.class);
    }

    @Override
    public void saveUserSetting(IUserSetting userSetting) {
        RestfulHttpRequest.jsonPost(appSession,"/platform/usersetting",
            userSetting);
    }

    @Override
    public List<OptInfo> listUserMenuOptInfos(String userCode, boolean asAdmin) {

        return listUserMenuOptInfosUnderSuperOptId(userCode,topOptId,asAdmin);
    }

    @Override
    public List<OptInfo> listUserMenuOptInfosUnderSuperOptId(String userCode, String superOptId,
                                                             boolean asAdmin) {
        return RestfulHttpRequest.getResponseObjectList(
            appSession,
            "/platform/usermenu/"+superOptId+"/"+userCode+"?asAdmin="+asAdmin,
            OptInfo.class);

    }

    @Override
    public List<UserRole> listUserRoles(String userCode) {
        return RestfulHttpRequest.getResponseObjectList(
            appSession,
            "/platform/userroles/"+userCode,
            UserRole.class);
    }

    @Override
    public List<UserRole> listRoleUsers(String roleCode) {
        return RestfulHttpRequest.getResponseObjectList(
            appSession,
            "/platform/roleusers/"+roleCode,
            UserRole.class);
    }

    @Override
    public List<? extends IUnitRole> listUnitRoles(String unitCode) {
        return RestfulHttpRequest.getResponseObjectList(
            appSession,
            "/platform/unitroles/"+unitCode,
            IUnitRole.class);
    }

    @Override
    public List<? extends IUnitRole> listRoleUnits(String roleCode) {
        return RestfulHttpRequest.getResponseObjectList(
            appSession,
            "/platform/roleunits/"+roleCode,
            IUnitRole.class);
    }

    @Override
    public void changeUserPassword(String userCode, String userPassword) {

        Map<String,String> userInfo = new HashMap<>();
        userInfo.put("userCode", userCode);
        userInfo.put("password", userPassword);
        userInfo.put("newPassword", userPassword);
        RestfulHttpRequest.jsonPost(appSession,
            "/platform/changepassword/"+userCode,
            userInfo);
    }

    @Override
    public boolean checkUserPassword(String userCode, String userPassword) {

        Map<String,String> userInfo = new HashMap<>();
        userInfo.put("userCode", userCode);
        userInfo.put("password", userPassword);
        userInfo.put("newPassword", userPassword);
        String sret = RestfulHttpRequest.jsonPost(
            appSession,"/platform/checkpassword/"+userCode,
            userInfo, true);
        return StringRegularOpt.isTrue(sret);

    }

    @Override
    public List<UserInfo> listAllUsers() {
        return RestfulHttpRequest.getResponseObjectList(
            appSession,
            "/platform/allusers/"+topOptId,
            UserInfo.class);
    }

    @Override
    public List<UnitInfo> listAllUnits() {
        return RestfulHttpRequest.getResponseObjectList(
            appSession,
            "/platform/allunits/"+topOptId,
            UnitInfo.class);
    }

    @Override
    public List<UserUnit> listAllUserUnits() {
        return RestfulHttpRequest.getResponseObjectList(
            appSession,
            "/platform/alluserunits/"+topOptId,
            UserUnit.class);
    }

    @Override
    public List<UserUnit> listUserUnits(String userCode) {
        return RestfulHttpRequest.getResponseObjectList(
            appSession,
            "/platform/userunits/"+topOptId+"/"+userCode,
            UserUnit.class);
    }

    @Override
    public List<UserUnit> listUnitUsers(String unitCode) {
        return RestfulHttpRequest.getResponseObjectList(
            appSession,
            "/platform/unitusers/"+topOptId+"/"+unitCode,
            UserUnit.class);
    }

    /**
     * 获取所有角色信息
     *
     * @return List 操作方法信息
     */
    @Override
    public List<? extends IRoleInfo> listAllRoleInfo() {
        return RestfulHttpRequest.getResponseObjectList(
            appSession,
            "/platform/rolerepo/"+topOptId,
            RoleInfo.class);
    }

    @Override
    public List<DataCatalog> listAllDataCatalogs() {
        return dataCatalogDao.listObjects();
        /*return RestfulHttpRequest.getResponseObjectList(
                appSession,
                "/platform/catalogs/"+topOptId,
                DataCatalog.class);*/
    }

    @Override
    public List<DataDictionary> listDataDictionaries(String catalogCode) {
        return dataDictionaryDao.listDataDictionary(catalogCode);
        /*return RestfulHttpRequest.getResponseObjectList(
                appSession,
                "/platform/dictionary/"+topOptId+"/"+catalogCode,
                DataDictionary.class);*/
    }


    public List<RolePower>  listAllRolePower(){
        return RestfulHttpRequest.getResponseObjectList(
            appSession,
            "/platform/allrolepowers/"+topOptId,
            RolePower.class);
    }

    /**
     * 获取业务操作信息
     *
     * @return List 业务信息
     */
    @Override
    public List<? extends IOptInfo> listAllOptInfo() {
        return RestfulHttpRequest.getResponseObjectList(
            appSession,
            "/platform/optinforepo/"+topOptId,
            OptInfo.class);
    }

    @Override
    public List<OptMethod> listAllOptMethod(){
        return RestfulHttpRequest.getResponseObjectList(
            appSession,
            "/platform/alloptmethods/"+topOptId,
            OptMethod.class);
    }

    /**
     * @return 所有的数据范围定义表达式
     */
    @Override
    public List<? extends IOptDataScope> listAllOptDataScope() {
        return RestfulHttpRequest.getResponseObjectList(
            appSession,
            "/platform/alloptdatascopes/"+topOptId,
            OptDataScope.class);
    }

    private CentitUserDetails loadUserDetails(String queryParam, String qtype) {
        HttpReceiveJSON resJson = RestfulHttpRequest.getResponseData(
            appSession,"/platform/userdetails/"+topOptId+"/"+queryParam+"?qtype="+qtype);

        if(resJson==null || resJson.getCode()!=0) {
            return null;
        }
        JsonCentitUserDetails userDetails =
            resJson.getDataAsObject("userDetails", JsonCentitUserDetails.class);
        //userDetails.getUserInfo().put("userPin", resJson.getDataAsString("userPin"));
        userDetails.setUserUnits(
            (JSONArray) resJson.getData("userUnits"));//, UserUnit.class));
        userDetails.setAuthoritiesByRoles(userDetails.getUserRoles());
        return userDetails;
    }

    @Override
    public CentitUserDetails loadUserDetailsByLoginName(String loginName) {
        return loadUserDetails(loginName,"loginName");
    }

    @Override
    public CentitUserDetails loadUserDetailsByUserCode(String userCode) {
        return loadUserDetails(userCode,"userCode");
    }

    @Override
    public CentitUserDetails loadUserDetailsByRegEmail(String regEmail) {
        return loadUserDetails(regEmail,"regEmail");
    }

    @Override
    public CentitUserDetails loadUserDetailsByRegCellPhone(String regCellPhone) {
        return loadUserDetails(regCellPhone,"regCellPhone");
    }

    @Override
    public void updateUserInfo(IUserInfo userInfo) {
        RestfulHttpRequest.jsonPost(
            appSession,"/platform/userinfo",
            userInfo,true);
    }

    /**
     * 新增菜单和操作
     * @param optInfos 菜单对象集合
     * @param optMethods 操作对象集合
     */
    @Override
    public void insertOrUpdateMenu(List<? extends IOptInfo> optInfos, List<? extends IOptMethod> optMethods) {
        Map<String, Object> param = new HashMap<>(4);
        param.put("optInfos", optInfos);
        param.put("optMethods", optMethods);
        RestfulHttpRequest.jsonPost(appSession,"/platform/insertopt", param);
    }

    @Override
    public List<? extends IOsInfo> listOsInfos() {
        return  RestfulHttpRequest.getResponseObjectList(
            appSession,
            "/platform/ipenvironment/osinfo",
            OsInfo.class);
    }
}
