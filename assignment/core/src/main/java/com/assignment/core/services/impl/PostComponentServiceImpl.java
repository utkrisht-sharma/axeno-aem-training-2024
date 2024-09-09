package com.assignment.core.services.impl;

import com.assignment.core.services.PostComponentService;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * This is a PostComponentServiceImpl class
 * which implements the getPosts method
 *
 */
@Component(service = PostComponentService.class , immediate = true)
public class PostComponentServiceImpl implements PostComponentService {


    private static final Logger LOGGER = LoggerFactory.getLogger(PostComponentServiceImpl.class);
    private static final String URL = "https://jsonplaceholder.typicode.com/posts";

    /**
     * @return it returns the data of URL passed in HttpGet
     */
    @Override
    public String getPosts(){
        try(CloseableHttpClient httpClient = HttpClients.createDefault()){
            HttpGet request = new HttpGet(URL);
            CloseableHttpResponse response = httpClient.execute(request);

            return EntityUtils.toString(response.getEntity());

        }
        catch(IOException e){
            LOGGER.error("not able to fetch data");
        }
        return "not found";
    }

}
