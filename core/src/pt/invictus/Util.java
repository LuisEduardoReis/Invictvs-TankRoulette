package pt.invictus;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.Vector2;

public class Util {
	
	public static float radToDeg = (float) (180 / Math.PI);
	public static float degToRad = (float) (Math.PI / 180);

	public static float stepTo(float a, float b, float x) {
		return Math.abs(b-a) <= x ? b : a + Math.signum(b-a)*x;
	}

	public static float randomRangef(float a, float b) {
		return (float) (a + Math.random()*(b-a));
	}
	public static int randomRangei(int x) {
		return (int) Math.floor(Math.random()*x);
	}

	
	public static boolean aabbToaabb(float x1, float y1, float w1, float h1,  float x2, float y2, float w2, float h2) {
		return (x1 < x2 + w2 && x1 + w1 > x2 &&	y1 < y2 + h2 && y1 + h1 > y2);
	}

	public static float pointDistance(float ix, float iy, float fx, float fy) {
		return (float) Math.sqrt((fx-ix)*(fx-ix) + (fy-iy)*(fy-iy));
	}
	public static float pointDistanceSqr(float ix, float iy, float fx, float fy) {
		return (float) (fx-ix)*(fx-ix) + (fy-iy)*(fy-iy);
	}
	public static float pointSquareDistance(float x, float y, float fx, float fy) {
		return (fx-x)*(fx-x) + (fy-y)*(fy-y);
	}

	public static float pointDirection(float x1, float y1, float x2, float y2) {
		return (float) Math.atan2(y2-y1, x2-x1);
	}
	
	public static float angleDifference(float a, float b) {
        double phi = Math.abs(b - a) % (2*Math.PI); 
        double diff = phi > Math.PI ? 2*Math.PI - phi : phi;
        
        return (float) ((a - b >= 0 && a - b <= Math.PI) || (a - b <=-Math.PI && a- b>= -2*Math.PI) ? diff : -diff); 
    }

	public static float stepToDirection(float a, float b, float x) {
		float d = Util.angleDifference(a, b);
		return Math.abs(d) < x ? b : a-x*Math.signum(d);
	}

	public static int pulse(float timer, float dur, float phase, float offset) {
		return (((timer+offset) % dur) > (dur*phase)) ? 1 : 0;
	}

	static GlyphLayout layout = new GlyphLayout();
	public static void drawTextCentered(SpriteBatch batch, BitmapFont font, String text, float x, float y) {
		layout.setText(font, text);
		font.draw(batch, text, x - layout.width/2, y + layout.height/2);
	}

	public static float clamp(float x, float a, float b) {
		return Math.max(a, Math.min(x, b));
	}

	public static boolean between(float x, float a, float b) {
		return x >= a && x <= b;
	}

	public static void drawTitle(SpriteBatch batch, BitmapFont font, String string, float x, float y, float alpha) {
		font.setColor(0.5f,0.5f,0.5f,alpha);
		Util.drawTextCentered(batch, font, string, x+10, y-10);
		font.setColor(1,1,1,alpha);
		Util.drawTextCentered(batch, font, string, x, y);		
	}
	
	public static void randomSound(Sound[] sounds) {Main.playSound(sounds[Util.randomRangei(sounds.length)]);}
	
	public static Vector2 getMapObjectPosition(MapObject o) {
		Vector2 r = new Vector2();
		MapProperties p = o.getProperties();
		r.set((Float) p.get("x"), (Float) p.get("y"));
		r.add(((Float) p.get("width"))/2, ((Float) p.get("height")/2));
		
		return r;
	}
	
	static Affine2 t = new Affine2();
	public static void drawCentered(SpriteBatch batch, TextureRegion s, float x, float y, float sw, float sh, float rotation, boolean center_h, boolean center_v) {
		
		int w = s.getRegionWidth(), h = s.getRegionHeight();
		
		t.idt();
		t.translate((int) x, (int) y);
		t.rotate(rotation);
		t.scale(sw/w, sh/h);		
		if (center_h) t.translate(-w/2,0);
		if (center_v) t.translate(0,-h/2);
		
	
		batch.draw(s, w, h, t);
	}
	
	public static String filledString(char c, int s) { return new String(new char[s]).replace('\0', c); }

	public static float interpolate(float x, float a, float b) {
		return a + x*(b-a);
	}
}
