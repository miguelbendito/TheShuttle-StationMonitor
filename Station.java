public class Station extends MyObject {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// parse command line arguments, if any, to override defaults
		GetOpt go = new GetOpt(args, "UE:W:e:w:R:");
		System.out.println(go);
		go.optErr = true;
		String usage = "Usage: -E numR -W numW -e rNap -w wNap -R runTime";
		
		int numShuttles;
		
		if(args.length>0)
			numShuttles = Integer.parseInt(args[0]);
		else
			numShuttles = 7;//default value
		
		
		int rNap = 2; // defaults
		int runTime = 40; // seconds
		
		RechargingStation rs = new RechargingStation(runTime);

		for (int i = 0; i < numShuttles; i++)
			new Shuttle("Shuttle", i, rNap * 1500, rs);

		new Controller("Controller", 1, 3000, rs);

		new Supervisor("Flight_Supervisor", 1, rNap * 3000, rs);
		nap(runTime * 1000);
	}
}
