package pt.invictus;

public class Sprites {

	
	public static Sprite circle, rect;
	public static Sprite[] player;
	public static Sprite wave, spark;
	public static Sprite bullet,luckybox,bomb, missile,moneybag,banknote,diamond,star;
	
	
	public static void createSprites() {
		
		rect = new Sprite().addFrame(Assets.spritesheet60[0][0]);
		circle = new Sprite().addFrame(Assets.spritesheet60[0][1]);
		wave = new Sprite().addFrame(Assets.spritesheet60[0][2]);
		spark = new Sprite().addFrame(Assets.spritesheet60[0][3]).setIRotation(-90);
		bullet = new Sprite().addFrame(Assets.spritesheet60[1][1]);
		luckybox = new Sprite().addFrame(Assets.spritesheet60[1][2]);
		bomb = new Sprite().addFrame(Assets.spritesheet60[1][3]);
		missile = new Sprite().addFrame(Assets.spritesheet60[2][2]);
		moneybag = new Sprite().addFrame(Assets.spritesheet60[2][3]);
		banknote = new Sprite().addFrame(Assets.spritesheet60[3][3]);
		diamond = new Sprite().addFrame(Assets.spritesheet60[3][2]);
		star = new Sprite().addFrame(Assets.spritesheet60[4][2]);
		
		player = new Sprite[4];
		for(int i = 0; i < 4; i++) {
			player[i] = new Sprite();
			player[i].i_rotation = -90;
			player[i].anim_delay = 1f/16;
			player[i].addFrame(Assets.spritesheet60[2+i][0]);
			player[i].addFrame(Assets.spritesheet60[2+i][1]);
		}
		
	}
}
