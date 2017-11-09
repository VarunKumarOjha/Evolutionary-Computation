
import Randoms.MersenneTwisterFast;


/**
 *
 * @author ojh0009
 */
public class TournamentSelection {
    MersenneTwisterFast rand = new MersenneTwisterFast();
    double[][] tournament_selection(double[][] x, int pool_size, int tour_size) {
        int N = x.length;
        int D = x[0].length;
        int rank = D - 2;
        int distance = D - 1;
        double[][] matingPool = new double[pool_size][D];
        //Until the mating pool is filled, perform tournament selection
        for (int i = 0; i < pool_size; i++) {
            //Select n individuals at random, where n = tour_size
            int[] candidate = randomIntVector(0, N);
            double[] c_obj_rank = new double[tour_size];
            double[] c_obj_distance = new double[tour_size];
            //Collect information about the selected candidates.
            for (int j = 0; j < tour_size; j++) {//System.out.print(" " + candidate[j]);
                c_obj_rank[j] = x[candidate[j]][rank];
                c_obj_distance[j] = x[candidate[j]][distance];
            }//for j           
            int[] minRanks = min(c_obj_rank);// System.out.print("  |");for (int j = 0; j < minRanks.length; j++){System.out.print(" " + candidate[minRanks[j]]);}
            //check is more than one candidates have
            if (minRanks.length > 1) {
                //check crowding distance
                int[] maxDist = max(c_obj_distance, minRanks);//System.out.print("  :");for (int j = 0; j < maxDist.length; j++){ System.out.print(" " + candidate[minRanks[maxDist[j]]]);}
                //System.out.print(" >"+candidate[minRanks[maxDist[0]]]);
                System.arraycopy(x[candidate[minRanks[maxDist[0]]]], 0, matingPool[i], 0, D);
            } else {
                //System.out.print(" >"+candidate[minRanks[0]]);
                System.arraycopy(x[candidate[minRanks[0]]], 0, matingPool[i], 0, D);
            }//if 
            //System.out.println();
        }//for i        
        return matingPool;
    }//fun: tournament

    public int[] randomIntVector(int min, int max) {
        int a[] = new int[max];
        for (int i = 0; i < max; i++) {
            if (i == 0) {
                a[i] = (int) ((max - min) * rand.nextDouble() + min);
            } else {
                while (true) {
                    boolean flag = false;
                    int r = (int) ((max - min) * Math.random() + min);
                    for (int j = 0; j < i; j++) {
                        if (r == a[j]) {
                            flag = true;
                            break;
                        }//if	
                    }//for
                    if (!flag) {
                        a[i] = r;
                        break;
                    }//if   
                }//while				
            }//else 	
            //System.out.println(i + " - " + a[i] + " ");
            //a[i] = i;System.out.println(i+" - "+a[i]+" ");
        }//for
        return a;
    }//permutation

    private int[] min(double[] c_obj) {
        double min = c_obj[0];
        int length = c_obj.length;
        int[] minIndex = new int[length];
        minIndex[0] = 0;
        for (int i = 1; i < length; i++) {
            minIndex[i] = -1;//assigning to test others same min
            if (c_obj[i] < min) {
                min = c_obj[i];
                minIndex[0] = i;//index into objective
            }//if
        }//for i
         
        //check is there is asme rank min exists
        for (int i = 0; i < length; i++) {
            if (i != minIndex[0]) {//not the selected index
                if (c_obj[i] == c_obj[minIndex[0]]) {//check for equal rank
                    minIndex[i] = i;//index into c_obj
                }//if
            }//if
        }//for i
        int count = 0;// cont equal ranks index
        for (int i = 0; i < length; i++) {
            if (minIndex[i] != -1) {
                count++;
            }
        }
        int[] totalSameRanks = new int[count];
        count = 0;
        for (int i = 0; i < length; i++) {
            if (minIndex[i] != -1) {
                totalSameRanks[count] = minIndex[i];
                count++;
            }//if
        }//for
        return totalSameRanks;
    }//min

    private int[] max(double[] dist, int[] rank) {
        //fileter distance
        int length = rank.length;
        double[] c_obj = new double[length];
        int count = 0;
        for (int i = 0; i < dist.length; i++) {
            if (rank[count] == i) {
                c_obj[count] = dist[i];//copy dist index
                //System.out.print(" (" + c_obj[count] + ")");
                count++;
            }//if  
        }//for        
        double max = c_obj[0];
        int[] maxIndex = new int[length];
        maxIndex[0] = 0;
        for (int i = 1; i < length; i++) {
            maxIndex[i] = -1;
            if (c_obj[i] > max) {//computing max
                max = c_obj[i];
                maxIndex[0] = i;//index into rank
                //System.out.println("New Max: "+max+" at: "+maxIndex[0]);
            }//if
        }//for i
        for (int i = 0; i < length; i++) {
            if (i != maxIndex[0]) {//not the selected index
                if (c_obj[maxIndex[0]] == c_obj[i]) {//check for equal distance
                    //System.out.println("True");
                    maxIndex[i] = i;//index of rank
                }
            }//if
        }//for i
        count = 0;
        for (int i = 0; i < length; i++) {
            if (maxIndex[i] != -1) {
                count++;
            }
        }
        //System.out.println("count"+count);
        int[] totalSameDist = new int[count];
        count = 0;
        for (int i = 0; i < length; i++) {
            if (maxIndex[i] != -1) {
                totalSameDist[count] = maxIndex[i];
                count++;
            }//if
        }//for
        return totalSameDist;
    }//max
}//class
