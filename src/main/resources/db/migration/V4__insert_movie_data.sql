INSERT INTO movies (title, year, num_ratings, av_rating)
SELECT nominee, year, 0, NULL
FROM awards
WHERE category = 'Best Picture';