/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.iscpif.touristflow;

import processing.core.*;
import de.fhpotsdam.unfolding.geo.Location;

/**
 *
 * @author guest
 */
public class Smooth {

    public static Location[] buff1;
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
        buff1 = new Location[p.width * p.height / 8];
        buff1Score = new float[p.width * p.height / 8];
        for (int k = 0; k < buff1Score.length; k++) {
            buff1Score[k] = -1;
        }

            init = false;
        
        initABCD();

    }

    public static void initABCD() {
        PApplet p = Application.session.getPApplet();
        A = Application.session.getMap().getLocationFromScreenPosition(0, 0);
        B = Application.session.getMap().getLocationFromScreenPosition(p.width, 0);
        C = Application.session.getMap().getLocationFromScreenPosition(p.width, p.height);
        D = Application.session.getMap().getLocationFromScreenPosition(0, p.height);
    }

    public static float coefZoom() { // en fonction du zoom la grille d'observation est plus ou moins fine 
        float k = 0;
        if (Application.session.getMap().getZoom() >= 8196) {
            k = 3;
        } else if (Application.session.getMap().getZoom() >= 4096) {
            k = 3;
        } else {
            k = 3;
        }
        return k;
    }

    public static void lissage() {
        PApplet p = Application.session.getPApplet();
        int cpt = 0;
        if (!init) {
            premiereUtilisationBuffeur();
            calculScore1();
        } else {
            if (deplacementCarte()) {
                //shortenBuff1();
                score2();

                initABCD();
                // calculScore1();

                /*for (int i = 0; i < buff1X.length; i++) {
                Location l = new Location(buff1X[i], buff1Y[i]);
                float lxy[] = Application.session.getMap().getScreenPositionFromLocation(l);
                buff1X[i] = lxy[0];
                buff1Y[i] = lxy[1];
                }*/

            }
            

        }
        cpt = 0;


        for (int i = 0; i < buff1.length; i++) {
            if (buff1[i] != null) {
                p.noStroke();
                float percent = 0;
                percent = PApplet.norm(buff1Score[cpt], 1, Application.session.getNodeMax());//On attribut une color à nos petits carrés en fonction du résultat de la méthode
                float xy[] = Application.session.getMap().getScreenPositionFromLocation(buff1[i]);
                int x = Integer(xy[0]);
                int y = Integer(xy[1]);


                if (percent > 0.16) {
                    int c = p.color(189, 73, 50);
                    p.fill(c, 100);
                    p.rect(x, y, 3, 3);
                } else if (percent > 0.12) {
                    int c = p.color(219, 158, 54);
                    p.fill(c, 100);
                    p.rect(x, y, 3, 3);
                } else if (percent > 0.8) {
                    int c = p.color(255, 211, 78);
                    p.fill(c, 100);
                    p.rect(x, y, 3, 3);
                } else if (percent > 0.1) {
                    int c = p.color(255, 250, 213);
                    p.fill(c, 100);
                    p.rect(x, y, 3, 3);
                } else {
                    int c = p.color(16, 91, 99);
                    p.fill(c, 100);
                    p.rect(x, y, 3, 3);
                }

                cpt++;
            }

        }

    }

    public static int Integer(float f) {
        int i1 = (int) f;
        int i2 = i1 + 1;
        float dif1 = f - i1;
        float dif2 = i2 - f;
        if (dif1 > dif2) {
            return i2;
        } else {
            return i1;
        }
    }

    public static void calculScore1() {

        PApplet p = Application.session.getPApplet();
        p.text("ok", 500, 500);
        int width = p.width;
        int height = p.height;
        int count = Application.session.getNodePourLissageCount() - 1;
        int zoom = (int) Application.session.getMap().getZoom();
        float DmaxOnScreen = Bibliotheque.meter2Pixel(Application.session.getDmax());
        int cpt = 0;

        for (float i = 0; i < width; i = i + 3) {//l'écran est découpé en petits carrés
            for (float j = 0; j < height; j = j + 3) {
                buff1[cpt] = Application.session.getMap().getLocationFromScreenPosition(i, j);

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


    public static float[] delete(int i, float[] tab) {

        if (i == 0) {
            tab = PApplet.subset(tab, i + 1);
        } else {
            float[] sub1 = PApplet.subset(tab, 0, i);
            float[] sub2 = PApplet.subset(tab, i + 1);
            tab = PApplet.concat(sub1, sub2);
        }
        return tab;
    }

    public static Location[] deleteLoc(int i, Location[] tab) {
        if (i == 0) {
            tab = (Location[]) PApplet.subset(tab, i + 1);
        } else {
            Location[] sub1 = (Location[]) PApplet.subset(tab, 0, i);
            Location[] sub2 = (Location[]) PApplet.subset(tab, i + 1);
            tab = (Location[]) PApplet.concat(sub1, sub2);
        }
        return tab;
    }

    public static boolean deplacementCarte() {
        boolean ret = false;
        Location l = Application.session.getMap().getLocationFromScreenPosition(0, 0);
        if (l.getLat() != A.getLat()) {
            ret = true;
        }
        return ret;
    }

    public static int cas() {
        Location l = Application.session.getMap().getLocationFromScreenPosition(0, 0);
        if ((l.getLat() < A.getLat()) && (l.getLon() < A.getLon())) {
            return 1;
        } else if ((l.getLat() < A.getLat()) && (l.getLon() > A.getLon())) {
            return 2;
        } else if ((l.getLat() > A.getLat()) && (l.getLon() > A.getLon())) {
            return 3;
        } else if ((l.getLat() > A.getLat()) && (l.getLon() < A.getLon())) {
            return 4;
        } else {
            return 0;
        }
    }

    public static void score2() {
        PApplet p = Application.session.getPApplet();

        int count = Application.session.getNodePourLissageCount() - 1;
        int zoom = (int) Application.session.getMap().getZoom();
        float DmaxOnScreen = Bibliotheque.meter2Pixel(Application.session.getDmax());
        int cpt = 0;

        Location[] buff2 = new Location[p.width * p.height / 8];
        float[] buff2Score = new float[p.width * p.height / 8];


        Location Aprim = Application.session.getMap().getLocationFromScreenPosition(0, 0);
        Location Bprim = Application.session.getMap().getLocationFromScreenPosition(p.width, 0);
        Location Cprim = Application.session.getMap().getLocationFromScreenPosition(p.width, p.height);
        Location Dprim = Application.session.getMap().getLocationFromScreenPosition(0, p.height);


        if (cas() == 1) {
            //rect 2
            Location ASeconde = new Location(Aprim.getLat(), A.getLon());
            float ASecondeScreen[] = Application.session.getMap().getScreenPositionFromLocation(ASeconde);
            Location DTrois = new Location(C.getLat(), Aprim.getLon());
            float DTroisScreen[] = Application.session.getMap().getScreenPositionFromLocation(DTrois);
            for (int i = 0; i < ASecondeScreen[0] - 1; i = i + 3) {
                for (int j = 0; j < DTroisScreen[1]; j = j + 3) {
                    buff2[cpt] = Application.session.getMap().getLocationFromScreenPosition(i, j);
                    if (Application.session.isBiweight()) {
                        buff2Score[cpt] = Biweight(i, j, count, zoom, DmaxOnScreen, Application.session.getNodePourLissage());//Utilisation de la méthode de Biweight
                    } else {
                        buff2Score[cpt] = Shepard(i, j);//Utilisation de la méthode de Biweight
                    }
                    cpt++;
                }
            }

            //rect 3
            for (int i = 0; i < p.width; i = i + 3) {
                for (int j = (int) DTroisScreen[1]; j < p.height; j = j + 3) {
                    buff2[cpt] = Application.session.getMap().getLocationFromScreenPosition(i, j);
                    if (Application.session.isBiweight()) {
                        buff2Score[cpt] = Biweight(i, j, count, zoom, DmaxOnScreen, Application.session.getNodePourLissage());//Utilisation de la méthode de Biweight
                    } else {
                        buff2Score[cpt] = Shepard(i, j);//Utilisation de la méthode de Biweight
                    }
                    cpt++;
                }
            }
            
            //rect 1
            Location l1 = Application.session.getMap().getLocationFromScreenPosition(0, p.height);
            Location l2 = Application.session.getMap().getLocationFromScreenPosition(p.width, 0);

            for (int i = 0; i < buff1.length; i++) {
                if (buff1[i] != null) {
                    if ((buff1[i].getLon() >= l1.getLon()) && (buff1[i].getLon() <= l2.getLon()) && (buff1[i].getLat() >= l1.getLat()) && (buff1[i].getLat() <= l2.getLat())) {
                        //res = PApplet.append(res, i);
                        buff2[cpt] = buff1[i];
                        buff2Score[cpt] = buff1Score[i];
                        cpt ++;
                    }
                }
            }
        }

        buff1 = buff2;
        buff1Score = buff2Score;

        //buff1 = (Location[]) PApplet.concat(buff2, buff1);
        //buff1Score = PApplet.concat(buff2Score, buff1Score);
    }
}
