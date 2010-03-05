package towerofdefense;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

import com.gemserk.componentsengine.components.Component;
import com.gemserk.componentsengine.messages.Message;
import com.gemserk.componentsengine.messages.SlickRenderMessage;
import com.gemserk.componentsengine.messages.UpdateMessage;

public class TestPerformanceComponent extends Component {

	public TestPerformanceComponent(String id) {
		super(id);
		 for (int i = 0; i < 1000; i++) {
		 things.add(new Button(new Vector2f((float) Math.random() * 800, (float) Math.random() * 600), null/*new Vector2f((float)Math.random(),(float)Math.random())*/, 60, "hola"));
		 }
//		for (int i = 0; i < 1000; i++) {
//			things.add(new Button(new Vector2f((float) Math.random() * 800, (float) Math.random() * 600), new Vector2f((float) Math.random(), (float) Math.random()), 60, null));
//		}
		// for (int i = 0; i < 1000; i++) {
		// things.add(new ImageButton(new Vector2f((float) Math.random() * 800, (float) Math.random() * 600), new Vector2f((float) Math.random(), (float) Math.random()), 60, "hola"));
		// }
		// for (int i = 0; i < 1000; i++) {
		// things.add(new ImageButton(new Vector2f((float) Math.random() * 800, (float) Math.random() * 600), new Vector2f((float) Math.random(), (float) Math.random()), 60, null));
		// }
	}

	List<UR> things = new ArrayList<UR>();

	@Override
	public void handleMessage(Message message) {
		if (message instanceof UpdateMessage) {
			UpdateMessage updateMessage = (UpdateMessage) message;
			update(updateMessage, updateMessage.getDelta());
		}
		if (message instanceof SlickRenderMessage) {
			SlickRenderMessage renderMessage = (SlickRenderMessage) message;
			render(renderMessage, renderMessage.getGraphics());
		}
	}

	private void render(SlickRenderMessage renderMessage, Graphics graphics) {
		for (UR ur : things) {
			ur.render(renderMessage, graphics);
		}
	}

	private void update(UpdateMessage updateMessage, int delta) {
		for (UR ur : things) {
			ur.update(updateMessage, delta);
		}
	}

	public class Button implements UR {

		Vector2f position;
		Vector2f direction;
		int size;
		String text;

		Color borderColor = Color.white;
		Color backgroundColor = Color.red;

		public Button(Vector2f position, Vector2f direction, int size, String text) {
			super();
			this.position = position;
			this.direction = direction;
			this.size = size;
			this.text = text;
		}

		@Override
		public void render(SlickRenderMessage renderMessage, Graphics g) {
			g.pushTransform();
			{

				g.translate(position.x, position.y);
				if(direction!=null)
					g.rotate(0, 0, (float) direction.getTheta());
				Color backupColor = g.getColor();

				g.setColor(backgroundColor);
				g.fillRect(-size / 2, -size / 2, size, size);
				g.setColor(borderColor);
				g.drawRect(-size / 2, -size / 2, size, size);
				if (text != null) {
					g.setColor(Color.white);
					g.drawString(text, 0, 0);

				}
				g.setColor(backupColor);
			}
			g.popTransform();
		}

		@Override
		public void update(UpdateMessage updateMessage, int delta) {

		}

	}

	public class ImageButton implements UR {

		Vector2f position;
		Vector2f direction;
		int size;
		String text;
		Image image;

		public ImageButton(Vector2f position, Vector2f direction, int size, String text) {
			super();
			this.position = position;
			this.direction = direction;
			this.size = size;
			this.text = text;
			try {
				this.image = new Image("assets/images/hudbutton.png");
			} catch (SlickException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void render(SlickRenderMessage renderMessage, Graphics g) {
			g.pushTransform();
			{

				g.translate(position.x, position.y);
				g.rotate(0, 0, (float) direction.getTheta());
				Color backupColor = g.getColor();

				g.drawImage(image, 0, 0);

				if (text != null) {
					g.setColor(Color.white);
					g.drawString(text, 0, 0);

				}
				g.setColor(backupColor);
			}
			g.popTransform();
		}

		@Override
		public void update(UpdateMessage updateMessage, int delta) {

		}

	}

	public interface UR {
		public void render(SlickRenderMessage renderMessage, Graphics g);

		public void update(UpdateMessage updateMessage, int delta);
	}

}
