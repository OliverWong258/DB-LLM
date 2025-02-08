package com.example.demo.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.demo.entity.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.ArrayList;
import java.util.List;
//import java.util.Map;

@Service
/**
 * 与python后端交互，提供针对向量数据库的插入和查询操作
 */
public class MilvusClientService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String insertServiceUrl;
    private final String searchServiceUrl;

    public MilvusClientService(
            @Value("${milvus.python.insert.url:http://localhost:8000/milvus/insert}") String insertServiceUrl,
            @Value("${milvus.python.search.url:http://localhost:8000/milvus/search}") String searchServiceUrl
    ) {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
        this.insertServiceUrl = insertServiceUrl;
        this.searchServiceUrl = searchServiceUrl;
    }

    /**
     * 插入文本到Milvus
     *
     * @param collectionName 集合名称
     * @param textsToInsert  List<TextToInsert>
     */
    public void insertTexts(String collectionName, List<TextToInsert> textsToInsert) {
        try {
            // 创建InsertRequest对象
            InsertRequest insertRequest = new InsertRequest(collectionName, textsToInsert);

            // 设置HTTP头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 将InsertRequest对象转换为JSON
            String jsonBody = objectMapper.writeValueAsString(insertRequest);

            // 创建HTTP请求实体
            HttpEntity<String> requestEntity = new HttpEntity<>(jsonBody, headers);

            // 发送POST请求
            //System.out.print("RequestEntity");
            //System.out.println(requestEntity);
            ResponseEntity<String> response = restTemplate.postForEntity(insertServiceUrl, requestEntity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                System.out.println("数据成功插入Milvus。");
            } else {
                System.err.println("插入失败，状态码：" + response.getStatusCode());
            }
        } catch (Exception e) {
            System.err.println("插入过程中发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 在Milvus中搜索相似文本
     *
     * @param collectionName 集合名称
     * @param queryText      查询文本
     * @param topK           返回的结果数量
     * @return List<SearchResult>
     */
    @SuppressWarnings("null")
    public List<SearchResult> searchTexts(String collectionName, String queryText, int topK) {
        List<SearchResult> searchResults = new ArrayList<>();
        try {
            // 创建SearchRequest对象
            SearchRequest searchRequest = new SearchRequest(collectionName, queryText, topK);

            // 设置HTTP头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 将SearchRequest对象转换为JSON
            String jsonBody = objectMapper.writeValueAsString(searchRequest);

            // 创建HTTP请求实体
            HttpEntity<String> requestEntity = new HttpEntity<>(jsonBody, headers);

            // 发送POST请求
            ResponseEntity<SearchResponse> response = restTemplate.postForEntity(searchServiceUrl, requestEntity, SearchResponse.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                searchResults = response.getBody().getResults();
                System.out.println("搜索成功，返回结果数量：" + searchResults.size());
            } else {
                System.err.println("搜索失败，状态码：" + response.getStatusCode());
            }
        } catch (Exception e) {
            System.err.println("搜索过程中发生错误: " + e.getMessage());
            e.printStackTrace();
        }
        return searchResults;
    }
}