package com.centit.platform.service.impl;

import com.centit.platform.dao.ApplicationRuleDao;
import com.centit.platform.po.ApplicationRule;
import com.centit.platform.service.ApplicationRuleService;
import com.centit.product.metadata.dao.DataCheckRuleDao;
import com.centit.support.database.utils.PageDesc;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author tian_y
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ApplicationRuleServiceImpl implements ApplicationRuleService {

    @Autowired
    private ApplicationRuleDao applicationRuleDao;

    @Autowired
    private DataCheckRuleDao dataCheckRuleDao;

    @Override
    public void createApplicationRule(ApplicationRule applicationRule) {
        applicationRuleDao.saveNewObject(applicationRule);
    }

    @Override
    public void updateApplicationRule(ApplicationRule applicationRule) {
        applicationRuleDao.updateObject(applicationRule);
    }

    @Override
    public void deleteApplicationRule(String id) {
        applicationRuleDao.deleteObjectById(id);
    }

    @Override
    public List<ApplicationRule> listApplicationRule(Map<String, Object> params, PageDesc pageDesc) {
        return applicationRuleDao.listObjectsByProperties(params, pageDesc);
    }

    @Override
    public ApplicationRule getApplicationRule(String id) {
        return applicationRuleDao.getObjectById(id);
    }

    @Override
    public void deleteAppRule(String osId, String ruleId) {
        if (osId == null) {
            return;
        }
        Map<String, Object> filterMap = new HashMap<>();
        filterMap.put("osId", osId);
        if (StringUtils.isNotBlank(ruleId)) {
            filterMap.put("ruleId", ruleId);
        }
        applicationRuleDao.deleteObjectsByProperties(filterMap);
    }

    @Override
    public void deleteCheckRule(String ruleId) {
        dataCheckRuleDao.deleteObjectById(ruleId);
        Map<String, Object> filterMap = new HashMap<>();
        if (StringUtils.isNotBlank(ruleId)) {
            filterMap.put("ruleId", ruleId);
        }
        applicationRuleDao.deleteObjectsByProperties(filterMap);
    }
}
