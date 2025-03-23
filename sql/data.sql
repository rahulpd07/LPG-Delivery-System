-- Insert Users Directly in Users Table
INSERT INTO `users` (`address`, `created_at`, `email`, `password`, `phone_number`, `role`, `username`)
VALUES
('123 Main St, NY', NOW(), 'admin@example.com', '$2a$10$abcdefg1234567890', '1234567890', 'ADMIN', 'admin_user');


-- Insert LPG Cylinders
INSERT INTO `lpg_cylinders` (`price`, `stock_quantity`, `type`, `weight`)
VALUES
(550.00, 50, 'DOMESTIC', 14.5),
(750.00, 30, 'COMMERCIAL', 18.5);


-- Insert Orders (Linked to Users and LPG Cylinders)
INSERT INTO `orders` (`capacity`, `created_at`, `cylinder_type`, `delivery_date`, `order_date`, `quantity`, `status`, `total_price`, `user_id`)
VALUES
(14.2, NOW(), 'DOMESTIC', DATE_ADD(NOW(), INTERVAL 3 DAY), NOW(), 2, 'PENDING', 100.00, 1);


-- Insert Deliveries (Assigned to Delivery Person)
INSERT INTO `deliveries` (`delivery_date`, `expected_delivery_date`, `notes`, `status`, `delivery_person_id`, `order_id`)
VALUES
(NOW(), DATE_ADD(NOW(), INTERVAL 2 DAY), 'Handle with care. Deliver before evening.', 'IN_TRANSIT', 1, 1);


-- Insert Payments (Linked to Orders and Users)
INSERT INTO `payments` (`amount`, `created_at`, `payment_date`, `payment_method`, `status`, `order_id`, `user_id`)
VALUES
(100.00, NOW(), NOW(), 'ONLINE', 'SUCCESS', 1, 1);


-- Insert Feedback (Linked to Orders and Users)
INSERT INTO `feedbacks` (`comments`, `created_at`, `rating`, `order_id`, `user_id`)
VALUES
('Very satisfied with the service. Will order again!', NOW(), 5, 1, 1);

