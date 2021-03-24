package com.centit.platformmodule.dao;

import com.centit.framework.jdbc.dao.BaseDaoImpl;
import com.centit.platformmodule.po.ApplicationTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class ApplicationTemplateDao extends BaseDaoImpl<ApplicationTemplate, String> {
    @Override
    public Map<String, String> getFilterField() {
        return null;
    }
}
