package ucd.rubicon.cognitive;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;


public class GoalParserArgStyle {	
	protected static String PAR_PRIORITY = "priority";
	protected static String PAR_START = "start";
	protected static String PAR_TIME_ISSUED = "issued";
	protected static String PAR_GOAL_TYPE = "type";
	protected static String PAR_DEADLINE = "deadline";
		
			
	public static GoalRequest parse(String goalKey, String request) throws Exception {
		 		
	    Scanner s = new Scanner(request);
	    String warning = "";
	    Map<String,String> properties = new HashMap<String, String>();	    
	    	    
	    while (s.hasNext()) {
	    	String par = s.next();
	    	if (!par.startsWith("-")) throw new Exception("Wrong argument name ("+par.substring(1)+")"); 	    		
	    	String parName = par.substring(1);
	    	if (!s.hasNext()) throw new Exception("Missing value for argument "+parName);	    		
	    	properties.put(parName, s.next());
	    }
	    
	    
	    GoalRequest goalRequest = new GoalRequest(goalKey, properties);
	    
	    String strGoalType = properties.remove(PAR_GOAL_TYPE);
	    goalRequest.setGoalType(strGoalType);
	    
	    String strPriority = properties.remove(PAR_PRIORITY);	    			
	    if (strPriority != null) {
	    	goalRequest.setPriority(Integer.parseInt(strPriority));	    				
	    }	
	    
	    //TODO: handles optional values
	    goalRequest.setTmIssued(Long.parseLong(properties.remove(PAR_TIME_ISSUED)));
	    goalRequest.setTmStart(Long.parseLong(properties.remove(PAR_START)));
	    goalRequest.setTmDeadline(Long.parseLong(properties.remove(PAR_DEADLINE)));
	    		
	    return goalRequest;	    		
	}
	
}
