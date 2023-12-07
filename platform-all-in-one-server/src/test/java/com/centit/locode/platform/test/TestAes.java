package com.centit.locode.platform.test;

import com.centit.support.security.SecurityOptUtils;

public class TestAes  {

	public static void main(String[] args){

        System.out.println(SecurityOptUtils.decodeSecurityString(
            "aescbc:0zvjYuUGkJZuxz56T3jrGaa6pilpVxLocFor9DPuuy6/SIn3u31i9Vex76DPY0ymPDWRW/EnQwHYRY76706wzQ=="));
        //cipher:KhktDzQz67BqoVkEOWgkEg==
        System.out.println(SecurityOptUtils.decodeSecurityString("cipher:KhktDzQz67BqoVkEOWgkEg=="));
        //System.out.println(SecurityOptUtils.encodeSecurityString("cipher:KhktDzQz67BqoVkEOWgkEg==","cipher"));
    }
}
