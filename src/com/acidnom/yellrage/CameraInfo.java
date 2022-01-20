package com.acidnom.yellrage;

public interface CameraInfo {
	public static final int CAMERA_WIDTH = 720;
	public static final int CAMERA_HEIGHT = 480;
	
	public static final int PADDING = 0;
	
	public static final int VIEWPORT_X = PADDING;
	public static final int VIEWPORT_Y = 0;
	public static final int VIEWPORT_WIDTH = CAMERA_WIDTH-PADDING*2;
	public static final int VIEWPORT_HEIGHT = CAMERA_HEIGHT;
	
	public static final int LAYER_COUNT = 3;
	public static final int GAME_LAYER = 0;
	public static final int FRAME_LAYER = 1;
	public static final int HUD_LAYER = 2;
}