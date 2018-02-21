1) Import the "WordCount" project as "Existing Maven Project" in Eclipse EE.

2) Build the Project by right clicking on pom.xml and then Run As -> Maven build. If
it asks for "goal", enter "package".

3) If it builds successfully, you will get a jar file in the "target" folder of your project. You can
then upload it to the cluster using either a FTP client or scp command or simply copy and paste the 
jar file in cluster with help of eclipse's Remote System Explorer. 
(i.e., copy the file from Local to Remote Connection (ex:- csgrads1.utdallas.edu or cs6360.utdallas.edu))

4) Run the jar file on the cluster by the following command:

hadoop jar WordCount.jar WordCount.WordCount.WordCount hdfs://cshadoop1/user/<NetID (vxg161330 in our case)>/Assignment1/Part1/ hdfs://cshadoop1/user/<NetID>/Assignment1b/Part1/

Our assignment1 part1 output is in hdfs://cshadoop1/user/vxg161330/Assignment1/Part1/

5) Enter the following command "hdfs dfs -ls /user/<NetID>/Assignment1b/Part1 " to see the output files on hadoop cluster.

6) Enter the following command "hdfs dfs -cat /user/<NetID>/Assignment1b/Part1/part-r-00000 " to see the word count.