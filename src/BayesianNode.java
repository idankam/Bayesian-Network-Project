import java.util.ArrayList;

public class BayesianNode {
    private Variable _variable;
    private Factor _factor;
    private ArrayList<BayesianNode> _parents;
    private ArrayList<BayesianNode> _kids;

    public BayesianNode(Variable variable){
        set_variable(variable);
        this._parents = new ArrayList<>();
        this._kids = new ArrayList<>();
    }
    public Factor get_factor() {
        return _factor;
    }

    public Variable get_variable() {
        return _variable;
    }

    public ArrayList<BayesianNode> get_kids() {
        return _kids;
    }

    public ArrayList<BayesianNode> get_parents() {
        return _parents;
    }

    public void set_factor(Factor _factor) {
        this._factor = _factor;
    }

    public void set_variable(Variable _variable) {
        this._variable = _variable;
    }

    public void add_kid(BayesianNode kid) {
        this._kids.add(kid);
    }

    public void add_parent(BayesianNode parent) {
        this._parents.add(parent);
    }

    @Override
    public String toString() {
        String parents = "";
        String kids = "";
        String factor = "";

        for (BayesianNode parent : _parents){
            parents += parent.get_variable().get_name() + ",";}

        for (BayesianNode kid : _kids){
            kids += kid.get_variable().get_name() + ",";}

        for (Variable v : _factor.get_variables()){factor+= v.get_name()+",";}
        factor+=": "+_factor.get_probabilities().toString();

        return "BayesianNode{" +
                "_variable=" + _variable.toString() +
                ", _factor=" + factor +
                ", _parents=" + parents +
                ", _kids=" + kids +
                '}'+"\n\n";
    }
}
