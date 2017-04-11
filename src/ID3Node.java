import java.util.LinkedList;
import java.util.Vector;

import static java.lang.Math.sqrt;

public class ID3Node {
    public double entropy;
    public DataSet data;
    public int splitAttribute;
    public int splitValue;
    public ID3Node[] children;
    public ID3Node parent;
    public int targetVal;
    public double upperPVal;

    public ID3Node(DataSet data){
        this.data = data;
    }
    public boolean hasChildren(){
        //Checks if node is a leaf node
        if(children==null){
            return false;
        }
        else{
            return true;
        }
    }
    public void get_targetVal(int targetCode,double z){
        //Get integer code for index of winning target class
        int[][] dataTable = this.data.get_dataTable();
        LinkedList<String> atrNames = this.data.get_atrNames();
        int[] counts = new int[atrNames.size()];

        for(int i =0;i<dataTable.length;i++){
            for(int j = 0;j<counts.length;j++){
                if(dataTable[i][targetCode] == j ) {
                    counts[j]++;
                    break;
                }
            }
        }
        int maxcountIndex = 0;
        int maxCount = 0;
        for(int j = 0;j<counts.length;j++){
            if(counts[j] > maxCount){
                maxCount = counts[j];
                maxcountIndex = j;
            }
        }
        this.targetVal = maxcountIndex;
        //Calculate error probability
        int errorCount = 0;
        for(int j = 0;j<counts.length;j++){
            if(j != maxcountIndex){
                errorCount += counts[j];
            }
        }
        double errorProb = 0;
        if(dataTable.length > 0) {
            errorProb = (double)errorCount/(double)dataTable.length;
            this.upperPVal = errorProb + z*sqrt(errorProb*(1-errorProb)/dataTable.length);
        }
        else{
            this.upperPVal = 0;
        }
    }
}