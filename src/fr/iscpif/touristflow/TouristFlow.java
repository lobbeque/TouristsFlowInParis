/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.iscpif.touristflow;

import de.fhpotsdam.unfolding.geo.Location;
import processing.core.*; 

/**
 *
 * @author jbilcke
 */
public class TouristFlow extends PApplet {

    /**
     * @param args the command line arguments
     */
    
    
    public static void main(String[] args) {
       // must match the name of your class ie "letsp5.Main" = packageName.className 
       PApplet.main( new String[]{"fr.iscpif.touristflow.TouristFlow"} ); 
   }
       
    @Override
   public void setup () { 
      Application.session.setPApplet(this);
      
      size(1400, 979);
      
      Application.session.setMap(new de.fhpotsdam.unfolding.Map(this));
      Application.session.map.zoomAndPanTo(new Location(48.866f, 2.359f), 10);// position de départ de la carte 
      de.fhpotsdam.unfolding.utils.MapUtils.createDefaultEventDispatcher(this, Application.session.getMap());
      
      Temps.setupDates(); 
      Bibliotheque.loadGraph(); 

      // les info sur les noeuds et edges sont stockés dans 2 matrices
      Application.session.setMatEdge(new float[5][(int)Application.session.getMaxEdgeTotal()]);
      Application.session.setMatNode(); 
      Bibliotheque.remplirTableauImage( Application.session.getIndex() ); 



      Application.session.setMyPoints(loadImage( "/home/guest/Bureau/Mon_script/data/ppp.png" ));

      smooth( );


      String[] arr = new String[] {"Noeud","Arc","Lissée"};
      Application.session.getBoutons().add( new BoutonMenu ( 50, 80, 50, "Carte", true, arr ));
      String[] arr1 = new String[] {"Info","Select","HeatMap"};
      Application.session.getBoutons().add( new BoutonMenu ( 30, 150, 50, "Noeud",arr1 ));
      Application.session.getBoutons().add( new BoutonMenu ( 20, 210, 30, "Info" ));
      Application.session.getBoutons().add( new BoutonMenu ( 20, 245, 50, "Select" ));
      Application.session.getBoutons().add( new BoutonMenu ( 20, 210, 70, "HeatMap" ));
      String[] arr2 = new String[] {"Exp(1/x)","Max Relatif","Log"};
      Application.session.getBoutons().add( new BoutonMenu ( 30, 80, 120, "Arc", arr2 ));
      Application.session.getBoutons().add( new BoutonMenu ( 20, 130, 185, "Max Relatif" ));
      Application.session.getBoutons().add( new BoutonMenu ( 20, 80, 185, "Exp(1/x)" ));
      Application.session.getBoutons().add( new BoutonMenu ( 20, 30, 185, "Log" ));
      String[] arr3 = new String[] {"Biweight","Shepard"};
      Application.session.getBoutons().add( new BoutonMenu ( 30, 150, 120, "Lissée", arr3 ));
      Application.session.getBoutons().add( new BoutonMenu ( 20, 210, 185, "Biweight" ));
      Application.session.getBoutons().add( new BoutonMenu ( 20, 210, 120, "Shepard" ));

      Bibliotheque.miseAJourMatriceDistance(Application.session.getIndex());
      
      // tableau qui contiendra les noeuds pour le lissage
      Application.session.setNodePourLissage( new float[3][Application.session.getTableauGephi()[Application.session.getIndex()].nodeCount]);
      
      Bibliotheque.meter2Pixel();
      
      Bibliotheque.distMinMax(); 
      
      // création des deux curseurs de sélection pour le lissage
      Application.session.setCurseur ( new Stick( 10, (float)(width/56 + 165), (float)(height/1.4 + 53), Application.session.getDmax(), (float)(width/4.375 - 10 - 165)));
      Application.session.setCurseur2 ( new Stick( 10, (float)(width/56 + 165), (float)(height/1.4 + 18), Application.session.getP(), (float)(width/4.375 - 10 - 165)));
   } 
    

    @Override
   public void draw () { 
       
      background(0);
      Application.session.getMap().draw();

      fill( 15, 9, 105, 100 );
      rect ( 0, 0, width, height );
      noFill();
      
      // Mises à jour s'il y a changement d'interval
      if ( Application.session.getIndexBis() != Application.session.getIndex() ) {
           
          Bibliotheque.remplirTableauImage(Application.session.getIndex());
          Bibliotheque.miseAJourMatriceDistance(Application.session.getIndex());
          Bibliotheque.distMinMax(); 
      }

      PFont font1 = createFont("DejaVuSans-ExtraLight-", 15);
      textFont(font1); 

      Application.session.setClosestDist(MAX_FLOAT);

      if ( Application.session.isDyn() ){
        Bibliotheque.MinMax();  
      }
      
      if (( ! Application.session.isHeat() ) && ( Application.session.isEdge() )) { // affichage des edges 
        Edge.afficheEdge();
      }

      if (( Application.session.isNode() ) || ( ! Application.session.isHeat() )) { // affichage des nodes
        Node.afficheNode();
      }

      stroke(153);
      fill(255, 255, 255, 100);
      
      // affichage de la time line
      Temps.drawDateSelector( );
     
      // affiche la légende en mode edge ou node 
      if (! Application.session.isHeat()) { 
         Affichage.afficheLegendeNodeEdge();
      }
      
      // affiche la légende en mode lissée
      if ( Application.session.isHeat() ){
        Affichage.afficheLegendeLissee();
      }

      Application.session.setEdgeMindyn(MAX_FLOAT);
      Application.session.setEdgeMaxdyn(MIN_FLOAT);
      Application.session.setNodeMindyn(MAX_FLOAT);
      Application.session.setNodeMaxdyn(MIN_FLOAT);  

      ellipseMode(CENTER);

      Affichage.afficheEchelle(); 

      
      for (int i = 0; i < Application.session.getBoutons().size(); i++) {
        BoutonMenu boutonMenu = (BoutonMenu) Application.session.getBoutons().get(i);
        boutonMenu.draw();
      }

      fill( 255);
      Application.session.setMatNode(0, 500, 89);
      text((float)Application.session.getMatNode(0, 500),600,600);
      text((float)Application.session.getMatNode(1, 500),600,650);
      text((float)Application.session.getMatNode(2, 500),600,700);
      text((float)Application.session.getTableauGephiCount(Application.session.getIndex(), 0), 600, 750);
      text((int)Application.session.getMaxNodeTotal(), 600, 800);
    }
   
    @Override
    public void mousePressed () {
  
      for (int i = 0; i < Application.session.getBoutons().size(); i++) {
        BoutonMenu boutonMenu = (BoutonMenu) Application.session.getBoutons().get(i);
        boutonMenu.pressed(mouseX, mouseY); // appel de la routine pour chaque noeud du menu
      }

      Application.session.setClicked(true);

      // Zones de changement d'interval
      float dist1 = dist ( 585, 35, mouseX, mouseY );
      float dist2 = dist ( 810, 35, mouseX, mouseY );
      if ( dist1 < 15 ) {
        int newHour = Temps.getHourIndex() - 1;
        Temps.setHour(newHour);
      }
      if ( dist2 < 15 ) {
        int newHour = Temps.getHourIndex() + 1;
        Temps.setHour(newHour);
      }

      float dist5 = dist( (float)(width/70 + width/8/2/11.6), (float)(height/1.8 + width/8/2/11.6), mouseX, mouseY );
      float dist6 = dist( (float)(width/70 + width/8/2/11.6), (float)(height/1.8 - width/8/2/11.6), mouseX, mouseY );
      if ( ! Application.session.isNodeDistri() ){
        if ( dist5 < 15 ) {
          Application.session.setNodeDistri(true);
        }
      } else {
        if (( dist5 < 15 ) || ( dist6 < 15 ))
          Application.session.setNodeDistri(false);
      }
      float dist7 = dist( (float)(width/70 + width/8/2/11.6 + width/8 - width/8/11.6), (float)(height/1.8 + width/8/2/11.6 + height/9.79), mouseX, mouseY );
      float dist8 = dist( (float)(width/70 + width/8/2/11.6 + width/8 - width/8/11.6), (float)(height/1.8 - width/8/2/11.6 + height/9.79), mouseX, mouseY );
      if ( ! Application.session.isEdgeDistri() ){
        if ( dist7 < 15 ) {
          Application.session.setEdgeDistri(true);
        }
      } else {
        if (( dist7 < 15 ) || ( dist8 < 15 ))
          Application.session.setEdgeDistri(false);
      }
    }



    @Override
    public void mouseReleased () {
      Application.session.setClicked(false);
    }


    @Override
    public void mouseDragged(){
      Application.session.getCurseur().dragged(mouseX,mouseY);
      Application.session.getCurseur2().dragged(mouseX,mouseY);
    }
   
   
}