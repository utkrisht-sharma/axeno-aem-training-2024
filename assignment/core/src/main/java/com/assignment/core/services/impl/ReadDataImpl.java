package com.assignment.core.services.impl;


import com.assignment.core.models.PostData;
import com.assignment.core.services.ReadData;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Component(service = ReadData.class, immediate = true)
public class ReadDataImpl implements ReadData {
    Logger logger = LoggerFactory.getLogger(ReadDataImpl.class);

    /**
     *{@inheritDoc}
     */
    @Override
    public List<PostData> readDataFromApi() {
        try {
            logger.info("Reading of data from API started.");
            HttpClient httpClient = HttpClient.newBuilder().build();
            HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create("https://jsonplaceholder.typicode.com/posts")).build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            ObjectMapper mapper = new ObjectMapper();
            List<PostData> data = mapper.readValue(response.body(), new TypeReference<List<PostData>>() {
            });
            return data;
        } catch (IOException e) {
            logger.error("IOException occurs. ");
        } catch (InterruptedException e) {
            logger.error("Interrupted Exception occurs. ");
        }
        return new ArrayList<>();
    }
}
