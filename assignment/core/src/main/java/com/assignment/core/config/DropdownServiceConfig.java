package com.assignment.core.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.osgi.service.metatype.annotations.Option;

/**
 * The {@code DropdownServiceConfig} interface defines the configuration for the Dropdown Service.
 * It allows specifying which option should be selected and displayed by the service.
 */
@ObjectClassDefinition(name = "Dropdown Service Configuration", description = "Configuration for Dropdown Service options")
public @interface DropdownServiceConfig {

    /**
     * Specifies the option to be selected and displayed.
     * <p>
     * This configuration provides three options:
     * <ul>
     * <li><strong>Image</strong>: Displays the image path.</li>
     * <li><strong>Link</strong>: Displays the path selected in the pathbrowser.</li>
     * <li><strong>Text</strong>: Displays the text field value.</li>
     * </ul>
     *
     * @return a {@code String} representing the selected option, with a default value of "TEXT".
     */
    @AttributeDefinition(
            name = "Selected Option",
            description = "Select the option to be displayed",
            options = {
                    @Option(label = "Image", value = "IMAGE"),
                    @Option(label = "Link", value = "LINK"),
                    @Option(label = "Text", value = "TEXT")
            }
    )
    String selectedOption() default "TEXT";
}
