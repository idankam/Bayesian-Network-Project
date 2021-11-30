import java.util.ArrayList;

public class Variable {
    private String _name;
    private ArrayList<String> _values;

    public Variable(String name, ArrayList<String> values){
        set_name(name);
        set_values(values);
    }

    public ArrayList<String> get_values() {
        return _values;
    }

    public String get_name() {
        return _name;
    }

    public void set_values(ArrayList<String> _values) {
        this._values = _values;
    }

    public void set_name(String _name) {
        this._name = _name;
    }

    public Variable copy(){
        String name = this._name;
        ArrayList<String> values = new ArrayList<>();
        for (String value : this._values){
            values.add(value);
        }
        Variable copy_var = new Variable(name, values);
        return copy_var;
    }

    @Override
    public String toString() {
        return _name.toString() + "=" + _values.toString();
    }
}