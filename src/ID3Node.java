import java.util.LinkedList;
import java.util.Vector;

/**
 * Created by Mack on 3/12/2017.
 */
public class ID3Node {
    public double entropy;
    public DataSet data;
    public int splitAttribute;
    public int splitValue;
    public ID3Node[] children;
    public ID3Node parent;
    public int targetVal;

    public ID3Node(DataSet data){
        this.data = data;
    }
    public boolean hasChildren(){
        //Checks if node is a leaf node
        if(children==null){
            return true;
        }
        else{
            return false;
        }
    }
    public void get_targetVal(int targetCode){
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
    }
}