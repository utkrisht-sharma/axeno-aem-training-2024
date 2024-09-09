package com.assignment.core.models;

import com.assignment.core.services.ReadData;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

@Model(adaptables = Resource.class)
public class JsonDataModel {
    Logger logger = LoggerFactory.getLogger(JsonDataModel.class);
    @OSGiService
    ReadData data;

    public List<PostData> getPost() {
        logger.info("starting reading of data from service.");
        return data.readDataFromApi();
    }

}
