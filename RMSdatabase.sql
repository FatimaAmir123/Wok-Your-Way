-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: localhost    Database: rms
-- ------------------------------------------------------
-- Server version	8.0.46

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `categories`
--

DROP TABLE IF EXISTS `categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categories` (
  `category_id` int NOT NULL AUTO_INCREMENT,
  `category_name` varchar(50) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`category_id`),
  UNIQUE KEY `category_name` (`category_name`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categories`
--

LOCK TABLES `categories` WRITE;
/*!40000 ALTER TABLE `categories` DISABLE KEYS */;
INSERT INTO `categories` VALUES (8,'Appetizers','Small starters to begin your meal'),(9,'Main Course','Primary hearty dishes'),(10,'Beverages','Cold and hot drinks'),(11,'Pasta','Italian style pasta dishes'),(12,'Burgers & Sandwiches','Freshly grilled burgers and sandwiches');
/*!40000 ALTER TABLE `categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `inventory`
--

DROP TABLE IF EXISTS `inventory`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `inventory` (
  `item_id` int NOT NULL AUTO_INCREMENT,
  `ingredient_name` varchar(100) NOT NULL,
  `quantity` int NOT NULL,
  `unit` varchar(20) NOT NULL,
  `last_updated` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`item_id`),
  UNIQUE KEY `ingredient_name` (`ingredient_name`)
) ENGINE=InnoDB AUTO_INCREMENT=53 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `inventory`
--

LOCK TABLES `inventory` WRITE;
/*!40000 ALTER TABLE `inventory` DISABLE KEYS */;
INSERT INTO `inventory` VALUES (1,'Chicken Breast',25,'kg','2026-05-23 13:56:53'),(2,'Fish Fillet',12,'kg','2026-05-23 13:56:53'),(3,'Beef Mince',10,'kg','2026-05-23 13:56:53'),(4,'Steak Strips',8,'kg','2026-05-23 13:56:53'),(5,'Fettuccine',15,'kg','2026-05-23 13:56:53'),(6,'Penne Pasta',14,'kg','2026-05-23 13:56:53'),(7,'Macaroni',12,'kg','2026-05-23 13:56:53'),(8,'Burger Buns',60,'pcs','2026-05-23 13:56:53'),(9,'Sandwich Bread',40,'pcs','2026-05-23 13:56:53'),(10,'Cheese (Mixed)',8,'kg','2026-05-23 13:56:53'),(11,'Fresh Cream',10,'litre','2026-05-23 13:56:53'),(12,'Butter',5,'kg','2026-05-23 13:56:53'),(13,'Garlic',3,'kg','2026-05-23 13:56:53'),(14,'Mushrooms',4,'kg','2026-05-23 13:56:53'),(15,'Tomatoes',6,'kg','2026-05-23 13:56:53'),(16,'Green Chillies',2,'kg','2026-05-23 13:56:53'),(17,'Cooking Oil',20,'litre','2026-05-23 13:56:53'),(18,'Breadcrumbs',5,'kg','2026-05-23 13:56:53'),(19,'Pineapple Juice',8,'litre','2026-05-23 13:56:53'),(20,'Mint Leaves',5,'kg','2026-06-06 07:23:20'),(21,'Beef Steak',30,'kg','2026-06-06 08:02:16'),(22,'Minced Beef',20,'kg','2026-06-06 08:02:16'),(23,'Mozzarella Cheese',15,'kg','2026-06-06 08:02:16'),(24,'Parmesan Cheese',10,'kg','2026-06-06 08:02:16'),(25,'Cheddar Cheese',12,'kg','2026-06-06 08:02:16'),(26,'Cream',20,'litre','2026-06-06 08:02:16'),(27,'Ice Cream',15,'kg','2026-06-06 08:02:16'),(28,'Pasta (Fettuccine)',15,'kg','2026-06-06 08:02:16'),(29,'Pasta (Penne)',15,'kg','2026-06-06 08:02:16'),(30,'Bread Bun',80,'pcs','2026-06-06 08:02:16'),(31,'Garlic Bread Loaf',30,'pcs','2026-06-06 08:02:16'),(32,'Mushroom',10,'kg','2026-06-06 08:02:16'),(33,'Chilli',5,'kg','2026-06-06 08:02:16'),(34,'Lettuce',8,'kg','2026-06-06 08:02:16'),(35,'Tomato',10,'kg','2026-06-06 08:02:16'),(36,'Potato',25,'kg','2026-06-06 08:02:16'),(37,'Cashew Nuts',5,'kg','2026-06-06 08:02:16'),(38,'Olive Oil',10,'litre','2026-06-06 08:02:16'),(39,'Tomato Sauce',15,'litre','2026-06-06 08:02:16'),(40,'Alfredo Sauce',10,'litre','2026-06-06 08:02:16'),(41,'Pink Sauce',10,'litre','2026-06-06 08:02:16'),(42,'Barbecue Sauce',8,'litre','2026-06-06 08:02:16'),(43,'Tarragon Sauce',8,'litre','2026-06-06 08:02:16'),(44,'Moroccan Spice Mix',3,'kg','2026-06-06 08:02:16'),(45,'Lemonade',30,'litre','2026-06-06 08:02:16'),(46,'Coconut Milk',20,'litre','2026-06-06 08:02:16'),(47,'Coffee',5,'kg','2026-06-06 08:02:16'),(48,'Milk',20,'litre','2026-06-06 08:02:16'),(49,'Blue Syrup',5,'litre','2026-06-06 08:02:16'),(50,'Sugar Syrup',10,'litre','2026-06-06 08:02:16'),(51,'Egg',99,'pcs','2026-06-06 08:04:46'),(52,'Flour',20,'kg','2026-06-06 08:02:16');
/*!40000 ALTER TABLE `inventory` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `menu_ingredients`
--

DROP TABLE IF EXISTS `menu_ingredients`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `menu_ingredients` (
  `id` int NOT NULL AUTO_INCREMENT,
  `menu_item_id` int NOT NULL,
  `inventory_item_id` int NOT NULL,
  `qty_per_serving` double NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_mi` (`menu_item_id`,`inventory_item_id`)
) ENGINE=InnoDB AUTO_INCREMENT=102 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `menu_ingredients`
--

LOCK TABLES `menu_ingredients` WRITE;
/*!40000 ALTER TABLE `menu_ingredients` DISABLE KEYS */;
INSERT INTO `menu_ingredients` VALUES (1,1,33,0.1),(2,1,23,0.08),(3,1,18,0.05),(4,1,51,1),(5,1,52,0.05),(6,1,38,0.03),(7,2,1,0.2),(8,2,18,0.06),(9,2,51,1),(10,2,52,0.05),(11,2,38,0.04),(12,3,31,0.5),(13,3,13,0.02),(14,3,12,0.04),(15,3,23,0.08),(16,3,24,0.03),(17,4,2,0.2),(18,4,18,0.06),(19,4,51,1),(20,4,52,0.05),(21,4,38,0.04),(22,5,1,0.25),(23,5,43,0.1),(24,5,26,0.08),(25,5,12,0.03),(26,5,13,0.01),(27,5,38,0.03),(28,6,1,0.25),(29,6,37,0.06),(30,6,38,0.04),(31,6,13,0.01),(32,6,39,0.05),(33,7,1,0.25),(34,7,44,0.04),(35,7,35,0.1),(36,7,38,0.04),(37,7,13,0.01),(38,8,2,0.25),(39,8,36,0.3),(40,8,52,0.06),(41,8,51,1),(42,8,18,0.06),(43,8,38,0.05),(44,9,1,0.25),(45,9,24,0.07),(46,9,23,0.06),(47,9,39,0.1),(48,9,18,0.05),(49,9,51,1),(50,9,38,0.04),(51,10,28,0.15),(52,10,40,0.12),(53,10,24,0.06),(54,10,26,0.08),(55,10,12,0.04),(56,10,13,0.01),(57,11,29,0.15),(58,11,41,0.12),(59,11,24,0.05),(60,11,26,0.06),(61,11,39,0.05),(62,11,13,0.01),(63,12,7,0.15),(64,12,25,0.1),(65,12,26,0.1),(66,12,12,0.04),(67,12,52,0.03),(68,12,48,0.1),(69,13,1,0.2),(70,13,9,2),(71,13,34,0.04),(72,13,35,0.05),(73,13,12,0.02),(74,13,38,0.02),(75,14,22,0.2),(76,14,30,1),(77,14,32,0.08),(78,14,25,0.05),(79,14,34,0.03),(80,14,35,0.04),(81,14,42,0.04),(82,15,21,0.25),(83,15,9,2),(84,15,32,0.06),(85,15,34,0.03),(86,15,35,0.04),(87,15,42,0.04),(88,15,12,0.02),(89,16,49,0.04),(90,16,45,0.25),(91,16,50,0.03),(92,17,46,0.15),(93,17,19,0.15),(94,17,50,0.03),(95,18,47,0.02),(96,18,48,0.2),(97,18,27,0.1),(98,18,50,0.03),(99,19,20,0.01),(100,19,45,0.25),(101,19,50,0.03);
/*!40000 ALTER TABLE `menu_ingredients` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `menuitems`
--

DROP TABLE IF EXISTS `menuitems`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `menuitems` (
  `item_id` int NOT NULL AUTO_INCREMENT,
  `item_name` varchar(100) NOT NULL,
  `description` text,
  `price` int NOT NULL,
  `category_id` int DEFAULT NULL,
  `is_available` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`item_id`),
  UNIQUE KEY `item_name` (`item_name`),
  KEY `category_id` (`category_id`),
  CONSTRAINT `menuitems_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `categories` (`category_id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `menuitems`
--

LOCK TABLES `menuitems` WRITE;
/*!40000 ALTER TABLE `menuitems` DISABLE KEYS */;
INSERT INTO `menuitems` VALUES (1,'Stuffed Chilli Bites','Crispy battered chilli bites stuffed with spiced cheese',450,8,1),(2,'Chicken Crunch Strips','Golden fried chicken strips with dipping sauce',650,8,1),(3,'Garlic Cheese Bread','Toasted bread with garlic butter and melted cheese',350,8,1),(4,'Finger Fish','Battered fish fingers served with tartare sauce',850,8,1),(5,'Chicken Tarragon','Grilled chicken in a creamy tarragon sauce',1200,9,1),(6,'Chicken Cashew Nut','Stir-fried chicken with cashews in savory sauce',1150,9,1),(7,'Moroccan Chicken','Slow-cooked chicken with North African spices',1250,9,1),(8,'Fish and Chips','Classic beer-battered fish with golden fries',1100,9,1),(9,'Chicken Parmesan','Breaded chicken breast with marinara and parmesan',1350,9,1),(10,'Fettuccine Alfredo','Fettuccine in rich butter and parmesan cream sauce',950,11,1),(11,'Pink Sauce Penne','Penne in a blush tomato cream sauce',900,11,1),(12,'Mac and Cheese','Classic macaroni baked with four-cheese sauce',800,11,1),(13,'Grilled Chicken Sandwich','Juicy grilled chicken with lettuce and chipotle mayo',750,12,1),(14,'Mushroom Swiss Burger','Beef patty topped with sauteed mushrooms and Swiss cheese',850,12,1),(15,'Steak Sandwich','Tender steak strips with caramelised onions in a baguette',950,12,1),(16,'Blue Lagoon','Blue curacao, lemon juice and sparkling water',350,10,1),(17,'Pina Colada','Pineapple juice, coconut cream and crushed ice',450,10,1),(18,'Cold Coffee with Ice Cream','Chilled espresso blended with vanilla ice cream',500,10,1),(19,'Mint Margarita','Fresh mint, lime juice and sparkling water',300,10,1);
/*!40000 ALTER TABLE `menuitems` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orderitems`
--

DROP TABLE IF EXISTS `orderitems`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orderitems` (
  `order_item_id` int NOT NULL AUTO_INCREMENT,
  `order_id` int NOT NULL,
  `item_id` int NOT NULL,
  `quantity` int NOT NULL,
  `subtotal` int NOT NULL,
  PRIMARY KEY (`order_item_id`),
  KEY `order_id` (`order_id`),
  KEY `item_id` (`item_id`),
  CONSTRAINT `orderitems_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`),
  CONSTRAINT `orderitems_ibfk_2` FOREIGN KEY (`item_id`) REFERENCES `menuitems` (`item_id`)
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orderitems`
--

LOCK TABLES `orderitems` WRITE;
/*!40000 ALTER TABLE `orderitems` DISABLE KEYS */;
INSERT INTO `orderitems` VALUES (1,1,14,2,1700),(2,2,5,1,1200),(3,2,10,1,950),(4,2,16,1,350),(5,3,10,1,950),(6,4,7,1,1250),(7,4,9,1,1350),(8,4,3,1,350),(9,4,17,1,450),(10,5,13,1,750),(11,5,19,2,600),(12,6,6,1,1150),(13,6,11,1,900),(14,6,18,1,500),(15,7,8,1,1100),(16,7,4,1,850),(17,8,5,1,1200),(18,8,12,1,800),(19,8,1,1,450),(20,8,16,1,350),(21,9,19,3,900),(22,10,5,1,1200),(23,11,15,1,950),(24,11,2,1,650),(25,12,7,1,1250),(26,12,9,1,1350),(27,12,3,2,700),(28,13,1,1,450),(29,13,2,2,1300),(32,15,1,1,450),(33,15,2,1,650),(34,16,1,1,450),(35,17,2,1,650),(36,18,4,1,850),(37,18,5,1,1200);
/*!40000 ALTER TABLE `orderitems` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `order_id` int NOT NULL AUTO_INCREMENT,
  `table_id` int NOT NULL,
  `waiter_id` int NOT NULL,
  `cashier_id` int DEFAULT NULL,
  `order_status` enum('Pending','Cooking','Ready','Served','Paid') DEFAULT 'Pending',
  `total_amount` int DEFAULT '0',
  `order_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`order_id`),
  KEY `table_id` (`table_id`),
  KEY `waiter_id` (`waiter_id`),
  KEY `cashier_id` (`cashier_id`),
  CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`table_id`) REFERENCES `restauranttables` (`table_id`),
  CONSTRAINT `orders_ibfk_2` FOREIGN KEY (`waiter_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `orders_ibfk_3` FOREIGN KEY (`cashier_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES (1,1,3,8,'Paid',1700,'2026-05-20 08:45:00'),(2,2,4,8,'Paid',2500,'2026-05-20 09:30:00'),(3,3,3,9,'Paid',950,'2026-05-20 10:00:00'),(4,4,5,8,'Paid',3400,'2026-05-21 08:15:00'),(5,5,6,9,'Paid',1450,'2026-05-21 09:00:00'),(6,6,7,8,'Paid',2550,'2026-05-21 14:30:00'),(7,7,3,9,'Paid',1950,'2026-05-22 08:00:00'),(8,8,4,8,'Paid',2800,'2026-05-22 09:45:00'),(9,9,5,9,'Paid',900,'2026-05-22 13:30:00'),(10,10,6,8,'Paid',1200,'2026-05-23 07:00:00'),(11,11,7,9,'Paid',1600,'2026-05-23 08:30:00'),(12,12,3,8,'Paid',3300,'2026-05-23 14:00:00'),(13,1,3,1,'Ready',1750,'2026-06-01 07:22:32'),(15,1,3,1,'Ready',1100,'2026-06-05 10:21:39'),(16,2,3,8,'Paid',450,'2026-06-05 10:35:11'),(17,6,3,1,'Ready',650,'2026-06-05 10:37:04'),(18,5,3,8,'Paid',2050,'2026-06-06 08:03:29');
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reservations`
--

DROP TABLE IF EXISTS `reservations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reservations` (
  `reservation_id` int NOT NULL AUTO_INCREMENT,
  `customer_name` varchar(100) NOT NULL DEFAULT 'Guest',
  `table_id` int NOT NULL,
  `reservation_time` datetime NOT NULL,
  `number_of_guests` int NOT NULL,
  `status` enum('Pending','Confirmed','Cancelled') DEFAULT 'Pending',
  `phone` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`reservation_id`),
  KEY `table_id` (`table_id`),
  CONSTRAINT `reservations_ibfk_2` FOREIGN KEY (`table_id`) REFERENCES `restauranttables` (`table_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reservations`
--

LOCK TABLES `reservations` WRITE;
/*!40000 ALTER TABLE `reservations` DISABLE KEYS */;
INSERT INTO `reservations` VALUES (1,'Ayesha Tariq',1,'2026-05-20 13:00:00',2,'Confirmed','03067711673'),(2,'Bilal Ahmed',3,'2026-05-20 14:00:00',4,'Confirmed','03206261179'),(3,'Hira Baig',5,'2026-05-20 15:30:00',3,'Cancelled','03153924550'),(4,'Usman Malik',2,'2026-05-21 19:00:00',5,'Confirmed','03401756639'),(5,'Zara Khan',7,'2026-05-21 20:00:00',2,'Pending','03000902571'),(6,'Hamid Sheikh',4,'2026-05-22 13:30:00',6,'Confirmed','03154758633'),(7,'Sana Qureshi',6,'2026-05-22 18:00:00',4,'Confirmed','03132685548'),(8,'Fahad Hussain',8,'2026-05-23 19:30:00',3,'Pending','03117427275'),(9,'Nadia Awan',9,'2026-05-24 20:00:00',7,'Pending','03325607774'),(10,'Omer Farooq',11,'2026-05-25 14:00:00',4,'Confirmed','03065564587');
/*!40000 ALTER TABLE `reservations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `restauranttables`
--

DROP TABLE IF EXISTS `restauranttables`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `restauranttables` (
  `table_id` int NOT NULL AUTO_INCREMENT,
  `table_number` varchar(10) NOT NULL,
  `capacity` int NOT NULL,
  `status` enum('Available','Occupied','Reserved') DEFAULT 'Available',
  PRIMARY KEY (`table_id`),
  UNIQUE KEY `table_number` (`table_number`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `restauranttables`
--

LOCK TABLES `restauranttables` WRITE;
/*!40000 ALTER TABLE `restauranttables` DISABLE KEYS */;
INSERT INTO `restauranttables` VALUES (1,'T1',2,'Available'),(2,'T2',4,'Available'),(3,'T3',4,'Available'),(4,'T4',6,'Available'),(5,'T5',2,'Available'),(6,'T6',4,'Available'),(7,'T7',6,'Available'),(8,'T8',4,'Available'),(9,'T9',8,'Available'),(10,'T10',2,'Available'),(11,'T11',4,'Available'),(12,'T12',6,'Available');
/*!40000 ALTER TABLE `restauranttables` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `full_name` varchar(100) NOT NULL,
  `phone` varchar(20) DEFAULT 'N/A',
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` enum('Admin','Waiter','Cashier','Chef') NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'Fatima Amir','0300-1234567','fatima_a','admin123','Admin','2026-05-23 13:56:53'),(3,'Hamza Khan','0322-5554433','hamza_w','pass123','Waiter','2026-05-23 13:56:53'),(4,'Sara Ahmed','0333-7778899','sara_w','pass123','Waiter','2026-05-23 13:56:53'),(5,'Ali Raza','0344-1122334','ali_w','pass123','Waiter','2026-05-23 13:56:53'),(6,'Hina Baig','0355-6677889','hina_w','pass123','Waiter','2026-05-23 13:56:53'),(7,'Usman Tariq','0366-3344556','usman_w','pass123','Waiter','2026-05-23 13:56:53'),(8,'Zara Malik','0377-9988776','zara_c','pass123','Cashier','2026-05-23 13:56:53'),(9,'Bilal Sheikh','0388-5566778','bilal_c','pass123','Cashier','2026-05-23 13:56:53'),(10,'Nasir Iqbal','0399-1122334','nasir_chef','chef123','Chef','2026-05-23 13:56:53'),(11,'Aisha Butt','0300-9988112','aisha_chef','chef123','Chef','2026-05-23 13:56:53');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-06-06 18:44:38
