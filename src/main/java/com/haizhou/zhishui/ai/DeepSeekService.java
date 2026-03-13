package com.haizhou.zhishui.ai;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class DeepSeekService {
    @Value("${deepseek.api.key}")
    private String apiKey;

    @Value("${deepseek.api.url}")
    private String apiUrl;

    private final Gson gson = new Gson();

    public String getChatResponse(String message) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(apiUrl);

        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setHeader("Authorization", "Bearer " + apiKey);

        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", "deepseek-chat");

        JsonArray messages = new JsonArray();

        JsonObject systemMessage = new JsonObject();
        systemMessage.addProperty("role", "system");
        systemMessage.addProperty("content", "你是海舟智税云平台的一个辅助智能退税专家，可以根据用户提供的信息与联网搜索政策来进行智能退税，并输出给用户，告诉用户怎么退税更优惠，在回答的过程保证有礼貌，并且不需要多余的标点符号，保持回答答案整洁");
        messages.add(systemMessage);

        JsonObject userMessage = new JsonObject();
        userMessage.addProperty("role", "user");
        userMessage.addProperty("content", message);
        messages.add(userMessage);

        requestBody.add("messages", messages);
        requestBody.addProperty("temperature", 0.7);
        requestBody.addProperty("max_tokens", 1024);

        StringEntity entity = new StringEntity(requestBody.toString(), StandardCharsets.UTF_8);
        httpPost.setEntity(entity);

        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                String responseString = EntityUtils.toString(responseEntity);
                JsonObject responseJson = gson.fromJson(responseString, JsonObject.class);
                JsonArray choices = responseJson.getAsJsonArray("choices");
                if (choices != null && choices.size() > 0) {
                    JsonObject choice = choices.get(0).getAsJsonObject();
                    JsonObject messageObj = choice.getAsJsonObject("message");
                    return messageObj.get("content").getAsString();
                }
            }
        } finally {
            httpClient.close();
        }

        return "抱歉，暂时无法获取回复，请稍后再试。";
    }
}