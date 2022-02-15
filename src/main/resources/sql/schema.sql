CREATE TABLE IF NOT EXISTS users (
  id VARCHAR(60)  DEFAULT RANDOM_UUID() PRIMARY KEY,
  username   VARCHAR(60)  NOT NULL,
  password   VARCHAR(60)      NOT NULL,
  deposit    DECIMAL     NOT NULL,
  role    CHAR(20)   NOT NULL
);