package com.luizdev.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.luizdev.main.Game;
import com.luizdev.world.Camera;

public class Entity {

	
	public static BufferedImage LIFEPACK_EN = Game.spritesheet.getSprite(2, 17, 16,16);
	public static BufferedImage WEPON_EN = Game.spritesheet.getSprite(18, 17, 16,16);
	public static BufferedImage BULLET_EN = Game.spritesheet.getSprite(18, 33, 16,16);
	public static BufferedImage ENEMY_EN = Game.spritesheet.getSprite(100, 37, 12,12);
	public static BufferedImage ENEMY_FEEDBACK = Game.spritesheet.getSprite(130,34,16,16);
	public static BufferedImage GUN_RIGHT = Game.spritesheet.getSprite(2, 50, 16,16);
	public static BufferedImage GUN_LEFT = Game.spritesheet.getSprite(18, 50, 16,16);
	public static BufferedImage BOSS_BS = Game.spritesheet.getSprite(66,65,32,32);
	
	protected int x;
	protected int y;
	protected int width;
	protected int height; 
	private BufferedImage sprite;
	
	private int maskx, masky, mwidth, mheight;
	
	public Entity(int x, int y, int width, int height, BufferedImage sprite) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.sprite = sprite; 
		
		this.maskx = 0; 
		this.masky = 0;
		this.mwidth = width;
		this.mheight = height;
		
	}
	
	public void setMask(int maskx, int masky, int mwidth, int mheigth) {
		this.maskx = maskx;
		this.masky = masky;
		this.mwidth = width;
		this.mheight = height;
	}

	public void setX(int newX) { 
		this.x = newX;
	}
	
	public void setY(int newY) {
		this.y = newY;
	}

	public int getX() {
		return this.x;
	}


	public int getY() {
		return this.y;
	}


	public int getWidth() {
		return this.width;
	}


	public int getHeight() {
		return this.height;
	}

	public void tick() {
		
	}
	
	public static boolean isCoidding(Entity e1, Entity e2) {
		Rectangle e1mask = new Rectangle(e1.getX() + e1.maskx, e1.getY() + e1.masky, e1.mwidth, e1.mheight);
		Rectangle e2mask = new Rectangle(e2.getX() + e2.maskx, e2.getY() + e2.masky, e2.mwidth, e2.mheight);
		
		return e1mask.intersects(e2mask);
	}
	
	public void render(Graphics g) {
		g.drawImage(sprite,this.getX() - Camera.x,this.getY() - Camera.y, null);
		//g.setColor(Color.red);
		//g.fillRect(this.getX()  + maskx - Camera.x, this.getY() + masky -  Camera.y, mwidth, height);
	}
	
}
