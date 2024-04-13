# How to Run

To run this api will need java, maven, docker desktop and mysql installed. (It can run with a basic docker installation not docker desktop but this would make the configuration on your machine more difficult)
To run:<br>

Start Docker desktop and wait for the engine to be up.<br>
If you dont want to install docker desktop make sure your docker engine is started. you may have to run <br>
```sudo chmod 666 /var/run/docker.sock```

Edit the Makefile in the MovieApi project to set your mysql user and password. Then run<br>
```make all```<br>
you may have to enter your password

The Makefile will:<br>
1. start mysql
2. put your username and password in a file so they arnt printed to the console
3. create the movie_db database
4. package the api jar
5. run the jar


If you run into any issues due to docker you can run<br>
```make all_no_tests```<br>
To run the api without running the tests.

you can run<br>
```make clean```<br>
to drop the database and delete the file with your mysql credentials