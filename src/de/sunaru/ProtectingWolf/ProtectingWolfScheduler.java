package de.sunaru.ProtectingWolf;

public class ProtectingWolfScheduler implements Runnable {
	
	public static ProtectingWolf plugin;
	
	private long cleanUpNow = 0;
	private long cleanUpMax = 30;
	private long respawnCheckNow = 0;
	private long respawnCheckMax = 1;

	public ProtectingWolfScheduler(ProtectingWolf instance) {
		plugin = instance;
	}

	@Override
	public void run() {
		cleanUpNow++;
		if (cleanUpNow == cleanUpMax) {
			ProtectingWolfVictims.getInstance().cleanUpDisputants();
			cleanUpNow = 0;
		}
		
		respawnCheckNow++;
		if (respawnCheckNow == respawnCheckMax) {
			ProtectingWolfRespawn.getInstance().respawnWolves();
			respawnCheckNow = 0;
		}
	}
}
