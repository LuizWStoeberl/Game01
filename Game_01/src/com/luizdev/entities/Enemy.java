package com.luizdev.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.luizdev.main.Game;
import com.luizdev.main.Sound;
import com.luizdev.world.Camera;
import com.luizdev.world.World;



public class Enemy extends Entity{

	private double speed =1;
	
	public int maskx = 8, masky=8, maskw=8, maskh=8;
	
	private int frames = 0, maxFrames = 10, index = 0, maxIndex = 1;
	
	private BufferedImage[] sprites;
	
	private int life  = 8;
	 
	private boolean isDamage = false;
	private int damageFrames = 10, damageCurrent = 0;
	
	public Enemy(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, null); 
		sprites = new BufferedImage[2];
		sprites[0] = Game.spritesheet.getSprite(101, 38, 11, 11);
		sprites[1] = Game.spritesheet.getSprite(117, 37, 11, 11);
	}
	
	public void tick() { 
		
		if(this.isColiddingWithPalyer() == false) {
		if(Game.rand.nextInt(5) < 3) {
		if((int)x < Game.player.getX() && World.isFree((int) (x+speed), this.getY())
				&& !(isColidding((int)(x+speed), this.getY()))) {
			x+=speed; 
		}
		
		else if ((int)x > Game.player.getX() && World.isFree((int) (x-speed), this.getY())
				&& !(isColidding((int)(x-speed), this.getY()))) {
			x-=speed;
		}
		
		if((int)y < Game.player.getY() && World.isFree(this.getX(), (int)(y+speed))
				&& !(isColidding(this.getX(), (int)(y+speed)))) {
			y+=speed; 
		}
		
		else if ((int)y > Game.player.getY() && World.isFree(this.getX(), (int)(y-speed))
				&& !(isColidding(this.getX(), (int)(y-speed)))) {
			y-=speed;
		}
		
			frames ++;
			if(frames == maxFrames) {
				frames = 0;
				index++;
				if(index>maxIndex) {
					index = 0;
				}
			}
		}
		}else {
			//estamos colidindo
			if(Game.rand.nextInt(100) < 10) {
			
			Sound.hurtEffect.play();
			Game.player.life -= Game.rand.nextInt(1,5);
			Game.player.isDamage = true;
			
			if(Game.player.life <= 0) {
				}
			}
		}
	
	collidingBullet();
		
	if(life <= 0 ){
		destroySelf();
		Sound.enemyDie.play();
		return;
	}
	
	if(isDamage) {
		this.damageCurrent++;
		if(this.damageCurrent == damageFrames) {
			this.damageCurrent = 0;
			this.isDamage = false;
		}
		
	}
	
	
}
	
	
	public void collidingBullet() {
		for(int i =0 ; i<Game.bulletshoot.size() ; i++) {
			Entity e = Game.bulletshoot.get(i);
			if(e instanceof BulletShoot) {
				if(Entity.isCoidding(this,e) ) {
					isDamage = true;
					life--;
					Game.bulletshoot.remove(i);
					
					return;
				}
			}
		}
	}
	
	public void destroySelf() {
		Game.enemies.remove(this);
		Game.entities.remove(this);
	}
	
	public boolean isColiddingWithPalyer() {
		Rectangle enemyCurrent = new Rectangle(this.getX() + maskx, this.getY() + masky, maskw,maskh);
		Rectangle player = new Rectangle(Game.player.getX(), Game.player.getY(),16,16);
		
		return enemyCurrent.intersects(player);
	}
	
	 
	public boolean isColidding(int xnext,int ynext) {
		Rectangle enemyCurrent = new Rectangle(xnext + maskx, ynext + masky, maskw,maskh);
		for(int i = 0; i < Game.enemies.size(); i++) {
			Enemy e = Game.enemies.get(i);
			if(e == this) 
				continue;
			
			Rectangle targetEnemy = new Rectangle(e.getX()+ maskx,e.getY()+ masky,maskw,maskh);
			if(targetEnemy.intersects(enemyCurrent)) {
				 return true;
			} 	
		}
		
		return false;
	} 
	
	public void render(Graphics g) {
		super.render(g);
		if(!isDamage){
		g.drawImage(sprites[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
		//g.setColor(Color.blue);
		//g.fillRect(this.getX() + maskx - Camera.x, this.getY() + masky - Camera.y, maskw,maskh);
		}else {
			g.drawImage(Entity.ENEMY_FEEDBACK, this.getX() - Camera.x, this.getY() - Camera.y, null);
		}
	}

}
