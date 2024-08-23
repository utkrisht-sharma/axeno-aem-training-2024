package com.assignment.core.servlets;

import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.JackrabbitAccessControlList;
import org.apache.jackrabbit.api.security.principal.PrincipalManager;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.jackrabbit.commons.jackrabbit.authorization.AccessControlUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFactory;
import javax.jcr.security.AccessControlManager;
import javax.jcr.security.Privilege;
import javax.servlet.Servlet;
import java.security.Principal;

/**
 * Servlet that handles the creation of user groups and the assignment of permissions in Adobe Experience Manager (AEM).
 * This servlet is invoked via a GET request and creates a new group, sets properties on it, and assigns permissions
 * to specific paths in the repository.
 */
@Component(service = Servlet.class, property = {
        Constants.SERVICE_DESCRIPTION + "= Group creation and permission assignment",
        ServletResolverConstants.SLING_SERVLET_PATHS + "=/bin/groupCreation",
        ServletResolverConstants.SLING_SERVLET_METHODS + "=HttpConstants.METHOD_GET",

})

public class CreateAssignmentUserGroupServlet extends SlingSafeMethodsServlet {
    public static final String GROUP_ID = "groupID";
    public static final String GROUP_NAME = "groupName";
    public static final String GROUP_EMAIL = "groupEmail";

    private static final Logger logger = LoggerFactory.getLogger(CreateAssignmentUserGroupServlet.class);

    /**
     * Handles GET requests to create a new user group and assign permissions.
     *
     * @param request  the Sling HTTP request containing group details as parameters
     * @param response the Sling HTTP response
     */
    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {

        // Fetching all Group Details from Servlet Parameter
        String groupID = request.getParameter(GROUP_ID);
        String groupName = request.getParameter(GROUP_NAME);
        String groupEmail = request.getParameter(GROUP_EMAIL);

        logger.debug("Received parameters - groupID: {}, groupName: {}, groupEmail: {}", groupID, groupName, groupEmail);

        try {
            // Getting ResourceResolver from the request
            ResourceResolver resourceResolver = request.getResourceResolver();

            // Acquiring a session from the resource resolver
            Session session = resourceResolver.adaptTo(Session.class);
            if (session == null) {
                logger.error("Failed to obtain JCR session.");
                response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            }

            // Getting UserManager from ResourceResolver
            UserManager userManager = resourceResolver.adaptTo(UserManager.class);
            if (userManager == null) {
                logger.error("Failed to obtain UserManager.");
                response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            }

            // Creation of a new group with groupID
            Group createdGroup = null;
            if (userManager.getAuthorizable(groupID) == null) {
                logger.debug("Creating group with ID: {}", groupID);
                createdGroup = userManager.createGroup(groupID);

                // Setting the createdGroup Profile Property
                ValueFactory valueFactory = session.getValueFactory();
                Value groupNameValue = valueFactory.createValue(groupName);
                createdGroup.setProperty("./profile/givenName", groupNameValue);

                Value groupEmailValue = valueFactory.createValue(groupEmail);
                createdGroup.setProperty("./profile/email", groupEmailValue);

                session.save();
                logger.info("Group successfully created with ID: {}", createdGroup.getID());
            } else {
                logger.info("Group with ID {} already exists.", groupID);
            }

            // Getting AccessControlManager from session
            AccessControlManager accessControlManager = session.getAccessControlManager();
            if (accessControlManager == null) {
                logger.error("Failed to obtain AccessControlManager.");
                response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            }

            // Adding ACL Permission to the Group for Specific Paths
            logger.debug("Assigning permissions to group {} for various paths.", groupID);
            assignGroupPermissions(session, accessControlManager, groupID, "/content/wknd/us/en/magazine", Privilege.JCR_READ, Privilege.JCR_WRITE);
            assignGroupPermissions(session, accessControlManager, groupID, "/content/wknd/ca/en/magazine", Privilege.JCR_READ, Privilege.JCR_WRITE);
            assignGroupPermissions(session, accessControlManager, groupID, "/content/experience-fragments/wknd/us/en/featured", Privilege.JCR_READ, Privilege.JCR_WRITE);
            assignGroupPermissions(session, accessControlManager, groupID, "/content/experience-fragments/wknd/ca/en/featured", Privilege.JCR_READ, Privilege.JCR_WRITE);
            assignGroupPermissions(session, accessControlManager, groupID, "/content/cq:tags/wknd-shared/region/apac", Privilege.JCR_READ, Privilege.JCR_WRITE);
            assignGroupPermissions(session, accessControlManager, groupID, "/content/cq:tags/wknd-shared/activity", Privilege.JCR_READ, Privilege.JCR_WRITE);
            assignGroupPermissions(session, accessControlManager, groupID, "/content/dam/wknd-shared/en/activities/cycling", Privilege.JCR_READ, Privilege.JCR_WRITE);
            assignGroupPermissions(session, accessControlManager, groupID, "/content/dam/wknd-shared/en/magazine/skitouring", Privilege.JCR_READ, Privilege.JCR_WRITE);
            assignGroupPermissions(session, accessControlManager, groupID, "/conf/wknd-shared/settings/dam/cfm/models/article", Privilege.JCR_READ, Privilege.JCR_WRITE);
            assignGroupPermissions(session, accessControlManager, groupID, "/conf/wknd-shared/settings/dam/cfm/models/adventure", Privilege.JCR_READ, Privilege.JCR_WRITE);

            logger.debug("Logging out session.");
            session.logout();
        } catch (RepositoryException e) {
            logger.error("Error in group creation or permission assignment: {}", e.getMessage(), e);
        }
    }

    /**
     * Assigns the specified permissions to the given group for the specified path in the JCR repository.
     *
     * @param session              the JCR session
     * @param accessControlManager the Access Control Manager for managing permissions
     * @param groupID              the ID of the group to which permissions will be assigned
     * @param path                 the repository path for which permissions are to be assigned
     * @param privileges           the privileges to be assigned to the group for the specified path
     */
    private void assignGroupPermissions(Session session, AccessControlManager accessControlManager, String groupID, String path, String... privileges) {
        try {
            logger.debug("Assigning permissions for group {} at path {}", groupID, path);

            JackrabbitAccessControlList acl = AccessControlUtils.getAccessControlList(session, path);
            if (acl != null) {
                JackrabbitSession jackrabbitSession = (JackrabbitSession) session;
                PrincipalManager principalManager = jackrabbitSession.getPrincipalManager();
                Principal principal = principalManager.getPrincipal(groupID);

                if (principal == null) {
                    logger.error("Principal not found for group {}", groupID);
                    return;
                }

                Privilege[] privilegeArray = AccessControlUtils.privilegesFromNames(session, privileges);
                acl.addEntry(principal, privilegeArray, true);

                accessControlManager.setPolicy(acl.getPath(), acl);
                session.save();

                logger.info("Permissions {} assigned to group {} for path {}", (Object) privileges, groupID, path);
            } else {
                logger.warn("ACL is null for path {}", path);
            }
        } catch (RepositoryException e) {
            logger.error("Error assigning permissions for path {}: {}", path, e.getMessage(), e);
        }
    }
}
