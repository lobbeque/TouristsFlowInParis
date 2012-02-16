/*

Copyright Quentin Lobbé (2012)
Author : Quentin Lobbé <quentin.lobbe@gmail.com>
Contributor : Julian Bilcke

This file is a part of TouristsFlowInParis Project

Build with Processing ( Ben Fry, Casey Reas ) ( GNU GPL )
Build with Unfloding ( Till Nagel, Felix Lange ) ( BSD )


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
import java.util.Arrays;
import processing.core.*;
import static java.lang.System.*;

/**
 *
 * @author Quentin Lobbé
 */
public class KMeans {

    // nombre de clusters
    static int n = 3;
    // centroïdes des clusters
    static java.util.ArrayList Centroides = new java.util.ArrayList();
    // centroïdes mémoires de l'état précédent 
    static java.util.ArrayList OldCentroides = new java.util.ArrayList();
    // clusters
    static java.util.ArrayList Clusters = new java.util.ArrayList();
    // clusters mémoires de l'état précédent 
    static java.util.ArrayList OldClusters = new java.util.ArrayList();
    static int[] oursinsInit = {0};
    static boolean[] kPressed = {};
    static float rayonMax = 0;

    public static void KMeansClean() {
        while (Clusters.size() > 0) {
            Clusters.remove(0);
            OldClusters.remove(0);
            Centroides.remove(0);
            OldCentroides.remove(0);
        }
        while (oursinsInit.length > 1) {
            oursinsInit = PApplet.shorten(oursinsInit);
        }
        while (kPressed.length > 0) {
            kPressed = PApplet.shorten(kPressed);
        }
        oursinsInit[0] = 0;
        rayonMax = 0;
    }

    public static void kMeansInit() {
        PApplet p = Application.session.getPApplet();
        // création de n Matrices de 2 tableaux de 32 valeurs 
        // création de n Tableaux clusters 

        out.println("kMeansInit");

        // initialisation de la mémoire des vieux centroides
        float[][] OldInit = new float[2][32];
        for (int i = 0; i < 32; i++) {
            OldInit[0][i] = 0;
            OldInit[1][i] = 0;
        }



        for (int i = 0; i < n; i++) {
            Centroides.add(matriceInit());
            OldCentroides.add(OldInit);
            String[] OldInitClusters = {};
            OldClusters.add(OldInitClusters);
            String[] cluster = {};
            Clusters.add(cluster);
            kPressed = (boolean[])PApplet.append(kPressed, true);
        }

        // ce tableau va recueillir les résultats des calculs de distance 
        float[] resultat = new float[n];

        // placer les oursins dans des classes
        for (int i = 0; i < Application.session.getOursins().size(); i++) {
            Oursin oursin = (Oursin) Application.session.Oursins.get(i);

            for (int j = 0; j < n; j++) {
                float[][] centroide = (float[][]) Centroides.get(j);
                resultat[j] = distance(centroide, oursin.getEntrant(), oursin.getSortant());

                out.println("calcule distance : " + resultat[j]);

            }
            String[] cluster = (String[]) Clusters.get(minDistance(resultat));

            out.println("cluster : " + minDistance(resultat));

            // la référence de l'oursin est stocker dans le clusteur correspondant 
            String ref = Float.toString(oursin.xNative) + '_' + Float.toString(oursin.yNative);
            String[] newCluster = PApplet.append(cluster, ref);
            Clusters.remove(minDistance(resultat));
            Clusters.add(minDistance(resultat), newCluster);
            out.println(newCluster.length);
        }
        int i = 0;
        // test condition d'arrêt 
        do {
            out.println("Cluster size :" + Clusters.size());
            out.println("Old cluster size :" + OldClusters.size());
            out.println("iteration " + i);
            newCentroide();
            clustering();
            maxRayon();
            out.println(rayonMax);
            i++;
        } while (!ConditionArret());

        out.println("fin de l'algorithme");

    }

    public static void clustering() {

        // mise en mémoire
        for (int i = 0; i < n; i++) {
            String[] cluster = (String[]) Clusters.get(i);
            OldClusters.remove(i);
            OldClusters.add(i, cluster);
            String[] temp = {};
            Clusters.remove(i);
            Clusters.add(i, temp);
        }

        // ce tableau va recueillir les résultats des calculs de distance 
        float[] resultat = new float[n];
        // placer les oursins dans des classes
        for (int i = 0; i < Application.session.getOursins().size(); i++) {
            Oursin oursin = (Oursin) Application.session.Oursins.get(i);

            for (int j = 0; j < n; j++) {
                float[][] centroide = (float[][]) Centroides.get(j);
                resultat[j] = distance(centroide, oursin.getEntrant(), oursin.getSortant());

                out.println("calcule distance : " + resultat[j]);

            }
            String[] cluster = (String[]) Clusters.get(minDistance(resultat));

            out.println("cluster : " + minDistance(resultat));

            // la référence de l'oursin est stockée dans le clusteur correspondant 
            String ref = Float.toString(oursin.xNative) + '_' + Float.toString(oursin.yNative);
            String[] newCluster = PApplet.append(cluster, ref);
            Clusters.remove(minDistance(resultat));
            Clusters.add(minDistance(resultat), newCluster);
            out.println(newCluster.length);
        }
    }

    public static void drawCluster() {
        PApplet p = Application.session.getPApplet();
        for (int i = 0; i < n; i++) {
            String[] cluster = (String[]) Clusters.get(i);

            if (kPressed[i] == true) {
                for (int j = 0; j < cluster.length; j++) {


                    String[] coordo = cluster[j].split("_");
                    Location l = new Location(Float.parseFloat(coordo[0]), Float.parseFloat(coordo[1]));
                    float xy[] = Application.session.getMap().getScreenPositionFromLocation(l);
                    p.fill(255 * (i + 1) / n);
                    p.stroke(16, 91, 136);
                    p.ellipse(xy[0], xy[1], 15, 15);
                }
            }
        }
        float dist = PApplet.dist(0, p.height / 18 + 50, 0, p.height - 45);
        for (int i = 0; i < n; i++) {
            float y = dist * (i + 1) / (n + 1) + p.height / 18 + 50;
            float x = p.width - 80;

            p.stroke(10, 150);
            p.fill(190, 201, 186, 100);
            p.strokeWeight(2);
            p.rect(x - 75, y - 55, 150, 110);

            p.stroke(16, 91, 136, 220);
            p.fill(255 * (i + 1) / n);
            p.ellipse(x + 50, y, 25, 25);

            p.fill(0);
            PFont font1 = p.createFont("DejaVuSans-ExtraLight-", 12);
            p.textFont(font1);
            p.textMode(PConstants.CENTER);
            p.text("cluster " + (i + 1), x + 30, y - 35);
            p.noFill();
            float[][] centroide = (float[][]) Centroides.get(i);

            p.stroke(16, 91, 136, 220);
            p.fill(16, 91, 136, 220);
            for (int l = 0; l < 16; l++) {
                drawArc(l * PConstants.PI / 8 + PConstants.PI / 64, centroide[0][l * 2], centroide[0][l * 2 + 1], x - 20, y);
            }
            p.stroke(182, 92, 96, 220);
            p.fill(182, 92, 96, 220);
            for (int l = 0; l < 16; l++) {
                drawArc(l * PConstants.PI / 8 - PConstants.PI / 64, centroide[1][l * 2], centroide[1][l * 2 + 1], x - 20, y);
            }
            p.noFill();
            p.noStroke();

        }
    }

    public static void drawArc(float angle, float poids, float rayon, float x, float y) {
        PApplet p = Application.session.getPApplet();
        if ((poids != 0) || (rayon != 0)) {
            p.smooth();

            float r = PApplet.map(rayon * 80 / 2009, 0, rayonMax * 80 / 2009, 0, 50);


            float x1 = (r) * PApplet.cos(angle) + x;
            float y1 = (r) * PApplet.sin(angle) + y;


            if (poids <= 0) {
                p.strokeWeight((float) 0.5);
            } else {
                p.strokeWeight(poids);
            }
            p.line(x, y, x1, y1);
        }
        p.strokeWeight(2);
    }

    // création de tableaux représentant des oursins randoms
    public static float[][] matriceInit() {
        PApplet p = Application.session.getPApplet();
        float[][] mat = new float[2][32];
        int index = 0;
        out.println(Arrays.toString(oursinsInit));
        do {
            index = (int) p.random(0, Application.session.getOursins().size());
            out.println(Arrays.toString(oursinsInit));
            out.println(index);
        } while (member(oursinsInit, index));
        out.println("fin while");
        oursinsInit = PApplet.append(oursinsInit, index);
        Oursin oursin = (Oursin) Application.session.Oursins.get(index);
        for (int i = 0; i < 32; i++) {
            mat[0][i] = oursin.getEntrant(i);
            mat[1][i] = oursin.getSortant(i);
        }
        return mat;
    }

    public static float distance(float[][] mat, float[] vect0, float[] vect1) {
        float dif0 = 0;
        float dif1 = 0;
        float sum = 0;
        // on calcule la distance euclidienne entre les deux oursins 
        for (int i = 0; i < 32; i++) {
            dif0 = mat[0][i] - vect0[i];
            dif1 = mat[1][i] - vect1[i];
            sum = sum + PApplet.pow(dif0, 2) + PApplet.pow(dif1, 2);
        }
        sum = PApplet.pow(sum, (float) 0.5);
        return sum;
    }

    public static void newCentroide() {
        // pour chaque cluster
        for (int i = 0; i < n; i++) {
            // on regarde la liste des oursins pointés par lui 
            String[] cluster = (String[]) Clusters.get(i);
            float[][] centroide = (float[][]) Centroides.get(i);

            out.println(Arrays.toString(centroide[0]));


            float[][] temp = new float[2][32];

            for (int j = 0; j < 32; j++) {
                temp[0][j] = 0;
                temp[1][j] = 0;
            }



            for (int j = 0; j < cluster.length; j++) {
                // on va extraire les coordonnées de la String
                String[] coordo = cluster[j].split("_");
                float x = Float.parseFloat(coordo[0]);
                float y = Float.parseFloat(coordo[1]);
                // et chercher l'oursin correspondant 
                for (int k = 0; k < Application.session.getOursins().size(); k++) {
                    Oursin oursin = (Oursin) Application.session.Oursins.get(k);
                    if ((oursin.getXN() == x) && (oursin.getYN() == y)) {
                        for (int l = 0; l < 32; l++) {
                            //On somme un à un les paramêtres
                            temp[0][l] = temp[0][l] + oursin.getEntrant(l);
                            temp[1][l] = temp[1][l] + oursin.getSortant(l);
                        }
                    }
                }
            }

            for (int k = 0; k < 32; k++) {
                // on fait une moyenne 
                temp[0][k] = temp[0][k] / cluster.length;
                temp[1][k] = temp[1][k] / cluster.length;
            }

            out.println(Arrays.toString(temp[0]));

            // on nettoie la mémoire 
            OldCentroides.remove(i);
            // on conserve l'état précédent pour tester la condition d'arrêt
            OldCentroides.add(i, centroide);
            // on remove le centroide précédent de la liste courante
            Centroides.remove(i);
            // on ajoute le nouveau à sa place        
            Centroides.add(i, temp);

        }
    }

    public static boolean ConditionArret() {
        boolean egalite = true;
        int i = 0;
        while ((i < Clusters.size()) && (egalite)) {
            String[] cluster = (String[]) Clusters.get(i);
            String[] oldCluster = (String[]) OldClusters.get(i);
            int j = 0;
            while ((j < cluster.length) && (egalite)) {
                egalite = member2(oldCluster, cluster[j]);
                j++;
            }
            i++;
        }
        return egalite;
    }

    public static boolean member2(String[] tab, String element) {
        int i = 0;
        boolean egalite = false;
        while ((i < tab.length) && (!egalite)) {
            if (tab[i].equals(element)) {
                egalite = true;
            }
            i++;
        }
        return egalite;
    }

    public static boolean member(int[] tab, int element) {
        int i = 0;
        boolean egalite = false;
        while ((i < tab.length) && (!egalite)) {
            if (tab[i] == element) {
                egalite = true;
            }
            i++;
        }
        return egalite;
    }

    public static int minDistance(float[] resultat) {
        float min = PConstants.MAX_FLOAT;
        int index = 0;
        for (int i = 0; i < n; i++) {
            if (resultat[i] < min) {
                min = resultat[i];
                index = i;
            }
        }
        return index;
    }

    public static float getN() {
        return n;
    }

    public static void setN(int i) {
        n = i;
    }

    public static void maxRayon() {
        float max = 0;
        for (int i = 0; i < n; i++) {
            float[][] centroide = (float[][]) Centroides.get(i);
            for (int j = 0; j < 16; j++) {
                max = PApplet.max(centroide[0][j * 2 + 1], max);
                max = PApplet.max(centroide[1][j * 2 + 1], max);
            }
        }
        rayonMax = PApplet.max(max, rayonMax);

    }

    public static void pressed(float mouseX, float mouseY) {
        PApplet p = Application.session.getPApplet();
        float dist = PApplet.dist(0, p.height / 18 + 50, 0, p.height - 45);
        for (int i = 0; i < n; i++) {
            float x = p.width - 80 - 75;
            float y = dist * (i + 1) / (n + 1) + p.height / 18 + 50 - 55;
            if ((x <= mouseX) && (x + 150 >= mouseX) && (y <= mouseY) && (y + 110 >= mouseY)) {
                if (kPressed[i] == true) {
                    kPressed[i] = false;
                } else {
                    kPressed[i] = true;
                }
            }

        }
    }
}
