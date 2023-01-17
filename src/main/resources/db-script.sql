CREATE TABLE `book` (
  `isbn` varchar(15) NOT NULL,
  `title` varchar(30) NOT NULL,
  `author` varchar(40) NOT NULL,
  `copies` int DEFAULT NULL,
  PRIMARY KEY (`isbn`)
);

INSERT INTO `book` VALUES ('123-456-789-123','Digital Fortress','Dan',9),('123-456-789-222','The Power of Now','EckartTolley',6),('123-567-432-890','Digital Fortress','Dan Brown',1),('234-678-345-345','Death on Air','Ngiao Marsh',3),('456-567-789-123','The 5AM Club','Robert Sharma',5),('788-908-566-123','The Rocking-Horse Winner','D.H Lawrence',7),('788-908-566-200','Effective Java 3','Prasad',2);

CREATE TABLE `member` (
                          `id` varchar(36) NOT NULL,
                          `name` varchar(100) NOT NULL,
                          `address` varchar(300) NOT NULL,
                          `contact` varchar(20) NOT NULL,
                          PRIMARY KEY (`id`)
);



INSERT INTO `member` VALUES ('23c16dd5-5a9b-11ed-80a7-3ca067a02115','Athula','Galle','011-1234567'),('23c4da07-5a9b-11ed-80a7-3ca067a02115','Vipula','Galle','077-3376993'),('23c4db2f-5a9b-11ed-80a7-3ca067a02115','Kapila','Panadura','077-6376754'),('23c4db2f-5a9b-11ed-80a7-3ca067a02117','Ruwan','Colombo','077-3376993'),('23c4db2f-5a9b-11ed-80a7-3ca067a02118','Pasindu','Kegalle','077-8898890'),('23c4db2f-5a9b-11ed-80a7-3ca067a02119','Dasun','Kandy','099-8987890'),('23c4db2f-5a9b-11ed-80a7-3ca067a02130','Sandun','Piliyandala','088-3435634');

CREATE TABLE `issue_note` (
                              `id` int NOT NULL AUTO_INCREMENT,
                              `date` date NOT NULL,
                              `member_id` varchar(36) NOT NULL,
                              PRIMARY KEY (`id`),
                              CONSTRAINT `fk_issue_note` FOREIGN KEY (`member_id`) REFERENCES `member` (`id`)
);

INSERT INTO `issue_note` VALUES (1,'2022-11-14','23c16dd5-5a9b-11ed-80a7-3ca067a02115'),(2,'2022-11-14','23c16dd5-5a9b-11ed-80a7-3ca067a02115'),(3,'2022-11-14','23c4da07-5a9b-11ed-80a7-3ca067a02115'),(4,'2022-11-15','23c16dd5-5a9b-11ed-80a7-3ca067a02115'),(5,'2022-11-15','23c4db2f-5a9b-11ed-80a7-3ca067a02115'),(6,'2022-11-15','23c4db2f-5a9b-11ed-80a7-3ca067a02117'),(7,'2022-11-15','23c16dd5-5a9b-11ed-80a7-3ca067a02115'),(8,'2022-11-16','23c4db2f-5a9b-11ed-80a7-3ca067a02118'),(9,'2022-11-16','23c4db2f-5a9b-11ed-80a7-3ca067a02119'),(10,'2022-11-16','23c4db2f-5a9b-11ed-80a7-3ca067a02119'),(12,'2022-11-16','23c4da07-5a9b-11ed-80a7-3ca067a02115'),(13,'2022-11-16','23c4db2f-5a9b-11ed-80a7-3ca067a02130');






CREATE TABLE `issue_item` (
  `issue_id` int NOT NULL,
  `isbn` varchar(25) NOT NULL,
  PRIMARY KEY (`issue_id`,`isbn`),
  CONSTRAINT `issue_item_ibfk_1` FOREIGN KEY (`issue_id`) REFERENCES `issue_note` (`id`),
  CONSTRAINT `issue_item_ibfk_2` FOREIGN KEY (`isbn`) REFERENCES `book` (`isbn`)
);





INSERT INTO `issue_item` VALUES (3,'123-456-789-123'),(3,'123-456-789-222'),(1,'123-567-432-890'),(4,'123-567-432-890'),(5,'123-567-432-890'),(6,'123-567-432-890'),(1,'234-678-345-345'),(12,'234-678-345-345'),(13,'234-678-345-345'),(8,'456-567-789-123'),(9,'456-567-789-123'),(3,'788-908-566-123'),(4,'788-908-566-123'),(5,'788-908-566-123'),(10,'788-908-566-123');



CREATE TABLE `return` (
  `date` date NOT NULL,
  `issue_id` int NOT NULL,
  `isbn` varchar(25) NOT NULL,
  PRIMARY KEY (`issue_id`,`isbn`),
  CONSTRAINT `return_ibfk_1` FOREIGN KEY (`issue_id`, `isbn`) REFERENCES `issue_item` (`issue_id`, `isbn`)
);


INSERT INTO `return` VALUES ('2022-11-14',1,'123-567-432-890'),('2022-11-16',3,'123-456-789-123'),('2022-11-16',3,'123-456-789-222'),('2022-11-15',4,'123-567-432-890'),('2022-11-15',4,'788-908-566-123');


