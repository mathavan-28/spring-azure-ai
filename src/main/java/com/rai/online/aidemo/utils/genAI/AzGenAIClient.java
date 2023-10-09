package com.rai.online.aidemo.utils.genAI;

import com.rai.online.aidemo.model.AIResponseTypeGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

@Slf4j
@Component
public class AzGenAIClient {

    private static final String API_VERSION = "2022-07-01-preview";

    private final AzlanguageClientProperties azlanguageClientProperties;

    private final Environment env;

    @Resource(name = "sessionScopedResponseBean")
    private AIResponseTypeGenerator sessionScopedResponseBean;

    public AzGenAIClient(AzlanguageClientProperties azlanguageClientProperties, Environment env) {
        this.azlanguageClientProperties = azlanguageClientProperties;
        this.env = env;
    }

    public HttpPost getGenAIRequest() {
        String languageServiceKey = azlanguageClientProperties.getSubscriptionKey();
        String languageServiceEndpoint = azlanguageClientProperties.getEndpoint();

        HttpPost request = null;
        try {
            URIBuilder builder = new URIBuilder(languageServiceEndpoint);
            builder.setParameter("api-version", API_VERSION);

            URI uri = builder.build();
            request = new HttpPost(uri);
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Ocp-Apim-Subscription-Key", languageServiceKey);
//            System.out.println("The Question asked :$ " + questionText);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return request;
    }

    public String processResponse(HttpPost postReq) throws IOException {
        HttpClient httpclient = HttpClients.createDefault();
        HttpResponse response = httpclient.execute(postReq);
        HttpEntity entity = response.getEntity();
        HashMap<String, Object> resultMap = parseEntityResponse(entity);

        if (resultMap != null) {
            resultMap.entrySet().stream()
                    .filter(e -> !e.getKey().startsWith("cs")).forEach(e -> System.out.println(e));
            //                System.out.println(EntityUtils.toString(entity));
        }
        log.info("setResponseForAssist responseType - {}", sessionScopedResponseBean.getVAssistResponseType());
        var responseType = (!ObjectUtils.isEmpty(sessionScopedResponseBean.getVAssistResponseType()))
                ? sessionScopedResponseBean.getVAssistResponseType() : env.getProperty("vassist.response-type");
        log.info("v assist response type in Client {}", responseType);
        if (!ObjectUtils.isEmpty(responseType) && responseType.equalsIgnoreCase("short")) {
            return (String) resultMap.get("shortAnswer");
        } else {
            return (String) resultMap.get("detailedAnswer");
        }
    }

    private HashMap parseEntityResponse(HttpEntity entity) throws IOException {
        HashMap result_map = new HashMap<String, String>();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(entity.getContent()));
        JSONTokener tokener = new JSONTokener(bufferedReader);
        JSONObject json = new JSONObject(tokener);
        JSONArray jsonArray = (JSONArray) json.get("answers");
        JSONObject topAnswerObject = (JSONObject) jsonArray.get(0);
        JSONObject shortAnswerObject = (JSONObject) topAnswerObject.get("answerSpan");
        result_map.put("shortAnswer", shortAnswerObject.getString("text"));
        result_map.put("cs_shortAnswer", shortAnswerObject.get("confidenceScore"));
        result_map.put("detailedAnswer", topAnswerObject.getString("answer"));
        result_map.put("cs_detailedAnswer", shortAnswerObject.get("confidenceScore"));
        return result_map;
    }
}
