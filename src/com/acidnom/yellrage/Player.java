package com.acidnom.yellrage;

import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.extension.physics.box2d.PhysicsConnector;
import org.anddev.andengine.extension.physics.box2d.PhysicsFactory;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.extension.physics.box2d.util.Vector2Pool;
import org.anddev.andengine.opengl.texture.region.TextureRegion;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

public class Player extends Sprite {
	
	private boolean isInAir;
	private PhysicsWorld mPhysicsWorld;
	
	private Body body;
	
	private ContactListener contactListener;
	
	private int life;
	private boolean isAlive;
	private boolean isInitialized;

	public Player(float pX, float pY, TextureRegion pTextureRegion, PhysicsWorld mpw) {
		super(pX, pY, pTextureRegion);
		// TODO Auto-generated constructor stub
		mPhysicsWorld = mpw;
		isInitialized = false;
		initPlayer();
	}
	
	public Player(float pX, float pY, TextureRegion pTextureRegion, PhysicsWorld mpw, boolean doInit) {
		super(pX, pY, pTextureRegion);
		// TODO Auto-generated constructor stub
		mPhysicsWorld = mpw;
		isInitialized = false;
		if (doInit) initPlayer();
	}
	
	public void initPlayer()
	{
		if (!isInitialized)
		{
			isInitialized = true;
			isInAir = false;
			
			body = PhysicsFactory.createBoxBody(mPhysicsWorld, this, BodyType.DynamicBody, PhysicsFactory.createFixtureDef(1f, 0f, 0f));
			this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(this, body, true, false));
			
			isAlive = true;
			life = 100;
			
			contactListener = new ContactListener(){
				@Override
				public void beginContact(Contact contact) {
					isInAir = false;
					//body.setLinearVelocity(new Vector2(7,0));
				}
				@Override
				public void endContact(Contact contact)
				{
				}
				@Override
				public void preSolve(Contact contact, Manifold oldManifold) {
					// TODO Auto-generated method stub
					
				}
				@Override
				public void postSolve(Contact contact, ContactImpulse impulse) {
					// TODO Auto-generated method stub
					
				}
			};
			
			mPhysicsWorld.setContactListener(contactListener);
		}
	}
	
	public void jump()
	{
		if (isInAir) return;
		isInAir = true;
		final Vector2 v = Vector2Pool.obtain(0f, -10f);
		body.applyLinearImpulse(v, body.getWorldCenter());
		Vector2Pool.recycle(v);
	}
	
	public void jumpWithHeight(float level)
	{
		if (isInAir) return;
		isInAir = true;
		final Vector2 v = Vector2Pool.obtain(0f, -5f-(8f*level));
		body.applyLinearImpulse(v, body.getWorldCenter());
		Vector2Pool.recycle(v);
	}
	
	public boolean decrementLife(int decAmount)
	{
		life -= decAmount;
		if (life <= 0)
		{
			life = 0;
			isAlive = false;
		}
		return isAlive;
	}
	
	public int getLife()
	{
		return life;
	}
	
	public boolean isAlive()
	{
		return isAlive;
	}
	
	public float getVelocity()
	{
		return body.getLinearVelocity().y;
	}
	
	public void setState(float pX, float pY, float velY, int pLife, boolean aliveness, boolean airness)
	{
		this.mX = pX;
		this.mY = pY;
		body.setLinearVelocity(0, velY);
		life = pLife;
		isAlive = aliveness;
		isInAir = airness;
	}
	public boolean isInAir()
	{
		return isInAir;
	}

}
