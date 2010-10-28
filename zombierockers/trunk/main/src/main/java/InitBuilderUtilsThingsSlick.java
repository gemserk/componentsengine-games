import java.util.Map;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.state.StateBasedGame;

import com.gemserk.componentsengine.utils.annotations.BuilderUtils;
import com.google.inject.Inject;


public class InitBuilderUtilsThingsSlick {

	@Inject @BuilderUtils Map<String,Object> builderUtils;
	@Inject StateBasedGame stateBasedGame;
	@Inject GameContainer gameContainer;
	

	public void config() {
		builderUtils.put("gameContainer", gameContainer);
		builderUtils.put("stateBasedGame", stateBasedGame);
	}
}
