import java.util.concurrent.RecursiveTask;

/**
 * @author Jesse Smart
 * SMRJES001
 */
public class windforkjoin extends RecursiveTask<CloudOutput> {

    static int dimx,dimy,dimt;
    static float[][][] convection;
    static int[][][] classification;

    private static int cutoff;
    int high, low;

    Vector[] vectors;
    CloudOutput outobject = new CloudOutput(new CloudData(dimx, dimy, dimt, convection, classification), new Vector());


    windforkjoin(){}
    windforkjoin(int low, int high, Vector [] vecArray, int t, int x, int y, float [][][] connvection, int [][][] classification, int cutoff){
        this.low = low;
        this.high = high;
        this.vectors = vecArray;
        this.dimx = x;
        this.dimt = t;
        this.dimy = y;
        this.convection = connvection;
        this.classification = classification;
        this.cutoff = cutoff;
    }


  //  @Override
    protected CloudOutput compute() {

        if (high -  low < cutoff){
            for (int i = low; i < high; i++){
                outobject.V.x += vectors[i].x;
                outobject.V.y += vectors[i].y;
                try {
                    CloudClassifier(outobject.CD, i, vectors);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return outobject;
        }
        else {
            int mid = low + high /2;

            windforkjoin left = new windforkjoin(low, mid,vectors, dimt, dimx, dimy, convection, classification, cutoff);
            windforkjoin right = new windforkjoin(mid, high, vectors, dimt, dimx, dimy, convection, classification, cutoff);
            left.fork();

            CloudOutput resultR = right.compute();
            CloudOutput resultL = left.join();

            Vector last = resultL.V.add(resultR.V);

            resultL.V = last;
            return resultL;
        }
    }

    public void CloudClassifier(CloudData cloudData, int i, Vector[] vectors) throws Exception{
        int [] ind = new int[3];
        cloudData.locate(i,ind);

        String bound = vectors[i].boundaryclass;
        float convection = cloudData.convection[ind[0]][ind[1]][ind[2]];

        double X = 0.0;
        double Y = 0.0;
        
        if (bound.equals("TL")){
            X = (vectors[i].x + vectors[i + 1].x + vectors[i+cloudData.dimy].x + vectors[i+cloudData.dimy + 1].x)/4.0;
            Y = (vectors[i].y + vectors[i + 1].y + vectors[i+cloudData.dimy].y + vectors[i+cloudData.dimy + 1].y)/4.0;
        }
        else if (bound.equals("TR")){
            X= (vectors[i].x + vectors[i - 1].x + vectors[i+cloudData.dimy].x + vectors[i+cloudData.dimy - 1].x)/4.0;
            Y = (vectors[i].y + vectors[i - 1].y + vectors[i+cloudData.dimy].y + vectors[i+cloudData.dimy - 1].y)/4.0;
        }
        else if (bound.equals("BL")){
            X  = (vectors[i].x + vectors[i + 1].x + vectors[i-cloudData.dimy].x + vectors[i-cloudData.dimy + 1].x)/4.0;
            Y = (vectors[i].y + vectors[i + 1].y + vectors[i-cloudData.dimy].y + vectors[i-cloudData.dimy + 1].y)/4.0;
        }
        else if (bound.equals("BR")){
            X = (vectors[i].x + vectors[i - 1].x + vectors[i-cloudData.dimy].x + vectors[i-cloudData.dimy - 1].x)/4.0;
            Y = (vectors[i].y + vectors[i - 1].y + vectors[i-cloudData.dimy].y + vectors[i-cloudData.dimy - 1].y)/4.0;
        }
        else if (bound.equals("L")){
            X= (vectors[i].x + vectors[i + 1].x + vectors[i+cloudData.dimy].x+ vectors[i+cloudData.dimy+1].x
                    + vectors[i-cloudData.dimy].x + vectors[i-cloudData.dimy+1].x)/6.0;
            Y = (vectors[i].y + vectors[i + 1].y + vectors[i+cloudData.dimy].y+ vectors[i+cloudData.dimy+1].y
                    + vectors[i-cloudData.dimy].y + vectors[i-cloudData.dimy+1].y)/6.0;
        }
        else if (bound.equals("T")){
            X = (vectors[i].x + vectors[i + 1].x + vectors[i-1].x+ vectors[i+cloudData.dimy].x
                    + vectors[i+cloudData.dimy - 1].x + vectors[i+cloudData.dimy+1].x)/6.0;
            Y = (vectors[i].y + vectors[i + 1].y + vectors[i-1].y+ vectors[i+cloudData.dimy].y
                    + vectors[i+cloudData.dimy - 1].y + vectors[i+cloudData.dimy+1].y)/6.0;
        }
        else if (bound.equals("R")){
            X = (vectors[i].x + vectors[i - 1].x + vectors[i+cloudData.dimy].x+ vectors[i+cloudData.dimy-1].x
                    + vectors[i-cloudData.dimy].x + vectors[i-cloudData.dimy-1].x)/6.0;
            Y = (vectors[i].y + vectors[i - 1].y + vectors[i+cloudData.dimy].y+ vectors[i+cloudData.dimy-1].y
                    + vectors[i-cloudData.dimy].y + vectors[i-cloudData.dimy-1].y)/6.0;
        }
        else if (bound.equals("B")){
            X = (vectors[i].x + vectors[i + 1].x + vectors[i-1].x+ vectors[i-cloudData.dimy].x
                    + vectors[i-cloudData.dimy-1].x + vectors[i-cloudData.dimy+1].x)/6.0;
            Y = (vectors[i].y + vectors[i + 1].y + vectors[i-1].y+ vectors[i-cloudData.dimy].y
                    + vectors[i-cloudData.dimy-1].y + vectors[i-cloudData.dimy+1].y)/6.0;
        }
        else if (bound.equals("M")){
            X = (vectors[i].x + vectors[i + 1].x + vectors[i-1].x+ vectors[i-cloudData.dimy].x
                    + vectors[i-cloudData.dimy-1].x + vectors[i-cloudData.dimy+1].x
                    +vectors[i+cloudData.dimy].x+vectors[i+cloudData.dimy-1].x+vectors[i+cloudData.dimy+1].x)/9.0;
            Y = (vectors[i].y + vectors[i + 1].y + vectors[i-1].y+ vectors[i-cloudData.dimy].y
                    + vectors[i-cloudData.dimy-1].y + vectors[i-cloudData.dimy+1].y
                    +vectors[i+cloudData.dimy].y+vectors[i+cloudData.dimy-1].y+vectors[i+cloudData.dimy+1].y)/9.0;
        }
        
        double len = Math.sqrt((X * X + Y * Y));

        classification[ind[0]][ind[1]][ind[2]] = 2;

        if (len > 0.2 && (float)len >= Math.abs(convection)){
            classification[ind[0]][ind[1]][ind[2]] = 1;
        }

        else if((float)len < Math.abs(convection) ){
            classification[ind[0]][ind[1]][ind[2]] = 0;
        }

    }

}
