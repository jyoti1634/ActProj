-- Flyway baseline migration: create core schema and minimal dev seed
-- Note: For an existing database, consider setting
-- spring.flyway.baseline-on-migrate=true in application.properties

CREATE TABLE IF NOT EXISTS `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(100) NOT NULL,
  `email` varchar(150) DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email_UNIQUE` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `accounts` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `account_name` varchar(200) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_user_idx` (`user_id`),
  CONSTRAINT `fk_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `financial_years` (
  `id` int NOT NULL AUTO_INCREMENT,
  `account_id` int NOT NULL,
  `year_start` int NOT NULL,
  `year_end` int NOT NULL,
  `opening_balance` decimal(12,2) DEFAULT '0.00',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_account_year` (`account_id`,`year_end`,`year_start`),
  KEY `account_id_idx` (`account_id`),
  CONSTRAINT `fk_account_id` FOREIGN KEY (`account_id`) REFERENCES `accounts` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `months` (
  `id` int NOT NULL AUTO_INCREMENT,
  `year_id` int NOT NULL,
  `month_name` varchar(20) NOT NULL,
  `opening_balance` decimal(12,2) DEFAULT '0.00',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_year_month` (`year_id`,`month_name`),
  KEY `year_id_idx` (`year_id`),
  CONSTRAINT `fk_year_id` FOREIGN KEY (`year_id`) REFERENCES `financial_years` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `ledger` (
  `id` int NOT NULL AUTO_INCREMENT,
  `month_id` int NOT NULL,
  `entry_date` date NOT NULL,
  `particular_csh` varchar(255) DEFAULT NULL,
  `particular_exp` varchar(255) DEFAULT NULL,
  `cash_amt` decimal(12,2) DEFAULT '0.00',
  `exp_amt` decimal(12,2) NOT NULL DEFAULT '0.00',
  `cshbank_amt` decimal(12,2) DEFAULT NULL,
  `expbank_amt` decimal(12,2) DEFAULT NULL,
  `classification_csh` varchar(100) DEFAULT NULL,
  `classification_exp` varchar(255) DEFAULT NULL,
  `cheque_no` varchar(50) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `month_id_idx` (`month_id`),
  CONSTRAINT `fk_month_id` FOREIGN KEY (`month_id`) REFERENCES `months` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Minimal dev seeds (change password to a secure hashed value for real use)
INSERT INTO `users` (`username`,`email`,`password`) VALUES ('dev','dev@example.com','password');
INSERT INTO `accounts` (`user_id`,`account_name`) VALUES (1,'Cash Book');
INSERT INTO `financial_years` (`account_id`,`year_start`,`year_end`,`opening_balance`) VALUES (1,2024,2025,0.00);
INSERT INTO `months` (`year_id`,`month_name`,`opening_balance`) VALUES (1,'April',0.00),(1,'May',0.00);
