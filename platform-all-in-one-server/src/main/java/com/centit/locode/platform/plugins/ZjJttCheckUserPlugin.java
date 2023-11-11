package com.centit.locode.platform.plugins;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.model.security.CentitUserDetails;
import com.centit.framework.model.security.ThirdPartyCheckUserDetails;
import com.centit.support.algorithm.DatetimeOpt;
import com.centit.support.security.SecurityOptUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

public class ZjJttCheckUserPlugin implements ThirdPartyCheckUserDetails {
    @Override
    public CentitUserDetails check(PlatformEnvironment platformEnvironment, JSONObject token) {
        String accessToken = token.getString("accessToken");
        if(StringUtils.isBlank(accessToken)){
            return null;
        }
        // AES_SECRET_KEY_SPEC   = "U2FsdGVkX1BymlPj";
        // AES_IV_PARAMETER_SPEC = "WUG1TpTpkinX9pNs";
        accessToken = SecurityOptUtils.decodeSecurityString(accessToken);
        if(StringUtils.isBlank(accessToken)){
            return null;
        }
        JSONObject jsonObject = JSON.parseObject(accessToken);
        if(jsonObject==null){
            return null;
        }
        String userCode = jsonObject.getString("userCode");
        Date loginTime = DatetimeOpt.castObjectToDate(jsonObject.get("loginTime"));
        Date currentTime = DatetimeOpt.currentUtilDate();
        if(StringUtils.isBlank(userCode) || loginTime==null || loginTime.after(DatetimeOpt.addMinutes(currentTime, 2)) ||
            loginTime.before(DatetimeOpt.addMinutes(currentTime, -2))) {
            return null;
        }
        return platformEnvironment.loadUserDetailsByUserCode(userCode);
    }
}
