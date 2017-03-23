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
               
2017-03-23 19:11:56 INFO  AbstractCommand:111 - Username  : admin
2017-03-23 19:11:56 INFO  AbstractCommand:111 - Namespace : secure
2017-03-23 19:11:56 INFO  AbstractCommand:111 - Master URL : https://api.engint.openshift.com/
2017-03-23 19:11:56 INFO  AbstractCommand:111 - ==========================
2017-03-23 19:11:57 INFO  AbstractCommand:111 - Pods
            
NAME             STATUS    IP         
====             ======    ==         
eap-app-1-build  Succeeded 10.1.1.76  
eap-app-1-deploy Failed    10.1.7.132 
sso-1-deploy     Failed    10.1.4.186 
tomcat-1-40who   Running   10.1.7.141 
tomcat-1-build   Succeeded 10.1.7.140 

```

### List the Services
```
java -jar target/openshift-client-1.0-SNAPSHOT.jar  \
            --url https://192.168.64.25:8443 \
            --namespace default \
            --user admin  \
            --password admin  \
            --cmd 'get services'
             
2017-03-23 19:15:01 INFO  AbstractCommand:115 - Username  : admin
2017-03-23 19:15:01 INFO  AbstractCommand:115 - Namespace : secure
2017-03-23 19:15:01 INFO  AbstractCommand:115 - Master URL : https://api.engint.openshift.com/
2017-03-23 19:15:01 INFO  AbstractCommand:115 - ==========================
2017-03-23 19:15:02 INFO  AbstractCommand:115 - Services
      
NAME           CLUSTER-IP     PORT     
====           ==========     ====     
eap-app        172.30.159.138          
secure-eap-app 172.30.251.145          
secure-sso     172.30.142.101          
sso            172.30.175.184          
tomcat         172.30.55.220  8080-tcp 

```

### List the Routes
```
java -jar target/openshift-client-1.0-SNAPSHOT.jar  \
            --url https://192.168.64.25:8443 \
            --namespace default \
            --user admin  \
            --password admin  \
            --cmd 'get routes'
            
2017-03-23 19:38:44 INFO  AbstractCommand:122 - Username  : admin
2017-03-23 19:38:44 INFO  AbstractCommand:122 - Namespace : myproject
2017-03-23 19:38:44 INFO  AbstractCommand:122 - Master URL : https://192.168.64.25:8443/
2017-03-23 19:38:44 INFO  AbstractCommand:122 - ==========================
2017-03-23 19:38:45 INFO  AbstractCommand:122 - Routes
 
NAME    HOST/PORT                         SERVICES PORT     
====    =========                         ======== ====     
wf      wf-myproject.192.168.64.25.nip.io wf       8080-tcp 
```            

### Token based with OpenShift Online
```
java -jar target/openshift-client-1.0-SNAPSHOT.jar  \
>             --url https://api.engint.openshift.com \
>             --namespace secure \
>             --token Xral1af_SRe01FYjZtx66wT6sGMsMQjg1oEg7anlW0E \
>             --cmd 'get pods'
2017-03-23 18:55:25 INFO  AbstractCommand:93 - Username  : admin
2017-03-23 18:55:26 INFO  AbstractCommand:93 - Namespace : secure
2017-03-23 18:55:26 INFO  AbstractCommand:93 - Master URL : https://api.engint.openshift.com/
2017-03-23 18:55:26 INFO  AbstractCommand:93 - ==========================
2017-03-23 18:55:27 INFO  AbstractCommand:93 - ============ Pods ===========
2017-03-23 18:55:27 INFO  AbstractCommand:93 - Pod : eap-app-1-build, Status : Succeeded, IP : 10.1.1.76
2017-03-23 18:55:27 INFO  AbstractCommand:93 - Pod : eap-app-1-deploy, Status : Failed, IP : 10.1.7.132
2017-03-23 18:55:27 INFO  AbstractCommand:93 - Pod : sso-1-deploy, Status : Failed, IP : 10.1.4.186
2017-03-23 18:55:27 INFO  AbstractCommand:93 - Pod : tomcat-1-40who, Status : Running, IP : 10.1.7.141
2017-03-23 18:55:27 INFO  AbstractCommand:93 - Pod : tomcat-1-build, Status : Succeeded, IP : 10.1.7.140

```

* Debug
```
java -agentlib:jdwp=transport=dt_socket,address=5005,server=y,suspend=y -jar target/openshift-client-1.0-SNAPSHOT.jar --url https://192.168.64.25:8443 --namespace test --user test --password test --cmd 'get pods'
```

## TODO

- Format List result as a [Table](https://www.ksmpartners.com/2013/08/nicely-formatted-tabular-output-in-java/) or [Table Formater](https://github.com/iNamik/java_text_tables)


