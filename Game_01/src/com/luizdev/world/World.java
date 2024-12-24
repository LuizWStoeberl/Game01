	package com.luizdev.world;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import com.luizdev.entities.Boss;
import com.luizdev.entities.Bullet;
import com.luizdev.entities.Enemy;
import com.luizdev.entities.Entity;
import com.luizdev.entities.Lifepack;
import com.luizdev.entities.Player;
import com.luizdev.entities.Weapon;
import com.luizdev.graficos.Spritesheet;
import com.luizdev.main.Game;

public class World {

	public static Tile[] tiles;
	public static  int WIDTH, HEIGHT;
	public static final int TILE_SIZE = 16;
	
	public World(String path) {
		try {
			BufferedImage map = ImageIO.read(getClass().getResource(path));
			int[] pixels = new int[map.getWidth() * map.getHeight()]; //array criado com o tamanho certo, indeoendente de quantos pixels for 
			WIDTH = map.getWidth();
			HEIGHT = map.getHeight();
			tiles = new Tile[map.getWidth() * map.getHeight()];
			map.getRGB(0, 0, map.getWidth(), map.getHeight(), pixels, 0, map.getWidth());
			for(int i =0;i<pixels.length;i++) {
				for(int xx = 0; xx<map.getWidth();xx++) {//pegando o eixo x do mapa
					for(int yy = 0; yy<map.getHeight();yy++) {//pegando o eixo y do mapa
						int pixelAtual = pixels[xx + (yy*map.getWidth())];//validando o valor do if de uma forma mais facil, pra n precisar escrever isso sempre dentro do if 
						tiles[xx+(yy*WIDTH)] = new FloorTile(xx*16, yy*16, Tile.TILE_FLOR);//sempre vai ser chão
						if(pixelAtual == 0xFF000000) {
							//CHÃO
							tiles[xx+(yy*WIDTH)] = new FloorTile(xx*16, yy*16, Tile.TILE_FLOR);
					
						}else if(pixelAtual == 0xFFE7C1FF) {
							tiles[xx+(yy*WIDTH)] = new FloorTile(xx*16, yy*16, 	Tile.TILE_FINALFLOR);
						}else if(pixelAtual == 0xFFFFFFFF ) { 
							//parede
							tiles[xx+(yy*WIDTH)] = new WallTile(xx*16, yy*16, Tile.TILE_WALL);
						}else if(pixelAtual == 0xFF9EFFEA) {
							tiles[xx+(yy*WIDTH)] = new WallTile(xx*16, yy*16, Tile.TILE_FINALWALL);
						}else if (pixelAtual == 0xFF0026FF ) {
							//player
							Game.player.setX(xx*16);
							Game.player.setY(yy*16); 
						}else if(pixelAtual == 0xFFFF0000) {
							//enemy  
							boolean enemyExists = false;
						    for (Enemy e : Game.enemies) {
						        if (e.getX() == xx*16 && e.getY() == yy*16) {
						            enemyExists = true;
						            break;
						        }
						    }
						    if (!enemyExists) {
						       // System.out.println("Creating enemy at position (" + (xx*16) + ", " + (yy*16) + ")");
						        Enemy en = new Enemy(xx*16, yy*16, 16, 16, Entity.ENEMY_EN);
						        Game.entities.add(en);
						        Game.enemies.add(en);
						    } 
						}else if (pixelAtual == 0xFF81FF68) {
						
							Game.boss.add(new Boss(xx*16, yy*16, 16, 16, Entity.BOSS_BS));
							
						}else if(pixelAtual == 0xFFFF6A00) {
							//arma
							boolean weaponExists = false;
							for (Entity e : Game.entities) {
							    if (e instanceof Weapon) {
							        weaponExists = true;
							        break;
							    }
							}
							if (!weaponExists) {
							    Game.entities.add(new Weapon(xx*16, yy*16, 16, 16, Entity.WEPON_EN));
							}
						}else if(pixelAtual == 0xFFFF00DC) {
							//lifepack
							Game.entities.add(new Lifepack(xx*16, yy*16, 16,16,Entity.LIFEPACK_EN));
						}else if(pixelAtual == 0xFFFFD800) {
							//bala
							boolean ammoExist = false;
						    for (Bullet e : Game.bullets) {
						        if (e.getX() == xx*16 && e.getY() == yy*16) {
						            ammoExist = true;
						            break;
						        }
						    }
						    if(!ammoExist) {
							Game.entities.add(new Bullet(xx*16, yy*16, 16,16,Entity.BULLET_EN));
						    }
						}
						
						
						
					}
				}
				//if(pixels[i] == 0xFFFF0000) { //0x é pra dizer q ta em hexadecimal, oq esta atras é o codigo da cor, nesse caso, vermelha, os primeiros dois "F's", também fazem parte do java
				//	System.out.println("Estou no pixel vermelho");
				//}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public static boolean isFree(int xnext, int ynext) {
		int x1 = xnext/TILE_SIZE;
		int y1 = ynext/TILE_SIZE;
		
		int x2 = (xnext+TILE_SIZE-1)/TILE_SIZE;
		int y2 = ynext/TILE_SIZE;
		
		int x3 = xnext/TILE_SIZE;
		int y3 = (ynext+TILE_SIZE-1)/TILE_SIZE;
		
		int x4 = (xnext+TILE_SIZE-1)/TILE_SIZE;
		int y4 = (ynext+TILE_SIZE-1)/TILE_SIZE;
		
		return !((tiles[x1 + (y1*World.WIDTH)] instanceof WallTile) ||
				(tiles[x2 + (y2*World.WIDTH)] instanceof WallTile) ||
				(tiles[x3 + (y3*World.WIDTH)] instanceof WallTile) ||
				(tiles[x4 + (y4*World.WIDTH)] instanceof WallTile));
	}
	
	
	public static void restartGame(String level) {
		Game.entities = new ArrayList<Entity>();
		Game.enemies = new ArrayList<Enemy>();
		Game.bullets = new ArrayList<Bullet>();
		Game.spritesheet = new Spritesheet("/spritesheet.png");
		Game.player = new Player(0,0, 16, 16, Game.spritesheet.getSprite(98, 2, 16, 16));
		Game.entities.add(Game.player);
		Game.world = new World("/"+level);
		return;
	}
	
	
	public void render(Graphics g) {
		
		int xstart = Camera.x >> 4;
		int ystart = Camera.y >> 4;
		
		int xfinal = xstart + (Game.WIDTH >> 4);
		int yfinal = ystart + (Game.HEIGHT >> 4);
		
		for(int xx= xstart; xx <= xfinal; xx++) {
			for(int yy = ystart; yy <= yfinal;yy++) {
				if(xx < 0 || yy < 0 || xx >= WIDTH || yy >= HEIGHT)
					continue;
				Tile tile = tiles[xx + (yy*WIDTH)];
				tile.render(g);
			}
		
		}
	}
	
}
