public class Supervisor extends MyObject implements Runnable {

	private int id = 0;
	private int rNap = 0; // milliseconds
	private RechargingStation rs = null;
	private boolean tracks[]=new boolean[3];
	   public void wait(Object object) {
	    	try {
				object.wait();
			} 
	    	
	    	catch (InterruptedException e) {
				e.printStackTrace();
			}
	    }
	
	
	public Supervisor(String name, int id, int rNap, RechargingStation rs) {
		super(name);
		this.id = id;
		this.rNap = rNap;
		this.rs = rs;
		for(int ind =0;ind <3; ind++) {
			tracks[ind]=false;
		}
		new Thread(this).start();
	}
	public int assingTrack() {
		for(int i1=0;i1<3;i1++) {
			if(!tracks[i1]) {;
				tracks[i1]=true;
				return i1+1;
			}
		}
		return 0;
	}
	public void releaseTrack(int pos ) {
		tracks[pos]=false;
	}

	
	@Override
	public void run() {
		int napping;
		while(rs.waitingToRefill.size()>0 || rs.openStation) {
			if(rs.waitingfortakeOff.size()==0) {
				synchronized (rs.flightNotification) {
					msg("No one is waiting.");
					wait(rs.flightNotification);
				}
			}
			int trackAvailable = assingTrack();
			if(trackAvailable==0) {
				synchronized (rs.takeOffNotification) {
					msg("There are no empty Tracks.");
					wait(rs.takeOffNotification);
				}
			}
			msg(rs.giveTrack(trackAvailable)+ "Move to Track " + trackAvailable);
			releaseTrack(trackAvailable-1);

		}
	}
	
	public void msg(String m) {
		System.out.println("[age=" + age() + ", " + getName() + "]: " + m);
	};
}
