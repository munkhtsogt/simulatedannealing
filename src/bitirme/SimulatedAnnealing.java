/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bitirme;


import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import org.ujmp.core.Matrix;
import org.ujmp.core.MatrixFactory;


/*-----------------------------------------------------------------*/
public class SimulatedAnnealing {
   public static double alpha ;
   public static double sigma ;  // Gaussian distribution
   private Matrix S        = MatrixFactory.dense(GmlGraph.n, GmlGraph.n); // Similarity matrix
   private Matrix nextS    = MatrixFactory.dense(GmlGraph.n, GmlGraph.n); // next Similarity matrix
   private Matrix bestS    = MatrixFactory.dense(GmlGraph.n, GmlGraph.n); // best Similarity matrix
   private Matrix W        = MatrixFactory.dense(GmlGraph.n, GmlGraph.n);  // weigth matrix
   private Matrix AS       = MatrixFactory.dense(GmlGraph.n, GmlGraph.n); // Actual similarity matrix
   private Matrix I        = MatrixFactory.eye(GmlGraph.n,GmlGraph.n);    // Identity matrix
   public  static Matrix A = MatrixFactory.dense(GmlGraph.n,GmlGraph.n); // Adjancency matrix
   public static int[] k   = new int[GmlGraph.n]; // k k(i) is the degree of vertex i (1 dugume giren ve cikan ayirt sayisi)
   private double[] mo     = new double[GmlGraph.n];  // modularity
   private double modValue;
   private Matrix pM       = MatrixFactory.dense(GmlGraph.n, GmlGraph.n, GmlGraph.n);
   public static double T;
   public static double epsilon;
   public static int First;
   public static int Second;
   public static int Cnumber;
    //--------------------------------------------------------------------------------
    // n sutun sayisi c satir sayisi
   public void SA(String outPath) throws IOException
   {
       FileWriter fstream = new FileWriter(outPath+"/Result.txt");
       PrintWriter print = new PrintWriter(fstream);
       print.println("Number of Nodes = "+GmlGraph.n);
       print.println("Number of edges = "+GmlGraph.m);
       print.println("****Column represents clusters****");
       //Create Weigth Matrix
       if(GmlGraph.n < 20)
         createWeightMatrix_1(W, GmlGraph.n);
       else
        createWeightMatrix_2(W, A, k, GmlGraph.m, GmlGraph.n);
      for(int c=2;c<= Cnumber;c++) // c topluluk sayisi
      {
       //int c = 2;
       Matrix U       = MatrixFactory.dense(c, GmlGraph.n);        // fuzzy partition matrix
       Matrix nextU   = MatrixFactory.dense(c, GmlGraph.n);        // next fuzzy partition matrix
       Matrix bestU   = MatrixFactory.dense(c, GmlGraph.n);        // best fuzzt partition matrix
       Matrix gV      = MatrixFactory.dense(c, GmlGraph.n);        // Gradian vector matrix
       Matrix nextgV  = MatrixFactory.dense(c, GmlGraph.n);
          //Start of Simulated Annealing
          Random generator = new Random();
          double prob;
          double delta;
          int i=0;
          double best = 1000000000.0;
           //First= 5;
           //Second=50;
          while(i < First)
          {
            //T = 400.0;
            //epsilon=0.0001;
            double To = Math.random()*T - 100.0;
            FuzzyPartitionMatrix(U, c);
            SimilarityMatrix(U, S, c);
            ActualSimilarityMatrix(AS, A);
            int success = 0;
            while(To> epsilon )
            {
             int j=0;
             while(j < Second)
             {
                double next,current;
                //current
                GradVectorof(S, U, gV, c);
                //current = this.getMaxGradVector(gV, c);
                current = CalcGoalFunc(U,S);
                //next
                //this.NextU(generator, U, nextU, c);
                nextPartitionOf(U, nextU, gV, c, sigma);
                Unormalize(nextU, c);
                SimilarityMatrix(nextU, nextS, c);
                GradVectorof(nextS, nextU, nextgV, c);
                //next = getMaxGradVector(nextgV, c);
                next = CalcGoalFunc(nextU,nextS);
                delta = next - current;
                if(delta<0)
                {
                    if(GmlGraph.n > 20)
                    {
                     success ++;
                     if(success % 3 ==0)
                      sigma = generateSuccessfullAlpha(sigma);
                    }
                    NextToCurrent(U, nextU, gV, nextgV, c);
                }
                else
                {
                  if(GmlGraph.n >20)
                  {
                   sigma = generateUnsuccessfullAlpha(sigma);
                   success = 0;
                  }
                  //prob shoud be [0,1]
                  prob=Math.random()*1.0-0.0;
                  if( prob<Math.exp((-delta)/To) )
                  {
                    NextToCurrent(U, nextU, gV, nextgV, c);
                  }
                }
                j++;
                if(current - best < 0)
                {
                  best = current;
                  TakeBest(bestU, U, c);
                }
             }
             To=To*alpha;
           }
          i++;
         }// end of Simulated Annealing
         print.println(bestU.transpose());
         //System.out.println(bestU);
         //bestU.showGUI();
         SimilarityMatrix(bestU, bestS, c);
         //System.out.println("OptGoalFunc="+this.CalcGoalFunc(bestU, bestS));
         //System.out.println("best MaxGradValue = "+best);
         modValue = fuzzifiedModularity(bestS, k, GmlGraph.m, GmlGraph.n);
         //System.out.println("Fuzzified Modularity = "+modValue);
         print.println("Fuzzified Modularity = "+modValue);
         mo[c] = modValue;
         for(int i1=0;i1<c;i1++)
         {
          for(int j=0;j<GmlGraph.n;j++)
          {
            pM.setAsDouble((bestU.getAsDouble(i1,j)),i1,j,c);
          }
         }
     } // end for
     //print last
     int index;
     index = getIndexOfMaxModularity(mo);
     //System.out.println("index = "+index);
     Matrix Result = MatrixFactory.dense(index,GmlGraph.n);
     Result.setLabel("Choosen PM:");
     for(int i1=0;i1<index;i1++)
      for(int j=0;j<GmlGraph.n;j++)
      {
        double pM_i_j = pM.getAsDouble(i1,j,index);
        Result.setAsDouble(pM_i_j, i1, j);
      }
      System.out.println(Result);
      System.out.println("Choosen cluster number = " + index);
      System.out.println("Maximum Fuzzified Modularity = " + mo[index]);
      print.println("************************************************************************");
      print.println("Choosen cluster number = " + index);
      print.println(Result.transpose());
      print.println("Maximum Fuzzified Modularity = " + mo[index]);
      print.println("End!!!");
      print.close();
    } // end of SA()
    public void FuzzyPartitionMatrix(Matrix U, int c){
        U.setLabel("U");
        double range, value, value_2;
        for(int i=0;i<GmlGraph.n;i++)
        {
          int t=1;
          value = Math.random()*1.0-0.0;
          U.setAsDouble(value,0,i);
          range = 1 - value;
           for(int j=1;j<c;j++)
           {
            if(t==c-1)
            {
              U.setAsDouble(range,j,i);
            }
            else
            {
             value_2 = Math.random()*range-0;
             U.setAsDouble(value_2,j,i);
             range = range - value_2;
             t++;
            }
          }
        }
      } // end of FuzzyPartitionMatrix

      public void SimilarityMatrix(Matrix U,Matrix S,int c){
         for (int i = 0; i < GmlGraph.n; i++)
         {
            for (int j = 0; j < GmlGraph.n; j++)
            {
                double S_i_j = 0.0;
                for (int k1 = 0; k1< c; k1++)
                {
                    double U_k_i = U.getAsDouble(k1,i);
                    double U_k_j = U.getAsDouble(k1,j);
                    S_i_j += (U_k_i * U_k_j);
                }
                S.setAsDouble(S_i_j, i, j);
            }
        }
         //System.out.println(S);
      } // end of Similarity Matrix
  public void ActualSimilarityMatrix(Matrix AS, Matrix A){
     //AS=A+I
     double AS_i_j;
     for(int i=0;i<GmlGraph.n;i++)
      for(int j=0;j<GmlGraph.n;j++)
      {
       AS_i_j = A.getAsDouble(i,j) + I.getAsDouble(i,j);
       AS.setAsDouble(AS_i_j, i, j);
      }

  }// end of ActualSimilarityMatrix
 public double CalcGoalFunc(Matrix U,Matrix S){
  double sum=0.0;
  double Diff_i_j, W_i_j;
  for(int i=0;i<GmlGraph.n;i++)
  {
    for(int j=0;j<GmlGraph.n;j++)
    {
     W_i_j = W.getAsDouble(i,j);
     Diff_i_j = AS.getAsDouble(i,j)-S.getAsDouble(i,j);
     sum = sum + W_i_j*Diff_i_j*Diff_i_j;
    }
  }
  return sum;
 } // end of Goalfunction
 public void GradVectorof(Matrix S,Matrix U,Matrix gV ,int c){
   // Opt.algo icin kullaniyor
   double e1,e2,ratio,result;
   double W_i_l, W_l_i, U_j_i, D_i_l, D_l_i;
   for(int j=0;j<c;j++)
   {
     for(int l=0;l<GmlGraph.n;l++)
     {
       double element = 0.0;
       for(int i=0;i<GmlGraph.n;i++)
       {
         W_i_l = W.getAsDouble(i,l);
         W_l_i = W.getAsDouble(l,i);
         D_i_l = AS.getAsDouble(i,l) - S.getAsDouble(i,l);
         D_l_i = AS.getAsDouble(l,i) - S.getAsDouble(l,i);
         e1 = W_i_l * D_i_l;
         e2 = W_l_i * D_l_i;
         ratio = (double)(1.0/c);
         U_j_i = U.getAsDouble(j,i);
         result = (e1 + e2) * (ratio - U_j_i);
         element = element + result;
       }
       element = element * 2.0;
       gV.setAsDouble(element,j,l);
     }
   }
 }  // end of GradVector
 public double getMaxGradVector(Matrix gV,int c){
    // opt.algo icin kullaniyor
    double maxvalue;
    maxvalue = Math.abs(gV.getAsDouble(0,0));
    for(int j=0;j<c;j++)
    {
      for(int l=0;l<GmlGraph.n;l++)
      {
        if(Math.abs(gV.getAsDouble(j,l)) > maxvalue)
        {
          double gV_j_l = gV.getAsDouble(j,l);
          maxvalue = Math.abs(gV_j_l);
        }
      }
    }
    return maxvalue;
  }// end of getMaxGradVector
 public void createWeightMatrix_1(Matrix W,int n){
     for(int i=0; i<n; i++)
       for(int j=0;j<n; j++)
         W.setAsDouble(1.0, i, j);
 }
 public void createWeightMatrix_2(Matrix W, Matrix A, int k[], int m, int n)
 {
        double element, observed, expected;
        // float[][] W = new float[n][n]; // weight matrix
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                // observed = A[i][j];
                // expected = (float) (k[i] * k[j]) / (2 * m);

                observed = A.getAsDouble(i,j);
                expected = (double) (k[i] * k[j]) / (2 * m);

                element = (double) Math.pow((observed - expected), 2);

                // W[i][j] = element;
                W.setAsDouble(element, i, j);

            }
        }
        // return W;
    }
 public void NextU(Random generator, Matrix U,Matrix nextU,int c){
   //Random generator = new Random();
   nextU.setLabel("nextU");
//   for(int i=0;i<c;i++)
//     for(int j=0;j<GmlGraph.n;j++)
//        nextU.setAsDouble((U.getAsDouble(i,j)),i,j);
   //nextU = U;
   double change, U_0_i, U_j_i;
   for(int i=0;i<GmlGraph.n;i++)
   {    
    change = generator.nextGaussian()*sigma;
    U_0_i = U.getAsDouble(0,i) + change;
    nextU.setAsDouble(U_0_i, 0, i);
    for(int j=1;j<c;j++)
    {
       U_j_i = U.getAsDouble(j,i);
       double observed = U_j_i - change/(c-1);
       nextU.setAsDouble(observed,j,i);
    }
   }
  } // end of nextu
  public void nextPartitionOf(Matrix U, Matrix nextU, Matrix gV, int c, double alfa){
    double next_U_i_j, U_i_j, gradientVector_i_j;
        for (int i = 0; i < c; i++) {
            for (int j = 0; j < GmlGraph.n; j++) {
                // next_U[i][j] = U[i][j] - alpha * gradientVector[i][j];

                U_i_j = U.getAsDouble(i,j);
                gradientVector_i_j = gV.getAsDouble(i,j);

                next_U_i_j = U_i_j - alfa * gradientVector_i_j;
                nextU.setAsDouble(next_U_i_j,i,j);
            }
        }
  }
  public void Unormalize(Matrix U,int c ){
    double ColumnSum;    
    for (int i = 0; i < GmlGraph.n; i++)
    {
            ColumnSum = 0.0;
            for (int j = 0; j < c; j++)
            {
               double U_j_i = U.getAsDouble(j,i);
                if (U_j_i < 0)
                {
                    U_j_i = 0.0;
                    U.setAsDouble(U_j_i,j,i);
                }
                else if(U_j_i > 1){}
                ColumnSum += U_j_i;
            }
            // U matrisinde bir düğümün üyelik derecelerinin toplamı
            // [1-tolerans, 1+tolerans] aralığında olması
            if (ColumnSum > (1 + 0.0001) || ColumnSum < (1 - 0.0001))
            {
                for (int j = 0; j < c; j++)
                {
                    double U_j_i = U.getAsDouble(j,i);
                    double new_U_j_i = (double) (U_j_i * (1.0 / ColumnSum));
                    U.setAsDouble(new_U_j_i, j, i);
                }
            }
        }
  }
  public double fuzzifiedModularity(Matrix S,int k[], int m, int n ){
     double Q_f=0.0;
     double element, observed, expected;
     double A_i_j, S_i_j;
     for(int i=0;i<n;i++)
     {
       for(int j=0;j<n;j++)
       {
                A_i_j =  A.getAsDouble(i,j);
                S_i_j =  S.getAsDouble(i,j);

                observed = A_i_j;
                expected = (double) (k[i] * k[j]) / (2 * m);

                element = (observed - expected) * S_i_j;
                Q_f = Q_f + element;
       }
     }
     Q_f = Q_f / (2 * m);
     return Q_f;
  }
  public int getIndexOfMaxModularity(double mo[])
  {
       double tmp=mo[2];
       int index=2;
       for(int i=3;i<GmlGraph.n;i++)
       {
         if(tmp<mo[i])
         {
          tmp = mo[i];
          index=i;
         }
       }
       return index;
  }
   public void NextToCurrent(Matrix U,Matrix nextU, Matrix gV,Matrix nextgV,int c){
     //        U = nextU;
     //        S = nextS;
     //        gV = nextgV;
     for(int i=0;i<c;i++)
       for(int j=0;j<GmlGraph.n;j++)
       {
        double nextU_i_j = nextU.getAsDouble(i,j);
        U.setAsDouble(nextU_i_j, i, j);
       }
     for(int i=0;i<GmlGraph.n;i++)
       for(int j=0;j<GmlGraph.n;j++)
       {
        double nextS_i_j = nextS.getAsDouble(i,j);
        S.setAsDouble(nextS_i_j, i, j);
       }
     for(int i=0;i<c;i++)
       for(int j=0;j<GmlGraph.n;j++)
       {
        double nextgV_i_j = nextgV.getAsDouble(i,j);
        gV.setAsDouble(nextgV_i_j, i, j);
       }

  }
 public void TakeBest(Matrix bestU, Matrix U, int c){
     //      bestU = U;
     bestU.setLabel("bestU");
      for(int i=0;i<c;i++)
       for(int j=0;j<GmlGraph.n;j++)
       {
        double U_i_j = U.getAsDouble(i,j);
        bestU.setAsDouble(U_i_j, i, j);
       }
 }
 public double generateSuccessfullAlpha(double alpha){
   return 1.5 * alpha;
 }
 public double generateUnsuccessfullAlpha(double alpha){
   return 0.5 * alpha;
 }
}
