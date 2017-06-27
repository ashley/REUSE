#Import filenames first
library(readr)

getUniqueFiles <- function(filePath,filename){
  atmosphere_fileNames <- read_delim(filePath, 
                                     " ", escape_double = FALSE, col_names = FALSE, 
                                     trim_ws = TRUE)
  atmosphere <- atmosphere_fileNames
  colnames(atmosphere)[1] <- "filename"
  colnames(atmosphere)[2] <- "fileindex"
  
  #How the datafram should look
  head(atmosphere)
  
  #Gets you the unique list of names in atmosphere
  unique_files <- as.data.frame(unique(unlist(atmosphere$filename)))
  colnames(unique_files)[1] <- "filename"
  
  #Compute the count of all the unique files. Might get errors if these columns already exist
  unique_files$count <- apply(unique_files,1, function(x){length(which(grepl(x,atmosphere$filename)))})
  unique_files$list <- apply(unique_files, 1, function(x){toString(which(grepl(x,atmosphere$filename)))})
  
  #Print out unique list
  write.table(unique_files, file=paste(filename,"uniquefiles.csv",sep="_"))
}

#getUniqueFiles("/Users/ashleychen/desktop/reuse/reuse/Analysis/Filenames/atmosphere_fileNames.txt","atmosphere")
getUniqueFiles("/Users/ashleychen/desktop/reuse/reuse/Analysis/Filenames/derby_fileNames.txt","derby")
getUniqueFiles("/Users/ashleychen/desktop/reuse/reuse/Analysis/Filenames/elasticsearch_fileNames.txt","elasticsearch")
getUniqueFiles("/Users/ashleychen/desktop/reuse/reuse/Analysis/Filenames/netty_fileNames.txt","netty")
getUniqueFiles("/Users/ashleychen/desktop/reuse/reuse/Analysis/Filenames/openjpa_fileNames.txt","openjpa")

