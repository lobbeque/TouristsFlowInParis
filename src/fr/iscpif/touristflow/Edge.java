/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.iscpif.touristflow;
import processing.core.*; 
import de.fhpotsdam.unfolding.geo.Location;
/**
 *
 * @author Quentin lobbé
 */
public class Edge {
    public static void afficheEdge(){
        PApplet p = Application.session.getPApplet();
        int compt = 0;

        for (int i = 0; i < Application.session.getTableauGephi()[Application.session.getIndex()].edgeCount; i++) {
          // Transformation des coordonnées de chaque edge
          Location l1 = new Location(Application.session.getMatEdge(0, i),Application.session.getMatEdge(1, i));
          Location l2 = new Location(Application.session.getMatEdge(2, i),Application.session.getMatEdge(3, i));
          float xy1[] = Application.session.getMap().getScreenPositionFromLocation(l1);
          float xy2[] = Application.session.getMap().getScreenPositionFromLocation(l2);

          float distance = Bibliotheque.distFrom( l1.getLat(), l1.getLon(), l2.getLat(), l2.getLon() );


          // Valuation de la transparence des edges en fonction de leur poids
          float value = Application.session.getMatEdge(4,i);
          
            float poids = 0;
            if ( Application.session.isLog() ) {
                if ( value > 2 ) {
                    poids =  PApplet.map(PApplet.log(value), PApplet.log(2),PApplet.log(Application.session.getEdgeMax()), 1, 10);
                }
            } else {
              poids =  PApplet.map(value, 2,Application.session.getEdgeMax(), 1, 15);
            }
              p.strokeWeight(poids);
          
          float a = 0;

          // Plus grande visibilité aux petits edges
          if ( Application.session.isPetit() && (! Application.session.isGros()) ) {

            a = PApplet.map(distance, Application.session.getDistMin(), Application.session.getDistMax(), (float)0.18,10); 
          }

          // Plus grande visibilité aux edges de poids forts 
          if ( Application.session.isGros() && ( ! Application.session.isPetit()) ) {
            if ( ! Application.session.isDyn() ) {
              a = PApplet.map(value, 0, Application.session.getEdgeMax(), 15, 255);
            } else {
              a = PApplet.map(value, 0, Application.session.getEdgeMaxdyn(), 15, 255);
            }
            if ( value < 2 )
                a = 0;
            //p.stroke(255, 227, 0, a);
            p.stroke(16, 91, 136, a);
          }
         if ( Application.session.isGros() && ( ! Application.session.isPetit()) ) { 
           if ( ( ( (xy1[1] > 0 ) && (xy1[0] > 0 ) && (xy1[0] < p.width ) && ( xy1[1] < p.height ) ) || ( (  xy2[0] > 0 ) && (  xy2[1] > 0 ) && (  xy2[0] < p.width ) && (  xy2[1] < p.height ) ) ) && ( Application.session.getMatEdge(4, i) > 2 ) ) { // on filtre en affichant uniquement les 2000 liens les plus forts situés dans la zone de viz
              p.line(xy1[0], xy1[1], xy2[0], xy2[1] ); 
              compt ++;// on affiche que les 2000 noeuds de poids forts dont l'une au moins des extrémités est visible à l'écran
            }
         } else {
           if  ( ( (xy1[1] > 0 ) && (xy1[0] > 0 ) && (xy1[0] < p.width ) && ( xy1[1] < p.height ) ) || ( (  xy2[0] > 0 ) && (  xy2[1] > 0 ) && (  xy2[0] < p.width ) && (  xy2[1] < p.height ) ) ) {
             p.stroke( 16,91, 136, PApplet.exp( 1 / a ) );
             p.line(xy1[0], xy1[1], xy2[0], xy2[1] ); 

           }
         }
        }
    }
}
