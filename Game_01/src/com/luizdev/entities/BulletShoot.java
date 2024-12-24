package com.luizdev.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.luizdev.main.Game;
import com.luizdev.world.Camera;
import com.luizdev.world.World;

public class BulletShoot extends Entity {

	private double dx;
	private double dy;
	private double spd = 4;
	
	private int life = 20,curlife = 0;
	
	private BufferedImage sprites;
	
	
	public BulletShoot(int x, int y, int width, int height, BufferedImage sprite, double dx, double dy) {
		super(x, y, width, height, sprite);
		this.dx = dx;
		this.dy = dy;
		sprites = Game.spritesheet.getSprite(40, 53, 4, 4);
		
	}
 

	
	public void tick() {
			x+=dx*spd;
			y+=dy*spd;
		curlife++;
		if(curlife == life) {
			Game.bulletshoot.remove(this);
			return;
		}
	}
	
	public void render(Graphics g) {
		//g.setColor(Color.yellow);
		//g.fillOval(this.getX() - Camera.x, this.getY() - Camera.y,width,height);
		super.render(g);
		g.drawImage(sprites,this.getX() - Camera.x,this.getY() - Camera.y,null);
	}
	
	
}
