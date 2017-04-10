import java.util.LinkedList;

public class C45Tree extends DecisionTree{

    //Gain calculated as gain ratio with gain from gain/splitInfo
    //Post-Pruning Implemented

    private double calculate_splitInfo(DataSet data, int atrCode){
        double splitInfo = 0;
        int[][] dataTable = data.get_dataTable();
        int size = dataTable.length;
        int count = 0;
        double prob = 0;
        LinkedList[] atrValues = data.get_atrValues();
        for(int j = 0;j<atrValues[atrCode].size();j++) {
            for (int k = 0; k < size; k++) {
                if(dataTable[k][atrCode] == j){
                    count++;
                }
            }
            //Ensure that count is not zero, else set entropy to zero for this iteration
            if(count != 0) {
                prob = ((double) count) / ((double) size);
                count = 0;
                splitInfo += -prob * log_base2(prob);
            }
    }
        return splitInfo;
    }
    public ID3Node build_tree(ID3Node root){
        //Creates a decision tree from the given dataset
        int targetCode = this.targetCode;
        DataSet subset;
        //Get information Gain
        root.entropy = calculate_entropy(root.data,targetCode);
        if(root.entropy == 0){
            root.children = null;
        }
        else {
            get_splitting_attribute(root, targetCode);
            LinkedList<String>[] atrValues = root.data.get_atrValues();
            int numChildren = atrValues[root.splitAttribute].size();
            ID3Node[] children = new ID3Node[numChildren];
            //Create branches off of the root for each value of the splitAttribute
            for (int i = 0; i < numChildren; i++) {
                subset = create_subset(root.data, root.splitAttribute, i);
                children[i] = new ID3Node(subset);
                children[i].parent = root;
                children[i].splitValue = i;
                build_tree(children[i]);
            }
            root.children = children;
        }
        //Calculate the majority class and the error probability
        root.get_targetVal(targetCode);
        return root;
    }

    public double prune_tree(ID3Node root){
        //Prune tree by comparing the error probability of a parent to its children
        //If the children increase the error, they can be pruned
        double errorSum = 0;
        if(root != null){
            if(root.hasChildren()){
                ID3Node[] children = root.children;
                for(int i = 0;i<children.length;i++){
                    int childCount = 0;
                    if(children[i].hasChildren()){
                        //Child is not leaf node
                        errorSum += prune_tree(children[i]);
                    }
                    else{
                        //Child is leaf node
                        errorSum += children[i].errorProb;
                    }
                }
            }
        }
        if(errorSum > root.errorProb){
            //Prune tree by breaking association between the parent and children
            root.children = null;
            System.out.print("Pruning");
        }
        return root.errorProb;
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
        }
    }
}
