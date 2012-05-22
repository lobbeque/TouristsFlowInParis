/*

Copyright : UMR Géographie Cités - Quentin Lobbé (2012)

Authors : 
Quentin Lobbé <quentin.lobbe@gmail.com>
Julie Fen-Chong <julie.fenchong@gmail.com>
Julian Bilcke <julian.bilcke@iscpif.fr>

This file is a part of TouristsFlowInParis Project

Build with Processing ( Ben Fry, Casey Reas ) ( GNU GPL )
Build with Unfolding ( Till Nagel, Felix Lange ) ( BSD )


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

import processing.core.*;
import de.fhpotsdam.unfolding.geo.Location;

/**
 *
 * @author Quentin lobbé
 */
public class Edge {

    public static void afficheEdge() {
        PApplet p = App.db.getPApplet();

        for (int i = 0; i < App.db.getTableauGephi()[App.db.getIndex()].edgeCount; i++) {
            // Transformation des coordonnées de chaque edge
            Location l1 = new Location(App.db.getMatEdge(0, i), App.db.getMatEdge(1, i));
            Location l2 = new Location(App.db.getMatEdge(2, i), App.db.getMatEdge(3, i));
            float xy1[] = App.db.getMap().getScreenPositionFromLocation(l1);
            float xy2[] = App.db.getMap().getScreenPositionFromLocation(l2);

            float distance = Misc.distFrom(l1.getLat(), l1.getLon(), l2.getLat(), l2.getLon());


            // Valuation de la transparence des edges en fonction de leur poids
            float value = App.db.getMatEdge(4, i);

            float epaisseur = 0;
            if (App.db.isLog()) {
                if (value > 2) {
                    epaisseur = PApplet.map(PApplet.log(value), PApplet.log(2), PApplet.log(App.db.getEdgeMax()), 1, 10);
                }
            } else if (App.db.isBoxCox()) {
                epaisseur = PApplet.map(Misc.CoxBox(value,'e'), 0, Misc.CoxBox(App.db.getEdgeMax(),'e'), 1, 10);        
            } else {
                epaisseur = PApplet.map(value, 2, App.db.getEdgeMax(), 1, 15);
            }
            p.strokeWeight(epaisseur);

            float transparence = 0;

            // Plus grande visibilité aux petits edges
            if (App.db.isPetit() && (!App.db.isGros())) {

                transparence = PApplet.map(distance, App.db.getDistMin(), App.db.getDistMax(), (float) 0.18, 10);
            }

            // Plus grande visibilité aux edges de poids forts 
            if (App.db.isGros() && (!App.db.isPetit())) {

                transparence = PApplet.map(value, 0, App.db.getEdgeMax(), 15, 255);

                if (value < 2) {
                    transparence = 0;
                }
                p.stroke(149, 32, 35, transparence);
            }
            if (App.db.isGros() && (!App.db.isPetit())) {
                if ((((xy1[1] > 0) && (xy1[0] > 0) && (xy1[0] < p.width) && (xy1[1] < p.height)) || ((xy2[0] > 0) && (xy2[1] > 0) && (xy2[0] < p.width) && (xy2[1] < p.height))) && (App.db.getMatEdge(4, i) > 2)) { // on filtre en affichant uniquement les 2000 liens les plus forts situés dans la zone de viz
                    p.line(xy1[0], xy1[1], xy2[0], xy2[1]);
                }
            } else {
                if (((xy1[1] > 0) && (xy1[0] > 0) && (xy1[0] < p.width) && (xy1[1] < p.height)) || ((xy2[0] > 0) && (xy2[1] > 0) && (xy2[0] < p.width) && (xy2[1] < p.height))) {
                    p.stroke(16, 91, 136, PApplet.exp(1 / transparence));
                    p.line(xy1[0], xy1[1], xy2[0], xy2[1]);

                }
            }
        }
    }
}
