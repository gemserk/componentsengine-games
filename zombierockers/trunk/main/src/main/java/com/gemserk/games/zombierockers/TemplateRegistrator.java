package com.gemserk.games.zombierockers;

import com.gemserk.componentsengine.templates.EntityBuilder;
import com.gemserk.componentsengine.templates.JavaEntityTemplate;
import com.gemserk.componentsengine.templates.RegistrableTemplateProvider;
import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * @author acoppes
 * Provides a nicer API for java templates registration.
 */
public class TemplateRegistrator {

	RegistrableTemplateProvider registrableTemplateProvider;

	Provider<JavaEntityTemplate> javaEntityTemplateProvider;
	
	@Inject
	public void setRegistrableTemplateProvider(RegistrableTemplateProvider registrableTemplateProvider) {
		this.registrableTemplateProvider = registrableTemplateProvider;
	}
	
	@Inject
	public void setJavaEntityTemplateProvider(Provider<JavaEntityTemplate> javaEntityTemplateProvider) {
		this.javaEntityTemplateProvider = javaEntityTemplateProvider;
	}

	private String id;

	public TemplateRegistrator with(String id) {
		this.id = id;
		return this;
	}

	public TemplateRegistrator register(EntityBuilder template) {
		registrableTemplateProvider.add(id, javaEntityTemplateProvider.get().with(template));
		return this;
	}

}