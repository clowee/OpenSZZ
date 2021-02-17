# OpenSZZ
OpenSZZ  is  our open source implementation of the SZZ Algorithm [1] to calculate the <i>BugInducingCommits</i> of any project using Git as versioning system and Jira as issue tracker. 
A cloud-native version is available on the [OpenSZZ-Cloud-Native repository](https://github.com/clowee/OpenSZZ-cloud-native)

A dataset including the analysis of 33 projects, has been published in 2019 [2]. 

The current version can tag commits in github with the faults reported in Jira. 

## How to cite

Valentina Lenarduzzi, Fabio Palomba, Davide Taibi, and Damian Andrew Tamburri. 2020. OpenSZZ: A Free, Open-Source, Web-Accessible Implementation of the SZZ Algorithm. In Proceedings of the 28th International Conference on Program Comprehension (ICPC '20). DOI:https://doi.org/10.1145/3387904.3389295


## Download
Release 0.1

## Usage: 

     * szz.jar -all githubUrl jiraUrl jiraKey
     * e.g.:  java -jar openszz.jar -all https://github.com/apache/batik https://issues.apache.org/jira/projects/BATIK batik
 
The script first clones the gitHub repository, then download the Jira faults, and finally maps faults to commits. 

 

# References

[1] Jacek Śliwerski, Thomas Zimmermann, and Andreas Zeller. 2005. When do changes induce fixes?. In Proceedings of the 2005 international workshop on Mining software repositories (MSR '05). ACM, New York, NY, USA, 1-5. DOI=http://dx.doi.org/10.1145/1082983.1083147

[2] V. Lenarduzzi, N. Saarimäki, and D. Taibi,“The Technical Debt Dataset”, in The Fifteenth International Conference on Predictive Models and Data Analytics in Software Engineering (PROMISE’19), Brazil, 2019.
