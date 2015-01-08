package ucd.rubicon.cognitive;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;


public class GoalParserCognitiveStyle {	
	protected static String PARAMETERS = "PARAMETERS";
					
	public static GoalRequest parse(String goalKey, String request) throws Exception {		 		
	    
	    Map<String,String> properties = new HashMap<String, String>();
	    
	    StringTokenizer st = new StringTokenizer(request," ");
	    GoalRequest goalRequest = new GoalRequest(goalKey, properties);
	    goalRequest.setTmIssued((int)Double.parseDouble(st.nextToken()));
	    goalRequest.setGoalType(st.nextToken());
	    properties.put(PARAMETERS, st.nextToken());
	    goalRequest.setPriority(Integer.parseInt(st.nextToken()));	    				
	    goalRequest.setTmDeadline((int)Double.parseDouble(st.nextToken()));
	    goalRequest.setTmStart((int)Double.parseDouble(st.nextToken()));
	    return goalRequest;	    
	}
	
}
