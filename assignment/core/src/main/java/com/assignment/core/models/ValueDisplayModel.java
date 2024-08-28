package com.assignment.core.models;

import com.assignment.core.services.DisplayValueService;
import com.assignment.core.services.DropDownService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;

/**
 * The {@code ValueDisplayModel} class is a Sling Model that provides logic to determine
 * which value to display based on a selected option. It adapts from a {@link SlingHttpServletRequest}.
 */
@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ValueDisplayModel {

    /**
     * Service used to retrieve the selected option from the dropdown.
     */
    @OSGiService
    private DropDownService dropDownService;

    /**
     * Service used to get the display value based on the selected option.
     */
    @OSGiService
    private DisplayValueService displayValueService;

    /**
     * The title value authored in the dialog.
     */
    @ValueMapValue
    private String title;

    /**
     * The image path authored in the dialog.
     */
    @ValueMapValue
    private String image;

    /**
     * The path selected in the pathbrowser authored in the dialog.
     */
    @ValueMapValue
    private String pathbrowser;

    /**
     * The text field value authored in the dialog.
     */
    @ValueMapValue
    private String textfield;

    /**
     * Initializes the model by creating a {@link DisplayValues} instance.
     * This method is called after the model's fields have been injected.
     */

    /**
     * Encapsulates the dialog values.
     */
    private DisplayValues displayValues;

    @PostConstruct
    protected void init() {
        // Initialize displayValues with the values from the dialog
        displayValues = new DisplayValues(title, image, pathbrowser, textfield);
    }

    /**
     * Returns the title value authored in the dialog.
     *
     * @return a {@code String} representing the title.
     */
    public String getTitle() {
        return displayValues.getTitle();
    }

    /**
     * Returns the image path authored in the dialog.
     *
     * @return a {@code String} representing the image path.
     */
    public String getImage() {
        return displayValues.getImage();
    }

    /**
     * Returns the path selected in the pathbrowser authored in the dialog.
     *
     * @return a {@code String} representing the path.
     */
    public String getPath() {
        return displayValues.getPath();
    }

    /**
     * Returns the text field value authored in the dialog.
     *
     * @return a {@code String} representing the text field value.
     */
    public String getText() {
        return displayValues.getText();
    }

    /**
     * Returns the selected option from the dropdown service.
     *
     * @return a {@code String} representing the selected option.
     */
    public String getSelectedOption() {
        return dropDownService.getSelectedOption();
    }

    /**
     * Returns the display value based on the selected option.
     * <p>
     * This method checks the selected option and returns the appropriate value:
     * <ul>
     * <li>If the option is "IMAGE", it returns the image path.</li>
     * <li>If the option is "LINK", it returns the path selected in the pathbrowser.</li>
     * <li>If the option is "TEXT", it returns the text field value.</li>
     * <li>For any other option, it returns an empty string.</li>
     * </ul>
     *
     * @return a {@code String} representing the display value based on the selected option.
     */
    public String getDisplayValue() {
        String selectedOption = getSelectedOption();
        return displayValueService.getDisplayValue(selectedOption, getImage(), getPath(), getText());
    }
}
