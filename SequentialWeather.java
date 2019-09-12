/**
 * @author Jesse Smart
 * SMRJES001
 */
public class SequentialWeather {

    int[] timeSize;
    float[] windLift;
    int steps;
    int xsize;
    int ysize;


    public SequentialWeather( int[] arr1, float[] arr2){
        timeSize = arr1;
        windLift = arr2;
        steps = arr1[0];
        xsize = arr1[1];
        ysize = arr1[2];
    }

    public float[] windAv(){
        float xOut = 0;
        float yOut = 0;
        int entries = xsize*ysize;
        for(int i = 0; i < entries ; i++){
            xOut += windLift[i*3];
            yOut += windLift[i*3 +1];
        }
        xOut = xOut / entries;
        yOut = yOut / entries;
        float[] out = new float[]{xOut,yOut};
        return out;
    }


}
