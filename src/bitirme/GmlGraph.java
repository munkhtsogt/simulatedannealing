/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bitirme;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.ujmp.core.MatrixFactory;


/**
 *
 * @author munkhuu
 */
public class GmlGraph {
    private String nodeId, nodeLabel, nodeValue;
    private String edgeSource, edgeTarget, edgeValue;
    private int edgeBegin;
    FileInputStream fstream;
    DataInputStream in;
    public static int n;                           // sutun sayisi
    public static int m;                           // ayirt sayisi
    //BufferedReader br;
    public void Nodes(String GMLpath) throws FileNotFoundException, IOException
    {
     int counter = 0;
     try
     {
      // path --> "/home/munkhuu/Desktop/Graphs/nepusz_9_14.gml"
      fstream = new FileInputStream(GMLpath);
      in = new DataInputStream(fstream);
      //br = new BufferedReader(new InputStreamReader(in));
      String strLine;
      while ((strLine =in.readLine()) != null)
      {
       if(strLine.contains("node") && (strLine = in.readLine()).contains("["))
       {
                            nodeId = "";
                            nodeLabel = "";
                            nodeValue = "";
                            while (!strLine.contains("]"))
                             {
                                strLine = in.readLine();

                                if (strLine.contains("id")) {
                                    nodeId = strLine.substring(strLine.indexOf("id") + 3);
                                     if(Integer.parseInt(nodeId)==0 && counter == 0)
                                       edgeBegin = 0;
                                     else if(Integer.parseInt(nodeId)==1 && counter == 0)
                                       edgeBegin = 1;
                                    //System.out.println(nodeId );

                                    strLine = in.readLine();
                                    counter++;
                                }
                                if (strLine.contains("label")) {
                                    nodeLabel = strLine.substring(strLine.indexOf("label") + 6);
                                    //System.out.println(nodeLabel );

                                    strLine = in.readLine();

                                }
                                if (strLine.contains("value")) {
                                    nodeValue = strLine.substring(strLine.indexOf("value") + 6);
                                    //System.out.println(nodeValue );
                                    strLine = in.readLine();
                                }
                           }
                       }
              } // while
       } // end of try
       catch(Exception e) {// Catch exception if any
                System.err.println("Error: " + e.getMessage());

       }
      n = counter;
      System.out.println("Number of Nodes = "+n);
    } // end of nodes

   public void Edges(String GMLpath) throws FileNotFoundException, IOException{
    SimulatedAnnealing.k = new int[n];
    SimulatedAnnealing.A = MatrixFactory.dense(n,n);
   int counter = 0;
   for(int i=0;i<n;i++)
    SimulatedAnnealing.k[i] = 0;
   try {
      fstream = new FileInputStream(GMLpath);
      in = new DataInputStream(fstream);
      String strLine;
      while ((strLine = in.readLine()) != null) {
                    // Print the content on the console
                    if (strLine.contains("edge") && (strLine = in.readLine()).contains("[")) {

                        edgeSource = "";
                        edgeTarget = "";
                        edgeValue  = "";

                        while (!strLine.contains("]")){
                            strLine = in.readLine();

                            if (strLine.contains("source")) {
                                edgeSource = strLine.substring(strLine.indexOf("source") + 7);


                            } else if (strLine.contains("target")) {
                                edgeTarget = strLine.substring(strLine.indexOf("target") + 7);
                                //System.out.println(Integer.parseInt(edgeSource));
                                //System.out.println(Integer.parseInt(edgeTarget));
                                counter++;
                                if(edgeBegin==0)
                                {
                                 SimulatedAnnealing.A.setAsDouble(1.0,Integer.parseInt(edgeSource),Integer.parseInt(edgeTarget));
                                 SimulatedAnnealing.A.setAsDouble(1.0,Integer.parseInt(edgeTarget),Integer.parseInt(edgeSource));

                                 SimulatedAnnealing.k[Integer.parseInt(edgeSource)]++;
                                 SimulatedAnnealing.k[Integer.parseInt(edgeTarget)]++;
                                }
                                else if(edgeBegin==1)
                                {
                                 SimulatedAnnealing.A.setAsDouble(1.0,Integer.parseInt(edgeSource)-1,Integer.parseInt(edgeTarget)-1);
                                 SimulatedAnnealing.A.setAsDouble(1.0,Integer.parseInt(edgeTarget)-1,Integer.parseInt(edgeSource)-1);

                                 SimulatedAnnealing.k[Integer.parseInt(edgeSource)-1]++;
                                 SimulatedAnnealing.k[Integer.parseInt(edgeTarget)-1]++;
                                }
                                 //System.out.println(Global.A);
                            } else if (strLine.contains("value")) {
                                edgeValue = strLine.substring(strLine.indexOf("value") + 6);
                            }

                           //System.out.println(Integer.parseInt(edgeSource));
                           //System.out.println(Integer.parseInt(edgeTarget));
                           //SimulatedAnnealing.A.setAsDouble(1.0,Integer.parseInt(edgeSource),Integer.parseInt(edgeTarget));
                        }
                    }
                }
       } // end of try
       catch(Exception e) {// Catch exception if any
                System.err.println("Error: " + e.getMessage());

       }
    m = counter;
    System.out.println("Number of Edge = "+counter);
   }//end of edges
   public void OpenGmlFile(String GMLpath) throws FileNotFoundException, IOException{
      this.Nodes(GMLpath);
      this.Edges(GMLpath);
      System.out.println("Edge begin with = "+edgeBegin);
      //System.out.println(SimulatedAnnealing.A);
      //SimulatedAnnealing.A.showGUI();
   }
   public void CloseGmlFile() throws IOException{
       try
       {
        in.close();
       }
       catch (Exception e){// Catch exception if any
                System.err.println("Error: " + e.getMessage());

       }
   }
}
