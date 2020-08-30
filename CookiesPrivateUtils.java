package com.dabaoyutech.lingshou.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookiesPrivateUtils {

    //    例子
    private JSONObject LZ(HttpServletResponse response,HttpServletRequest request) {
        CookiesPrivateUtils CookiesPrivateUtils = new CookiesPrivateUtils();
//        设置Cookies值
        JSONObject SetCookiesJSONObject = CookiesPrivateUtils.SetCookies(response,request, "Patha", "1");
        if (!SetCookiesJSONObject.getString("errcode").equals("0")) {
            return SetCookiesJSONObject;
        }
//        取Cookies值
        JSONObject GetCookiesJSONObject = CookiesPrivateUtils.GetCookies(response);
        if (!GetCookiesJSONObject.getString("errcode").equals("0")) {
            return GetCookiesJSONObject;
        }
        JSONObject GetCookiesJSONObjectMsg = JSONObject.parseObject(GetCookiesJSONObject.getString("msg"));
        System.out.println("取单Cookies1：" + GetCookiesJSONObjectMsg.getString("Patha"));
//        删除Cookies值
        JSONObject deleteCookiesJSONObject = CookiesPrivateUtils.deleteCookies(response,request, "Patha");
        if (!deleteCookiesJSONObject.getString("errcode").equals("0")) {
            return deleteCookiesJSONObject;
        }
//        取Cookies值 转换类型 Cookies[]到JSONObject
        GetCookiesJSONObject = CookiesPrivateUtils.GetCookies(response);
        if (!GetCookiesJSONObject.getString("errcode").equals("0")) {
            return GetCookiesJSONObject;
        }
        GetCookiesJSONObjectMsg = JSONObject.parseObject(GetCookiesJSONObject.getString("msg"));
        System.out.println("取单Cookies2：" + GetCookiesJSONObjectMsg.getString("Patha"));

//        不在例子范围-------------开始
        return new JSONObject();
//        不在例子范围-------------结束
    }

    //    添加单Cookies
    public JSONObject SetCookies(HttpServletResponse response,HttpServletRequest request, String name, String value) {
        JSONObject Return = new JSONObject();
        JSONObject TokenPrivateUtilsJSONObject = GetCookies(request);
        if (!TokenPrivateUtilsJSONObject.getString("errcode").equals("0")) {
            return TokenPrivateUtilsJSONObject;
        }
        JSONObject TokenPrivateUtilsJSONObjectMsg = JSONObject.parseObject(TokenPrivateUtilsJSONObject.getString("msg"));
        if (value == null) {
            TokenPrivateUtilsJSONObjectMsg.remove(name);
        } else {
            TokenPrivateUtilsJSONObjectMsg.put(name, value);
        }
        StringBuilder cookieString = new StringBuilder();
        for (String key : TokenPrivateUtilsJSONObjectMsg.keySet()) {
            cookieString.append(key).append("=").append(TokenPrivateUtilsJSONObjectMsg.getString(key)).append(";");
        }
//        cookieString.append(" HttpOnly;");
        response.setHeader("set-cookie", cookieString.toString());
//        response.setHeader("cookie", cookieString.toString());

//        System.out.println("添加单Cookies cookieString.toString()：" + cookieString.toString());
        Return.put("errcode", "0");
        Return.put("msg", "成功");
        return Return;
    }

    //    删除单Cookies
    public JSONObject deleteCookies(HttpServletResponse response,HttpServletRequest request, String name) {
        JSONObject Return = new JSONObject();
        JSONObject SetCookiesJSONObject = SetCookies(response,request, name, null);
        if (!SetCookiesJSONObject.getString("errcode").equals("0")) {
            return SetCookiesJSONObject;
        }
        Return.put("errcode", "0");
        Return.put("msg", "成功");
        return Return;
    }

    //    取全部Cookies response版
    public JSONObject GetCookies(HttpServletResponse response) {
        JSONObject Return = new JSONObject();

//        System.out.println("取getHeaders set-cookies：" + response.getHeaders("set-cookie"));
//        System.out.println("取getHeaders cookies：" + response.getHeaders("cookie"));
        JSONObject Headers=new JSONObject();
        for(String Header:response.getHeaderNames()){
            String value =response.getHeader(Header);
            Headers.put(Header.toLowerCase(),value);
//            System.out.println("取Header：" + Header.toLowerCase()+"    :"+value);
        }

//        System.out.println("取Cookies set-cookies：" + Headers.getString("set-cookie"));
//        System.out.println("取Cookies cookies：" + Headers.getString("cookie"));
        String cookie= Headers.getString("set-cookie");
        if(StringUtils.isBlank(cookie)){
            cookie= Headers.getString("cookie");
            if(StringUtils.isBlank(cookie)){
                Return.put("errcode", "1");
                Return.put("msg", "cookie为空");
                return Return;
            }
        }
        String[] cookiesArr = cookie.split(";");
        JSONObject cookieObject = new JSONObject();
        for (String cookiesArrValue : cookiesArr) {
//            System.out.println("取Cookies cookiesArrValue：" + cookiesArrValue);
            String[] cookiesArrValueValue = cookiesArrValue.split("=");
            if (cookiesArrValueValue.length == 2) {
                cookieObject.put(cookiesArrValueValue[0], cookiesArrValueValue[1]);
            } else if (cookiesArrValueValue.length > 2) {
                StringBuilder o = new StringBuilder();
                for (int i = 1; i < cookiesArrValueValue.length; i++) {
                    o.append(cookiesArrValueValue[i]).append("=");
                }
                o.append("=");
                cookieObject.put(cookiesArrValueValue[0], o.toString());
            }else {
//                cookieObject.put(cookiesArrValueValue[0], "");
            }
        }
        Return.put("errcode", "0");
        Return.put("msg", JSONObject.toJSONString(cookieObject));
        System.out.println("取全部Cookies JSONObject.toJSONString(cookieObject)：" + JSONObject.toJSONString(cookieObject));
        return Return;
    }

    //    取全部Cookies request版
    public JSONObject GetCookies(HttpServletRequest request) {
        JSONObject Return = new JSONObject();
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            Return.put("errcode", "1");
            Return.put("msg", "取cookies失败");
            return Return;
        }
        JSONObject cookieObject = new JSONObject();
        for (Cookie cookie : cookies) {
            if (!StringUtils.isBlank(cookie.getValue())) {
                cookieObject.put(cookie.getName(), cookie.getValue());
            }
        }
        Return.put("errcode", "0");
        Return.put("msg", JSONObject.toJSONString(cookieObject));
//        System.out.println("取全部Cookies JSONObject.toJSONString(cookieObject)：" + JSONObject.toJSONString(cookieObject));
        return Return;
    }
}
