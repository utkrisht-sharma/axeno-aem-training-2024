package com.assignment.core.services.impl;

import com.assignment.core.services.DisplayValueService;
import org.osgi.service.component.annotations.Component;

/**
 * The {@code DisplayValueServiceImpl} class implements the {@link DisplayValueService} interface
 * and provides the logic for retrieving the display value based on the selected option.
 * <p>
 * This implementation is registered as an OSGi component and is used to determine which value
 * to display depending on the selected option.
 */
@Component(service = DisplayValueService.class, immediate = true)
public class DisplayValueServiceImpl implements DisplayValueService {

    /**
     * Retrieves the display value based on the selected option.
     * <p>
     * This method uses the provided selected option to determine which value to return:
     * <ul>
     * <li>If the option is "IMAGE", it returns the provided image path.</li>
     * <li>If the option is "LINK", it returns the provided path.</li>
     * <li>If the option is "TEXT", it returns the provided text.</li>
     * <li>For any other option, it returns an empty string.</li>
     * </ul>
     *
     * @param selectedOption a {@code String} representing the option selected by the user.
     * @param image          a {@code String} representing the image path.
     * @param path           a {@code String} representing the path.
     * @param text           a {@code String} representing the text.
     * @return a {@code String} representing the display value based on the selected option.
     */
    @Override
    public String getDisplayValue(String selectedOption, String image, String path, String text) {
        switch (selectedOption) {
            case "IMAGE":
                return image;
            case "LINK":
                return path;
            case "TEXT":
                return text;
            default:
                return "";
        }
    }
}
