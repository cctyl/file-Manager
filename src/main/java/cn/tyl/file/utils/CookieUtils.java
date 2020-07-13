package cn.tyl.file.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class CookieUtils {


    /**
     * 根据Cookie名获取对应的Cookie
     *
     * @param request    HttpServletRequest
     * @param cookieName cookie名称
     * @return 对应cookie，如果不存在则返回null
     * @author zifangsky
     */
    public static Cookie getCookie(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null || cookieName == null || cookieName.equals(""))
            return null;

        for (Cookie c : cookies) {
            if (c.getName().equals(cookieName))
                return  c;
        }
        return null;
    }

    /**
     * 根据Cookie名获取对应的Cookie值
     *
     * @param request    HttpServletRequest
     * @param cookieName cookie名称
     * @return 对应cookie值，如果不存在则返回null
     * @author zifangsky
     */
    public static String getCookieValue(HttpServletRequest request, String cookieName) {
        Cookie cookie = getCookie(request, cookieName);
        if (cookie == null)
            return null;
        else
            return cookie.getValue();
    }

    /**
     * 删除指定Cookie
     *
     * @param response HttpServletResponse
     * @param cookie   待删除cookie
     * @author zifangsky
     */
    public static void delCookie(HttpServletResponse response, Cookie cookie,HttpServletRequest request) {
        if (cookie != null) {
            cookie.setMaxAge(0);
            cookie.setValue(null);
            HttpSession session = request.getSession();
            cookie.setPath(session.getServletContext().getContextPath());

            response.addCookie(cookie);
        }
    }

    /**
     * 根据cookie名删除指定的cookie
     *
     * @param request    HttpServletRequest
     * @param response   HttpServletResponse
     * @param cookieName 待删除cookie名
     * @author zifangsky
     */
    public static void delCookie(HttpServletRequest request, HttpServletResponse response, String cookieName) {
        Cookie c = getCookie(request, cookieName);
        if (c != null && c.getName().equals(cookieName)) {
            delCookie(response, c,request);
        }
    }



}
