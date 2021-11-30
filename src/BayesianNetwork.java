import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Scanner;
import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors


public class BayesianNetwork {
    private Hashtable _nodes;

    public BayesianNetwork(){
        this._nodes = new Hashtable();
    }

    public Hashtable get_nodes() {
        return _nodes;
    }

    public void addNode(String name, BayesianNode node){
        this._nodes.put(name, node);
    }

    // this method read the data from the xml file and turns it to Bayesian Network
    public static BayesianNetwork readXML(String fileName){
        BayesianNetwork net = new BayesianNetwork();
        try {
            File xmlFile = new File(fileName);
            Scanner scanner = new Scanner(xmlFile);
            while (scanner.hasNextLine()) {
                String data = scanner.nextLine();
                if(data.matches("(.*<VAR.*)")){
                    initializeVariables(net, scanner);
                }
                else if(data.matches("(.*<DEF.*)")) {
                    initializeNodes(net, scanner);
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return net;
    }

    // from each part of Variable in the xml file,
    // this method create bayesian node and append it to the Bayesian Network
    private static void initializeVariables(BayesianNetwork net, Scanner scanner){
        String data = scanner.nextLine();
        String name = getData(data);
        ArrayList<String> values = new ArrayList<>();
        data = scanner.nextLine();
        while (data.matches("(.*)<OUTCOME>(.*)</OUTCOME>(.*)")){
            String value = getData(data);
            values.add(value);
            data = scanner.nextLine();
        }
        Variable var = new Variable(name, values);
        BayesianNode node = new BayesianNode(var);
        net.addNode(name, node);
    }

    // this method set the data of each Bayesian Node from the xml file
    private static void initializeNodes(BayesianNetwork net, Scanner scanner){
        // the main data we want to get:
        String name;
        ArrayList<String> parents = new ArrayList<>();
        ArrayList<Double> probs = new ArrayList<>();

        //name
        String data = scanner.nextLine();
        name = getData(data);

        //parents
        data = scanner.nextLine();
        while (data.matches("(.*)<GIVEN>(.*)</GIVEN>(.*)")){
            parents.add(getData(data));
            data = scanner.nextLine();
        }
        //initialize parents
        BayesianNode cur_node = (BayesianNode) net.get_nodes().get(name);
        for (String parent : parents){
            BayesianNode parent_node = (BayesianNode) net.get_nodes().get(parent);
            cur_node.add_parent(parent_node);
        }
        //initialize as kid to all parents
        for (String parent : parents){
            BayesianNode parent_node = (BayesianNode) net.get_nodes().get(parent);
            parent_node.add_kid(cur_node);
        }

        //probabilities:
        String[] probs_str = data.split(">")[1].split("<")[0].split(" ");
        for (String str : probs_str){
            probs.add(Double.parseDouble(str));
        }

        //make factor:
        ArrayList<Variable> varsOfFactor = new ArrayList<>();
        for (String parent : parents){
            varsOfFactor.add( ((BayesianNode) net.get_nodes().get(parent)).get_variable() );
        }
        varsOfFactor.add(cur_node.get_variable());
        Factor f = new Factor(varsOfFactor, probs);
        cur_node.set_factor(f);
    }

    private static String getData(String line){
        String value = line.split(">")[1].split("<")[0];
        return value;
    }

    public ArrayList<String> getVariablesNames(){
        ArrayList<String> names = new ArrayList<String>(this.get_nodes().keySet());
        return names;
    }

    @Override
    public String toString() {
        return "BayesianNetwork{" +
                "_nodes=\n" + _nodes.toString() +
                '}';
    }
}
