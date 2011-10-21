/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.iscpif.touristflow;

import de.fhpotsdam.unfolding.Map;
import de.fhpotsdam.unfolding.geo.Location;
import java.util.ArrayList;
import processing.core.*; 
/**
 *
 * @author guest
 */
public class Session {
       
    PApplet p;
    
    // pour la carte zoomée
    de.fhpotsdam.unfolding.Map map;
    de.fhpotsdam.unfolding.geo.Location locationCourante;

    // pour le menu
    java.util.ArrayList Boutons = new java.util.ArrayList();
    
    // pour les oursins
    java.util.ArrayList Oursins = new java.util.ArrayList();
    
    int nbMaxEnfants = 30; 

    // pour le heatmap
    PImage myPoints;

    // pour le gephi
    Gephi[] TableauGephi = new Gephi[6]; 

    // index du Graph courant
    int index; 
    int indexBis;

    // pour la carte lissée
    float Dmax = 500;
    float DmaxPas = 50; 
    float DmaxOnScreen; 
    float P = (float) 0.4;
    float d;
    int NodePourLissageCount;
    float[][] NodePourLissage; 
    float[][] NodePourLissageHold; 
    Stick curseur;
    Stick curseur2;


    PImage myMap;

    // matrices de stockage des noeuds et edges courants ( triées du poids fort au poids faible )
    float[][] matEdge;
    float[][] matNode;
    float[][] sortant;
    float[][] entrant; 

    // tableau de stockage des longueurs d'edges ( triée de la plus grande à la plus petite )
    float[] tabEdgeDist;

    // utile pour calculer le noeud le noeud clicable le plus proche
    float closestDist;
    float closestText;
    float closestTextX;
    float closestTextY;


    // Booléan utilisés dans la gestion et le passage d'un mode de visualisation à un autre  
    boolean clicked = false;
    boolean select = true;
    boolean info = false;
    boolean heat = false;
    boolean petit = false;
    boolean gros = true;
    boolean edge = true;
    boolean node = false;
    boolean Biweight = false;
    boolean Shepard = false;
    boolean dyn = false;
    boolean chaud = false;
    boolean nodeDistri = false;
    boolean edgeDistri = false;
    boolean Log = false;
    boolean premierLissage = false; 
    boolean oursin = false;


    // Définition de constantes pour le calcul des Min et Max 
    float nodeMin = PConstants.MAX_FLOAT;
    float nodeMax = PConstants.MIN_FLOAT;
    float edgeMin = PConstants.MAX_FLOAT;
    float edgeMax = PConstants.MIN_FLOAT;

    float edgeMindyn = PConstants.MAX_FLOAT;
    float edgeMaxdyn = PConstants.MIN_FLOAT;
    float nodeMindyn = PConstants.MAX_FLOAT;
    float nodeMaxdyn = PConstants.MIN_FLOAT;

    float distMax = PConstants.MIN_FLOAT;
    float distMin = PConstants.MAX_FLOAT;

    float compMaxEdge = PConstants.MIN_FLOAT;

    static float maxEdgeTotal = PConstants.MIN_FLOAT;
    static float maxNodeTotal = PConstants.MIN_FLOAT;

    public void setPApplet(PApplet p) {
        this.p = p;
    }
    public PApplet getPApplet() {
        return p;
    }
    public boolean isBiweight() {
        return Biweight;
    }

    public ArrayList getBoutons() {
        return Boutons;
    }
    
    public ArrayList getOursins() {
        return Oursins;
    }

    public float getDmax() {
        return Dmax;
    }

    public float getDmaxOnScreen() {
        return DmaxOnScreen;
    }

    public float getDmaxPas() {
        return DmaxPas;
    }

    public boolean isLog() {
        return Log;
    }

    public float[][] getNodePourLissage() {
        return NodePourLissage;
    }
    
    public float getNodePourLissage(int i, int j) {
        return NodePourLissage[i][j];
    }

    public int getNodePourLissageCount() {
        return NodePourLissageCount;
    }

    public float[][] getNodePourLissageHold() {
        return NodePourLissageHold;
    }

    public float getP() {
        return P;
    }

    public boolean isShepard() {
        return Shepard;
    }

    public Gephi[] getTableauGephi() {
        return TableauGephi;
    }
    
    public float getTableauGephiNode(int index, int bts, int i){
        if ( bts == 0 ){
            return (float)TableauGephi[index].btsLon[i];
        } else if ( bts == 1) {
            return (float)TableauGephi[index].btsLat[i];
        } else if ( bts == 2) {
            return (float)TableauGephi[index].btsDegree[i];
        }
        else return 0;
    }
    
    public float getTableauGephiCount(int index, int i){
        if ( i == 0){
            return (float)TableauGephi[index].nodeCount;
        } else if ( i == 1 ){
            return (float)TableauGephi[index].edgeCount;
        }
        else return 0;
    }

    public boolean isChaud() {
        return chaud;
    }

    public boolean isClicked() {
        return clicked;
    }

    public float getClosestDist() {
        return closestDist;
    }

    public float getClosestText() {
        return closestText;
    }

    public float getClosestTextX() {
        return closestTextX;
    }

    public float getClosestTextY() {
        return closestTextY;
    }

    public float getCompMaxEdge() {
        return compMaxEdge;
    }

    public Stick getCurseur() {
        return curseur;
    }

    public Stick getCurseur2() {
        return curseur2;
    }

    public float getD() {
        return d;
    }

    public float getDistMax() {
        return distMax;
    }

    public float getDistMin() {
        return distMin;
    }

    public boolean isDyn() {
        return dyn;
    }

    public boolean isEdge() {
        return edge;
    }

    public boolean isEdgeDistri() {
        return edgeDistri;
    }

    public float getEdgeMax() {
        return edgeMax;
    }

    public float getEdgeMaxdyn() {
        return edgeMaxdyn;
    }

    public float getEdgeMin() {
        return edgeMin;
    }

    public float getEdgeMindyn() {
        return edgeMindyn;
    }

    public boolean isGros() {
        return gros;
    }

    public boolean isHeat() {
        return heat;
    }

    public int getIndex() {
        return index;
    }

    public int getIndexBis() {
        return indexBis;
    }

    public boolean isInfo() {
        return info;
    }

    public Location getLocationCourante() {
        return locationCourante;
    }

    public Map getMap() {
        return map;
    }

    public float[][] getMatEdge() {
        return matEdge;
    }
    
    public float getMatEdge( int i, int j ) {
        return matEdge[i][j];
    }

    public float[] getTabEdgeDist() {
        return tabEdgeDist;
    }

    public float[][] getMatNode() {
        return matNode;
    }
    
    public float getMatNode(int i, int j) {
        return matNode[i][j];
    }
    
    public float[][] getSortant() {
        return sortant;
    }
    
    public float getSortant(int i, int j) {
        return sortant[i][j];
    }
    
    public float[][] getEntrant() {
        return entrant;
    }
    
    public float getEntrant(int i, int j) {
        return entrant[i][j];
    }

    public float getMaxEdgeTotal() {
        return maxEdgeTotal;
    }

    public float getMaxNodeTotal() {
        return maxNodeTotal;
    }

    public PImage getMyMap() {
        return myMap;
    }

    public PImage getMyPoints() {
        return myPoints;
    }

    public int getNbMaxEnfants() {
        return nbMaxEnfants;
    }

    public boolean isNode() {
        return node;
    }

    public boolean isNodeDistri() {
        return nodeDistri;
    }

    public float getNodeMax() {
        return nodeMax;
    }

    public float getNodeMaxdyn() {
        return nodeMaxdyn;
    }

    public float getNodeMin() {
        return nodeMin;
    }

    public float getNodeMindyn() {
        return nodeMindyn;
    }

    public boolean isPetit() {
        return petit;
    }

    public boolean isPremierLissage() {
        return premierLissage;
    }

    public boolean isSelect() {
        return select;
    }

    public void setBiweight(boolean Biweight) {
        this.Biweight = Biweight;
    }

    public void setBoutons(ArrayList Boutons) {
        this.Boutons = Boutons;
    }
    
    public void setOursins(ArrayList Oursins) {
        this.Oursins = Oursins;
    }

    public void setDmax(float Dmax) {
        this.Dmax = Dmax;
    }

    public void setDmaxOnScreen(float DmaxOnScreen) {
        this.DmaxOnScreen = DmaxOnScreen;
    }

    public void setDmaxPas(float DmaxPas) {
        this.DmaxPas = DmaxPas;
    }

    public void setLog(boolean Log) {
        this.Log = Log;
    }

    public void setNodePourLissage(float[][] NodePourLissage) {
        this.NodePourLissage = NodePourLissage;
    }
    
    public void setNodePourLissage(int i, int j, float a) {
        this.NodePourLissage[i][j] = a;
    }

    public void setNodePourLissageCount(int NodePourLissageCount) {
        this.NodePourLissageCount = NodePourLissageCount;
    }

    public void setNodePourLissageHold(float[][] NodePourLissageHold) {
        this.NodePourLissageHold = NodePourLissageHold;
    }

    public void setP(float P) {
        this.P = P;
    }

    public void setShepard(boolean Shepard) {
        this.Shepard = Shepard;
    }

    public void setTableauGephi(Gephi[] TableauGephi) {
        this.TableauGephi = TableauGephi;
    }
    
    public void setTableauGephi(int i, Gephi gephi) {
        this.TableauGephi[i] = gephi;
    }

    public void setChaud(boolean chaud) {
        this.chaud = chaud;
    }

    public void setClicked(boolean clicked) {
        this.clicked = clicked;
    }

    public void setClosestDist(float closestDist) {
        this.closestDist = closestDist;
    }

    public void setClosestText(float closestText) {
        this.closestText = closestText;
    }

    public void setClosestTextX(float closestTextX) {
        this.closestTextX = closestTextX;
    }

    public void setClosestTextY(float closestTextY) {
        this.closestTextY = closestTextY;
    }

    public void setCompMaxEdge(float compMaxEdge) {
        this.compMaxEdge = compMaxEdge;
    }

    public void setCurseur(Stick curseur) {
        this.curseur = curseur;
    }

    public void setCurseur2(Stick curseur2) {
        this.curseur2 = curseur2;
    }

    public void setD(float d) {
        this.d = d;
    }

    public void setDistMax(float distMax) {
        this.distMax = distMax;
    }

    public void setDistMin(float distMin) {
        this.distMin = distMin;
    }

    public void setDyn(boolean dyn) {
        this.dyn = dyn;
    }

    public void setEdge(boolean edge) {
        this.edge = edge;
    }

    public void setEdgeDistri(boolean edgeDistri) {
        this.edgeDistri = edgeDistri;
    }

    public void setEdgeMax(float edgeMax) {
        this.edgeMax = edgeMax;
    }

    public void setEdgeMaxdyn(float edgeMaxdyn) {
        this.edgeMaxdyn = edgeMaxdyn;
    }

    public void setEdgeMin(float edgeMin) {
        this.edgeMin = edgeMin;
    }

    public void setEdgeMindyn(float edgeMindyn) {
        this.edgeMindyn = edgeMindyn;
    }

    public void setGros(boolean gros) {
        this.gros = gros;
    }

    public void setHeat(boolean heat) {
        this.heat = heat;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setIndexBis(int indexBis) {
        this.indexBis = indexBis;
    }

    public void setInfo(boolean info) {
        this.info = info;
    }

    public void setLocationCourante(Location locationCourante) {
        this.locationCourante = locationCourante;
    }

    public void setMap(Map map) {
        this.map = map;
    }

    public void setMatEdge(float[][] matEdge) {
        this.matEdge = matEdge;
    }
    
    public void setMatEdge(int i, int j , float a) {
        this.matEdge[i][j] = a;
    }
    
    public void setSortant(float[][] sortant) {
        this.sortant = sortant;
    }
    
    public void setSortant(int i, int j, float a) {
        this.sortant[i][j] = a;
    }
    
    public void setEntrant(float[][] entrant) {
        this.entrant = entrant;
    }
    
    public void setEntrant(int i, int j, float a) {
        this.entrant[i][j] = a;
    }

    public void setTabEdgeDist(float[] tabEdgeDist) {
        this.tabEdgeDist = tabEdgeDist;
    }
    
    public void setTabEdgeDist(int i, float a) {
        this.tabEdgeDist[i] = a;
    }

    public void setMatNode() {
        this.matNode = new float[3][(int)this.maxNodeTotal];
    }
    
    public void setMatNode(int i, int j , float a) {
        this.matNode[i][j] = a;
    }

    public void setMaxEdgeTotal(float maxEdgeTotal) {
        this.maxEdgeTotal = maxEdgeTotal;
    }

    public void setMaxNodeTotal(float maxNodeTotal) {
        this.maxNodeTotal = maxNodeTotal;
    }

    public void setMyMap(PImage myMap) {
        this.myMap = myMap;
    }

    public void setMyPoints(PImage myPoints) {
        this.myPoints = myPoints;
    }

    public void setNbMaxEnfants(int nbMaxEnfants) {
        this.nbMaxEnfants = nbMaxEnfants;
    }

    public void setNode(boolean node) {
        this.node = node;
    }

    public void setNodeDistri(boolean nodeDistri) {
        this.nodeDistri = nodeDistri;
    }

    public void setNodeMax(float nodeMax) {
        this.nodeMax = nodeMax;
    }

    public void setNodeMaxdyn(float nodeMaxdyn) {
        this.nodeMaxdyn = nodeMaxdyn;
    }

    public void setNodeMin(float nodeMin) {
        this.nodeMin = nodeMin;
    }

    public void setNodeMindyn(float nodeMindyn) {
        this.nodeMindyn = nodeMindyn;
    }

    public void setPetit(boolean petit) {
        this.petit = petit;
    }

    public void setPremierLissage(boolean premierLissage) {
        this.premierLissage = premierLissage;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

    public boolean isOursin() {
        return oursin;
    }

    public void setOursin(boolean oursin) {
        this.oursin = oursin;
    }
}
