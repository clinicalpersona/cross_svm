

class svm_train {
	private svm_parameter param;		// set by parse_command_line
	private svm_problem prob;		// set by read_problem
	private svm_model model;
	private String input_file_name;		// set by parse_command_line
	private String model_file_name;		// set by parse_command_line
	private String error_msg;
	private int cross_validation;
	private int nr_fold;

	private static svm_print_interface svm_print_null = new svm_print_interface()
	{
		public void print(String s) {}
	};

	private static void exit_with_help()
	{
            System.out.print(
                             "Usage: svm_train [options] training_set_file [model_file]\n"
                             +"options:\n"
                             +"-s svm_type : set type of SVM (default 0)\n"
                             +"	0 -- C-SVC		(multi-class classification)\n"
                             +"	1 -- nu-SVC		(multi-class classification)\n"
                             +"	2 -- one-class SVM\n"
                             +"	3 -- epsilon-SVR	(regression)\n"
                             +"	4 -- nu-SVR		(regression)\n"
                             +"-t kernel_type : set type of kernel function (default 2)\n"
                             +"	0 -- linear: u'*v\n"
                             +"	1 -- polynomial: (gamma*u'*v + coef0)^degree\n"
                             +"	2 -- radial basis function: exp(-gamma*|u-v|^2)\n"
                             +"	3 -- sigmoid: tanh(gamma*u'*v + coef0)\n"
                             +"	4 -- precomputed kernel (kernel values in training_set_file)\n"
                             +"-d degree : set degree in kernel function (default 3)\n"
                             +"-g gamma : set gamma in kernel function (default 1/num_features)\n"
                             +"-r coef0 : set coef0 in kernel function (default 0)\n"
                             +"-c cost : set the parameter C of C-SVC, epsilon-SVR, and nu-SVR (default 1)\n"
                             +"-n nu : set the parameter nu of nu-SVC, one-class SVM, and nu-SVR (default 0.5)\n"
                             +"-p epsilon : set the epsilon in loss function of epsilon-SVR (default 0.1)\n"
                             +"-m cachesize : set cache memory size in MB (default 100)\n"
                             +"-e epsilon : set tolerance of termination criterion (default 0.001)\n"
                             +"-h shrinking : whether to use the shrinking heuristics, 0 or 1 (default 1)\n"
                             +"-b probability_estimates : whether to train a SVC or SVR model for probability estimates, 0 or 1 (default 0)\n"
                             +"-w weight : set the parameter C of class i to weight*C, for C-SVC (default 1)\n"
                             +"-v n : n-fold cross validation mode\n"
                             +"-q : quiet mode (no outputs)\n"
                             +"-f problem_type : sparse (0) or full (1) (default 0)\n"
                             +"-u reuse : in cross-validation, compute dot products exactly once, yes (1) or no (0) (default 0)\n"
							 +"-o precompute kernal matrix, yes (1) or no (0) (default 0)\n"

			);
            System.exit(1);
	}

	private void do_cross_validation(xsvm svm)
	{
		int i;
		int total_correct = 0;
		double total_error = 0;
		double sumv = 0, sumy = 0, sumvv = 0, sumyy = 0, sumvy = 0;
		double[] target = new double[prob.l];

		svm.svm_cross_validation(prob,param,nr_fold,target);
		if(param.svm_type == svm_parameter.EPSILON_SVR ||
		   param.svm_type == svm_parameter.NU_SVR)
		{
			for(i=0;i<prob.l;i++)
			{
				double y = prob.y[i];
				double v = target[i];
				total_error += (v-y)*(v-y);
				sumv += v;
				sumy += y;
				sumvv += v*v;
				sumyy += y*y;
				sumvy += v*y;
			}
			System.out.print("Cross Validation Mean squared error = "+total_error/prob.l+"\n");
			System.out.print("Cross Validation Squared correlation coefficient = "+
				((prob.l*sumvy-sumv*sumy)*(prob.l*sumvy-sumv*sumy))/
				((prob.l*sumvv-sumv*sumv)*(prob.l*sumyy-sumy*sumy))+"\n"
				);
		}
		else
		{
			for(i=0;i<prob.l;i++)
				if(target[i] == prob.y[i])
					++total_correct;
			System.out.print("Cross Validation Accuracy = "+100.0*total_correct/prob.l+"%\n");
		}
	}
	
	private void run(String argv[]) throws Exception
	{	
		parse_command_line(argv);

        long dt1 = System.currentTimeMillis();
		prob = xsvm.read_problem(input_file_name, param);
        dt1 = System.currentTimeMillis() - dt1;
        System.out.println("Model load time: " + + dt1/1000.0 + "s");

		error_msg = xsvm.svm_check_parameter(prob, param);
		if(error_msg != null)
		{
			System.err.print("ERROR: "+error_msg+"\n");
			System.exit(1);
		}
		
		xsvm svm = new xsvm();
		svm.svm_set_print_string_function(param.print_func);

        if (param.precompute_kernel == 1) {
            long dt2 = System.currentTimeMillis();
            svm.precalculate_kernel(prob, param);
            dt2 = System.currentTimeMillis() - dt2;
            System.out.println("Precompute execution time: " + + dt2/1000.0 + "s");
        }
		else if (param.reuse_dp == 1) {
			svm.init_dp_val(prob.l);
		}
		
		if(cross_validation != 0) {
			do_cross_validation(svm);
		}
		else {
			model = svm.svm_train(prob, param);
			svm.svm_save_model(model_file_name,model);
		}
	}

	public static void main(String argv[]) throws Exception, ClassNotFoundException
	{
		long startTime = System.currentTimeMillis();
		svm_train t = new svm_train();
		t.run(argv);
		long exeTime = System.currentTimeMillis()- startTime;
		System.out.println("Execution time " + (double)exeTime / 1000 + " s");
	}

	private static double atof(String s)
	{
		return Double.parseDouble(s);
	}

	private static int atoi(String s)
	{
		return Integer.parseInt(s);
	}

	private void parse_command_line(String argv[])
	{
		int i;
		param = new svm_parameter();
		// default values
		param.svm_type = svm_parameter.C_SVC;
		param.kernel_type = svm_parameter.RBF;
		param.degree = 3;
		param.gamma = 0;	// 1/num_features
		param.coef0 = 0;
		param.nu = 0.5;
		param.cache_size = 100;
		param.C = 1;
		param.eps = 1e-3;
		param.p = 0.1;
		param.shrinking = 1;
		param.probability = 0;
		param.nr_weight = 0;
		param.weight_label = new int[0];
		param.weight = new double[0];
		param.problem_type = 0; // sparse
		param.reuse_dp = 0; //don't save
		cross_validation = 0;
		param.print_func = null;	// default printing to stdout
		param.precompute_kernel = 0; //don't precompute

		// parse options
		for(i=0;i<argv.length;i++)
		{
			if(argv[i].charAt(0) != '-') break;
			if(++i>=argv.length)
				exit_with_help();
			switch(argv[i-1].charAt(1))
			{
				case 's':
					param.svm_type = atoi(argv[i]);
					break;
				case 't':
					param.kernel_type = atoi(argv[i]);
					break;
				case 'd':
					param.degree = atoi(argv[i]);
					break;
				case 'g':
					param.gamma = atof(argv[i]);
					break;
				case 'r':
					param.coef0 = atof(argv[i]);
					break;
				case 'n':
					param.nu = atof(argv[i]);
					break;
				case 'm':
					param.cache_size = atof(argv[i]);
					break;
				case 'c':
					param.C = atof(argv[i]);
					break;
				case 'e':
					param.eps = atof(argv[i]);
					break;
				case 'p':
					param.p = atof(argv[i]);
					break;
				case 'h':
					param.shrinking = atoi(argv[i]);
					break;
				case 'b':
					param.probability = atoi(argv[i]);
					break;
				case 'q':
					param.print_func = svm_print_null;
					i--;
					break;
				case 'v':
					cross_validation = 1;
					nr_fold = atoi(argv[i]);
					if(nr_fold < 2)
					{
						System.err.print("n-fold cross validation: n must >= 2\n");
						exit_with_help();
					}
					break;
				case 'w':
					++param.nr_weight;
					{
						int[] old = param.weight_label;
						param.weight_label = new int[param.nr_weight];
						System.arraycopy(old,0,param.weight_label,0,param.nr_weight-1);
					}

					{
						double[] old = param.weight;
						param.weight = new double[param.nr_weight];
						System.arraycopy(old,0,param.weight,0,param.nr_weight-1);
					}

					param.weight_label[param.nr_weight-1] = atoi(argv[i-1].substring(2));
					param.weight[param.nr_weight-1] = atof(argv[i]);
					break;
				case 'f':
					param.problem_type = atoi(argv[i]);
					if (param.problem_type != 0 && param.problem_type != 1)
						exit_with_help();
					break;
				case 'u':
					param.reuse_dp = atoi(argv[i]);
					if (param.reuse_dp != 0 && param.reuse_dp != 1)
						exit_with_help();
					break;
				case 'o':
					param.precompute_kernel = 1;
					break;
				default:
					System.err.print("Unknown option: " + argv[i-1] + "\n");
					exit_with_help();
			}
			if (param.kernel_type == 4) {
				param.problem_type = 1;
			}
		}
		
		// determine filenames

		if(i>=argv.length)
			exit_with_help();

		input_file_name = argv[i];

		if(i<argv.length-1)
			model_file_name = argv[i+1];
		else
		{
			int p = argv[i].lastIndexOf('/');
			++p;	// whew...
			model_file_name = argv[i].substring(p)+".model";
		}
	}
}
