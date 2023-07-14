package com.centit.locode.runtime.service;

import com.centit.framework.security.model.CentitUserDetails;

/**
 * 导入操作和导出操作，不是对称的，
 * 导入操作仅仅导入文件信息和工作流引擎信息
 * 作为一个额外的特性，可以导入数据字典
 * @author codefan@sina.com
 */
public interface EnvironmentImportManager {

    /**
     * @param importType dictionary，file，flow
     * @param ud 操作人员信息
     */
    void importEnvironment(String importType, CentitUserDetails ud);
}
