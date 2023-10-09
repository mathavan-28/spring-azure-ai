package com.rai.online.aidemo.utils.genAI;

import com.rai.online.aidemo.utils.AzureAIClientProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Getter
@Setter
@RefreshScope
@Component
@ConfigurationProperties(prefix = "client.azure.language")
public class AzlanguageClientProperties extends AzureAIClientProperties {

}
