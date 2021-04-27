package com.centit.platform.dao;

import com.centit.framework.jdbc.dao.BaseDaoImpl;
import com.centit.platform.po.GroupInfo;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * @author zhf
 */
@Repository
public class GroupInfoDao extends BaseDaoImpl<GroupInfo, String> {
    @Override
    public Map<String, String> getFilterField() {
        return null;
    }
}
