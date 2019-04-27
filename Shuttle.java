public class Shuttle extends MyObject implements Runnable {

	private int id = 0;
	private int rNap = 0; // milliseconds
	private RechargingStation rs = null;
	public volatile int chargeNeed;
	public volatile int takeoffTrack = -1;
	
	public Object comunications = new Object();

	public Shuttle(String name, int id, int rNap, RechargingStation rs) {
		super(name + "_" + (id + 1));
		this.id = id + 1;
		this.rNap = rNap;
		this.rs = rs;
		new Thread(this).start();

	}

	public void wait(Object object) {
		try {
			object.wait();
		}

		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while (rs.openStation) {
			int napping;
			// while (true) {
			napping = 1 + (int) random(rNap);
			msg("Cruising for :" + napping);
			nap(napping);
			rs.landed++;// notify you landed
			rs.formGroups(this);
			synchronized (rs.waitingForMore) {
				rs.waitingForMore.notify();
			}
			synchronized (comunications) {
				msg("Waiting for controller on landing platfrorm");
				wait(comunications);
			}
			chargeNeed = (1 + (int) random(50)) + 50;
			msg("My tank has " + (100 - chargeNeed) + "%");
			rs.startRecharge(this);
			synchronized (comunications) {
				msg("My Tank is Full");
				msg("Moving to takeoff area. Waiting for aircraft Supervisor instrunctions");
				wait(comunications);
			}
			rs.takeOff(takeoffTrack);
			rs.landed--;
			msg("Taking off from Track" + takeoffTrack);
		}
	}

	public void msg(String m) {
		System.out.println("[age=" + age() + ", " + getName() + "]: " + m);
	}

	public String getShuttleName() {
		return getName();
	}

}