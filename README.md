# Bayesian-Network-Project
### This project is implementing Bayesian Network, Bayes Ball algorithm and Variable Elimination Algorithm.

## Bayesian Network:
A Bayesian network (also known as a Bayes network, Bayes net, belief network, or decision network) is a probabilistic graphical model that represents a set of variables and their conditional dependencies via a directed acyclic graph (DAG). Bayesian networks are ideal for taking an event that occurred and predicting the likelihood that any one of several possible known causes was the contributing factor. For example, a Bayesian network could represent the probabilistic relationships between diseases and symptoms. Given symptoms, the network can be used to compute the probabilities of the presence of various diseases.
(wikipedia)

## Bayes Ball Algorithm
Goal: We wish to determine whether a given conditional statement such as XA q XB | XC
is true given a directed graph.
The algorithm is as follows:
1. Shade nodes, XC, that are conditioned on.
2. If _there is not_ a path between XA and XB, then the nodes XA and XB must be _conditionally independent_.
3. If _there is_ a path between XA and XB, then the nodes XA and XB are _conditionally dependent_.
Link to the article of _Ross D. Shachter_ which created the algorithm: https://arxiv.org/ftp/arxiv/papers/1301/1301.7412.pdf
- the idea:
![image](https://user-images.githubusercontent.com/79406881/144015549-1545f298-e61d-44f4-ad71-cc76f41790ff.png)
- example:
![image](https://user-images.githubusercontent.com/79406881/144015833-acf0bfb0-acba-4d37-b7d5-12bd116ca99a.png)

## 
