import java.util.LinkedList;

/**
 * Created by Mack on 3/15/2017.
 */
public class Rule {
    //A Rule is a statement of the form (a is valA ==> b), (if a is aVal, then b)
    //b is also a rule, so rules can be nested inside each other
    public int a;
    public int aVal;
    public DataSet dataset;
    public int targetVal;
    public Rule[] b;

    public Rule(int a, int aVal, DataSet dataset){
        this.a = a;
        this.aVal = aVal;
        this.dataset = dataset;
    }
    public void print_rule(int targetCode, int numIndents){
        //Prints rules recursively, using indentation to represent tree structure
        String aString = dataset.get_atrNames().get(a);
        String aValString = dataset.get_atrValues()[a].get(aVal);
        for(int j = 0;j<numIndents;j++){
            System.out.print("\t");     //Create tree structure with tabs
        }
        System.out.print("If " + aString + " is " + aValString + ", then ");
        if(b != null){
            System.out.println();
            for(int i = 0;i < b.length;i++) {
                b[i].print_rule(targetCode, numIndents+1);
            }
        }
        else{
            System.out.println(dataset.get_atrNames().get(targetCode) + " is " + dataset.get_atrValues()[targetCode].get(targetVal));
        }
    }
}
