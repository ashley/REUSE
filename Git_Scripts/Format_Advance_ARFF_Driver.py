from Format_Advance_ARFF import *

file = FormatFile()
file.formatRelations()
file.formatAttributesShort(defaultAttributes())
file.formatData(getDatafromTxt())
f = open('Weka/Data-Complete.txt', 'w')
f.writelines(line + u'\n' for line in file.lines)
print "done"