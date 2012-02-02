/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.iscpif.touristflow;

import java.util.ArrayList;
import processing.core.*;
import de.fhpotsdam.unfolding.geo.Location;
import java.util.Arrays;
import static java.lang.System.*;

/**
 *
 * @author Quentin lobbé
 */
public class Smooth {

    // Grille utilisée pour les Arrows
    public static float[][] Grille;
    public static float[][] buff1;
    public static float[] buff1Score;
    // pour le nuage de flèches
    public static ArrayList arrowsINsmooth = new ArrayList();
    public static ArrayList arrowsOUTsmooth = new ArrayList();
    public static boolean init;
    //public static boolean nonInit = false;
    public static float ca = 0;

    /*
     * Cette fonction est appelée à chaque lancement de la carte lissée ou du champ de flêches 
     * Elle initialise tous les tableaux et buffeurs nécessaires ont bon fonctionnement de la viz
     */
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
        while (arrowsINsmooth.size() > 0) {
            arrowsINsmooth.remove(0);
        }
        while (arrowsOUTsmooth.size() > 0) {
            arrowsOUTsmooth.remove(0);
        }
        init = false;
    }

    /* *********** pour afficher la carte lissée ************* */
    /*
     * Pour chaque cellule du maillage on calcule un score de lissage via "calculScore1()"
     * Les coordonnées des cellules sont rangés dans buff1[][]
     * Les scores des cellules sont rangés dans buff1Score[][]
     * On transforme alors les scores en couleurs
     * Tant que la carte n'est pas déplacée on ne refait pas les calculs
     */
    public static void lissage() {
        PApplet p = Application.session.getPApplet();
        int cpt = 0;
        if (Application.session.isDraged()) {
            init = false;
        }

        if (!init) {
            premiereUtilisationBuffeur();
            calculScore1();
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
                } else if (percent > 0.07) {
                    int c = p.color(255, 250, 213);
                    p.fill(c, 100);
                    p.rect(buff1[0][i], buff1[1][i], 3, 3);
                } else {
                    //int c = p.color(16, 91, 99);
                    int c = p.color(116, 162, 207);
                    p.fill(c, 100);
                    p.rect(buff1[0][i], buff1[1][i], 3, 3);
                }

                cpt++;
            }

        }

    }

    /*
     * Pour chaque cellule de la grille visible à l'écran 
     * on calcule le score de l'issage entrant et sortant via "Biweight" ou "Shepard"
     */
    public static void calculScore1() {

        PApplet p = Application.session.getPApplet();
        int width = p.width;
        int height = p.height;
        int count = Application.session.getNodePourLissageCount() - 1;
        int zoom = (int) Application.session.getMap().getZoom();
        float DmaxOnScreen = Bibliotheque.meter2Pixel(Application.session.getDmaxSmooth());
        int cpt = 0;
        for (float i = 0; i < width; i = i + 3) {//l'écran est découpé en petits carrés
            for (float j = 0; j < height; j = j + 3) {
                buff1[0][cpt] = i;
                buff1[1][cpt] = j;
                if (Application.session.isBiweight()) {
                    buff1Score[cpt] = Biweight(i, j, count, zoom, DmaxOnScreen, Application.session.getNodePourLissage());//Utilisation de la méthode de Biweight
                } else {
                    buff1Score[cpt] = Shepard(i, j);//Utilisation de la méthode de Biweight
                }
                cpt++;
            }
        }
    }

    /*
     * Lors du lissage nous ne regardons pas les noeuds trop éloignés ( distance trés supérieures à DMaxOnScreen )
     * On va donc travailer sur une série de noeuds proches rangés dans NodePourLissage[][]
     */
    public static void miseAJourCarteLissee() {
        PApplet p = Application.session.getPApplet();
        Application.session.setNodePourLissageCount(0);
        Application.session.setNodePourLissageHold(Application.session.getNodePourLissage());
        Application.session.setNodePourLissage(new float[3][Application.session.getNBRoamBTSMoy(0).length]);

        int nbInterval = 24 / Temps.getHourCount();

        for (int i = 0; i < Application.session.getNBRoamBTSMoy(0).length; i++) {
            if (Application.session.getNBRoamBTSMoy(2, i) > 0) {
                Location l1 = new Location(Application.session.getNBRoamBTSMoy(2, i), Application.session.getNBRoamBTSMoy(1, i));
                float xy[] = Application.session.getMap().getScreenPositionFromLocation(l1);


                if ((xy[0] < p.width + p.width / 2) && (xy[1] < p.height + p.height / 2) && (xy[0] > 0 - p.width / 2) && (xy[1] > 0 - p.height / 2)) {
                    Application.session.setNodePourLissage(0, Application.session.getNodePourLissageCount(), xy[0]);
                    Application.session.setNodePourLissage(1, Application.session.getNodePourLissageCount(), xy[1]);
                    // commenter l'instruction ci dessous et décommenter la suivante pour revenir à la carte lissée depuis le graphe et non les csv
                    Application.session.setNodePourLissage(2, Application.session.getNodePourLissageCount(), Application.session.getNBRoamBTSMoy(Application.session.getIndex() * nbInterval + 3, i));
                    //Application.session.setNodePourLissage(2, Application.session.getNodePourLissageCount(), Application.session.getMatNode(2, i));
                    Application.session.setNodePourLissageCount(Application.session.getNodePourLissageCount() + 1);
                }
            }
        }
    }

    /*
     * Pour une cellule donnée, on observe toutes les antennes visibles dans le rayon de lissage "DmaxOnScreen"
     * Grâce à la méthode de Biweight on calcule une moyenne pondérée du poids de celles ci
     * fonction de pondération : ( 1 - ( d / Dmax )² )² 
     */
    public static float Biweight(float i, float j, int count, int zoom, float DmaxOnScreen, float[][] tabNode) {
        PApplet p = Application.session.getPApplet();
        float sum1 = 1;
        float sum2 = 1;
        for (int k = 0; k < count; k++) {
            if (tabNode[2][k] > 2) {
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

    /*
     * Pour une cellule donnée, on observe toutes les antennes visibles dans le rayon de lissage "DmaxOnScreen"
     * Grâce à la méthode de Shepard on calcule une moyenne pondérée du poids de celles ci
     * fonstion de pondération : 1/(d^p)
     */
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

    /* *********** pour afficher le champ de flèches ************* */
    /*
     * Pour chaque cellule du maillage on calcule un score de lissage via "calculScore2()"
     * Les coordonnées des cellules sont rangés dans buff2[][]
     * Les scores des cellules sont rangés dans buff2ScoreIN/OUT[][]
     * On transforme alors les scores en flèches via "new Arrow ..." et "updateLight()"
     * Tant que la carte n'est pas déplacée on ne refait pas les calculs
     */
    public static void lissageArrow() {
        PApplet p = Application.session.getPApplet();


        if (Application.session.isDraged()) {
            init = false;
        }

        if (!init) {
            premiereUtilisationBuffeur();
            calculScore2();
        }

        for (int i = 0; i < arrowsINsmooth.size() ; i++) {
            Arrow a = (Arrow) arrowsINsmooth.get(i);
            Arrow b = (Arrow) arrowsOUTsmooth.get(i);
            Location l = new Location(a.getX(), a.getY());
            float xy[] = Application.session.getMap().getScreenPositionFromLocation(l);
            if ((0 < xy[0]) && (xy[0] < p.width) && (0 < xy[1]) && (xy[1] < p.height)) {
                if (Application.session.isIN()) {             
                    a.updateLight();
                }
                if (Application.session.isOUT()) {
                    b.updateLight();
                }
            }
        }
    }

    /*
     * Pour chaque cellule de la grille "Grille[i][j]" visible à l'écran 
     * on calcule le score de l'issage entrant et sortant via "CalculeLissageArrow( ..., true/false )"
     */
    public static void calculScore2() {

        PApplet p = Application.session.getPApplet();
        float DmaxOnScreen = Bibliotheque.meter2Pixel(Application.session.getDmax());
        float angle = 0;
        for (int i = 0; i < Grille[0].length; i++) {
            if (Grille[0][i] > 0) {
                Location l = new Location(Grille[0][i], Grille[1][i]);
                float xy[] = Application.session.getMap().getScreenPositionFromLocation(l);
                if ((0 < xy[0]) && (xy[0] < p.width) && (0 < xy[1]) && (xy[1] < p.height)) {

                    // vérifier qu'une flèche n'a pas été calculée
                    boolean deja = false;
                    for (int z = 0; z < arrowsINsmooth.size(); z++) {
                        Arrow a = (Arrow) arrowsINsmooth.get(z);
                        Arrow b = (Arrow) arrowsOUTsmooth.get(z);
                        if (((a.getX() == Grille[0][i]) && (a.getY() == Grille[1][i])) || ((b.getX() == Grille[0][i]) && (b.getY() == Grille[1][i]))) {
                            deja = true;
                        }
                    }


                    if (!deja) {
                        float[] temp = new float[6];
                        temp = CalculeLissageArrow(xy[0], xy[1], DmaxOnScreen);

                        angle = PApplet.atan2(temp[2], temp[1]);
                        angle = -angle;
                        if (angle < 0) {
                            angle = 2 * PConstants.PI + angle;
                        }

                        Arrow a = new Arrow(Grille[0][i], Grille[1][i], temp[0], angle, true);
                        arrowsINsmooth.add(a);

                        angle = PApplet.atan2(temp[5], temp[4]);
                        angle = -angle;
                        if (angle < 0) {
                            angle = 2 * PConstants.PI + angle;
                        }

                        Arrow b = new Arrow(Grille[0][i], Grille[1][i], temp[3], angle, false);
                        arrowsOUTsmooth.add(b);
                    }
                }

            }
        }




    }

    /*
     * Pour une cellule donnée, on observe toutes les flèches visibles dans le rayon de lissage "DmaxOnScreen"
     * Grâce à la méthode de Biweight on calcule une moyenne pondérée de l'angle et de la taille de la flêche
     */
    public static float[] CalculeLissageArrow(float i, float j, float DmaxOnScreen) {

        // in    
        // size
        float sum1 = 0;
        float sum2 = 1;
        // xi
        float sum3 = 0;
        float sum4 = 1;
        // yi
        float sum5 = 0;
        float sum6 = 1;

        // out
        // size
        float sum7 = 0;
        float sum8 = 1;
        // xi
        float sum9 = 0;
        float sum10 = 1;
        // yi
        float sum11 = 0;
        float sum12 = 1;

        float[] ret = new float[6];


        for (int k = 0; k < Application.session.arrowsIN.size(); k++) {

            Arrow a = (Arrow) Application.session.arrowsIN.get(k);
            Arrow b = (Arrow) Application.session.arrowsOUT.get(k);

            Location l = new Location(a.x, a.y);
            float xy[] = Application.session.getMap().getScreenPositionFromLocation(l);

            Location l1 = new Location(a._x, a._y);
            float xy1[] = Application.session.getMap().getScreenPositionFromLocation(l1);

            Location l2 = new Location(b._x, b._y);
            float xy2[] = Application.session.getMap().getScreenPositionFromLocation(l2);

            float d = PApplet.dist(xy[0], xy[1], i, j);
            if (d < DmaxOnScreen) {
                float tmp1 = PApplet.sq(1 - PApplet.sq(d / DmaxOnScreen));
                sum1 = sum1 + tmp1 * a.getSize();
                sum2 = sum2 + tmp1;

                sum3 = sum3 + tmp1 * (xy[0] - xy1[0]);
                sum4 = sum4 + tmp1;

                sum5 = sum5 + tmp1 * (xy[1] - xy1[1]);
                sum6 = sum6 + tmp1;

                sum7 = sum7 + tmp1 * b.getSize();
                sum8 = sum8 + tmp1;

                sum9 = sum9 + tmp1 * (xy[0] - xy2[0]);
                sum10 = sum10 + tmp1;

                sum11 = sum11 + tmp1 * (xy[1] - xy2[1]);
                sum12 = sum12 + tmp1;

            }

        }

        ret[0] = sum1 / sum2;
        ret[1] = sum3 / sum4;
        ret[2] = sum5 / sum6;
        ret[3] = sum7 / sum8;
        ret[4] = sum9 / sum10;
        ret[5] = sum11 / sum12;

        return ret;
    }

    public static void setGrille(float[][] mat) {
        Grille = mat;
    }
}
