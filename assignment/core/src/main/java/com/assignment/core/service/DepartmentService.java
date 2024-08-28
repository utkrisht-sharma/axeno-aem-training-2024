package com.assignment.core.service;

import java.util.List;

/**
 * Service interface for managing department-related operations.
 * This interface provides a method to retrieve a list of faculty names associated with a specific department.
 */
public interface DepartmentService {

    /**
     * Retrieves a list of faculty names associated with the specified department.
     *
     * @param department the name of the department for which faculty names are to be retrieved
     * @return a list of faculty names for the specified department, or null if the department does not exist
     */
    List<String> getFacultyNames(String department);
}
