package com.gemserk.componentsengine.commons.entities;

import com.gemserk.commons.slick.util.ScreenshotGrabber;
import com.gemserk.componentsengine.components.ReferencePropertyComponent;
import com.gemserk.componentsengine.components.annotations.EntityProperty;
import com.gemserk.componentsengine.components.annotations.Handles;
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.componentsengine.properties.Property;
import com.gemserk.componentsengine.templates.EntityBuilder;
import com.google.inject.Inject;

public class ScreenshotGrabberEntityBuilder extends EntityBuilder {


	@Override
	public void build() {
		
		property("prefix", parameters.get("prefix"));
		property("extension", parameters.get("extension"));

		component(new ReferencePropertyComponent("makeScreenshotHandler") {

			@Inject
			ScreenshotGrabber screenshotGrabber;

			@EntityProperty
			Property<String> prefix;

			@EntityProperty
			Property<String> extension;

			@Handles
			public void takeScreenshot(Message message) {
				screenshotGrabber.saveScreenshot(prefix.get(), extension.get());
			}

		});
		
	}
}