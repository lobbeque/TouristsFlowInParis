/*

Copyright : UMR Géographie Cités - Quentin Lobbé (2012)

Authors : 
Quentin Lobbé <quentin.lobbe@gmail.com>
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

import codeanticode.glgraphics.GLConstants;
import de.fhpotsdam.unfolding.Map;
import java.util.Properties;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.providers.MBTilesMapProvider;
import de.fhpotsdam.unfolding.utils.MapUtils;
import javax.script.ScriptException;
import org.openide.util.Exceptions;
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
    boolean ok = false;
    boolean showLegend = true;

    public static void main(String[] args) {
        // must match the name of your class ie "letsp5.Main" = packageName.className 
        PApplet.main(new String[]{"fr.iscpif.touristflow.TouristFlow"});
    }

    @Override
    public void setup() {
        App.db.setPApplet(this);
        App.conf.fonts.recreateUsing(this);

        //size(1200, 979);
        size(1200, 900);
        
        MouseHandlerCustom.setWidth(width);
        MouseHandlerCustom.setHeight(height);

        //Si l'on se sert d'un proxy, décommenter et ajouter le nom du proxy  
        /*Properties props = System.getProperties();
        props.put("http.proxyHost", "proxyhostname");
        props.put("http.proxyPort", "proxyhostport");*/



        // info à rentrer pour créer la carte unfloding
        Map map = new Map(this, 0, 0, width, height, new MBTilesMapProvider("jdbc:sqlite:./ressources/idf_light.mbtiles"));
        App.db.setMap(map);
        map.setZoomRange(9, 14);
        
        App.db.map.zoomAndPanTo(new Location(48.866f, 2.359f), 10);// position de départ de la carte 
        MapUtilsCustom.createDefaultEventDispatcherCustom(this, App.db.getMap());
        
        //Application.session.getMap().
 
        //initialiser la date courante de la carte
        Temps.setupDates();
        //charger les gexf
        Misc.loadGraph();

        // les info sur les noeuds et edges sont stockés dans 2 matrices
        App.db.setMatEdge(new float[5][(int) App.db.getMaxEdgeTotal()]);
        App.db.setMatNode();
        // on réserve de la place pour le mode sélection et oursins
        App.db.setOutputs(new float[6][(int) App.db.getMaxEdgeTotal() / 20]);
        App.db.setInputs(new float[6][(int) App.db.getMaxEdgeTotal() / 20]);
        App.db.setEdgePoids(new float[(int) App.db.getMaxEdgeTotal()]);
        App.db.setNodePoids(new float[(int) App.db.getMaxNodeTotal()]);
        // on initialise les 2 matrices principales sur lesquelles nou sallons maintenant travailler 
        Misc.remplirTableauImage(App.db.getIndex());
        // on initialise les effectifs pour les graphiques de distributions
        Misc.effectif("edge");
        Misc.effectif("node");

        // chargement du points pour le heatmap
        App.db.setMyPoints(loadImage("./ressources/ppp.png"));

        smooth();

        // centre du bouton principal du menu
        float xMap = (float) (width / 17.5);
        float yMap = (float) (height / 18);

        // définition des boutons du menu et des dépendences entre eux 
        String[] arr = new String[]{"Noeud", "Arc", "Lissée"};
        App.db.getBoutons().add(new BoutonMenu(50, xMap, yMap, "Carte", true, arr));
        String[] arr1 = new String[]{"Box Cox Noeud", "Select", "HeatMap", "Oursins"};
        String[] arr4 = new String[]{"Oursins"};
        App.db.getBoutons().add(new BoutonMenu(30, xMap + 70, yMap, "Noeud", arr1));
        App.db.getBoutons().add(new BoutonMenu(20, xMap + 130, yMap - 20, "Box Cox Noeud"));
        App.db.getBoutons().add(new BoutonMenu(20, xMap + 165, yMap, "Select", arr4));
        App.db.getBoutons().add(new BoutonMenu(20, xMap + 130, yMap + 20, "HeatMap"));
        String[] arr2 = new String[]{"Exp(1/x)", "Box Cox", "Log"};
        App.db.getBoutons().add(new BoutonMenu(30, xMap, yMap + 70, "Arc", arr2));
        App.db.getBoutons().add(new BoutonMenu(20, xMap + 50, yMap + 135, "Box Cox"));
        App.db.getBoutons().add(new BoutonMenu(20, xMap, yMap + 135, "Exp(1/x)"));
        App.db.getBoutons().add(new BoutonMenu(20, xMap - 50, yMap + 135, "Log"));
        String[] arr3 = new String[]{"Biweight", "Shepard", "Arrow"};
        App.db.getBoutons().add(new BoutonMenu(30, xMap + 70, yMap + 70, "Lissée", arr3));
        App.db.getBoutons().add(new BoutonMenu(20, xMap + 130, yMap + 135, "Biweight"));
        App.db.getBoutons().add(new BoutonMenu(20, xMap + 130, yMap + 70, "Shepard"));
        App.db.getBoutons().add(new BoutonMenu(20, xMap + 260, yMap, "Oursins"));
        App.db.getBoutons().add(new BoutonMenu(20, xMap + 70, yMap + 135, "Arrow"));

        Misc.miseAJourMatriceDistance(App.db.getIndex());

        // tableau qui contiendra les noeuds pour le lissage
        App.db.setNodePourLissage(new float[3][App.db.getTableauGephi()[App.db.getIndex()].nodeCount]);
        // initialisation de la constante Dmax, utile au lissage 
        App.db.setDmaxOnScreen(Misc.meter2Pixel(App.db.getDmax()));

        Misc.distMinMax();

        zoom = App.db.getMap().getZoom();

        // création des deux curseurs de sélection pour le lissage
        App.db.setCurseur(new Stick(10, (float) (width / 56 + 165), (float) (height - 150 + 53), App.db.getDmaxSmooth(), (float) (320 - 10 - 165), 0, 1500, (float) 1 / 3));
        App.db.setCurseur2(new Stick(10, (float) (width / 56 + 165), (float) (height - 150 + 18), App.db.getP(), (float) (320 - 10 - 165), 0, (float) 1.2, (float) 1 / 3));
        // création des deux curseurs pour le box cox
        App.db.setCurseur3(new Stick(10, (float) (width / 56 + 175 + 30), (float) (height - 245), App.db.getLambdaE(), 115, (float) -1.5, (float) 1.5, (float) 5 / 6));
        App.db.setCurseur4(new Stick(10, (float) (width / 56 + 175 + 30), (float) (height - 295), App.db.getLambdaE(), 115, (float) -1.5, (float) 1.5, (float) 5 / 6));
        // création du curseur pour le kmeans
        App.db.setCurseur5(new Stick(10, (float) (width - 250 + 10), (float) (height / 18 + 28), KMeans.getN(), 60, (float) 1, (float) 5, (float) 3 / 5));
        App.db.setCurseur6(new Stick(10, (float) (width / 70 + 10), (float) (height - 500 + 145), App.db.getArrowsMax(), 100, (float) 0, (float) 25, (float) 1 / 5));
        App.db.setCurseur7(new Stick(10, (float) (width / 70 + 10), (float) (height - 500 + 187), App.db.getDmax(), 100, (float) 0, (float) 2500, (float) 1 / 5));

        App.db.setNBRoamBTSMoy(Misc.readData());

        // charger les liens vers les csv contenant les info sur les flêches d'anisotropie
        App.db.setReferencesArrows(new String[25]);
        String prefix = "./ressources/Arrow31 March 2009 ";
        
        for (int i=0;i<24;i++) {
            App.db.setReferencesArrows(i, prefix + nf(i, 2)+"h.csv");
        }

        Affichage.setTemp(0);
        Affichage.setTemp2(0);
    }

    @Override
    public void draw() {

        background(0);
        App.db.getMap().draw();

        fill(190, 201, 186, 100);
        rect(0, 0, width, height);
        noFill();

        // Mises à jour s'il y a changement d'interval
        if (App.db.getIndexBis() != App.db.getIndex()) {

            Misc.remplirTableauImage(App.db.getIndex());
            Misc.miseAJourMatriceDistance(App.db.getIndex());

            Misc.miseAJourOursins();
            if (App.db.isUrchin()) {
                KMeans.KMeansClean();
            }

            if (App.db.isArrow()) {
                if ((App.db.arrowsIN.size() > 0) || (App.db.arrowsOUT.size() > 0)) {

                    Misc.supprimerArrow();
                    Misc.creerArrow();
                }
                if (Affichage.isDrawArrow()) {

                    Smooth.initBuff1();
                }
            }

            if (App.db.isHeat()) {
                Smooth.initBuff1();
            }
        }

        // on surveille le niveau de zoom
        if (zoom != App.db.getMap().getZoom()) {
            zoom = App.db.getMap().getZoom();
            // s'il change on met à jour la carte lissée
            if (App.db.isHeat()) {
                Smooth.initBuff1();
            }
        }

        PFont font1 = createFont("DejaVuSans-ExtraLight-", 12);
        textFont(font1);

        App.db.setClosestDist(MAX_FLOAT);

        if (App.db.isEdge()) { // affichage des edges 
            Edge.afficheEdge();
        }

        if ((App.db.isNode()) || (!App.db.isHeat())) { // affichage des nodes
            if (!ok) {
                Node.afficheNode();
            }

        }

        stroke(153);
        fill(255, 255, 255, 100);

        // affichage de la "time line"
        Temps.drawDateSelector();
        stroke(255);
        fill(255);
        strokeWeight(2);
        if (showLegend) Affichage.afficheEchelle();

        noStroke();
        if (showLegend) {

        // affiche la légende en mode edge ou node 
        if ((!App.db.isHeat()) && (!App.db.isChaud()) && (!App.db.isArrow())) {
            Affichage.afficheLegendeNodeEdge();
        }

        // affiche la légende en mode lissée
        if ((App.db.isHeat()) && (!App.db.isChaud())) {
            Affichage.afficheLegendeLissee();
        }

        // affiche la légende en mode heatMap
        if (App.db.isChaud()) {
            Affichage.afficheLegendeHeatMap();
        }

        // affiche la légende en mode Cluster
        if (App.db.isUrchin()) {
            Affichage.afficheCluster();
        }
        }

        ellipseMode(CENTER);

        if (showLegend) {
        // dessiner les boutons du menu
        for (int i = 0; i < App.db.getBoutons().size(); i++) {
            BoutonMenu boutonMenu = (BoutonMenu) App.db.getBoutons().get(i);
            boutonMenu.draw();
        }
        }

        // dessiner les oursins crées  
        if ((!App.db.isSelect()) && (App.db.isUrchin())) {

            for (int z = 0; z < App.db.getOursins().size(); z++) {
                Urchin oursin = (Urchin) App.db.getOursins().get(z);
                oursin.draw();
            }
        }

        // dessiner les clusters
        if (App.db.isKmeansDraw()) {
            KMeans.drawCluster();
        }

        if (App.db.isArrow()) {
            Affichage.afficheArrow();
        }

        fill(255);
        //Location location = Application.session.getMap().getLocationFromScreenPosition(mouseX, mouseY);
        //text(location.toString(), mouseX, mouseY);

        /*if ( ok ){
        Redistribution.printGrille();
        }*/

    }
    // compteur pour les captures
    int compteurImage = 0;

    @Override
    public void keyReleased() {
        // capture écran
        if (key == 's' || key == 'S') {
            showLegend=false;
            draw();
            save(App.conf.exports.screenshotsDirPath + compteurImage + ".png");
            showLegend=true;
            compteurImage++;
        }

        if (key == '1') {
            //Redistribution.getGrilleAgreger(1000);
            //Redistribution.Agreger();
            Misc.writeArrowData();
        }
        
        // lancer l'export en shape 
        if (key == '2') {
           ShapeFileCreator shape = new ShapeFileCreator();
            try {
                shape.export();
            } catch (ScriptException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

    }

    @Override
    public void mousePressed() {

        for (int i = 0; i < App.db.getBoutons().size(); i++) {
            BoutonMenu boutonMenu = (BoutonMenu) App.db.getBoutons().get(i);
            boutonMenu.pressed(mouseX, mouseY); // appel de la routine pour chaque noeud du menu
        }

        // création d'un oursin quand on clique dessus
        if ((!App.db.isSelect()) && (App.db.isUrchin())) {
            for (int j = 0; j < App.db.getTableauGephi()[App.db.getIndex()].nodeCount; j++) {
                Location l1 = new Location(App.db.getMatNode(0, j), App.db.getMatNode(1, j));
                float xy1[] = App.db.getMap().getScreenPositionFromLocation(l1);
                if (dist(mouseX, mouseY, xy1[0], xy1[1]) < (width / 140)) {
                    Affichage.selectionOursins(xy1[0], xy1[1], App.db.getMatNode(0, j), App.db.getMatNode(1, j));
                }
            }
        }

        App.db.setClicked(true);

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
        if (!App.db.isNodeDistri()) {
            if (dist5 < 20) {
                App.db.setNodeDistri(true);
            }
        } else {
            if ((dist5 < 20) || (dist6 < 20)) {
                App.db.setNodeDistri(false);
            }
        }
        if (!App.db.isEdgeDistri()) {
            if ((width / 70 + 175 - 15 <= mouseX) && (width / 70 + 175 >= mouseX) && (height - 235 <= mouseY) && (height - 220 >= mouseY)) {
                App.db.setEdgeDistri(true);
            }
        } else {
            if ((width / 70 + 175 - 15 <= mouseX) && (width / 70 + 175 >= mouseX) && (height - 220 <= mouseY) && (height - 205 >= mouseY)) {
                App.db.setEdgeDistri(false);
            }
        }

        if (!App.db.isEdgeBoxCoxDistri()) {
            if ((width / 70 + 2 * 175 - 15 <= mouseX) && (width / 70 + 2 * 175 >= mouseX) && (height - 235 <= mouseY) && (height - 220 >= mouseY)) {
                App.db.setEdgeBoxCoxDistri(true);
            }
        } else {
            if ((width / 70 + 2 * 175 - 15 <= mouseX) && (width / 70 + 2 * 175 >= mouseX) && (height - 220 <= mouseY) && (height - 205 >= mouseY)) {
                App.db.setEdgeBoxCoxDistri(false);
            }
        }

        if (!App.db.isNodeBoxCoxDistri()) {
            if ((width / 70 + 2 * 175 - 15 <= mouseX) && (width / 70 + 2 * 175 >= mouseX) && (height - 320 <= mouseY) && (height - 305 >= mouseY)) {
                App.db.setNodeBoxCoxDistri(true);
            }
        } else {
            if ((width / 70 + 2 * 175 - 15 <= mouseX) && (width / 70 + 2 * 175 >= mouseX) && (height - 335 <= mouseY) && (height - 320 >= mouseY)) {
                App.db.setNodeBoxCoxDistri(false);
            }
        }

        if (!App.db.isLissageDistri()) {
            if ((width / 56 <= mouseX) && (width / 56 + 15 >= mouseX) && (height - 150 <= mouseY) && (height - 150 + 15 >= mouseY)) {
                App.db.setLissageDistri(true);
            }
        } else {
            if ((width / 56 <= mouseX) && (width / 56 + 15 >= mouseX) && (height - 150 - 15 <= mouseY) && (height - 150 >= mouseY)) {
                App.db.setLissageDistri(false);
            }
        }

        // zones d'activation des boutons du menu Oursins 
        if (App.db.isUrchin()) {
            if ((width - 250 + 20 <= mouseX) && (width - 250 + 220 / 2 >= mouseX) && (height / 18 - 15 <= mouseY) && (height / 18 >= mouseY)) {
                Misc.showOursins();
            } else if ((width - 250 + 220 / 2 <= mouseX) && (width - 250 + 220 - 20 >= mouseX) && (height / 18 - 15 <= mouseY) && (height / 18 >= mouseY)) {
                Misc.hideOursins();
            } else if ((width - 250 + 20 <= mouseX) && (width - 250 + 220 / 2 >= mouseX) && (height / 18 - 30 <= mouseY) && (height / 18 - 15 >= mouseY)) {
                Misc.creerOursinsVue();
            } else if ((width - 250 + 220 / 2 <= mouseX) && (width - 250 + 220 - 20 >= mouseX) && (height / 18 - 30 <= mouseY) && (height / 18 - 15 >= mouseY)) {
                Misc.effacerOursins();
            }
        }

        // pour les Arrows
        if (App.db.isArrow()) {
            if ((width / 70 <= mouseX) && (width / 70 + 120 >= mouseX) && (height - 300 + 100 / 3 + 10 <= mouseY) && (height - 300 + 200 / 3 + 5 >= mouseY)) {
                Misc.creerArrow();
            } else if ((width / 70 <= mouseX) && (width / 70 + 120 >= mouseX) && (height - 300 + 200 / 3 + 5 <= mouseY) && (height - 200 >= mouseY)) {
                Misc.supprimerArrow();
            } else if ((width / 70 <= mouseX) && (width / 70 + 120 >= mouseX) && (height - 300 + 10 <= mouseY) && (height - 300 + 100 / 3 + 10 >= mouseY)) {
                if (Affichage.isDrawArrow()) {
                    Affichage.setDrawArrow(false);
                } else {
                    if ((App.db.getMap().getZoom() <= 8192) && (App.db.arrowsIN.size() > 0)) {
                        Affichage.setDrawArrow(true);
                    } else {

                        out.println("il faut dézoomer ou appuyer sur créer avant d'afficher le champ");
                    }

                }
            }

            float dist7 = dist(width / 70 + 60, height - 500 + 300 / 9 + 300 / 18 - 5, mouseX, mouseY);
            float dist8 = dist(width / 70 + 60, height - 500 + 600 / 9 + 300 / 18 - 5, mouseX, mouseY);

            if (!App.db.isIN()) {
                if (dist8 <= 10) {
                    App.db.setIN(true);
                }
            } else {
                if (dist8 <= 10) {
                    App.db.setIN(false);
                }
            }

            if (!App.db.isOUT()) {
                if (dist7 <= 10) {
                    App.db.setOUT(true);
                }
            } else {
                if (dist7 <= 10) {
                    App.db.setOUT(false);
                }
            }


        }

        // zones d'activation des boutons du menu cluster
        float dist7 = dist((float) (width - 250 + 107), (float) (height / 18 + 32), mouseX, mouseY);
        float dist8 = dist((float) (width - 250 + 150), (float) (height / 18 + 30), mouseX, mouseY);
        float dist9 = dist((float) (width - 250 + 195), (float) (height / 18 + 30), mouseX, mouseY);
        if (App.db.isUrchin()) {
            if (dist7 < 15) {
                if (App.db.getOursins().size() != 0) {
                    KMeans.kMeansInit();
                    out.println("Cluster init ok");
                }
            } else if (dist8 < 15) {
                if (App.db.getOursins().size() != 0) {
                    if (App.db.isKmeansDraw()) {
                        App.db.setKmeansDraw(false);
                    } else {
                        App.db.setKmeansDraw(true);
                        out.println("Cluster représentation ok");
                    }
                }
            } else if (dist9 < 15) {
                App.db.setKmeansDraw(false);
                KMeans.KMeansClean();
                out.println("Cluster Clean");
            }
        }

        if (App.db.isNode()) {
            float dist = dist((float) (width / 70 + 35), (float) (height - 320 + 25), mouseX, mouseY);
            if (dist < 15) {
                if (ok) {
                    ok = false;
                } else {
                    ok = true;
                }
            }
        }

        // Affichage indépendant des calques des clusters
        if (App.db.isKmeansDraw()) {
            KMeans.pressed(mouseX, mouseY);
        }
    }

    @Override
    public void mouseReleased() {
        App.db.setClicked(false);
        App.db.setDragged(true);
    }

    @Override
    public void mouseDragged() {
        App.db.getCurseur().dragged(mouseX, mouseY);
        App.db.getCurseur2().dragged(mouseX, mouseY);
        App.db.getCurseur3().dragged(mouseX, mouseY);
        App.db.getCurseur4().dragged(mouseX, mouseY);
        if (!App.db.isKmeansDraw()) {
            App.db.getCurseur5().dragged(mouseX, mouseY);
        }
        App.db.setDragged(false);
        App.db.getCurseur6().dragged(mouseX, mouseY);
        App.db.getCurseur7().dragged(mouseX, mouseY);
    }
}