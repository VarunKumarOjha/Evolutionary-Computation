/**
 *
 * @author ojh0009
 */
class EvaluateObjectives {
    //example: two objective function
    double[] evalute(double[] x,int dim, int obj) {
        double[] ret = new double[obj];
        ret[0] = 0.0;
        for(int i = 0; i < dim - 1;i++){
            ret[0] = ret[0] - 10*Math.exp(-0.2*Math.sqrt(x[i]*x[i]+ x[i + 1]*x[i + 1]));
        }//for
        ret[1] = 0.0;
        for(int i = 0; i < dim;i++){
            ret[1] = ret[1] + (Math.pow(Math.abs(x[i]),0.8) + 5.0* Math.pow(Math.sin(x[i]),3));
        }//for               
        //System.out.println(ret[0]+" "+ret[1]);
       return  ret;
    }//fun:evalute 
}//class
