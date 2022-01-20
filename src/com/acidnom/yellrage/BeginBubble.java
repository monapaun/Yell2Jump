package com.acidnom.yellrage;

import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.extension.physics.box2d.PhysicsConnector;
import org.anddev.andengine.extension.physics.box2d.PhysicsFactory;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.opengl.texture.region.TextureRegion;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

class BeginBubble {
	Sprite face;
	Body body;
	PhysicsWorld physicsWorld;
	TextureRegion bubbleTextureRegion;
	Scene scene;
	private static final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f);
	
	public BeginBubble (PhysicsWorld physicsWorld, TextureRegion bubbleTextureRegion, Scene scene) {
		this.physicsWorld = physicsWorld;
		this.bubbleTextureRegion = bubbleTextureRegion;
		this.scene = scene;
		
	}
	void addBubble(final float pX, final float pY) {
		face = new Sprite(pX, pY, this.bubbleTextureRegion);
		body = PhysicsFactory.createCircleBody(this.physicsWorld, face, BodyType.DynamicBody, FIXTURE_DEF);
		//face.animate(200);
		this.scene.attachChild(face);
		this.physicsWorld.registerPhysicsConnector(new PhysicsConnector(face, body, true, true));
	}

}
