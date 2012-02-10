package me.sablednah.MobHealth;

import com.garbagemule.MobArena.Arena;
import com.garbagemule.MobArena.MobArenaListener;
import com.garbagemule.MobArena.waves.Wave.WaveBranch;
import com.garbagemule.MobArena.waves.Wave.WaveType;

public class MobHealthArenaListener extends MobArenaListener {

    @Override
    public void onWave(Arena arena, int waveNumber, String waveName, WaveBranch waveBranch, WaveType waveType) {
    	if (waveType==WaveType.BOSS) {
    		// Apparently at this point although WaveType is BOSS - arena.getWave returns a default wave - not castable to BossWave
    		// So... lets hand over to a 1 tick scheduled task and get the health once all the Wave start stuff has finished :)
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new getBossHealth(arena), 1L);
		}
    }
}
