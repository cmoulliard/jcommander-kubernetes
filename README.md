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

* Next, test the jcommander using one of these command where you will change the parameters 

* Connect with the username and password
```
mvn clean compile exec:java -Dexec.args="--url https://192.168.64.25:8443 --namespace test --user test --password test"
``` 
* Connect using the access token (= oc whoami -t)
``` 
mvn clean compile exec:java -Dexec.args="--url https://192.168.64.25:8443 --namespace test --token=m30C_AdfgoCGqykkaZbvMHbDp4gZDRSERxk17foxywg 
```

