package com.rai.online.aidemo.services;

//import com.rai.online.aidemo.apis.model.SpeechResponse;
//import com.rai.online.aidemo.apis.model.SpeechRequest;

import com.rai.online.aidemo.model.AiProcessedImageResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.concurrent.ExecutionException;

public interface AzureAIUtilsService {

    String synthesizeSpeech(MultipartFile wavFile); //throws ExecutionException, InterruptedException;
//
//    SpeechResponse getSynthesizedSpeechContent(String endpoint);
    AiProcessedImageResponse processImage(MultipartFile imageFile);

    String getResponseAudioForVoice(String message);
}
