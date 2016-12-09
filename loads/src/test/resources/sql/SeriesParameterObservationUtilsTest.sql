DROP TABLE IF EXISTS time_series_observation;
CREATE TABLE time_series_observation (
	id                        INT(10) NOT NULL,
	data_point                FLOAT   NOT NULL,
	time_point                TIMESTAMP,
	discrete_point            FLOAT,

	PRIMARY KEY(id)

);


DROP TABLE IF EXISTS datetime_observation;
CREATE TABLE datetime_observation (
	id                        INT(10) NOT NULL,
	datetime_point            datetime,

	PRIMARY KEY(id)

);