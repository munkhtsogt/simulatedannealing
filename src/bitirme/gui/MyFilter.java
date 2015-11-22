/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bitirme.gui;

import java.io.File;

/**
 *
 * @author karaa
 */
public class MyFilter extends javax.swing.filechooser.FileFilter {

    public boolean accept(File file) {
        String filename = file.getName();
        return filename.endsWith(".gml");
    }

    public String getDescription() {
        return "*.gml";
    }
}
