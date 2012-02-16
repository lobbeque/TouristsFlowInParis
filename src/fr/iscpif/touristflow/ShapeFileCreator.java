/*

Copyright Quentin Lobbé (2012)
Author : Quentin Lobbé <quentin.lobbe@gmail.com>
Contributor : Julian Bilcke

This file is a part of TouristsFlowInParis Project

Build with Processing ( Ben Fry, Casey Reas ) ( GNU GPL )
Build with pyShp ( jlawhead@geospatialpython.org ) ( MIT Licence )


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

import org.python.util.PythonInterpreter;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.script.ScriptException;
import static java.lang.System.*;
import processing.core.*;

/**
 *
 * @author Quentin Lobbé & Julian Bilcke
 */
public class ShapeFileCreator {

    //ScriptEngine python;
    PythonInterpreter python;

    ShapeFileCreator() {
        python = new PythonInterpreter();
        // python = new ScriptEngineManager().getEngineByName("python");

        python.execfile("lib/pyshapefile.py");


    }

    private static String readFileAsString(String filePath) throws java.io.IOException {
        byte[] buffer = new byte[(int) new File(filePath).length()];
        BufferedInputStream f = null;
        try {
            f = new BufferedInputStream(new FileInputStream(filePath));
            f.read(buffer);
        } finally {
            if (f != null) {
                try {
                    f.close();
                } catch (IOException ignored) {
                }
            }
        }
        return new String(buffer);
    }

    // nous n'exportons que ce que nous voyons à l'écran 
    // par exemple, pour exporter les nodes il faut afficher les nodes
    // puis cliquer sur '2' pour appeler l'export 
    // l'export se fera dans le fichier Output 
    // info sur l'utilisation de la librairie PyShp : http://code.google.com/p/pyshp/wiki/PyShpDocs
    public void export() throws ScriptException {

        if (Application.session.isNode()) {
            exportNode();
        }

        if (Application.session.isEdge()) {
            exportEdge();
        }

        if (Application.session.isArrow() && !Affichage.isDrawArrow()) {

            exportArrow();
        }

        if (Application.session.isArrow() && Affichage.isDrawArrow()) {

            exportArrowSmooth();
        }

    }

    void exportNode() {
        out.println("Ecriture Noeuds en cours");

        python.exec("w = Writer()");
        python.exec("w = Writer(POINT)");
        python.exec("w.field('TYPE')");
        python.exec("w.field('ID','N','40')");
        python.exec("w.field('DEGREE','N','40')");
        for (int i = 0; i < Application.session.getTableauGephi()[Application.session.getIndex()].nodeCount; i++) {
            python.exec("w.point('" + Application.session.getMatNode(1, i) + "','" + Application.session.getMatNode(0, i) + "')");
            python.exec("w.record('BTS','" + i + "','" + Application.session.getMatNode(2, i) + "')");
        }
        String fichierSortieNodes = "output/nodes_" + Temps.getDateText();
        python.exec("w.save('" + fichierSortieNodes + "')");

        out.println("Ecriture Noeuds fin");
    }

    void exportEdge() {
        out.println("Ecriture Arcs en cours");

        python.exec("w1 = Writer()");
        python.exec("w1 = Writer(POLYLINE)");
        python.exec("w1.field('TYPE')");
        python.exec("w1.field('ID')");
        python.exec("w1.field('POIDS','N','40')");
        for (int i = 0; i < Application.session.getTableauGephi()[Application.session.getIndex()].edgeCount; i++) {
            python.exec("w1.line(parts=[[[" + Application.session.getMatEdge(1, i) + "," + Application.session.getMatEdge(0, i) + "],[" + Application.session.getMatEdge(3, i) + "," + Application.session.getMatEdge(2, i) + "]]])");
            python.exec("w1.record('Flow','" + i + "','" + Application.session.getMatEdge(4, i) + "')");
        }
        String fichierSortieEdges = "output/edges_" + Temps.getDateText();
        python.exec("w1.save('" + fichierSortieEdges + "')");

        out.println("Ecriture Arcs fin");
    }

    void exportArrow() {

        out.println("Ecriture Flèches en cours");

        python.exec("w = Writer()");
        python.exec("w1 = Writer()");
        python.exec("w = Writer(POINT)");
        python.exec("w1 = Writer(POINT)");
        python.exec("w.field('TYPE')");
        python.exec("w.field('ANGLE','N','40')");
        python.exec("w.field('POIDS','N','40')");
        python.exec("w1.field('TYPE')");
        python.exec("w1.field('ANGLE','N','40')");
        python.exec("w1.field('POIDS','N','40')");
        for (int i = 0; i < Application.session.arrowsIN.size(); i++) {
            Arrow a = (Arrow) Application.session.arrowsIN.get(i);
            Arrow b = (Arrow) Application.session.arrowsOUT.get(i);
            
                python.exec("w.point('" + a.getY() + "','" + a.getX() + "')");
                python.exec("w.record('ARROW IN','" + PApplet.degrees(a.getAngle() - PConstants.PI / 2) + "','" + a.getSize() + "')");
            
                python.exec("w1.point('" + b.getY() + "','" + b.getX() + "')");
                python.exec("w1.record('ARROW OUT','" + PApplet.degrees(b.getAngle() - PConstants.PI / 2) + "','" + b.getSize() + "')");
            
        }
        String fichierSortieArrowsIN = "output/arrowsIN_" + Temps.getDateText();
        python.exec("w.save('" + fichierSortieArrowsIN + "')");
        String fichierSortieArrowsOUT = "output/arrowsOUT_" + Temps.getDateText();
        python.exec("w1.save('" + fichierSortieArrowsOUT + "')");



        out.println("Ecriture Flèches fin");
    }

    void exportArrowSmooth() {

        out.println("Ecriture Flèches lissées en cours");

        python.exec("w = Writer()");
        python.exec("w1 = Writer()");
        python.exec("w = Writer(POINT)");
        python.exec("w1 = Writer(POINT)");
        python.exec("w.field('TYPE')");
        python.exec("w.field('ANGLE','N','40')");
        python.exec("w.field('POIDS','N','40')");
        python.exec("w1.field('TYPE')");
        python.exec("w1.field('ANGLE','N','40')");
        python.exec("w1.field('POIDS','N','40')");
        for (int i = 0; i < Smooth.arrowsINsmooth.size(); i++) {
            Arrow a = (Arrow) Smooth.arrowsINsmooth.get(i);
            Arrow b = (Arrow) Smooth.arrowsOUTsmooth.get(i);
            if (a.getTaille() > 0) {
            python.exec("w.point('" + a.getY() + "','" + a.getX() + "')");
            python.exec("w.record('ARROW IN Smooth','" + PApplet.degrees(-a.getAngle() + PConstants.PI - PConstants.PI/2) + "','" + a.getTaille() + "')");
            }
            if (b.getTaille() > 0) {
            python.exec("w1.point('" + b.getY() + "','" + b.getX() + "')");
            python.exec("w1.record('ARROW OUT Smooth','" + PApplet.degrees(-b.getAngle() + 2*PConstants.PI - PConstants.PI/2 ) + "','" + b.getTaille() + "')");
            }
            }
        String fichierSortieArrowsIN = "output/arrowsSmooth_IN_" + Temps.getDateText();
        python.exec("w.save('" + fichierSortieArrowsIN + "')");
        String fichierSortieArrowsOUT = "output/arrowsSmooth_OUT_" + Temps.getDateText();
        python.exec("w1.save('" + fichierSortieArrowsOUT + "')");



        out.println("Ecriture Flèches lissée fin");
    }
}
