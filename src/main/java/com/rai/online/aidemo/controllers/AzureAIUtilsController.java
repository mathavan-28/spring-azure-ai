package com.rai.online.aidemo.controllers;

//import com.rai.online.aidemo.apis.model.SpeechResponse;

import com.rai.online.aidemo.model.AIResponseTypeGenerator;
import com.rai.online.aidemo.model.AiProcessedImageResponse;
import com.rai.online.aidemo.model.DemoMessage;
import com.rai.online.aidemo.services.AzureAIUtilsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

@Slf4j
@RestController
public class AzureAIUtilsController {

    private AzureAIUtilsService azureAIUtilsService;

    @Resource(name = "sessionScopedResponseBean")
    private AIResponseTypeGenerator sessionScopedResponseBean;

    public AzureAIUtilsController(AzureAIUtilsService azureAIUtilsService) {
        this.azureAIUtilsService = azureAIUtilsService;
    }

//    @Override
//    public ResponseEntity<SpeechResponse> getRecordedSpeechByEndpoint(String endpointUrl) {
//        return SpringAiApi.super.getRecordedSpeechByEndpoint(endpointUrl);
//    }

    @PostMapping(value = "/spring-ai/speech-synthesize", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DemoMessage> uploadFile(@RequestParam("file") MultipartFile file) {
        log.info("user speech content - {}", file);
        String resultedSpeech = azureAIUtilsService.synthesizeSpeech(file);
        DemoMessage messageResponse = new DemoMessage();
        messageResponse.setAppName("Spring AI");
        messageResponse.setMessage(resultedSpeech);
        String audBytes = azureAIUtilsService.getResponseAudioForVoice(resultedSpeech, false);
        messageResponse.setAudioFile(audBytes);
//        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
//                .path("/download-file/")
//                .path(fileName)
//                .toUriString();
//        return new FileResponse(fileName, fileDownloadUri,
//                file.getContentType(), file.getSize());
        return new ResponseEntity<>(messageResponse, HttpStatus.CREATED);
    }

    @PostMapping(value = "/spring-ai/virtual-assist", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DemoMessage> genAIResponse(@RequestParam("file") MultipartFile file) {
        log.info("user speech content - {}", file);
        String resultedSpeech = azureAIUtilsService.getGenAIResponseForVA(file);
        DemoMessage messageResponse = new DemoMessage();
        messageResponse.setAppName("Spring AI");
        messageResponse.setMessage(resultedSpeech);
        String audBytes = azureAIUtilsService.getResponseAudioForVoice(resultedSpeech, true);
        messageResponse.setAudioFile(audBytes);
//        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
//                .path("/download-file/")
//                .path(fileName)
//                .toUriString();
//        return new FileResponse(fileName, fileDownloadUri,
//                file.getContentType(), file.getSize());
        return new ResponseEntity<>(messageResponse, HttpStatus.CREATED);
    }

//    @Override
//    public ResponseEntity<SpeechResponse> recordSpeech() {
//                log.info("user speech content - {}", speechRequest);
//     //   log.info("user speech content - {}", speechRequest.getSpeechContent());
////        User userResponse = userService.saveUser(userRequest);
////        log.info("User created!..");
////        return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
//        return SpringAiApi.super.recordSpeech(new SpeechRequest());
//    }

    @PostMapping(value = "/spring-ai/process-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AiProcessedImageResponse> uploadImage(@RequestParam("imageFile") MultipartFile file) {
        log.info("user image content - {}", file);
        AiProcessedImageResponse imageResponse = azureAIUtilsService.processImage(file);
        return new ResponseEntity<>(imageResponse, HttpStatus.CREATED);
    }

//    @GetMapping(value = "/spring-ai/ai-response")
//    public ResponseEntity<AIResponseTypeGenerator> setResponseForAssist(@RequestParam(value = "responseFormat") String responseFormat) {
//        log.info("input responseType - {}", responseFormat);
//        sessionScopedResponseBean.setVAssistResponseType(responseFormat);
//        AIResponseTypeGenerator aiResponseTypeGenerator = new AIResponseTypeGenerator();
//        aiResponseTypeGenerator.setVAssistResponseType(sessionScopedResponseBean.getVAssistResponseType());
//        log.info("setResponseForAssist responseType - {}", aiResponseTypeGenerator.getVAssistResponseType());
//        return new ResponseEntity<>(aiResponseTypeGenerator, HttpStatus.OK);
//    }

    @GetMapping("/spring-ai/ai-response")
    public ResponseEntity<DemoMessage> createMessage(@RequestParam(value = "responseFormat") String responseFormat) {
        log.info("called from angular2!..." + responseFormat);
        sessionScopedResponseBean.setVAssistResponseType(responseFormat);
        DemoMessage demoMessage = new DemoMessage("AIDemo", "Congrats ! Response type has changed !", null);
        return new ResponseEntity<>(demoMessage, HttpStatus.OK);
    }
}
