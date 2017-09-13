package pt.invictus.controllers;

import com.badlogic.gdx.utils.viewport.Viewport;

public interface GameController {
	
	public enum Key {
		A,X,START,BACK
	}
	
	void update();
	
	float getLookDir(float x, float y, Viewport viewport);
	float getLookNormal();
	
	
	float getTrottleAxis();

	boolean getKeyDown(Key key);
	boolean getKeyPressed(Key key);
	String getKeyName(Key key);
	
	boolean getActive();
	void setActive(boolean active);

}
