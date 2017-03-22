# Instructions

git clone the project

start minishift

add cluster role to admin user 

and next run this command 

mvn clean compile exec:java -Plogin -Docp.masterurl=https://192.168.64.25:8443 -Docp.namespace=test -Docp.user=admin -Docp.password=admin
 
mvn clean compile exec:java -Ptoken -Docp.masterurl=https://192.168.64.25:8443 -Docp.namespace=test -Docp.token=m30C_AdfgoCGqykkaZbvMHbDp4gZDRSERxk17foxywg 


