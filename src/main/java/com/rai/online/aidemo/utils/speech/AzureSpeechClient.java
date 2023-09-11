package com.rai.online.aidemo.utils.speech;

import com.microsoft.cognitiveservices.speech.CancellationDetails;
import com.microsoft.cognitiveservices.speech.CancellationReason;
import com.microsoft.cognitiveservices.speech.ResultReason;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechRecognitionResult;
import com.microsoft.cognitiveservices.speech.SpeechRecognizer;
import com.microsoft.cognitiveservices.speech.SpeechSynthesisCancellationDetails;
import com.microsoft.cognitiveservices.speech.SpeechSynthesisResult;
import com.microsoft.cognitiveservices.speech.SpeechSynthesizer;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.net.URI;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Slf4j
@Component
public class AzureSpeechClient {

    private final AzSpeechClientProperties azSpeechClientProperties;

    private final Environment env;

    protected AzureSpeechClient(AzSpeechClientProperties azSpeechClientProperties, Environment env) {

        this.azSpeechClientProperties = azSpeechClientProperties;

        this.env = env;
    }

    public SpeechConfig getSpeechConfig(){
        String speechKey = azSpeechClientProperties.getSubscriptionKey();
        String speechRegion = azSpeechClientProperties.getRegion();
        SpeechConfig speechConfig = SpeechConfig.fromSubscription(speechKey, speechRegion);
        SpeechConfig speechConfig1 = SpeechConfig.fromEndpoint(URI.create("https://test-speech-072023.cognitiveservices.azure.com/sts/v1.0/issuetoken"),speechKey);
        speechConfig.setSpeechRecognitionLanguage("en-US");

        speechConfig1.setSpeechRecognitionLanguage("en-US");
        return speechConfig;
    }

    public String recognizeTextFromFile(SpeechConfig speechConfig, MultipartFile wavFile) throws InterruptedException, ExecutionException {
//        AudioConfig audioConfig = AudioConfig.fromWavFileInput("src/main/resources/demo1.wav");
        String resultedSpeech = null;
        File tempFile = null;
        try {
            tempFile = File.createTempFile("prefix-", "-suffix");
            wavFile.transferTo(tempFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        AudioConfig audioConfig = AudioConfig.fromWavFileInput(tempFile.getPath());
        //   AudioConfig audioConfig = AudioConfig.fromDefaultMicrophoneInput();
        SpeechRecognizer speechRecognizer = new SpeechRecognizer(speechConfig, audioConfig);

        System.out.println("Speak into your microphone.");
        Future<SpeechRecognitionResult> task = speechRecognizer.recognizeOnceAsync();
        SpeechRecognitionResult speechRecognitionResult = task.get();

        if (speechRecognitionResult.getReason() == ResultReason.RecognizedSpeech) {
            resultedSpeech = speechRecognitionResult.getText();
            System.out.println("RECOGNIZED: Text=" + resultedSpeech);
        }
        else if (speechRecognitionResult.getReason() == ResultReason.NoMatch) {
            System.out.println("NOMATCH: Speech could not be recognized.");
        }
        else if (speechRecognitionResult.getReason() == ResultReason.Canceled) {
            CancellationDetails cancellation = CancellationDetails.fromResult(speechRecognitionResult);
            System.out.println("CANCELED: Reason=" + cancellation.getReason());

            if (cancellation.getReason() == CancellationReason.Error) {
                System.out.println("CANCELED: ErrorCode=" + cancellation.getErrorCode());
                System.out.println("CANCELED: ErrorDetails=" + cancellation.getErrorDetails());
                System.out.println("CANCELED: Did you set the speech resource key and region values?");
            }
        }
        return resultedSpeech;

    }

    public String  getResponseAudio(SpeechConfig speechConfig, String keyword) throws IOException {
        speechConfig.setSpeechSynthesisVoiceName("en-US-JennyNeural");
        AudioConfig audioConfig = AudioConfig.fromWavFileOutput("src/main/resources/aiSpeech.wav");
        SpeechSynthesizer speechSynthesizer = new SpeechSynthesizer(speechConfig,audioConfig);

        // Get text from the console and synthesize to the default speaker.
//        System.out.println("Enter some text that you want to speak >");
//        String text = new Scanner(System.in).nextLine();
        String text= "Perfect. Here we go";
        var keywordsList = env.getProperty("speech.keyword-list");
//        List keywordsList = List.of("mandate","mandates","statement","statements","international payments","transaction","transactions");

        if (keyword.isEmpty() || !keywordsList.contains(keyword.toLowerCase()))
        {
            text= "Sorry, Please try again!";
        }

        SpeechSynthesisResult speechSynthesisResult = null;

        try {
            speechSynthesisResult = speechSynthesizer.SpeakTextAsync(text).get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }

        if (speechSynthesisResult.getReason() == ResultReason.SynthesizingAudioCompleted) {
            System.out.println("Speech synthesized to speaker for text [" + text + "]");

        }
        else if (speechSynthesisResult.getReason() == ResultReason.Canceled) {
            SpeechSynthesisCancellationDetails cancellation = SpeechSynthesisCancellationDetails.fromResult(speechSynthesisResult);
            System.out.println("CANCELED: Reason=" + cancellation.getReason());

            if (cancellation.getReason() == CancellationReason.Error) {
                System.out.println("CANCELED: ErrorCode=" + cancellation.getErrorCode());
                System.out.println("CANCELED: ErrorDetails=" + cancellation.getErrorDetails());
                System.out.println("CANCELED: Did you set the speech resource key and region values?");
            }
        }
        File file = new File("src/main/resources/aiSpeech.wav");
        byte[] bytes = Files.readAllBytes(file.toPath());
        File f = new File("src/main/resources/aiSpeech.wav");
        String encoded = Base64.getEncoder().encodeToString(bytes);

////        return new File("src/main/resources/aiSpeech.wav");
//        InputStream in = getClass()
//                .getResourceAsStream("src/main/resources/aiSpeech.wav");

        return encoded;


    }
}
