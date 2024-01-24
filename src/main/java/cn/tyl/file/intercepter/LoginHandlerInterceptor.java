package cn.tyl.file.intercepter;

import cn.tyl.file.commons.R;
import cn.tyl.file.utils.AuthUtils;
import cn.tyl.file.utils.CookieUtils;
import cn.tyl.file.utils.ExceptionUtil;

import cn.tyl.file.utils.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Slf4j
public class LoginHandlerInterceptor implements HandlerInterceptor {


    @Autowired
    ResponseUtils responseUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        //从请求中获取token
        String loginToken;
        try {
            loginToken = CookieUtils.getCookieValue(request, "auth");
            boolean checkResult = AuthUtils.checkToken(loginToken);

            if (!checkResult) {
                responseUtils.returnWithJson(response, R.error().data("message", "未登陆，无操作权限"));
                return false;
            } else {
                request.getSession().setAttribute("token",loginToken);
                //已登陆，放行请求
                return true;
            }
        } catch (NullPointerException e) {
            log.debug("未登陆而出现获取不到cookie的异常" + ExceptionUtil.getMessage(e));
            responseUtils.returnWithJson(response, R.error().data("message", "未登陆，无操作权限"));
            return false;

        } catch (Exception e) {
            log.error(ExceptionUtil.getMessage(e));
            responseUtils.returnWithJson(response, R.error().data("message", "未登陆，无操作权限"));
            return false;
        }

    }


}
