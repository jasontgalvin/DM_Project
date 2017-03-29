import java.io.File;
import java.io.IOException;
import java.util.*;

import static java.lang.System.exit;

/**
 * Created by Mack on 3/11/2017.
 */
public class DecisionTree {

    public static DataSet read_data(String path) throws IOException{
        //Imports the data file and places the transactions in a list
        Scanner in = new Scanner(new File(path));
        int totalAttributes;
        int[][] data;
        if(in.hasNextLine()) {
            Scanner firstScan = new Scanner(in.nextLine());
            DataSet dataSet = new DataSet();

            //Read in the attribute names
            LinkedList<String> attributes = new LinkedList<>();
            dataSet.set_atrNames(attributes);
            while(firstScan.hasNext()) {
                attributes.add(firstScan.next());
            }
            totalAttributes = attributes.size();


            //Store data in a linked list of strings until we know how many rows there are
            LinkedList<String[]> dataLists = new LinkedList<>();

            //Create and initialize an array for storing all possible values for each attribute
            LinkedList<String>[] attributeValues = new LinkedList[totalAttributes];
            for(int i=0;i<totalAttributes;i++){
                attributeValues[i] = new LinkedList<>();
            }

            //Parse all rows of the input, add records to temp list and store attribute options in attributeValues
            while (in.hasNext()) {
                String[] dataRecord = new String[totalAttributes];
                for(int i = 0;i<totalAttributes;i++) {
                    dataRecord[i] = in.next();
                    if(!attributeValues[i].contains(dataRecord[i])){
                        attributeValues[i].add(dataRecord[i]);
                    }
                }
                dataLists.add(dataRecord);
            }

            //Convert Linked List to array of integer codes
            // For each term in data, replace with integer position of term
            int numOfRows = dataLists.size();
            data = new int[numOfRows][totalAttributes];
            for(int i = 0;i<numOfRows;i++){
                for(int j = 0;j<totalAttributes;j++){
                    data[i][j] = attributeValues[j].indexOf(dataLists.get(i)[j]);
                }
            }

            dataSet.set_atrNames(attributes);
            dataSet.set_atrValues(attributeValues);
            dataSet.set_dataTable(data);


            firstScan.close();
            in.close();
            return dataSet;
        }
        else{
            throw new IOException("File is empty");
        }
    }
    public static DataSet create_subset(DataSet dataset, int attribute, int value){
        //Creates a subset of a dataset that contains the given value of the splitting attribute
        DataSet subset = new DataSet();
        //Copy the attribute names and values over - there is no need to create new indexes
        subset.set_atrNames(dataset.get_atrNames());
        subset.set_atrValues(dataset.get_atrValues());

        int[][] dataTable = dataset.get_dataTable();
        int size = dataTable.length;
        LinkedList<Integer> subsetIndexes = new LinkedList<>();
        //Iterate through all rows, checking if each row contains given value for the splitting attribute
        //Add the indexes of these rows to linkedlist for later processing
        for(int i = 0;i < size;i++){
            if(dataTable[i][attribute] == value){
                subsetIndexes.add(i);
            }
        }
        //Create table for subset and fill it with the rows that met the condition above
        int[][] subsetTable = new int[subsetIndexes.size()][dataset.get_atrNames().size()];
        int currIndex;
        for(int i = 0;i<subsetTable.length;i++){
            currIndex = subsetIndexes.get(i);
            for(int j = 0;j<subsetTable[0].length;j++){
                subsetTable[i][j] = dataTable[currIndex][j];
            }
        }
        //Add table to subset dataset
        subset.set_dataTable(subsetTable);

        return subset;
    }
    public static double calculate_entropy(DataSet data,int targetCode){
        //Calculate the entropy of each dataset with regards to information given by attribute targetCode
        double entropy = 0;
        int[][] dataTable = data.get_dataTable();
        int size = dataTable.length;
        double prob  = 0;
        int count = 0;
        if(size == 0){
            return 0;
        }
        LinkedList<String>[] atrValues = data.get_atrValues();
        for(int j = 0;j<atrValues[targetCode].size();j++) {
            for (int i = 0; i < size; i++) {
                if(dataTable[i][targetCode] == j){
                    count++;
                }
            }
            //Ensure that count is not zero, else set entropy to zero for this iteration
            if(count != 0) {
                prob = ((double) count) / ((double) size);
                count = 0;
                entropy += -prob * Math.log(prob);
            }
        }
        return entropy;
    }
    public static void get_splitting_attribute(ID3Node node,int targetCode){
        //Find attribute with the highest gain and adds it to the given node
        DataSet data = node.data;
        LinkedList[] atrValues = data.get_atrValues();
        DataSet subset;
        int dataSize = data.get_dataTable().length;
        int subsetSize;
        double subsetEntropy;
        double atrEntropy = 0;
        double highestGain = 0;
        double gain;
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
            //Compute gain from entropies and check to see if it is the highest gain yet
            gain = node.entropy - atrEntropy;
            if(gain > highestGain){
                highestGain = gain;
                node.splitAttribute = i;
            }
            atrEntropy = 0;
        }
    }
    public static ID3Node build_tree(ID3Node root,int targetCode){
        //Creates a decision tree from the given dataset
        DataSet subset;
        //Get information Gain
        root.entropy = calculate_entropy(root.data,targetCode);
        if(root.entropy == 0){
            root.children = null;
            //This is a leaf node, so decide which class this node belongs to
            root.get_targetVal(targetCode);
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
                build_tree(children[i], targetCode);
            }
            root.children = children;
        }
        return root;
    }
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
    public static int get_target_code(DataSet data,String target){
        //Retrieves the integer code for the target string
        int targetCode = -1;
        int counter = 0;
        LinkedList<String> attributes = data.get_atrNames();
        Iterator<String> itr = attributes.iterator();
        while(itr.hasNext()){
            String curr = itr.next();
            if(curr.equals(target)){
                targetCode = counter;
                return targetCode;
            }
            counter++;
        }
        return targetCode;
    }

    public static Rule[] create_rules(ID3Node currNode){
        //Recursively generate rules from decision tree
        ID3Node[] children = currNode.children;
        Rule[] rules = null;
        if(currNode.children != null) {
            int splitAttribute, splitValue;
            rules = new Rule[children.length];
            //Create a rule for each child
            for (int i = 0; i < children.length; i++) {
                splitAttribute = currNode.splitAttribute;
                splitValue = children[i].splitValue;
                rules[i] = new Rule(splitAttribute, splitValue, currNode.data);
                //Recursively create rules for each child
                rules[i].b = create_rules(children[i]);
                //Transfer targetVal of children to the children's rule if the child is a leaf node
                if(children[i].children == null){
                    rules[i].targetVal = children[i].targetVal;
                }
            }
        }
        return rules;
    }

    public static void main(String[] args) {
        //Get parameters from user
        String[] options = get_user_options();
        String path = options[0];
        String target = options[1];

        try {
            DataSet data = read_data(path);
            //Get the targetCode for the attribute we want to classify
            int targetCode = get_target_code(data,target);
            if(targetCode == -1){
                System.out.println("The given target attribute name is not present in the table");
                exit(1);
            }
            // Build a tree
            ID3Node root = new ID3Node(data);
            ID3Node tree = build_tree(root,targetCode);
            //Print all rules
            Rule[] rules = create_rules(tree);
            for(int i = 0;i<rules.length;i++) {
                rules[i].print_rule(targetCode,0);
            }
        }
        catch(IOException e){
            System.out.println("Error Reading file");
        }
    }
}
