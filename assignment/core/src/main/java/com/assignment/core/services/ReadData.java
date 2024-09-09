package com.assignment.core.services;

import com.assignment.core.models.PostData;

import java.io.IOException;
import java.util.List;

public interface ReadData {
    /**
     * method to read data from the mock api
     */
    List<PostData> readDataFromApi();
}
