package me.sablednah.MobHealth;

import com.garbagemule.MobArena.Arena;
import com.garbagemule.MobArena.waves.BossWave;

public class getBossHealth implements Runnable {
	public Arena a;
	
	public getBossHealth(Arena arena) {
		this.a = arena;
	}

	@Override
	public void run() {
		BossWave bw = (BossWave) a.getWave();
		if (a.isBossWave()) {
			MobHealth.maBossHealthMax=bw.getHealth();
		}		
	}

}
