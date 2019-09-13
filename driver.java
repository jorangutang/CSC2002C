import java.util.concurrent.ForkJoinPool;

/**
 * @author Jesse Smart
 * SMRJES001
 * this class is the 'main' class of the project
 *
 */
public class driver {

    /**
     * main method creates objects, calls for data reading, initializes pool framework and does time tracking
     *
     * @param args
     */
    public static void main(String[] args) {
        CloudData cloudData = new CloudData();
        String outputFile;

        if (args.length==0){
            cloudData.readData("largesample_input.txt");
            outputFile = "output2.txt";
        }
        else{
            cloudData.readData(args[0]);
            outputFile = args[1];
        }


        int cores = Runtime.getRuntime().availableProcessors() ;
        ForkJoinPool pool = new ForkJoinPool(cores);
        CloudThread cloudThread = new CloudThread(0, cloudData.advection.length, cloudData  );

        long time1 = System.currentTimeMillis();
        pool.invoke(cloudThread);
        long time2 = System.currentTimeMillis();

        long runtime = time2 - time1;

        System.out.println("Parallel problem solved on " + cores + " cores took " + runtime + " milliseconds");

        float xav = cloudData.xtot /cloudData.dim();
        float yav = cloudData.ytot /cloudData.dim();

        windVector out = new windVector(xav, yav, 0);

        cloudData.writeData(outputFile, out);

    }




}
