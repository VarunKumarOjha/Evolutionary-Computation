
import Randoms.JavaRand;
import Randoms.MersenneTwisterFast;
import java.util.Vector;

/**
 *
 * @author ojh0009
 */
public class GeneticOperator {
    MersenneTwisterFast rand = new MersenneTwisterFast();
    double[][] genetic_operator(double[][] parent_chromosome, int dim, int obj, int mu, int mum, double[] l_limit, double[] u_limit) {
       
        int N = parent_chromosome.length;
        int D = parent_chromosome[0].length;
        int p = 0;
        Vector child = new Vector();
        boolean was_crossover = false;
        boolean was_mutation = false;
        for (int i = 0; i < N; i++) {
            //Initialize the children to be null vector.
            double[] child_1 = new double[D];
            double[] child_2 = new double[D];
            double[] child_3 = new double[D];
            //With 90 % probability perform crossover
            if (rand.nextDouble() < 0.9) {
                //Select the first parent 
                int[] parents =  randomIntVector(0,N);
                //Make sure both the parents are not the same
                int parent1 = parents[0];
                int parent2 = parents[1];
                int parent2Index = 1;
                while (true) {
                    if(parent2Index != N){
                    if (!isequal(parent_chromosome, parent1, parents[parent2Index])) {
                        parent2 = parents[parent2Index];
                        break;
                    } else {
                        parent2Index++;
                    }
                    }else{
                        //System.out.println("Error is no unique candidate found");
                        break;
                    }
                }//while
                ////System.out.println(parent1+" - "+parent2);
                //Get the chromosome information for each randomnly selected
                //parents
                double[] parent_1 = new double[D];
                double[] parent_2 = new double[D];
                System.arraycopy(parent_chromosome[parent1], 0, parent_1, 0, D);
                System.arraycopy(parent_chromosome[parent2], 0, parent_2, 0, D);
                //Perform corssover for each decision variable in the chromosome.
                for (int j = 0; j < dim; j++) {
                    //SBX (Simulated Binary Crossover).
                    //For more information about SBX refer the enclosed pdf file.
                    //Generate a random number
                    double u = rand.nextDouble();
                    double bq;
                    if (u <= 0.5) {
                        bq = Math.pow((2.0 * u), (1.0 / (mu + 1.0)));
                    } else {
                        bq = Math.pow((1.0 / (2.0 * (1.0 - u))), (1.0 / (mu + 1.0)));
                    }//if  
                    //Generate the jth element of first child
                    child_1[j] = 0.5 * (((1.0 + bq) * parent_1[j]) + (1.0 - bq) * parent_2[j]);
                    //Generate the jth element of second child
                    child_2[j] = 0.5 * (((1.0 - bq) * parent_1[j]) + (1.0 + bq) * parent_2[j]);
                    //Make sure that the generated element is within the specified
                    //decision space else set it to the appropriate extrema.
                    if (child_1[j] > u_limit[j]) {
                        child_1[j] = u_limit[j];
                    } else if (child_1[j] < l_limit[j]) {
                        child_1[j] = l_limit[j];
                    }//if
                    if (child_2[j] > u_limit[j]) {
                        child_2[j] = u_limit[j];
                    } else if (child_2[j] < l_limit[j]) {
                        child_2[j] = l_limit[j];
                    }//if 
                    //System.out.print(" ["+child_1[j]+" "+child_2[j]+"]");
                }//for j
                //System.out.println();
                //Evaluate the objective function for the offsprings and as before
                //concatenate the offspring chromosome with objective value.
                EvaluateObjectives ev = new EvaluateObjectives();
                double[] fun1 = ev.evalute(child_1, dim, obj);
                double[] fun2 = ev.evalute(child_2, dim, obj);
                int indx = 0;
                for (int j = dim; j < D - 2; j++) {//2 minus becuase of rank and distance
                    child_1[j] = fun1[indx];
                    child_2[j] = fun2[indx];
                    indx++;
                }//for j
                child_1[dim + obj] = -1;//initial rank
                child_1[dim + obj + 1] = -1;//initial distance
                child_2[dim + obj] = -1;//initial rank
                child_2[dim + obj + 1] = -1;//initial distance
                //Set the crossover flag. When crossover is performed two children
                //are generate, while when mutation is performed only only child is generated.
                was_crossover = true;
                was_mutation = false;
            } else {//With 10 % probability perform mutation. Mutation is based on polynomial mutation. 
                //int parent3 = (int) ((N - 0) * Math.random() + 0);
                int parent3 = rand.nextInt(N);// ((N - 0) * Math.random() + 0);
                double[] parent_3 = new double[D];
                //Get the chromosome information for the randomnly selected parent.                
                System.arraycopy(parent_chromosome[parent3], 0, parent_3, 0, D);
                for (int j = 0; j < dim; j++) {
                    double u = rand.nextDouble();
                    double delta;
                    if (u <= 0.5) {
                        delta = Math.pow((2.0 * u), (1.0 / (mum + 1.0))) - 1.0;
                    } else {
                        delta = 1.0 - Math.pow((2 * (1.0 - u)), (1.0 / (mum + 1.0)));
                    }//if  
                    //Generate the jth element of first child
                    child_3[j] = parent_3[j] + delta;
                    //decision space else set it to the appropriate extrema.
                    if (child_3[j] > u_limit[j]) {
                        child_3[j] = u_limit[j];
                    } else if (child_3[j] < l_limit[j]) {
                        child_3[j] = l_limit[j];
                    }//if 
                    //System.out.print(" "+child_3[j]);
                }//for j
                //System.out.println();
                //Evaluate the objective function for the offspring and as before
                //concatenate the offspring chromosome with objective value.  
                EvaluateObjectives ev = new EvaluateObjectives();
                double[] fun = ev.evalute(child_3, dim, obj);
                int indx = 0;
                for (int j = dim; j < D - 2; j++) {//2 minus becuase of rank and distance
                    child_3[j] = fun[indx];
                    indx++;
                }//for j
                child_3[dim + obj] = -1;//initial rank
                child_3[dim + obj + 1] = -1;//initial distance
                //Set the mutation flag
                was_crossover = false;
                was_mutation = true;
            }//if  
            if (was_crossover) {
                child.add(child_1);
                child.add(child_2);
                p = p + 2;
            } else if (was_mutation) {
                child.add(child_3);
                p = p + 1;
            }//if
        }//for i
        //System.out.println(p + " = " + child.size());
        double[][] offspring = new double[p][D];
        for (int i = 0; i < p; i++) {
            //System.out.print(i + " :");
            double[] childVal = (double[]) (Object) child.get(i);
            for (int j = 0; j < D; j++) {
                offspring[i][j] = childVal[j];
                //System.out.printf(" %.2f", childVal[j]);
            }//for j 
            //System.out.println();
        }//for i
        return offspring;//return offspring
    }

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
            ////System.out.println(i + " - " + a[i] + " ");
            //a[i] = i;//System.out.println(i+" - "+a[i]+" ");
        }//for
        return a;
    }//permutation

    private boolean isequal(double[][] parent_chromosome, int parent1, int parent2) {
        int D = parent_chromosome[parent1].length;
        boolean equal = true;
        for (int i = 0; i <D; i++) {
            if (!(parent_chromosome[parent1][i] == parent_chromosome[parent2][i])) {
                equal = false;
            }//if
        }//for
        return equal;
    }//faun: isqual

}
