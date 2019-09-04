# OpenSZZ
OpenSZZ  is  our open source implementation of the SZZ Algorithm [1] to calculate the <i>BugInducingCommits</i> of any project using Git as versioning system and Jira as issue tracker. 
A cloud-native version is available on the [OpenSZZ-Cloud-Native repository](https://github.com/clowee/OpenSZZ-cloud-native)

A dataset including the analysis of 33 projects, has been published in 2019 [2]. 

The current version can tag commits in github with the faults reported in Jira. 


## Download
Release 0.1

## Usage: 
1. Clone the GitHub Repository 

2. Download the faults from Jira: 
     
     szz.jar -d jiraUrtl jiraKey
     
     eg: szz.jar https://issues.apache.org/jira AMBARI
     
     The script saves the file faults.csv containing the issues reported in Jira  

3. Save gitLogsMap 
     
     szz.jar -l gitRepositoryPath
   
     e.g. szz.jar -l ./projects/ambari 
      
     This script saves the file gitlog.csv containing the parsed gitlog with all the information needed to execute szz

4. Map Faults to commits
      
      szz.jar -m jiraKey
     
     the script takes in input the files generated before (faults.csv and gitlog.csv) and generate the final result in the file FaultInducingCommits.csv
      
      
## Usage [alternative]:
This command executes all the steps at once: 
* szz.jar -all githubUrl jiraUrl jiraKey
 
The script first clones the gitHub repository, then download the Jira faults, and finally maps faults to commits. 

 

# References

[1] Jacek Śliwerski, Thomas Zimmermann, and Andreas Zeller. 2005. When do changes induce fixes?. In Proceedings of the 2005 international workshop on Mining software repositories (MSR '05). ACM, New York, NY, USA, 1-5. DOI=http://dx.doi.org/10.1145/1082983.1083147

[2] V. Lenarduzzi, N. Saarimäki, and D. Taibi,“The Technical Debt Dataset”, in The Fifteenth International Conference on Predictive Models and Data Analytics in Software Engineering (PROMISE’19), Brazil, 2019.
