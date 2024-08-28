package com.assignment.core.service.impl;

import com.assignment.core.config.DepartmentFacultyConfig;
import com.assignment.core.service.DepartmentService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of the {@link DepartmentService} interface.
 * <p>
 * This service manages faculty names associated with different departments using OSGi factory configurations.
 * It allows dynamic addition, modification, and retrieval of faculty information for various departments.
 * </p>
 * <p>
 * The service uses a static map to store department-faculty mappings. The map is synchronized to ensure thread safety
 * during updates and retrievals.
 * </p>
 */
@Component(
        service = DepartmentService.class,
        configurationPolicy = ConfigurationPolicy.REQUIRE
)
@Designate(
        ocd = DepartmentFacultyConfig.class,
        factory = true
)
public class DepartmentServiceImpl implements DepartmentService {

    private static final Logger LOG = LoggerFactory.getLogger(DepartmentServiceImpl.class);

    // Map to hold department names as keys and corresponding faculty names as values
    private static final Map<String, Set<String>> departmentFacultyMap = new HashMap<>();

    /**
     * Activates the service and updates the department-faculty mapping whenever a configuration is added or modified.
     * <p>
     * This method is called when the service is activated or when a configuration is modified. It updates the mapping
     * of department names to faculty names based on the provided configuration.
     * </p>
     *
     * @param config the configuration object for the department-faculty mapping
     */
    @Activate
    @Modified
    protected void activate(DepartmentFacultyConfig config) {
        String department = config.department().toLowerCase();
        Set<String> facultyNames = new HashSet<>(Arrays.asList(config.facultyNames()));

        synchronized (departmentFacultyMap) {
            departmentFacultyMap.put(department, facultyNames);
            LOG.info("Configuration activated/modified for department: {}", department);
        }
    }

    /**
     * Retrieves the list of faculty names associated with a given department.
     * <p>
     * This method returns a sorted list of faculty names for the specified department. If the department does not
     * exist in the configuration, it returns null.
     * </p>
     *
     * @param department the name of the department
     * @return a sorted list of faculty names for the specified department, or null if the department does not exist
     */
    @Override
    public List<String> getFacultyNames(String department) {
        synchronized (departmentFacultyMap) {
            Set<String> facultyNames = departmentFacultyMap.get(department.toLowerCase());
            if (facultyNames != null) {
                return facultyNames.stream().sorted().collect(Collectors.toList());
            }
        }
        return null;
    }
}
