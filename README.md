# sandbox
Multiproject

## avro

java -jar /path/to/avro-tools-1.8.1.jar compile schema user.avsc .    
This compiles to java source class from schema to use in code.

## elasticSearch
ElasticSearch latest image does not work with spring boot client.
https://www.docker.elastic.co/#
```docker pull docker.elastic.co/elasticsearch/elasticsearch:6.0.0```
```docker pull docker.elastic.co/elasticsearch/elasticsearch:5.2.1```
```docker run -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" docker.elastic.co/elasticsearch/elasticsearch:6.0.0```
```docker run -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" docker.elastic.co/elasticsearch/elasticsearch:5.2.1```

username:   elastic
password:   changeme


TODO
[ ] - add gradle dependency to generate class with task