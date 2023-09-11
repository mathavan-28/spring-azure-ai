package com.rai.online.aidemo.services.impl;

//import com.rai.online.aidemo.apis.model.SpeechRequest;
//import com.rai.online.aidemo.apis.model.SpeechResponse;
import com.azure.ai.textanalytics.TextAnalyticsClient;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.rai.online.aidemo.exceptions.SpringAIDemoException;
import com.rai.online.aidemo.model.AiProcessedImageResponse;
import com.rai.online.aidemo.services.AzureAIUtilsService;
import com.rai.online.aidemo.utils.analyze.AzAnalyseClient;
import com.rai.online.aidemo.utils.image.AzVisionClient;
import com.rai.online.aidemo.utils.speech.AzureSpeechClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.http.client.methods.HttpPost;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static com.rai.online.aidemo.exceptions.SpringAIDemoErrorCode.E2012;

@Slf4j
@Service
public class AzureAIUtilsServiceImpl implements AzureAIUtilsService {

    private final AzureSpeechClient azureSpeechClient;

    private final AzAnalyseClient azAnalyseClient;

    private final AzVisionClient azVisionClient;

    public AzureAIUtilsServiceImpl(AzureSpeechClient azureSpeechClient, AzAnalyseClient azAnalyseClient, AzVisionClient azVisionClient) {
        this.azureSpeechClient = azureSpeechClient;
        this.azAnalyseClient = azAnalyseClient;
        this.azVisionClient = azVisionClient;
    }

    @Override
    @Transactional
    public String synthesizeSpeech(MultipartFile wavFile) { //throws ExecutionException, InterruptedException {
        String resultStr = "Hello World!";
        SpeechConfig speechConfig = azureSpeechClient.getSpeechConfig();
        log.info("Azure Utils Service.. speech - {}", speechConfig);
        try {
            resultStr = azureSpeechClient.recognizeTextFromFile(speechConfig,wavFile);
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
            return  getProcessedText(resultStr);
        } else {
            throw new SpringAIDemoException(E2012, "User already exists!");
        }
    }
//
//    @Override
//    public SpeechResponse getSynthesizedSpeechContent(String endpoint) {
//        return null;
//    }

    private String getProcessedText(String resultStr) {
        TextAnalyticsClient config = azAnalyseClient.getTextAnalyticsClient();
        return azAnalyseClient.extractKeyPhrasesExample(config, resultStr);
    }


    @Override
    @Transactional
    public AiProcessedImageResponse processImage(MultipartFile imageFile){
        HttpPost requestObj = azVisionClient.getVisionClientRequestObject();
        try {
            AiProcessedImageResponse imageResponse = azVisionClient.processImage(requestObj, imageFile);
            return imageResponse;
        } catch (IOException e) {
            log.error("Unable to get response from Client {}",e.getMessage());
            return null;
        }
    }


    @Override
    @Transactional
    public String getResponseAudioForVoice(String keyword){
        byte[] audioBytes=null;
//        File audFile=null;
        String audFile= null;
        SpeechConfig speechConfig = azureSpeechClient.getSpeechConfig();
        log.info("Azure Utils Service.. speech - {}", speechConfig);
        try {
            audFile = azureSpeechClient.getResponseAudio(speechConfig,keyword);
//            FileUtils.writeByteArrayToFile(new File("/tmp/sampleau.wav"), audioBytes);
        } catch (IOException e ) {
            throw new SpringAIDemoException(E2012, e.getMessage());
        }
        return audFile;
        //return new File("/tmp/sampleau.wav");
    }
}
