package com.gemserk.games.zombierockers.gamestates;

import java.util.HashMap;

import org.newdawn.slick.geom.Rectangle;

import com.gemserk.componentsengine.commons.components.ImageRenderableComponent;
import com.gemserk.componentsengine.commons.components.RectangleRendererComponent;
import com.gemserk.componentsengine.commons.gui.TextFieldSlickImpl;
import com.gemserk.componentsengine.game.GlobalProperties;
import com.gemserk.componentsengine.input.InputMappingBuilder;
import com.gemserk.componentsengine.input.InputMappingBuilderConfigurator;
import com.gemserk.componentsengine.input.KeyboardMappingBuilder;
import com.gemserk.componentsengine.properties.FixedProperty;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.properties.ReferenceProperty;
import com.gemserk.componentsengine.slick.utils.SlickUtils;
import com.gemserk.componentsengine.templates.EntityBuilder;
import com.gemserk.componentsengine.templates.JavaEntityTemplate;
import com.gemserk.resources.ResourceManager;
import com.google.inject.Inject;
import com.google.inject.Provider;

@SuppressWarnings("unchecked")
public class ProfileScreenEntityBuilder extends EntityBuilder {

	@Inject
	Provider<InputMappingBuilderConfigurator> inputMappingConfiguratorProvider;

	@Inject
	SlickUtils slick;

	@Inject
	GlobalProperties globalProperties;

	@Inject
	ResourceManager resourceManager;

	@Inject
	Provider<JavaEntityTemplate> javaEntityTemplateProvider;

	@Override
	public void build() {

		property("screenBounds", parameters.get("screenBounds"));

		final Rectangle screenBounds = Properties.getValue(entity, "screenBounds");

		final Rectangle labelRectangle = slick.rectangle(-160, -25, 320, 50);

		// TextField textField = new TextField("", new ClipboardAwtImpl());
		// textField.setMaxLength(20);
		//		
		// property("textFieldSlickImpl", new TextFieldSlickImpl(textField));

		property("textFieldSlickImpl", parameters.get("textFieldSlickImpl"));
		property("text", new FixedProperty(entity) {
			@Override
			public Object get() {
				TextFieldSlickImpl textFieldSlickImpl = Properties.getValue(getHolder(), "textFieldSlickImpl");
				return textFieldSlickImpl.getTextField().getText();
			}
		});
		
		component(new ImageRenderableComponent("background")).withProperties(new ComponentProperties() {
			{
				property("position", slick.vector((float) (screenBounds.getWidth() * 0.5f), (float) (screenBounds.getHeight() * 0.5f)));
				property("color", slick.color(1, 1, 1, 1));
				property("direction", slick.vector(1, 0));
				property("layer", 0);
				property("image", resourceManager.get("background"));
			}
		});

		child(templateProvider.getTemplate("gemserk.gui.label").instantiate(entity.getId() + "_titleLabel", new HashMap<String, Object>() {
			{
				put("font", resourceManager.get("FontTitle"));
				put("position", slick.vector(screenBounds.getCenterX(), 40f));
				put("color", slick.color(0.3f, 0.8f, 0.3f, 1f));
				put("bounds", labelRectangle);
				put("align", "center");
				put("valign", "center");
				put("layer", 1);
				put("message", "Profile");
			}
		}));

		child(templateProvider.getTemplate("gemserk.gui.label").instantiate(entity.getId() + "_profileLabel", new HashMap<String, Object>() {
			{
				put("font", resourceManager.get("FontMessage"));
				put("position", slick.vector(screenBounds.getCenterX(), screenBounds.getCenterY() - 50));
				put("color", slick.color(0.2f, 0.2f, 0.8f, 1f));
				put("bounds", labelRectangle);
				put("align", "center");
				put("valign", "center");
				put("layer", 1);
				put("message", "Enter your name and press return");
			}
		}));

		component(new RectangleRendererComponent("background2")).withProperties(new ComponentProperties() {
			{
				property("position", slick.vector(screenBounds.getCenterX(), screenBounds.getCenterY()));
				property("rectangle", slick.rectangle(-160, -25, 320, 50));
				property("lineColor", slick.color(0f, 0f, 0f, 1f));
				property("fillColor", slick.color(1f, 1f, 1f, 1f));
				property("layer", 5);
				property("cornerRadius", 3);
			}
		});

		child(templateProvider.getTemplate("gemserk.gui.label").instantiate(entity.getId() + "_textFieldTextLabel", new HashMap<String, Object>() {
			{
				put("font", resourceManager.get("FontDialogMessage"));
				put("position", slick.vector(screenBounds.getCenterX(), screenBounds.getCenterY()));
				put("color", slick.color(0.2f, 0.8f, 0.2f, 1f));
				put("bounds", slick.rectangle(-160, -25, 320, 50));
				put("align", "left");
				put("valign", "center");
				put("layer", 10);
				put("message", new ReferenceProperty<Object>("text", entity));
			}
		}));
		
		child(templateProvider.getTemplate("zombierockers.gui.button").instantiate(entity.getId() + "_backButton", new HashMap<String, Object>() {
			{
				put("font", resourceManager.get("FontDialogMessage"));
				put("position", slick.vector(screenBounds.getMaxX() - 100, screenBounds.getMaxY() - 40f));
				put("bounds", labelRectangle);
				put("align", "center");
				put("valign", "center");
				put("layer", 1);
				put("message", "BACK");
				put("buttonReleasedSound", resourceManager.get("ButtonSound"));
				put("buttonReleasedMessageId", parameters.get("onBackButton"));
			}
		}));
		
		component(inputMappingConfiguratorProvider.get().configure(new InputMappingBuilder("inputMappingComponent") {

			@Override
			public void build() {

				keyboard(new KeyboardMappingBuilder() {
					@Override
					public void build() {
						press("return", "onProfileUpdated");
					}
				});
			}

		}));

	}
}