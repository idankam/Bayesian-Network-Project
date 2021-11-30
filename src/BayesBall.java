import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

public class BayesBall {
    private static int fromPARENT = -1;
    private static int fromKID = 1;
    private static int FIRST = 0;

    private BayesianNetwork _net;
    private Hashtable _isFoundFromKid;
    private Hashtable _isFoundFromParent;
    private Hashtable _isGiven;
    private BayesianNode _start;
    private BayesianNode _goal;
    private Boolean _isIndependent; //checked later and will change if needed

    public BayesBall(BayesianNetwork net, String line){
        String start = get_start_value(line);
        String goal = get_goal_value(line);
        ArrayList<String> given = get_given_values(line);

        set_net(net);
        set_start((BayesianNode) net.get_nodes().get(start));
        set_goal((BayesianNode) net.get_nodes().get(goal));

        _isGiven = new Hashtable();
        _isFoundFromKid = new Hashtable();
        _isFoundFromParent = new Hashtable();
        Enumeration keys = net.get_nodes().keys();

        while (keys.hasMoreElements()){
            String name = (String) keys.nextElement();
            _isGiven.put(name, false);
            _isFoundFromKid.put(name, false);
            _isFoundFromParent.put(name, false);
        }
        for(String name : given){
            _isGiven.replace(name, true);
        }

        _isIndependent = true; //initialize
        if(!isFirstOrGoalAreGiven()){ // if one of them is given - they are not dependence
            runAlgo(this._start, FIRST); // if there is dependency: _isIndependent = false
        }
    }

    private ArrayList<String> get_given_values(String line) {
        ArrayList<String> given = new ArrayList<>();
        String[] task_and_given = line.split("\\|");
        if(task_and_given.length > 1){
            String[] given_values = task_and_given[1].split(",");
            for( String given_value : given_values){
                given.add(given_value.split("=")[0]);
            }
        }
        return given;
    }

    private String get_goal_value(String line) {
        String goal_value = line.split("\\|")[0].split("-")[1];
        return goal_value;
    }

    private String get_start_value(String line) {
        String start_value = line.split("\\|")[0].split("-")[0];
        return start_value;
    }

    public BayesBall(BayesianNetwork net, String start, String goal, ArrayList<String> given){

        set_net(net);
        set_start((BayesianNode) net.get_nodes().get(start));
        set_goal((BayesianNode) net.get_nodes().get(goal));

        _isGiven = new Hashtable();
        _isFoundFromKid = new Hashtable();
        _isFoundFromParent = new Hashtable();
        Enumeration keys = net.get_nodes().keys();

        while (keys.hasMoreElements()){
            String name = (String) keys.nextElement();
            _isGiven.put(name, false);
            _isFoundFromKid.put(name, false);
            _isFoundFromParent.put(name, false);
        }
        for(String name : given){
            _isGiven.replace(name, true);
        }

        _isIndependent = true; //initialize
        if(!isFirstOrGoalAreGiven()){ // if one of them is given - they are not dependence
            runAlgo(this._start, FIRST); // if there is dependency: _isIndependent = false
        }
    }

    public void set_start(BayesianNode _start) {
        this._start = _start;
    }

    public void set_goal(BayesianNode _goal) {
        this._goal = _goal;
    }

    public void set_net(BayesianNetwork _net) {
        this._net = _net;
    }

    public void runAlgo(BayesianNode start, int from){
        boolean isGiven = (boolean) this._isGiven.get( start.get_variable().get_name());

        // already found dependency
        if(this._isIndependent == false){
            return;
        }
        // reached to goal = there is dependency
        else if (start.get_variable().get_name().equals(_goal.get_variable().get_name())) {
            this._isIndependent = false;
            return;
        }
        //algo for first node
        else if (from == FIRST){
            goToParents(start);
            goToKids(start);
        }
        // algo for node from kid which is not given:
        else if (from == fromKID && !isGiven) {
            goToParents(start);
            goToKids(start);
        }
        // algo for node from parent which is not given:
        else if (from == fromPARENT && !isGiven) {
            goToKids(start);
        }
        // algo for node from parent which is given:
        else if (from == fromPARENT && isGiven) {
            goToParents(start);
        }
    }

    // go to parent if not given
    private void goToParents(BayesianNode node){
        for (BayesianNode parent: node.get_parents()) {
            String parentName = parent.get_variable().get_name();
            if((Boolean)this._isGiven.get(parentName) == false  &&  (boolean) _isFoundFromKid.get(parentName) == false){
                _isFoundFromKid.replace(parentName, true);
                runAlgo(parent, fromKID);
            }
        }
    }

    // go to all kids
    private void goToKids(BayesianNode node){
        for (BayesianNode kid : node.get_kids()){
            String kidName = kid.get_variable().get_name();
            if ((boolean) _isFoundFromParent.get(kidName) == false) {
                _isFoundFromParent.replace(kidName, true);
                runAlgo(kid, fromPARENT);
            }
        }
    }

    private boolean isFirstOrGoalAreGiven(){
        boolean start_isGiven = (boolean) this._isGiven.get( (String) this._start.get_variable().get_name());
        boolean goal_isGiven = (boolean) this._isGiven.get( (String) this._goal.get_variable().get_name());
        if (start_isGiven || goal_isGiven)
            return true;
        else
            return false;
    }

    public Boolean get_isIndependent() {
        return _isIndependent;
    }
}
