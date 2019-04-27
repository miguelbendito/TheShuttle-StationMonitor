import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;


//monitor class
public class RechargingStation extends MyObject implements Runnable{


	int runtime = 1000;
	int numRecharge = 3;// Number of spaces in the recharging station.
	int takeOffTrack = 0;

	boolean openStation = true;
	boolean openTakeOff = false;


	public volatile int charging = 0;
	public volatile int landed = 0;
	public volatile int onTakeoff = 0;
	public volatile int tankTotal = 200;

	public Object refillTank = new Object();
	public Object rechargingRoomIsEmpty = new Object();
	public Object waitingForMore = new Object();
	public Object landingPlatform = new Object();
	public Object moveInside = new Object();
	public Object flightNotification = new Object();
	public Object takeOffNotification = new Object();
	
	public Queue<Shuttle> waitingToRefill = new LinkedList<Shuttle>();
	public Queue<Shuttle> waitingfortakeOff = new LinkedList<Shuttle>();

	public void wait(Object object) {
		try {
			object.wait();
		}

		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	public RechargingStation(int runtime){
		new Thread(this).start();
		this.runtime *=runtime;
	}
	public void moveToRechargingArea() {

		while (waitingToRefill.size() < numRecharge) {
			synchronized (waitingForMore) {
				msg("Waiting for " + (3 - waitingToRefill.size()) + "more shuttles");
				wait(waitingForMore);
			}
		}
		for (int i = 0; i < numRecharge; i++) {
			Object tmp = waitingToRefill.remove().comunications;
			charging++;
			synchronized (tmp) {
				tmp.notify();
			}
		}
	}

	public void formGroups(Shuttle t) {
		waitingToRefill.add(t);
	}

	public void startRecharge(Shuttle i) {
		tankTotal -= i.chargeNeed;
		if (tankTotal < i.chargeNeed) {

			synchronized (refillTank) {
				msg("Tank has not enough fuel");
				refillTank.notify();
			}
		}
		i.chargeNeed = 0;
		charging--;
		if (charging == 0) {
			synchronized (rechargingRoomIsEmpty) {
				rechargingRoomIsEmpty.notify();
			}
			synchronized (refillTank) {
				msg("" + charging);
				refillTank.notify();
			}
		}
		waitingfortakeOff.add(i);

		synchronized (i.comunications) {
			i.comunications.notify();
		}
		synchronized (flightNotification) {
			flightNotification.notify();
		}
	}

	public String giveTrack(int trackNumber) {
		String name = "";
		waitingfortakeOff.peek().takeoffTrack = trackNumber;
		name = waitingfortakeOff.peek().getShuttleName();
		synchronized (waitingfortakeOff.peek().comunications) {
			waitingfortakeOff.remove().comunications.notify();
		}
		return name;
	}

	public void takeOff(int track) {
		synchronized (takeOffNotification) {
			takeOffNotification.notify();
		}
		msg("track number " + track + " is free.");

	}

	public void msg(String m) {
		System.out.println("[age=" + age() + ", Main Station]: " + m);
	}

	@Override
	public void run() {
		while(age() != runtime) {
			//System.out.println(age());
		}
		openStation=false;
		numRecharge--;
		numRecharge--;
	}

	

}