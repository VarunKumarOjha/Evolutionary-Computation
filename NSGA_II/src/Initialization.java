import Randoms.JavaRand;
import Randoms.*;
import java.util.Vector;

/**
 *
 * @author ojh0009
 */
class Initialization {

    /**Random initial solutions */
    public double[][] initiator(int pop, int dim, int obj, double[] min, double[] max) {
        MersenneTwisterFast rand = new MersenneTwisterFast();
        ////System.out.println("Initialization----------------------");        
        int length_of_chrome = dim + obj + 2;//1 for rank column and 1 cor distance column
        double[][] population = new double[pop][length_of_chrome];
        for (int i = 0; i < pop; i++) {
            double[] var = new double[dim];
            double[] fun;
            for (int j = 0; j < dim; j++) {
                var[j] = ((max[j] - min[j]) * rand.nextDouble() + min[j]);
                //var[j] = rand.nextUniform(min[j],max[j]);
                population[i][j] = var[j];
                //System.out.printf("  %.3f", population[i][j]);
            }
            //System.out.println();
            EvaluateObjectives ev = new EvaluateObjectives();
            fun = ev.evalute(var, dim, obj);
            int indx = 0;
            for (int j = dim; j < length_of_chrome - 2; j++) {//2 minus becuase of rank and distance
                population[i][j] = fun[indx];
                indx++;
            }
            population[i][dim + obj] = -1;//initial rank
            population[i][dim + obj + 1] = -1;//initial distance
        }
        return population;
    }//fun:initiator

    void display(double[][] population) {
        int N = population.length;
        int D = population[0].length;
        for (int i = 0; i < N; i++) {
            System.out.print(i + " : ");
            for (int j = 0; j < D ; j++) {
                if(j == D-2){
                    System.out.printf("  %d", (int)population[i][j]);
                }else{
                    System.out.printf("  %.2f", population[i][j]);
                }//if                
            }//for
            System.out.println();
        }//for
    }//fun:dispaly

    class Individual {

        int n;
        Vector p;

        Individual() {
            p = new Vector();
        }

        void set_p(Vector val) {
            p.addAll(val);
        }

        Vector get_p() {
            return p;
        }
    }//class: Individual

    double[][] nonDominationSort(double[][] x, int dim, int obj) {
        int N = x.length;
        int D = (dim + obj + 2);
        //int frontNum = 1;
        int front = 0;
        Individual[] individual = new Individual[N];
        Vector F = new Vector();
        Vector f = new Vector();
        //Non-Dominated sort. 
        for (int i = 0; i < N; i++) {
            individual[i] = new Individual();
            // Number of individuals that dominate this individual
            individual[i].n = 0;
            //Individuals which this individual dominate
            Vector d = new Vector();
            for (int j = 0; j < N; j++) {
                int dom_less = 0;
                int dom_equl = 0;
                int dom_more = 0;
                for (int k = 0; k < obj; k++) {
                    if (x[i][dim + k] < x[j][dim + k]) {
                        dom_less = dom_less + 1;
                    } else if (x[i][dim + k] == x[j][dim + k]) {
                        dom_equl = dom_equl + 1;
                    } else {
                        dom_more = dom_more + 1;
                    }
                }// for k
                if (dom_less == 0 && dom_equl != obj) {
                    // Number of individuals that dominate this individual                    
                    individual[i].n = individual[i].n + 1;
                } else if (dom_more == 0 && dom_equl != obj) {
                    //Individuals which this individual dominate
                    ////System.out.print(" "+j);
                    d.add(j);
                }
            }// for j
            individual[i].set_p(d);
            ////System.out.println();
            if (individual[i].n == 0) {
                x[i][dim + obj] = front;
                f.add(i);
            }//if
        }// for i 
        //checking individuals
        /*for (int i = 0; i < N; i++) {
         // Number of individuals that dominate this individual
         ////System.out.println("Dominated: "+individual[i].n);
         //Individuals which this individual dominate
         //System.out.print("Dominates:");
         Vector p = (Vector)individual[i].get_p();
         for (int j = 0; j < p.size(); j++) {
         //System.out.print(" "+p.get(j));
         }// for j
         //System.out.println();
         }// for i*/

        F.add(front, f);
        // Find the subsequent fronts        
        for (int i = 0; i < F.size() && !((Vector) F.get(front)).isEmpty(); i++) {
            System.out.print("Fornt" + " " + front + ": " + ((Vector) F.get(front)).size() + " >");
            f = new Vector();
            for (int j = 0; j < ((Vector) F.get(front)).size(); j++) {
                int indx = Integer.parseInt((((Vector) F.get(front))).get(j).toString());
                System.out.print(" " + indx);
                if (!individual[indx].get_p().isEmpty()) {
                    for (int k = 0; k < individual[indx].get_p().size(); k++) {
                        int indIndex = Integer.parseInt(individual[indx].get_p().get(k).toString());
                        individual[indIndex].n = individual[indIndex].n - 1;
                        if (individual[indIndex].n == 0) {
                            x[indIndex][dim + obj] = front + 1;
                            f.add(indIndex);
                        }//if                        
                    }//for k
                }//if
            }//for j
            front = front + 1;
            F.add(front, f);
            System.out.println();
        }//for i 
        //Checking if any indivial has not been picked in front        
        f = new Vector();
        for (int i = 0; i < N; i++) {
            if (x[i][dim + obj] == -1.0) {
                x[i][dim + obj] = front;
                f.add(i);
            }//if
        }//for  
        //front = front + 1;
        F.add(front, f);
        //System.out.print("Fornt" + " " + front + ": " + ((Vector) F.get(front)).size() + " >");
        for (int j = 0; j < ((Vector) F.get(front)).size(); j++) {
            int indx = Integer.parseInt((((Vector) F.get(front))).get(j).toString());
            //System.out.print(" " + indx);
        }//for j
        //System.out.println("Unsorted:");
        //display(x);
        //System.out.println("Front Based Sorting");
        //sorting according to front
        int[] index_of_Fronts = sort2DarrayCol(x, dim + obj);
        double[][] sorted_based_on_fronts = sort2DArray(x, index_of_Fronts);
        //display(sorted_based_on_fronts);
        //x = sort(x, dim, obj);

        //computing crrowding distance
        //System.out.println("Crowding distance computation:");
        int current_index = 0;
        double[][] z = new double[N][D];
        for (int i = 0; i < F.size()-1 && !((Vector) F.get(i)).isEmpty(); i++) {
            //System.out.print("Font" + i + ":");
            int length_Fi = ((Vector) F.get(i)).size();
            double[][] y = new double[length_Fi][D];
            double[][] objVal = new double[length_Fi][obj];
            int previous_index = current_index;
            int pointer = 0;
            for (int j = 0; j < length_Fi; j++) {
                int indexInto = current_index + j;
                //System.out.print(" ("+j+"):" + indexInto);
                System.arraycopy(sorted_based_on_fronts[indexInto], 0, y[j], 0, D);
                pointer++;
            }//for j 
            current_index = current_index + pointer;
            for (int j = 0; j < obj; j++) {
                int[] index_of_obj = sort2DarrayCol(y, dim + j);
                double[][] sorted_based_on_objectives = sort2DArray(y, index_of_obj);
                double f_max = sorted_based_on_objectives[length_Fi - 1][dim + j];
                double f_min = sorted_based_on_objectives[0][dim + j];
                ////System.out.println(f_max + " " + f_min);                
                objVal[length_Fi - 1][j] = Double.POSITIVE_INFINITY;//inifinity
                objVal[0][j] = Double.POSITIVE_INFINITY;//inifinity
                for (int k = 1; k < length_Fi - 1; k++) {
                    double next_obj = sorted_based_on_objectives[k + 1][dim + j];                    
                    double previous_obj = sorted_based_on_objectives[k - 1][dim + j];
                    if (f_max - f_min == 0.0) {
                        objVal[k][j]  = Double.POSITIVE_INFINITY;//Infinity
                    } else {
                        objVal[k][j]  = (next_obj - previous_obj) / (f_max - f_min);
                        System.out.printf("%.3f, %.3f, %.3f, %.3f, %.3f",next_obj,previous_obj,f_max,f_min,objVal[k][j]);
                        //System.exit(0);
                    }//if                 
                }//for k
            }//for j
            double[] distance = new double[length_Fi];
            for (int j = 0; j < length_Fi; j++) {
                distance[j] = 0.0;
                for (int k = 0; k < obj; k++) {
                    distance[j] = distance[j] + objVal[j][k]; 
                }//for k                
                y[j][dim + obj + 1] = distance[j];
            }//for j 
            //copy y to z
            for (int j = 0; j < length_Fi; j++) {
                System.arraycopy(y[j], 0, z[previous_index++], 0, D);
            }//for j 
            //System.out.println();
        }//for i
        for (int i = 0; i < N; i++) {
            System.arraycopy(z[i], 0, x[i], 0, D);
        }//for j 
        //System.out.println();
        ////display(x);
        return x;
    }//fun : non - dominant
    //Sorting ----------------------------------------------------
    public int[] sort2DarrayCol(double[][] data, int colIndex) {
        int N = data.length;
        int[] indices = new int[N];
        double[] x = new double[N];
        for (int i = 0; i < N; i++) {
            x[i] = data[i][colIndex];
        }
        indices[0] = 0;
        for (int i = 1; i < N; i++) {
            int j = i;
            for (; j >= 1 && x[j] < x[j - 1]; j--) {
                double temp = x[j];
                x[j] = x[j - 1];
                indices[j] = indices[j - 1];
                x[j - 1] = temp;
            }
            indices[j] = i;
        }
        /*for (int i = 0; i < N; i++) {
            //System.out.print(" " + indices[i]);
         }*/
        return indices;//indices of sorted elements
    }//inster sort

    private double[][] sort2DArray(double[][] array, int[] index) {
        int N = array.length;
        int D = array[0].length;
        double[][] sortedArray = new double[N][D];
        for (int i = 0; i < N; i++) {
            System.arraycopy(array[index[i]], 0, sortedArray[i], 0, D);
        }//for i
        return sortedArray;
    }//sort 2D array 
    
    double[][] intermediate_population(double[][] main_population, double[][] offspring_population) {
        int mN = main_population.length;
        int D = main_population[0].length;
        int oN = offspring_population.length;
        double[][] intermediate_population = new double[mN+oN][D]; 
        for(int i = 0;i< mN+oN; i++){
            for(int j = 0;j< D;j++){
                if(i < mN){
                    intermediate_population[i][j] =  main_population[i][j]; 
                }else{
                    intermediate_population[i][j] =  offspring_population[i-mN][j]; 
                }//if 
            }//for j
        }//for i
        return intermediate_population;
    }//fun: intermediate opulation
    
    double[][] replace_chromosome(double[][] intermediate_population, int dim, int obj, int pop) {
        int N = intermediate_population.length;
        int D = dim+obj+2;
        double[][] replaced_pop = new double[pop][D];
        int[] rank_index = sort2DarrayCol(intermediate_population, dim + obj);
        double[][] sorted_based_on_ranks = sort2DArray(intermediate_population, rank_index);
        //System.out.println("Rank based Sorting :Replace");
        //display(sorted_based_on_ranks);
        int max_rank = maxRank(sorted_based_on_ranks,dim,obj);
        int previous_index = 0;
        for(int i = 0;i <= max_rank;i++){
            //Get the index for current rank i.e the last the last element in the
            //sorted_chromosome with rank i. 
            int current_index = findMax(sorted_based_on_ranks,dim,obj,i);
            //Check to see if the population is filled if all the individuals with
            //rank i is added to the population. 
            //System.out.print(current_index+"]");
            if(current_index > pop){
                //If so then find the number of individuals with in with current rank i.
                int remaining = pop - previous_index;
                //System.out.print(">"+remaining+">");
                //Get information about the individuals in the current rank i.
                int temp_length = current_index - previous_index;
                double[][] temp_pop = new double[temp_length][D];
                int idx_temp = 0;
                int copy_index = previous_index;
                for(; copy_index < current_index; copy_index++){
                    System.arraycopy(sorted_based_on_ranks[copy_index], 0, temp_pop[idx_temp++], 0, D);                    
                }//for 
                //ort the individuals with rank i in the descending order based on
                //the crowding distance.
                int[] indexDist = sort2DarrayColDecend(temp_pop,dim + obj+1);
                temp_pop = sort2DArray(temp_pop, indexDist); 
                for(int j = 0; j < remaining;j++){
                    System.arraycopy(temp_pop[j], 0, replaced_pop[previous_index], 0, D);  
                    ////System.out.print((previous_index)+" ");
                    previous_index++;
                }//for
                ////System.out.println();
            }else if(current_index < pop){
                //copy population with rank i
                for(; previous_index <= current_index; previous_index++){
                    System.arraycopy(sorted_based_on_ranks[previous_index], 0, replaced_pop[previous_index], 0, D); 
                    ////System.out.print((previous_index)+" ");
                }//for
                ////System.out.println();
            }else if(current_index == pop){
                //copy all population with rank i
                for(; previous_index < current_index; previous_index++){
                    System.arraycopy(sorted_based_on_ranks[previous_index], 0, replaced_pop[previous_index], 0, D);                    
                    ////System.out.print((previous_index)+" ");
                }//for
                ////System.out.println();
            }//if
        }//for        
        return replaced_pop;
    }//fun: replace
    int maxRank(double[][] x, int dim, int obj){
        int N = x.length;
        int D = dim+obj;
        int rank = (int)x[0][dim+obj];
        for(int i = 1;i < N;i++){
            if(x[i][dim+obj] > rank){
                rank = (int)x[i][dim+obj];
            }//if
        }//for
        return rank;
    }//fin rank
    int findMax(double[][] x, int dim, int obj,int rank){
        int N = x.length;
        int index = 0;
        for(int i = 0;i < N;i++){
            if((int)x[i][dim+obj] == rank){
                index = i;                
            }//if
        }//for
        return index;
    }//fin rank
    
    public int[] sort2DarrayColDecend(double[][] data, int colIndex) {
        int N = data.length;
        int[] indices = new int[N];
        double[] x = new double[N];
        for (int i = 0; i < N; i++) {
            x[i] = data[i][colIndex];
        }
        indices[0] = 0;
        for (int i = 1; i < N; i++) {
            int j = i;
            for (; j >= 1 && x[j] > x[j - 1]; j--) {
                double temp = x[j];
                x[j] = x[j - 1];
                indices[j] = indices[j - 1];
                x[j - 1] = temp;
            }
            indices[j] = i;
        }
        /*for (int i = 0; i < N; i++) {
            //System.out.print(" " + indices[i]);
         }*/
        return indices;//indices of sorted elements
    }//inster sort
    
}//class
