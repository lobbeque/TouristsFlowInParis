### import csv to Gexf graph file ###

import csv
from gexf import Gexf

reader1 = csv.reader(open("bts.csv","rb1"))
reader2 = csv.reader(open("graphe_fran_03_30_allday.csv","rb2"))

bts = {}
temp = {}

# CSV bts parsing 
for row in reader1 :
	array = row[0].split(';')
	id = str(array[2])
	lat = array[0] 
	lon = array[1]
	bts[id]=(lat,lon)

# create graph
gexf = Gexf("Fabien & Julie & Quentin","paris french people whole day")
graph = gexf.addGraph("directed","static","world graph map")

# add Attribute
latitude = graph.addNodeAttribute("lat", "0", "double")
longitude = graph.addNodeAttribute("lon", "0", "double")
duration = graph.addEdgeAttribute("temp","0", "str")



edgeid = 1
for row in reader2 :
	# CSV edge parsing
	array = row[0].split(';')
	source = array[0]
	target = array[1] 
	duration_edge = array[2]
	weight = array[3]
	
	if source != "source" and target != "target" :
		
		# add node
		if source not in temp.keys():
			(lat,lon) = bts[str(source)] 
			node = graph.addNode("%s" % str(source), "%s"% str(source))
			node.addAttribute(latitude, str(lat))
			node.addAttribute(longitude, str(lon))
			temp[source] = (lat,lon)

		if target not in temp.keys():
			(lat,lon) = bts[str(target)] 
			node = graph.addNode("%s" % str(target), "%s"% str(target))
			node.addAttribute(latitude, str(lat))
			node.addAttribute(longitude, str(lon))
			temp[target] = (lat,lon)
		
		# add edge
		edge = graph.addEdge("%s" % edgeid, "%s" % str(source), "%s" % str(target), "%s" % str(weight))
		edge.addAttribute(duration, str(duration_edge))
		edgeid += 1
		print edgeid


# write file
output_file=open("./gexf-wholeday" + "/" + "frenchWholeday.gexf","w+")
gexf.write(output_file)

