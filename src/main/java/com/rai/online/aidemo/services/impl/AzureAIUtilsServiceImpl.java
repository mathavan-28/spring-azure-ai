package com.rai.online.aidemo.services.impl;

//import com.rai.online.aidemo.apis.model.SpeechRequest;
//import com.rai.online.aidemo.apis.model.SpeechResponse;

import com.azure.ai.textanalytics.TextAnalyticsClient;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.rai.online.aidemo.exceptions.SpringAIDemoException;
import com.rai.online.aidemo.model.AiProcessedImageResponse;
import com.rai.online.aidemo.services.AzureAIUtilsService;
import com.rai.online.aidemo.utils.analyze.AzAnalyseClient;
import com.rai.online.aidemo.utils.genAI.AzGenAIClient;
import com.rai.online.aidemo.utils.image.AzVisionClient;
import com.rai.online.aidemo.utils.speech.AzureSpeechClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;

import static com.rai.online.aidemo.exceptions.SpringAIDemoErrorCode.E2012;

@Slf4j
@Service
public class AzureAIUtilsServiceImpl implements AzureAIUtilsService {

    private final AzureSpeechClient azureSpeechClient;

    private final AzAnalyseClient azAnalyseClient;

    private final AzVisionClient azVisionClient;

    private final AzGenAIClient azGenAIClient;

    public AzureAIUtilsServiceImpl(AzureSpeechClient azureSpeechClient, AzAnalyseClient azAnalyseClient, AzVisionClient azVisionClient, AzGenAIClient azGenAIClient) {
        this.azureSpeechClient = azureSpeechClient;
        this.azAnalyseClient = azAnalyseClient;
        this.azVisionClient = azVisionClient;
        this.azGenAIClient = azGenAIClient;
    }

    @Override
    @Transactional
    public String synthesizeSpeech(MultipartFile wavFile) { //throws ExecutionException, InterruptedException {
        String resultStr = "Hello World!";
        SpeechConfig speechConfig = azureSpeechClient.getSpeechConfig();
        log.info("Azure Utils Service.. speech - {}", speechConfig);
        try {
            resultStr = azureSpeechClient.recognizeTextFromFile(speechConfig, wavFile);
        } catch (InterruptedException e) {
            throw new SpringAIDemoException(E2012, e.getMessage());
        } catch (ExecutionException e) {
            throw new SpringAIDemoException(E2012, e.getMessage());
        }
//        log.info("Azure Utils Service.. speech - {}", speechRequest);
////        User user = buildUserRequest(userRequest);
////        UserEntity userEntity = new UserEntity();
////        convertToEntity(user, userEntity);
//////        validatorService.validateUser(user);

        if (StringUtils.hasText(resultStr)) {
            return getProcessedText(resultStr);
        } else {
            throw new SpringAIDemoException(E2012, "Improper data exists!");
        }
    }
//
//    @Override
//    public SpeechResponse getSynthesizedSpeechContent(String endpoint) {
//        return null;
//    }

    @Override
    @Transactional
    public String getGenAIResponseForVA(MultipartFile wavFile) {
        String resultStr = "Hello World!";
        SpeechConfig speechConfig = azureSpeechClient.getSpeechConfig();
        log.info("Azure Utils Service.. speech - {}", speechConfig);
        try {
            resultStr = azureSpeechClient.recognizeTextFromFile(speechConfig, wavFile);
        } catch (InterruptedException e) {
            throw new SpringAIDemoException(E2012, e.getMessage());
        } catch (ExecutionException e) {
            throw new SpringAIDemoException(E2012, e.getMessage());
        }
        if (StringUtils.hasText(resultStr)) {
            return getResponseFromAI(resultStr);
        } else {
            throw new SpringAIDemoException(E2012, "Improper data exists!");
        }
    }

    private String getResponseFromAI(String query) {
        String resultStr = "Hello World!";
        if (query.toLowerCase().contains("madhavan") || query.toLowerCase().contains("how are you")) {
            return "Oh Shit, you're so annoying.. get lost please..";
        }
        HttpPost postReq = azGenAIClient.getGenAIRequest();
        StringEntity reqEntity = null;
        try {
            reqEntity = new StringEntity(buildGenAIQuery(query));
            postReq.setEntity(reqEntity);
            resultStr = azGenAIClient.processResponse(postReq);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return resultStr;
    }

    private String buildGenAIQuery(String query) {
        System.out.println("The Question asked :$ " + query);
        //"how long it takes to run 100 meters?"
        String json = null;
        try {
            json = readFileAsString("/Users/mathavanv/Documents/preparations/AI/knowledge-base.json");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
//        System.out.println(json);
        return String.format(json, query);
    }

    private String readFileAsString(String file) throws Exception {
        return new String(Files.readAllBytes(Paths.get(file)));
    }

    private String getProcessedText(String resultStr) {
        TextAnalyticsClient config = azAnalyseClient.getTextAnalyticsClient();
        return azAnalyseClient.extractKeyPhrasesExample(config, resultStr);
    }

    @Override
    @Transactional
    public AiProcessedImageResponse processImage(MultipartFile imageFile) {
        HttpPost requestObj = azVisionClient.getVisionClientRequestObject();
        try {
            AiProcessedImageResponse imageResponse = azVisionClient.processImage(requestObj, imageFile);
            return imageResponse;
        } catch (IOException e) {
            log.error("Unable to get response from Client {}", e.getMessage());
            return null;
        }
    }

    @Override
    @Transactional
    public String getResponseAudioForVoice(String keyword, boolean forGenAI) {
        byte[] audioBytes = null;
//        File audFile=null;
        String audFile = null;
        SpeechConfig speechConfig = azureSpeechClient.getSpeechConfig();
        log.info("Azure Utils Service.. speech - {}", speechConfig);
        try {
            audFile = azureSpeechClient.getResponseAudio(speechConfig, keyword, forGenAI);
//            FileUtils.writeByteArrayToFile(new File("/tmp/sampleau.wav"), audioBytes);
        } catch (IOException e) {
            throw new SpringAIDemoException(E2012, e.getMessage());
        }
        return audFile;
        //return new File("/tmp/sampleau.wav");
    }
}
