from Format_Advance_ARFF import *


#f = open("/Users/ashleychen/Desktop/REUSE/REUSE/Repos/okhttp/1364_Accepted/1364_Accepted_INFO.txt",'r')


def reduced(attributeList, dataList):
	change = "0"
	for i in range(len(attributeList)):
		print i
		#tempA = attributeList[i]
		#attributeList[i] = change
		tempD = []
		for a in range(len(dataList)):
			tempD.append(dataList[a][i])
			dataList[a][i] = change
		
		

		file = FormatFile()
		file.formatRelations()
		

		file.formatAttributesShort(attributeList)	
		file.formatData(dataList)

		print dataList

		f = open('Weka/Data-'+str(i)+'-Complete.txt', 'w')
		f.writelines(line + u'\n' for line in file.lines)
		print "done"

		#attributeList.insert(i,tempA)

		for a in range(len(dataList)):
			dataList[a][i]=tempD[i]

reduced(defaultAttributes(),getDatafromTxt())

