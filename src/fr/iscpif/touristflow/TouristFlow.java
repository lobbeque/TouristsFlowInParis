/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.iscpif.touristflow;

import de.fhpotsdam.unfolding.geo.Location;
import processing.core.*;

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


        Application.session.setMap(new de.fhpotsdam.unfolding.Map(this));
        Application.session.map.zoomAndPanTo(new Location(48.866f, 2.359f), 10);// position de départ de la carte 
        de.fhpotsdam.unfolding.utils.MapUtils.createDefaultEventDispatcher(this, Application.session.getMap());

        Temps.setupDates();
        Bibliotheque.loadGraph();

        // les info sur les noeuds et edges sont stockés dans 2 matrices
        Application.session.setMatEdge(new float[5][(int) Application.session.getMaxEdgeTotal()]);
        Application.session.setMatNode();
        Application.session.setSortant(new float[6][(int) Application.session.getMaxEdgeTotal() / 20]);
        Application.session.setEntrant(new float[6][(int) Application.session.getMaxEdgeTotal() / 20]);
        Bibliotheque.remplirTableauImage(Application.session.getIndex());



        Application.session.setMyPoints(loadImage("/home/guest/Bureau/Mon_script/data/ppp.png"));

        smooth();
        
        float xMap = (float) (width / 17.5);
        float yMap = (float) (height / 18);

        // définition des boutons du menu et des dépendences entre eux 
        String[] arr = new String[]{"Noeud", "Arc", "Lissée"};
        Application.session.getBoutons().add(new BoutonMenu(50, xMap, yMap, "Carte", true, arr));
        String[] arr1 = new String[]{"Info", "Select", "HeatMap"};
        String[] arr4 = new String[]{"Oursins"};
        Application.session.getBoutons().add(new BoutonMenu(30, xMap + 70, yMap, "Noeud", arr1));
        Application.session.getBoutons().add(new BoutonMenu(20, xMap + 130, yMap - 20, "Info"));
        Application.session.getBoutons().add(new BoutonMenu(20, xMap + 165, yMap, "Select", arr4));
        Application.session.getBoutons().add(new BoutonMenu(20, xMap + 130, yMap + 20, "HeatMap"));
        String[] arr2 = new String[]{"Exp(1/x)", "Max Relatif", "Log"};
        Application.session.getBoutons().add(new BoutonMenu(30, xMap, yMap + 70, "Arc", arr2));
        Application.session.getBoutons().add(new BoutonMenu(20, xMap + 50, yMap + 135, "Max Relatif"));
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

        Application.session.setDmaxOnScreen(Bibliotheque.meter2Pixel(Application.session.getDmax()));

        Bibliotheque.distMinMax();
        
        zoom = Application.session.getMap().getZoom();

        // création des deux curseurs de sélection pour le lissage
        Application.session.setCurseur(new Stick(10, (float) (width / 56 + 165), (float) (height - 150 + 53), Application.session.getDmax(), (float) (320 - 10 - 165)));
        Application.session.setCurseur2(new Stick(10, (float) (width / 56 + 165), (float) (height - 150 + 18), Application.session.getP(), (float) (320 - 10 - 165)));
    }

    @Override
    public void draw() {

        background(0);
        Application.session.getMap().draw();

        //fill(15, 9, 105, 100);
        fill(190, 201, 186, 100);
        rect(0, 0, width, height);
        noFill();

        // Mises à jour s'il y a changement d'interval
        if (Application.session.getIndexBis() != Application.session.getIndex()) {

            Bibliotheque.remplirTableauImage(Application.session.getIndex());
            Bibliotheque.miseAJourMatriceDistance(Application.session.getIndex());
            Bibliotheque.miseAJourOursins();
            if ( Application.session.isHeat() ){
                Smooth.initBuff1();
            }
        }
        
        if ( zoom != Application.session.getMap().getZoom() ){
            zoom = Application.session.getMap().getZoom();
            if ( Application.session.isHeat() ){
                Smooth.initBuff1();
            }
        }

        PFont font1 = createFont("DejaVuSans-ExtraLight-", 15);
        textFont(font1);

        Application.session.setClosestDist(MAX_FLOAT);

        if (Application.session.isDyn()) {
            Bibliotheque.MinMax();
        }

        if ((!Application.session.isHeat()) && (Application.session.isEdge())) { // affichage des edges 
            Edge.afficheEdge();
        }

        if ((Application.session.isNode()) || (!Application.session.isHeat())) { // affichage des nodes
            Node.afficheNode();
        }

        stroke(153);
        fill(255, 255, 255, 100);

        // affichage de la time line
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
        
        if ( Application.session.isChaud() ){
            Affichage.afficheLegendeHeatMap();
        }

        Application.session.setEdgeMindyn(MAX_FLOAT);
        Application.session.setEdgeMaxdyn(MIN_FLOAT);
        Application.session.setNodeMindyn(MAX_FLOAT);
        Application.session.setNodeMaxdyn(MIN_FLOAT);

        ellipseMode(CENTER);



        for (int i = 0; i < Application.session.getBoutons().size(); i++) {
            BoutonMenu boutonMenu = (BoutonMenu) Application.session.getBoutons().get(i);
            boutonMenu.draw();
        }

        if ((!Application.session.isSelect()) && (Application.session.isOursin())) {
            if (!Application.session.getOursins().isEmpty()) {
                fill(190, 201, 186, 100);
                rect(0, 0, width, height);
                textAlign(PConstants.LEFT, PConstants.TOP);
                stroke(153);
                fill(16, 91, 136);
                text("Arc Entrant", width / 56, (float) (height / 1.958));
                fill(182, 92, 96);
                stroke(153);
                text("Arc Sortant", width / 56, (float) (height / 2.06));
            }
            for (int z = 0; z < Application.session.getOursins().size(); z++) {
                Oursin oursin = (Oursin) Application.session.getOursins().get(z);
                oursin.draw();
            }
        }
        
        text(Application.session.getMap().getZoom(),800,600);

        fill(255);
    }

    @Override
    public void mousePressed() {

        for (int i = 0; i < Application.session.getBoutons().size(); i++) {
            BoutonMenu boutonMenu = (BoutonMenu) Application.session.getBoutons().get(i);
            boutonMenu.pressed(mouseX, mouseY); // appel de la routine pour chaque noeud du menu
        }

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
        float dist7 = dist((float) (width / 70 + width / 8 / 2 / 11.6 + 175 - width / 8 / 11.6), (float) (height - 320 + width / 8 / 2 / 11.6 + 100), mouseX, mouseY);
        float dist8 = dist((float) (width / 70 + width / 8 / 2 / 11.6 + 175 - width / 8 / 11.6), (float) (height - 320 - width / 8 / 2 / 11.6 + 100), mouseX, mouseY);
        if (!Application.session.isEdgeDistri()) {
            if (dist7 < 20) {
                Application.session.setEdgeDistri(true);
            }
        } else {
            if ((dist7 < 20) || (dist8 < 20)) {
                Application.session.setEdgeDistri(false);
            }
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
        Application.session.setDraged(false);
    }
}