
package ucd.rubicon.cognitive;

public class GoalRecord extends GoalRequest implements Comparable<GoalRecord> {
		
	enum GOAL_STATUS {INITIALIZED, SUBMITTED, FAIL_SCHEDULE, ACTIVE, SUSPENDED, RESCHEDULING, FAILED_EXECUTION, COMPLETED};
	protected static GOAL_STATUS state;
		
	public GoalRecord(GoalRequest request) {
		super(request);
		state = (request.getTmIssued()==0) ? GOAL_STATUS.INITIALIZED : GOAL_STATUS.SUBMITTED;
	}
	
	
	public int compareTo(GoalRecord arg0) {
		final int BEFORE = -1;
	    final int EQUAL = 0;
	    final int AFTER = 1;

	    if ( this == arg0 ) return EQUAL;

	    if (this.getPriority() < arg0.getPriority()) return AFTER;
	    if (this.getPriority() > arg0.getPriority()) return BEFORE;
	    return EQUAL;
	}
	

}
