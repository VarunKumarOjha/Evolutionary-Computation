
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ojh0009
 */
public class NSGAII {

   
    public static void main(String args[]) {
        System.out.println("This is NSGA II Module");
        NSGAII nsga = new NSGAII(20, 100);
    }     
    /**
     * The main module of the algorithm
     */
    private NSGAII(int pop, int gen) {
        if (pop < 10) {
            System.out.println("Minimum population is 20");
            return;
        }//if
        if (gen < 5) {
            System.out.println("Minimum generation is 20");
            return;
        }//if
        int obj = 2;
        int dim = 5;
        double[] min = new double[dim];
        double[] max = new double[dim];
        for(int j =0;j < dim;j++){
            min[j] = 0.0;
            max[j] = 1.0;
        }  
        //initialize population       
        Initialization init = new Initialization();
        double[][] main_population = init.initiator(pop, dim, obj, min, max); 
        System.out.println("Initial Population");
        init.display(main_population);        
        main_population  = init.nonDominationSort(main_population,dim,obj);
        System.out.println("Non Dominant Sorting");
        init.display(main_population);  
        //start evolution        
        for(int i = 0; i < gen;i++){
            System.out.println(i+" geration \n Chrome:"+main_population.length);
            int pool = (int)(pop/2);
            System.out.println(" pool Size:"+pool);
            int tour = 2;
            TournamentSelection tournament = new TournamentSelection();
            double[][]parent_chromosome = tournament.tournament_selection(main_population, pool, tour);
            System.out.println(" parent_chromosome:"+parent_chromosome.length);
            init.display(parent_chromosome); 
            int mu = 20;
            int mum = 20;
            GeneticOperator ga = new GeneticOperator();
            double[][] offspring_population = ga.genetic_operator(parent_chromosome, dim,obj, mu, mum, min, max);
            System.out.println(" offspring_population:"+offspring_population.length);
            ////init.display(offspring_population);
            //Intermediate population
            //Intermediate population is the combined population of parents and
            //offsprings of the current generation. The population size is two
            //times the initial population.
            //intermediate_chromosome is a concatenation of current population and
            //the offspring population.
            double[][] intermediate_population = init.intermediate_population(main_population,offspring_population);
            System.out.println(" intermediate_population (U):"+intermediate_population.length);
            ////init.display(intermediate_population);
            //Non-domination-sort of intermediate population
            //The intermediate population is sorted again based on non-domination sort
            //before the replacement operator is performed on the intermediate population.
            intermediate_population = init.nonDominationSort(intermediate_population,dim,obj);
            System.out.println(" intermediate_population (S):"+intermediate_population.length);
            ////init.display(intermediate_population);
            //Perform Selection 
            //Once the intermediate population is sorted only the best solution is
            //selected based on it rank and crowding distance. Each front is filled in
            //ascending order until the addition of population size is reached. The
            //last front is included in the population based on the individuals with
            //least crowding distance
            main_population = init.replace_chromosome(intermediate_population, dim, obj, pop);
            System.out.println(" main_population:"+main_population.length);
            ////init.display(main_population);            
        }//for gen
        try {
            FileWriter fw = new FileWriter("Solution.csv");
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pr = new PrintWriter(bw);
            for(int i = 0;i < pop;i++){
                for(int j =0;j<dim+obj+2;j++){
                    pr.print(main_population[i][j]+",");
                }//for j
                pr.println();
            }//for
            pr.close();
        }catch(Exception e){
            
        }
    }//nsga

}
