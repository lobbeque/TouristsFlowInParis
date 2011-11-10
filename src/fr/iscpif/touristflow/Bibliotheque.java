/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.iscpif.touristflow;

import de.fhpotsdam.unfolding.geo.Location;
import processing.core.*;
import java.util.Arrays;

/**

 * 
 * 
 * 
 * @author Quentin Lobbé
 */
public class Bibliotheque {

    static void loadGraph() {
        Application.session.setTableauGephi(0, new Gephi());
        Application.session.getTableauGephi()[0].loadGraph("/home/guest/Bureau/quentin/roaming_2009_03_31-prepa-0-4.gexf");
        Application.session.setTableauGephi(1, new Gephi());
        Application.session.getTableauGephi()[1].loadGraph("/home/guest/Bureau/quentin/roaming_2009_03_31-prepa-4-8.gexf");
        Application.session.setTableauGephi(2, new Gephi());
        Application.session.getTableauGephi()[2].loadGraph("/home/guest/Bureau/quentin/roaming_2009_03_31-prepa-8-12.gexf");
        Application.session.setTableauGephi(3, new Gephi());
        Application.session.getTableauGephi()[3].loadGraph("/home/guest/Bureau/quentin/roaming_2009_03_31-prepa-12-16.gexf");
        Application.session.setTableauGephi(4, new Gephi());
        Application.session.getTableauGephi()[4].loadGraph("/home/guest/Bureau/quentin/roaming_2009_03_31-prepa-16-20.gexf");
        Application.session.setTableauGephi(5, new Gephi());
        Application.session.getTableauGephi()[5].loadGraph("/home/guest/Bureau/quentin/roaming_2009_03_31-prepa-20-24.gexf");
        Application.session.setIndex(0);
    }

    static void maxEdgeTot(int i) {
        if (i > Application.session.getMaxEdgeTotal()) {
            Application.session.setMaxEdgeTotal(i);
        }
    }

    static void maxNodeTot(int i) {
        if (i > Application.session.getMaxNodeTotal()) {
            Application.session.setMaxNodeTotal(i);
        }
    }

    // calcule la distance entre deux points dont on ne connait que la lat/long
    static float distFrom(float lat1, float lng1, float lat2, float lng2) {
        float earthRadius = (float) 3958.75;
        float dLat = PApplet.radians(lat2 - lat1);
        float dLng = PApplet.radians(lng2 - lng1);
        float a = PApplet.sin(dLat / 2) * PApplet.sin(dLat / 2)
                + PApplet.cos(PApplet.radians(lat1)) * PApplet.cos(PApplet.radians(lat2)) * PApplet.sin(dLng / 2) * PApplet.sin(dLng / 2);
        float c = 2 * PApplet.atan2(PApplet.sqrt(a), PApplet.sqrt(1 - a));
        float dist = earthRadius * c;
        int meterConversion = 1609;
        float d = (float) dist * (float) meterConversion;
        return d;
    }

    // calcule une valeur étalon permettant de passer de la distance en pixel à la distance en mêtre
    static float meter2Pixel(float dReel) {
        Location l1 = new Location((float) 48.895, (float) 2.283);
        Location l2 = new Location((float) 48.878, (float) 2.436);
        float xy1[] = Application.session.getMap().getScreenPositionFromLocation(l1);
        float xy2[] = Application.session.getMap().getScreenPositionFromLocation(l2);
        float distReel = distFrom(l1.getLat(), l1.getLon(), l2.getLat(), l2.getLon());
        float distScreen = PApplet.dist(xy1[0], xy1[1], xy2[0], xy2[1]);
        float dScreen = dReel * distScreen / distReel;
        return dScreen;
    }

    // détermine l'edge de plus grande et plus courte taille
    static void distMinMax() {
        for (int k = 0; k < Temps.getHourCount(); k++) {
            for (int i = 0; i < Application.session.getTableauGephi()[k].edgeCount; i++) {
                Location l1 = new Location(Application.session.getMatEdge(0, i), Application.session.getMatEdge(1, i));
                Location l2 = new Location(Application.session.getMatEdge(2, i), Application.session.getMatEdge(3, i));
                float distance = distFrom(l1.getLat(), l1.getLon(), l2.getLat(), l2.getLon());
                if (distance > Application.session.getDistMax()) {
                    Application.session.setDistMax(distance);
                }
                if (distance < Application.session.getDistMin()) {
                    Application.session.setDistMin(distance);
                }
            }
        }
    }

    // à chaque changement d'interval, les données sont stockées de l'objet Gephi vers ces 2 matrices, les actions de filtrages et traitements se feront sur ces matrices
    static void remplirTableauImage(int index) {
        PApplet p = Application.session.getPApplet();
        int cpt = 0;
        
        for (int i = 0; i < Application.session.getNodePoids().length; i ++) {
            Application.session.setNodePoids(i, 0);
        }

        for (int k = 0; k < Application.session.getEdgePoids().length; k++) {
            Application.session.setEdgePoids(k, 0);
        }

        for (int i = 0; i < Application.session.getTableauGephi()[Application.session.getIndex()].edgeCount; i++) {
            Application.session.setMatEdge(0, i, (float) Application.session.getTableauGephi()[Application.session.getIndex()].edge[i][0]);
            Application.session.setMatEdge(1, i, (float) Application.session.getTableauGephi()[Application.session.getIndex()].edge[i][1]);
            Application.session.setMatEdge(2, i, (float) Application.session.getTableauGephi()[Application.session.getIndex()].edge[i][2]);
            Application.session.setMatEdge(3, i, (float) Application.session.getTableauGephi()[Application.session.getIndex()].edge[i][3]);
            Application.session.setMatEdge(4, i, (float) Application.session.getTableauGephi()[Application.session.getIndex()].edge[i][4]);
            cpt++;
            Application.session.setEdgePoids(i, (float) Application.session.getTableauGephi()[Application.session.getIndex()].edge[i][4]);
        }




        for (int i = 0; i < Application.session.getTableauGephiCount(Application.session.getIndex(), 0); i++) {
            Application.session.setMatNode(0, i, (float) Application.session.getTableauGephiNode(Application.session.getIndex(), 1, i));
            Application.session.setMatNode(1, i, (float) Application.session.getTableauGephiNode(Application.session.getIndex(), 0, i));
            Application.session.setMatNode(2, i, (float) Application.session.getTableauGephiNode(Application.session.getIndex(), 2, i));
            Application.session.setNodePoids(i, (float) Application.session.getTableauGephiNode(Application.session.getIndex(), 2, i));
        }
        //TriRapide.trirapide(Application.session.getMatNode(), (int) Application.session.getTableauGephi()[Application.session.getIndex()].nodeCount, 3);

        Application.session.setNodePoids(PApplet.sort(Application.session.getNodePoids()));
        Application.session.setEdgePoids(PApplet.sort(Application.session.getEdgePoids()));

        Application.session.setIndexBis(index);
    }

    static void effacerOursins() {
        while (!Application.session.getOursins().isEmpty()) {
            Application.session.getOursins().remove(0);
        }
    }

    static void miseAJourOursins() {
        float[][] temp = new float[2][50];
        int i = 0;
        while (!Application.session.getOursins().isEmpty()) {
            Oursin oursin = (Oursin) Application.session.Oursins.get(0);
            temp[0][i] = oursin.getXN();
            temp[1][i] = oursin.getYN();
            Application.session.getOursins().remove(0);
            i++;
        }
        for (int j = 0; j < i + 1; j++) {
            Location l = new Location(temp[0][j], temp[1][j]);
            float xy[] = Application.session.getMap().getScreenPositionFromLocation(l);

            Affichage.selectionOursins(xy[0], xy[1], temp[0][j], temp[1][j]);
        }

    }

    static void miseAJourMatriceDistance(int index) {
        Application.session.setTabEdgeDist(new float[(int) Application.session.getMaxEdgeTotal()]);
        for (int j = 0; j < Application.session.getTableauGephi()[Application.session.getIndex()].edgeCount; j++) {
            Application.session.setTabEdgeDist(j, distFrom(Application.session.getMatEdge(0, j), Application.session.getMatEdge(1, j), Application.session.getMatEdge(2, j), Application.session.getMatEdge(3, j)));
        }

        TriRapide.trirapide2(Application.session.getTabEdgeDist(), (int) Application.session.getTableauGephi()[Application.session.getIndex()].edgeCount);

    }

    static void maxNbRepartitionEdge(int comp) {
        if (comp > Application.session.getCompMaxEdge()) {
            Application.session.setCompMaxEdge(comp);
        }
    }

    static float[] remplissagePointsCardinaux(float[] pointsCardinaux, int cpt, int sens) {

        // on va créer 16 tableaux qui correspondront à une division de l'espace autour d'un noeud suivant les points cardinaux

        float[] sud = new float[cpt];

        float[] sudsudEst = new float[cpt];

        float[] sudEst = new float[cpt];

        float[] sudEstEst = new float[cpt];

        float[] Est = new float[cpt];

        float[] nordEstEst = new float[cpt];

        float[] nordEst = new float[cpt];

        float[] nordnordEst = new float[cpt];

        float[] nord = new float[cpt];

        float[] nordnordOuest = new float[cpt];

        float[] nordOuest = new float[cpt];

        float[] nordOuestOuest = new float[cpt];

        float[] Ouest = new float[cpt];

        float[] sudOuestOuest = new float[cpt];

        float[] sudOuest = new float[cpt];

        float[] sudsudOuest = new float[cpt];

        // les arcs sortants sont rangés en fonction de leur orientation dans le tableau qui va bien

        float angle = 0;

        for (int j = 0; j < cpt; j++) {

            if (sens == 1) {
                angle = Application.session.getEntrant(5, j);
            } else if (sens == 0) {
                angle = Application.session.getSortant(5, j);
            }

            if ((-PConstants.PI / 16 <= angle) && (angle < PConstants.PI / 16)) {
                sud = PApplet.splice(sud, j, 0);

            } else if ((PConstants.PI / 16 <= angle) && (angle < 3 * PConstants.PI / 16)) {
                sudsudEst = PApplet.splice(sudsudEst, j, 0);

            } else if ((3 * PConstants.PI / 16 <= angle) && (angle < 5 * PConstants.PI / 16)) {
                sudEst = PApplet.splice(sudEst, j, 0);

            } else if ((5 * PConstants.PI / 16 <= angle) && (angle < 7 * PConstants.PI / 16)) {
                sudEstEst = PApplet.splice(sudEstEst, j, 0);

            } else if ((7 * PConstants.PI / 16 <= angle) && (angle < 9 * PConstants.PI / 16)) {
                Est = PApplet.splice(Est, j, 0);

            } else if ((9 * PConstants.PI / 16 <= angle) && (angle < 11 * PConstants.PI / 16)) {
                nordEstEst = PApplet.splice(nordEstEst, j, 0);

            } else if ((11 * PConstants.PI / 16 <= angle) && (angle < 13 * PConstants.PI / 16)) {
                nordEst = PApplet.splice(nordEst, j, 0);

            } else if ((13 * PConstants.PI / 16 <= angle) && (angle < 15 * PConstants.PI / 16)) {
                nordnordEst = PApplet.splice(nordnordEst, j, 0);

            } else if (((15 * PConstants.PI / 16 <= angle) && (angle <= PConstants.PI)) || ((- 15 * PConstants.PI / 16 > angle) && (angle >= -PConstants.PI))) {
                nord = PApplet.splice(nord, j, 0);

            } else if ((- 13 * PConstants.PI / 16 > angle) && (angle >= - 15 * PConstants.PI / 16)) {
                nordnordOuest = PApplet.splice(nordnordOuest, j, 0);

            } else if ((- 11 * PConstants.PI / 16 > angle) && (angle >= - 13 * PConstants.PI / 16)) {
                nordOuest = PApplet.splice(nordOuest, j, 0);

            } else if ((- 9 * PConstants.PI / 16 > angle) && (angle >= - 11 * PConstants.PI / 16)) {
                nordOuestOuest = PApplet.splice(nordOuestOuest, j, 0);

            } else if ((- 7 * PConstants.PI / 16 > angle) && (angle >= - 9 * PConstants.PI / 16)) {
                Ouest = PApplet.splice(Ouest, j, 0);

            } else if ((- 5 * PConstants.PI / 16 > angle) && (angle >= - 7 * PConstants.PI / 16)) {
                sudOuestOuest = PApplet.splice(sudOuestOuest, j, 0);

            } else if ((- 3 * PConstants.PI / 16 > angle) && (angle >= - 5 * PConstants.PI / 16)) {
                sudOuest = PApplet.splice(sudOuest, j, 0);

            } else if ((-PConstants.PI / 16 > angle) && (angle >= - 3 * PConstants.PI / 16)) {
                sudsudOuest = PApplet.splice(sudsudOuest, j, 0);
            }
        }

        pointsCardinaux = traitementPointsCardinaux(Est, pointsCardinaux, 0, sens);
        pointsCardinaux = traitementPointsCardinaux(sudEstEst, pointsCardinaux, 2, sens);
        pointsCardinaux = traitementPointsCardinaux(sudEst, pointsCardinaux, 4, sens);
        pointsCardinaux = traitementPointsCardinaux(sudsudEst, pointsCardinaux, 6, sens);
        pointsCardinaux = traitementPointsCardinaux(sud, pointsCardinaux, 8, sens);
        pointsCardinaux = traitementPointsCardinaux(sudsudOuest, pointsCardinaux, 10, sens);
        pointsCardinaux = traitementPointsCardinaux(sudOuest, pointsCardinaux, 12, sens);
        pointsCardinaux = traitementPointsCardinaux(sudOuestOuest, pointsCardinaux, 14, sens);
        pointsCardinaux = traitementPointsCardinaux(Ouest, pointsCardinaux, 16, sens);
        pointsCardinaux = traitementPointsCardinaux(nordOuestOuest, pointsCardinaux, 18, sens);
        pointsCardinaux = traitementPointsCardinaux(nordOuest, pointsCardinaux, 20, sens);
        pointsCardinaux = traitementPointsCardinaux(nordnordOuest, pointsCardinaux, 22, sens);
        pointsCardinaux = traitementPointsCardinaux(nord, pointsCardinaux, 24, sens);
        pointsCardinaux = traitementPointsCardinaux(nordnordEst, pointsCardinaux, 26, sens);
        pointsCardinaux = traitementPointsCardinaux(nordEst, pointsCardinaux, 28, sens);
        pointsCardinaux = traitementPointsCardinaux(nordEstEst, pointsCardinaux, 30, sens);

        return pointsCardinaux;
    }

    // Traitement des points cardinaux des Oursins
    static float[] traitementPointsCardinaux(float[] pointCardinal, float[] pointsCardinaux, int rang, int sens) {
        float sum = 0;
        float moy = 0;

        // si la zone n'a pas d'arc normalement le tableau correspondant vaut -1
        if (pointCardinal.length >= 1) {
            for (int i = 0; i < pointCardinal.length; i++) {
                if (pointCardinal[i] > 0) {
                    if (sens == 0) {
                        sum = sum + Application.session.getSortant(4, i);
                        Location location1 = Application.session.getMap().getLocationFromScreenPosition(Application.session.getSortant(0, i), Application.session.getSortant(1, i));
                        Location location2 = Application.session.getMap().getLocationFromScreenPosition(Application.session.getSortant(2, i), Application.session.getSortant(3, i));
                        moy = moy + distFrom(location1.getLat(), location1.getLon(), location2.getLat(), location2.getLon());
                    } else if (sens == 1) {
                        sum = sum + Application.session.getEntrant(4, i);
                        Location location3 = Application.session.getMap().getLocationFromScreenPosition(Application.session.getEntrant(0, i), Application.session.getEntrant(1, i));
                        Location location4 = Application.session.getMap().getLocationFromScreenPosition(Application.session.getEntrant(2, i), Application.session.getEntrant(3, i));
                        moy = moy + distFrom(location3.getLat(), location3.getLon(), location4.getLat(), location4.getLon());
                    }
                }
            }
            // sinon on calcul la moyenne des distances et la somme des poids 
            moy = moy / pointCardinal.length;
            moy = PApplet.map(PApplet.log(moy), 0, PApplet.log(Application.session.getDistMax()), 0, 2000);
            sum = PApplet.map(PApplet.log(sum), 0, PApplet.log(Application.session.getEdgeMax()), 0, 10);

        }
        pointsCardinaux[rang] = sum;
        pointsCardinaux[rang + 1] = moy;

        return pointsCardinaux;
    }

    // fonction de calcule des effectifs max pour les distributions
    public static void effectif(String cas) {
        float count = 0;
        float cpt = 0;
        float max = PConstants.MIN_FLOAT;
        if ("node".equals(cas)) {
            count = Application.session.getNodePoids().length;
            cpt = 0;
            float temp = 0;
            for (int i = 1; i < count; i++) {
                if (Application.session.getNodePoids(i) > 0) {
                    if (cpt == 0) {
                        temp = Application.session.getNodePoids(i);
                        cpt = 1;
                    } else {
                        if (Application.session.getNodePoids(i) == temp) {
                            cpt++;
                        } else {
                            max = PApplet.max(cpt, max);
                            cpt = 1;
                            temp = Application.session.getNodePoids(i);
                        }
                    }
                }
            }
            Application.session.setNodeEffMax(max);
        } else if ("edge".equals(cas)) {
            count = Application.session.getEdgePoids().length;
            cpt = 0;
            float temp = 0;
            for (int i = 1; i < count; i++) {
                if (Application.session.getEdgePoids(i) > 0) {
                    if (cpt == 0) {
                        temp = Application.session.getEdgePoids(i);
                        cpt = 1;
                    } else {
                        if (Application.session.getEdgePoids(i) == temp) {
                            cpt++;
                        } else {
                            max = PApplet.max(cpt, max);
                            cpt = 1;
                            temp = Application.session.getEdgePoids(i);
                        }
                    }
                }
            }
            Application.session.setEdgeEffMax(max);
            Application.session.setNodeEffMax(max);
        }
    }
}
