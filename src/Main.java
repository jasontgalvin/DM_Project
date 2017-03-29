import java.util.Scanner;

public class Main {
    public static String[] get_user_options(){
        //Gets the data file path and target attribute from the user
        String[] options = new String[2];
        Scanner scan = new Scanner(System.in);
        System.out.print("Enter path to file:");
        options[0] = scan.next();
        System.out.print("Enter the target attribute (Same spelling as in the data file):");
        options[1] = scan.next();
        return options;
    }
    public static void main(String[] args) {
        //Get parameters from user
        String[] options = get_user_options();
        String path = options[0];
        String target = options[1];

        DecisionTree dt = new DecisionTree();
        dt.init(path,target);
        dt.print_rules();
    }
}
