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
public class Node {
    
    

    public static boolean hide = false;

    public static void afficheNode() {
        PApplet p = App.db.getPApplet();
        float radius = 0;
        for (int i = (int) App.db.getTableauGephiCount(App.db.getIndex(), 0) - 1; i >= 0; i--) {
            p.noStroke();
            // Transformation des coordonnées de chaque noeud !!! Attention il faut toujours inverser la latitude et la longitude
            Location l = new Location(App.db.getMatNode(0, i), App.db.getMatNode(1, i));
            float xy[] = App.db.getMap().getScreenPositionFromLocation(l);


            // Valuation de la taille du noeuds en fonction de son poids
            float degree = App.db.getMatNode(2, i);


            if (!App.db.isBoxCoxNode()) {
                radius = PApplet.map(degree, App.db.getNodeMin(), App.db.getNodeMax(), 1, 10);
            } else {
                radius = PApplet.map(Bibliotheque.CoxBox(degree, 'n'), Bibliotheque.CoxBox(App.db.getNodeMin(), 'n'), Bibliotheque.CoxBox(App.db.getNodeMax(), 'n'), 1, 10);
            }
            p.ellipseMode(PConstants.RADIUS);
            float zoom = App.db.getMap().getZoom();
            if ((App.db.isNode()) && (!App.db.isChaud())) {
                //p.fill(182, 92, 96, 200);
                p.fill(38,155,225,200);
                if (!hide) {
                    p.ellipse(xy[0], xy[1], radius * PApplet.exp(zoom / 20000), radius * PApplet.exp(zoom / 20000));
                }
                p.fill(178, 206, 255, 200);

                p.ellipse(xy[0], xy[1], radius * PApplet.exp(zoom / 20000) / 5, radius * PApplet.exp(zoom / 20000) / 5);

            }

            //mode Heatmap
            if (App.db.isChaud()) {
                HeatMap.drawHeatMap((int) xy[0], (int) xy[1], (int) radius, (int) degree);
            }

            float d = PApplet.dist(xy[0], xy[1], p.mouseX, p.mouseY);

            //mode sélection
            if ((d < radius + 2) && App.db.isClicked() && (!App.db.isSelect()) && (!App.db.isOursin())) {
                Affichage.selection(xy[0], xy[1], radius, i);
            }

        }
    }

    public static boolean getHide() {
        return hide;
    }

    public static void setHide(boolean a) {
        hide = a;
    }
}
