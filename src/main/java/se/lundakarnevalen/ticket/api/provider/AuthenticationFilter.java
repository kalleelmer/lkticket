package se.lundakarnevalen.ticket.api.provider;

import se.lundakarnevalen.ticket.db.AuthToken;
import se.lundakarnevalen.ticket.db.Profile;
import se.lundakarnevalen.ticket.db.User;
import se.lundakarnevalen.ticket.logging.ErrorLogger;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Provider
public class AuthenticationFilter implements javax.ws.rs.container.ContainerRequestFilter {

	@Context
	private ResourceInfo resourceInfo;

	private static final String AUTHORIZATION_PROPERTY = "Authorization";

	@Override
	public void filter(ContainerRequestContext requestContext) {
		if (requestContext.getUriInfo().getPath().equals("swagger.json")) return;
		Class<?> clas = resourceInfo.getResourceClass();

		Method method = resourceInfo.getResourceMethod();

		if (clas.isAnnotationPresent(DenyAll.class) || method.isAnnotationPresent(DenyAll.class)) {
			throw new ForbiddenException();
		}

		if (clas.isAnnotationPresent(PermitAll.class) || method.isAnnotationPresent(PermitAll.class)) {
			return;
		}

		if (requestContext.getMethod().equals("OPTIONS")) {
			return;
		}

		final MultivaluedMap<String, String> headers = requestContext.getHeaders();

		final List<String> authorizations = headers.get(AUTHORIZATION_PROPERTY);

		if (authorizations == null || authorizations.isEmpty()) {
			throw new NotAuthorizedException("Login required");
		}

		if (method.isAnnotationPresent(RolesAllowed.class)) {
			RolesAllowed rolesAnnotation = method.getAnnotation(RolesAllowed.class);
			if (checkRoles(requestContext, authorizations, rolesAnnotation)) {
				return;
			}
		}
		if (clas.isAnnotationPresent(RolesAllowed.class)) {
			RolesAllowed rolesAnnotation = clas.getAnnotation(RolesAllowed.class);
			if (checkRoles(requestContext, authorizations, rolesAnnotation)) {
				return;
			}
		}
		throw new ForbiddenException();
	}

	private boolean checkRoles(ContainerRequestContext requestContext, final List<String> authorizations,
	                           RolesAllowed rolesAnnotation) {
		Set<String> rolesSet = new HashSet<String>(Arrays.asList(rolesAnnotation.value()));
		String authorization = authorizations.get(0);
		User user = null;
		try {
			if (authorization.startsWith("Token ")) {
				String tokenValue = authorization.substring(6, authorization.length());
				AuthToken token = AuthToken.getSingle(tokenValue);
				if (token == null) {
					throw new NotAuthorizedException("Bad token");
				}
				user = token.getUser();
			} else {
				throw new NotAuthorizedException("Unknown Authorization format");
			}
			if (user == null) {
				throw new NotAuthorizedException("User not authenticated");
			}
			requestContext.setProperty("user_id", user.id);
			if (rolesSet.contains("USER")) {
				return true;
			}
			for (Profile p : user.getProfiles()) {
				if (rolesSet.contains(p.getName())) {
					return true;
				}
			}
		} catch (SQLException e) {
			ErrorLogger.getInstance().put(e);
			throw new InternalServerErrorException("Database error");
		}
		return false;
	}
}