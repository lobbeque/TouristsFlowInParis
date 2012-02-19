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

import de.fhpotsdam.unfolding.geo.Location;
import processing.core.*;
import static java.lang.System.*;

/**
 *
 * @author Quentin Lobbé
 *  
 */

/*
 * Cette classe redistribue le graphe sur l'espace géographique
 * Elle normalise ainsi la distribution des capteurs de flux ( BTS ) 
 */
public class Redistribution {

    public static float X = 0;
    public static float Y = 0;
    public static final Location[] mat = new Location[100000];
    public static float Edge[][] = Application.session.getMatEdge();
    public static float Node[][] = new float[4][mat.length];
    public static float cellule = 0;

    public static void getGrilleAgreger(float n) {
        PApplet p = Application.session.getPApplet();
        cellule = n;
        float d = Bibliotheque.meter2Pixel(n);
        Location l1 = Application.session.getMap().getLocationFromScreenPosition(0, 0);
        Location l2 = Application.session.getMap().getLocationFromScreenPosition(d, d);
        X = l1.getLat() - l2.getLat();
        Y = l2.getLon() - l1.getLon();

        int cpt = 0;

        for (float i = (float) 48.1437; i < 49.1729; i = (float) (i + X)) {
            for (float j = (float) 1.48335; j < 3.4472; j = (float) (j + Y)) {
                mat[cpt] = new Location(i, j);
                cpt++;
            }
        }
        out.println("done");
        out.println(cpt);
    }

    public static void printGrille() {
        PApplet p = Application.session.getPApplet();
        for (int i = 0; i < Node[0].length; i++) {
            if (Node[1][i] > 0) {
                Location l = new Location(Node[1][i], Node[2][i]);
                float poids = PApplet.map(Node[3][i], 0, 1000, 0, 15);
                float xy[] = Application.session.getMap().getScreenPositionFromLocation(l);
                if ((0 < xy[0]) && (xy[0] < p.width) && (0 < xy[1]) && (xy[1] < p.height)) {
                    p.ellipse(xy[0], xy[1], 1, 1);
                }
            }
        }
        for (int j = 0; j < Edge[0].length; j++) {
            if (Edge[0][j] > 0) {
                Location l1 = new Location(Edge[0][j], Edge[1][j]);
                Location l2 = new Location(Edge[2][j], Edge[3][j]);
                float xy1[] = Application.session.getMap().getScreenPositionFromLocation(l1);
                float xy2[] = Application.session.getMap().getScreenPositionFromLocation(l2);

                float value = Application.session.getMatEdge(4, j);
                float poids = PApplet.map(value, 2, Application.session.getEdgeMax(), 1, 15);
                p.strokeWeight(poids);
                float a = PApplet.map(value, 0, Application.session.getEdgeMax(), 15, 255);
                if (value < 2) {
                    a = 0;
                }

                p.stroke(16, 91, 136, a);


                p.line(xy1[0], xy1[1], xy2[0], xy2[1]);
            }
        }
    }

    public static boolean isBTS(Location l) {
        boolean ret = false;
        for (int i = 0; i < Application.session.getNBRoamBTSMoy(0).length; i++) {
            Location l1 = new Location(Application.session.getNBRoamBTSMoy(2, i), Application.session.getNBRoamBTSMoy(1, i));
            if ((l.getLat() <= l1.getLat()) && (l1.getLat() < (l.getLat() + X)) && (l.getLon() <= l1.getLon()) && (l1.getLon() < l.getLon() + Y)) {
                ret = true;
                break;
            }
        }
        return ret;
    }

    public static boolean isIN(Location l, Location BTS) {
        boolean ret = false;
        if ((l.getLat() <= BTS.getLat()) && (BTS.getLat() < (l.getLat() + X)) && (l.getLon() <= BTS.getLon()) && (BTS.getLon() < l.getLon() + Y)) {
            ret = true;
        }
        return ret;
    }

    public static void Agreger() {
        PApplet p = Application.session.getPApplet();
        Edge = Application.session.getMatEdge();
        Node = new float[4][mat.length];
        int cpt = 0;
        for (int i = 0; i < mat.length; i++) {
            if (mat[i] != null) {
                if (mat[i].getLat() > 0) {
                    if (isBTS(mat[i])) {

                        //id
                        Node[0][cpt] = cpt + 1;
                        //lat
                        Node[1][cpt] = mat[i].getLat() + X / 2;
                        //lon
                        Node[2][cpt] = mat[i].getLon() + Y / 2;
                        //poids
                        Node[3][cpt] = 0;

                        for (int j = 0; j < Edge[0].length; j++) {
                            if (Edge[0][j] > 0) {
                                Location source = new Location(Edge[0][j], Edge[1][j]);
                                Location target = new Location(Edge[2][j], Edge[3][j]);
                                if (isIN(mat[i], source) && isIN(mat[i], target)) {

                                    Node[3][cpt] = Node[3][cpt] + Edge[4][j];
                                    Edge[0][j] = 0;
                                    out.println(Node[3][cpt]);
                                } else if (isIN(mat[i], source)) {
                                    Edge[0][j] = Node[0][cpt];
                                    //Edge[1][j] = Node[2][cpt];
                                } else if (isIN(mat[i], target)) {
                                    Edge[2][j] = Node[0][cpt];
                                    //Edge[3][j] = Node[2][cpt];
                                }
                            }
                        }
                        cpt++;

                        out.println(cpt);
                    }
                }
            }
        }
        
        writeCSV();
    }

    public static void writeCSV() {
        PApplet p = Application.session.getPApplet();

        // un fichier pour les noeuds

        String[] linesNode = {};
        linesNode = PApplet.append(linesNode, "id;Lat;Lon;Flux_Interne");
        for (int i = 0; i < Node[0].length; i++) {
            if (Node[0][i] > 0) {
                String words = Integer.toString((int)Node[0][i]) + ';' + Float.toString(Node[1][i]) + ';' + Float.toString(Node[2][i]) + ';' + Float.toString(Node[3][i]);
                linesNode = PApplet.append(linesNode, words);
            }
        }
        p.saveStrings("Aggregation noeud_" + cellule + " m_" + Temps.getDateText() + ".csv", linesNode);

        // un fichier pour les arcs

        String[] linesEdge = {};
        linesEdge = PApplet.append(linesEdge, "source;target;Poids");
        for (int i = 0; i < Edge[0].length; i++) {
            if (Edge[0][i] > 0) {
                String words = Integer.toString((int)Edge[0][i]) + ';' + Integer.toString((int)Edge[2][i]) + ';' + Float.toString(Edge[4][i]);
                linesEdge = PApplet.append(linesEdge, words);
            }
        }
        p.saveStrings("Aggregation Arcs_" + cellule + " m_" + Temps.getDateText() + ".csv", linesEdge);
    }
}
