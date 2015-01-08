package ucd.rubicon.cognitive;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class GoalRequest {
	
	protected static int    MAX_PRIORITY = 15;
	protected static int    DEFAULT_PRIORITY = 7;
	//protected static String REQUEST_POST = "post";
	
	protected String 	goalType;
	protected String 	goalKey;
	protected int 		priority;
	protected long    	tmIssued;
	protected long		tmStart;
	protected long    	tmDeadline;	
	protected Map<String, String>	 	properties;

	public GoalRequest(String goalKey, Map<String,String> properties) {
		this.goalKey = goalKey;
		this.goalType = "";
		this.priority = DEFAULT_PRIORITY;
		this.tmIssued = 0;
		this.tmStart = 0;
		this.tmDeadline = 0;
		this.properties = properties;
	}

	public GoalRequest(String goalKey) {
		this(goalKey, new HashMap<String, String>());
	}

	public GoalRequest(GoalRequest g) {
		this(g.getGoalKey(), g.getProperties());
		this.priority = g.getPriority();
		this.goalType = g.getGoalType();
		this.tmIssued = g.getTmIssued();
		this.tmStart = g.getTmStart();
		this.tmDeadline = g.getTmDeadline();		
	}	
	
	
	public String getGoalType() {
		return goalType;
	}
	
	public String getGoalKey() {
		return goalKey;
	}
	
	public void setGoalType(String goalType) {
		this.goalType = goalType;
	}
	
	public void setGoalKey(String goalKey) {
		this.goalKey = goalKey;
	}

	public int getPriority() {
		return priority;
	}
	
	public void setPriority(int priority) {
		this.priority = priority;
	}
	
	public long getTmStart() {
		return tmStart;
	}
	
	public void setTmIssued(long tmIssued) {
		this.tmIssued = tmIssued;
	}

	public void setTmStart(long tmStart) {
		this.tmStart = tmStart;
	}
	
	public long getTmDeadline() {
		return tmDeadline;
	}
	
	public void setTmDeadline(long tmDeadline) {
		this.tmDeadline = tmDeadline;
	}
	
	public Map<String, String> getProperties() {
		return properties;
	}
	
	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}
	
	public void setProperty(String propertyName, String propertyValue) {
		properties.put(propertyName, propertyValue);
	}

	public long getTmIssued() {
		return tmIssued;
	}
	
	public void print() {
		System.out.println(goalKey+":"+goalType+"("+properties+")"
				+" issued:"+ ((tmIssued == 0) ? "no" : tmIssued)
				+" start:"+ ((tmStart == 0) ? "no" : tmStart)
				+" deadline:"+ ((tmDeadline == 0) ? "no" : tmDeadline));
	}
	
	
}
