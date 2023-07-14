package com.centit.locode.platform.dao;

import com.centit.framework.core.dao.CodeBook;
import com.centit.framework.jdbc.dao.BaseDaoImpl;
import com.centit.locode.platform.po.ApplicationTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class ApplicationTemplateDao extends BaseDaoImpl<ApplicationTemplate, String> {
    @Override
    public Map<String, String> getFilterField() {
        Map<String, String> filterField = new HashMap<>();
        filterField.put("templateName", CodeBook.LIKE_HQL_ID);
        filterField.put("isUsed", CodeBook.EQUAL_HQL_ID);
        filterField.put("templateType", CodeBook.EQUAL_HQL_ID);
        return filterField;
    }
}
