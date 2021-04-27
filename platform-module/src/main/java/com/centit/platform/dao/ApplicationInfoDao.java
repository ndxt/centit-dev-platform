package com.centit.platform.dao;

import com.centit.platform.po.ApplicationInfo;
import com.centit.framework.jdbc.dao.BaseDaoImpl;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class ApplicationInfoDao extends BaseDaoImpl<ApplicationInfo, String> {
    @Override
    public Map<String, String> getFilterField() {
        return null;
    }
}
