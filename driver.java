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
        System.out.println(cloudData.advection[1].x);
        System.out.println(cloudData.advection[1].y);
        System.out.println(cloudData.advection[1].convection);

        ForkJoinPool pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
        CloudThread cloudThread = new CloudThread(0, cloudData.advection.length, cloudData  );
        pool.invoke(cloudThread);


        windVector out = new windVector(cloudData.xtot, cloudData.ytot, 0);

        cloudData.writeData(outputFile, out);


        //methodcall
    }



}
