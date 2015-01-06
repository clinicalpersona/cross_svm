cross_svm
=========

<<<<<<< HEAD
cross_svm unlocks and enhances the hidden (or at least less-well
known) power of libsvm library. With cross_svm, SVM cross-validation
learning is at least 10-20 times faster than the "standard" libsvm
command-line, on many dense datasets. In addition to the
cross-validation improvement, it implements additional speed-ups which
often provide 2-3 fold performance gain.


Introduction
------------

cross_svm is a faster and backward-compatible Java version of the
well-known libsvm library for Support Vector Machines. It performs
cross-validation learning much faster than libsvm - depending on the
sparsity of input data, often more than order of magnitude
faster. This is achieved by utilizing the power of the libsvm
_precomputed kernel_ command-line option _-t 4_. In this mode, libsvm
takes as input a precomputed kernel matrix instead of the input data
file. That way, there is no need for repeated computations of the
kernel matrix elements, as is otherwise done in the `regular' libsvm
command line. This may lead to dramatic performance gains for many
datasets, with the tradeoff that entire kernel matrix must fit in RAM.

The cross_svm code is based on libsvm version 3-17. The learning
algorithm is exactly the same. The speedup is achieved by keeping
kernel in RAM and rearranging data structures. Any results discrepancy
compared with libsvm-3.17 is a bug.


COPYRIGHT/LICENSE
-----------------

Please see file COPYRIGHT. It is almost identical to the original
libsvm COPYRIGHT, except that it adds Dejan Miljkovic, Ljubomir
Buturovic, Alejandrina Pattin, who contributed the performance
improvements. Almost all improvements are contained in file xsvm.java,
with small changes in svm_train.java. xsvm.java is the cross_svm version
of libsvm file svm.java, which contains the main SVM learning code.


Performance gain
----------------

Performance gains on several datasets are shown in Figs. 1-2.

CCv1.1 is cancer genomics dataset, with 4 classes, 22215 genomic
features and 484 samples.

codv1.1 is cancer genomics dataset with 2 classes, 6138 features and
1471 samples.

GSE6532 is cancer genomics dataset with 2 classes, 44754 features and
254 samples.

The other datasets are from UCI repository.

Note that cross_svm is also much faster than liblinear on the three genomic
datasets we tested. 


Compiling cross_svm
-------------------

You can use the provided cross_svm.jar if you wish to start
right-away. If you make changes and wish to rebuild, simply type:
```
$ javac *.java
$ jar cvf cross_svm.jar *.class
```

Using cross_svm
---------------

cross_svm is backward-compatible with libsvm 3-17. The only change are
two new command-line switches, -u and -f. Type
```
$ java -cp cross_svm.jar svm_train
```
to get Usage message. The new switches are:
```
-f problem_type : sparse (0) or full (1) (default 0)
-u reuse : in cross-validation, compute dot products exactly once, yes (1) or no (0) (default 0)
```
Both switches are optional.

Specifying -f 1 is useful if the full dataset (i.e., data with all
elements, not just non-zero) fits in RAM.  In our experience, in those
cases -f 1 improves performance by a factor of 2 to 3. It tells
cross_svm to use more efficient kernel computation, due to the _full_
representation of the dataset.  For compatibility with libsvm, by
default all data files are considered sparse, so you need to
explicitly state -f 1 to gain performance.

Specifying -u 1 is useful in cross-validation (-v mode). It means that
cross_svm will compute each kernel matrix element exactly once, when
needed to perform cross-validation. In contrast, libsvm computes
kernel matrix elements multiple times. -u 1 provides the bulk of
performance gains shown in the graphs below. The trade-off is that
kernel matrix must fit in the computer RAM. Given that the matrix is
symmetric, it consumes about 4\*N\*(N+1) bytes, where N is number of
samples in the training set. For example, you need about 40G RAM to
analyze a 100K-sample dataset, and about 1TB to analyze a 500K-sample
dataset. The largest dataset we analyzed had 72,309 samples.

Examples (-u 1 means use the fast cross-validation; -f 1 means convert
the input file to full format upon reading, for additional performance
gain):
```
$ java -cp cross_svm.jar svm_train -t 2 -v 10 -u 1 -f 1 codv1.1.libsvm     # 17 sec
$ java -cp libsvm.jar svm_train -t 2 -v 10 codv1.1.libsvm                # 638 sec

$ java -cp cross_svm.jar svm_train -t 2 -v 10 -u 1 -f 1 gisette_scale      # 68 sec
$ java -cp libsvm.jar svm_train -t 2 -v 10 gisette_scale                 # 2223 sec
```
Suggested workflow
------------------

As a reminder, cross_svm is useful in cross-validation mode. Therefore
this workflow assumes you are running cross-validation (-v
command-line argument).

1. Run cross_svm with -u 1 command-line argument

2. Run libsvm 

3. Run liblinear

4. Choose the fastest among cross_svm, libsvm, liblinear

5. If cross_svm is the fastest, and the _full_ data file fits in RAM
(i.e., N*f values (where f is number of features) fit in RAM),
continue running cross_svm with `-u 1 -f 1' command-line options.


Performance graphs
------------------

![alt tag](https://github.com/clinicalpersona/cross_svm/raw/master/cross_svm_performance.png)

Figure 1. cross_svm performance speedup using non-linear kernels. The
x-axis is the dataset/kernel combination, y-axis is the speedup factor
compared with libsvm. The numbers on top of bars are density of the
dataset (the proportion of non-zero elements in the data matrix). The
horizontal red line is speedup factor of 1.

![alt tag](https://github.com/clinicalpersona/cross_svm/raw/master/cross_svm_liblinear.png)

Figure 2. Speed comparison between cross_svm and liblinear. The x-axis
is the dataset, y-axis is the execution time in seconds.  The numbers
on top of bars are density of the dataset (the proportion of non-zero
elements in the data matrix).
=======
faster and simpler version of SVM learning software libsvm
>>>>>>> 7f5dd3f6c3f4b3349ce7453f1ea5f6f195a84f23
