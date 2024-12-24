package com.luizdev.main;

import java.applet.Applet;
import java.applet.AudioClip;

public class Sound {

	private AudioClip clip;
	
	public static final Sound musicBackground = new Sound("/music.wav");
	public static final Sound hurtEffect = new Sound("/hurt.wav");
	public static final Sound hurtEnemy = new Sound("/hurtEmeny.wav");
	public static final Sound pickupAmmo = new Sound("/pickupAmmo.wav");
	public static final Sound pickupLife = new Sound("/pickupLife.wav");
	public static final Sound shoot = new Sound("/shoot.wav");
	public static final Sound pickupGun = new Sound("/pickupGun.wav");
	public static final Sound enemyDie = new Sound("/enemyDie.wav");
	
	
	private Sound(String name) {
		try {
			clip = Applet.newAudioClip(Sound.class.getResource(name));
		}catch(Throwable e){
			
		}
	}
	
	public void play() {
		try {
			new Thread () {
				public void run() {
					clip.play();
				}
			}.start();
		}catch (Throwable e) {
		
		}
	}
	
	public void loop() {
		try {
			new Thread () {
				public void run() {
					clip.loop();
				}
			}.start();
		}catch (Throwable e) {
		
		}
	}
	
}
