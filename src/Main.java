import java.util.Scanner;

public class Main {

    public static String[] get_user_options(){
        //Gets the data file path and target attribute from the user
        String[] options = new String[3];
        Scanner scan = new Scanner(System.in);
        System.out.print("Enter path to training data file:");
        options[0] = scan.next();
        System.out.print("Enter the target attribute (Same spelling as in the data file):");
        options[1] = scan.next();
        System.out.print("Enter path to testing data file:");
        options[2] = scan.next();
        return options;
    }
    public static void main(String[] args) {
        //Get parameters from user
//        String[] options = get_user_options();
//        String trainpath = options[0];
//        String target = options[1];
//        String testpath = options[2];

        String trainpath = "data1";
        String testpath = "data2";
        String target = "playtennis";

        C45Tree dt = new C45Tree();
        dt.init(trainpath,testpath,target);
        dt.print_rules();
        dt.write_toFile();
    }
}
