import java.util.Scanner;

public class Main {

    public static String[] get_user_options(){
        //Gets the data file path and target attribute from the user
        String[] options = new String[2];
        Scanner scan = new Scanner(System.in);
        System.out.print("Enter path to training data file:");
        options[0] = scan.next();
        System.out.print("Enter path to testing data file:");
        options[1] = scan.next();
        return options;
    }
    public static void main(String[] args) {
        //Get parameters from user
        String[] options = get_user_options();
        String trainpath = options[0];
        String testpath = options[1];

//        String trainpath = "sec_train.txt";
//        String testpath = "sec_test.txt";
//        String target = "label";

        C45Tree dt = new C45Tree();
//        DecisionTree dt = new DecisionTree();
        dt.init(trainpath,testpath);
        dt.init_tree();
        dt.build_tree(dt.tree);
        dt.prune_tree(dt.tree);
        dt.init_rules();
        dt.classify();
        dt.print_rules();
        dt.write_toFile();
    }
}
