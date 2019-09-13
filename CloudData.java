
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.PrintWriter;

public class CloudData {

	windVector [] advection; // in-plane regular grid of wind vectors, that evolve over time
	int [][][] classification; // cloud type per grid point, evolving over time
	int dimx, dimy, dimt; // data dimensions

    float xtot;
    float ytot;

	// overall number of elements in the timeline grids
	int dim(){
		return dimt*dimx*dimy;
	}
	
	// convert linear position into 3D location in simulation grid
	void locate(int pos, int [] ind)
	{
		ind[0] = (int) pos / (dimx*dimy); // t
		ind[1] = (pos % (dimx*dimy)) / dimy; // x
		ind[2] = pos % (dimy); // y
	}
	
	// read cloud simulation data from file
	void readData(String fileName){ 
		try{ 
			Scanner sc = new Scanner(new File(fileName), "UTF-8");
			
			// input grid dimensions and simulation duration in timesteps
			dimt = sc.nextInt();
			dimx = sc.nextInt(); 
			dimy = sc.nextInt();
			
			// initialize and load advection (wind direction and strength) and convection
			advection = new windVector[dimt * dimx * dimy];
			int count = 0;
			for(int t = 0; t < dimt*dimy*dimx; t++){
			            float x1 = sc.nextFloat();
                        float y1 = sc.nextFloat();
                        float convec = sc.nextFloat();
                        advection[count] = new windVector(x1,y1,convec);
                        BClassifier(count, advection[count]);
                        count++;
					}
			
			classification = new int[dimt][dimx][dimy];
			sc.close(); 
		} 
		catch (IOException e){ 
			System.out.println("Unable to open input file "+fileName);
			e.printStackTrace();
		}
		catch (java.util.InputMismatchException e){ 
			System.out.println("Malformed input file "+fileName);
			e.printStackTrace();
		}
	}
	
	// write classification output to file
	void writeData(String fileName, windVector wind){
		 try{ 
			 FileWriter fileWriter = new FileWriter(fileName);
			 PrintWriter printWriter = new PrintWriter(fileWriter);
			 printWriter.printf("%d %d %d\n", dimt, dimx, dimy);
			 printWriter.printf("%f %f\n", wind.x, wind.y);
			 
			 for(int t = 0; t < dimt; t++){
				 for(int x = 0; x < dimx; x++){
					for(int y = 0; y < dimy; y++){
						printWriter.printf("%d ", classification[t][x][y]);
					}
				 }
				 printWriter.printf("\n");
		     }
				 
			 printWriter.close();
		 }
		 catch (IOException e){
			 System.out.println("Unable to open output file "+fileName);
				e.printStackTrace();
		 }
	}
    public void BClassifier(int i, windVector vector){
        boolean leftBoundary = false;
        boolean rightBoundary = false;
        boolean topBoundary = false;
        boolean bottomBoundary = false;


        for (int j = dimx-1; j <= dim(); j+=dimx) {
            if(i ==j)
                rightBoundary=true;
        }

        for (int j = 0; j <= dim()-dimy; j+=dimy) {
            if(i ==j)
                leftBoundary=true;
        }
        for (int j = (dimy*dimy-dimy); j < dim() - 1; j+= (dimy*dimy)) {
            for (int k = j; k < (j+dimy) ; k++) {
                if (i==k)
                    bottomBoundary=true;
            }
        }
        for (int j = 0; j <= dim()-1; j+= (dimy*dimy)) {
            for (int k = j; k < (j+dimy) ; k++) {
                if(i==k)
                    topBoundary=true;
            }
        }


        if(topBoundary){
            if(leftBoundary)
                vector.boundclass=5;
            else if(rightBoundary)
                vector.boundclass=6;
            else vector.boundclass=2;
        } else if (bottomBoundary){
            if(leftBoundary)
                vector.boundclass=7;
            else if(rightBoundary)
                vector.boundclass=8;
            else vector.boundclass=4;
        } else if(rightBoundary)
            vector.boundclass=3;
        else if(leftBoundary)
            vector.boundclass=1;
        else vector.boundclass=0;
    }
}
