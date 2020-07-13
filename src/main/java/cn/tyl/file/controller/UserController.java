package cn.tyl.file.controller;


import cn.hutool.core.util.IdUtil;
import cn.tyl.file.commons.R;
import cn.tyl.file.commons.UserInfo;
import cn.tyl.file.utils.CookieUtils;
import cn.tyl.file.utils.RedisUtils;
import cn.tyl.file.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@RestController
public class UserController {


    @Value("${user.data.uname}")
    String uname;

    @Value("${user.data.upwd}")
    String upwd;

    @Autowired
    RedisUtils redisUtils;


    /**
     * 登陆
     * @param response
     * @param session
     * @param user
     * @return
     */
    @PostMapping("/login")
    public R userLogin(
            HttpServletResponse response, HttpSession session,
            @RequestBody UserInfo user
    ) {

        if (user.getUsername().equals(uname) && user.getPassword().equals(upwd)) {
            //登陆成功
            String simpleUUID = IdUtil.simpleUUID();
            redisUtils.set("admin", simpleUUID);

            Cookie cookie = new Cookie("loginToken", simpleUUID);
            //7天 3600*24*7
            cookie.setMaxAge(604800);
            cookie.setPath(session.getServletContext().getContextPath());

            response.addCookie(cookie);
            return R.ok();
        } else {
            return R.error();
        }


    }

    /**
     * 测试是否已经登陆
     * 如果已经登陆，就能够得到这个数据
     * 如果尚未登陆，哪就不能访问这个方法
     * 根据返回的结果来判断是否登陆
     * @return
     */
    @GetMapping("/who")
    public R testLogin(){


        return R.ok();

    }

}
