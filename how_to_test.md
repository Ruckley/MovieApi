# Testing

To send requests to the api you can send them via curl like this:<br>
```curl -X POST "http://localhost:8080/movie_api/rate_movie?t=Moulin+Rouge&y=1952&r=9&apikey=myToken"```<br>

To run tests navigate to MovieApi and run:<br>
```mvn test```