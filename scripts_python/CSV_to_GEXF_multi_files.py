### import csv to Gexf graph file ###

import csv
import os
import sys
import re
import glob
from gexf import Gexf



# chemin vers le dossier des edges
path = '/home/guest/Bureau/24h' 

cpt = 1
for infile in glob.glob(os.path.join(path, '*.csv')):

	print " fichier : " + str(cpt)

	reader2 = csv.reader(open(infile,"rb2"))
	(PATH, FILENAME) = os.path.split(infile)
	(ShortName, Extension) = os.path.splitext(FILENAME)

	bts = {}
	temp = {}
	
	print str(bts)
	
	# chemin vers le fichier des bts
        reader1 = csv.reader(open("bts.csv","rb1"))
	
	# CSV bts parsing 
	for row in reader1 :
		array = row[0].split(';')
		id = str(array[2])
		lat = array[0] 
		lon = array[1]
		bts[id]=(lat,lon)

	print str (bts)

	# create graph
	gexf = Gexf("Fabien & Julie & Quentin","paris french people whole day")
	graph = gexf.addGraph("directed","static","world graph map")

	# add Attribute
	latitude = graph.addNodeAttribute("lat", "0", "double")
	longitude = graph.addNodeAttribute("lon", "0", "double")



	edgeid = 1
	for row in reader2 :		
	
		# CSV edge parsing
		array = row[0].split(';')
		source = array[0]
		target = array[1]
		weight = array[2]
	
		if source != "bts0" and target != "bts1" :
		
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
			edgeid += 1
			print edgeid


	# write file
	output_file=open("./gexf-24h" + "/" + ShortName + ".gexf","w+")
	gexf.write(output_file)

	cpt = cpt + 1

