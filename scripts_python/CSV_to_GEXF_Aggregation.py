### import csv to Gexf graph file ###

import csv
from gexf import Gexf

reader1 = csv.reader(open("Aggregation noeud_1000.0 m_31 March 2009 20h.csv","rb1"))
reader2 = csv.reader(open("Aggregation Arcs_1000.0 m_31 March 2009 20h.csv","rb2"))

bts = {}
temp = {}

# CSV bts parsing 
for row in reader1 :
	array = row[0].split(';')
	id = str(array[0])
	lat = array[1] 
	lon = array[2]
	flux = array[3]
	bts[id]=(lat,lon,flux)

# create graph
gexf = Gexf("Quentin","foreigner people")
graph = gexf.addGraph("directed","static","world graph map")

# add Attribute
latitude = graph.addNodeAttribute("lat", "0", "double")
longitude = graph.addNodeAttribute("lon", "0", "double")
flux_interne = graph.addNodeAttribute("flux_interne", "0", "double")



edgeid = 1
for row in reader2 :
	# CSV edge parsing
	array = row[0].split(';')
	source = array[0]
	target = array[1] 
	weight = array[2]
	
	if source != "source" and target != "target" :
		
		# add node
		if source not in temp.keys():
			(lat,lon,flux) = bts[str(source)] 
			node = graph.addNode("%s" % str(source), "%s"% str(source))
			node.addAttribute(latitude, str(lat))
			node.addAttribute(longitude, str(lon))
			node.addAttribute(flux_interne, str(flux))
			temp[source] = (lat,lon,flux)

		if target not in temp.keys():
			(lat,lon,flux) = bts[str(target)] 
			node = graph.addNode("%s" % str(target), "%s"% str(target))
			node.addAttribute(latitude, str(lat))
			node.addAttribute(longitude, str(lon))
			node.addAttribute(flux_interne, str(flux))
			temp[target] = (lat,lon,flux)
		
		# add edge
		edge = graph.addEdge("%s" % edgeid, "%s" % str(source), "%s" % str(target), "%s" % str(weight))
		edgeid += 1
		print edgeid


# write file
output_file=open("./gexf-wholeday" + "/" + "Aggregation graphe_1000.0 m_31 March 2009 20h.gexf","w+")
gexf.write(output_file)

