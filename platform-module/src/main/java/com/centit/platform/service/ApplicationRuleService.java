package com.centit.platform.service;

import com.centit.platform.po.ApplicationRule;
import com.centit.support.database.utils.PageDesc;

import java.util.List;
import java.util.Map;

/**
 * @author tian_y
 */
public interface ApplicationRuleService {

    void createApplicationRule(ApplicationRule applicationRule);

    void updateApplicationRule(ApplicationRule applicationRule);

    void deleteApplicationRule(String id);

    List<ApplicationRule> listApplicationRule(Map<String, Object> params, PageDesc pageDesc);

    ApplicationRule getApplicationRule(String id);

    void deleteAppRule(String osId, String ruleId);
}
