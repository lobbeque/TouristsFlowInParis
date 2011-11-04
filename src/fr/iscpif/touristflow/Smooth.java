/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.iscpif.touristflow;

import processing.core.*;
import de.fhpotsdam.unfolding.geo.Location;

/**
 *
 * @author Quentin lobbé
 */
public class Smooth {

    public static float[][] buff1;
    public static float[] buff1Score;
    public static boolean init;
    //public static boolean nonInit = false;
    public static Location A;
    public static Location B;
    public static Location C;
    public static Location D;
    public static float ca = 0;

    public static void initBuff1() {
        PApplet p = Application.session.getPApplet();
        buff1 = new float[2][p.width * p.height / 8];
        for (int i = 0; i < buff1[0].length; i++) {
            buff1[0][i] = -1;
            buff1[1][i] = -1;
        }
        buff1Score = new float[p.width * p.height / 8];
        for (int k = 0; k < buff1Score.length; k++) {
            buff1Score[k] = -1;
        }

            init = false;
    }

    public static void lissage() {
        PApplet p = Application.session.getPApplet();
        int cpt = 0;
        if (!init) {
            premiereUtilisationBuffeur();
            calculScore1();
        } else {
            if (Application.session.isDraged()) {
              
              calculScore1();  
              Application.session.setDraged(false);
            }
            

        }
        cpt = 0;


        for (int i = 0; i < buff1[0].length; i++) {
            if (buff1[0][i] != -1) {
                p.noStroke();
                float percent = 0;
                percent = PApplet.norm(buff1Score[cpt], 1, Application.session.getNodeMax());//On attribut une color à nos petits carrés en fonction du résultat de la méthode

                if (percent > 0.16) {
                    int c = p.color(189, 73, 50);
                    p.fill(c, 100);
                    p.rect(buff1[0][i], buff1[1][i], 3, 3);
                } else if (percent > 0.12) {
                    int c = p.color(219, 158, 54);
                    p.fill(c, 100);
                    p.rect(buff1[0][i], buff1[1][i], 3, 3);
                } else if (percent > 0.8) {
                    int c = p.color(255, 211, 78);
                    p.fill(c, 100);
                    p.rect(buff1[0][i], buff1[1][i], 3, 3);
                } else if (percent > 0.1) {
                    int c = p.color(255, 250, 213);
                    p.fill(c, 100);
                    p.rect(buff1[0][i], buff1[1][i], 3, 3);
                } else {
                    int c = p.color(16, 91, 99);
                    p.fill(c, 100);
                    p.rect(buff1[0][i], buff1[1][i], 3, 3);
                }

                cpt++;
            }

        }

    }



    public static void calculScore1() {

        PApplet p = Application.session.getPApplet();
        int width = p.width;
        int height = p.height;
        int count = Application.session.getNodePourLissageCount() - 1;
        int zoom = (int) Application.session.getMap().getZoom();
        float DmaxOnScreen = Bibliotheque.meter2Pixel(Application.session.getDmax());
        int cpt = 0;
        for (float i = 0; i < width; i = i + 3) {//l'écran est découpé en petits carrés
            for (float j = 0; j < height; j = j + 3) {
                buff1[0][cpt]=i;
                buff1[1][cpt]=j;
                if (Application.session.isBiweight()) {
                    buff1Score[cpt] = Biweight(i, j, count, zoom, DmaxOnScreen, Application.session.getNodePourLissage());//Utilisation de la méthode de Biweight
                } else {
                    buff1Score[cpt] = Shepard(i, j);//Utilisation de la méthode de Biweight
                }
                cpt++;
            }
        }
    }

    public static void miseAJourCarteLissee() {
        PApplet p = Application.session.getPApplet();
        Application.session.setNodePourLissageCount(0);
        Application.session.setNodePourLissageHold(Application.session.getNodePourLissage());
        Application.session.setNodePourLissage(new float[3][Application.session.getTableauGephi()[Application.session.getIndex()].nodeCount]);
        for (int i = 0; i < Application.session.getTableauGephi()[Application.session.getIndex()].nodeCount; i++) {
            Location l = new Location(Application.session.getMatNode(0, i), Application.session.getMatNode(1, i));
            float xy[] = Application.session.getMap().getScreenPositionFromLocation(l);
            if (Application.session.getMap().getZoom() >= 16384) {
                if ((xy[0] < p.width + p.width) && (xy[1] < p.height + p.height) && (xy[0] > 0 - p.width) && (xy[1] > 0 - p.height)) { // nous travaillons sur un tableau regroupant les BTS visible à l'écran et non tout ceux visibles sur la carte
                    Application.session.setNodePourLissage(0, Application.session.getNodePourLissageCount(), xy[0]);
                    Application.session.setNodePourLissage(1, Application.session.getNodePourLissageCount(), xy[1]);
                    Application.session.setNodePourLissage(2, Application.session.getNodePourLissageCount(), Application.session.getMatNode(2, i));
                    Application.session.setNodePourLissageCount(Application.session.getNodePourLissageCount() + 1);
                }
            } else if (Application.session.getMap().getZoom() >= 8192) {
                if ((xy[0] < p.width + p.width / 2) && (xy[1] < p.height + p.height / 2) && (xy[0] > 0 - p.width / 2) && (xy[1] > 0 - p.height / 2)) { // nous travaillons sur un tableau regroupant les BTS visible à l'écran et non tout ceux visibles sur la carte
                    Application.session.setNodePourLissage(0, Application.session.getNodePourLissageCount(), xy[0]);
                    Application.session.setNodePourLissage(1, Application.session.getNodePourLissageCount(), xy[1]);
                    Application.session.setNodePourLissage(2, Application.session.getNodePourLissageCount(), Application.session.getMatNode(2, i));
                    Application.session.setNodePourLissageCount(Application.session.getNodePourLissageCount() + 1);
                }
            } else {
                if ((xy[0] < p.width) && (xy[1] < p.height) && (xy[0] > 0) && (xy[1] > 0)) { // nous travaillons sur un tableau regroupant les BTS visible à l'écran et non tout ceux visibles sur la carte
                    Application.session.setNodePourLissage(0, Application.session.getNodePourLissageCount(), xy[0]);
                    Application.session.setNodePourLissage(1, Application.session.getNodePourLissageCount(), xy[1]);
                    Application.session.setNodePourLissage(2, Application.session.getNodePourLissageCount(), Application.session.getMatNode(2, i));
                    Application.session.setNodePourLissageCount(Application.session.getNodePourLissageCount() + 1);
                }
            }
        }
        Application.session.setDmaxOnScreen(Bibliotheque.meter2Pixel(Application.session.getDmax()));
    }

    public static float Biweight(float i, float j, int count, int zoom, float DmaxOnScreen, float[][] tabNode) {
        PApplet p = Application.session.getPApplet();
        float sum1 = 1;
        float sum2 = 1;
        for (int k = 0; k < count; k++) {
            if (zoom <= 4096) {
                if (tabNode[2][k] > 2) {
                    float d = PApplet.dist(tabNode[0][k], tabNode[1][k], i, j);

                    if (d < DmaxOnScreen) {
                        float tmp1 = PApplet.sq(1 - PApplet.sq(d / DmaxOnScreen));
                        sum1 = sum1 + tmp1 * tabNode[2][k];
                        sum2 = sum2 + tmp1;
                    }
                }
            } else {
                float d = PApplet.dist(tabNode[0][k], tabNode[1][k], i, j);
                if (d < DmaxOnScreen) {
                    float tmp1 = PApplet.sq(1 - PApplet.sq(d / DmaxOnScreen));
                    sum1 = sum1 + tmp1 * tabNode[2][k];
                    sum2 = sum2 + tmp1;
                }
            }
        }
        p.noStroke();
        float poids = sum1 / sum2;
        return poids;
    }

    public static float Shepard(float i, float j) {
        PApplet p = Application.session.getPApplet();
        float sum1 = 1;
        float sum2 = 1;
        for (int k = 0; k < Application.session.getNodePourLissageCount() - 1; k++) {
            float d = PApplet.dist(Application.session.getNodePourLissage(0, k), Application.session.getNodePourLissage(1, k), i, j);
            if (d < Application.session.getDmaxOnScreen()) {
                float tmp1 = 1 / PApplet.pow(d, Application.session.getP());
                sum1 = sum1 + tmp1 * Application.session.getNodePourLissage(2, k);
                sum2 = sum2 + tmp1;
            }
        }
        p.noStroke();
        float poids = sum1 / sum2;
        return poids;

    }

    public static void premiereUtilisationBuffeur() {
        init = true;
        //nonInit = true;
    }
    
}
