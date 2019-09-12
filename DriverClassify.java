import java.util.*;
import java.io.*;
import java.util.concurrent.ForkJoinPool;


/**
 * @author Jesse Smart
 * SMRJES001
 */
public class DriverClassify {

    public static int dimt, dimx, dimy;
    public static float [][][] convection; // vertical air movement strength, that evolves over time
    public static int [][][] classification; // cloud type per grid point, evolving over time


    public static void main(String[] args) {
        CloudData cloudData = new CloudData();
        CloudOutput result = new CloudOutput();
        String filename = "";
        String outputFile = "";
        if (args.length==0){
            cloudData.readData(filename);
            outputFile = "output.txt";
        }
        else{
            cloudData.readData(args[0]);
            outputFile = args[1];
        }

        dimy = cloudData.dimy;
        dimx = cloudData.dimx;
        dimt = cloudData.dimt;

        convection = cloudData.convection;
        classification = cloudData.classification;

        System.out.println("cloud data successfully read.");

        Vector[] vectorArray = cloudData.vectors; //array of wind vectors ready for operations (linear in time)

        System.out.println(cloudData.dimt + "  " + cloudData.dimx + "  " + cloudData.dimy);
        // System.out.println("last: "+vectorArray[5242879].boundaryClassification); //should be 8



        int [] cutoffs = {20000, 120000, 220000, 320000, 420000, 520000, 620000, 720000, 820000, 920000, 1020000, 1120000, 1220000, 1320000, 1420000, 1520000};
        for (int j = 0; j <2 ; j++) {
            double count=0;
            // System.gc();//minimize likelihood that garbage collector will run during execution
            for (int i = 0; i < 1; i++) {

                long currentTime = System.currentTimeMillis();
                result = sum(vectorArray, cutoffs[j]); //invokes forkJoinPool and all threads. Returns vector in form X-sum; Y-sum when finished
                long timeAfterRun = System.currentTimeMillis();

                long runTime = (timeAfterRun - currentTime);
                System.out.println("Paralell program executed in: " + runTime + "ms");

                if(i>3){ //take average of just last 7, first 3 runs used for cache warming
                    count+=runTime;
                }


            }
            System.out.println("\nAverage runtime = "+count/7.0+"\n"); //return this average runtime

        }








        System.out.println(vectorArray.length);
        System.out.println("X sum: " + result.V.x);
        System.out.println("Y sum: " + result.V.y);

        double x_av = result.V.x / (double) cloudData.dim();
        double y_av = result.V.y / (double) cloudData.dim();

        System.out.println(x_av);
        System.out.println(y_av);
        Vector Vwrite = new Vector((float)x_av, (float)y_av);
        result.CD.writeData(outputFile, Vwrite);  //DATA WRITTEN



    }
    static final ForkJoinPool fjPool = new ForkJoinPool();
    static CloudOutput sum(Vector [] vectorArray, int sequential_cutoff){
        return fjPool.invoke(new windforkjoin(0,vectorArray.length, vectorArray, dimt, dimx, dimy, convection, classification, sequential_cutoff));
    }

}
