package cn.tyl.file.controller;


import cn.tyl.file.commons.R;
import cn.tyl.file.commons.UserInfo;
import cn.tyl.file.config.AppProperties;
import cn.tyl.file.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@RestController
@RequiredArgsConstructor
public class UserController {



    private final AppProperties appProperties;
    private final AuthUtils authUtils;

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

        if (user.getUsername().equals(appProperties.getUname()) && user.getPassword().equals(appProperties.getUpwd())) {
            //登陆成功
            Cookie cookie = new Cookie("auth", AuthUtils.generate(appProperties.getUname()));
            cookie.setMaxAge(43200);
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
    public R testLogin(HttpServletRequest request){
        return R.ok().data("username",AuthUtils.getUsername((String) request.getSession().getAttribute("token")));
    }

}
