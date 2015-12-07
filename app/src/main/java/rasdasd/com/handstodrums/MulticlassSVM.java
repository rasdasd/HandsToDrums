package rasdasd.com.handstodrums;

import java.util.ArrayList;

/**
 * Created by robert on 12/2/15.
 */
public class MulticlassSVM {
    //Define MulticlassSVM class constructor:
    svm_model trained_model;
    public MulticlassSVM()
    {
    }

    public void train(ArrayList<float[]> x_train, ArrayList<Integer> y_train)
    {
        //Define SVM problem statement:
        svm_problem prob = new svm_problem();
        prob.l = y_train.size();
        prob.x = new svm_node[prob.l][];
        int m = x_train.get(0).length;

        for(int i=0; i < prob.l; i++)
        {
            svm_node[] x = new svm_node[m];
            for (int j = 0; j < m; j++)
            {
                x[j] = new svm_node();
                x[j].index = j+1;
                x[j].value = x_train.get(i)[j];
            }
            prob.x[i] = x;
        }
        prob.y = new double[prob.l];
        for(int i=0; i < prob.l; i++)
            prob.y[i] = y_train.get(i);

        //Define SVM parameters:
        svm_parameter param = new svm_parameter();
        param.svm_type = svm_parameter.C_SVC;   //Type of SVM                                       (default C_SVC)
        param.kernel_type = svm_parameter.RBF;  //Type of kernel function                           (default RBF)
        param.degree = 3;                       //Degree of kernel function                         (default 3)
        param.gamma = 1.0/(m+1);                //Gamma in kernel function                          (default 1.0/(m+1))
        param.coef0 = 0;                        //Coefficient 0 in kernel function                  (default 0)
        param.nu = 0.5;                         //Parameter nu of nu-SVC, one-class SVM, and nu-SVR (default 0.5)
        param.cache_size = 100;                 //Cache memory size in MB                           (default 40)
        param.C = 1;                            //Parameter C of C-SVC, epsilon-SVR, and nu-SVR     (default 1)
        param.eps = 1e-3;                       //Tolerance of termination criterion                (default 1e-3)
        param.p = 0.1;                          //Epsilon in loss function of epsilon-SVR           (default 0.1)
        param.shrinking = 1;                    //Whether to use the shrinking heuristics, 0 or 1   (default 1)
        param.probability = 0;                  //Whether to train an SVC or SVR model, 0 or 1      (default 0)
        param.nr_weight = 0;
        param.weight_label = new int[0];
        param.weight = new double[0];

        //Initialize training:
        svm_model model;
        model = svm.svm_train(prob,param);
        this.trained_model = model;
    }

    public int classify(float[] datapoint)
    {
        int m = datapoint.length;
        svm_node[] x = new svm_node[m];
        for(int j=0;j<m;j++)
        {
            x[j] = new svm_node();
            x[j].index = -1;
            x[j].value = datapoint[j];
        }

        int nr_class = 4;
        double[] dec_values;
        if(trained_model.param.svm_type == svm_parameter.ONE_CLASS ||
                trained_model.param.svm_type == svm_parameter.EPSILON_SVR ||
                trained_model.param.svm_type == svm_parameter.NU_SVR)
        {
            dec_values = new double[1];
        }
        else{
        dec_values = new double[nr_class*(nr_class-1)/2];
        }

        return (int)svm.svm_predict_values(trained_model, x, dec_values);

    }
}
