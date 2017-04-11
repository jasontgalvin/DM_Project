library("rpart")
library("partykit")

data <- read.table("Preprocessed_data.txt",header=TRUE)
#read in data 
data <- read.table("Preprocessed_data.txt",header=TRUE,sep="\t")
#split data by even column and odd
L<-length(data[,1])
even_data<-data[seq(0,L,+2),]
odd_data<-data[seq(1,L,+2),]

even_data<-subset(even_data,select=-c(count))
odd_data<-subset(odd_data,select=-c(count))

t<-system.time(data.tree <- rpart(label~.,data=even_data,method="anova"))
plot(as.party(data.tree))

p <- predict(data.tree,odd_data,type="prob")
confMat <-table(odd_data$label,p)
accuracy <- sum(diag(confMat))/sum(confMat)

print(summary(data.tree))
print("accuracy")
print(accuracy)
print(t)
