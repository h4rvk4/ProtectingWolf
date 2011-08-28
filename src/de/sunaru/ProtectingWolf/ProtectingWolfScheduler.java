package de.sunaru.ProtectingWolf;

public class ProtectingWolfScheduler implements Runnable {

	@Override
	public void run() {
		ProtectingWolfVictims victims = ProtectingWolfVictims.getInstance();
		victims.cleanUpDisputants();
	}
	
}
