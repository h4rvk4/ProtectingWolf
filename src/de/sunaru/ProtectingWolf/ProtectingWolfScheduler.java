package de.sunaru.ProtectingWolf;

public class ProtectingWolfScheduler implements Runnable {
	
	public static ProtectingWolf plugin;

	public ProtectingWolfScheduler(ProtectingWolf instance) {
		plugin = instance;
	}

	@Override
	public void run() {
		System.out.println("starting");
		ProtectingWolfVictims victims = ProtectingWolfVictims.getInstance();
		victims.cleanUpDisputants();
		victims = null;
		System.out.println("stoping");
		System.out.println("active workers:" + plugin.getServer().getScheduler().getActiveWorkers().size());
	}
	
}
