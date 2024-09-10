package com.assignment.core.services.impl;

import com.assignment.core.api.UserPostData;

import com.assignment.core.config.DisplayUserpostConfig;


import com.assignment.core.services.DisplayUserpostService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

@Component(service = DisplayUserpostService.class, immediate = true)
@Designate(ocd = DisplayUserpostConfig.class)
public class DisplayUserpostServiceImpl implements DisplayUserpostService {

    private static final Logger LOG = LoggerFactory.getLogger(DisplayUserpostServiceImpl.class);

    private String postApiUrl;

    @Activate
    @Modified
    protected void activate(DisplayUserpostConfig config) {
        this.postApiUrl = config.apiUrl();
        LOG.info("Activated with API URL: {}", postApiUrl);
    }

    @Override
    public List<UserPostData> getPostData() {
        LOG.info("Fetching data from URL: {}", postApiUrl);
        String jsonData = "";
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpGet request = new HttpGet(postApiUrl);
            CloseableHttpResponse response = httpClient.execute(request);
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    jsonData = EntityUtils.toString(entity);
                } else {
                    LOG.warn("No data found at the provided URL.");
                    return List.of(); // Return an empty list if no data is found
                }

        } catch (IOException e) {
            LOG.error("Error fetching data from URL: {}", postApiUrl, e);
            return List.of(); // Return an empty list in case of an error
        }

        // Parse the JSON data into a list of UserPostData
        Gson gson = new Gson();
        Type listType = new TypeToken<List<UserPostData>>() {}.getType();
        List<UserPostData> userPostDataList = gson.fromJson(jsonData, listType);

        if (userPostDataList == null) {
            LOG.warn("Deserialization returned null. The JSON data may be malformed.");
            return List.of(); // Return an empty list if deserialization fails
        }

        LOG.info("Successfully fetched {} records", userPostDataList.size());
        return userPostDataList;
    }
}
