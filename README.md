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
TODO
[ ] - add gradle dependency to generate class with task