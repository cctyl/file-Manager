package cn.tyl.file.utils;

import cn.tyl.file.config.AppProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class AuthUtils {


    @Autowired
    private AppProperties appProperties;
    public static final long EXPIRE = 1000 * 60 * 60 * 12;

    public static String key;

    @PostConstruct
    public void init() {
        key = appProperties.getKey();
    }


    /**
     * 生成jwt key
     *
     * @param username
     * @return
     */
    public static String generate(String username) {
        String token = Jwts.builder()
                .setHeaderParam("typ", "JWT")   //设置规则为JWT
                .setHeaderParam("alg", "HS256")
                .setSubject("login")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE))   //设置过期时间，超过这个时间就过期了
                .claim("username", username)        //设置用户昵称
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();

        return token;
    }

    /**
     * 判断token是否存在与有效
     *
     * @param jwtToken
     * @return
     */
    public static boolean checkToken(String jwtToken) {
        if (StringUtils.isEmpty(jwtToken)) return false;
        try {
            Jwts.parser().setSigningKey(key).parseClaimsJws(jwtToken);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static String getUsername(String jwtToken) {
        if (StringUtils.isEmpty(jwtToken))
            throw  new RuntimeException("empty");
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(key).parseClaimsJws(jwtToken);
        Claims claims = claimsJws.getBody();
        return (String) claims.get("username");
    }
}
