package com.assignment.core.services.impl;

import com.assignment.core.config.DropdownServiceConfig;
import com.assignment.core.services.DropDownService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;

/**
 * Implementation of the {@link DropDownService} interface.
 * This service retrieves the selected option from a dropdown configuration.
 */
@Component(service = DropDownService.class, immediate = true)
@Designate(ocd = DropdownServiceConfig.class)
public class DropDownServiceImpl implements DropDownService {

    /** The currently selected option configured in the dropdown. */
    private String selectedOption;

    /**
     * Activates or modifies the component configuration, initializing the selected option.
     *
     * @param config the configuration for the dropdown service, containing the selected option.
     */
    @Activate
    @Modified
    protected void activate(DropdownServiceConfig config) {
        this.selectedOption = config.selectedOption();
    }

    /**
     * {@inheritDoc}
     *
     * Retrieves the currently selected option from the dropdown configuration.
     *
     * @return a {@code String} representing the selected option.
     */
    public String getSelectedOption() {
        return this.selectedOption;
    }
}
