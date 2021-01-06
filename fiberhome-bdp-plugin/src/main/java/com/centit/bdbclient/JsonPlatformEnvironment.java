package com.centit.bdbclient;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.framework.components.CodeRepositoryCache;
import com.centit.framework.model.basedata.IOsInfo;
import com.centit.framework.model.basedata.IUserInfo;
import com.centit.framework.staticsystem.po.*;
import com.centit.framework.system.dao.DataCatalogDao;
import com.centit.framework.system.dao.DataDictionaryDao;
import com.centit.support.file.FileIOOpt;
import com.centit.support.file.FileSystemOpt;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.List;

public class JsonPlatformEnvironment extends AbstractStaticPlatformEnvironment {

    private static Log logger = LogFactory.getLog(com.centit.framework.staticsystem.service.impl.JsonPlatformEnvironment.class);

    protected String appHome;

    @Autowired
    private DataDictionaryDao dataDictionaryDao;

    @Autowired
    private DataCatalogDao dataCatalogDao;


    public void setAppHome(String appHome) {
        this.appHome = appHome;
    }

    private void loadConfigFromJSONString(String jsonStr){
        JSONObject json = JSON.parseObject(jsonStr);
        JSONArray tempJa= json.getJSONArray("userInfos");
        if(tempJa!=null) {
            List<UserInfo> userinfos = tempJa.toJavaList(UserInfo.class);
            CodeRepositoryCache.userInfoRepo.setFreshData(userinfos);
        }
        tempJa= json.getJSONArray("optInfos");
        if(tempJa!=null) {
            List<OptInfo> optinfos = tempJa.toJavaList(OptInfo.class);
            CodeRepositoryCache.optInfoRepo.setFreshData(optinfos);
        }
        tempJa= json.getJSONArray("optMethods");
        if(tempJa!=null) {
            List<OptMethod> optmethods = tempJa.toJavaList(OptMethod.class);
            CodeRepositoryCache.optMethodRepo.setFreshData(optmethods);
        }

        tempJa= json.getJSONArray("optDataScopes");
        if(tempJa!=null) {
            optDataScopes = tempJa.toJavaList(OptDataScope.class);
        }

        tempJa= json.getJSONArray("roleInfos");
        if(tempJa!=null) {
            List<RoleInfo> roleinfos = tempJa.toJavaList(RoleInfo.class);
            CodeRepositoryCache.roleInfoRepo.setFreshData(roleinfos);
        }

        tempJa= json.getJSONArray("rolePowers");
        if(tempJa!=null) {
            List<RolePower> rolepowers = tempJa.toJavaList(RolePower.class);
            CodeRepositoryCache.rolePowerRepo.setFreshData(rolepowers);
        }

        tempJa = json.getJSONArray("userRoles");
        if (tempJa != null) {
            List<UserRole> userroles = tempJa.toJavaList(UserRole.class);
            allUserRoleRepo.setFreshData(userroles);
        }

        tempJa = json.getJSONArray("unitInfos");
        if (tempJa != null) {
            List<UnitInfo> unitinfos = tempJa.toJavaList(UnitInfo.class);
            CodeRepositoryCache.unitInfoRepo.setFreshData(unitinfos);
        }

        tempJa = json.getJSONArray("userUnits");
        if (tempJa != null) {
            List<UserUnit> userunits = tempJa.toJavaList(UserUnit.class);
            CodeRepositoryCache.userUnitRepo.setFreshData(userunits);
        }

        tempJa = json.getJSONArray("osInfos");
        if (tempJa != null) {
            List<OsInfo> osInfos = tempJa.toJavaList(OsInfo.class);
            CodeRepositoryCache.osInfoCache.setFreshData(osInfos);
        }

        tempJa = json.getJSONArray("dataCatalogs");
        if (tempJa != null) {
            List<DataCatalog> datacatalogs = tempJa.toJavaList(DataCatalog.class);
            CodeRepositoryCache.catalogRepo.setFreshData(datacatalogs);
        }

        tempJa = json.getJSONArray("dataDictionaries");
        if (tempJa != null) {
            List<DataDictionary> datadictionaies = tempJa.toJavaList(DataDictionary.class);
            allDictionaryRepo.setFreshData(datadictionaies);
        }
    }

    public String loadJsonStringFormConfigFile(String fileName) throws IOException {
        String jsonFile = appHome +"/config"+ fileName;
        if(FileSystemOpt.existFile(jsonFile)) {
            return FileIOOpt.readStringFromFile(jsonFile,"UTF-8");
        }else{
            return FileIOOpt.readStringFromInputStream(
                    new ClassPathResource(fileName).getInputStream(),"UTF-8");
        }
    }
    /**
     * 刷新数据字典
     */
    protected synchronized void reloadPlatformData() {
        try {
            //CodeRepositoryCache.evictAllCache();
            String jsonstr = loadJsonStringFormConfigFile("/static_system_config.json");
            loadConfigFromJSONString(jsonstr);
        } catch (IOException e) {
            logger.error(e.getLocalizedMessage());
        }
        organizePlatformData();
        //static_system_user_pwd.json
        try {
            String jsonStr = loadJsonStringFormConfigFile("/static_system_user_pwd.json");
            JSONObject json = JSON.parseObject(jsonStr);
            for(IUserInfo u : CodeRepositoryCache.userInfoRepo.getCachedTarget()){
                String spwd = json.getString(u.getUserCode());
                if(StringUtils.isNotBlank(spwd))
                    ((UserInfo)u).setUserPin(spwd);
            }
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        }
    }

    /**
     * 修改用户密码
     *
     * @param userCode userCode
     * @param userPassword userPassword
     */
    @Override
    public void changeUserPassword(String userCode, String userPassword) {
        UserInfo ui= (UserInfo) CodeRepositoryCache.codeToUserMap.getCachedTarget().get(userCode);
        if(ui==null)
            return;
        JSONObject json = null;
        String jsonFile = "/static_system_user_pwd.json";
        try {
            String jsonstr = loadJsonStringFormConfigFile(jsonFile);
            json = JSON.parseObject(jsonstr);
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        }

        if(json==null)
            json = new JSONObject();
        try {
            ui.setUserPin(passwordEncoder.encodePassword(userPassword, userCode));
            json.put(userCode,ui.getUserPin());
            FileIOOpt.writeStringToFile(json.toJSONString(),appHome +"/config"+jsonFile);
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        }
    }

    /**
     * 获取所有的字段类型
     */
    @Override
    public List<com.centit.framework.system.po.DataCatalog> listAllDataCatalogs() {
        return dataCatalogDao.listObjects();
        /*return RestfulHttpRequest.getResponseObjectList(
            appSession,"/catalogs/"+topOptId, DataCatalog.class);*/
    }

    /**
     * 根据字典类型查询字典明细
     * @param catalogCode 字典类别
     */
    @Override
    public List<com.centit.framework.system.po.DataDictionary> listDataDictionaries(String catalogCode) {
        return dataDictionaryDao.listDataDictionary(catalogCode);
        /*return RestfulHttpRequest.getResponseObjectList(
            appSession,"/dictionary/"+topOptId+"/"+catalogCode,
            DataDictionary.class);*/
    }

}
