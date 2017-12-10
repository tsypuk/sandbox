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

### Avro
```
java -jar tools/avro-tools-1.8.2.jar
```
```
Version 1.8.2
 of Apache Avro
Copyright 2010-2015 The Apache Software Foundation

This product includes software developed at
The Apache Software Foundation (http://www.apache.org/).
----------------
Available tools:
          cat  extracts samples from files
      compile  Generates Java code for the given schema.
       concat  Concatenates avro files without re-compressing.
   fragtojson  Renders a binary-encoded Avro datum as JSON.
     fromjson  Reads JSON records and writes an Avro data file.
     fromtext  Imports a text file into an avro data file.
      getmeta  Prints out the metadata of an Avro data file.
    getschema  Prints out schema of an Avro data file.
          idl  Generates a JSON schema from an Avro IDL file
 idl2schemata  Extract JSON schemata of the types from an Avro IDL file
       induce  Induce schema/protocol from Java class/interface via reflection.
   jsontofrag  Renders a JSON-encoded Avro datum as binary.
       random  Creates a file with randomly generated instances of a schema.
      recodec  Alters the codec of a data file.
       repair  Recovers data from a corrupt Avro Data file
  rpcprotocol  Output the protocol of a RPC service
   rpcreceive  Opens an RPC Server and listens for one message.
      rpcsend  Sends a single RPC message.
       tether  Run a tethered mapreduce job.
       tojson  Dumps an Avro data file as JSON, record per line or pretty.
       totext  Converts an Avro data file to a text file.
     totrevni  Converts an Avro data file to a Trevni file.
  trevni_meta  Dumps a Trevni file's metadata as JSON.
trevni_random  Create a Trevni file filled with random instances of a schema.
trevni_tojson  Dumps a Trevni file as JSON.
```

```
java -jar tools/avro-tools-1.8.2.jar tojson /tmp/results/avro/part-r-00000.avro
```
```
{"year":2017,"temperature":49,"stationId":"home"}

```

```
java -jar tools/avro-tools-1.8.2.jar getschema /tmp/results/avro/part-r-00000.avro
```
```
{
  "type" : "record",
  "name" : "WeatherRecord",
  "doc" : "A weather reading.",
  "fields" : [ {
    "name" : "year",
    "type" : "int"
  }, {
    "name" : "temperature",
    "type" : "int"
  }, {
    "name" : "stationId",
    "type" : "string"
  } ]
}

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