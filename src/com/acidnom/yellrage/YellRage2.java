package com.acidnom.yellrage;

import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.engine.handler.physics.PhysicsHandler;
import org.anddev.andengine.engine.handler.timer.ITimerCallback;
import org.anddev.andengine.engine.handler.timer.TimerHandler;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.primitive.Rectangle;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.Scene.IOnAreaTouchListener;
import org.anddev.andengine.entity.scene.Scene.IOnSceneTouchListener;
import org.anddev.andengine.entity.scene.Scene.ITouchArea;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.scene.menu.MenuScene;
import org.anddev.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.anddev.andengine.entity.scene.menu.item.IMenuItem;
import org.anddev.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.anddev.andengine.entity.scene.menu.item.decorator.ColorMenuItemDecorator;
import org.anddev.andengine.entity.shape.Shape;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.text.ChangeableText;
import org.anddev.andengine.entity.text.Text;
import org.anddev.andengine.entity.util.FPSLogger;
//import org.anddev.andengine.extension.physics.box2d.PhysicsConnector;
import org.anddev.andengine.extension.physics.box2d.PhysicsFactory;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.ui.activity.LayoutGameActivity;
import org.anddev.andengine.util.HorizontalAlign;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.SensorManager;
import android.os.Bundle;
//import android.util.Log;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.EditText;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class YellRage2 extends LayoutGameActivity implements CameraInfo, IOnMenuItemClickListener, IOnSceneTouchListener, IOnAreaTouchListener{
    /** Called when the activity is first created. */
	private Camera mCamera;				   // AndEngine's Camera element
	
	private PhysicsWorld mPhysicsWorld;	   // Handles the physics 
	private Scene mScene;				   // Contains the scene
	private MenuScene mMenuScene;		   // Pause Menu
	private MenuScene mGameOverScene;	   // Game Over Menu
	final Text[] countdownText = new Text[4];
	private BitmapTextureAtlas bitmapTextureAtlas; // Player and wall gfx
	private BitmapTextureAtlas wallTextureAtlas;
	private BitmapTextureAtlas bgTextureAtlas;
	private BitmapTextureAtlas mFontTexture;
	private TextureRegion playerTextureRegion;
	private TextureRegion wallTextureRegion;
	private TextureRegion bgTextureRegion;
	private Font mFont;
	
	private BitmapTextureAtlas unpauseTextureAtlas;
	private BitmapTextureAtlas wheelTextureAtlas;
	private TextureRegion wheelTextureRegion;
	private TextureRegion unpauseTextureRegion;
	private BitmapTextureAtlas quitTextureAtlas;
	private TextureRegion quitTextureRegion;
	private BitmapTextureAtlas gameoverTextureAtlas;
	private TextureRegion gameoverTextureRegion;
	private BitmapTextureAtlas highscoresTextureAtlas;
	private TextureRegion highscoresTextureRegion;
	
	private Player player;				   // the Player class
	private Wall[] wall = new Wall[11];
	private Wheel[] wheel = new Wheel[30];
	
	//private float temp; //time counter
	private float temp_collide=0; //remembers the last collision
	private float randX, randY; //random container
	private int k=1; // wall holder
	private int q=0; //wheel holder
	private Text gameOverText;
	private BitmapTextureAtlas gameOverFontTexture;
	private Font gameOverFont;
	private static final int MENU_UNPAUSE = 0;
	private static final int MENU_HIGHSCORES = 1;
	private static final int MENU_QUIT = 2;
	private static final int MENU_TITLE = 3;
	
	private static final int DIALOG_HIGH_SCORE = 0;
	
	private HighScoreTable hsTable;
	private EditText nameEditText;
	
	private YellRage2 me;
	
	private int secondsElapsed;
	
	private boolean setPauseOnLoad;
	
	private StateVariables state;
	
	@Override
    protected int getLayoutID() {
            return R.layout.main;
    }

    @Override
    protected int getRenderSurfaceViewID() {
            return R.id.xmllayoutexample_rendersurfaceview;
    }

	@Override
	public Engine onLoadEngine() {
		setPauseOnLoad = false;
		state = new StateVariables();

		Log.d("MEEP","MEEP");
		//Toast.makeText(this, "This game is best played in a quiet room. Loud tapping on wheels might cause the player to jump undesirably. ", Toast.LENGTH_LONG).show();
		this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		
		nameEditText = new EditText(this);
		hsTable = new HighScoreTable(this);
		
		me = this;
		
		Wall.setVelocity(-150);
		
		return new Engine(new EngineOptions(true, ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mCamera));
	}

	@Override
	public void onLoadResources() {
		this.bitmapTextureAtlas = new BitmapTextureAtlas(32, 32, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.wallTextureAtlas = new BitmapTextureAtlas(64, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.wheelTextureAtlas = new BitmapTextureAtlas(64, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.bgTextureAtlas = new BitmapTextureAtlas(1024, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.playerTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.bitmapTextureAtlas, this, "gfx/player.png", 0, 0);
		this.wallTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.wallTextureAtlas, this, "gfx/wall.png", 0, 0);
		this.wheelTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.wheelTextureAtlas, this, "gfx/grey.png", 0, 0);
		this.bgTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.bgTextureAtlas, this, "gfx/background.png", 0, 0);
		this.mEngine.getTextureManager().loadTexture(this.bitmapTextureAtlas);
		this.mEngine.getTextureManager().loadTexture(this.wallTextureAtlas);
		this.mEngine.getTextureManager().loadTexture(this.wheelTextureAtlas);
		this.mEngine.getTextureManager().loadTexture(this.bgTextureAtlas);
		this.mFontTexture = new BitmapTextureAtlas(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mFont = new Font(this.mFontTexture, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 16, true, Color.BLACK);
		this.mEngine.getTextureManager().loadTexture(this.mFontTexture);
		this.mEngine.getFontManager().loadFont(this.mFont);
		
		this.unpauseTextureAtlas =  new BitmapTextureAtlas(256, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.unpauseTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.unpauseTextureAtlas, this, "gfx/unpause.png", 0, 0);
		this.mEngine.getTextureManager().loadTexture(this.unpauseTextureAtlas);
		this.quitTextureAtlas =  new BitmapTextureAtlas(256, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.quitTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.quitTextureAtlas, this, "gfx/quit.png", 0, 0);
		this.mEngine.getTextureManager().loadTexture(this.quitTextureAtlas);
		this.gameoverTextureAtlas =  new BitmapTextureAtlas(512, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.gameoverTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.gameoverTextureAtlas, this, "gfx/gameover.png", 0, 0);
		this.mEngine.getTextureManager().loadTexture(this.gameoverTextureAtlas);
		this.highscoresTextureAtlas =  new BitmapTextureAtlas(256, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.highscoresTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.highscoresTextureAtlas, this, "gfx/hsbutton.png", 0, 0);
		this.mEngine.getTextureManager().loadTexture(this.highscoresTextureAtlas);
		
		this.gameOverFontTexture = new BitmapTextureAtlas(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.gameOverFont = new Font(this.gameOverFontTexture, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 52, true, Color.WHITE);

		this.mEngine.getTextureManager().loadTexture(this.gameOverFontTexture);
		this.mEngine.getFontManager().loadFont(this.gameOverFont);
	}

	@Override
	public Scene onLoadScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());		

		mScene = new Scene(); // create the scene
		mScene.setBackground(new ColorBackground(0.9f, 0.9f, 0.9f));
		mScene.setOnSceneTouchListener(this);
		
		this.mMenuScene = this.createMenuScene(); // for the pause menu
		this.mGameOverScene = this.createGameOverMenuScene(); // for the game over menu
		
//		countdownText[3] = new Text(CAMERA_WIDTH/2f-26, CAMERA_HEIGHT/2f - COUNTDOWN_FONT_SIZE/2f, this.mFont, "3", HorizontalAlign.CENTER);
//		countdownText[2] = new Text(CAMERA_WIDTH/2f-26, CAMERA_HEIGHT/2f - COUNTDOWN_FONT_SIZE/2f, this.mFont, "2", HorizontalAlign.CENTER);
//		countdownText[1] = new Text(CAMERA_WIDTH/2f-26, CAMERA_HEIGHT/2f - COUNTDOWN_FONT_SIZE/2f, this.mFont, "1", HorizontalAlign.CENTER);
//		countdownText[0] = new Text(CAMERA_WIDTH/2f-26*2, CAMERA_HEIGHT/2f - COUNTDOWN_FONT_SIZE/2f, this.mFont, "GO!", HorizontalAlign.CENTER);
//		
//		for (int i = 1; i < countdownText.length; i++)
//			countdownText[i].setPosition(CAMERA_WIDTH/2f - countdownText[i].getWidth()/2f, CAMERA_HEIGHT/2f - COUNTDOWN_FONT_SIZE/2f);
		
		 
		
		secondsElapsed = 0;
		
		//final Random random = new Random(9001);
		final Random random = new Random();
		
		final Sprite background = new Sprite(0,0,this.bgTextureRegion);
//		final Sprite background = new Sprite(0,0,this.bgTextureRegion) {
//			@Override
//			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
//				Log.v("tag","jump1");
//				if(mPhysicsWorld != null  && player != null) {
//					Log.v("tag","jump2");
//					if(pSceneTouchEvent.isActionDown() && !Wheel.oneIsClicked) {
//						Log.v("tag","jump3");
//						player.jumpWithHeight(0.5f);
//						return true;
//					}
//				}
//				return false;
//			}
//		};;
		mScene.registerTouchArea(background);
		mScene.attachChild(background);
		
		gameOverText = new Text(CAMERA_WIDTH/2f-26*9, CAMERA_HEIGHT/2f - 104f/2f, gameOverFont, "GAME OVER", HorizontalAlign.CENTER);
		
		final ChangeableText elapsedText = new ChangeableText(11, 11, this.mFont, "Seconds elapsed:", "Seconds elapsed: XXXXX".length());
		final ChangeableText lifeText = new ChangeableText(600, 10, this.mFont, "life:", "life: XXX".length());

		mScene.attachChild(elapsedText);
		mScene.attachChild(lifeText);
		
		mPhysicsWorld = new PhysicsWorld(new Vector2(0, SensorManager.GRAVITY_EARTH), false); // init the physics
						// --- Create the ground
		final Shape ground = new Rectangle(VIEWPORT_X, 340, VIEWPORT_WIDTH, 20);
		// VIEWPORT_HEIGHT-320+2 - original ground shape height
		final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0f, 0f);
		
		final Body groundBody = PhysicsFactory.createBoxBody(mPhysicsWorld, ground, BodyType.StaticBody, wallFixtureDef);
		groundBody.setUserData("ground"); // define this object as the ground (for player definition)
		
		ground.setColor(0.3f, 0.3f, 0.3f);
						// --- end ground creation
		
		player = new Player((CAMERA_WIDTH-32f)/5f, 100, playerTextureRegion, mPhysicsWorld,false);
		
		//lifeText.setText("Life: " + player.getLife());
		
		mScene.attachChild(ground);
		mScene.attachChild(player);
		
		for (int i = 0; i < wheel.length; i++)
		{
			wheel[i] = new Wheel(-50, -50, YellRage2.this.wheelTextureRegion);
		}
		
		mScene.registerUpdateHandler(new IUpdateHandler() {
			@Override
			public void reset() { }

			@Override
			public void onUpdate(final float pSecondsElapsed) {
				
				
				if (!player.isAlive()) return;
				
				for (int j=1; j<=10; j++){
					if(player.collidesWith(wall[j])) {
						if (YellRage2.this.mEngine.getSecondsElapsedTotal()-temp_collide>2f){
							if (!player.decrementLife(10)) // if player is dead after decrement
							{
								gameover();
							}
							lifeText.setText("Life: " + player.getLife());
							//Log.d("moo", "collided ");
						};	
						temp_collide=YellRage2.this.mEngine.getSecondsElapsedTotal();
						
					
					};
				};
			};
		});

		
		mScene.registerUpdateHandler(new TimerHandler(3.0f, true, new ITimerCallback() {
			public void onTimePassed(final TimerHandler pTimerHandler) {
				if (!player.isAlive()) return;
				//temp= YellRage.this.mEngine.getSecondsElapsedTotal();
				//Log.d("moo", "clicked on " + temp);
				randX = random.nextFloat() * CAMERA_WIDTH / 5;
				randY = random.nextInt(5);
				
				//wheel[q] = new Wheel(random.nextFloat() * CAMERA_WIDTH, -30-randY*10, YellRage.this.wheelTextureRegion);
				//Log.v("MOO",Integer.toString(q));
				
				if (wheel[q]!=null && wheel[q].isReady())
				{
					wheel[q].attachToScene(mScene);
					q++;
					if(q>=wheel.length) q=0;
				}
				
				//mScene.attachChild(wheel[q]);
				randY++;
				
				if (randY<=2) {
					for (int j=1; j<=randY+1; j++){
						k++;
						if (k>10) k=1;
						//Log.d("moo","contour"+k);
						wall[k] = new Wall (800+randX, 290-32*(j-1), YellRage2.this.wallTextureRegion);
						wall[k].setScale((float) 0.5);
						mScene.attachChild(wall[k]);
					}
				} else if (randY==3) {
					k++;
					if (k>10) k=1;
					wall[k] = new Wall (800+randX, 290, YellRage2.this.wallTextureRegion);
					wall[k].setScale((float) 0.5);
					mScene.attachChild(wall[k]);
					k++;
					if (k>10) k=1;
					wall[k] = new Wall (800+randX, 290-32, YellRage2.this.wallTextureRegion);
					wall[k].setScale((float) 0.5);
					mScene.attachChild(wall[k]);
					k++;
					if (k>10) k=1;
					wall[k] = new Wall (800+randX+32, 290, YellRage2.this.wallTextureRegion);
					wall[k].setScale((float) 0.5);
					mScene.attachChild(wall[k]);
					k++;
					if (k>10) k=1;
					wall[k] = new Wall (800+randX+32, 290-32, YellRage2.this.wallTextureRegion);
					wall[k].setScale((float) 0.5);
					mScene.attachChild(wall[k]);
				} else if (randY==4) {
					k++;
					if (k>10) k=1;
					wall[k] = new Wall (800+randX, 290, YellRage2.this.wallTextureRegion);
					wall[k].setScale((float) 0.5);
					mScene.attachChild(wall[k]);
					k++;
					if (k>10) k=1;
					wall[k] = new Wall (800+randX+32, 290, YellRage2.this.wallTextureRegion);
					wall[k].setScale((float) 0.5);
					mScene.attachChild(wall[k]);
					k++;
					if (k>10) k=1;
					wall[k] = new Wall (800+randX+64, 290, YellRage2.this.wallTextureRegion);
					wall[k].setScale((float) 0.5);
					mScene.attachChild(wall[k]);
					k++;
					if (k>10) k=1;
					wall[k] = new Wall (800+randX+32, 290-32, YellRage2.this.wallTextureRegion);
					wall[k].setScale((float) 0.5);
					mScene.attachChild(wall[k]);
				} else {
					k++;
					if (k>10) k=1;
					wall[k] = new Wall (800+randX, 290, YellRage2.this.wallTextureRegion);
					wall[k].setScale((float) 0.5);
					mScene.attachChild(wall[k]);
					k++;
					if (k>10) k=1;
					wall[k] = new Wall (800+randX+64, 290, YellRage2.this.wallTextureRegion);
					wall[k].setScale((float) 0.5);
					mScene.attachChild(wall[k]);
					k++;
					if (k>10) k=1;
					wall[k] = new Wall (800+randX, 290-32, YellRage2.this.wallTextureRegion);
					wall[k].setScale((float) 0.5);
					mScene.attachChild(wall[k]);
					k++;
					if (k>10) k=1;
					wall[k] = new Wall (800+randX+32, 290-32, YellRage2.this.wallTextureRegion);
					wall[k].setScale((float) 0.5);
					mScene.attachChild(wall[k]);
					k++;
					if (k>10) k=1;
					wall[k] = new Wall (800+randX+64, 290-32, YellRage2.this.wallTextureRegion);
					wall[k].setScale((float) 0.5);
					mScene.attachChild(wall[k]);
				}
			}
		}));
		
		mScene.registerUpdateHandler(new TimerHandler(1.0f, true, new ITimerCallback() {
			@Override
			public void onTimePassed(final TimerHandler pTimerHandler) {
				if (!player.isAlive()) return;
				secondsElapsed += 1;
				elapsedText.setText("Seconds elapsed: " + secondsElapsed);
				
				for (int j=0; j < wheel.length; j++)
				{
					if (wheel[j] == null) continue;
					if (wheel[j].getY() > CAMERA_HEIGHT)
					{
						wheel[j].removeFromScene();
						if (!wheel[j].hasBeenClicked())
						{
							//Log.v("Mew","INCREMENTINGVELOCITY!!");
							Wall.incrementVelocity();
							for (int i=0; i < wall.length; i++)
							{
								if (wall[i] != null) wall[i].updateVelocity();
							}
							wheel[j].resetClickState();
						}
//						if (wheel[q].hasBeenClicked())
//						{
//							for (int i=0; i < wall.length; i++)
//							{
//								wall[i].updateVelocity();
//							}
//						}
						//wheel[q].reset();
					}
				}
			}
		}));
		
		mScene.registerUpdateHandler(mPhysicsWorld);
		mScene.setTouchAreaBindingEnabled(true);
		if (setPauseOnLoad == true)
		{
			if (state.isSet() && state.player_isalive && state.player_life > 0 && state.world_secondselapsed > 0) // check the life just in case
			{
				player.detachSelf();
				player = new Player(state.player_x, state.player_y, playerTextureRegion, mPhysicsWorld);
				player.setState(state.player_x, state.player_y, state.player_velocity, state.player_life, state.player_isalive, state.player_isinair);
				secondsElapsed = state.world_secondselapsed;
				
				k = state.world_k;
				q = state.world_q;
				
				for (int i = 0; i < wheel.length; i++)
				{
					if (state.wheel_existence[i])
					{
						wheel[i].setState(mScene, state.wheel_x[i], state.wheel_y[i], state.wheel_isready[i], state.wheel_hasbeenclicked[i]);
					}
				}
				
				Wall.wallVelocity = state.wall_velocity;
				
				for (int i = 0; i < wall.length; i++)
				{
					if (state.wall_existence[i])
					{
						wall[i] = new Wall (state.wall_x[i], state.wall_y[i], YellRage2.this.wallTextureRegion);
						wall[i].setScale((float) 0.5);
						mScene.attachChild(wall[i]);
					}
				}
				
				mScene.attachChild(player);
				this.mScene.setChildScene(this.mMenuScene, false, true, true);
				state.unset();
			}
		}
		setPauseOnLoad = false;
		
		player.initPlayer();
		
		lifeText.setText("Life: " + player.getLife());
		elapsedText.setText("Seconds elapsed: " + secondsElapsed);
		
		return mScene;
	}
	
	@Override
	public void onLoadComplete() {
	}
	
	@Override
	protected void onPause() { // make sure recording stops properly
		//stopMCRThread();
//		if (hsTable != null) hsTable.closeDB();
		/*
		 * savedInstanceState.putFloat("Player_x", player.getX());
	  savedInstanceState.putFloat("Player_y", player.getY());
	  savedInstanceState.putFloat("Player_velocity", player.getVelocity());
	  savedInstanceState.putInt("Player_life", player.getLife());
	  savedInstanceState.putBoolean("Player_isalive", player.isAlive());
	  savedInstanceState.putBoolean("Player_isinair", player.isInAir());
		 * */
//		if (player != null)
//		{
//			state.player_x = player.getX();
//			state.player_y = player.getY();
//			state.player_velocity = player.getVelocity();
//			state.player_life = player.getLife();
//			state.player_isalive = player.isAlive();
//			state.player_isinair = player.isInAir();
//			Log.v("oP",Boolean.toString(state.player_isalive));
//			Log.v("oP",Integer.toString(state.player_life));
//			Log.v("oP","variables stored...");
//		}
		super.onPause();
	}
	
	@Override
	protected void onStop() { // make sure recording stops properly
		//stopMCRThread();
//		if (hsTable != null) hsTable.closeDB();
		super.onStop();
	}
	
	@Override
	protected void onResume() { // make sure recording starts again properly
		super.onResume();
		//startMCRThread();
//		if (hsTable != null && hsTable.isClosed())
//			hsTable.openDB();
			
	}
	

	

//	@Override
//	public void onAudioChunkReady() {
//		// This function takes care of reading the audio chunk for analysis when one is ready.
//		// We simply get the maximum value from the data and report push that to the sound level
//		// display. 
//		AudioChunkMarker acm = mrThread.getAudioChunkMarker();
//		short[][] data;
//		float dataMax;
//		float soundLevel;
//		//runningAverage = 0;
//		//Log.v("AudioChunk","Received!");
//		if (acm != null )
//		{//get the max value
//			data = mrThread.getFrame(acm.getPosition());
//			dataMax = 0;
//			for (int i = 0; i < data.length; i++)
//			{
//				for (int j=0; j < data[0].length;j++)
//				{
//					runningAverage -= runningData[Qpos];
//					runningData[Qpos] = data[i][j];
//					runningAverage += data[i][j];
//					Qpos = ++Qpos % filterLength;
//					if (runningAverage > dataMax)
//						dataMax = runningAverage;
//					if (data[i][j] > dataMax)
//						dataMax = data[i][j];
//				}
//			}
//			soundLevel = dataMax / (MicrophoneReadThread.MAXAUDIOVALUE); 
//			//Log.d("soundLevel",String.valueOf(soundLevel));
//			if (player.isAlive() & soundLevel > 0.1f)
//			{
//				if (soundLevel > 0.8f) soundLevel = 0.8f;
//				//player.jumpWithHeight(soundLevel);
//			}
//			//sld.updateLevel(dataMax / MicrophoneReadThread.MAXAUDIOVALUE); // normalize and push it to the sound level controller
//		}
//	}
	
	@Override
	public boolean onKeyDown(final int pKeyCode, final KeyEvent pEvent) {
		if(pKeyCode == KeyEvent.KEYCODE_MENU && pEvent.getAction() == KeyEvent.ACTION_DOWN) {
			if(this.mScene.hasChildScene()) {
				/* Remove the menu and reset it. */
				this.mMenuScene.back();
			} else {
				/* Attach the menu. */
				this.mScene.setChildScene(this.mMenuScene, false, true, true);
			}
			return true;
		} else {
			return super.onKeyDown(pKeyCode, pEvent);
		}
	}
	
	private void gameover()
	{
		gameOverText.setPosition((CAMERA_WIDTH - gameOverText.getWidth())/2f, 70f);
		mScene.attachChild(gameOverText);
		mScene.setChildScene(this.mGameOverScene);
		if (secondsElapsed > hsTable.getLowestHighScore())
		{//display high score window
			 mScene.registerUpdateHandler(new TimerHandler(1.0f, new ITimerCallback() {   
	          	    @Override
	          	    public void onTimePassed(final TimerHandler pTimerHandler){   
	          	    	
	          	    	me.runOnUiThread(new Runnable() /*can't create handler inside thread that has not called looper.prepare: this is the solution I used*/
		                {
	                        @Override
	                        public void run()
	                        {
	                        	//Toast.makeText(mContext, "Bye bye", Toast.LENGTH_LONG).show(); 
	                        	me.showDialog(0);
	                        }
		                });
	          	        mScene.unregisterUpdateHandler(pTimerHandler);
	          	    }
	          	}));
		}
	}
	
	protected MenuScene createMenuScene() {
		final MenuScene menuScene = new MenuScene(this.mCamera);
		
		final IMenuItem resetMenuItem = new ColorMenuItemDecorator(new SpriteMenuItem(MENU_UNPAUSE, this.unpauseTextureRegion), 1.0f,1.0f,1.0f, 1.0f,1.0f,1.0f);
		//final IMenuItem resetMenuItem = new ColorMenuItemDecorator(new TextMenuItem(MENU_UNPAUSE, this.gameOverFont, "UNPAUSE"), 1.0f,0.0f,0.0f, 1.0f,1.0f,1.0f);
		resetMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		menuScene.addMenuItem(resetMenuItem);

		final IMenuItem quitMenuItem = new ColorMenuItemDecorator(new SpriteMenuItem(MENU_QUIT, this.quitTextureRegion), 1.0f,1.0f,1.0f, 1.0f,1.0f,1.0f);
		//final IMenuItem quitMenuItem = new ColorMenuItemDecorator(new TextMenuItem(MENU_QUIT, this.gameOverFont, "QUIT"), 1.0f,0.0f,0.0f, 1.0f,1.0f,1.0f);
		quitMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		menuScene.addMenuItem(quitMenuItem);

		menuScene.buildAnimations();

		menuScene.setBackgroundEnabled(false);

		menuScene.setOnMenuItemClickListener(this);
		return menuScene;
	}
	
	protected MenuScene createGameOverMenuScene() {
		final MenuScene menuScene = new MenuScene(this.mCamera);
		
		final IMenuItem gameOverTitleMenuItem = new ColorMenuItemDecorator(new SpriteMenuItem(MENU_TITLE, this.gameoverTextureRegion), 1.0f,1.0f,1.0f, 1.0f,1.0f,1.0f);
		gameOverTitleMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		menuScene.addMenuItem(gameOverTitleMenuItem);
		
//		final IMenuItem blankMenuItem = new ColorMenuItemDecorator(new TextMenuItem(MENU_TITLE, this.mMenuFont, " "), 1.0f,0.0f,0.0f, 1.0f,0.0f,0.0f);
//		blankMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
//		menuScene.addMenuItem(blankMenuItem);

		final IMenuItem highScoresMenuItem = new ColorMenuItemDecorator(new SpriteMenuItem(MENU_HIGHSCORES, this.highscoresTextureRegion), 1.0f,1.0f,1.0f, 1.0f,1.0f,1.0f);
		//final IMenuItem highScoresMenuItem = new ColorMenuItemDecorator(new TextMenuItem(MENU_HIGHSCORES, this.gameOverFont, "HIGH SCORES"), 1.0f,0.0f,0.0f, 1.0f,1.0f,1.0f);
		highScoresMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		menuScene.addMenuItem(highScoresMenuItem);

		final IMenuItem quitMenuItem = new ColorMenuItemDecorator(new SpriteMenuItem(MENU_QUIT, this.quitTextureRegion), 1.0f,1.0f,1.0f, 1.0f,1.0f,1.0f);
		//final IMenuItem quitMenuItem = new ColorMenuItemDecorator(new TextMenuItem(MENU_QUIT, this.gameOverFont, "QUIT"), 1.0f,0.0f,0.0f, 1.0f,1.0f,1.0f);
		quitMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		menuScene.addMenuItem(quitMenuItem);
		
//		final IMenuItem blankMenuItem2 = new ColorMenuItemDecorator(new TextMenuItem(MENU_TITLE, this.mMenuFont, " "), 1.0f,0.0f,0.0f, 1.0f,0.0f,0.0f);
//		blankMenuItem2.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
//		menuScene.addMenuItem(blankMenuItem2);
//		

		menuScene.buildAnimations();

		menuScene.setBackgroundEnabled(false);

		menuScene.setOnMenuItemClickListener(this);
		return menuScene;
	}
	
	@Override
	protected Dialog onCreateDialog(final int pID) {
		switch(pID) {
			case DIALOG_HIGH_SCORE:
				return new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_info)
				.setTitle("HIGH SCORE! Name:")
				.setCancelable(true)
				.setView(nameEditText)
				.setPositiveButton("Add", new OnClickListener() {
					@Override
					public void onClick(final DialogInterface pDialog, final int pWhich) {
						hsTable.addNewHighScore(nameEditText.getText().toString(), secondsElapsed);
					}
				})
				.setNegativeButton("Cancel", new OnClickListener() {
					@Override
					public void onClick(final DialogInterface pDialog, final int pWhich) {
					}
				})
				.create();
			default:
				return super.onCreateDialog(pID);
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	  // Save UI state changes to the savedInstanceState.
	  // This bundle will be passed to onCreate if the process is
	  // killed and restarted.
//	  savedInstanceState.putBoolean("MyBoolean", true);
//	  savedInstanceState.putDouble("myDouble", 1.9);
//	  savedInstanceState.putInt("MyInt", 1);
//	  savedInstanceState.putString("MyString", "Welcome back to Android");
	  // etc.
		if (player != null)
		{
			Log.d("SIS","start saving...");
//			savedInstanceState.putFloat("Player_x", state.player_x);
//			savedInstanceState.putFloat("Player_y", state.player_y);
//			savedInstanceState.putFloat("Player_velocity", state.player_velocity);
//			savedInstanceState.putInt("Player_life", state.player_life);
//			savedInstanceState.putBoolean("Player_isalive", state.player_isalive);
//			savedInstanceState.putBoolean("Player_isinair", state.player_isinair);
			savedInstanceState.putFloat("Player_x", player.getX());
			savedInstanceState.putFloat("Player_y", player.getY());
			savedInstanceState.putFloat("Player_velocity", player.getVelocity());
			savedInstanceState.putInt("Player_life", player.getLife());
			savedInstanceState.putBoolean("Player_isalive", player.isAlive());
			savedInstanceState.putBoolean("Player_isinair", player.isInAir());
			savedInstanceState.putInt("World_secondselapsed", secondsElapsed);
			
			boolean[] wheelExistence = new boolean[wheel.length];
			float[] wheelPosX = new float[wheel.length];
			float[] wheelPosY = new float[wheel.length];
			boolean[] wheelHasBeenClicked = new boolean[wheel.length];
			boolean[] wheelIsReady = new boolean[wheel.length];
			
			for (int i = 0; i < wheel.length; i++)
			{
				if (wheel[i] == null)
				{
					wheelExistence[i] = false;
					continue;
				}
				wheelExistence[i] = true;
				wheelPosX[i] = wheel[i].getX();
				wheelPosY[i] = wheel[i].getY();
				wheelHasBeenClicked[i] = wheel[i].hasBeenClicked();
				wheelIsReady[i] = wheel[i].isReady();
			}
			
			savedInstanceState.putBooleanArray("Wheel_existence", wheelExistence);
			savedInstanceState.putFloatArray("Wheel_x", wheelPosX);
			savedInstanceState.putFloatArray("Wheel_y", wheelPosY);
			savedInstanceState.putBooleanArray("Wheel_hasbeenclicked", wheelHasBeenClicked);
			savedInstanceState.putBooleanArray("Wheel_isready", wheelIsReady);
			
			boolean[] wallExistence = new boolean[wall.length];
			float[] wallPosX = new float[wall.length];
			float[] wallPosY = new float[wall.length];
			
			for (int i = 0; i < wall.length; i++)
			{
				if (wall[i] == null)
				{
					wallExistence[i] = false;
					continue;
				}
				wallExistence[i] = true;
				wallPosX[i] = wall[i].getX();
				wallPosY[i] = wall[i].getY();
			}
			
			savedInstanceState.putBooleanArray("Wall_existence", wallExistence);
			savedInstanceState.putFloatArray("Wall_x", wallPosX);
			savedInstanceState.putFloatArray("Wall_y", wallPosY);
			
			savedInstanceState.putInt("Wall_velocity", Wall.wallVelocity);
			savedInstanceState.putInt("World_q", q);
			savedInstanceState.putInt("World_k", k);
			
			Log.d("SIS","Life:"+savedInstanceState.getInt("Player_life"));
		}
		else if (state.isSet())
		{
			savedInstanceState.putFloat("Player_x", state.player_x);
			savedInstanceState.putFloat("Player_y", state.player_y);
			savedInstanceState.putFloat("Player_velocity", state.player_velocity);
			savedInstanceState.putInt("Player_life", state.player_life);
			savedInstanceState.putBoolean("Player_isalive", state.player_isalive);
			savedInstanceState.putBoolean("Player_isinair", state.player_isinair);
			savedInstanceState.putInt("World_secondselapsed", state.world_secondselapsed);
			
			savedInstanceState.putBooleanArray("Wheel_existence", state.wheel_existence);
			savedInstanceState.putFloatArray("Wheel_x", state.wheel_x);
			savedInstanceState.putFloatArray("Wheel_y", state.wheel_y);
			savedInstanceState.putBooleanArray("Wheel_hasbeenclicked", state.wheel_hasbeenclicked);
			savedInstanceState.putBooleanArray("Wheel_isready", state.wheel_isready);
			
			savedInstanceState.putBooleanArray("Wall_existence", state.wall_existence);
			savedInstanceState.putFloatArray("Wall_x", state.wall_x);
			savedInstanceState.putFloatArray("Wall_y", state.wall_y);
			
			savedInstanceState.putInt("Wall_velocity", state.wall_velocity);
			savedInstanceState.putInt("World_q", state.world_q);
			savedInstanceState.putInt("World_k", state.world_k);
			
			Log.d("SIS2","Life:"+savedInstanceState.getInt("Player_life"));
		}
		super.onSaveInstanceState(savedInstanceState);
	}
	
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
	  super.onRestoreInstanceState(savedInstanceState);
	  // Restore UI state from the savedInstanceState.
	  // This bundle has also been passed to onCreate.
//	  boolean myBoolean = savedInstanceState.getBoolean("MyBoolean");
//	  double myDouble = savedInstanceState.getDouble("myDouble");
//	  int myInt = savedInstanceState.getInt("MyInt");
//	  String myString = savedInstanceState.getString("MyString");
	  state.player_x = savedInstanceState.getFloat("Player_x");
	  state.player_y = savedInstanceState.getFloat("Player_y");
	  state.player_velocity = savedInstanceState.getFloat("Player_velocity");
	  state.player_life = savedInstanceState.getInt("Player_life");
	  state.player_isalive = savedInstanceState.getBoolean("Player_isalive");
	  state.player_isinair = savedInstanceState.getBoolean("Player_isinair");
	  state.world_secondselapsed = savedInstanceState.getInt("World_secondselapsed");
	  
	  state.wheel_existence = savedInstanceState.getBooleanArray("Wheel_existence");
	  state.wheel_x = savedInstanceState.getFloatArray("Wheel_x");
	  state.wheel_y = savedInstanceState.getFloatArray("Wheel_y");
	  state.wheel_hasbeenclicked = savedInstanceState.getBooleanArray("Wheel_hasbeenclicked");
	  state.wheel_isready = savedInstanceState.getBooleanArray("Wheel_isready");
	  
	  state.wall_existence = savedInstanceState.getBooleanArray("Wall_existence");
	  state.wall_x = savedInstanceState.getFloatArray("Wall_x");
	  state.wall_y = savedInstanceState.getFloatArray("Wall_y");
	  
	  state.wall_velocity = savedInstanceState.getInt("Wall_velocity");
	  state.world_k = savedInstanceState.getInt("World_k");
	  state.world_q = savedInstanceState.getInt("World_q");
	  
	  Log.d("RIS","Life:"+savedInstanceState.getInt("Player_life"));
	  
	  state.set();
	  
	  setPauseOnLoad = true;
	  Log.d("BEEP","BEEP!");
	}
	
	
	
	@Override
	public boolean onMenuItemClicked(final MenuScene pMenuScene, final IMenuItem pMenuItem, final float pMenuItemLocalX, final float pMenuItemLocalY) {
		switch(pMenuItem.getID()) {
			case MENU_UNPAUSE:
//				this.mScene.reset();
//				//this.onLoadScene();
//
//				/* Remove the menu and reset it. */
//				this.mScene.clearChildScene();
//				this.mMenuScene.reset();
				this.mMenuScene.back();
				return true;
			case MENU_HIGHSCORES:
				//insert display high scores code here
				startActivity(new Intent(this, HighScoreChart.class));
				return true;
			case MENU_QUIT:
				/* End Activity. */
				this.finish();
				return true;
			case MENU_TITLE: // do NOTHING
				return true;
			default:
				return false;
		}
	}
	
	//Obstacle
	//Inner Classes
	//Obstacle
	//Inner Classes
		private static class Wall extends Sprite {
			private final PhysicsHandler mPhysicsHandler;
			private static int wallVelocity = -150;

			public Wall(final float pX, final float pY, final TextureRegion pTextureRegion) {
				super(pX, pY, pTextureRegion);
				this.mPhysicsHandler = new PhysicsHandler(this);
				this.mPhysicsHandler.setVelocityX(wallVelocity);
				this.registerUpdateHandler(this.mPhysicsHandler);
			}
			
			public static void incrementVelocity()
			{
				wallVelocity -= 10;
				//Log.v("Wall","VELOCITY INCREASED!!");
			}
			
			public void updateVelocity()
			{
				this.mPhysicsHandler.setVelocityX(wallVelocity);
			}
			
			public static void setVelocity(int vel)
			{
				wallVelocity = vel;
			}
			
//			@Override
//			protected void onManagedUpdate(final float pSecondsElapsed) {
//				super.onManagedUpdate(pSecondsElapsed);
//			}
		}
		
		private static class Wheel extends Sprite /*implements IOnAreaTouchListener*/ {
			private final PhysicsHandler mPhysicsHandler;
			private boolean isReady;
			private static final Random random = new Random();
			private boolean neverAttached;
			private boolean hasBeenClicked;

			public Wheel(final float pX, final float pY, final TextureRegion pTextureRegion) {
				super(pX, pY, pTextureRegion);
				this.mPhysicsHandler = new PhysicsHandler(this);
				this.registerUpdateHandler(this.mPhysicsHandler);
				setReady(true);
				neverAttached = true;
				hasBeenClicked = false;
				this.mPhysicsHandler.setVelocityY(30);
			}
			
			public void attachToScene(Scene scene)
			{
				resetFlake();
				scene.attachChild(this);
				if (neverAttached)
				{
					scene.registerTouchArea(this);
					neverAttached = false;
				}
				setReady(false);
			}
			
			public void setState(Scene scene, float pX, float pY, boolean ready, boolean beenClicked)
			{
				isReady = ready;
				hasBeenClicked = beenClicked;
				if (!isReady)
				{
					mX = pX;
					mY = pY;
					scene.attachChild(this);
					if (neverAttached)
					{
						scene.registerTouchArea(this);
						neverAttached = false;
					}
				}
			}
			
			public void resetFlake()
			{
				this.setPosition(random.nextFloat() * (CAMERA_WIDTH-32f), -32f-random.nextFloat()*30f);
				hasBeenClicked = false;
			}
			
			public void resetClickState()
			{
				hasBeenClicked = false;
			}
			
			@Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if(pSceneTouchEvent.isActionDown()) {
					this.setPosition(0, CAMERA_HEIGHT+20f);
					setReady(true);
					hasBeenClicked = true;
					return true;
				}
				return false;
				//return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
            }
			
			public void removeFromScene()
			{
				this.detachSelf();
				setReady(true);
			}
			
			public boolean hasBeenClicked()
			{
				return hasBeenClicked;
			}
//			@Override
//			protected void onManagedUpdate(final float pSecondsElapsed) {
//				//this.mPhysicsHandler.setVelocityY(30);
//				if (this.getY() > CAMERA_HEIGHT)
//				{
//					this.detachSelf();
//					//isReady = true;
//				}
//				super.onManagedUpdate(pSecondsElapsed);
//			}

			public boolean isReady() {
				return isReady;
			}

			public void setReady(boolean isReady) {
				this.isReady = isReady;
			}
			
//			public void launch(Scene scene)
//			{
//				
//				scene.attachChild(this);
//			}
//			@Override
//			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final ITouchArea pTouchArea, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
//				if (pSceneTouchEvent.getAction() == MotionEvent.ACTION_DOWN)
//				{
//					//this.removeFace((Sprite)pTouchArea);
//				}
//				return true;
//			}
		}

//		private void removeFace(final Sprite face) {
//			final PhysicsConnector facePhysicsConnector = this.mPhysicsWorld.getPhysicsConnectorManager().findPhysicsConnectorByShape(face);
//
//			this.mPhysicsWorld.unregisterPhysicsConnector(facePhysicsConnector);
//			this.mPhysicsWorld.destroyBody(facePhysicsConnector.getBody());
//
//			this.mScene.unregisterTouchArea(face);
//			this.mScene.detachChild(face);
//			
//			System.gc();
//		}
		
		@Override
		public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
				ITouchArea pTouchArea, float pTouchAreaLocalX,
				float pTouchAreaLocalY) {
			// TODO Auto-generated method stub
			//Log.v("test","test");
			return false;
		}

		
		@Override
		public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
			Log.v("tag","jump1");
			if(this.mPhysicsWorld != null  && this.player != null) {
				Log.v("tag","jump2");
				if(pSceneTouchEvent.isActionDown()) {
					Log.v("tag","jump3");
					this.player.jumpWithHeight(0.5f);
					return true;
				}
			}
			return false;
		}
}