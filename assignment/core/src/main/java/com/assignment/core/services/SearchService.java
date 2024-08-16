package com.assignment.core.services;

import com.day.cq.wcm.api.WCMException;
import org.apache.sling.api.resource.ResourceResolver;
import javax.jcr.RepositoryException;
import java.util.List;

public interface SearchService {

    /**
     * Searches for pages under the given path that have the specified properties and
     * were last modified between 2018 and 2020.
     *
     * @param resolver         the ResourceResolver
     * @param path             the path to search under
     * @param propertyOne      the first property name
     * @param propertyOneValue the first property value
     * @param propertyTwo      the second property name
     * @param propertyTwoValue the second property value
     * @return List of page paths matching the criteria
     */
     List<String> searchPages(ResourceResolver resolver, String path, String propertyOne,
                                    String propertyOneValue, String propertyTwo, String propertyTwoValue);

}
