import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;

/**
 * @author Jesse Smart
 * SMRJES001
 */
public class CloudThread extends RecursiveAction {

    private static int seqcutoff = 5;
    int low, high;
    CloudData indata;
    int xtot;
    int ytot;

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
            action1.xtot = xtot;
            CloudThread action2 = new CloudThread(mid, high, indata);
            action2.ytot = ytot;
            action1.fork();
            action2.fork();

        } else {
            for (int i = low; i < high; i++) {
                indata.xtot += indata.advection[i].x;
                xtot += indata.advection[i].x;
                indata.ytot += indata.advection[i].y;
                ytot += indata.advection[i].y;
                classify(indata.advection[i], i);
            }
        }
    }


    public void classify( windVector wind, int index){

        int [] coords = new int[3];
        indata.locate(index,coords); //converts a linear index into a 3D location in the grid. Use this 3D location to
        //access the correct position in the classification and convection arrays
        float convection = wind.convection;
        int boundaryClassification = wind.boundclass;
        windVector[] vectorArray = indata.advection;

        windVector localVector = new windVector();

        double x_av = 0.0;
        double y_av = 0.0;



        if (boundaryClassification==5){ //topLeft
            x_av = (vectorArray[index].x + vectorArray[index+1].x + vectorArray[index+indata.dimy].x + vectorArray[index+indata.dimy +1].x)/4.0;
            y_av = (vectorArray[index].y + vectorArray[index+1].y + vectorArray[index+indata.dimy].y + vectorArray[index+indata.dimy +1].y)/4.0;

        }
        else if(boundaryClassification==6){//topRight

            x_av= (vectorArray[index].x + vectorArray[index-1].x + vectorArray[index+indata.dimy].x + vectorArray[index+indata.dimy-1].x)/4.0;
            y_av = (vectorArray[index].y + vectorArray[index-1].y + vectorArray[index+indata.dimy].y + vectorArray[index+indata.dimy-1].y)/4.0;

        }
        else if (boundaryClassification==7){ //bottomLeft
            x_av  = (vectorArray[index].x + vectorArray[index+1].x + vectorArray[index-indata.dimy].x + vectorArray[index-indata.dimy+1].x)/4.0;
            y_av = (vectorArray[index].y + vectorArray[index+1].y + vectorArray[index-indata.dimy].y + vectorArray[index-indata.dimy+1].y)/4.0;

        }else if(boundaryClassification==8){ //bottomRight

            x_av = (vectorArray[index].x + vectorArray[index-1].x + vectorArray[index-indata.dimy].x + vectorArray[index-indata.dimy-1].x)/4.0;
            y_av = (vectorArray[index].y + vectorArray[index-1].y + vectorArray[index-indata.dimy].y + vectorArray[index-indata.dimy-1].y)/4.0;

        }else if(boundaryClassification==1){ //Left boundary

            x_av= (vectorArray[index].x + vectorArray[index+1].x + vectorArray[index+indata.dimy].x+ vectorArray[index+indata.dimy+1].x
                    + vectorArray[index-indata.dimy].x + vectorArray[index-indata.dimy+1].x)/6.0;
            y_av = (vectorArray[index].y + vectorArray[index+1].y + vectorArray[index+indata.dimy].y+ vectorArray[index+indata.dimy+1].y
                    + vectorArray[index-indata.dimy].y + vectorArray[index-indata.dimy+1].y)/6.0;


        }
        else if(boundaryClassification==2) { //top boundary

            x_av = (vectorArray[index].x + vectorArray[index+1].x + vectorArray[index-1].x+ vectorArray[index+indata.dimy].x
                    + vectorArray[index+indata.dimy-1].x + vectorArray[index+indata.dimy+1].x)/6.0;

            y_av = (vectorArray[index].y + vectorArray[index+1].y + vectorArray[index-1].y+ vectorArray[index+indata.dimy].y
                    + vectorArray[index+indata.dimy-1].y + vectorArray[index+indata.dimy+1].y)/6.0;

        }
        else if (boundaryClassification == 3) { //right boundary

            x_av = (vectorArray[index].x + vectorArray[index-1].x + vectorArray[index+indata.dimy].x+ vectorArray[index+indata.dimy-1].x
                    + vectorArray[index-indata.dimy].x + vectorArray[index-indata.dimy-1].x)/6.0;
            y_av = (vectorArray[index].y + vectorArray[index-1].y + vectorArray[index+indata.dimy].y+ vectorArray[index+indata.dimy-1].y
                    + vectorArray[index-indata.dimy].y + vectorArray[index-indata.dimy-1].y)/6.0;


        } else if(boundaryClassification==4){//bottom boundary

            x_av = (vectorArray[index].x + vectorArray[index+1].x + vectorArray[index-1].x+ vectorArray[index-indata.dimy].x
                    + vectorArray[index-indata.dimy-1].x + vectorArray[index-indata.dimy+1].x)/6.0;

            y_av = (vectorArray[index].y + vectorArray[index+1].y + vectorArray[index-1].y+ vectorArray[index-indata.dimy].y
                    + vectorArray[index-indata.dimy-1].y + vectorArray[index-indata.dimy+1].y)/6.0;

        } else{ //normal - vector doesn't lie on any boundary.

            x_av = (vectorArray[index].x + vectorArray[index+1].x + vectorArray[index-1].x+ vectorArray[index-indata.dimy].x
                    + vectorArray[index-indata.dimy-1].x + vectorArray[index-indata.dimy+1].x
                    +vectorArray[index+indata.dimy].x+vectorArray[index+indata.dimy-1].x+vectorArray[index+indata.dimy+1].x)/9.0;
            y_av = (vectorArray[index].y + vectorArray[index+1].y + vectorArray[index-1].y+ vectorArray[index-indata.dimy].y
                    + vectorArray[index-indata.dimy-1].y + vectorArray[index-indata.dimy+1].y
                    +vectorArray[index+indata.dimy].y+vectorArray[index+indata.dimy-1].y+vectorArray[index+indata.dimy+1].y)/9.0;


        }

        double lenLocalAverage = Math.sqrt((x_av*x_av)+(y_av*y_av));

        indata.classification[coords[0]][coords[1]][coords[2]] = 2;

        if(lenLocalAverage>0.2 && (float)lenLocalAverage>=Math.abs(convection)){
            indata.classification[coords[0]][coords[1]][coords[2]] = 1;

        }
        else if (Math.abs(convection)>(float)lenLocalAverage){

            indata.classification[coords[0]][coords[1]][coords[2]] = 0;
        }


    }

}

