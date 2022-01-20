package com.acidnom.yellrage;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.primitive.Rectangle;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.Scene.IOnSceneTouchListener;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.shape.Shape;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.extension.physics.box2d.PhysicsFactory;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.extension.physics.box2d.util.Vector2Pool;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.view.RenderSurfaceView;
import org.anddev.andengine.sensor.accelerometer.AccelerometerData;
import org.anddev.andengine.sensor.accelerometer.IAccelerometerListener;
import org.anddev.andengine.ui.activity.BaseGameActivity;
import org.anddev.andengine.ui.activity.LayoutGameActivity;
import org.anddev.andengine.util.MathUtils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.hardware.SensorManager;

import android.util.Log;
import android.util.TypedValue;

import android.view.Gravity;
import android.view.MotionEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import android.widget.FrameLayout;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

public class YellMenu extends LayoutGameActivity implements  IAccelerometerListener, IOnSceneTouchListener, CameraInfo {

	private BitmapTextureAtlas mBitmapTextureAtlas;
	private BitmapTextureAtlas ngButtonTextureAtlas;
	private BitmapTextureAtlas hsButtonTextureAtlas;
	private BitmapTextureAtlas hButtonTextureAtlas;
	private BitmapTextureAtlas bgTextureAtlas;
	private BitmapTextureAtlas contButtonTextureAtlas;
	private BitmapTextureAtlas setButtonTextureAtlas;
	private BitmapTextureAtlas quitButtonTextureAtlas;

	private TextureRegion mCircleFaceTextureRegion;
	private TextureRegion ngButtonTextureRegion;
	private TextureRegion hsButtonTextureRegion;
	private TextureRegion hButtonTextureRegion;
	private TextureRegion bgTextureRegion;
	private TextureRegion contButtonTextureRegion;
	private TextureRegion setButtonTextureRegion;
	private TextureRegion quitButtonTextureRegion;
	
	public static Context gameContext;
	private Scene mScene;
	private PhysicsWorld mPhysicsWorld;
	private Context context ;
	static int mode = 1;
	
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
		final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		final EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
		engineOptions.getTouchOptions().setRunOnUpdateThread(true);

		return new Engine(engineOptions);
	}

	@Override
	public void onLoadResources() {
		
		context = this;
		
		/* Textures. */
		this.mBitmapTextureAtlas = new BitmapTextureAtlas(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.ngButtonTextureAtlas = new BitmapTextureAtlas(256,128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.hsButtonTextureAtlas = new BitmapTextureAtlas(256,128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.hButtonTextureAtlas = new BitmapTextureAtlas(256,128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.contButtonTextureAtlas = new BitmapTextureAtlas(256,128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.setButtonTextureAtlas = new BitmapTextureAtlas(256,128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.quitButtonTextureAtlas = new BitmapTextureAtlas(256,128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.bgTextureAtlas = new BitmapTextureAtlas(1024, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		/* TextureRegions. */
		this.ngButtonTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.ngButtonTextureAtlas, this, "ngbutton.png", 0, 0);
		this.hsButtonTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.hsButtonTextureAtlas, this, "hsbutton.png", 0, 0);
		this.hButtonTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.hButtonTextureAtlas, this, "hbutton.png", 0, 0);
		this.contButtonTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.contButtonTextureAtlas, this, "contbutton.png", 0, 0);
		this.setButtonTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.setButtonTextureAtlas, this, "setbutton.png", 0, 0);
		this.quitButtonTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.quitButtonTextureAtlas, this, "quit.png", 0, 0);
		this.mCircleFaceTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "smgrey.png", 0, 0); // 64x32
		this.bgTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.bgTextureAtlas, this, "background.png", 0, 0);
		
		this.mEngine.getTextureManager().loadTexture(this.mBitmapTextureAtlas);
		this.mEngine.getTextureManager().loadTexture(this.ngButtonTextureAtlas);
		this.mEngine.getTextureManager().loadTexture(this.hsButtonTextureAtlas);
		this.mEngine.getTextureManager().loadTexture(this.hButtonTextureAtlas);
		this.mEngine.getTextureManager().loadTexture(this.contButtonTextureAtlas);
		this.mEngine.getTextureManager().loadTexture(this.setButtonTextureAtlas);
		this.mEngine.getTextureManager().loadTexture(this.quitButtonTextureAtlas);
		this.mEngine.getTextureManager().loadTexture(this.bgTextureAtlas);
	}

	@Override
	public Scene onLoadScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());
		
		this.mScene = new Scene();
		this.mScene.setBackground(new ColorBackground(0, 0, 0));
		this.mScene.setOnSceneTouchListener(this);

		this.mPhysicsWorld = new PhysicsWorld(new Vector2(0, SensorManager.LIGHT_NO_MOON), false);

		final Shape ground = new Rectangle(3, CAMERA_HEIGHT - 3, CAMERA_WIDTH-6, 1);
		final Shape roof = new Rectangle(3, 3, CAMERA_WIDTH-6, 1);
		final Shape left = new Rectangle(3, 3, 1, CAMERA_HEIGHT-6);
		final Shape right = new Rectangle(CAMERA_WIDTH - 3, 3, 1, CAMERA_HEIGHT-6);

		final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, ground, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, roof, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, left, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, right, BodyType.StaticBody, wallFixtureDef);

		this.mScene.attachChild(ground);
		this.mScene.attachChild(roof);
		this.mScene.attachChild(left);
		this.mScene.attachChild(right);
		
		final Sprite background = new Sprite(0,0,this.bgTextureRegion);
		mScene.attachChild(background);

		this.mScene.registerUpdateHandler(this.mPhysicsWorld);
		
		for(int i=1;i<=60;i++) {
			BeginBubble bubble = new BeginBubble (mPhysicsWorld, mCircleFaceTextureRegion,mScene);
			bubble.addBubble(MathUtils.random(0.05f, 0.95f)*CAMERA_WIDTH, MathUtils.random(0.05f, 0.95f)*CAMERA_HEIGHT);
		}

		int centerX = (CAMERA_WIDTH - this.ngButtonTextureRegion.getWidth()) / 12 * 2;
		int centerY = (CAMERA_HEIGHT - this.ngButtonTextureRegion.getHeight()) / 32 * 2;
		final Sprite ngButton = new Sprite(centerX, centerY, this.ngButtonTextureRegion){
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if (pSceneTouchEvent.getAction() == MotionEvent.ACTION_DOWN)
				{
					if (mode==1) startActivity(new Intent(context, YellRage.class));
						else startActivity(new Intent(context, YellRage2.class));
				}
				return true;
			}
		};
		mScene.attachChild(ngButton);
		mScene.registerTouchArea(ngButton);
		
//		centerX = (CAMERA_WIDTH - this.ngButtonTextureRegion.getWidth()) / 12 * 10;
//		centerY = (CAMERA_HEIGHT - this.ngButtonTextureRegion.getHeight()) / 32 * 2;
//		final Sprite contButton = new Sprite(centerX, centerY, this.contButtonTextureRegion){
//			@Override
//			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
//				if (pSceneTouchEvent.getAction() == MotionEvent.ACTION_DOWN)
//				{
//					//cont activity
//				}
//				return true;
//			}
//		};
//		mScene.attachChild(contButton);
//		mScene.registerTouchArea(contButton);
		
		centerX = (CAMERA_WIDTH - this.ngButtonTextureRegion.getWidth()) / 12 * 11 ;
		centerY = (CAMERA_HEIGHT - this.ngButtonTextureRegion.getHeight()) / 32 * 11;
		final Sprite setButton = new Sprite(centerX, centerY, this.setButtonTextureRegion){
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if (pSceneTouchEvent.getAction() == MotionEvent.ACTION_DOWN)
				{
					startActivity(new Intent(context, Prefs.class));;
				}
				return true;
			}
		};
		mScene.attachChild(setButton);
		mScene.registerTouchArea(setButton);

		centerX = (CAMERA_WIDTH - this.hsButtonTextureRegion.getWidth()) / 12 * 10;
		centerY = (CAMERA_HEIGHT - this.hsButtonTextureRegion.getHeight()) / 32 * 2;
		final Sprite hsButton = new Sprite(centerX, centerY, this.hsButtonTextureRegion){
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if (pSceneTouchEvent.getAction() == MotionEvent.ACTION_DOWN)
				{
					startActivity(new Intent(context, HighScoreChart.class));
				}
				return true;
			}
		};
		mScene.attachChild(hsButton);
		mScene.registerTouchArea(hsButton);
		
		centerX = (CAMERA_WIDTH - this.hButtonTextureRegion.getWidth()) / 12 * 1;
		centerY = (CAMERA_HEIGHT - this.hButtonTextureRegion.getHeight()) / 32 * 11;
		final Sprite hButton = new Sprite(centerX, centerY, this.hButtonTextureRegion) {
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if (pSceneTouchEvent.getAction() == MotionEvent.ACTION_DOWN)
				{
					Intent helpIntent = new Intent (context, Help.class);
					startActivity(helpIntent);
				}
				return true;
			}
		};
		mScene.attachChild(hButton);
		mScene.registerTouchArea(hButton);
		
		centerX = (CAMERA_WIDTH - this.ngButtonTextureRegion.getWidth()) / 2 ;
		centerY = (CAMERA_HEIGHT - this.ngButtonTextureRegion.getHeight()) / 32 * 20;
		final Sprite quitButton = new Sprite(centerX, centerY, this.quitButtonTextureRegion){
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if (pSceneTouchEvent.getAction() == MotionEvent.ACTION_DOWN)
				{
					finish();
				}
				return true;
			}
		};
		mScene.attachChild(quitButton);
		mScene.registerTouchArea(quitButton);
		
		mScene.setTouchAreaBindingEnabled(true);
		
		gameContext = context;
		
		return this.mScene;
	}

	@Override
	public void onLoadComplete() {
	}

	@Override
	public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
		if(this.mPhysicsWorld != null) {
			if(pSceneTouchEvent.isActionDown()) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void onAccelerometerChanged(final AccelerometerData pAccelerometerData) {
		final Vector2 gravity = Vector2Pool.obtain(pAccelerometerData.getX(), pAccelerometerData.getY());
		this.mPhysicsWorld.setGravity(gravity);
		Vector2Pool.recycle(gravity);
	}
	
	@Override
	public void onResumeGame() {
		super.onResumeGame();

		this.enableAccelerometerSensor(this);
	}

	@Override
	public void onPauseGame() {
		super.onPauseGame();

		this.disableAccelerometerSensor();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		this.disableAccelerometerSensor();
	}
	
	/* Handles clicks on the "menu" button */
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
    	return true;
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.sound:
				YellMenu.mode=1;
				return true;
			case R.id.touch:
				YellMenu.mode=2;
				return true;
			// More items go here (if any) ...
		}
		return false;
	}
	
//	private void openModeDialog() {
//		new AlertDialog.Builder(this)
//		.setTitle(R.string.game_mode)
//		.setItems(R.array.mode,
//		new DialogInterface.OnClickListener() {
//			public void onClick(DialogInterface dialoginterface,int i) {
//				mode=i;
//			}
//		})
//		.show();
//	}
   
}


