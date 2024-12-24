package com.luizdev.main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.text.PlainDocument;

import com.luizdev.entities.Boss;
import com.luizdev.entities.Bullet;
import com.luizdev.entities.BulletShoot;
import com.luizdev.entities.Enemy;
import com.luizdev.entities.Entity;
import com.luizdev.entities.Player;
import com.luizdev.entities.Weapon;
import com.luizdev.graficos.Spritesheet;
import com.luizdev.graficos.UI;
import com.luizdev.world.Camera;
import com.luizdev.world.World;

public class Game extends Canvas implements Runnable, KeyListener, MouseListener{

	public static JFrame frame;
	private Thread thread;
	private boolean isRunning = true;
	public static final int WIDTH = 240;
	public static final int HEIGHT = 160;
	public static final int SCALE=3;
	 
	private BufferedImage image;
	
	public static  List<Entity> entities;
	public static  List<Enemy> enemies;
	public static List<Bullet> bullets;
	public static List<BulletShoot> bulletshoot;
	public static Spritesheet spritesheet;
	public static World world;
	public static Player player;
	public static List<Weapon> weapons;
	public static ArrayList<Boss> boss;
	
	public static Random rand;
	 
	public UI ui;
	
	public static String gameState = "MENU";
	private boolean showMassageGameOver = true;
	private int framesGameOver = 0;
	private boolean restartGame = false;
	
	public Menu menu;
	
	
	private int CUR_LEVEL = 1, MAX_LEVEL=5;
	private BufferedImage[] playerAnimation;//tem q ser array pq vai ser animado(mais de uma imagem por player)
	private int frames=0;
	private int maxFrames=20;//quantos frames vou animar o personagem. Quanto menor, mais rapido
	private int curAnimation = 0, maxAnimation = 2;
	private int x=0;
	
	
	//IMPORTANTE: as animações e td q envolvem o player, é legal ter uma classe própia.
	
	public Game() {
		Sound.musicBackground.loop();
		rand = new Random();
		addKeyListener(this);
		addMouseListener(this);
		this.setPreferredSize(new Dimension(WIDTH*SCALE,HEIGHT*SCALE));
		frame = new JFrame("Game #1");
		initFrame();
		//inicializando objetos 
		ui = new UI();
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);//padrao
		entities = new ArrayList<Entity>();
		enemies = new ArrayList<Enemy>();
		bullets = new ArrayList<Bullet>();
		bulletshoot = new ArrayList<BulletShoot>();
		weapons = new ArrayList<Weapon>();
		boss = new ArrayList<Boss>();
		
		
		spritesheet = new Spritesheet("/spritesheet.png");
		player = new Player(0,0, 16, 16, spritesheet.getSprite(98, 2, 16, 16));
		entities.add(player);
		world = new World("/level1.png");
		 
		menu = new Menu();
		//ou:
		//entities.add(new Player(0,0, 16, 16, spritesheet.getSprite(98, 2, 16, 16)));
		//ambos funcionam, a diferencça é que no primeiro, eu crio a variavel "player" separada
	}
	
	public void initFrame() {
		frame.add(this);
		frame.setResizable(false);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	public synchronized void start() {
		thread = new Thread(this);
		isRunning = true;
		thread.start();
	}
	
	public synchronized void stop() {
		isRunning = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	public static void main(String args[]) {
		Game game = new Game();
		game.start();
	}

	public void tick() {
		if(gameState == "NORMAL") {
		this.restartGame = false;
		
		for(int i=0;i<entities.size();i++) {
			Entity e = entities.get(i);
			/*if(e instanceof Player) {
				//estou dando tick () no player
			}*/
			e.tick();
		}
	
		for(int i = 0; i<bulletshoot.size();i++) {
			bulletshoot.get(i).tick();
		}
		
		if(enemies.size() == 0) {
			//System.out.println("Próximo Level");
			CUR_LEVEL++;
			if(CUR_LEVEL > MAX_LEVEL) {
				CUR_LEVEL = 1;
			}
			String newWorld = "level" + CUR_LEVEL + ".png";
			World.restartGame(newWorld);
		}
		}else if(gameState == "GAME_OVER") {
			//System.out.println("Game Over!");
			this.framesGameOver ++;
			if(this.framesGameOver == 20) {
				this.framesGameOver = 0;
				if(this.showMassageGameOver) {
					this.showMassageGameOver = false;
				}else {
					this.showMassageGameOver = true;
				}
			}
		
		
		if(restartGame) {
			this.restartGame = false;
			this.gameState = "NORMAL";
			CUR_LEVEL = 1;
			String newWorld = "level" + CUR_LEVEL + ".png";
			World.restartGame(newWorld);
		}
		}else if(gameState == "MENU") {
			menu.tick();
		}
		
	}
	
	public void render() {
		//parte onde se renderiza o jogo (personagens e td mais)
		
		BufferStrategy bs = this.getBufferStrategy();
		if(bs == null) {
			this.createBufferStrategy(3);
			return;
		}
		Graphics g = image.getGraphics();//Graphics é uma biblioteca para usar retangulos
		g.setColor(new Color(0,0,0)); //escolhe uma cor
		g.fillRect(0, 0, 240, 120); //cria um retangulo
		
		world.render(g);
		
		g.setFont(new Font("arial", Font.BOLD, 15));
		//g.setColor(Color.blue);//escolhe  cor 
		//g.fillRect(10, 10, 20, 20);// cria retangulo
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(new Color(0,0,0,180));//esse 180 é pra ficar mais escuro
		//IMPORTANTE: a renderização é por ordem, ou seja, o que vem antes no código é renderizado primeiro
		//g2.fillRect(0, 0, WIDTH, HEIGHT);
		//g2.rotate(Math.toRadians(90),90,90);//onde:
		//Math.toRadians é um metodo pra converter unidades
		//o primeiro '(90)' é o grau q vou rotacionar
		//os outros 2 '90,90'são os pontos de origem onde quero rotacionar
		//se eu colocar '90+8,90+8' (pq a imagem tem 16px e a metade é 8), vou rotacionar no meio da imagem
		for(int i=0;i<entities.size();i++) {
			Entity e = entities.get(i);
			e.render(g);
		}
		for(int i = 0; i<bulletshoot.size();i++) {
			bulletshoot.get(i).render(g);
		}
		ui.render(g);
		/***/
		g.dispose();//limpar dados q foram utilizados antes e n precisamos mais
		g = bs.getDrawGraphics();
		g.drawImage(image, 0, 0, WIDTH*SCALE, HEIGHT*SCALE, null);
		g.setFont(new Font("Arial", Font.BOLD, 20));
		g.setColor(Color.white);
		g.drawString("Munição: " + player.novaamoo, 580,25);
		if(gameState == "GAME_OVER"){
			Graphics2D g3 = (Graphics2D) g;
			g3.setColor(new Color(0,0,0,100));
			g3.fillRect(0, 0,WIDTH*SCALE,HEIGHT*SCALE);
			g.setFont(new Font("Arial", Font.BOLD,50));
			g.setColor(Color.white);
			g.drawString("Game Over", (WIDTH*SCALE)/2 - 120,(HEIGHT*SCALE)/2 - 40);
			g.setFont(new Font("Arial", Font.BOLD,30));
			if(showMassageGameOver){
				g.drawString(">Precione Enter para reiniciar<", (WIDTH*SCALE)/2 - 200,(HEIGHT*SCALE)/2 + 20);
			}
			
		}else if(gameState == "MENU") {
			menu.render(g);
		}
		bs.show(); //motra td oq for feito
		
		
	}
	
	@Override
	public void run() {
		long lastTime = System.nanoTime();//nanoTime pega o tempo atual do computador em nanosegundos(mt preciso)
		double amountOfTicks = 60.0;
		double ns = 1000000000 / amountOfTicks;//esse numero gigante ali é 1 segundo em nanosegundos, estou dividindo 1 segundo em 60 partes
		double delta = 0;
		int frames = 0;
		double timer = System.currentTimeMillis();//tbm medida de tempo, menos precisa porém mais leve pro pc
		requestFocus();
		while(isRunning) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			if(delta >= 1) {
				tick();
				render();
				frames++;
				delta--;
			}
			if(System.currentTimeMillis() - timer >=1000/*1 seg dps de ter mostrado mensagem*/) {
				System.out.println("FPS: " + frames);
				frames = 0;
				timer+=1000;
			}
		}
		stop();//garantia q se por algum motivo sair do looping, ele vai parar
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_RIGHT ||
				e.getKeyCode() == KeyEvent.VK_D) { 
			//getKeyCode pega o codigo da tecla, keyevent.vk_right, lê a tecla "direita", a setinha. ou seja, se a seta direita for precionada, entra nesse if, o mesm pro "d"
			player.right = true;
			
		}else if(e.getKeyCode() == KeyEvent.VK_LEFT ||
				e.getKeyCode() == KeyEvent.VK_A) {
			player.left = true;
			
			
		}
		
		//AGR ESCREVO SEPARADO PARA CIMA E BAIXO, PARA QUE EU POSSA APERTAR VERTICAL E HORIZONTAL AO MSM TEMPO
		
		if(e.getKeyCode() == KeyEvent.VK_UP || 
				e.getKeyCode() == KeyEvent.VK_W){
			
			player.up = true;
			
			if(gameState == "MENU") {
				menu.up = true;
			}
			
		}else if(e.getKeyCode() == KeyEvent.VK_DOWN || 
				e.getKeyCode() == KeyEvent.VK_S) {
			
			player.down = true;
			
			if(gameState == "MENU") {
				menu.down = true;
			}
		}
		
	if(e.getKeyCode() == KeyEvent.VK_SPACE) {
		player.shoot = true;
	}
	
	if(e.getKeyCode() == KeyEvent.VK_ENTER){
		this.restartGame = true;
		if(gameState == "MENU"){
				menu.enter = true;
		}
	}
	
	if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
		gameState = "MENU";
		menu.pause = true;
	}
	
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_RIGHT ||
				e.getKeyCode() == KeyEvent.VK_D) { 
			//getKeyCode pega o codigo da tecla, keyevent.vk_right, lê a tecla "direita", a setinha. ou seja, se a seta direita for precionada, entra nesse if, o mesm pro "d"
			player.right = false;
			
		}else if(e.getKeyCode() == KeyEvent.VK_LEFT ||
				e.getKeyCode() == KeyEvent.VK_A) {
			player.left = false;
			
			
		}
		
		//AGR ESCREVO SEPARADO PARA CIMA E BAIXO, PARA QUE EU POSSA APERTAR VERTICAL E HORIZONTAL AO MSM TEMPO
		
		if(e.getKeyCode() == KeyEvent.VK_UP || 
				e.getKeyCode() == KeyEvent.VK_W){
			
			player.up = false;
			
		}else if(e.getKeyCode() == KeyEvent.VK_DOWN || 
				e.getKeyCode() == KeyEvent.VK_S) {
			
			player.down = false;
			
		}
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		player.mouseShoot = true;
		player.mx = (e.getX()/3);
		player.my = (e.getY()/3);	
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	
	
}

