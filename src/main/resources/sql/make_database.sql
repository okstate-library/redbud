CREATE TABLE `campus` (
  `id` int NOT NULL AUTO_INCREMENT,
  `institution_id` varchar(45) DEFAULT NULL,
  `campus_id` varchar(45) DEFAULT NULL,
  `campus_name` varchar(45) DEFAULT NULL,
  `campus_code` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `circulation_log` (
  `id` int NOT NULL AUTO_INCREMENT,
  `location` varchar(45) DEFAULT NULL,
  `item_id` varchar(50) DEFAULT NULL,
  `barcode` varchar(100) DEFAULT NULL,
  `material_type` varchar(45) DEFAULT NULL,
  `title` varchar(1000) DEFAULT NULL,
  `call_number` varchar(100) DEFAULT NULL,
  `loan_date` date DEFAULT NULL,
  `num_loans` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `institution` (
  `id` int NOT NULL AUTO_INCREMENT,
  `institution_id` varchar(45) DEFAULT NULL,
  `institution_name` varchar(250) DEFAULT NULL,
  `institution_code` varchar(250) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `library` (
  `id` int NOT NULL AUTO_INCREMENT,
  `campus_id` varchar(45) DEFAULT NULL,
  `library_id` varchar(45) DEFAULT NULL,
  `library_name` varchar(45) DEFAULT NULL,
  `library_code` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `location` (
  `id` int NOT NULL AUTO_INCREMENT,
  `institution_id` varchar(45) DEFAULT NULL,
  `campus_id` varchar(45) DEFAULT NULL,
  `library_id` varchar(45) DEFAULT NULL,
  `location_id` varchar(45) DEFAULT NULL,
  `location_name` varchar(250) DEFAULT NULL,
  `location_code` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=312 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `patron_group` (
  `group_id` int NOT NULL AUTO_INCREMENT,
  `folio_group_id` varchar(50) DEFAULT NULL,
  `folio_group_name` varchar(250) DEFAULT NULL,
  `institution_code` varchar(45) DEFAULT NULL,
  `institution_group` varchar(45) DEFAULT NULL,
  `is_folio_only` bit(1) NOT NULL,
  PRIMARY KEY (`group_id`)
) ENGINE=InnoDB AUTO_INCREMENT=119 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `role` (
  `role_id` int NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `user` (
  `user_id` bigint NOT NULL AUTO_INCREMENT,
  `first_name` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `username` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `enabled` bit(1) NOT NULL,
  `deleted` bit(1) NOT NULL,
  `role_id` int DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `UK_ob8kqyqqgmefl0aco34akdtpe` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;


INSERT INTO `role`
(`role_id`,`name`)
VALUES
(0,'ROLE_USER');

INSERT INTO `role`
(`role_id`,`name`)
VALUES
(1,'ROLE_ADMIN');


INSERT INTO `user`
(`user_id`,`first_name`,`last_name`,`email`,`username`,`password`,`enabled`,`deleted`,`role_id`)
VALUES
('1','first','last','admin@okstate.edu','admin','$2a$12$0nyne1/4.1.28ILaR9CqBuf0Uj.zne2Xr.OkQDd3XPW0OKbkKqrX6',1,0,1);

