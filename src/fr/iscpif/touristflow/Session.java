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
 * @author Quentin Lobbé
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
    
    // pour les arrows
    ArrayList arrowsIN = new ArrayList();
    ArrayList arrowsOUT = new ArrayList();
    float arrowsMax = 5;
    
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
    float DmaxSmooth = 500;
    float DmaxPas = 50; 
    float DmaxOnScreen; 
    float P = (float) 0.4;
    float d;
    int NodePourLissageCount;
    float[][] NodePourLissage; 
    float[][] NodePourLissageHold; 
    Stick curseur;
    Stick curseur2;
    Stick curseur3;
    Stick curseur4;
    Stick curseur5;
    
    // pour tester le champ de flèches
    Stick curseur6;
    Stick curseur7;
    
    // pour les arraws
    String[] referencesArrows;
    
    
    // pour le box cox 
    float lambdaE = 1;
    float lambdaN = 1;


    PImage myMap;

    // matrices de stockage des noeuds et edges courants ( triées du poids fort au poids faible )
    float[][] matEdge;
    float[][] matNode;
    float[][] sortant;
    float[][] entrant; 
    
    float[] EdgePoids;
    float[] NodePoids;

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
    boolean boxcoxNode = false;
    boolean heat = false;
    boolean petit = false;
    boolean gros = true;
    boolean edge = true;
    boolean node = false;
    boolean Biweight = false;
    boolean Shepard = false;
    boolean boxcox = false;
    boolean chaud = false;
    boolean nodeDistri = false;
    boolean edgeDistri = false;
    boolean lissageDistri = false;
    boolean edgeBoxCoxDistri = false;
    boolean nodeBoxCoxDistri = false;
    boolean Log = false;
    boolean premierLissage = false; 
    boolean oursin = false;
    boolean draged = false;
    boolean kmeansDraw = false;
    boolean arrow = false;
    boolean IN = true;
    boolean OUT = true;

    // Définition de constantes pour le calcul des Min et Max 
    float nodeMin = PConstants.MAX_FLOAT;
    float nodeMax = PConstants.MIN_FLOAT;
    float edgeMin = PConstants.MAX_FLOAT;
    float edgeMax = PConstants.MIN_FLOAT;

    float distMax = PConstants.MIN_FLOAT;
    float distMin = PConstants.MAX_FLOAT;

    float compMaxEdge = PConstants.MIN_FLOAT;

    static float maxEdgeTotal = PConstants.MIN_FLOAT;
    static float maxNodeTotal = PConstants.MIN_FLOAT;
    
    float nodeEffMax =  PConstants.MIN_FLOAT;
    float edgeEffMax =  PConstants.MIN_FLOAT;
    
    float[][] NBRoamBTSMoy;

    public float[][] getNBRoamBTSMoy() {
        return NBRoamBTSMoy;
    }
    
    public float[] getNBRoamBTSMoy(int i) {
        return NBRoamBTSMoy[i];
    }
    
    public float getNBRoamBTSMoy( int i, int j ) {
        return NBRoamBTSMoy[i][j];
    }

    public void setNBRoamBTSMoy(float[][] NBRoamBTSMoy) {
        this.NBRoamBTSMoy = NBRoamBTSMoy;
    }
    
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

    public Stick getCurseur3() {
        return curseur3;
    }

    public void setCurseur3(Stick curseur3) {
        this.curseur3 = curseur3;
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

    public boolean isBoxCox() {
        return boxcox;
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

    public float getEdgeMin() {
        return edgeMin;
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

    public boolean isBoxCoxNode() {
        return boxcoxNode;
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

    public float[] getEdgePoids() {
        return EdgePoids;
    }
    
    public float getEdgePoids( int i) {
        return EdgePoids[i];
    }
    
    public float[] getNodePoids() {
        return NodePoids;
    }
    
    public float getNodePoids( int i) {
        return NodePoids[i];
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

    public float getNodeMin() {
        return nodeMin;
    }

    public void setDraged(boolean draged) {
        this.draged = draged;
    }

    public boolean isDraged() {
        return draged;
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

    public void setBoxCox(boolean dyn) {
        this.boxcox = dyn;
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

    public void setEdgeMin(float edgeMin) {
        this.edgeMin = edgeMin;
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

    public void setBoxCoxNode(boolean boxcoxNode) {
        this.boxcoxNode = boxcoxNode;
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

    public void setEdgePoids(float[] EdgePoids) {
        this.EdgePoids = EdgePoids;
    }
    
    public void setEdgePoids(int i, float a ) {
        this.EdgePoids[i] = a;
    }
    
    public void setNodePoids(float[] NodePoids) {
        this.NodePoids = NodePoids;
    }
    
    public void setNodePoids(int i, float a ) {
        this.NodePoids[i] = a;
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
    
    public float getTabEdgeDist(int i) {
        return tabEdgeDist[i];
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

    public void setNodeMin(float nodeMin) {
        this.nodeMin = nodeMin;
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

    public boolean isEdgeBoxCoxDistri() {
        return edgeBoxCoxDistri;
    }

    public void setEdgeBoxCoxDistri(boolean edgeBoxCoxDistri) {
        this.edgeBoxCoxDistri = edgeBoxCoxDistri;
    }

    public float getLambdaE() {
        return lambdaE;
    }

    public void setLambdaE(float lambdaE) {
        this.lambdaE = lambdaE;
    }
    
    public float getLambdaN() {
        return lambdaN;
    }

    public void setLambdaN(float lambdaN) {
        this.lambdaN = lambdaN;
    }

    public float getEdgeEffMax() {
        return edgeEffMax;
    }

    public float getNodeEffMax() {
        return nodeEffMax;
    }

    public void setEdgeEffMax(float edgeEffMax) {
        this.edgeEffMax = edgeEffMax;
    }

    public void setNodeEffMax(float nodeEffMax) {
        this.nodeEffMax = nodeEffMax;
    }

    public Stick getCurseur4() {
        return curseur4;
    }

    public void setCurseur4(Stick curseur4) {
        this.curseur4 = curseur4;
    }
    
    public Stick getCurseur5() {
        return curseur5;
    }

    public void setCurseur5(Stick curseur5) {
        this.curseur5 = curseur5;
    }

    public boolean isNodeBoxCoxDistri() {
        return nodeBoxCoxDistri;
    }

    public void setNodeBoxCoxDistri(boolean nodeBoxCoxDistri) {
        this.nodeBoxCoxDistri = nodeBoxCoxDistri;
    }

    public boolean isKmeansDraw() {
        return kmeansDraw;
    }

    public void setKmeansDraw(boolean kmeansDraw) {
        this.kmeansDraw = kmeansDraw;
    }

    public boolean isArrow() {
        return arrow;
    }

    public void setArrow(boolean arrow) {
        this.arrow = arrow;
    }

    public Stick getCurseur6() {
        return curseur6;
    }

    public Stick getCurseur7() {
        return curseur7;
    }

    public void setCurseur6(Stick curseur6) {
        this.curseur6 = curseur6;
    }

    public void setCurseur7(Stick curseur7) {
        this.curseur7 = curseur7;
    }

    public float getArrowsMax() {
        return arrowsMax;
    }

    public void setArrowsMax(float arrowsMax) {
        this.arrowsMax = arrowsMax;
    }


    public String getReferencesArrows( int i ) {
        return referencesArrows[i];
    }

    public void setReferencesArrows(String[] referencesArrows) {
        this.referencesArrows = referencesArrows;
    }
    
    public void setReferencesArrows(int i, String str) {
        this.referencesArrows[i] = str;
    }

    public boolean isLissageDistri() {
        return lissageDistri;
    }

    public void setLissageDistri(boolean lissageDistri) {
        this.lissageDistri = lissageDistri;
    }

    public boolean isIN() {
        return IN;
    }

    public boolean isOUT() {
        return OUT;
    }

    public void setIN(boolean IN) {
        this.IN = IN;
    }

    public void setOUT(boolean OUT) {
        this.OUT = OUT;
    }

    public float getDmaxSmooth() {
        return DmaxSmooth;
    }

    public void setDmaxSmooth(float DmaxSmooth) {
        this.DmaxSmooth = DmaxSmooth;
    }
    
    
    
    
}
