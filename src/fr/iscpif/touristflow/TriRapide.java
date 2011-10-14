/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.iscpif.touristflow;

/**
 *
 * @author Quentin Lobbé
 */
public class TriRapide {
    
    // version de tri d'une matrice
    
    static void echanger(float mat[][], float a, float b, int longmat) {
        for ( int i  = 0; i < longmat; i ++ ) { 
          float temp = mat[i][(int)a];
          mat[i][(int)a] = mat[i][(int)b];
          mat[i][(int)b] = temp;
        }
    }
    
    static float partition( float mat[][], float debut, float fin, int longmat ) {
        float compt = debut;
        float pivot = mat[longmat - 1][(int)debut];
        int i;
  
        for ( i = (int)debut + 1; i <= fin; i ++ ) {
           if ( mat[longmat - 1][i] > pivot ) {
               compt ++;
               echanger ( mat, compt, i, longmat );
           }
        }
        echanger( mat, compt, debut, longmat );
        return( compt );
    }
    
    static void trirapidebis( float mat[][], float debut, float fin, int longmat ) {
        if( debut < fin ) {
            float pivot = partition ( mat, debut, fin, longmat );
            trirapidebis( mat, debut, pivot - 1, longmat );
            trirapidebis( mat, pivot + 1, fin, longmat );
        }
    }
    
    static void trirapide( float mat[][], int longueur, int longmat ) {
         trirapidebis( mat, 0, longueur - 1, longmat );
    }
    
    // version de tri d'un unique tableau

    static void echanger2(float tab[], float a, float b) {
        float temp = tab[(int)a];
        tab[(int)a] = tab[(int)b];
        tab[(int)b] = temp;
    }

    static float partition2( float tab[], float debut, float fin) {
      float compt = debut;
      float pivot = tab[(int)debut];
      int i;

      for ( i = (int)debut + 1; i <= fin; i ++ ) {
        if ( tab[i] > pivot ) {
          compt ++;
          echanger2( tab, compt, i );
        }
      }
      echanger2( tab, compt, debut );
      return( compt );
    }

    static void trirapidebis2( float tab[], float debut, float fin ) {
      if( debut < fin ) {
        float pivot = partition2( tab, debut, fin );
        trirapidebis2( tab, debut, pivot - 1 );
        trirapidebis2( tab, pivot + 1, fin );
      }
    }

    static void trirapide2( float tab[], int longueur ) {
      trirapidebis2( tab, 0, longueur - 1 );
    }
    
}
