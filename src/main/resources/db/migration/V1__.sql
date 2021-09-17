CREATE TABLE apartment
(
    id           BIGINT       NOT NULL      AUTO_INCREMENT,
    address      VARCHAR(255) NULL,
    square_value DOUBLE       NULL,
    CONSTRAINT pk_apartment PRIMARY KEY (id)
);

CREATE TABLE jobs
(
    id  BIGINT       NOT NULL       AUTO_INCREMENT,
    job VARCHAR(255) NULL,
    CONSTRAINT pk_jobs PRIMARY KEY (id)
);

CREATE TABLE users
(
    id            BIGINT       NOT NULL     AUTO_INCREMENT,
    name          VARCHAR(255) NULL,
    age           INT          NULL,
    apartments_id BIGINT       NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

ALTER TABLE users
    ADD CONSTRAINT FK_USERS_ON_APARTMENTS FOREIGN KEY (apartments_id) REFERENCES apartment (id);

CREATE TABLE users_jobs
(
    user_id BIGINT NOT NULL,
    job_id  BIGINT NOT NULL
);

ALTER TABLE users_jobs
    ADD CONSTRAINT FK_USERS_TO_JOBS FOREIGN KEY (job_id) REFERENCES jobs (id);
ALTER TABLE users_jobs
    ADD CONSTRAINT FK_JOBS_TO_USERS FOREIGN KEY (user_id) REFERENCES users (id);

CREATE TABLE cars
(
    id      BIGINT       NOT NULL       AUTO_INCREMENT,
    model   VARCHAR(255)  NOT NULL,
    number   VARCHAR(255) NOT NULL,
    user_id BIGINT       NOT NULL,
    CONSTRAINT pk_cars PRIMARY KEY (id)
);

ALTER TABLE cars
    ADD CONSTRAINT FK_CARS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);
