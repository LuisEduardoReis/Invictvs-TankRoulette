package pt.invictus;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Affine2;

public class Sprite {
	
	public ArrayList<TextureRegion> frames;
		
	public float anim_delay; 
	public boolean anim_loop;
	
	public float i_rotation;
	
	public Sprite() {
		frames = new ArrayList<TextureRegion>();
		
		anim_loop = true;
		anim_delay = -1;
		
		i_rotation = 0;
	}
	
	public Sprite addFrame(TextureRegion frame) { frames.add(frame); return this; }
	
	
	static Affine2 t = new Affine2();
	static Color color = new Color();
	public void render(SpriteBatch batch,int anim_index, float x, float y, float scaleX, float scaleY, float rotation, Color color) {
		
		TextureRegion s = frames.get(anim_index);
		int w = s.getRegionWidth(), h = s.getRegionHeight();
		
		t.idt();
		t.translate((int) x, (int) y);
		t.scale(scaleX, scaleY);
		t.rotate(rotation + i_rotation);
		t.translate(-w/2, -h/2);
	
		
		color.set(color);
		batch.setColor(color);
		batch.draw(s, w, h, t);
		batch.setColor(Color.WHITE);
	}

	public Sprite setIRotation(int i) { this.i_rotation = i; return this; }
}