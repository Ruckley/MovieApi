# Improvements

### API key implementation
Currently the api key is just a hardcoded value. There should be a process for generating keys that are stored, encrypted, in a db.
health_check route should not need api key.

### ratings implementation
There may be a race condition issue with this implementation if 2 requests update the same movie rating at the same time.
This could be corected my making this route optionally blocking
Another solution would be to create a table that records each rating as an entry in a new table and the average is calculated from this table at regular intervals.

### Tests
The tests could do with a refactor and are far from extensive. I did not have the time to write a full suite.
All tests are essentially integration tests that run api routes. To test a normal api I might make service level unit tests for mre complex logic. However apart from the rate_movie logic all logic here is sql operations.
The tests currently dont exit automatically. I believ this is due to the webflux client but unfortunately I dont have time to debug.

### Containerisation
I would have prefered to create a cokder container you could ru this in so three wont be any configuration issues but unfortunate I dont have the time.

