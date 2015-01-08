package ucd.rubicon.cognitive;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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
import core.PeisTupleResultSet;
import core.PeisJavaInterface.PeisSubscriberHandle;
import core.PeisJavaMT.FLAG;

public class MockCognitiveLayer {

		protected PeisSubscriberHandle hndlFeedback;
		protected CallbackObject callback;
		static protected ExecutorService executor = Executors.newCachedThreadPool();
		static String SPACE = " ";
		static String DOT = ".";	
		
		public MockCognitiveLayer() {
			hndlFeedback = PeisJavaMT.peisjava_subscribe(PeisJavaMT.peisjava_peisid(), CognitiveInterface.FEEDBACK_KEY_TUPLE+".*");
			System.out.println("MockCognitiveLayer ready to subit goals and receive feedback");
		
			// 	register a callback to monitor changes in the goals posted by the Cognitive Layer
			PeisJavaMT.peisjava_registerTupleCallback(
				PeisJavaMT.peisjava_peisid(),
				CognitiveInterface.FEEDBACK_KEY_TUPLE,
				new CallbackObject() {
					@Override
					public void callback(PeisTuple tuple) {
						if (tuple.getStringData() != null && !tuple.getStringData().equals("") && !tuple.getStringData().equals("()")) {
							//final String goal = tuple.getStringData();								      
							System.out.println("MockCognitiveLayer received feedback: "+tuple.getStringData());
						}
					}
				} //End CallbackObject definition
			);
		
		}


		public void test(final int testNumber) throws Exception {
			executor.submit(
					new Callable() {
						public Object call() {
							try {
								switch (testNumber) {
								case 1: _test1(); break;
								}									
		    				} catch(Throwable t) {
		    					t.printStackTrace();
		    					//TODO log
		    				}
							return null;					    					 
						}
					});
		}
		
		protected void postGoal(String goalKey, String goalType, int startOffsetSec, int deadlineOffsetSec, String args, int priority) {
			//returns the number of milliseconds since epoch, i.e. since midnight UTC on the 1st January 1970.
			//long now = System.currentTimeMillis(); 
			long now  = PeisJavaMT.peisjava_gettime();
			
			long timeDeadline = now + deadlineOffsetSec;
			long timeStart = now + startOffsetSec;
			
			
			String goal =
				""+now+ SPACE +
				goalType + SPACE +
				args + SPACE +
				priority + SPACE +
				timeDeadline + SPACE +
				timeStart + SPACE;								

			System.out.println("Mockup cognitive posting "+ goal);
			PeisJavaMT.peisjava_setStringTuple(CognitiveInterface.GOAL_KEY_TUPLE+DOT+goalKey, goal);
		}
		
		protected void _test1() throws InterruptedException {
			
			System.out.println("Waiting 2 seconds before posting goals");
			Thread.sleep(10000);
			
			//postGoal(goalKey, goalType, startOffsetSec, deadlineOffsetSec, args, priority)
			System.out.println("Posting TV_OFF to start in 30 seconds");
			postGoal("TV_ON", "1", 5, 500, "nothing_particular", 10);
			
			System.out.println("Waiting 10 seconds before posting TV_ON");
			Thread.sleep(10000);

			System.out.println("Posting TV_ON goal to start in 10 seconds");
			postGoal("CLEAN", "2", 30, 500, "nothing_particular", 10);
			

			//System.out.println("Posting CLEAN goal to start in 30 seconds");
			//postGoal("CLEAN", "2", 30, 1000, "KITCHEN", 10);

		}

}
