/**
 * @author Jesse Smart
 * SMRJES001
 */
public class Vector {
    
    float x;
    float y;
    String boundaryclass;

    public Vector(){}

    public Vector(float x, float y){
        this.x =x;
        this.y =y;
    }

    public Vector add (Vector other){
        return new Vector(x+other.x, y+other.y);
    }
}
