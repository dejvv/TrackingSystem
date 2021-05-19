CREATE DATABASE tracking;
CREATE USER trackinguser WITH ENCRYPTED PASSWORD 'NgR7151Yt2nH';
CREATE ROLE trackinguser WITH PASSWORD 'NgR7151Yt2nH' LOGIN;
GRANT ALL PRIVILEGES ON DATABASE tracking TO trackinguser;

CREATE TABLE IF NOT EXISTS Account(
	accountId bigint, 
	accountName varchar(255) not null,
	isActive boolean not null default false,
	PRIMARY KEY(accountId)
);

ALTER TABLE Account OWNER TO trackinguser;

INSERT INTO Account(accountId, accountName, isActive)
VALUES(8401212908335, 'tester zero', TRUE),  
(1923909481023, 'tester one', FALSE),
(4968374182321, 'tester two', TRUE),
(5874593749372, 'tester three', TRUE),
(4208390509000, 'tester four', TRUE),
(9875259730498, 'tester five', FALSE),
(1892739187548, 'tester six', FALSE),
(8927392879822, 'tester seven', TRUE),
(2775509879120, 'tester eight', FALSE),
(6879430923821, 'tester nine', TRUE);