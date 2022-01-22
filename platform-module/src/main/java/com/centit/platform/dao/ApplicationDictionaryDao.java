package com.centit.platform.dao;

import com.centit.framework.jdbc.dao.BaseDaoImpl;
import com.centit.platform.po.ApplicationDictionary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class ApplicationDictionaryDao extends BaseDaoImpl<ApplicationDictionary, String> {

    public Map<String, Object> getReferences(String dictionaryId){
        String sql = " SELECT CATALOG_NAME, CATALOG_STYLE, OPT_ID FROM F_DATACATALOG WHERE CATALOG_CODE = ?";
        return this.getJdbcTemplate().queryForMap(sql, new Object[]{dictionaryId});
    }
}
