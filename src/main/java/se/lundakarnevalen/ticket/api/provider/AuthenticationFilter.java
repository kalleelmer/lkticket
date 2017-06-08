package se.lundakarnevalen.ticket.api.provider;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

@Provider
public class AuthenticationFilter implements javax.ws.rs.container.ContainerRequestFilter {

	@Context
	private ResourceInfo resourceInfo;

	private static final String AUTHORIZATION_PROPERTY = "Authorization";

	@Override
	public void filter(ContainerRequestContext requestContext) {
		Method method = resourceInfo.getResourceMethod();

		if (method.isAnnotationPresent(PermitAll.class)) {
			return;
		}

		if (method.isAnnotationPresent(DenyAll.class)) {
			throw new ForbiddenException();
		}

		final MultivaluedMap<String, String> headers = requestContext.getHeaders();

		final List<String> authorization = headers.get(AUTHORIZATION_PROPERTY);

		if (authorization == null || authorization.isEmpty()) {
			throw new NotAuthorizedException("Login required");
		}

		if (method.isAnnotationPresent(RolesAllowed.class)) {
			RolesAllowed rolesAnnotation = method.getAnnotation(RolesAllowed.class);
			Set<String> rolesSet = new HashSet<String>(Arrays.asList(rolesAnnotation.value()));
			String role = authorization.get(0);
			if (rolesSet.contains(role)) {
				return;
			}
			if (rolesSet.contains("USER")) {
				return;
			}
		}
		throw new ForbiddenException();
	}
}