/**
 * @author Jesse Smart
 * SMRJES001
 */
public class Classifications {

    int[][][] classes;
    float xtotal = 0;
    float ytotal = 0;


    public void xadd(float in) {
        xtotal+=in;
    }

    public void yadd(float in) {
        ytotal+=in;
    }
}
