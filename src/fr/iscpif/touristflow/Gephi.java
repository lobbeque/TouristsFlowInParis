/*

Copyright Quentin Lobbé (2012)
Author : Quentin Lobbé <quentin.lobbe@gmail.com>
Contributors : Julian Bilcke && Fabien Pfaender 

This file is a part of TouristsFlowInParis Project

Build with Gephi ( Mathieu Bastian, Gephi.org ) ( GNU GPL )


This software is a computer program whose purpose is to manipulate, 
explore and visualize large graphs of flow through time and space .
This software is governed by the CeCILL license under French law and 
abiding by the rules of distribution of free software. You can use, 
modify and/ or redistribute the software under the terms of the CeCILL 
license as circulated by CEA, CNRS and INRIA at the following URL 
"http://www.cecill.info".


As a counterpart to the access to the source code and rights to copy, 
modify and redistribute granted by the license, users are provided only with a 
limited warranty and the software's author, the holder of the economic rights, 
and the successive licensors have only limited liability.


In this respect, the user's attention is drawn to the risks associated with loading, 
using, modifying and/or developing or reproducing the software by the user in light 
of its specific status of free software, that may mean that it is complicated to manipulate, 
and that also therefore means that it is reserved for developers and experienced professionals 
having in-depth computer knowledge. Users are therefore encouraged to load and test 
the software's suitability as regards their requirements in conditions enabling the security 
of their systems and/or data to be ensured and, more generally, to use and operate it in 
the same conditions as regards security.


The fact that you are presently reading this means that you have had knowledge of the CeCILL 
license and that you accept its terms.
 
 */


package fr.iscpif.touristflow;

import java.io.File;
import static java.lang.System.*;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeController;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.graph.api.DirectedGraph;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.EdgeDefault;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.processor.plugin.DefaultProcessor;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.openide.util.Lookup;

/**
 *
 * @author Fabien Pfaender && Quentin lobbé
 */
public class Gephi {

    double[] btsLat;
    double[] btsLon;
    int[] btsDegree;
    double[] btsAttribut1;
    int nodeCount;
    int edgeCount;
    double[][] edge;

    Gephi() {
    }

    void loadG(String path) {

        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        pc.newProject();
        Workspace workspace = pc.getCurrentWorkspace();

        //Import file 
        ImportController importController = Lookup.getDefault().lookup(ImportController.class);
        Container container;
        try {
            File file = new File(path);
            container = (Container) importController.importFile(file);
            container.getLoader().setEdgeDefault(EdgeDefault.DIRECTED);	//Force DIRECTED container.setAllowAutoNode(false); //Don’t create missing nodes
        } catch (Exception ex) {
            return;
        }

        //Append imported data to GraphAPI
        importController.process(container, new DefaultProcessor(), workspace);

        //Get a graph model - it exists because we have a workspace
        GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getModel();

        DirectedGraph directedGraph = graphModel.getDirectedGraph();
        nodeCount = directedGraph.getNodeCount();
        edgeCount = directedGraph.getEdgeCount();
        edge = new double[edgeCount][5];
        
        out.println(edgeCount);
        out.println(nodeCount);

        btsLat = new double[nodeCount];
        btsLon = new double[nodeCount];
        btsDegree = new int[nodeCount];
        btsAttribut1 = new double[nodeCount];

        AttributeController ac = Lookup.getDefault().lookup(AttributeController.class);
        AttributeModel model = ac.getModel();



        //Iterate values - fastest
        AttributeColumn latCol = model.getNodeTable().getColumn("lat");
        AttributeColumn lonCol = model.getNodeTable().getColumn("lon");
        AttributeColumn att1Col = null;
        //s'il y a des attributs supplémentaires 
        if (model.getNodeTable().countColumns() > 4) {
            att1Col = model.getNodeTable().getColumn(4);
        }

        //Iterate values - normal
        int i = 0;
        for (org.gephi.graph.api.Node n : graphModel.getGraph().getNodes()) {
            // enlever le commentaire pour ignorer les antennnes de degré inférieur à 10
            //if (directedGraph.getDegree(n) < 100.0) continue;

            btsLat[i] = (Double) n.getNodeData().getAttributes().getValue(latCol.getIndex());
            btsLon[i] = (Double) n.getNodeData().getAttributes().getValue(lonCol.getIndex());
            btsDegree[i] = directedGraph.getDegree(n);
            if (att1Col != null) {
                btsAttribut1[i] = (Double) n.getNodeData().getAttributes().getValue(att1Col.getIndex());
            }

            //calcul des min et max
            if (btsDegree[i] > Application.session.getNodeMax()) {
                Application.session.setNodeMax((float) btsDegree[i]);
            }
            if (btsDegree[i] < Application.session.getNodeMin()) {
                Application.session.setNodeMin((float) btsDegree[i]);
            }

            i++;
        }
        Bibliotheque.maxNodeTot(nodeCount);

        i = 0;
        for (org.gephi.graph.api.Edge e : directedGraph.getEdges()) {
            // enlever le commentaire pour ignorer les liens inférieurs à 10 en poids
            //if (e.getWeight() < 10.0) continue;

            edge[i][0] = (Double) e.getSource().getNodeData().getAttributes().getValue(latCol.getIndex());
            edge[i][1] = (Double) e.getSource().getNodeData().getAttributes().getValue(lonCol.getIndex());
            edge[i][2] = (Double) e.getTarget().getNodeData().getAttributes().getValue(latCol.getIndex());
            edge[i][3] = (Double) e.getTarget().getNodeData().getAttributes().getValue(lonCol.getIndex());
            edge[i][4] = new Double(e.getWeight());

            //calcul des min et max
            if (edge[i][4] > Application.session.getEdgeMax()) {
                Application.session.setEdgeMax((float) edge[i][4]);
            }
            if (edge[i][4] < Application.session.getEdgeMin()) {
                Application.session.setEdgeMin((float) edge[i][4]);
            }

            i++;
        }
        Bibliotheque.maxEdgeTot(edgeCount);
        out.println(btsLon[1] + " " + btsLat[1]);

    }
}
