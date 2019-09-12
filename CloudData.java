import java.io.File;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.PrintWriter;
//import java.util.Vector;

public class CloudData {

    public Vector[][][] advection; // in-plane regular grid of wind vectors, that evolve over time
    float[][][] convection; // vertical air movement strength, that evolves over time
    int[][][] classification; // cloud type per grid point, evolving over time
    int dimx, dimy, dimt; // data dimensions
    public Vector[] vectors;

    CloudData() {}

    CloudData(int x, int y, int t, float[][][] convection, int[][][] classification) {
        this.dimt = t;
        this.dimx = x;
        this.dimy = y;
        this.convection = convection;
        this.classification = classification;
    }

    // overall number of elements in the timeline grids
    int dim() {
        return dimt * dimx * dimy;
    }

    // convert linear position into 3D location in simulation grid
    void locate(int pos, int[] ind) {
        System.out.println(dimt + " " + dimx + " " + dimy);
        ind[0] = (int) pos / (dimx * dimy); // t
        ind[1] = (pos % (dimx * dimy)) / dimy; // x
        ind[2] = pos % (dimy); // y
    }

    // read cloud simulation data from file
    void readData(String fileName) {
        try {
            Scanner sc = new Scanner(new File(fileName), "UTF-8");

            // input grid dimensions and simulation duration in timesteps
            dimt = sc.nextInt();
            System.out.println(dimt);
            dimx = sc.nextInt();
            System.out.println(dimx);
            dimy = sc.nextInt();
            System.out.println(dimy);

            vectors = new Vector[dim()];


            // initialize and load advection (wind direction and strength) and convection
            advection = new Vector[dimt][dimx][dimy];
            convection = new float[dimt][dimx][dimy];
            int pos = 0;
            for (int t = 0; t < dimt; t++)
                for (int x = 0; x < dimx; x++)
                    for (int y = 0; y < dimy; y++) {
                        advection[t][x][y] = new Vector();
                        advection[t][x][y].x = sc.nextFloat();
                        advection[t][x][y].y = sc.nextFloat();
                        vectors[pos] = advection[t][x][y];
                        convection[t][x][y] = sc.nextFloat();
                        classifier(pos  ,vectors[pos]);

                        pos++;
                    }

            classification = new int[dimt][dimx][dimy];
            sc.close();
        } catch (IOException e) {
            System.out.println("Unable to open input file " + fileName);
            e.printStackTrace();
        } catch (InputMismatchException e) {
            System.out.println("Malformed input file " + fileName);
            e.printStackTrace();
        }
    }

    // write classification output to file
    void writeData(String fileName, Vector wind) {
        try {
            FileWriter fileWriter = new FileWriter(fileName);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.printf("%d %d %d\n", dimt, dimx, dimy);
            printWriter.printf("%f %f\n", wind.x, wind.y);

            for (int t = 0; t < dimt; t++) {
                for (int x = 0; x < dimx; x++) {
                    for (int y = 0; y < dimy; y++) {
                        printWriter.printf("%d ", classification[t][x][y]);
                    }
                }
                printWriter.printf("\n");
            }

            printWriter.close();
        } catch (IOException e) {
            System.out.println("Unable to open output file " + fileName);
            e.printStackTrace();
        }
    }

    public void classifier(int position, Vector v) {
        int[] setter = {1, 1}; // initially middle middle

        for (int l = 0; l < dim() - dimy; l += dimy) {
            if (position == l) {
                setter[0] = 0;
            }
        }

        for (int r = dimx - 1; r <= dim(); r += dimx) {
            if (position == r) {
                setter[0] = 2;
            }
        }

        for (int t = 0; t <= dim() - 1; t += (dimy * dimy)) {
            for (int k = t; k < (t + dimy); k++) {
                if (position == k) {
                    setter[1] = 0;
                }
            }
        }

        for (int b = (dimy *dimy - dimy); b < dim() - 1; b += (dimy *dimy)) {
            for (int k = b; k < (b + dimy); k++) {
                if (position == k) {
                    setter[1] = 2;
                }
            }
        }

        if (setter[0] == 0) { //top
            if (setter[1] == 0) { //left
                v.boundaryclass = "TL";
            } else if (setter[1] == 2) { //right
                v.boundaryclass = "TR";
            }
        } else if (setter[0] == 2) { //bottom
            if (setter[1] == 0) { //left
                v.boundaryclass = "BL";
            } else if (setter[1] == 2) { //right
                v.boundaryclass = "BR";
            }
        } else if (setter[1] == 0) {
            v.boundaryclass = "R";
        }else if (setter[1] == 2){
            v.boundaryclass = "L";
        }
        else v.boundaryclass = "M";
    }
}
