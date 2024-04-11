ALTER TABLE awards ADD COLUMN year VARCHAR(4);
UPDATE awards SET year = CASE WHEN year_string NOT LIKE '%/%' THEN SUBSTRING(year_string, 1, 4) WHEN year_string LIKE '%/%' THEN CONCAT(SUBSTRING(year_string, 1, 2),SUBSTRING(year_string, 6, 2)) END;
ALTER TABLE awards DROP COLUMN year_string;
ALTER TABLE awards MODIFY COLUMN year INT NOT NULL;