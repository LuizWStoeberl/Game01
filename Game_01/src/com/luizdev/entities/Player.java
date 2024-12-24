package com.luizdev.entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.luizdev.graficos.Spritesheet;
import com.luizdev.main.Game;
import com.luizdev.main.Sound;
import com.luizdev.world.Camera;
import com.luizdev.world.World;

public class Player extends Entity {
	
	public boolean right, up, left, down ;
	public int right_dir = 0, left_dir = 1;
	public int dir = right_dir;
	public double speed = 1.5; //velocidade, é padrão
	
	private BufferedImage[] rightPlayer;
	private BufferedImage[] leftPlayer;
	private BufferedImage playerDamage;
	
	private boolean hasGun = false;
	
	private int frames = 0, maxFrames = 25, index = 0, maxIndex = 3;
	private boolean moved = false;
	
	public int ammo = 0, novaamoo;
	
	public boolean isDamage = false;
	private int damageFrames = 0;
	
	public boolean shoot = false,mouseShoot = false;
	
	public double life = 100, maxlife = 100;
	public int mx,my;

	public Player(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, sprite);
		
		rightPlayer = new BufferedImage[4];
		leftPlayer = new BufferedImage[4];
		playerDamage = Game.spritesheet.getSprite(130, 17, 16,16);
		for(int i =0; i < 4; i++) {
			rightPlayer[i] = Game.spritesheet.getSprite(34 + (i*16), 18, 16, 16);
		}
		for(int i =0; i < 4; i++) {
			leftPlayer[i] = Game.spritesheet.getSprite(34 + (i*16), 33, 16, 16);
		}
		
	}
 
	public void tick() {
		moved = false; 
		if(right && World.isFree((int)(x+speed),this.getY())) {
			moved = true;
			dir = right_dir;
			x+=speed;
		}else if(left && World.isFree((int)(x-speed),this.getY())){
			moved = true;
			dir = left_dir;
			x-=speed;
		}
		moved = false;
		if(up && World.isFree(this.getX(),(int)(y-speed))) {
			moved = true;
			y-=speed;
		}else if (down && World.isFree(this.getX(),(int)(y+speed))) {
			moved = true;
			y+=speed;
		}
		
		if(moved) {
			frames ++;
			if(frames == maxFrames) {
				frames = 0;
				index++;
				if(index>maxIndex) {
					index = 0;
				}
			}
		}
		
		this.checkColisionLife();
		this.checkColisionAmmo();
		this.checkColisionGun();
		
		if(isDamage) {
			this.damageFrames++;
			if(this.damageFrames == 8) {
				this.damageFrames = 0;
				isDamage = false;
			}
		}
		
		if(shoot ) {
			shoot = false;
			if(hasGun && novaamoo > 0) {
			novaamoo--;
			//Criar bala e atirar
			shoot = false;
			int dx = 0;
			int px = 0;
			int py = 8;
			if(dir == right_dir) {
				px = 4;
				dx = 1;
			}else {
				px = -4;
				dx = -1;
			}
			
			
		BulletShoot bullet = new BulletShoot(this.getX() + px,this.getY() + py, 3, 3, null, dx, 0);
		Game.bulletshoot.add(bullet);
		
			}
		}
		
		if(mouseShoot) {
			mouseShoot = false;
			double angle =Math.atan2(my - (this.getY()+8 - Camera.y),mx - (this.getX()+8 - Camera.x));
			//System.out.println(angle);
			if(hasGun && novaamoo > 0) {
			novaamoo--;
			Sound.shoot.play();
			
			double dx = Math.cos(angle);
			double dy = Math.sin(angle);
			int px = 8;
			int py = 8;
			
		BulletShoot bullet = new BulletShoot(this.getX() + px,this.getY() + py, 3, 3, null, dx, dy);
		Game.bulletshoot.add(bullet);
			}
		}
		
		if(life<=0) {
			//Game Over
			Game.gameState = "GAME_OVER";
		}
		
		Camera.x = Camera.clamp(this.getX() - (Game.WIDTH/2),0,World.WIDTH*16 - Game.WIDTH);
		Camera.y = Camera.clamp(this.getY() - (Game.HEIGHT/2), 0, World.HEIGHT*16 - Game.HEIGHT);
	
	}	
	
	public void checkColisionAmmo() {
		for(int i = 0; i < Game.entities.size(); i++) {
			Entity atual = Game.entities.get(i);
			if(atual instanceof Bullet) {
				if(Entity.isCoidding(this, atual)) {
				ammo+=1;
				novaamoo = ammo ;
				//System.out.println("Munição atual: " + ammo);
				Game.entities.remove(atual);
				Sound.pickupAmmo.play();
			}
			}
		}
	}
	
	public void checkColisionLife() {
		for(int i = 0; i < Game.entities.size(); i++) {
			Entity atual = Game.entities.get(i);
			if(atual instanceof Lifepack) {
				if(Entity.isCoidding(this, atual)) {
				life+=8;
				if(life >= 100)
					life = 100;
				Game.entities.remove(atual);
				Sound.pickupLife.play();
			}
			}
		}
		return;
	}
	
	
	public void checkColisionGun() {
		for(int i = 0; i < Game.entities.size(); i++) {
			Entity atual = Game.entities.get(i);
			if(atual instanceof Weapon) {
				if(Entity.isCoidding(this, atual)) {
				hasGun = true; 
				System.out.println("Pegou a arma!");
				Game.entities.remove(atual);
				Sound.pickupGun.play();
			}
			}
		}
	}
	
	
	
		
	public void render(Graphics g) {
		if(!isDamage) {
		if(dir == right_dir) {
			g.drawImage(rightPlayer[index], this.getX() - Camera.x,this.getY() - Camera.y,null);
			if(hasGun) {
				// desenhar arma para direita
				g.drawImage(Entity.GUN_RIGHT,this.getX() - Camera.x, this.getY() - Camera.y, null);
			}
		}else if(dir == left_dir) {
			g.drawImage(leftPlayer[index], this.getX() - Camera.x,this.getY() - Camera.y,null);
			if(hasGun) {
				// desenhar arma para esquerda
				g.drawImage(Entity.GUN_LEFT,this.getX() - Camera.x, this.getY() - Camera.y, null);
		}
		}
		}else {
			g.drawImage(playerDamage, this.getX() - Camera.x, this.getY() - Camera.y, null);

	}
	}
}
