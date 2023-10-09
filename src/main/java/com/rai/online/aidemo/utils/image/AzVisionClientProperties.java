package com.rai.online.aidemo.utils.image;

import com.rai.online.aidemo.utils.AzureAIClientProperties;
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
@ConfigurationProperties(prefix = "client.azure.vision")
public class AzVisionClientProperties extends AzureAIClientProperties {

    @GuardedBy("this")
    private String threshold;
}
