package com.centit.locode.platform.test;

import com.centit.support.security.SecurityOptUtils;

public class TestAes  {

	public static void main(String[] args){
        System.out.println(SecurityOptUtils.encodeSecurityString("cipher:KhktDzQz67BqoVkEOWgkEg==","cipher"));
    }
}
