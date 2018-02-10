package se.lundakarnevalen.ticket;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.jaxrs.listing.SwaggerSerializers;
import io.swagger.models.Contact;
import io.swagger.models.Info;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Jonathan Schurmann on 2/9/18.
 */
@ApplicationPath("/lkticket")
public class ServletApplication extends Application {

	@Override
	public Set<Class<?>> getClasses() {
		Set<Class<?>> resources = new HashSet<>();
		try {
			ClassPath classPath = ClassPath.from(getClass().getClassLoader());
			ImmutableSet<ClassPath.ClassInfo> classes = classPath.getTopLevelClassesRecursive("se.lundakarnevalen.ticket.api");
			classes.forEach(classInfo -> resources.add(classInfo.load()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		resources.add(ApiListingResource.class);
		resources.add(SwaggerSerializers.class);
		return resources;
	}

	public ServletApplication() {
		configureSwagger();
	}

	private void configureSwagger() {
		BeanConfig beanConfig = new BeanConfig();
		beanConfig.setVersion("1.0.0");
		beanConfig.setSchemes(new String[]{"http", "https"});
		beanConfig.setHost("api-dev.lkticket.net:80");
		beanConfig.setResourcePackage("se.lundakarnevalen.ticket.api");
		beanConfig.setPrettyPrint(true);
		beanConfig.getSwagger().info(new Info()
				.version("1.0.0")
				.title("LKTicket")
				.contact(new Contact()
						.name("Lundakarnevalen")
						.url("http://lundakarnevalen.se")));
		beanConfig.setScan(true);
	}
}
