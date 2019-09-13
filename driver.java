import java.util.concurrent.ForkJoinPool;

/**
 * @author Jesse Smart
 * SMRJES001
 */
public class driver {

    public static void main(String[] args) {
        CloudData cloudData = new CloudData();
        String outputFile;

        if (args.length==0){
            cloudData.readData("simplesample_input.txt");
            outputFile = "output.txt";
        }
        else{
            cloudData.readData(args[0]);
            outputFile = args[1];
        }


        System.out.println( Runtime.getRuntime().availableProcessors());
        ForkJoinPool pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());

        CloudThread cloudThread = new CloudThread(0, cloudData.advection.length, cloudData  );
        pool.invoke(cloudThread);

        float xav = cloudData.xtot /cloudData.dim();
        float yav = cloudData.ytot /cloudData.dim();

        windVector out = new windVector(xav, yav, 0);

        cloudData.writeData(outputFile, out);

    }




}
