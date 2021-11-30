import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class VariableElimination {

    private BayesianNetwork _net;
    private String _query;
    private String _queryValue;
    private String[] _evidence;
    private String[] _evidenceValues;
    private ArrayList<String> _hidden;
    private String[] _relevant;
    private ArrayList<Factor> _factors;
    private int _additionCounter;
    private int _multiplicationCounter;
    private boolean _is_immediate;
    private String _answer;

    // initializing the data and run the algorithm
    public VariableElimination(BayesianNetwork net, String line){
        this._net = net;
        this._query = getQuery(line);
        this._queryValue = getQueryValue(line);
        this._evidence = getEvidence(line);
        this._evidenceValues = getEvidenceValues(line);
        this._hidden = getHidden(line);
        this._relevant = getRelevant();
        this._is_immediate = false;
        this._factors = get_first_factors();
        this._additionCounter = 0;
        this._multiplicationCounter = 0;
        this.run_algo();
    }

    // print data + factors at the moment
    // use this method for tracking the progress
    public void print_data(){
        System.out.println();
        System.out.println("query: " + _query+", value: " +_queryValue);
        System.out.print("evidences: ");
        for(String q :_evidence){System.out.print(q +",");}
        System.out.print(", values  : " );
        for(String q :_evidenceValues){System.out.print(q +",");}
        System.out.println();
        System.out.print("hidden:  ");
        for(String q :_hidden){System.out.print(q +",");}
        System.out.println();
        System.out.print("relevant:  ");
        for(String q :_relevant){System.out.print(q +",");};
        System.out.println();
        System.out.println("there are " + this._factors.size() + " factors.");
        System.out.println();
        for(Factor f : _factors) {
            System.out.println(f.toString());
        }
        System.out.println("add: " + this._additionCounter + " , multiply: " + this._multiplicationCounter);
    }

    // return the current factors list
    public ArrayList<Factor> get_factors() {
        return _factors;
    }

    // get the query variable from the text
    private String getQuery(String line){
        String q = line.substring(2);
        q = q.split("=")[0];
        return q;
    }

    // get the value of the query variable from the text
    private String getQueryValue(String line){
        String q = line.split("=")[1];
        String v = line.split("=")[1].split("\\|")[0];
        return v;
    }

    // get the evidence variables from the text
    private String[] getEvidence(String line){
        if(line.contains("|)")){
            return new String[0];
        }
        else {
            String[] evidences = line.split("\\|")[1].split("\\)")[0].split(",");
            String[] result = new String[evidences.length];
            for (int i = 0; i < evidences.length; i++) {
                result[i] = evidences[i].split("=")[0];
            }
            return result;
        }
    }

    // get the values of the evidence variables from the text
    private String[] getEvidenceValues(String line){
        if(line.contains("|)")){
            return new String[0];
        }
        else {
            String[] evidences = line.split("\\|")[1].split("\\)")[0].split(",");
            String[] result = new String[evidences.length];
            for (int i = 0; i < evidences.length; i++) {
                result[i] = evidences[i].split("=")[1];
            }
            return result;
        }
    }

    // get the hidden variables from the text
    private ArrayList<String> getHidden(String line){
        if(line.endsWith(")") || line.endsWith(" ")){
            ArrayList<String> result_list = new ArrayList<>();
            return result_list;
        }
        else{
            String[] result = line.split("\\) ")[1].split("-");
            ArrayList<String> result_list = new ArrayList<>(Arrays.asList(result));
            return result_list;
        }
    }

    // get only the relevant variables for the algorithm
    private String[] getRelevant(){

        // start with all the variables
        ArrayList<String> variables = this._net.getVariablesNames();
        ArrayList<String> not_relevant_variables = new ArrayList<>();
        for (String varName : variables){

            // query variable remains in the list
            if (varName.equals(this._query)){continue;}

            // evidence variable remains in the list
            ArrayList<String> evidenceVars = new ArrayList<>();
            if(this._evidence != null){
                evidenceVars = new ArrayList<>(Arrays.asList(this._evidence));
                if (evidenceVars.contains(varName)){continue;}
            }

            // nodes which is not ancestor variable (of query or evidence variables) not remains in the list
            if (isAncestor(varName) == false){
                not_relevant_variables.add(varName);
                continue;
            }

            // ancestor variables which is not dependant with the query variable not remains in the list
            BayesBall b = new BayesBall(this._net, varName, this._query, evidenceVars);
            if (b.get_isIndependent() == true){
                not_relevant_variables.add(varName);
                //variables.remove(varName);
            }
        }

        // remove all the not relevant vars from the list
        if(!not_relevant_variables.isEmpty()){
            for(String not_relevant_var : not_relevant_variables){
                variables.remove(not_relevant_var);
            }
        }

        // return the relevant variables
        String[] relevant = variables.toArray(new String[0]);
        return relevant;
    }

    // use this method in order to get only not relevant variables for the algorithm.
    // 1. start with all the variables
    // 2.remove every variable which is relevant
    // 3. return non_relevant
    private String[] getNotRelevant(){
        ArrayList<String> variables = this._net.getVariablesNames();
        for (String varName : this._relevant){
            variables.remove(varName);
        }
        String[] not_relevant = variables.toArray(new String[0]);
        return not_relevant;
    }

    // this method checks if variable node is an ancestor of query or evidence variables:
    private boolean isAncestor(String varName){
        Queue<BayesianNode> nextNodes = new LinkedList<>();
        BayesianNode firstNode = (BayesianNode) this._net.get_nodes().get(varName);
        nextNodes.add(firstNode);
        while (!nextNodes.isEmpty()){
            BayesianNode curr_node = nextNodes.remove();
            String name = curr_node.get_variable().get_name();
            ArrayList<String> evidenceVars=new ArrayList<>();
            if(this._evidence != null) {
                evidenceVars = new ArrayList<>(Arrays.asList(this._evidence));
            }
            if(name.equals(this._query) || evidenceVars.contains(name)){return true;}
            else if(!curr_node.get_kids().isEmpty()){
                for(BayesianNode kid: curr_node.get_kids()){nextNodes.add(kid);}
            }
        }
        return false;
    }

    // method task: this method initializing the factors list for starting the algorithm.
    // 1. the method return the factors of the relevant variables only.
    // 2. it removes factors with one values.
    // 3. the method remove unneeded rows from factors (by evidence values).
    // 4. it removes factors which contains variables which are not relevant (notice that if one none relevant
    //    variable is in the factor, it will be removed because all the variables will be none relevant also).
    private ArrayList<Factor> get_first_factors(){

        // make list of factors from all relevant nodes:
        ArrayList<Factor> factors = new ArrayList<>();
        for (String varName: this._relevant){
            factors.add(((BayesianNode) this._net.get_nodes().get(varName)).get_factor());
        }

        this._factors = factors;
        if(immediate_result_check()){
            return factors;
        }

        ArrayList<String> evidenceVars = new ArrayList<>();
        if(this._evidence != null){
            evidenceVars = new ArrayList<>(Arrays.asList(this._evidence));
        }

        // check if there are rows to remove in each factor (by evidence (given) variable):
        for (int i=0; i<factors.size();i++) {
            Factor f = factors.get(i);
            for (Variable var : f.get_variables()) {
                f = factors.get(i);
                if (evidenceVars.contains(var.get_name())) {

                    // if there is just one variable and it is in evidence,
                    // it means the factor will include just one row, in this case we can remove the factor.
                    if (f.get_variables().size()==1){
                        factors.remove(i);
                        i--;
                        break;
                    }
                    // otherwise, there is more than one variable (for now), so just remove the unneeded rows:
                    else
                    {
                        String var_value = this._evidenceValues[evidenceVars.indexOf(var.get_name())];
                        Factor refactor_f = Factor.refactor_by_value(f, var, var_value);
                        factors.set(i, refactor_f);
                    }
                }
            }
        }

        // remove factors which contains none relevant variable
        String[] non_relevant = this.getNotRelevant();
        for (int i=0; i<factors.size();i++) {
            Factor f = factors.get(i);
            for (Variable var : f.get_variables()) {
                if (Arrays.asList(non_relevant).contains(var.get_name())) {
                    factors.remove(i);
                    i--;
                    break;
                }
            }
        }
        return factors;
    }

    // this method run the Variable Elimination algorithm, at the end, it set the answer.
    // 0. if there is immediate answer from one of the factors, do not run. otherwise:
    // 1. while there is hidden vars to eliminate, do:
    // 1.1 get next var to eliminate
    // 1.2 Create a list of all the factors in which this variable appears
    // 1.3. if the list is empty, remove this var from the list of the hidden vars
    // 1.4 if the size of the list of factors is 1, eliminate the var from this factor.
    // 1.5 if the size of the list of factors is above 1, join 2 factors from the list (With the fewest lines).
    // 2. after elimination of all the hidden factors, if there is more than one factor remain,
    //    join them all (all this factors contain just the query factor).
    // 3. Make a normalization to the desired value and that is the answer.
    private void run_algo() {
        // 0.
        if(!this._is_immediate){
            // 1.
            while(this._hidden.size()>0){
                String next_hidden = this._hidden.get(0);
                ArrayList<Integer> indexes = get_indexes_of_factors(next_hidden); // indexes of factors with the first hidden variable
                // 1.3
                if(indexes.size()==0){
                    this._hidden.remove(0);
                    continue;
                }
                // 1.4
                else if(indexes.size()==1){
                    this.eliminate(indexes.get(0), next_hidden);
                    continue;
                }
                // 1.5
                else // indexes.size()>1
                {
                    int[] two_indexes = get_the_two_smallest_factors(indexes);
                    this.join_factors(next_hidden, two_indexes[0], two_indexes[1]);
                }
            }
            // 2
            while(this._factors.size()>1){
                this.join_factors(this._query, 0, 1);
            }
            // 3.
            double result = get_result_from_last_factor();
            result = round(result, 5);
            String ans = result + "," + this._additionCounter + "," + this._multiplicationCounter;
            this._answer = ans;
        }
    }

    // this method get the result from the last factor by regularization.
    // 1. get the numerator from the proper row (by the query value).
    // 2. get the denominator from all the rows in the factor.
    // 3. the answer is the division between them.
    private double get_result_from_last_factor() {
        Factor last_f = this._factors.get(0);
        ArrayList<Double> probs = last_f.get_probabilities();
        ArrayList<String> indexes = last_f.get_probsIndexes();
        double numerator = probs.get(indexes.indexOf(this._queryValue));
        double denominator = 0.0;
        for(double prob : probs){
            denominator+=prob;
        }
        this._additionCounter+= indexes.size()-1;
        double result = numerator / denominator;
        return result;
    }

    // this method join two factors by their common variables.
    // for rows that match by values (for common variables) the prob in the new factor will be the multiply of both probabilities.
    private void join_factors(String next_hidden, int index_factor_1, int index_factor_2) {
        // get all data:
        // a.factors:
        Factor f1 = this._factors.get(index_factor_1);
        Factor f2 = this._factors.get(index_factor_2);

        // b. vars names:
        ArrayList<String> f1_vars_names = f1.get_variables_names();
        ArrayList<String> f2_vars_names = f2.get_variables_names();

        // c. factors data - probs, indexes, variables:
        ArrayList<Double> f1_probs = f1.get_probabilities();
        ArrayList<Double> f2_probs = f2.get_probabilities();
        ArrayList<Variable> f1_variables = f1.get_variables();
        ArrayList<Variable> f2_variables = f2.get_variables();
        ArrayList<String> f1_probs_indexes = f1.get_probsIndexes();
        ArrayList<String> f2_probs_indexes = f2.get_probsIndexes();

        // d. common and not common vars and vars names:
        ArrayList<Variable> common_variables = get_common_vars(f1_variables, f2_variables);
        ArrayList<Variable> not_common_variables = get_not_common_vars(f1_variables, f2_variables, common_variables);
        ArrayList<String> common_vars_names = Factor.get_variables_names(common_variables);
        ArrayList<String> not_common_vars_names = Factor.get_variables_names(not_common_variables);

        // e. make new data (probs, indexes, variables) lists:
        ArrayList<Double> join_probs = new ArrayList<>();
        ArrayList<String> join_probs_indexes = new ArrayList<>();
        ArrayList<Variable> join_variables = new ArrayList<>();

        // set new data by common variables values:
        // Run through each row in factor 1, find the values of the common variables in the current row,
        // find all the corresponding rows for this row in factor 2, create a new row for each match
        // of row from factor 1 to row from factor 2 as follows: The probability in the new row is
        // equal to multiply of probabilities in these rows, values The variables in the new row are
        // a union of the values in these rows (where the order is the order of the factor 1
        // variables and then the addition of the unique variables to the factor 2).
        for (int i_f1 =0; i_f1<f1_probs_indexes.size(); i_f1++){
            String f1_index_row = f1_probs_indexes.get(i_f1);
            Hashtable f1_common_values = new Hashtable();
            String[] f1_row_values_array = f1_index_row.split(",");
            for (int i=0; i< f1_row_values_array.length; i++){
                if(common_vars_names.contains(f1_vars_names.get(i))){
                    f1_common_values.put(f1_vars_names.get(i), f1_row_values_array[i]);
                }
            }

            for (int i_f2=0; i_f2<f2_probs_indexes.size(); i_f2++) {
                String f2_index_row = f2_probs_indexes.get(i_f2);
                String[] f2_row_values_array = f2_index_row.split(",");
                boolean flag = true;
                for (int i = 0; i < f2_row_values_array.length; i++) {
                    if (common_vars_names.contains(f2_vars_names.get(i)) && !f1_common_values.get(f2_vars_names.get(i)).equals(f2_row_values_array[i])) {
                        flag = false;
                    }
                }
                if (flag) {
                    String join_str = join_factors_indexes(f1_index_row, f2_row_values_array, common_vars_names, f2_vars_names);
                    join_probs.add(f1_probs.get(i_f1)*f2_probs.get(i_f2));
                    join_probs_indexes.add(join_str);
                    this._multiplicationCounter++;
                }
            }
        }
        // remove the "old factors"
        if (index_factor_1 > index_factor_2){
            this._factors.remove(index_factor_1);
            this._factors.remove(index_factor_2);
        }
        else {
            this._factors.remove(index_factor_2);
            this._factors.remove(index_factor_1);
        }

        // if the new factor will not be an empty factor - add it to the factors list.
        if(join_probs.size()>1){
            join_variables = get_join_vars(f1_variables, f2_variables, common_vars_names);
            Factor join_factor = new Factor(join_variables, join_probs, join_probs_indexes);
            this._factors.add(join_factor);
        }
    }

    // this method return a list which is the union of the  vars from 2 factors
    private ArrayList<Variable> get_join_vars(ArrayList<Variable> f1_variables, ArrayList<Variable> f2_variables, ArrayList<String> common_vars_names) {
        ArrayList<Variable> join_vars = new ArrayList<>();
        for (Variable var_f1 : f1_variables) {
            join_vars.add(var_f1);
        }
        for (Variable var_f2 : f2_variables) {
            if (!common_vars_names.contains(var_f2.get_name())) {
                join_vars.add(var_f2);
            }
        }
        return join_vars;
    }

    // this method return a list of none common vars from 2 factors (vars which appears just in one factor)
    private ArrayList<Variable> get_not_common_vars(ArrayList<Variable> f1_variables, ArrayList<Variable> f2_variables, ArrayList<Variable> common_variables) {
        ArrayList<Variable> not_common_vars = new ArrayList<>();
        ArrayList<String> common_vars_names = Factor.get_variables_names(common_variables);

        for (Variable var_f1 : f1_variables) {
            if (!common_vars_names.contains(var_f1.get_name())) {
                not_common_vars.add(var_f1);
            }
        }
        for (Variable var_f2 : f2_variables) {
            if (!common_vars_names.contains(var_f2.get_name())) {
                not_common_vars.add(var_f2);
            }
        }
        return not_common_vars;
    }
    // this method return a list of the common vars from 2 factors (vars which appears in both)
    private ArrayList<Variable> get_common_vars(ArrayList<Variable> f1_variables, ArrayList<Variable> f2_variables) {
        ArrayList<Variable> common_vars = new ArrayList<>();
        ArrayList<String> f2_vars_names = Factor.get_variables_names(f2_variables);
        for (Variable var_f1: f1_variables){
            if (f2_vars_names.contains(var_f1.get_name())){
                common_vars.add(var_f1);
            }
        }
        return common_vars;
    }

    // this method gets values of variables (strings from 2 rows, one from each factor).
    // it returns a string which represent a new row in the new factor - a union of the 2 rows.
    private String join_factors_indexes(String indexes_f1, String[] indexes_f2, ArrayList<String> common_vars_names, ArrayList<String> f2_vars_names) {
        // "insert" all variables indexes from factor1
        String result = indexes_f1;
        // "insert" variables indexes from factor2 only for unique variables (which only factor2 includes)
        for (int i=0; i<indexes_f2.length; i++){
            if (!common_vars_names.contains(f2_vars_names.get(i))){
                String index = indexes_f2[i];
                result += "," + index;
            }
        }
        return result;
    }

    // this method sort list of specific factors (which contains the current var
    // to eliminate) by size (number of rows) and Returns the two smallest.
    private int[] get_the_two_smallest_factors(ArrayList<Integer> indexes) {
        Integer[] indexes_array = indexes.toArray(new Integer[0]);
        int n = indexes_array.length;
        // bubble sort
        for (int i = 0; i < n-1; i++)
            for (int j = 0; j < n-i-1; j++)
                if (get_smaller(indexes_array[j],indexes_array[j+1]) == indexes_array[j+1])
                {
                    // swap indexes_array[j+1][j+1] and indexes_array[j+1][j]
                    int temp = indexes_array[j];
                    indexes_array[j] = indexes_array[j+1];
                    indexes_array[j+1] = temp;
                }
        int[] smallest = {indexes_array[0], indexes_array[1]};
        return smallest;
    }

    // the method get twp indexes (of factors from the factors list)
    // and return which one is smaller (by amount of rows and if the size is equals than by the vars ascii).
    private int get_smaller(int i1, int i2) {
        int num_rows_factor_i1 = this._factors.get(i1).get_probabilities().size();
        int num_rows_factor_i2 = this._factors.get(i2).get_probabilities().size();
        if(num_rows_factor_i1 < num_rows_factor_i2){ return i1; }
        else if(num_rows_factor_i1 > num_rows_factor_i2){ return i2; }
        else {
            int ascii_factor_1 = get_sum_ascii(this._factors.get(i1).get_variables());
            int ascii_factor_2 = get_sum_ascii(this._factors.get(i2).get_variables());
            if(ascii_factor_1 < ascii_factor_2){ return i1; }
            else { return i2; }
        }
    }

    // this method sum the ascii number of the variables characters and returns the sum.
    private int get_sum_ascii(ArrayList<Variable> variables) {
        int sum=0;
        for (Variable var : variables){
            String str =  var.get_name();
            for(int i=0; i<str.length(); i++)
            {
                int asciiValue = str.charAt(i);
                sum += asciiValue;
            }
        }
        return sum;
    }

    // this method eliminate a variable from factor
    // after the elimination, if the factor is not empty, it will remain in the factors list.
    // after the elimination, if the factor is empty, it will be removed from the factors list.
    // note: "empty factor" = one row and no variables.
    public void eliminate(int eliminate_factor_index, String eliminate_var_name) {
        // get current factor data
        Factor f = this._factors.get(eliminate_factor_index);
        ArrayList<Double> old_probs = f.get_probabilities();
        ArrayList<String> old_probs_indexes = f.get_probsIndexes();
        ArrayList<Variable> old_variables = f.get_variables();

        // initialize new factor lists:
        ArrayList<Double> new_probs = new ArrayList<>();
        ArrayList<String> new_probs_indexes = new ArrayList<>();;
        ArrayList<Variable> new_variables = new ArrayList<>();;;

        // make list of "new_variables":
        // (i.e. make same variables without the factor which will be eliminated)
        for (Variable var : old_variables){
            if (!var.get_name().equals(eliminate_var_name)){
                new_variables.add(var);
            }
        }

        // find index i of hidden_var:
        int index_eliminate_var_name=0;
        for (int i=0; i<old_variables.size(); i++){
            String curr_var_name = f.get_variables().get(i).get_name();
            if(curr_var_name.equals(eliminate_var_name)){
                index_eliminate_var_name = i;
                break;
            }
        }

        // eliminate process:
        // (0. make regex of first row string in old_probs_indexes list. use regex to get correct rows to eliminate this step)
        //  1. sum probabilities and add to new_probs list
        //  2. make new values_string and add to new_probs_indexes list
        // (3. remove rows from old data for not repeat the same rows)
        while (old_probs.size() > 0){

            // make regex of first row string:
            String first_row_str = old_probs_indexes.get(0);
            String[] values_array = first_row_str.split(",");
            values_array[index_eliminate_var_name] = "(.*)";
            String first_row_regex = "";
            for (String value : values_array){
                first_row_regex += value + ",";
            }
            first_row_regex = first_row_regex.substring(0,first_row_regex.length()-1);

            // get rows indexes to eliminate
            int values_amount = old_variables.get(index_eliminate_var_name).get_values().size();
            int[] eliminate_indexes = new int[values_amount];
            int remains_counter = values_amount;
            for (int i=0; i< old_probs_indexes.size(); i++){
                if(old_probs_indexes.get(i).matches(first_row_regex)){
                    eliminate_indexes[remains_counter-1] = i;
                    remains_counter--;
                    if(remains_counter == 0)
                        break;
                }
            }

            // get sum probabilities:
            boolean is_first = true;
            double sum_probs = 0;
            for (int index : eliminate_indexes){
                if (is_first){
                    sum_probs = old_probs.get(index);
                    is_first = false;
                }
                else {
                    sum_probs += old_probs.get(index);
                    this._additionCounter++;
                }
            }

            // make new values_string (without the value of eliminate variable):
            String new_values_str = "";
            for (int i=0; i<values_array.length; i++){
                if (i == index_eliminate_var_name){
                    continue;
                }
                else {
                    new_values_str += values_array[i] + ",";
                }
            }
            if(!new_values_str.equals("")){
                new_values_str = new_values_str.substring(0, new_values_str.length()-1);
            }

            // set new (sum) prob and new values_string for new factor
            new_probs.add(sum_probs);
            new_probs_indexes.add(new_values_str);

            // remove rows which we used (eliminate_indexes rows):
            for (int index : eliminate_indexes){
                old_probs.remove(index);
                old_probs_indexes.remove(index);
            }
        }

        // remove old factor
        this._factors.remove(eliminate_factor_index);

        // if there is more than one row, make new factor and add to the factors list.
        // if there is only one row it means this is an empty factor, i.e., it has no meaning
        // for the algo and calculation, so "remove" it (= do not create it).
        if(new_probs.size()>1){
            Factor f_after_eliminate = new Factor(new_variables, new_probs, new_probs_indexes);
            this._factors.add(f_after_eliminate);
        }
    }

    // this method create list of factors indexes which contains the given hidden variable and return it.
    private ArrayList<Integer> get_indexes_of_factors(String hidden_var_name) {
        ArrayList<Integer> indexes = new ArrayList<>();
        for(int i=0; i<this._factors.size(); i++){
            for (Variable var :this._factors.get(i).get_variables()){
                if (var.get_name().equals(hidden_var_name)){
                    indexes.add(i);
                    break;
                }
            }
        }
        return indexes;
    }

    // check if there is the answer in one of the factors
    // if there is return true and set the result, else return false and continue the algorithm
    private boolean immediate_result_check() {

        // make array with the names of evidence and query variables
        String[] query_and_evidence_names = new String[this._evidence.length+1];
        int i=0;
        for (;i<this._evidence.length; i++){
            query_and_evidence_names[i] = this._evidence[i];
        }
        query_and_evidence_names[i] = this._query;

        for( Factor f: this._factors ){
            // check if all query and evidence names is in the factor
            if (f.get_variables().size() == query_and_evidence_names.length){
                boolean is_same_variables = true;
                for (Variable var : f.get_variables()){
                    if (!Arrays.asList(query_and_evidence_names).contains(var.get_name())){
                        is_same_variables = false;
                        break;
                    }
                }
                if(is_same_variables){
                    this._is_immediate = true;
                    set_immediate_result(f);
                    return true;
                }
            }
        }
        return false;
    }

    // this method get a number and return it rounded after specific places after the point.
    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    // this method set the final (String) answer of the algo (without all the calculation)
    private void set_immediate_result(Factor f) {
        String result_indexes = "";
        for (Variable var : f.get_variables()){
            String value;
            if(!var.get_name().equals(this._query)){
                value = this._evidenceValues[Arrays.asList(this._evidence).indexOf(var.get_name())]+",";
            }
            else {
                value = this._queryValue;
            }
            result_indexes+=value;
        }
        int row_index = f.get_probsIndexes().indexOf(result_indexes);
        double result = f.get_probabilities().get(row_index);
        result = round(result, 5);
        this._answer = result+",0,0";
    }

    // return the final answer (String - line for output file)
    public String get_answer(){
        return _answer;
    }
}
