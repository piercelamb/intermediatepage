(this code depends on a local cassandra instance running and a local postgres instance running called "my-playful-prelaunch-development" I'm not sure if it will run otherwise)"

#To run this code:

##a.) If you have activator installed:

```1.) git clone to the dir of your choice.

2.) navigate to that dir and type "activator run"

3.) open a browser and go to http://localhost:9000 (note this will begin the compilation process so could take a bit)

(I'm assuming sbt will download all the necessary dependencies, if it doesn't try "activator clean compile" then "activator run")```

##b.) If activator isnt installed:

1.) Download activator: https://typesafe.com/get-started
2.) add activator to your PATH: export PATH=$PATH:/relativePath/to/activator
3.) refer to a.)

##To run cassandra locally:
1.) navigate to cassandra dir.
2.) Type sudo ./bin/cassandra -f

##To run postgres locally:
1.) sudo apt-get update/sudo apt-get install postgresql postgresql-contrib
2.) login as sudo -i -u postgres (this is the default superuser)
3.) Must create new ubuntu user named after your desired DB name "sudo adduser my-playful-prelaunch-development"
4.) load psql (postgres shell) ./bin/psql
5.) list users using "\du" command, make sure "my-playful-prelaunch-development" is a super user if it isn't:
6.) createuser my-playful-prelaunch-development (make sure to make this user a superuser)
7.) createdb my-playful-prelaunch-development
8.) you then have to edit postgres's hba_conf file and change the host all all 127.0.0.1/32 md5 line to host all all 127.0.0.1/32 trust; this removes all need for authentication


Currently depends on:

Scala 2.11.4
Play 2.3.0
postgresql 9.3
selenium 2.44.0
cassandra 2.1.4
play2-auth 0.13.0
jbcrypt 0.3m
scalikejdbc 2.2.0




