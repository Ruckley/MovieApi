# Assumptions

The exercised asked for the top ratings rote to return the top rated ordered by box office value, however, this information was no provided in the CSV so I ordered by year to prove how I would do it (with a sql command  rahter than in code). Apologies if I missed something.

I issued it was ok to hardcode the awards data into a migration file, I originally tried inserting directly from the CSV but ran into msql configuration issues. This solution seemed mor stable.

The instructions said I should base the api of the OMDB API. This is why I have parameters set in the url. Normally I would prefer to have them set in a json in the body of the request for back end APIS.

Any data in the csv that didnt have a rating is insertd into the table with a null won column and the api will return unknown if requested.

I could have removed any award entry which wasnt n the Best picture category but decided to keep it to allow for future features.

I originally planned to use hibernate but learned that it is blocking by nature. I was excited to try webflux so I decided to leave it out. I believe there is a reactive hibernate library I might try in future.