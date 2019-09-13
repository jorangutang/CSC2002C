/**
 * @author Jesse Smart
 * SMRJES001
 */
public class windVector {

    float x;
    float y;
    float localx;
    float localy;
    float convection;
    int boundclass;

    windVector(){}
    windVector(float x, float y, float convection){
        this.x = x;
        this.y = y;
        this.convection = convection;
    }

}
