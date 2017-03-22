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

