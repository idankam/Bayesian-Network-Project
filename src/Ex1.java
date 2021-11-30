import java.io.*;
import java.util.Scanner;

public class Ex1 {

    // open and read from input file
    static File file = new File("input9.txt");
    static Scanner scanner;
    static {
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred with file reading.");
            e.printStackTrace();
        }
    }

    // the function write to text file the result of the queries in the input file.
    public static void main(String[] args) throws IOException {
        File output = new File("output.txt");
        BufferedWriter out = new BufferedWriter(new FileWriter(output));
        String XML_name = scanner.nextLine();
        BayesianNetwork net = BayesianNetwork.readXML(XML_name);
        while(scanner.hasNextLine()){
            String line = scanner.nextLine();

            // BAYESBALL
            if(!line.startsWith("P")){
                BayesBall bb = new BayesBall(net, line);
                Boolean answer = bb.get_isIndependent();
                String result;
                if (answer) {
                    result = "yes";
                }
                else {
                    result = "no";
                }
                out.write(result);
            }
            // VARIABLE ELIMINATION
            else {
                VariableElimination v_e = new VariableElimination(net, line);
                String result = v_e.get_answer();
                out.write(result);
            }
            if (scanner.hasNextLine()){
                out.newLine();
            }
        }
        scanner.close();
        out.close();
    }
}
