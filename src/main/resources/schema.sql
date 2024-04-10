CREATE DATABASE IF NOT EXISTS movie_db;
CREATE TABLE IF NOT EXISTS awards (id INT AUTO_INCREMENT PRIMARY KEY, year_string VARCHAR(20), category VARCHAR(255), nominee VARCHAR(255),additional_info TEXT,won BOOLEAN);

SET GLOBAL local_infile = 'ON';
LOAD DATA LOCAL INFILE 'academy_awards_clean' INTO TABLE awards FIELDS TERMINATED BY ',' ENCLOSED BY '"' LINES TERMINATED BY '\n' IGNORE 1 ROWS (year_string, category, nominee, additional_info, won);

ALTER TABLE awards ADD COLUMN year VARCHAR(4);
UPDATE awards SET year = CASE WHEN year_string NOT LIKE '%/%' THEN SUBSTRING(year_string, 1, 4) WHEN year_string LIKE '%/%' THEN CONCAT(SUBSTRING(year_string, 1, 2),SUBSTRING(year_string, 6, 2)) END;
ALTER TABLE awards DROP COLUMN year_string;
ALTER TABLE awards MODIFY COLUMN year INT;