-- phpMyAdmin SQL Dump
-- version 5.0.3
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jan 27, 2021 at 10:39 AM
-- Server version: 10.4.14-MariaDB
-- PHP Version: 7.2.34

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `stockmanager`
--

-- --------------------------------------------------------

--
-- Table structure for table `products`
--

CREATE TABLE `products` (
  `product_id` int(11) NOT NULL,
  `product_name` varchar(50) NOT NULL,
  `product_category` varchar(50) DEFAULT NULL,
  `scale` varchar(20) NOT NULL,
  `init_quantity_in_scale` float NOT NULL,
  `unit_cost_price` float NOT NULL,
  `unit_sell_price` float NOT NULL,
  `supplier` varchar(50) DEFAULT NULL,
  `rem_quantity` float NOT NULL,
  `date_entry` varchar(30) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `products`
--

INSERT INTO `products` (`product_id`, `product_name`, `product_category`, `scale`, `init_quantity_in_scale`, `unit_cost_price`, `unit_sell_price`, `supplier`, `rem_quantity`, `date_entry`) VALUES
(2, 'Monitors-20 inches', 'Monitors', 'units', 50, 15000, 18000, 'x', 40, '04-01-2020'),
(4, 'keyboard-wireless', 'computer accessories', 'units', 15, 4000, 5000, 'x', 8, '08-01-2020'),
(5, 'Wireless Mouse', 'Computer accessories', 'units', 50, 3000, 3500, 'x', 48, '01-01-2021'),
(6, 'USB Flash drives - 8GB', 'Computer Accessories', 'packets', 20, 3000, 3500, 'x', 18, '01-01-2021');

-- --------------------------------------------------------

--
-- Table structure for table `sales`
--

CREATE TABLE `sales` (
  `sales_id` int(11) NOT NULL,
  `dateTime` varchar(50) NOT NULL,
  `product_id` int(11) NOT NULL,
  `quantity` float NOT NULL,
  `unitPrice` float NOT NULL,
  `total` float NOT NULL,
  `buyer` varchar(30) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `sales`
--

INSERT INTO `sales` (`sales_id`, `dateTime`, `product_id`, `quantity`, `unitPrice`, `total`, `buyer`) VALUES
(1, 'Mon Jan 11 14:29:55 WAT 2021', 1, 2, 1200, 2400, NULL),
(2, 'Mon Jan 11 14:58:54 WAT 2021', 1, 2, 1200, 2400, NULL),
(3, 'Mon Jan 11 14:59:41 WAT 2021', 1, 2, 1200, 2400, NULL),
(4, 'Tue Jan 12 19:11:44 WAT 2021', 2, 4, 18000, 72000, NULL),
(5, 'Fri Jan 15 13:44:28 WAT 2021', 3, 1, 3000, 3000, NULL),
(6, 'Fri Jan 15 13:44:55 WAT 2021', 1, 1, 1200, 1200, NULL),
(7, 'Fri Jan 15 14:09:02 WAT 2021', 1, 3, 1200, 3600, NULL),
(8, 'Fri Jan 15 14:14:15 WAT 2021', 2, 3, 18000, 54000, NULL),
(9, 'Fri Jan 15 14:27:27 WAT 2021', 4, 3, 5000, 15000, NULL),
(10, 'Fri Jan 15 14:53:08 WAT 2021', 3, 6, 3000, 18000, NULL),
(11, 'Fri Jan 15 14:58:32 WAT 2021', 1, 3, 1200, 3600, NULL),
(12, 'Fri Jan 15 14:58:32 WAT 2021', 3, 4, 3000, 12000, NULL),
(13, 'Fri Jan 15 15:02:28 WAT 2021', 2, 4, 18000, 72000, NULL),
(14, 'Fri Jan 15 15:03:41 WAT 2021', 2, 3, 18000, 54000, NULL),
(15, 'Fri Jan 15 15:03:41 WAT 2021', 3, 3, 3000, 9000, NULL),
(16, 'Fri Jan 15 15:03:43 WAT 2021', 4, 4, 5000, 20000, NULL),
(17, 'Sat Jan 16 09:47:57 WAT 2021', 1, 5, 1200, 6000, NULL),
(18, 'Tue Jan 19 10:41:17 WAT 2021', 5, 2, 3500, 7000, NULL),
(19, 'Tue Jan 19 10:42:12 WAT 2021', 6, 2, 3500, 7000, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `user_id` int(11) NOT NULL,
  `username` varchar(25) NOT NULL,
  `password` varchar(20) NOT NULL,
  `role` varchar(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`user_id`, `username`, `password`, `role`) VALUES
(1, 'lah', 'joel', 'admin'),
(2, 'joel', 'evariste', 'sales');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `products`
--
ALTER TABLE `products`
  ADD PRIMARY KEY (`product_id`);

--
-- Indexes for table `sales`
--
ALTER TABLE `sales`
  ADD PRIMARY KEY (`sales_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`user_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `products`
--
ALTER TABLE `products`
  MODIFY `product_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `sales`
--
ALTER TABLE `sales`
  MODIFY `sales_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=20;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
