library(readr)

getEntropy <- function(filePath){
  fi <- read_delim(filePath, 
                   " ", escape_double = FALSE, col_names = FALSE, 
                   trim_ws = TRUE, skip = 1)
  colnames(fi)[1] <- "filePath"
  colnames(fi)[2] <- "Entropy"
  colnames(fi)[3] <- "CE_AVG"
  colnames(fi)[4] <- "CE_LEFT"
  colnames(fi)[5] <- "CE_RIGHT"
  colnames(fi)[6] <- "CE_DIFF"

  
  fi$commit <- apply(fi,1,function(x){strsplit(x,"/")[[1]][8]})
  fi$filename <- apply(fi,1,function(x){strsplit(x,"/")[[1]][10]})
  
  return(fi)
}

getUniqueFiles <- function(fi){
  
  #Gets you the unique list of names in atmosphere
  unique_files <- as.data.frame(unique(fi$filename))
  colnames(unique_files)[1] <- "filename"
  
  #Compute the count of all the unique files. Might get errors if these columns already exist
  unique_files$count <- apply(unique_files,1, function(x){length(which(grepl(x,fi$filename)))})
  unique_files$list <- apply(unique_files, 1, function(x){toString(which(grepl(x,fi$filename)))})
  return(unique_files)
}

wat <- apply(atmos, 1, function(x){ atmosFiles[atmosFiles$filename==x[8], ][[3]] <- paste(atmosFiles[atmosFiles$filename==x[8], ][[3]],"2",sep=" ") })

#Aggregate projects
atmos <- getEntropy("/Users/ashleychen/desktop/reuse/reuse/Analysis/Entropy/atmosphere_2_entropy.txt")
atmosFiles <- getUniqueFiles(atmos)
atmos_buggy <- getEntropy("/Users/ashleychen/desktop/reuse/reuse/Analysis/Entropy/atmosphere_2_entropy_buggy.txt")
netty <- getEntropy("/Users/ashleychen/desktop/reuse/reuse/Analysis/Entropy/netty_1_entropy.txt")
nettyFiles <- getUniqueFiles(netty)
netty_buggy <- getEntropy("/Users/ashleychen/desktop/reuse/reuse/Analysis/Entropy/netty_1_entropy_buggy.txt")
openjpa <- getEntropy("/Users/ashleychen/desktop/reuse/reuse/Analysis/Entropy/openjpa_1_entropy.txt")
openjpa_buggy <- getEntropy("/Users/ashleychen/desktop/reuse/reuse/Analysis/Entropy/openjpa_1_entropy_buggy.txt")
openjpaFiles <- getUniqueFiles(openjpa)
elasticsearch <- getEntropy("/Users/ashleychen/desktop/reuse/reuse/Analysis/Entropy/elasticsearch_1_entropy.txt")
elasticsearch_buggy <- getEntropy("/Users/ashleychen/desktop/reuse/reuse/Analysis/Entropy/elasticsearch_1_entropy_buggy.txt")
elasticsearchFiles <- getUniqueFiles(elasticsearch)
derby <- getEntropy("/Users/ashleychen/desktop/reuse/reuse/Analysis/Entropy/derby_1_entropy.txt")
derby_buggy <- getEntropy("/Users/ashleychen/desktop/reuse/reuse/Analysis/Entropy/derby_1_entropy_buggy.txt")
derbyFiles <- getUniqueFiles(derby)

openjpaToDerby <- getEntropy("/Users/ashleychen/desktop/reuse/reuse/Analysis/Entropy/openjpatoderby_1_entropy.txt")
openjpaToDerby_buggy <- getEntropy("/Users/ashleychen/desktop/reuse/reuse/Analysis/Entropy/openjpatoderby_1_entropy_buggy.txt")
derbyToOpenjpa <- getEntropy("/Users/ashleychen/desktop/reuse/reuse/Analysis/Entropy/derbytoopenjpa_1_entropy_buggy.txt")
derbyToOpenjpa_buggy <- getEntropy("/Users/ashleychen/desktop/reuse/reuse/Analysis/Entropy/derbytoopenjpa_1_entropy_buggy.txt")
openjpaToAtmosphere <- getEntropy("/Users/ashleychen/desktop/reuse/reuse/Analysis/Entropy/openjpatoatmosphere_1_entropy.txt")
openjpaToAtmosphere_buggy <- getEntropy("/Users/ashleychen/desktop/reuse/reuse/Analysis/Entropy/openjpatoatmosphere_1_entropy_buggy.txt")

openjpaToDerby$group <- "fixed"
openjpaToDerby_buggy$group <- "not_fixed"
derbyToOpenjpa$group <- "fixed"
derbyToOpenjpa_buggy$group <- "not_fixed"
openjpaToAtmosphere$group <- "fixed"
openjpaToAtmosphere_buggy$group <- "not_fixed"

#Prepare data frames
atmos$group <- "fixed"
atmos$project <- "atmosphere"
atmos_buggy$group <- "not_fixed"
atmos_buggy$project <- "atmosphere"

openjpa$group <- "fixed"
openjpa$project <- "openjpa"
openjpa_buggy$group <- "not_fixed"
openjpa_buggy$project <- "openjpa"

netty$group <- "fixed"
netty$project <- "netty"
netty_buggy$group <- "not_fixed"
netty_buggy$project <- "netty"

elasticsearch$group <- "fixed"
elasticsearch$project <- "elasticsearch"
elasticsearch_buggy$group <- "not_fixed"
elasticsearch_buggy$project <- "elasticsearch"

derby$group <- "fixed"
derby$project <- "elasticsearch"
derby_buggy$group <- "not_fixed"
derby_buggy$project <- "elasticsearch"



sum(atmosphere.testCount,derby.testCount,elasticsearch.testCount,openjpa.testCount,netty.testCount)*2

#combine
dat <- rbind(atmos,atmos_buggy,openjpa,openjpa_buggy, netty, netty_buggy, elasticsearch, elasticsearch_buggy)
dat_all <- rbind(atmos,atmos_buggy,openjpa,openjpa_buggy,netty,netty_buggy,elasticsearch,elasticsearch_buggy,derby, derby_buggy)
atmosphere_all <- rbind(atmos,atmos_buggy)
derby_all <- rbind(derby,derby_buggy)
elasticsearch_all <- rbind(elasticsearch,elasticsearch_buggy)
netty_all <- rbind(netty,netty_buggy)
openjpa_all <- rbind(openjpa,openjpa_buggy)

openjpaToAtmosphere_all <- rbind(openjpaToAtmosphere,openjpaToAtmosphere_buggy)
openjpaToDerby_all <- rbind(openjpaToDerby,openjpaToDerby_buggy)
derbyToOpenjpa_all <- rbind(derbyToOpenjpa,derbyToOpenjpa_buggy)


#write
write.csv(dat, file = "dat.csv",row.names=FALSE)
write.csv(atmosphere_all, file = "atmosphere.csv",row.names=FALSE)
write.csv(derby_all, file = "derby.csv",row.names=FALSE)
write.csv(elasticsearch_all, file = "elasticsearch.csv",row.names=FALSE)
write.csv(netty_all, file = "netty.csv",row.names=FALSE)
write.csv(openjpa_all, file = "openjpa.csv",row.names=FALSE)

#Quick Summary Entropy
mean(dat$Entropy[dat$group=="not_fixed"])
mean(derby$Entropy)
mean(derby_buggy$Entropy)
mean(dat$Entropy[dat$group=="fixed"]) - mean(dat$Entropy[dat$group=="not_fixed"])
summary(dat$Entropy[dat$group=="fixed"])
summary(dat$Entropy[dat$group=="not_fixed"])

#T Test
t.test(atmos$Entropy,atmos_buggy$Entropy,alternative = c("l"))

#Mann-Whitney-Wilcoxon Test
wilcox.test(atmos$CE_AVG,atmos_buggy$CE_AVG,conf.level = .95)
wilcox.test(openjpa$CE_AVG,openjpa_buggy$CE_AVG,conf.level = .95)
wilcox.test(netty$CE_AVG,netty_buggy$CE_AVG,conf.level = .95)
wilcox.test(elasticsearch$CE_AVG,elasticsearch_buggy$CE_AVG,conf.level = .95)
wilcox.test(derby$CE_AVG,derby_buggy$CE_AVG,conf.level = .95)
wilcox.test(dat$CE_AVG[dat$group=="fixed"],dat$CE_AVG[dat$group=="not_fixed"],conf.level = .95)
wilcox.test(dat_all$CE_AVG[dat$group=="fixed"],dat_all$CE_AVG[dat$group=="not_fixed"],conf.level = .95)
wilcox.test(openjpaToAtmosphere_all$CE_AVG[openjpaToAtmosphere_all$group=="fixed"],openjpaToAtmosphere_all$CE_AVG[openjpaToAtmosphere_all$group=="not_fixed"],conf.level = .95)

#Boxplot for comparig fix and not_fix
boxplot(dat$Entropy[dat$group=="fixed"],dat$Entropy[dat$group=="not_fixed"],names=c("Fixed","Buggy"),xlab="Change Quality",ylab="Change Entropy")
boxplot(dat$CE_AVG[dat_all$group=="fixed"],dat_all$CE_AVG[dat$group=="not_fixed"],names=c("Fixed","Buggy"),xlab="Change Quality",ylab="Change Cross-Entropy",outline=FALSE)

boxplot(atmos$Entropy,atmos_buggy$Entropy,names=c("Fixed", "Not Fixed"))
boxplot(netty$Entropy,netty_buggy$Entropy, elasticsearch$Entropy, elasticsearch_buggy$Entropy,names=c("netty_f","netty_b","elastic_f","elastic_b"))
boxplot(elasticsearch$Entropy,elasticsearch_buggy$Entropy,names=c("Fixed","Not Fixed"))
boxplot(openjpa$Entropy,openjpa_buggy$Entropy,names=c("Fixed", "Not Fixed"))
boxplot(derby$Entropy,derby_buggy$Entropy,names=c("Fixed","Not Fixed"))
boxplot(dat$Entropy[dat$group=="fixed"],dat$Entropy[dat$group=="not_fixed"],names=c("Fixed","Not Fixed"))

atmos_file<- rbind(subset(atmos_buggy, atmos_buggy$filename=="GlassFishWebSocketSupport.java"), subset(atmos,atmos$filename=="GlassFishWebSocketSupport.java"))
elastic_file<- rbind(subset(elasticsearch_buggy, elasticsearch_buggy$filename=="ZenDiscovery.java"), subset(elasticsearch,elasticsearch$filename=="ZenDiscovery.java"))
open_file<- rbind(subset(openjpa_buggy, openjpa_buggy$filename=="DB2Dictionary.java"), subset(openjpa,openjpa$filename=="DB2Dictionary.java"))
netty_file<- rbind(subset(netty_buggy, netty_buggy$filename=="AbstractChannel.java"), subset(netty,netty$filename=="AbstractChannel.java"))
derby_file<- rbind(subset(derby, derby$filename=="ElasticsearchIntegrationTest.java"))
dat_file <- rbind(atmos_file,elastic_file,open_file,netty_file,derby_file)
#Plot of entropy over index
plot(atmos_buggy$Entropy,type="l",col="red")
lines(atmos$Entropy,col="green")

atmos_file$source <- "fix"
glassEntropy$commit <- glassEntropy$comm
atmos_file$commit
glassCombine <- rbind(atmos_file,glassEntropy)

tmp <- subset(glassCombine, duplicated(commit))

library(ggplot2)
ggplot(aes(x = commit, y = Entropy, color = group), data = atmos_file) + geom_line() + geom_point()
ggplot(aes(x = commit, y = Entropy), data = glassEntropy) + geom_line() + geom_point()

ggplot(aes(x = commit, y = Entropy, color=source, group=commit), data = glassCombine) + 
  geom_line() + 
  geom_point() + 
  scale_colour_manual(values=c("green", "red")) +
  scale_fill_manual(values=c("black", "white"))

ggplot(aes(x = commit, y = Entropy), data = elastic_file) + geom_line() + geom_point()
ggplot(aes(x = commit, y = Entropy, color = group), data = open_file) + geom_line() + geom_point()
ggplot(aes(x = commit, y = Entropy, color = group), data = netty_file) + geom_line() + geom_point()
ggplot(aes(x = commit, y = Entropy, color = group), data = derby_file) + geom_line() + geom_point()
ggplot(aes(x = commit, y = Entropy, color = project), data = dat_file) + geom_line() + geom_point()

#Plot of specific file entropy over bug fixes
plot(atmos$Entropy[atmos$filename == "Processor.java"])