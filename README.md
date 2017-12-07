# sandbox
Multiproject

## avro

java -jar /path/to/avro-tools-1.8.1.jar compile schema user.avsc .    
This compiles to java source class from schema to use in code.

## elasticSearch
ElasticSearch latest image does not work with spring boot client.
https://www.docker.elastic.co/#
```docker pull docker.elastic.co/elasticsearch/elasticsearch:6.0.0```
```docker pull docker.elastic.co/elasticsearch/elasticsearch:5.5.0```
```docker pull docker.elastic.co/elasticsearch/elasticsearch:5.2.1```
```docker pull docker.elastic.co/elasticsearch/elasticsearch:2.4-alpine```
```docker run -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" docker.elastic.co/elasticsearch/elasticsearch:6.0.0```
```docker run -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" docker.elastic.co/elasticsearch/elasticsearch:5.5.0```
```docker run -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" docker.elastic.co/elasticsearch/elasticsearch:5.2.1```
```docker run -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" docker.elastic.co/elasticsearch/elasticsearch:2.4-alpine```

username:   elastic
password:   changeme

## zookeeper
```
ssh to zookeeper instance

start client
zookeeper-client


[zkshell: 8] ls /
[zookeeper]

[zkshell: 9] create /zk_test my_data
Created /zk_test

[zkshell: 12] get /zk_test
my_data
cZxid = 5
ctime = Fri Jun 05 13:57:06 PDT 2009
mZxid = 5
mtime = Fri Jun 05 13:57:06 PDT 2009
pZxid = 5
cversion = 0
dataVersion = 0
aclVersion = 0
ephemeralOwner = 0
dataLength = 7
numChildren = 0

[zkshell: 14] set /zk_test junk

[zkshell: 16] delete /zk_test
[zkshell: 17] ls /
[zookeeper]
[zkshell: 18]
```

## hadoop

### inside cluster
```
ssh root@clouder-1
sudo -u hdfs hadoop fs -mkdir /user/root
```
change permissions:
```
sudo -u hdfs hadoop fs -chown root /user/root
hadoop namenode -format
```
### from client machine
```
export HADOOP_USER_NAME=hdfs

hadoop fs -mkdir hdfs://cloudera-1:8020/user/root/
hadoop fs -ls hdfs://cloudera-1:8020/user/root/
hadoop fs -chown root hdfs://cloudera-1:8020/user/root/
/user/hdfs/.Trash/Current/user/root/doyle1512506113907
hadoop fs -cat hdfs://cloudera-1:8020/user/root/pg1661.txt
echo "Line-to-add" | hadoop hdfs dfs -appendToFile hdfs://cloudera-1:8020/user/root/doyle
```
### Compression codecs
After running job with compression view the results
```
gunzip -c part-00000.gz| less
```
### Sequence
```
hadoop fs -text numbers.seq | head
hadoop fs -text hdfs://cloudera-1:8020/user/root/sequence.seq
```

## Hive
dataset:
```
https://raw.githubusercontent.com/hortonworks/data-tutorials/master/tutorials/hdp/interactive-query-for-hadoop-with-apache-hive-on-apache-tez/assets/driver_data.zip
```
create Hive tables:
```
create table drivers
(driverId int,
 name string,
 ssn bigint,
 location string,
 certified string,
 wageplan string)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY ','
STORED AS TEXTFILE
TBLPROPERTIES("skip.header.line.count"="1");
```
TODO
[ ] - add gradle dependency to generate class with task