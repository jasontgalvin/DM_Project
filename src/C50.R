#import packages for C50
library("C50")
library("partykit")
#read in data 
data <- read.table("data1",header=TRUE)
test <- read.table("data2",header=TRUE)
#create tree and plot
t<-system.time(data.tree <- C5.0(formula=playtennis~.,data=data))
plot(as.party(data.tree))
#predict and calculate accuracy rate
p <- predict(data.tree,test,type="class")
confMat <-table(test$playtennis,p)
accuracy <- sum(diag(confMat))/sum(confMat)

#print out the result 
print(summary(data.tree))
print("Accuracy")
print(accuracy)
print(t)
