library("rpart")
library("C50")
library("partykit")

data <- read.table("data2",header=TRUE)
test <- read.table("data1",header=TRUE)

t<-system.time(data.tree <- rpart(playtennis~.,data=data,method="class"))
plot(as.party(data.tree))

p <- predict(data.tree,test,type="class")
confMat <-table(test$playtennis,p)
accuracy <- sum(diag(confMat))/sum(confMat)

print(summary(data.tree))
print("accuracy")
print(accuracy)
print(t)
