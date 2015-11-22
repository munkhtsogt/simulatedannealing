/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bitirme;

import bitirme.gui.SAinterface;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 *
 * @author munkhuu
 */
public class Main {
    SAinterface in;
    public static void main(String[] args) throws FileNotFoundException, IOException {
         SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            SAinterface s = null;
                try {
                    s = new SAinterface();
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            JFrame jf = new JFrame();
            jf.add(s);
            jf.pack();
            jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            jf.setLocationRelativeTo(null);
            jf.setVisible(true);

           }
         });
         

//       GmlGraph gml = new GmlGraph();
//       gml.OpenGmlFile("/home/munkhuu/Desktop/Graphs/nepusz_7_8.gml");
//       SimulatedAnnealing sa = new SimulatedAnnealing();
//       sa.SA();
//       gml.CloseGmlFile();
       
    }
}
