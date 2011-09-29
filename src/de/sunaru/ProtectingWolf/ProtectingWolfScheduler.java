package de.sunaru.ProtectingWolf;

public class ProtectingWolfScheduler implements Runnable {
	
	public static ProtectingWolf plugin;

	public ProtectingWolfScheduler(ProtectingWolf instance) {
		plugin = instance;
	}

	@Override
	public void run() {
		ProtectingWolfVictims victims = ProtectingWolfVictims.getInstance();
		victims.cleanUpDisputants();
	}
}