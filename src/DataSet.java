import java.util.LinkedList;

/**
 * Created by Mack on 3/11/2017.
 */
public class DataSet {
    private LinkedList<String> atrNames;
    private LinkedList<String>[] atrValues;
    private int[][] dataTable;

    public DataSet(){

    }

    //Getters
    public int[][] get_dataTable(){
        return this.dataTable;
    }
    public LinkedList<String> get_atrNames(){
        return this.atrNames;
    }
    public LinkedList<String>[] get_atrValues(){
        return this.atrValues;
    }

    //Setters
    public void set_dataTable(int[][] data){
        this.dataTable = data;
    }
    public void set_atrNames(LinkedList<String> atrNames){
        this.atrNames = atrNames;
    }
    public void set_atrValues(LinkedList<String>[] atrValues){
        this.atrValues = atrValues;
    }

    public void print_dataTable(){
        for(int i = 0;i < this.dataTable.length;i++) {
            for (int j = 0; j < this.atrNames.size(); j++) {
                System.out.print(dataTable[i][j]);
            }
            System.out.println();
        }
    }
    public void print_data(){
        for(int i = 0;i < this.dataTable.length;i++) {
            System.out.print("( ");
            for (int j = 0; j < this.atrNames.size(); j++) {
                System.out.print(this.atrValues[j].get(this.dataTable[i][j]) + " ");
            }
            System.out.println(")");
        }
    }
}
