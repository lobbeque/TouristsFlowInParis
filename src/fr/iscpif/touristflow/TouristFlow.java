/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.iscpif.touristflow;

import java.util.Properties;
import de.fhpotsdam.unfolding.geo.Location;
import processing.core.*;
import static java.lang.System.*;

/**
 *
 * @author Quentin Lobbé
 */
public class TouristFlow extends PApplet {

    /**
     * @param args the command line arguments
     */
    float zoom;

    public static void main(String[] args) {
        // must match the name of your class ie "letsp5.Main" = packageName.className 
        PApplet.main(new String[]{"fr.iscpif.touristflow.TouristFlow"});
    }

    @Override
    public void setup() {
        Application.session.setPApplet(this);

        //size(1400, 979);
        size(1200, 800);

        //Si l'on se sert d'un proxy, décommenter et ajouter le nom du proxy  
        /*Properties props = System.getProperties();
        props.put("http.proxyHost", "proxyhostname");
        props.put("http.proxyPort", "proxyhostport");*/



        // info à rentrer pour créer la carte unfloding
        Application.session.setMap(new de.fhpotsdam.unfolding.Map(this));
        Application.session.map.zoomAndPanTo(new Location(48.866f, 2.359f), 10);// position de départ de la carte 
        de.fhpotsdam.unfolding.utils.MapUtils.createDefaultEventDispatcher(this, Application.session.getMap());

        //initialiser la date courante de la carte
        Temps.setupDates();
        //charger les gexf
        Bibliotheque.loadGraph();

        // les info sur les noeuds et edges sont stockés dans 2 matrices
        Application.session.setMatEdge(new float[5][(int) Application.session.getMaxEdgeTotal()]);
        Application.session.setMatNode();
        // on réserve de la place pour le mode sélection et oursins
        Application.session.setSortant(new float[6][(int) Application.session.getMaxEdgeTotal() / 20]);
        Application.session.setEntrant(new float[6][(int) Application.session.getMaxEdgeTotal() / 20]);
        Application.session.setEdgePoids(new float[(int) Application.session.getMaxEdgeTotal()]);
        Application.session.setNodePoids(new float[(int) Application.session.getMaxNodeTotal()]);
        // on initialise les 2 matrices principales sur lesquelles nou sallons maintenant travailler 
        Bibliotheque.remplirTableauImage(Application.session.getIndex());
        // on initialise les effectifs pour les graphiques de distributions
        Bibliotheque.effectif("edge");
        Bibliotheque.effectif("node");

        // chargement du points pour le heatmap
        Application.session.setMyPoints(loadImage("./Ressources/ppp.png"));

        smooth();

        // centre du bouton principal du menu
        float xMap = (float) (width / 17.5);
        float yMap = (float) (height / 18);

        // définition des boutons du menu et des dépendences entre eux 
        String[] arr = new String[]{"Noeud", "Arc", "Lissée"};
        Application.session.getBoutons().add(new BoutonMenu(50, xMap, yMap, "Carte", true, arr));
        String[] arr1 = new String[]{"Box Cox Noeud", "Select", "HeatMap"};
        String[] arr4 = new String[]{"Oursins"};
        Application.session.getBoutons().add(new BoutonMenu(30, xMap + 70, yMap, "Noeud", arr1));
        Application.session.getBoutons().add(new BoutonMenu(20, xMap + 130, yMap - 20, "Box Cox Noeud"));
        Application.session.getBoutons().add(new BoutonMenu(20, xMap + 165, yMap, "Select", arr4));
        Application.session.getBoutons().add(new BoutonMenu(20, xMap + 130, yMap + 20, "HeatMap"));
        String[] arr2 = new String[]{"Exp(1/x)", "Box Cox", "Log"};
        Application.session.getBoutons().add(new BoutonMenu(30, xMap, yMap + 70, "Arc", arr2));
        Application.session.getBoutons().add(new BoutonMenu(20, xMap + 50, yMap + 135, "Box Cox"));
        Application.session.getBoutons().add(new BoutonMenu(20, xMap, yMap + 135, "Exp(1/x)"));
        Application.session.getBoutons().add(new BoutonMenu(20, xMap - 50, yMap + 135, "Log"));
        String[] arr3 = new String[]{"Biweight", "Shepard"};
        Application.session.getBoutons().add(new BoutonMenu(30, xMap + 70, yMap + 70, "Lissée", arr3));
        Application.session.getBoutons().add(new BoutonMenu(20, xMap + 130, yMap + 135, "Biweight"));
        Application.session.getBoutons().add(new BoutonMenu(20, xMap + 130, yMap + 70, "Shepard"));
        Application.session.getBoutons().add(new BoutonMenu(20, xMap + 230, yMap, "Oursins"));

        Bibliotheque.miseAJourMatriceDistance(Application.session.getIndex());

        // tableau qui contiendra les noeuds pour le lissage
        Application.session.setNodePourLissage(new float[3][Application.session.getTableauGephi()[Application.session.getIndex()].nodeCount]);
        // initialisation de la constante Dmax, utile au lissage 
        Application.session.setDmaxOnScreen(Bibliotheque.meter2Pixel(Application.session.getDmax()));

        Bibliotheque.distMinMax();

        zoom = Application.session.getMap().getZoom();

        // création des deux curseurs de sélection pour le lissage
        Application.session.setCurseur(new Stick(10, (float) (width / 56 + 165), (float) (height - 150 + 53), Application.session.getDmax(), (float) (320 - 10 - 165), 0, 1500, (float) 1 / 3));
        Application.session.setCurseur2(new Stick(10, (float) (width / 56 + 165), (float) (height - 150 + 18), Application.session.getP(), (float) (320 - 10 - 165), 0, (float) 1.2, (float) 1 / 3));
        // création des deux curseurs pour le box cox
        Application.session.setCurseur3(new Stick(10, (float) (width / 56 + 175 + 30), (float) (height - 245), Application.session.getLambdaE(), 115, (float) -1.5, (float) 1.5, (float) 5 / 6));
        Application.session.setCurseur4(new Stick(10, (float) (width / 56 + 175 + 30), (float) (height - 295), Application.session.getLambdaE(), 115, (float) -1.5, (float) 1.5, (float) 5 / 6));
        // création du curseur pour le kmeans
        Application.session.setCurseur5(new Stick(10, (float) (width - 250 + 10), (float) (height / 18 + 28), KMeans.getN(), 60, (float) 1, (float) 5, (float) 3 / 5));

    }

    @Override
    public void draw() {

        background(0);
        Application.session.getMap().draw();

        fill(190, 201, 186, 100);
        rect(0, 0, width, height);
        noFill();

        // Mises à jour s'il y a changement d'interval
        if (Application.session.getIndexBis() != Application.session.getIndex()) {

            Bibliotheque.remplirTableauImage(Application.session.getIndex());
            Bibliotheque.miseAJourMatriceDistance(Application.session.getIndex());

            Bibliotheque.miseAJourOursins();

            if (Application.session.isHeat()) {
                Smooth.initBuff1();
            }
        }

        // on surveille le niveau de zoom
        if (zoom != Application.session.getMap().getZoom()) {
            zoom = Application.session.getMap().getZoom();
            // s'il change on met à jour la carte lissée
            if (Application.session.isHeat()) {
                Smooth.initBuff1();
            }
        }

        PFont font1 = createFont("DejaVuSans-ExtraLight-", 12);
        textFont(font1);

        Application.session.setClosestDist(MAX_FLOAT);

        if ((!Application.session.isHeat()) && (Application.session.isEdge())) { // affichage des edges 
            Edge.afficheEdge();
        }

        if ((Application.session.isNode()) || (!Application.session.isHeat())) { // affichage des nodes
            Node.afficheNode();
        }

        stroke(153);
        fill(255, 255, 255, 100);

        // affichage de la "time line"
        Temps.drawDateSelector();
        stroke(255);
        fill(255);
        strokeWeight(2);
        Affichage.afficheEchelle();

        noStroke();

        // affiche la légende en mode edge ou node 
        if ((!Application.session.isHeat()) && (!Application.session.isChaud())) {
            Affichage.afficheLegendeNodeEdge();
        }

        // affiche la légende en mode lissée
        if ((Application.session.isHeat()) && (!Application.session.isChaud())) {
            Affichage.afficheLegendeLissee();
        }

        // affiche la légende en mode heatMap
        if (Application.session.isChaud()) {
            Affichage.afficheLegendeHeatMap();
        }

        // affiche la légende en mode Cluster
        if (Application.session.isOursin()) {
            Affichage.afficheCluster();
        }

        ellipseMode(CENTER);

        // dessiner les boutons du menu
        for (int i = 0; i < Application.session.getBoutons().size(); i++) {
            BoutonMenu boutonMenu = (BoutonMenu) Application.session.getBoutons().get(i);
            boutonMenu.draw();
        }

        // dessiner les oursins crées  
        if ((!Application.session.isSelect()) && (Application.session.isOursin())) {

            for (int z = 0; z < Application.session.getOursins().size(); z++) {
                Oursin oursin = (Oursin) Application.session.getOursins().get(z);
                oursin.draw();
            }
        }

        // dessiner les clusters
        if (Application.session.isKmeansDraw()) {
            KMeans.drawCluster();
        }
        fill(255);
    }
    // compteur pour les captures
    int compteurImage = 0;

    @Override
    public void keyReleased() {
        // capture écran
        if (key == 's' || key == 'S') {
            save("roaming_2009_03_29-custom-12-16-num_" + compteurImage + ".png");
            compteurImage++;
        }

        if (key == 'o') {
            if (Node.getHide()) {
                Node.setHide(false);
            } else {
                Node.setHide(true);
            }
        }

    }

    @Override
    public void mousePressed() {

        for (int i = 0; i < Application.session.getBoutons().size(); i++) {
            BoutonMenu boutonMenu = (BoutonMenu) Application.session.getBoutons().get(i);
            boutonMenu.pressed(mouseX, mouseY); // appel de la routine pour chaque noeud du menu
        }

        // création d'un oursin quand on clique dessus
        if ((!Application.session.isSelect()) && (Application.session.isOursin())) {
            for (int j = 0; j < Application.session.getTableauGephi()[Application.session.getIndex()].nodeCount; j++) {
                Location l1 = new Location(Application.session.getMatNode(0, j), Application.session.getMatNode(1, j));
                float xy1[] = Application.session.getMap().getScreenPositionFromLocation(l1);
                if (dist(mouseX, mouseY, xy1[0], xy1[1]) < (width / 140)) {
                    Affichage.selectionOursins(xy1[0], xy1[1], Application.session.getMatNode(0, j), Application.session.getMatNode(1, j));
                }
            }
        }

        Application.session.setClicked(true);

        // Zones de changement d'interval
        float dist1 = dist((float) (width / 2.39), (float) (height / 27.97), mouseX, mouseY);
        float dist2 = dist((float) (width / 1.728), (float) (height / 27.97), mouseX, mouseY);
        if (dist1 < 15) {
            int newHour = Temps.getHourIndex() - 1;
            Temps.setHour(newHour);
        }
        if (dist2 < 15) {
            int newHour = Temps.getHourIndex() + 1;
            Temps.setHour(newHour);
        }

        // Zones d'affichage des distributions ( petits carrés bleus et rouges )
        float dist5 = dist((float) (width / 70 + width / 8 / 2 / 11.6), (float) (height - 320 + width / 8 / 2 / 11.6), mouseX, mouseY);
        float dist6 = dist((float) (width / 70 + width / 8 / 2 / 11.6), (float) (height - 320 - width / 8 / 2 / 11.6), mouseX, mouseY);
        if (!Application.session.isNodeDistri()) {
            if (dist5 < 20) {
                Application.session.setNodeDistri(true);
            }
        } else {
            if ((dist5 < 20) || (dist6 < 20)) {
                Application.session.setNodeDistri(false);
            }
        }
        if (!Application.session.isEdgeDistri()) {
            if ((width / 70 + 175 - 15 <= mouseX) && (width / 70 + 175 >= mouseX) && (height - 235 <= mouseY) && (height - 220 >= mouseY)) {
                Application.session.setEdgeDistri(true);
            }
        } else {
            if ((width / 70 + 175 - 15 <= mouseX) && (width / 70 + 175 >= mouseX) && (height - 220 <= mouseY) && (height - 205 >= mouseY)) {
                Application.session.setEdgeDistri(false);
            }
        }

        if (!Application.session.isEdgeBoxCoxDistri()) {
            if ((width / 70 + 2 * 175 - 15 <= mouseX) && (width / 70 + 2 * 175 >= mouseX) && (height - 235 <= mouseY) && (height - 220 >= mouseY)) {
                Application.session.setEdgeBoxCoxDistri(true);
            }
        } else {
            if ((width / 70 + 2 * 175 - 15 <= mouseX) && (width / 70 + 2 * 175 >= mouseX) && (height - 220 <= mouseY) && (height - 205 >= mouseY)) {
                Application.session.setEdgeBoxCoxDistri(false);
            }
        }

        if (!Application.session.isNodeBoxCoxDistri()) {
            if ((width / 70 + 2 * 175 - 15 <= mouseX) && (width / 70 + 2 * 175 >= mouseX) && (height - 320 <= mouseY) && (height - 305 >= mouseY)) {
                Application.session.setNodeBoxCoxDistri(true);
            }
        } else {
            if ((width / 70 + 2 * 175 - 15 <= mouseX) && (width / 70 + 2 * 175 >= mouseX) && (height - 335 <= mouseY) && (height - 320 >= mouseY)) {
                Application.session.setNodeBoxCoxDistri(false);
            }
        }

        // zones d'activation des boutons du menu Oursins 
        if (Application.session.isOursin()) {
            if ((width - 250 + 20 <= mouseX) && (width - 250 + 220 / 2 >= mouseX) && (height / 18 - 15 <= mouseY) && (height / 18 >= mouseY)) {
                Bibliotheque.showOursins();
            } else if ((width - 250 + 220 / 2 <= mouseX) && (width - 250 + 220 - 20 >= mouseX) && (height / 18 - 15 <= mouseY) && (height / 18 >= mouseY)) {
                Bibliotheque.hideOursins();
            } else if ((width - 250 + 20 <= mouseX) && (width - 250 + 220 / 2 >= mouseX) && (height / 18 - 30 <= mouseY) && (height / 18 - 15 >= mouseY)) {
                Bibliotheque.creerOursinsVue();
            } else if ((width - 250 + 220 / 2 <= mouseX) && (width - 250 + 220 - 20 >= mouseX) && (height / 18 - 30 <= mouseY) && (height / 18 - 15 >= mouseY)) {
                Bibliotheque.effacerOursins();
            }
        }

        // zones d'activation des boutons du menu cluster
        float dist7 = dist((float) (width - 250 + 107), (float) (height / 18 + 32), mouseX, mouseY);
        float dist8 = dist((float) (width - 250 + 150), (float) (height / 18 + 30), mouseX, mouseY);
        float dist9 = dist((float) (width - 250 + 195), (float) (height / 18 + 30), mouseX, mouseY);
        if (Application.session.isOursin()) {
            if (dist7 < 15) {
                if (Application.session.getOursins().size() != 0) {
                    KMeans.kMeansInit();
                    out.println("Cluster init ok");
                }
            } else if (dist8 < 15) {
                if (Application.session.getOursins().size() != 0) {
                    if (Application.session.isKmeansDraw()) {
                        Application.session.setKmeansDraw(false);
                    } else {
                        Application.session.setKmeansDraw(true);
                        out.println("Cluster représentation ok");
                    }
                }
            } else if (dist9 < 15) {
                Application.session.setKmeansDraw(false);
                KMeans.KMeansClean();
                out.println("Cluster Clean");
            }
        }

        // Affichage indépendant des calques des clusters
        if (Application.session.isKmeansDraw()) {
            KMeans.pressed(mouseX, mouseY);
        }
    }

    @Override
    public void mouseReleased() {
        Application.session.setClicked(false);
        Application.session.setDraged(true);
    }

    @Override
    public void mouseDragged() {
        Application.session.getCurseur().dragged(mouseX, mouseY);
        Application.session.getCurseur2().dragged(mouseX, mouseY);
        Application.session.getCurseur3().dragged(mouseX, mouseY);
        Application.session.getCurseur4().dragged(mouseX, mouseY);
        if (!Application.session.isKmeansDraw()) {
            Application.session.getCurseur5().dragged(mouseX, mouseY);
        }
        Application.session.setDraged(false);
    }
}