CREATE TABLE `File` (
	`id` BIGINT NOT NULL UNIQUE,
	`name` varchar(250) NOT NULL,
	`path` varchar(250) NOT NULL,
	`id_path` varchar(250) NOT NULL,
	`parent_id` BIGINT,
	`class` varchar(250) NOT NULL,
	`owner_id` BIGINT NOT NULL,
	`content_id` BIGINT,
	`create_date` DATE NOT NULL,
	`update_date` DATE NOT NULL,
	PRIMARY KEY (`id`)
);

CREATE TABLE `File_Content` (
	`id` BIGINT NOT NULL UNIQUE,
	`data` VARBINARY(10240) NOT NULL,
	PRIMARY KEY (`id`)
);

CREATE TABLE `Folder` (
	`id` BIGINT NOT NULL UNIQUE
);

CREATE TABLE `Text_File` (
	`id` BIGINT NOT NULL UNIQUE,
	`type` varchar(250) NOT NULL
);

CREATE TABLE `AbstractFile_GENERATOR_TABLE` (
	`key_gen` varchar(250) NOT NULL,
	`hi` BIGINT NOT NULL,
	PRIMARY KEY (`key_gen`)
);

CREATE TABLE `User` (
	`id` BIGINT NOT NULL UNIQUE,
	`email` varchar(250) NOT NULL UNIQUE,
	`password` varchar(250) NOT NULL,
	`reset_token` varchar(250),
	`token_expiration_date` DATE,
	PRIMARY KEY (`id`)
);

CREATE TABLE `USER_GENERATOR_TABLE` (
	`key_gen` varchar(250) NOT NULL,
	`hi` BIGINT NOT NULL,
	PRIMARY KEY (`key_gen`)
);

CREATE TABLE `Permission` (
	`id` BIGINT NOT NULL UNIQUE,
	`file_id` BIGINT NOT NULL,
	`read` BOOLEAN,
	`write` BOOLEAN,
	`del` BOOLEAN,
	`user_id` BIGINT NOT NULL,
	PRIMARY KEY (`id`)
);

CREATE TABLE `Permission_GENERATOR_TABLE` (
	`key_gen` varchar(250) NOT NULL,
	`hi` BIGINT NOT NULL,
	PRIMARY KEY (`key_gen`)
);

ALTER TABLE `File` ADD CONSTRAINT `File_fk0` FOREIGN KEY (`parent_id`) REFERENCES `File`(`id`);

ALTER TABLE `File` ADD CONSTRAINT `File_fk1` FOREIGN KEY (`content_id`) REFERENCES `File_Content`(`id`);

ALTER TABLE `Folder` ADD CONSTRAINT `Folder_fk0` FOREIGN KEY (`id`) REFERENCES `File`(`id`);

ALTER TABLE `Text_File` ADD CONSTRAINT `Text_File_fk0` FOREIGN KEY (`id`) REFERENCES `File`(`id`);

ALTER TABLE `Permission` ADD CONSTRAINT `Permission_fk0` FOREIGN KEY (`file_id`) REFERENCES `File`(`id`);