package cn.tyl.file.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

@Component
@Slf4j
public class ResponseUtils {
    @Autowired
    ObjectMapper objectMapper;

    public  void returnWithJson(HttpServletResponse response, Object data){


        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        PrintWriter out = null ;
        try{
            String string = objectMapper.writeValueAsString(data);
            out = response.getWriter();
            out.append(string);

        }
        catch (Exception e){
          log.error(ExceptionUtil.getMessage(e));

        }
    }
}
