/*

Copyright : UMR Géographie Cités - Quentin Lobbé (2012)

Authors : 
Quentin Lobbé <quentin.lobbe@gmail.com>
Julie Fen-Chong <julie.fenchong@gmail.com>
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

import de.fhpotsdam.unfolding.Map;
import de.fhpotsdam.unfolding.geo.Location;
import java.util.ArrayList;
import processing.core.*; 

/**
 *
 * @author Quentin Lobbé
 */
public class Db {
       
    PApplet p;
    
    // pour la carte zoomée
    de.fhpotsdam.unfolding.Map map;
    de.fhpotsdam.unfolding.geo.Location locationCourante;

    // pour le menu
    java.util.ArrayList buttons = new java.util.ArrayList();
    
    // pour les oursins
    java.util.ArrayList urchins = new java.util.ArrayList();
    
    // pour les arrows
    ArrayList arrowsIN = new ArrayList();
    ArrayList arrowsOUT = new ArrayList();
    float arrowsMax = 5;
    
    int nbMaxEnfants = 30; 

    // pour le heatmap
    PImage myPoints;

    // pour le gephi
    Gephi[] gephiLoaders = new Gephi[25]; 

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
    int nodeForSmoothingCount;
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
    float[][] outputs;
    float[][] inputs; 
    
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
    boolean shepard = false;
    boolean boxcox = false;
    boolean chaud = false;
    boolean nodeDistri = false;
    boolean edgeDistri = false;
    boolean lissageDistri = false;
    boolean edgeBoxCoxDistri = false;
    boolean nodeBoxCoxDistri = false;
    boolean logTransform = false;
    boolean firstSmoothing = false; 
    boolean isUrchin = false;
    boolean dragged = false;
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
        return buttons;
    }
    
    public ArrayList getOursins() {
        return urchins;
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
        return logTransform;
    }

    public float[][] getNodePourLissage() {
        return NodePourLissage;
    }
    
    public float getNodePourLissage(int i, int j) {
        return NodePourLissage[i][j];
    }

    public int getNodePourLissageCount() {
        return nodeForSmoothingCount;
    }

    public float[][] getNodePourLissageHold() {
        return NodePourLissageHold;
    }

    public float getP() {
        return P;
    }

    public boolean isShepard() {
        return shepard;
    }

    public Gephi[] getTableauGephi() {
        return gephiLoaders;
    }
    
    public float getTableauGephiNode(int index, int bts, int i){
        if ( bts == 0 ){
            return (float)gephiLoaders[index].btsLon[i];
        } else if ( bts == 1) {
            return (float)gephiLoaders[index].btsLat[i];
        } else if ( bts == 2) {
            return (float)gephiLoaders[index].btsDegree[i];
        }
        else return 0;
    }
    
    public float getTableauGephiCount(int index, int i){
        if ( i == 0){
            return (float)gephiLoaders[index].nodeCount;
        } else if ( i == 1 ){
            return (float)gephiLoaders[index].edgeCount;
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
        return outputs;
    }
    
    public float getSortant(int i, int j) {
        return outputs[i][j];
    }
    
    public float[][] getEntrant() {
        return inputs;
    }
    
    public float getEntrant(int i, int j) {
        return inputs[i][j];
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

    public void setDragged(boolean draged) {
        this.dragged = draged;
    }

    public boolean isDragged() {
        return dragged;
    }

    public boolean isPetit() {
        return petit;
    }

    public boolean isPremierLissage() {
        return firstSmoothing;
    }

    public boolean isSelect() {
        return select;
    }

    public void setBiweight(boolean Biweight) {
        this.Biweight = Biweight;
    }

    public void setButtons(ArrayList buttons) {
        this.buttons = buttons;
    }
    
    public void setUrchins(ArrayList urchins) {
        this.urchins = urchins;
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

    public void setLogTransform(boolean logTransform) {
        this.logTransform = logTransform;
    }

    public void setNodePourLissage(float[][] NodePourLissage) {
        this.NodePourLissage = NodePourLissage;
    }
    
    public void setNodePourLissage(int i, int j, float a) {
        this.NodePourLissage[i][j] = a;
    }

    public void setNodeforSmoothingCount(int nodeForSmoothingCount) {
        this.nodeForSmoothingCount = nodeForSmoothingCount;
    }

    public void setNodePourLissageHold(float[][] NodePourLissageHold) {
        this.NodePourLissageHold = NodePourLissageHold;
    }

    public void setP(float P) {
        this.P = P;
    }

    public void setShepard(boolean shepard) {
        this.shepard = shepard;
    }

    public void setGephiLoaders(Gephi[] gephiLoaders) {
        this.gephiLoaders = gephiLoaders;
    }
    
    public void setGephiLoader(int i, Gephi gephi) {
        this.gephiLoaders[i] = gephi;
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
    
    public void setOutputs(float[][] outputs) {
        this.outputs = outputs;
    }
    
    public void setOutput(int i, int j, float a) {
        this.outputs[i][j] = a;
    }
    
    public void setInputs(float[][] inputs) {
        this.inputs = inputs;
    }
    
    public void setInput(int i, int j, float a) {
        this.inputs[i][j] = a;
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

    public void setFirstSmoothing(boolean premierLissage) {
        this.firstSmoothing = premierLissage;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

    public boolean isUrchin() {
        return isUrchin;
    }

    public void isUrchin(boolean urchin) {
        this.isUrchin = urchin;
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
