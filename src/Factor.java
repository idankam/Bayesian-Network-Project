import java.util.ArrayList;
import java.util.Arrays;

public class Factor {
    private ArrayList<Variable> _variables;
    private ArrayList<Double> _probabilities;
    private ArrayList<String> _probsIndexes;

    public Factor(ArrayList<Variable> variables, ArrayList<Double> prob){
        set_probabilities(prob);
        set_variables(variables);
        set_probsIndexes();
    }

    public Factor(ArrayList<Variable> variables, ArrayList<Double> prob, ArrayList<String> probsIndexes){
        set_probabilities(prob);
        set_variables(variables);
        set_probsIndexes(probsIndexes);
    }

    public ArrayList<Variable> get_variables() {
        return _variables;
    }

    public ArrayList<String> get_variables_names() {
        ArrayList<String> names = new ArrayList<String>();
        for (Variable var : this._variables){
            names.add(var.get_name());
        }
        return names;
    }

    public static ArrayList<String> get_variables_names(ArrayList<Variable> vars) {
        ArrayList<String> names = new ArrayList<String>();
        for (Variable var : vars) {
            names.add(var.get_name());
        }
        return names;
    }

    public ArrayList<Double> get_probabilities() {
        return _probabilities;
    }

    public ArrayList<String> get_probsIndexes() { return _probsIndexes; }

    public void set_probabilities(ArrayList<Double> _probabilities) {
        this._probabilities = new ArrayList<>();
        for (Double value: _probabilities){
            this._probabilities.add(value);
        }
    }

    public void set_variables(ArrayList<Variable> _variables) {
        this._variables = new ArrayList<>();
        for (Variable var: _variables){
            this._variables.add(var.copy());
        }
    }

    private void set_probsIndexes(ArrayList<String> probsIndexes) {
        this._probsIndexes = new ArrayList<>();
        for (String value: probsIndexes){
            this._probsIndexes.add(value);
        }
    }

    // this method initialize the indexes (values of vars) in each row in the factor.
    private void set_probsIndexes() {
        String[] probIndexes = new String[this._probabilities.size()];
        Variable last_var = this._variables.get(this._variables.size()-1);
        String[] values = last_var.get_values().toArray(new String[0]);
        int value_index = 0;
        for (int i = 0; i<probIndexes.length; i++){
            probIndexes[i] = values[value_index];
            value_index = (value_index+1) % values.length;
        }

        if(this._variables.size() > 1){
            int var_index = this._variables.size()-2;
            while( var_index >= 0){
                String first_str = probIndexes[0];
                Variable var = this._variables.get(var_index);
                values = var.get_values().toArray(new String[0]);
                value_index = 0;
                for (int i = 0; i<probIndexes.length; i++){
                    if(probIndexes[i].equals(first_str)){
                        probIndexes[i] = values[value_index] + "," + probIndexes[i];
                        value_index = (value_index+1) % values.length;
                    }
                    else{
                        int previous_value_index = (value_index-1)%values.length;
                        if (previous_value_index<0) { previous_value_index+=values.length; }
                        probIndexes[i] = values[previous_value_index] + "," + probIndexes[i];
                    }
                }
                var_index--;
            }
        }

        this._probsIndexes = new ArrayList<>(Arrays.asList(probIndexes));
    }

    public Factor copy(){
        Factor copy_factor = new Factor(this.get_variables(), this.get_probabilities(), this.get_probsIndexes());
        return copy_factor;
    }

    // remove rows (from factor) which not contain the specified value of the given variable:
    public static Factor refactor_by_value(Factor f, Variable var, String value){
        // make a copy for not change the original factor:
        Factor new_factor = f.copy();
        ArrayList<Double> probs = new_factor.get_probabilities();
        ArrayList<String> probs_indexes = new_factor.get_probsIndexes();
        ArrayList<Variable> vars = new_factor.get_variables();

        // make a regex for check if row include the specific value of the given variable:
        String regex = "";
        for (int i = 0; i<vars.size(); i++){
            if(vars.get(i).get_name().equals(var.get_name())){
                regex += value + ",";
            }
            else{
                regex += "(.*)" + ",";
            }
        }
        regex = regex.substring(0, regex.length()-1);

        // remove rows which is not match the regex
        for(int i=0; i<probs.size(); ){
            if(!probs_indexes.get(i).matches(regex)){
                probs.remove(i);
                probs_indexes.remove(i);
            }
            else {
                i++;
            }
        }

        // remove the given variable from the variables list
        for(int i=0; i<vars.size(); i++){
            if(vars.get(i).get_name().equals(var.get_name())){
                vars.remove(i);
                break;
            }
        }

        // create and return new factors after the changes (after remove rows and given variable):
        Factor f_refactor = new Factor(vars, probs);
        return f_refactor;
    }

    @Override
    public String toString() {
        String str =  "Factor:\n" +
                "_variables=" + _variables.toString()+"\n";
        for(int i=0; i<_probabilities.size();i++)
            str+= _probsIndexes.get(i) + " | " + _probabilities.get(i) + "\n";

        return str;
    }
}
