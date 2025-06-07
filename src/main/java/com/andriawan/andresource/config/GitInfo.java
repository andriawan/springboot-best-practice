package com.andriawan.andresource.config;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:git.properties")
public class GitInfo {
  @Value("${git.commit.id.abbrev}")
  String gitCommitHash;

  @Value("${git.closest.tag.name}")
  String tagVersion;

  @Value("${git.build.version}")
  String buildVersion;

  @Value("${git.commit.time}")
  String lastUpdate;

  public String getVersion() {
    return Optional.of(tagVersion)
        .filter(tag -> !tag.isBlank())
        .or(() -> Optional.of(buildVersion))
        .filter(buildVersion -> !buildVersion.isBlank())
        .orElse(gitCommitHash);
  }
}
