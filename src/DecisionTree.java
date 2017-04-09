import java.io.*;
import java.util.*;

import static java.lang.System.exit;


public class DecisionTree{
    public String trainpath;
    public String testpath;
    public String target;
    public int targetCode;
    public Rule[] rules;
    public ID3Node tree;
    public DataSet outData;
    public double accuracy;

    private DataSet read_data(String path) throws IOException{
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
    public DataSet create_subset(DataSet dataset, int attribute, int value){
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
    public double log_base2(double x){
        if(x!=0){
            return Math.log(x)/Math.log(2);
        }
        else{
            return 0;
        }
    }
    public double calculate_entropy(DataSet data,int targetCode){
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
                entropy += -prob * log_base2(prob);
            }
        }
        return entropy;
    }
    private void get_splitting_attribute(ID3Node node,int targetCode){
        //Find attribute with the highest gain and adds it to the given node
        System.out.println("OH NO\n\n\n");
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
    private ID3Node build_tree(ID3Node root){
        //Creates a decision tree from the given dataset
        int targetCode = this.targetCode;
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
                build_tree(children[i]);
            }
            root.children = children;
        }
        return root;
    }

    private int get_target_code(DataSet data,String target){
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

    private Rule[] create_rules(ID3Node currNode){
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
    private double calc_accuracy(){
        //Calculates the accuracy of the Naive Bayes Classifiers Predictions
        DataSet data = this.outData;
        int targetCode = this.targetCode;
        double acc = 0;
        int[][] dataTable = data.get_dataTable();
        int totalTuples = dataTable.length;
        int totalAttributes = data.get_atrNames().size();
        int correct = 0;
        //Count number of correct predictions
        for(int i = 0;i<totalTuples;i++){
            if(dataTable[i][targetCode] == dataTable[i][totalAttributes-1]){
                correct++;
            }
        }
        acc = (double)correct/(double)totalTuples;
        return acc;
    }
    public DataSet classify_input(DataSet testData){
        int targetCode = this.targetCode;
        Rule[] rules = this.rules;
        //Process and classify all test examples
        int[][] dataTable = testData.get_dataTable();
        LinkedList<String> atrNames = testData.get_atrNames();
        LinkedList<String>[] atrValues = testData.get_atrValues();
        atrNames.add("classification");
        //Create a classes column in the data, transfer target code  values to class values
        LinkedList<String> classes = new LinkedList<>();
        for(int i = 0;i<atrValues[targetCode].size();i++){
            classes.add(atrValues[targetCode].get(i));
        }
        //Transfer contents of old data values to new larger array
        LinkedList<String>[] newAtrValues = new LinkedList[atrValues.length+1];
        for(int i  = 0;i<atrValues.length;i++){
            newAtrValues[i] = atrValues[i];
        }
        //Fill classification values with classes
        newAtrValues[atrValues.length] = classes;

        int[][] newDataTable = new int[dataTable.length][dataTable[0].length+1];
        for(int i = 0;i<dataTable.length;i++) {
            //Copy values from dataTable into outputDataTable
            for(int j  =0;j<dataTable[0].length;j++){
                newDataTable[i][j] = dataTable[i][j];
            }
            //Fill classification column with classification predictions
            int cl = -1;

            for(int k = 0;k<rules.length;k++) {
                cl = rules[k].classify(dataTable[i]);
                if(cl!= -1){
                    break;
                }
            }
            if(cl != -1) {
                newDataTable[i][dataTable[0].length] = cl;
            }
        }

        DataSet outputDataset = new DataSet();
        outputDataset.set_dataTable(newDataTable);
        outputDataset.set_atrNames(atrNames);
        outputDataset.set_atrValues(newAtrValues);

        return outputDataset;
    }
    public void print_rules(){
        //Prints out all rules for this decision tree
        Rule[] rules = this.rules;
        for(int i = 0;i<rules.length;i++) {
            rules[i].print_rule(this.targetCode,0);
        }
    }
    public void write_toFile(){
        //Write results to a file
        try(Formatter writer = new Formatter(new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("Results.txt"), "utf-8")))) {
            DataSet data = this.outData;
            double accuracy = this.accuracy;
            int[][] dataTable = data.get_dataTable();
            LinkedList<String> atrNames = data.get_atrNames();
            LinkedList<String>[] atrValues = data.get_atrValues();
            Iterator<String> itr = atrNames.iterator();
            Formatter formatter = new Formatter();

            //Print Column Headers
            while(itr.hasNext()){
                writer.format("%15s",itr.next());
            }
            writer.format("\n-----------------------------------------------------------------------------------------------\n");
            //Print rows
            for(int i = 0;i < dataTable.length;i++) {
                for (int j = 0; j < atrNames.size(); j++) {
                    writer.format("%15s",atrValues[j].get(dataTable[i][j]));
                }
                writer.format("\n");
            }
            //Print Accuracy
            writer.format("\nAccuracy: " + accuracy);
            System.out.println("Results have been outputted to file Results.txt\n Accuracy = " +accuracy);
            try {writer.close();} catch (Exception ex) {/*ignore*/}
        }
        catch (IOException ex) {
            System.out.println("Failed to write to file");
        }
    }

    public void init(String trainpath,String testpath, String target){
        this.trainpath = trainpath;
        this.testpath = testpath;
        this.target = target;
        try {
            DataSet trainData = read_data(trainpath);
            DataSet testData = read_data(testpath);
            //Get the targetCode for the attribute we want to classify
            this.targetCode = get_target_code(trainData,target);
            if(this.targetCode == -1){
                System.out.println("The given target attribute name is not present in the table");
                exit(1);
            }
            // Build a tree
            ID3Node root = new ID3Node(trainData);
            this.tree = build_tree(root);
            //Print all rules
            this.rules = create_rules(tree);
            //Classify test file, append classification column to test DataSet
            this.outData = classify_input(testData);
            this.accuracy = calc_accuracy();
        }
        catch(IOException e){
            System.out.println("Error Reading file");
        }
    }
}
