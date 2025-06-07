package com.andriawan.andresource.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
    name = "jwt",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer")
@SecurityScheme(name = "basicAuth", type = SecuritySchemeType.HTTP, scheme = "basic")
public class Swagger {

  @Autowired GitInfo gitInfo;

  @Value("${spring.application.name}")
  String appName;

  @Value("${app.description}")
  String appDesc;

  @Value("${app.summary}")
  String appSummary;

  @Bean
  public OpenAPI custom() {
    Server currentServer = new Server();
    currentServer.url("/");
    currentServer.setDescription("current");
    String description =
        String.format(
            "%s. Last Update: %s. Last commit id: %s",
            appDesc, gitInfo.lastUpdate, gitInfo.gitCommitHash);
    Info info =
        new Info()
            .version(gitInfo.getVersion())
            .title(appName)
            .summary(appSummary)
            .extensions(Map.of("test", "string"))
            .description(description);
    return new OpenAPI().addServersItem(currentServer).info(info);
  }
}
