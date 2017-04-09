import java.util.LinkedList;

public class C45Tree extends DecisionTree{

    //Entropy calculated as gain ratio with gain from gain/splitInfo
    //Split info is --we have no idea

    private double calculate_splitInfo(DataSet data, int atrCode){
        double splitInfo = 0;
        double entropy = 0;
        int[][] dataTable = data.get_dataTable();
        int size = dataTable.length;
        int count = 0;
        double prob = 0;
        LinkedList[] atrValues = data.get_atrValues();
        for(int j = 0;j<atrValues[atrCode].size();j++) {
            for (int k = 0; k < size; k++) {
                if(dataTable[atrCode][targetCode] == j){
                    count++;
                }
            }
            //Ensure that count is not zero, else set entropy to zero for this iteration
            if(count != 0) {
                prob = ((double) count) / ((double) size);
                count = 0;
                entropy += -prob * log_base2(prob);
            }
        }
        return splitInfo;
    }

    public void get_splitting_attribute(ID3Node node,int targetCode){
        //Find attribute with the highest gain and adds it to the given node
        DataSet data = node.data;
        LinkedList[] atrValues = data.get_atrValues();
        DataSet subset;
        int dataSize = data.get_dataTable().length;
        int subsetSize;
        double subsetEntropy;
        double atrEntropy = 0;
        double highestGainRatio = 0;
        double gainRatio = 0;
        double gain;
        double splitInfo = 0;
        //Loop through all attributes, storing the code of the attribute with the highest gain
        for(int i = 0;i< atrValues.length;i++){
            if(targetCode == i){
                continue;
            }
            for(int j = 0;j<atrValues[i].size();j++){
                subset = create_subset(data,i,j);
                subsetEntropy = calculate_entropy(subset,targetCode);
                subsetSize = subset.get_dataTable().length;
                atrEntropy += (subsetSize/dataSize)*subsetEntropy;
            }
            //Calculate SplitInfo for the current attribute, i
            splitInfo = calculate_splitInfo(data,i);
            //Compute gain ratio from entropies and check to see if it is the highest gain yet
            gain = node.entropy - atrEntropy;
            gainRatio = gain/splitInfo;
            if(gainRatio > highestGainRatio){
                highestGainRatio = gainRatio;
                node.splitAttribute = i;
            }
            atrEntropy = 0;
            System.out.println("HEY THERE");
        }
    }
}
