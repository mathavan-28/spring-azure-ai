package com.rai.online.aidemo.utils.speech;

import com.rai.online.aidemo.utils.AzureAIClientProperties;
import com.rai.online.aidemo.utils.ClientProperties;
import lombok.Getter;
import lombok.Setter;
import net.jcip.annotations.GuardedBy;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Getter
@Setter
@RefreshScope
@Component
@ConfigurationProperties(prefix = "client.azure.speech")
public class AzSpeechClientProperties extends AzureAIClientProperties {

}
