package ucd.rubicon.cognitive;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import core.CallbackObject;
import core.PeisJavaMT;
import core.PeisTuple;


import core.CallbackObject;
import core.PeisJavaMT;
import core.PeisJavaMT.FLAG;
import core.PeisTuple;
import core.PeisJavaInterface.PeisSubscriberHandle;
import core.PeisTupleResultSet;


public class CognitiveInterface {

	protected PeisSubscriberHandle hndlReqGoal;
	protected PeisSubscriberHandle hndlParUsePlan;
	protected CallbackObject callback;
	public static String GOAL_KEY_TUPLE="COGL2CNL";
	public static String FEEDBACK_KEY_TUPLE="CNL2COGL";
	public static String PAR_USECSP_KEY_TUPLE="PAR_USECSP";	
	
	public static String PAR_GOAL_STYLE="PAR_GOAL_STYLE";
	public static String PAR_GOAL_STYLE_ARG="ARG";
	public static String PAR_GOAL_STYLE_COG="COG";
	public static String PAR_GOAL_STYLE_TUPLE="TUPLE";
	
	protected boolean bUseCSP = false;
	protected String parGoalStyle = PAR_GOAL_STYLE_COG; //PAR_GOAL_STYLE_TUPLE;
	
	protected PriorityQueue<GoalRecord> _queueGoals = new PriorityQueue<GoalRecord>();
	protected Map<String, GoalRecord> mapGoals = new HashMap<String, GoalRecord>();
	protected int goalCounter = 0;
	
	static protected ExecutorService executor = Executors.newCachedThreadPool();
	
	public CognitiveInterface() {
		hndlReqGoal = PeisJavaMT.peisjava_subscribe(-1, GOAL_KEY_TUPLE+".*");
		hndlParUsePlan = PeisJavaMT.peisjava_subscribe(PeisJavaMT.peisjava_peisid(), PAR_USECSP_KEY_TUPLE);
		System.out.println("Control Layer Interface ready to process goals");
		PeisJavaMT.peisjava_setStringTuple(PAR_USECSP_KEY_TUPLE, "false");
	
		// 	register a callback to monitor changes in the goals posted by the Cognitive Layer
		PeisJavaMT.peisjava_registerTupleCallback(
			-1,
			GOAL_KEY_TUPLE+".*",
			new CallbackObject() {
				@Override
				public void callback(final PeisTuple tuple) {
					System.out.println("Callback for goals, tuple is "+ tuple);
					if (tuple.getStringData() != null && !tuple.getStringData().equals("") && !tuple.getStringData().equals("()")) {
						//final String goal = tuple.getStringData();								      
						System.out.println("Control Layer Interface processing updated goals");
						
						executor.submit(
								new Callable() {
									public Object call() {
										try {
											processGoals(tuple.creator);
					    				} catch(Throwable t) {
					    					t.printStackTrace();
					    					//TODO log
					    				}
										return null;					    					 
									}
								});
					}
				}
			} //End CallbackObject definition
		);

		/*
		PeisJavaMT.peisjava_registerTupleCallback(
			PeisJavaMT.peisjava_peisid(),
			PAR_USECSP_KEY_TUPLE,
			new CallbackObject() {
				@Override
				public void callback(PeisTuple tuple) {
					if (tuple.getStringData() != null && !tuple.getStringData().equals("") && !tuple.getStringData().equals("()")) {
						String useCSP = tuple.getStringData();						
						bUseCSP = (useCSP.equalsIgnoreCase("TRUE"));
						System.out.println("Control Interface received "+PAR_USECSP_KEY_TUPLE+"("+useCSP+"): using csp = "+bUseCSP);
					}
				}
			} //End CallbackObject definition
		);

		PeisJavaMT.peisjava_registerTupleCallback(
				PeisJavaMT.peisjava_peisid(),
				this.PAR_GOAL_STYLE,
				new CallbackObject() {
					@Override
					public void callback(PeisTuple tuple) {
						if (tuple.getStringData() != null && !tuple.getStringData().equals("") && !tuple.getStringData().equals("()")) {
							parGoalStyle = tuple.getStringData();						
							System.out.println("Control Interface set to use "+parGoalStyle + " goal style");
						}
					}
				} //End CallbackObject definition
			);
			*/
	
	}


	protected void updateGoal(GoalRecord goalRecord) {
		System.out.println("Processing goal :"+goalRecord.getGoalKey()+" start="+goalRecord.getTmStart());
		goalRecord.print();

		long now = PeisJavaMT.peisjava_gettime();
		long delay = goalRecord.getTmStart() - now;
		long timeStart = -1;
		
		if (delay > 10) {
			timeStart =goalRecord.getTmStart();
		}

		if (delay < 0) {
			return;
		}
		
		// goal types can be TV_ON or CLEAN
		GoalRecord oldRecord = mapGoals.get(goalRecord.getGoalKey());
		if (oldRecord != null) {
			// TODO, check this, what if the demo wants multiple runs?
			System.out.println("Goal was already submitted");
		} else {
			goalCounter++;
			PeisJavaMT.peisjava_setStringTuple("service."+goalCounter+".request.type", goalRecord.getGoalKey());
			PeisJavaMT.peisjava_setStringTuple("service."+goalCounter+".request.args", "nothing_particular");
			PeisJavaMT.peisjava_setStringTuple("service."+goalCounter+".request.requester", "COGNITIVE_LAYER");
			PeisJavaMT.peisjava_setStringTuple("service."+goalCounter+".request.state","ON");
			
			System.out.println("Now        is "+ now);
			System.out.println("Delay      is "+ delay);
			System.out.println("Time Start is "+ timeStart);
			PeisJavaMT.peisjava_setStringTuple("service."+goalCounter+".request.start", ""+timeStart);
			PeisJavaMT.peisjava_setStringTuple("service."+goalCounter+".request.dead","-1");
			mapGoals.put(goalRecord.getGoalKey(), goalRecord);
		}
	}
	
	public void processGoals(int creator) throws Exception {

		PeisTupleResultSet result = PeisJavaMT.peisjava_getTuples(creator, GOAL_KEY_TUPLE+".*", FLAG.BLOCKING);
		PeisTuple res = null;
		
		if (this.parGoalStyle.equalsIgnoreCase(this.PAR_GOAL_STYLE_ARG) ||
			this.parGoalStyle.equalsIgnoreCase(this.PAR_GOAL_STYLE_COG)) {
					
			while ((res = result.next()) != null) {							
				String data = res.getStringData();							
				String key = res.getKey(); 
				if (!key.startsWith(CognitiveInterface.GOAL_KEY_TUPLE+".")) {
					System.out.println("CognitiveInterface, invalid key prefix ("+key+") expected ("+CognitiveInterface.GOAL_KEY_TUPLE+")");
					continue; // ignore this goal
				}
				key = key.substring(CognitiveInterface.GOAL_KEY_TUPLE.length()+1);
				System.out.println("In CognitiveInterface.process goal("+key+" "+data+")");
				GoalRequest goalRequest;
				if (this.parGoalStyle.equalsIgnoreCase(this.PAR_GOAL_STYLE_ARG)) {
					goalRequest = GoalParserArgStyle.parse(key, data);
				} else {
					goalRequest = GoalParserCognitiveStyle.parse(key, data);
				}
				GoalRecord goalRecord = new GoalRecord(goalRequest);
				updateGoal(goalRecord);				
			}
		} else {
			GoalRequest current = null;
			String currentKey = "";
			while ((res = result.next()) != null) {			
				String data = res.getStringData();
				String keys[] = res.getKey().split(".");
				String key = keys[1];
				String field = keys[2];
				if (!currentKey.equals(key)) {
					if (current!=null) {
						updateGoal(new GoalRecord(current));
					}
					current = new GoalRequest(currentKey = key, new HashMap<String, String>());					
				}
				
				if (field.equalsIgnoreCase(GoalParserArgStyle.PAR_PRIORITY)) {
					current.setPriority(Integer.parseInt(data));
				} else if (field.equalsIgnoreCase(GoalParserArgStyle.PAR_TIME_ISSUED)) {
					current.setTmIssued(Long.parseLong(data));
				}  else if (field.equalsIgnoreCase(GoalParserArgStyle.PAR_START)) {
					current.setTmStart(Long.parseLong(data));
				}  else if (field.equalsIgnoreCase(GoalParserArgStyle.PAR_DEADLINE)) {
					current.setTmDeadline(Long.parseLong(data));
				}  else if (field.equalsIgnoreCase(GoalParserArgStyle.PAR_GOAL_TYPE)) {
					current.setGoalType(data);
				}
			}
			
			if (current!=null) {
				updateGoal(new GoalRecord(current));
			}

		}
		
		System.out.println("Goal Requests");
		System.out.println("-----------------------------------------");
		for (GoalRequest req: _queueGoals) {
			req.print();
		}
			
	}

}

