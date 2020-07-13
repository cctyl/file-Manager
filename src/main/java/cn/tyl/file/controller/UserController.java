package cn.tyl.file.controller;


import cn.hutool.core.util.IdUtil;
import cn.tyl.file.commons.R;
import cn.tyl.file.utils.RedisUtils;
import cn.tyl.file.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
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



    @PostMapping("/login")
    public R userLogin(
            HttpServletResponse response, HttpSession session,
            @RequestParam String username,
            @RequestParam String password){

        if (username.equals(uname) &&password.equals(upwd) ){
            //登陆成功
            String simpleUUID = IdUtil.simpleUUID();
            redisUtils.set("admin",simpleUUID);

            Cookie cookie = new Cookie("loginToken",simpleUUID);
            //7天 3600*24*7
            cookie.setMaxAge(604800);
            cookie.setPath(session.getServletContext().getContextPath());

            response.addCookie(cookie);
            return R.ok();
        }else {
            return R.error();
        }


    }

}
