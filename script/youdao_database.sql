/*Table for tbl_basic_translation*/
CREATE TABLE youdao.tbl_basic_translation (
	id INT NOT NULL AUTO_INCREMENT,
	query varchar(256) NULL,
	detail_translation varchar(2048) NULL,
	count INT NULL,
	`date` DATETIME NULL,
	phonetic varchar(256) NULL,
	uk_phonetic varchar(256) NULL,
	us_phonetic varchar(256) NULL,
	uk_speech_url varchar(1024) NULL,
	us_speech_url varchar(1024) NULL,
	uk_speech_actual_url varchar(1024) NULL,
	us_speech_actual_url varchar(1024) NULL,
	lang varchar(100) NULL,
	CONSTRAINT tbl_basic_translation_PK PRIMARY KEY (id)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8
COLLATE=utf8_general_ci ;

/*Table for tbl_explains*/
CREATE TABLE youdao.tbl_explains (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT,
	explains varchar(4096) NULL,
	b_id INT NULL,
	CONSTRAINT tbl_explains_PK PRIMARY KEY (id),
	CONSTRAINT tbl_explains_tbl_basic_translation_FK FOREIGN KEY (b_id) REFERENCES youdao.tbl_basic_translation(id)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8
COLLATE=utf8_general_ci ;

/*Table for tbl_web_translation*/
CREATE TABLE youdao.tbl_web_translation (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT,
	web_key varchar(1024) NULL,
	web_value varchar(4096) NULL,
	b_id INT NULL,
	CONSTRAINT tbl_web_translation_PK PRIMARY KEY (id),
	CONSTRAINT tbl_web_translation_tbl_basic_translation_FK FOREIGN KEY (b_id) REFERENCES youdao.tbl_basic_translation(id)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8
COLLATE=utf8_general_ci ;

