package pt.invictus.controllers;

import java.util.HashMap;
import java.util.HashSet;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.utils.viewport.Viewport;

import pt.invictus.Util;

public class Xbox360Controller extends ControllerAdapter implements GameController {

	Controller controller;
	HashMap<Object, Boolean> pressed = new HashMap<Object, Boolean>();
	HashMap<Object, Boolean> down = new HashMap<Object, Boolean>();
	HashSet<Integer> keys = new HashSet<Integer>();
	PovDirection pov = PovDirection.center;
	
	public Xbox360Controller(Controller c) {
		this.controller = c;
		
		c.addListener(this);
		
		keys.add(XBox360Pad.BUTTON_A);
		keys.add(XBox360Pad.BUTTON_X);
		keys.add(XBox360Pad.BUTTON_START);
		keys.add(XBox360Pad.BUTTON_BACK);
		
		for(Object o : keys) {
			pressed.put(o, false);
			down.put(o, false);
		}
		
		for(PovDirection p : PovDirection.values()) {
			pressed.put(p, false);
			down.put(p, false);
		}
	}
	
	@Override
	public void update() {	
		for(Object o : keys) {
			boolean v = controller.getButton((Integer) o);
			pressed.put(o, v && !down.get(o));
			down.put(o, v);
		}
		for(PovDirection p : PovDirection.values()) {
			pressed.put(p, p.equals(pov) && !down.get(p));
			down.put(p, p.equals(pov));
		}
	}
	
	boolean active = true;
	@Override
	public boolean getActive() {return active;}
	@Override
	public void setActive(boolean active) {this.active = active;};
	
	@Override
	public float getLookDir(float x, float y, Viewport viewport) {
		float rax = controller.getAxis(XBox360Pad.AXIS_LEFT_X);
		float ray = controller.getAxis(XBox360Pad.AXIS_LEFT_Y);
		return - (float) Math.atan2(ray, rax);
	}
	
	@Override
	public float getLookNormal() {
		float rax = controller.getAxis(XBox360Pad.AXIS_LEFT_X);
		float ray = controller.getAxis(XBox360Pad.AXIS_LEFT_Y);
		return Util.pointDistance(0, 0, rax, ray);
	}
	
	@Override
	public float getTrottleAxis() { return controller.getAxis(XBox360Pad.AXIS_RIGHT_TRIGGER); }
	
	@Override
	public boolean getKeyDown(Key key) {
		switch(key) {
		case A: return down.get(XBox360Pad.BUTTON_A);
		case X: return down.get(XBox360Pad.BUTTON_X);
		case BACK: return down.get(XBox360Pad.BUTTON_BACK);
		case START: return down.get(XBox360Pad.BUTTON_START);
		}
		return false;
	}

	@Override
	public boolean getKeyPressed(Key key) {
		switch(key) {
		case A: return pressed.get(XBox360Pad.BUTTON_A);
		case X: return pressed.get(XBox360Pad.BUTTON_X);
		case BACK: return pressed.get(XBox360Pad.BUTTON_BACK);
		case START: return pressed.get(XBox360Pad.BUTTON_START);
		}
		return false;
	}

	@Override
	public String getKeyName(Key key) {
		switch(key) {
		case A: return "A";
		case X: return "X";
		case BACK: return "Back";
		case START: return "Start";
		}
		return "<undefined>";		
	}
	
	
	@Override
	public String toString() {
		return controller.toString();
	}

	@Override
	public boolean povMoved(Controller controller, int povIndex, PovDirection value) {
		pov = value;		
		return super.povMoved(controller, povIndex, value);
	}


}
