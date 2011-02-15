package com.gemserk.games.zombierockers.gamestates;

import java.util.HashMap;

import org.newdawn.slick.Input;
import org.newdawn.slick.geom.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gemserk.componentsengine.commons.components.ImageRenderableComponent;
import com.gemserk.componentsengine.commons.components.RectangleRendererComponent;
import com.gemserk.componentsengine.commons.gui.TextFieldSlickImpl;
import com.gemserk.componentsengine.components.ReferencePropertyComponent;
import com.gemserk.componentsengine.components.annotations.EntityProperty;
import com.gemserk.componentsengine.components.annotations.Handles;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.game.GlobalProperties;
import com.gemserk.componentsengine.input.InputMappingBuilder;
import com.gemserk.componentsengine.input.InputMappingBuilderConfigurator;
import com.gemserk.componentsengine.input.KeyboardMappingBuilder;
import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory;
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.componentsengine.messages.MessageQueue;
import com.gemserk.componentsengine.messages.messageBuilder.MessageBuilder;
import com.gemserk.componentsengine.properties.FixedProperty;
import com.gemserk.componentsengine.properties.Properties;
import com.gemserk.componentsengine.properties.Property;
import com.gemserk.componentsengine.properties.ReferenceProperty;
import com.gemserk.componentsengine.slick.utils.SlickUtils;
import com.gemserk.componentsengine.templates.EntityBuilder;
import com.gemserk.componentsengine.templates.JavaEntityTemplate;
import com.gemserk.datastore.Data;
import com.gemserk.datastore.DataStore;
import com.gemserk.resources.ResourceManager;
import com.gemserk.scores.Scores;
import com.google.inject.Inject;
import com.google.inject.Provider;

@SuppressWarnings( { "unchecked", "unused" })
public class EnterScoreScreenEntityBuilder extends EntityBuilder {

	protected static final Logger logger = LoggerFactory.getLogger(EnterScoreScreenEntityBuilder.class);

	@Inject
	SlickUtils slick;

	@Inject
	GlobalProperties globalProperties;

	@Inject
	MessageQueue messageQueue;

	@Inject
	MessageBuilder messageBuilder;

	@Inject
	ResourceManager resourceManager;

	@Inject
	ChildrenManagementMessageFactory childrenManagementMessageFactory;

	@Inject
	Scores scores;

	@Inject
	Input input;

	@Inject
	Provider<InputMappingBuilderConfigurator> inputMappingConfiguratorProvider;

	@Inject
	DataStore dataStore;

	@Inject
	Provider<JavaEntityTemplate> javaEntityTemplateProvider;

	@Override
	public void build() {

		final Rectangle labelRectangle = slick.rectangle(-160, -25, 320, 50);

		property("screenBounds", parameters.get("screenBounds"));

		final Rectangle screenBounds = Properties.getValue(entity, "screenBounds");

		component(new ImageRenderableComponent("gameScreenshotRenderer")).withProperties(new ComponentProperties() {
			{
				property("color", slick.color(1, 1, 1, 1));
				property("position", slick.vector(screenBounds.getCenterX(), screenBounds.getCenterY()));
				property("direction", slick.vector(1, 0));
				property("layer", 0);
				property("image", globalProperties.getProperties().get("screenshot"));
			}
		});

		component(new RectangleRendererComponent("background")).withProperties(new ComponentProperties() {
			{
				property("position", slick.vector(0, 0));
				property("rectangle", screenBounds);
				property("lineColor", slick.color(0.2f, 0.2f, 0.2f, 0.0f));
				property("fillColor", slick.color(0.5f, 0.5f, 0.5f, 0.5f));
				property("layer", 1);
			}
		});

		property("levelName", parameters.get("levelName"));
		property("points", parameters.get("points"));
		property("textFieldSlickImpl", parameters.get("textFieldSlickImpl"));

		property("position", slick.vector(400, 300));
		property("color", slick.color(0f, 0f, 0f, 1f));

		final Long points = Properties.getValue(entity, "points");
		final String levelName = Properties.getValue(entity, "levelName");

		property("text", new FixedProperty(entity) {
			@Override
			public Object get() {
				TextFieldSlickImpl textFieldSlickImpl = Properties.getValue(getHolder(), "textFieldSlickImpl");
				return textFieldSlickImpl.getTextField().getText();
			}
		});

		property("enterScorePanel", javaEntityTemplateProvider.get().with(new EntityBuilder() {
			@Override
			public void build() {

				child(templateProvider.getTemplate("gemserk.gui.label").instantiate(entity.getId() + "_enterScoreLabel", new HashMap<String, Object>() {
					{
						put("font", resourceManager.get("FontDialogMessage"));
						put("position", slick.vector(screenBounds.getCenterX(), screenBounds.getCenterY() - 40));
						put("color", slick.color(0.2f, 0.2f, 0.8f, 1f));
						put("bounds", slick.rectangle(-160, -25, 320, 50));
						put("align", "center");
						put("valign", "center");
						put("layer", 10);
						put("message", "You made " + points + " points, enter your name");
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
						put("message", parameters.get("text"));
					}
				}));

			}

		}).instantiate(entity.getId() + "_enterScorePanel", new HashMap<String, Object>() {
			{
				put("text", new ReferenceProperty<Object>("text", entity));
			}
		}));

		property("uploadingScorePanel", javaEntityTemplateProvider.get().with(new EntityBuilder() {
			@Override
			public void build() {

				child(templateProvider.getTemplate("gemserk.gui.label").instantiate(entity.getId() + "_uploadingScoreLabel", new HashMap<String, Object>() {
					{
						put("font", resourceManager.get("FontDialogMessage"));
						put("position", slick.vector(screenBounds.getCenterX(), screenBounds.getCenterY()));
						put("color", slick.color(0.2f, 0.8f, 0.2f, 1f));
						put("bounds", slick.rectangle(-160, -25, 320, 50));
						put("align", "center");
						put("valign", "center");
						put("layer", 10);
						put("message", "Uploading your score, please wait...");
					}
				}));

			}

		}).instantiate(entity.getId() + "_uploadingScorePanel"));

		Entity enterScorePanel = Properties.getValue(entity, "enterScorePanel");
		Entity uploadingScorePanel = Properties.getValue(entity, "uploadingScorePanel");

		final Data currentProfile = (Data) globalProperties.getProperties().get("profile");

		if (currentProfile.getTags().contains("guest")) {
			child(enterScorePanel);
			component(new ReferencePropertyComponent("saveScoreWhenEnter") {

				@EntityProperty
				Property<String> levelName;

				@EntityProperty
				Property<Long> points;

				@EntityProperty
				Property<TextFieldSlickImpl> textFieldSlickImpl;

				@EntityProperty
				Property<Entity> uploadingScorePanel;

				@Handles
				public void scoreEntered(Message message) {

					final String name = textFieldSlickImpl.get().getTextField().getText();

					Data profile = (Data) globalProperties.getProperties().get("profile");
					
					profile.getTags().remove("guest");
					profile.getValues().put("name", name);

					dataStore.update(profile);

					messageQueue.enqueue(childrenManagementMessageFactory.removeEntity(entity.getId() + "_enterScorePanel"));
					messageQueue.enqueue(childrenManagementMessageFactory.addEntity(uploadingScorePanel.get(), entity));

					Entity uploadScoreEntity = templateProvider.getTemplate("entities.uploadScore").instantiate("uploadScore", new HashMap<String, Object>() {
						{
							put("name", name);
							put("points", points.get());
							put("levelName", levelName.get());
						}
					});
					messageQueue.enqueue(childrenManagementMessageFactory.addEntity(uploadScoreEntity, entity));
				}

			});

			component(inputMappingConfiguratorProvider.get().configure(new InputMappingBuilder("inputMappingComponent") {

				@Override
				public void build() {

					keyboard(new KeyboardMappingBuilder() {
						@Override
						public void build() {
							press("return", "scoreEntered");
						}
					});
				}

			}));

		} else {
			child(uploadingScorePanel);
			child(templateProvider.getTemplate("entities.uploadScore").instantiate("uploadScore", new HashMap<String, Object>() {
				{
					put("name", currentProfile.getValues().get("name"));
					put("points", points);
					put("levelName", levelName);
				}
			}));
		}

	}
}