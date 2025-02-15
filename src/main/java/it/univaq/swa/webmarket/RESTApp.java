package it.univaq.swa.webmarket;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.jakarta.rs.json.JacksonJsonProvider;

import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import it.univaq.swa.webmarket.exceptions.AppExceptionMapper;
import it.univaq.swa.webmarket.exceptions.JacksonExceptionMapper;
import it.univaq.swa.webmarket.jackson.ObjectMapperContextResolver;
import it.univaq.swa.webmarket.resources.PurchaseRequestsRes;
import it.univaq.swa.webmarket.security.AuthLoggedFilter;
import it.univaq.swa.webmarket.security.AuthenticationRes;
import it.univaq.swa.webmarket.security.CORSFilter;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@ApplicationPath("rest")
public class RESTApp extends Application {

	private final Set<Class<?>> classes;

	public RESTApp() {
		HashSet<Class<?>> c = new HashSet<>();
		
		c.add(PurchaseRequestsRes.class);
		c.add(AuthenticationRes.class);

		c.add(JacksonJsonProvider.class);

		c.add(ObjectMapperContextResolver.class);

		c.add(AuthLoggedFilter.class);

		c.add(CORSFilter.class);

		c.add(AppExceptionMapper.class);
		c.add(JacksonExceptionMapper.class);

		c.add(OpenApiResource.class);

		classes = Collections.unmodifiableSet(c);
	}

	@Override
	public Set<Class<?>> getClasses() {
		return classes;
	}
}
