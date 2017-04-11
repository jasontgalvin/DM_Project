#import packages for C50
library("C50")
#read in data 
data <- read.table("pdata",header=TRUE,sep="\t")
#split data by even column and odd
L<-length(data[,1])
even_data<-data[seq(0,L,+2),]
odd_data<-data[seq(1,L,+2),]
#use even data as training sets
t<-system.time(data.tree <- C5.0(formula=label~.,data=even_data))
#predict and calculate accuracy rate
p <- predict(data.tree,odd_data,type="class")
confMat <-table(odd_data$label,p)
accuracy <- sum(diag(confMat))/sum(confMat)

#print out the result 
print(summary(data.tree))
print("Accuracy")
print(accuracy)
print(t)
