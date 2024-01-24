package cn.tyl.file.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "config")
@Component
@Data
public class AppProperties {

    private String uname;
    private String upwd;
    private String key;
}
