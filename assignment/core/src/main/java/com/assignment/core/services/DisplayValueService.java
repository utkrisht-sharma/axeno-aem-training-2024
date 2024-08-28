package com.assignment.core.services;

/**
 * The {@code DisplayValueService} interface defines a service for retrieving the display value
 * based on a selected option. This service is used to determine which value to show
 * depending on the selected option.
 */
public interface DisplayValueService {

    /**
     * Retrieves the display value based on the selected option.
     * <p>
     * This method uses the selected option to decide which value to return:
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
    String getDisplayValue(String selectedOption, String image, String path, String text);
}
