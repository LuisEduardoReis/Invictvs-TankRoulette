package pt.invictus.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.viewport.Viewport;

import pt.invictus.Util;

public class KeyBoardMouseController implements GameController {

	
	
	boolean mouse_left_pressed, mouse_right_pressed;
	boolean mouse_left_down, mouse_right_down;
	
	@Override
	public void update() {
		boolean v;
		
		v = Gdx.input.isButtonPressed(Input.Buttons.LEFT);
		mouse_left_pressed = v && !mouse_left_down;
		mouse_left_down = v;
		
		v = Gdx.input.isButtonPressed(Input.Buttons.RIGHT);
		mouse_right_pressed = v && !mouse_right_down;
		mouse_right_down =  v;
	}
	
	boolean active = true;
	@Override
	public boolean getActive() {return active;}
	@Override
	public void setActive(boolean active) {this.active = active;};
	
	
	@Override
	public float getLookDir(float x, float y, Viewport viewport) {
		Ray r = viewport.getPickRay(Gdx.input.getX(),Gdx.input.getY());		
		return Util.pointDirection(x, y, r.origin.x, r.origin.y);
	}
	@Override
	public float getLookNormal() {
		return 1;
	}
	
	@Override
	public float getTrottleAxis() { 
		if (Gdx.input.isKeyPressed(Input.Keys.W)) return 1;
		if (Gdx.input.isKeyPressed(Input.Keys.S)) return -1;
		return 0;
	}
	
	@Override
	public boolean getKeyDown(Key key) {
		switch(key) {
		case A: return mouse_left_down;
		case X: return mouse_right_down;
		case START: return Gdx.input.isKeyPressed(Input.Keys.SPACE) || Gdx.input.isKeyPressed(Input.Keys.ENTER);
		case BACK: return Gdx.input.isKeyPressed(Input.Keys.BACKSPACE);
		}
		return false;
	}

	@Override
	public boolean getKeyPressed(Key key) {
		switch(key) {
		case A: return mouse_left_pressed;
		case X: return mouse_right_pressed;
		case START: return Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.ENTER);
		case BACK: return Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE);
		}
		return false;
	}

	@Override
	public String getKeyName(Key key) {
		switch(key) {
		case A: return "LMB";
		case X: return "RMB";
		case BACK: return "Backspace";
		case START: return "Space";
		}
		return "<undefined>";
	}

	@Override
	public String toString() {
		return "Keyboard Controller";
	}





	

}
