package com.rai.online.aidemo.utils.analyze;

import com.azure.ai.textanalytics.TextAnalyticsClient;
import com.azure.ai.textanalytics.TextAnalyticsClientBuilder;
import com.azure.ai.textanalytics.models.CategorizedEntity;
import com.azure.core.credential.AzureKeyCredential;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AzAnalyseClient {

    private final AzAnalyseClientProperties azAnalyseClientProperties;

//    private static String KEY = "5ca10e361f524951ad91952b00ed14ef";
//
//    private static String ENDPOINT = "https://test-ai-language-service.cognitiveservices.azure.com/";

    protected AzAnalyseClient(AzAnalyseClientProperties azAnalyseClientProperties) {

        this.azAnalyseClientProperties = azAnalyseClientProperties;
//        TextAnalyticsClient client = sample.getTextAnalyticsClient(KEY, ENDPOINT);

    }

    public TextAnalyticsClient getTextAnalyticsClient() {
        String analyseServiceKey = azAnalyseClientProperties.getSubscriptionKey();
        String analyseServiceEndpoint = azAnalyseClientProperties.getEndpoint();

        return new TextAnalyticsClientBuilder()
                .credential(new AzureKeyCredential(analyseServiceKey))
                .endpoint(analyseServiceEndpoint)
                .buildClient();
    }

    public String extractKeyPhrasesExample(TextAnalyticsClient client, String text) {
        String extractedText = "";
        // The text to be analyzed
        if (text == null)
            text = "i want to create a new mandate for my account";

        System.out.printf("Recognized phrases: %n");
        for (String keyPhrase : client.extractKeyPhrases(text)) {
            System.out.printf("%s%n", keyPhrase);
            extractedText = keyPhrase;
        }
        for (CategorizedEntity entityphrase : client.recognizeEntities(text)) {
            System.out.printf("%s%n", entityphrase.getText());
            System.out.printf("%s%n", entityphrase.getSubcategory());
            System.out.printf("%s%n", entityphrase.toString());
        }
        return extractedText;
    }
}
