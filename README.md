# Instructions

* Git clone, build the project
```
git clone the project
cd jcommander-kubernetes
mvn clean install
```

* Start minishift and give more rights to theadmin user
```
minishift start
oc login -u system:admin
oc adm policy add-cluster-role-to-user cluster-role to admin
oc login -u admin -p admin 
```

* Create a namespace `test` using the user `test`
```
oc login -u test -p test -n test
```

* Next, test the jcommander using one of these command 

* Connect with the username and password
```
mvn clean compile exec:java -Dexec.args="--url https://192.168.64.25:8443 --namespace test --user test --password test"
``` 
* Connect using the access token (= oc whoami -t)
``` 
mvn clean compile exec:java -Dexec.args="--url https://192.168.64.25:8443 --namespace test --token DexGhitaeWIkH_DTlJMCHFWwRRkUPsCE0QsSuU9U8n8"
```

## Using the command line tool
```
java -jar target/openshift-client-1.0-SNAPSHOT.jar  \
            --url https://192.168.64.25:8443 \
            --namespace default \
            --user admin  \
            --password admin  \
            --cmd 'get pods' 
```

### List the pods

```
java -jar target/openshift-client-1.0-SNAPSHOT.jar  \
            --url https://192.168.64.25:8443 \
            --namespace default \
            --user admin  \
            --password admin  \
            --cmd 'get pods' 
               
2017-03-23 18:29:48 INFO  AbstractCommand:93 - Username  : admin
2017-03-23 18:29:48 INFO  AbstractCommand:93 - Namespace : default
2017-03-23 18:29:48 INFO  AbstractCommand:93 - Master URL : https://192.168.64.25:8443/
2017-03-23 18:29:48 INFO  AbstractCommand:93 - ==========================
2017-03-23 18:29:48 INFO  AbstractCommand:93 - ============ Pods ===========
2017-03-23 18:29:48 INFO  AbstractCommand:93 - Pod : docker-registry-1-1sqio, Status : Running, IP : 172.17.0.2
2017-03-23 18:29:48 INFO  AbstractCommand:93 - Pod : router-1-x3vme, Status : Running, IP : 192.168.64.25

```

### List the Services
```
java -jar target/openshift-client-1.0-SNAPSHOT.jar  \
            --url https://192.168.64.25:8443 \
            --namespace default \
            --user admin  \
            --password admin  \
            --cmd 'get services'
             
2017-03-23 18:29:29 INFO  AbstractCommand:93 - Username  : admin
2017-03-23 18:29:29 INFO  AbstractCommand:93 - Namespace : default
2017-03-23 18:29:29 INFO  AbstractCommand:93 - Master URL : https://192.168.64.25:8443/
2017-03-23 18:29:29 INFO  AbstractCommand:93 - ==========================
2017-03-23 18:29:30 INFO  AbstractCommand:93 - ============ Services ===========
2017-03-23 18:29:30 INFO  AbstractCommand:93 - Service : docker-registry, Cluster IP : 172.30.241.217, Port if : 5000-tcp
2017-03-23 18:29:30 INFO  AbstractCommand:93 - Service : kubernetes, Cluster IP : 172.30.0.1, Port if : https
2017-03-23 18:29:30 INFO  AbstractCommand:93 - Service : router, Cluster IP : 172.30.49.159, Port if : 80-tcp

```

* Debug
```
java -agentlib:jdwp=transport=dt_socket,address=5005,server=y,suspend=y -jar target/openshift-client-1.0-SNAPSHOT.jar --url https://192.168.64.25:8443 --namespace test --user test --password test --cmd 'get pods'
```

## TODO

- Format List result as a [Table](https://www.ksmpartners.com/2013/08/nicely-formatted-tabular-output-in-java/) or [Table Formater](https://github.com/iNamik/java_text_tables)


