package com.assignment.core.servlets;

import com.assignment.core.constants.Constants;
import com.assignment.core.models.StudentEntry;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.Servlet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * A servlet that handles GET requests to provide a list of top students
 * based on their total marks.
 */
@Component(service = Servlet.class, property = {
        "sling.servlet.paths=/apps/assignment/components/studentDetails",
        "sling.servlet.methods=GET"
})
public class TopStudentsServlet extends SlingSafeMethodsServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(TopStudentsServlet.class);

    /**
     * Handles GET requests to retrieve and return the top students based on their total marks..
     */
    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        ResourceResolver resolver = request.getResourceResolver();
        Resource studentEntriesResource = resolver.getResource(Constants.STUDENT_ENTRIES_PATH);
        List<StudentEntry> students = new ArrayList<>();

        if (studentEntriesResource != null) {
            for (Resource studentEntryResource : studentEntriesResource.getChildren()) {
                StudentEntry entry = studentEntryResource.adaptTo(StudentEntry.class);
                if (entry != null) {
                    students.add(entry);
                }
            }
        }

        if (students.isEmpty()) {
            LOGGER.info("No student entries found at path: {}", Constants.STUDENT_ENTRIES_PATH);
            response.setContentType("text/plain");
            response.getWriter().write("No student entries found.");
            return;
        }

        students.sort(Comparator.comparingInt(StudentEntry::getTotalMarks).reversed());

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < Math.min(Constants.TOP_N_STUDENTS, students.size()); i++) {
            StudentEntry student = students.get(i);
            if (i > 0) {
                result.append(", ");
            }
            result.append(student.getStudentName()).append(" ")
                    .append(student.getTotalMarks()).append(" ")
                    .append(Constants.RANKS[i]);
        }

        LOGGER.info("Top {} students retrieved successfully.", Constants.TOP_N_STUDENTS);

        response.setContentType("text/plain");
        response.getWriter().write(result.toString());
    }
}
