package com.idle.rushcutter.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.idle.rushcutter.exception.PathException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class OdsayApiClient {

    @Value("${odsay-api-key}")
    private String apiKey;

    public JsonNode getSubwayPath(String startId, String endId, String sopt) {
        try {
            String url = String.format("https://api.odsay.com/v1/api/subwayPath?" + "apiKey=%s&CID=1000&SID=%s&EID=%s&lang=0&output=json", URLEncoder.encode(apiKey, StandardCharsets.UTF_8), URLEncoder.encode(startId, StandardCharsets.UTF_8), URLEncoder.encode(endId, StandardCharsets.UTF_8));
            if (sopt != null && !sopt.isEmpty()) {
                url += "&Sopt=" + URLEncoder.encode(sopt, StandardCharsets.UTF_8);
            }
            URI uri = new URI(url);

            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());

            if (root.get("result") == null) {
                throw new PathException(root);
            }

            return root.get("result");
        } catch (Exception e) {
            throw new PathException("[ODSay API]: " + e.getMessage(), e);
        }
    }
}