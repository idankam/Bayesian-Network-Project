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
2. If the ball _cannot_ reach XB, then the nodes XA and XB must be conditionally independent.
3. If the ball can reach XB, then the nodes XA and XB are conditionally dependent.
