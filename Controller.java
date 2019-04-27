public class Controller extends MyObject implements Runnable {

	private int id = 0;
	private int rNap = 0; // milliseconds
	private RechargingStation rs = null;
	
    public void wait(Object object) {
    	try {
			object.wait();
		} 
    	
    	catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
    	
	public Controller(String name, int id, int rNap, RechargingStation rs) {
		super(name + id);
		this.id = id;
		this.rNap = rNap;
		this.rs = rs;
		new Thread(this).start();
	}
	public void refillTank(Object r) {
		int napp = 200+rs.tankTotal;
		rs.tankTotal=200;
		msg("Refilling tank. It will take " +napp );
		nap(napp);
		msg("Tank is full");
	}
	@Override
	public void run() {
		while(rs.waitingToRefill.size()>0 || rs.openStation) {
		int napping;
		napping = rNap;
		msg("Recharging station will open in  " + napping);
		nap(napping);
		msg("Recharging station is now open");
		rs.moveToRechargingArea();
		synchronized (rs.rechargingRoomIsEmpty) {	
			synchronized (rs.refillTank) {	
				wait(rs.refillTank);
			}
			msg("refilling Tank");
			wait(rs.rechargingRoomIsEmpty);
		}
		msg("Cleaning Up and refelling");
		}
	}
	
	public void msg(String m) {
		System.out.println("[age=" + age() + ", " + getName() + "]: " + m);
	};

}
