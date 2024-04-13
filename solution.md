# Solution

This API exposes 4 routes under movie_api. Al routes need to include an apitoken to be authorised

## API Overview

### health_check 
GET<br>
A simple healthcheck route that returns ok 

### best_picture_winner
GET<br>
Takes a movie title and an optional year parameter.
Returns a json indicating if that movie won best picture and in what year.

movie title can be partial, it is searched for by %<search_term>%
if no year is given the first [MoviesService.java](src%2Fmain%2Fjava%2Forg%2Fbb%2Fapp%2Fapi%2FMoviesService.java)entry in the database with a matching name is returned

example usage: http://localhost:8080/movie_api/best_picture_winner?t=Moulin+Rouge&y=1952&apikey=myToken

### rate_movie
POST<br>
Takes a movie title and rating with an optional year parameter.<br>
Updates the movie database with the new rating and returns a json with the updates rating.<br>
Title must be exact.<br>
If the title is not found in the database the request is rejected with a not found message<br>
Movies with the same title from different years are rated separately.
If year is not given first entry in db is used (this should be corrected)

example usage: http://localhost:8080/movie_api/rate_movie?t=Moulin+Rouge&y=1952&r=9&apikey=myToken

### top_rated
GET<br>
Takes no parameters.<br>
Returns a json of the top 10 rated movies ordered by year. I chose to order by year as no box office data was given in the task csv.<br>

example usage: http://localhost:8080/movie_api/top_rated?apikey=myToken

The API is written using Springboot and Webflux and a mysql db is used for storage. I chose webflux as I wanted to attempt a non-blocking api.

## Implementation Details

### Data
All data is stored in a mysql database. Connection to this database is handled via R2DBC

The original dataset had malformed entries. Mostly unescaped commas and every entry has more columns than there are headers.
As there were a limited number of these I manually corrected them and used excel to generate the SQL requests seen in resources.db.migration.V2__insert_award_data. In a production environment I would eiher correct the application generating this data, or write a new application to clean it up.<br>
Data is inserted into the db on start via flyway migrations. The awards data is then manipulated to make a consistent year column, see resources.db.migration.V3. A movies table with rating information is created from the awards entries with "Best Picture as their category". See resources.db.migration.V4__insert_movie_data.

### Rating implementation
Rating implementation is simple. The movies table holds the movie title, the number orf ratings and the average rating.
A rate_movie request calculates the new average and increases the numRatings count. There are flaws in this implementation that are discussed in to_do.md

### API key
Api key is handled by a simple filter on all routes see ApiTokenFilter. This is a very rudimentary implementation.

### Testing
Testing is done using Test containers. All tests use a real mysql db inside a container. This is how I would prefer to write tests in a production environment as I like to avoid mocking wherever possible.<br>
In hindsight this may not have been they best way to do it for an exercise as it is hard to know the docker settings on the machine you choose to run it on. Hopefully you dont ru into any issues!