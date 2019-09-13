import java.util.concurrent.RecursiveAction;

/**
 * @author Jesse Smart
 * SMRJES001
 */
public class CloudThread extends RecursiveAction {

    private static int seqcutoff = 1;
    int low, high;
    CloudData indata;

    public CloudThread(int low, int high, CloudData input) {
        this.low = low;
        this.high = high;
        indata = input;
    }

    @Override
    protected void compute() {
        if ((high - low) > seqcutoff) {
            int mid = (high + low) / 2;
            CloudThread action1 = new CloudThread(low, mid, indata);
            CloudThread action2 = new CloudThread(mid, high, indata);
            action1.fork();
            action2.fork();
            action1.join();
            action2.join();

        } else {
            for (int i = low; i < high; i++) {
                indata.xtot += indata.advection[i].x;
                indata.ytot += indata.advection[i].y;
                classification(indata.advection[i], i);
            }
        }
    }


    public void classification (windVector wind, int index){

        int [] ind = new int[3];
        indata.locate(index,ind); 
        float convection = wind.convection;
        int boundclass = wind.boundclass;
        windVector[] vectors = indata.advection;

        double localX = 0.0;
        double localY = 0.0;



        if (boundclass==5){
            localY = (vectors[index].y + vectors[index+1].y + vectors[index+indata.dimy].y + vectors[index+indata.dimy +1].y)/4.0;
            localX = (vectors[index].x + vectors[index+1].x + vectors[index+indata.dimy].x + vectors[index+indata.dimy +1].x)/4.0;

        }
        else if(boundclass==6){

            localY = (vectors[index].y + vectors[index-1].y + vectors[index+indata.dimy].y + vectors[index+indata.dimy-1].y)/4.0;
            localX= (vectors[index].x + vectors[index-1].x + vectors[index+indata.dimy].x + vectors[index+indata.dimy-1].x)/4.0;

        }
        else if (boundclass==7){
            localY = (vectors[index].y + vectors[index+1].y + vectors[index-indata.dimy].y + vectors[index-indata.dimy+1].y)/4.0;
            localX  = (vectors[index].x + vectors[index+1].x + vectors[index-indata.dimy].x + vectors[index-indata.dimy+1].x)/4.0;

        }else if(boundclass==8){

            localY = (vectors[index].y + vectors[index-1].y + vectors[index-indata.dimy].y + vectors[index-indata.dimy-1].y)/4.0;
            localX = (vectors[index].x + vectors[index-1].x + vectors[index-indata.dimy].x + vectors[index-indata.dimy-1].x)/4.0;

        }else if(boundclass==1){

            localY = (vectors[index].y + vectors[index+1].y + vectors[index+indata.dimy].y+ vectors[index+indata.dimy+1].y
                    + vectors[index-indata.dimy].y + vectors[index-indata.dimy+1].y)/6.0;
            localX= (vectors[index].x + vectors[index+1].x + vectors[index+indata.dimy].x+ vectors[index+indata.dimy+1].x
                    + vectors[index-indata.dimy].x + vectors[index-indata.dimy+1].x)/6.0;


        }
        else if(boundclass==2) {

            localY = (vectors[index].y + vectors[index+1].y + vectors[index-1].y+ vectors[index+indata.dimy].y
                    + vectors[index+indata.dimy-1].y + vectors[index+indata.dimy+1].y)/6.0;

            localX = (vectors[index].x + vectors[index+1].x + vectors[index-1].x+ vectors[index+indata.dimy].x
                    + vectors[index+indata.dimy-1].x + vectors[index+indata.dimy+1].x)/6.0;

        }
        else if (boundclass == 3) {

            localY = (vectors[index].y + vectors[index-1].y + vectors[index+indata.dimy].y+ vectors[index+indata.dimy-1].y
                    + vectors[index-indata.dimy].y + vectors[index-indata.dimy-1].y)/6.0;

            localX = (vectors[index].x + vectors[index-1].x + vectors[index+indata.dimy].x+ vectors[index+indata.dimy-1].x
                    + vectors[index-indata.dimy].x + vectors[index-indata.dimy-1].x)/6.0;

        } else if(boundclass==4){

            localY = (vectors[index].y + vectors[index+1].y + vectors[index-1].y+ vectors[index-indata.dimy].y
                    + vectors[index-indata.dimy-1].y + vectors[index-indata.dimy+1].y)/6.0;

            localX = (vectors[index].x + vectors[index+1].x + vectors[index-1].x+ vectors[index-indata.dimy].x
                    + vectors[index-indata.dimy-1].x + vectors[index-indata.dimy+1].x)/6.0;

        } else{

            localY = (vectors[index].y + vectors[index+1].y + vectors[index-1].y+ vectors[index-indata.dimy].y
                    + vectors[index-indata.dimy-1].y + vectors[index-indata.dimy+1].y
                    +vectors[index+indata.dimy].y+vectors[index+indata.dimy-1].y+vectors[index+indata.dimy+1].y)/9.0;

            localX = (vectors[index].x + vectors[index+1].x + vectors[index-1].x+ vectors[index-indata.dimy].x
                    + vectors[index-indata.dimy-1].x + vectors[index-indata.dimy+1].x
                    +vectors[index+indata.dimy].x+vectors[index+indata.dimy-1].x+vectors[index+indata.dimy+1].x)/9.0;

        }

        double length1 = Math.sqrt((localX*localX)+(localY*localY));

        indata.classification[ind[0]][ind[1]][ind[2]] = 2;

        if(length1>0.2 && (float)length1>=Math.abs(convection)){
            indata.classification[ind[0]][ind[1]][ind[2]] = 1;

        }
        else if (Math.abs(convection)>(float)length1){

            indata.classification[ind[0]][ind[1]][ind[2]] = 0;
        }


    }

}

