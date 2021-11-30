# Bayesian-Network-Project
### This project is implementing Bayesian Network, Bayes Ball algorithm and Variable Elimination Algorithm.

## 1. Bayesian Network:
A Bayesian network (also known as a Bayes network, Bayes net, belief network, or decision network) is a probabilistic graphical model that represents a set of variables and their conditional dependencies via a directed acyclic graph (DAG). Bayesian networks are ideal for taking an event that occurred and predicting the likelihood that any one of several possible known causes was the contributing factor. For example, a Bayesian network could represent the probabilistic relationships between diseases and symptoms. Given symptoms, the network can be used to compute the probabilities of the presence of various diseases.
(wikipedia)

## 2. Bayes Ball Algorithm
Link to the article of _Ross D. Shachter_ which created the algorithm: https://arxiv.org/ftp/arxiv/papers/1301/1301.7412.pdf
</br>
Goal: We wish to determine whether a given conditional statement such as XA q XB | XC
is true given a directed graph.
The algorithm is as follows:
1. Shade nodes, XC, that are conditioned on.
2. If _there is not_ a path between XA and XB, then the nodes XA and XB must be _conditionally independent_.
3. If _there is_ a path between XA and XB, then the nodes XA and XB are _conditionally dependent_.

##### the idea for implementing Bayes Ball Algorithm:
<img src="https://user-images.githubusercontent.com/79406881/144015549-1545f298-e61d-44f4-ad71-cc76f41790ff.png" width="600" height="400">

##### example:
<img src="https://user-images.githubusercontent.com/79406881/144015833-acf0bfb0-acba-4d37-b7d5-12bd116ca99a.png" width="600" height="300">

## 3. Variable elimination
Variable elimination (VE) is a simple and general exact inference algorithm in probabilistic graphical models, such as Bayesian networks and Markov random fields.[1] It can be used for inference of maximum a posteriori (MAP) state or estimation of conditional or marginal distributions over a subset of variables. The algorithm has exponential time complexity, but could be efficient in practice for the low-treewidth graphs, if the proper elimination order is used. (wikipedia)
##### 
It is called Variable Elimination because it eliminates one by one those
variables which are irrelevant for the query.
- It relies on some basic operations on a class of functions known as
factors.
- It uses an algorithmic technique called dynamic programming
##### Variable elimination process:
We would like to compute: P(Q|E1=e1,...,Ek=ek)

* Start with initial factors 
  * local CPTs instantiated by evidence 
  * If an instantiated CPT becomes one-valued, discard the factor 
* While there are still hidden variables (not Q or evidence): 
  * Pick a hidden variable H 
  * Join all factors mentioning H 
  * Eliminate (sum out) H 
  * If the factor becomes one-valued, discard the factor           
* Join all remaining factors and normalize 

#### there are 3 operations in this process: Join Factors, Eliminate, Normalize

#### A. JOIN:
* Get all factors over the joining variable
* Build a new factor over the union of the variables

#### B. ELIMINATE 
* Take a factor and sum out a variable - marginalization
* Shrinks a factor to a smaller one

#### C. NORMALIZE
* Take every probability in the last factor and divide it with the sum of all probabilities in the factor (including the one we are dividing).
* The answer we are looking for is the probability of the query value.

##### examples of Variable Eliminate process:
![image](https://user-images.githubusercontent.com/79406881/144024872-4fbae8fb-539f-4380-ab61-8bc64cbbc238.png)
* __our factors:__</br>

![image](https://user-images.githubusercontent.com/79406881/144025097-755fbf91-aaff-4d91-8861-01ecafc7929a.png)  ![image](https://user-images.githubusercontent.com/79406881/144025140-c9a7efd3-cc68-467b-a9f2-aa41783d3cf6.png)
</br>
* __Join A:__</br>
![image](https://user-images.githubusercontent.com/79406881/144023118-85b31cfd-0d6d-4672-acaf-89fd435804a2.png)
![image](https://user-images.githubusercontent.com/79406881/144023153-d3daf34c-fa46-4d7c-9015-6675424358e5.png)

</br>

* __Eliminate A:__</br>

![image](https://user-images.githubusercontent.com/79406881/144023322-a1e8d23a-89b5-4dd8-8c39-8eb78843618e.png)

</br>

* __Join and Eliminate E:__</br>

![image](https://user-images.githubusercontent.com/79406881/144023656-09ab381b-e12d-468b-9252-b45169b88eaa.png)

</br>

* __Join and Normalize B (the Qeury variable):__</br>
![image](https://user-images.githubusercontent.com/79406881/144023779-1bfe4efa-4efe-4e23-b518-ac64d6adf7ea.png)


* and one more example of (complex) join:
![image](https://user-images.githubusercontent.com/79406881/144023905-5300d864-86d1-47eb-af24-0360c119e91c.png)



