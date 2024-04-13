# Scaling

This API is non blocking and should be able to handle many users. However if the number of users grows to thousands or more it will need work to scale:

Containerisation. The API should be containerised and many instances made available on multiple machines. This would normally be handled by kubernetes for docker containers.<br>
Kubernetes would be able to provide load balancing.
The data for this API is not too extensive is small and will not grow quickly so sharding is probably not necessary but any solution will need the db to be abe to handle access from all container instances concurrently.

The issues with th rate_movie route will become more prevent here.
A different solution could be used:<br>
All ratings are recorded in a ratings db.
A blocking chron job runs that calculates the average rating for each movie on an hourly/daily basis.
This way race conditions on updates for the same movie will not be an issue.

