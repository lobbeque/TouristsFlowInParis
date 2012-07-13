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
