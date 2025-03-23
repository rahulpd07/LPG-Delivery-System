-- 1️⃣ Ensure the database exists
CREATE DATABASE IF NOT EXISTS lpg_db;
USE lpg_db;

-- lpg_db.users definition
CREATE TABLE `users` (
  `user_id` bigint NOT NULL AUTO_INCREMENT,
  `address` varchar(255) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `phone_number` varchar(255) DEFAULT NULL,
  `role` enum('ADMIN','CUSTOMER','DELIVERY_PERSON') DEFAULT NULL,
  `username` varchar(255) NOT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- lpg_db.lpg_cylinders definition
CREATE TABLE `lpg_cylinders` (
  `cylinder_id` bigint NOT NULL AUTO_INCREMENT,
  `price` double DEFAULT NULL,
  `stock_quantity` int DEFAULT NULL,
  `type` enum('COMMERCIAL','DOMESTIC') DEFAULT NULL,
  `weight` double DEFAULT NULL,
  PRIMARY KEY (`cylinder_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- lpg_db.orders definition

CREATE TABLE `orders` (
  `order_id` bigint NOT NULL AUTO_INCREMENT,
  `capacity` double DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `cylinder_type` enum('COMMERCIAL','DOMESTIC') DEFAULT NULL,
  `delivery_date` datetime(6) DEFAULT NULL,
  `order_date` datetime(6) DEFAULT NULL,
  `quantity` int DEFAULT NULL,
  `status` enum('CANCELLED','DELIVERED','PENDING','IN_TRANSIT') DEFAULT NULL,
  `total_price` double DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`order_id`),
  KEY `FK32ql8ubntj5uh44ph9659tiih` (`user_id`),
  CONSTRAINT `FK32ql8ubntj5uh44ph9659tiih` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- lpg_db.payments definition
CREATE TABLE `payments` (
  `payment_id` bigint NOT NULL AUTO_INCREMENT,
  `amount` double DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `payment_date` datetime(6) DEFAULT NULL,
  `payment_method` enum('CASH_ON_DELIVERY','ONLINE') DEFAULT NULL,
  `status` enum('FAILED','PENDING','SUCCESS') DEFAULT NULL,
  `order_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`payment_id`),
  UNIQUE KEY `UK8vo36cen604as7etdfwmyjsxt` (`order_id`),
  KEY `FKj94hgy9v5fw1munb90tar2eje` (`user_id`),
  CONSTRAINT `FK81gagumt0r8y3rmudcgpbk42l` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`),
  CONSTRAINT `FKj94hgy9v5fw1munb90tar2eje` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- lpg_db.feedbacks definition
CREATE TABLE `feedbacks` (
  `feedback_id` bigint NOT NULL AUTO_INCREMENT,
  `comments` varchar(255) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `rating` int DEFAULT NULL,
  `order_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`feedback_id`),
  UNIQUE KEY `UKhmlh8kxpicstcu589yif47wah` (`order_id`),
  KEY `FK312drfl5lquu37mu4trk8jkwx` (`user_id`),
  CONSTRAINT `FK312drfl5lquu37mu4trk8jkwx` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `FKbdhoov2mv332ks2m84owt5tv3` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`),
  CONSTRAINT `feedbacks_chk_1` CHECK (((`rating` <= 5) and (`rating` >= 1)))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- lpg_db.deliveries definition
CREATE TABLE `deliveries` (
  `delivery_id` bigint NOT NULL AUTO_INCREMENT,
  `delivery_date` datetime(6) DEFAULT NULL,
  `expected_delivery_date` datetime(6) DEFAULT NULL,
  `notes` varchar(255) DEFAULT NULL,
  `status` enum('DELIVERED','IN_TRANSIT','PENDING') DEFAULT NULL,
  `delivery_person_id` bigint NOT NULL,
  `order_id` bigint NOT NULL,
  PRIMARY KEY (`delivery_id`),
  UNIQUE KEY `UKk36n9p5v7dd96hpgkwybvbogt` (`order_id`),
  KEY `FKj83vua77cbq1e1u1dwo1fuwlh` (`delivery_person_id`),
  CONSTRAINT `FK7isx0rnbgqr1dcofd5putl6jw` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`),
  CONSTRAINT `FKj83vua77cbq1e1u1dwo1fuwlh` FOREIGN KEY (`delivery_person_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
