package com.rai.online.aidemo.utils.image;

import com.rai.online.aidemo.model.AiProcessedImageResponse;
import com.rai.online.aidemo.utils.AIDemoColorCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class AzVisionClient {

    private static String COLOR_CODE = null;

    private final AzVisionClientProperties azVisionClientProperties;

    protected AzVisionClient(AzVisionClientProperties azVisionClientProperties) {

        this.azVisionClientProperties = azVisionClientProperties;
//        TextAnalyticsClient client = sample.getTextAnalyticsClient(KEY, ENDPOINT);

    }

    public HttpPost getVisionClientRequestObject() {
        HttpPost requestObj = null;
        try {
            URIBuilder builder = new URIBuilder(azVisionClientProperties.getEndpoint());
            builder.setParameter("handwriting", "false");
            builder.setParameter("mode", "Printed");
//                builder.setParameter("detectOrientation", String.valueOf(true));
            URI uri = builder.build();
            requestObj = new HttpPost(uri);
            //   request.setHeader("Content-Type", "application/octet-stream");
            requestObj.setHeader("Ocp-Apim-Subscription-Key", "4dc83ea2d03848d5a4cab8ba35bf8116");
        } catch (Exception e) {
            // Display error message.
            System.out.println(e.getMessage());
        }
        return requestObj;
    }

    public AiProcessedImageResponse processImage(HttpPost requestObj, MultipartFile imageFile) throws IOException {
        File tempFile = null;
        try {
            tempFile = File.createTempFile("prefix-", "-suffix");
            imageFile.transferTo(tempFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

//        String imageFilePath = "src/main/resources/ocr_form_filled.png";
//                File localImageFile = new File(imageFilePath);
//                MultipartEntityBuilder mbuilder = MultipartEntityBuilder.create();
//                mbuilder.addBinaryBody("image", localImageFile);
//                HttpEntity reqEntity = mbuilder.build();

        File localImageFile = new File(tempFile.getPath());
        InputStreamEntity reqEntity = null;
        JSONObject json = null;
        CloseableHttpClient httpclient = null;
        CloseableHttpResponse textResponse = null;
        try {
            reqEntity = new InputStreamEntity(
                    new FileInputStream(localImageFile), -1, ContentType.APPLICATION_OCTET_STREAM);
            reqEntity.setChunked(true);

            // Request body.
            requestObj.setEntity(reqEntity);
            httpclient = HttpClients.createDefault();

            textResponse = httpclient.execute(requestObj);
            json = getTextResponse(textResponse);
            httpclient.close();
        } catch (IOException e) {
            textResponse.close();
            throw new RuntimeException(e);
        } finally {
            httpclient.close();
        }
        return convertToResponseModel(json);
    }

    private JSONObject handleResponseDefault(HttpEntity entity, String sourceLocation) throws Exception {
        JSONObject json = null;
        if (entity != null) {
            String jsonString = EntityUtils.toString(entity);
            json = new JSONObject(jsonString);
            json.put("imageLocation", sourceLocation);
        }
        return json;
    }

    private void addParameters(URIBuilder builder, List<String> visualFeatures, List<String> details) {
        addParameters(builder, "visualFeatures", visualFeatures);
        addParameters(builder, "details", details);
    }

    private void addParameters(URIBuilder builder, String name, List<String> list) {
        if (list != null) {
            StringBuffer sb = new StringBuffer();
            for (String item : list) {
                sb.append(item).append(",");
            }
            if (sb.length() > 0) {
                sb.setLength(sb.length() - 1);
            }
            builder.setParameter(name, sb.toString());
        }
    }

    private JSONObject getTextResponse(HttpResponse textResponse) {
        JSONObject json = null;
        try {
            if (textResponse.getStatusLine().getStatusCode() != 202) {
                // Format and display the JSON error message.
                HttpEntity entity = textResponse.getEntity();
                String jsonString = EntityUtils.toString(entity);
                json = new JSONObject(jsonString);
                System.out.println("Error:\n");
                System.out.println(json.toString(2));
                return json;
            }

            String operationLocation = null;

            // The 'Operation-Location' in the response contains the URI to retrieve the
            // recognized text.
            Header[] responseHeaders = textResponse.getAllHeaders();
            for (Header header : responseHeaders) {
                if (header.getName().equals("Operation-Location")) {
                    // This string is the URI where you can get the text recognition operation result.
                    operationLocation = header.getValue();
                    break;
                }
            }

            // NOTE: The response may not be immediately available. Handwriting recognition
            // is an async operation that can take a variable amount of time depending on the
            // length of the text you want to recognize. You may need to wait or retry this operation.
            System.out.println("\nHandwritten text submitted. Waiting 3 seconds to retrieve the recognized text.\n");
            Thread.sleep(2000);
            // Execute the second REST API call and get the response.
            json = getTextOperations(operationLocation);
        } catch (Exception e) {
            // Display error message.
            System.out.println(e.getMessage());
        }

        return json;
    }

    public JSONObject getTextOperations(String resultUrl) {
        return get(resultUrl);
    }

    /**
     * Comment get method
     */
    private JSONObject get(String url) {
        CloseableHttpClient httpclient = null;
        CloseableHttpResponse resultResponse = null;
        JSONObject json = null;

        try {
            HttpGet resultRequest = new HttpGet(url);
            resultRequest.setHeader("Ocp-Apim-Subscription-Key", "4dc83ea2d03848d5a4cab8ba35bf8116");
            httpclient = HttpClients.createDefault();
            resultResponse = httpclient.execute(resultRequest);
            HttpEntity responseEntity = resultResponse.getEntity();

            json = handleResponseDefault(responseEntity, null);
        } catch (Exception e) {
            try {
                resultResponse.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            // Display error message.
            System.out.println(e.getMessage());
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return json;
    }

    private AiProcessedImageResponse convertToResponseModel(JSONObject json) {
        AiProcessedImageResponse aiProcessedImageResponse = new AiProcessedImageResponse();
        ArrayList<String> arrayList = new ArrayList<>();
        Map<String, String> color_map = new HashMap<String, String>();
        if (json != null) {
            JSONObject gson = (JSONObject) json.get("analyzeResult");
            JSONObject readResults = (JSONObject) gson.getJSONArray("readResults").get(0);
            JSONArray arr = readResults.getJSONArray("lines");

//            JSONObject gson = (JSONObject) json.get("recognitionResult");
//            JSONArray arr = gson.getJSONArray("lines");
            var skipElements = true;
            for (int i = 0; i < arr.length(); i++) {
                JSONObject gson1 = arr.getJSONObject(i);
//                JSONObject appearance = (JSONObject) gson1.get("appearance");
//                JSONObject line_attributes =  (JSONObject) appearance.get("style");
//                BigDecimal confidence_score = (BigDecimal) line_attributes.get("confidence");

                JSONArray wordsArray = gson1.getJSONArray("words");
                List<BigDecimal> tempList = new ArrayList<>();
                for (var obj : wordsArray) {
                    JSONObject word = (JSONObject) obj;
                    tempList.add((BigDecimal) word.get("confidence"));
                }
                BigDecimal confidence_score = tempList.stream()
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(new BigDecimal(tempList.size()), 3, RoundingMode.HALF_EVEN);

                COLOR_CODE = confidence_score.compareTo(new BigDecimal(azVisionClientProperties.getThreshold())) > 0 ? AIDemoColorCode.COLOR_GREEN.getValue() : AIDemoColorCode.COLOR_RED.getValue();
                var lineText = (String) gson1.get("text");
                if(confidence_score.compareTo(new BigDecimal(azVisionClientProperties.getThreshold())) > 0)
                    System.out.println(lineText+": "+confidence_score);
                if (lineText.equals("Creditor name")) {
                    skipElements = false;
                }

                if (!skipElements) {
                    lineText = lineText.replaceAll("\\.", "");
                    if (lineText != null && !lineText.trim().isEmpty()) {
                        arrayList.add(lineText);
                        color_map.put(lineText, COLOR_CODE);
                    }
                }
            }
//            System.out.println("text content: ++++++++++");
            System.out.println(arrayList);
            for (int i = 0; i < arrayList.size(); i = i + 2) {
                var key = arrayList.get(i).trim();
                var value = arrayList.get(i + 1).replaceAll(":", "").trim();
                if (key.equalsIgnoreCase("Creditor name")) {
                    //                    System.out.println("Creditor name: " + arrayList.get(i + 1).trim());
                    aiProcessedImageResponse.setCreditorName(value);
                    aiProcessedImageResponse.setCreditorNameColor(color_map.get(arrayList.get(i + 1)));
                } else if (key.equalsIgnoreCase("Creditor address")) {
                    aiProcessedImageResponse.setCreditorAddress(value);
                    aiProcessedImageResponse.setCreditorAddressColor(color_map.get(arrayList.get(i + 1)));
                    //                    System.out.println("Creditor address: " + arrayList.get(i+1).trim());
                } else if (key.equalsIgnoreCase("Creditor postcode")) {
                    aiProcessedImageResponse.setCreditorPostalCode(value);
                    aiProcessedImageResponse.setCreditorPostalCodeColor(color_map.get(arrayList.get(i + 1)));
                    //                    System.out.println("Creditor postcode: " + arrayList.get(i+1).trim());
                } else if (key.contains("Creditor town")) {
                    var val = arrayList.get(i + 2).trim();
                    aiProcessedImageResponse.setCreditorCity(arrayList.get(i + 2).trim());
                    aiProcessedImageResponse.setCreditorCityColor(color_map.get(arrayList.get(i + 2)));
                    //                    System.out.println("Creditor address: " + );
                    i++;
                } else if (key.contains("Creditor country")) {
                    aiProcessedImageResponse.setCreditorCountry(value);
                    aiProcessedImageResponse.setCreditorCountryColor(color_map.get(arrayList.get(i + 1)));
                    //                    System.out.println("Creditor country: " + arrayList.get(i+1).trim());
                } else if (key.contains("Creditor identifier")) {
                    aiProcessedImageResponse.setCreditorIdentifier(key.substring(key.indexOf(":") + 1).trim());
                    aiProcessedImageResponse.setCreditorIdentifierColor(color_map.get(arrayList.get(i)));
                    //                    System.out.println("Creditor identifier: " + key.substring(key.indexOf(":") + 1).trim());
                    i--;
                } else if (key.contains("Mandate reference")) {
                    aiProcessedImageResponse.setMandateReference(value);
                    aiProcessedImageResponse.setMandateReferenceColor(color_map.get(arrayList.get(i + 1)));
                    //                    System.out.println("Mandate reference: " + arrayList.get(i+1).trim());
                }
            }
        }
        return aiProcessedImageResponse;
    }
}
