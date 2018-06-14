USE `lkticket`;

GRANT SELECT ON `profiles` TO 'lkticket'@localhost;

GRANT SELECT ON `locations` TO 'lkticket'@localhost;

GRANT SELECT, UPDATE(`alive`) ON `printers` TO 'lkticket'@localhost;

GRANT SELECT, INSERT(`name`), UPDATE(`name`) ON `shows` TO 'lkticket'@localhost;

GRANT SELECT, INSERT(`email`), UPDATE(`name`) ON `users` TO 'lkticket'@localhost;

GRANT SELECT, INSERT, DELETE ON `user_profiles` TO 'lkticket'@localhost;

GRANT SELECT, INSERT(`token`,`user_id`) ON `user_tokens` TO 'lkticket'@localhost;

GRANT SELECT, INSERT(`show_id`, `start`) ON `performances` TO 'lkticket'@localhost;

GRANT SELECT, INSERT(`show_id`, `name`, `ticketCount`), UPDATE(`ticketCount`) ON `categories` TO 'lkticket'@localhost;

GRANT SELECT, INSERT(`show_id`, `name`) ON `rates` TO 'lkticket'@localhost;

GRANT SELECT, INSERT, UPDATE(`active_ticket_id`, `profile_id`) ON `seats` TO 'lkticket'@localhost;

GRANT SELECT, INSERT(`category_id`, `rate_id`, `price`), DELETE ON `prices` TO 'lkticket'@localhost;

GRANT SELECT, INSERT(`expires`, `identifier`), UPDATE(`customer_id`) ON `orders` TO 'lkticket'@localhost;

GRANT SELECT, INSERT (`order_id`, `seat_id`, `rate_id`, `price`), UPDATE (`order_id`, `printed`, `paid`, `cancelled`) ON `tickets` TO 'lkticket'@localhost;

GRANT SELECT, INSERT (`email`, `phone`, `name`) ON `customers` TO 'lkticket'@localhost;

GRANT SELECT, INSERT ON `customer_profiles` TO 'lkticket'@localhost;

GRANT SELECT, INSERT (`transaction_id`, `order_id`, `amount`, `method`, `reference`) ON `payments` TO 'lkticket'@localhost;

GRANT SELECT, INSERT (`user_id`, `order_id`, `profile_id`, `customer_id`, `printer_id`, `location_id`) ON `transactions` TO 'lkticket'@localhost;

GRANT SELECT, INSERT (`ticket_id`, `transaction_id`, `activity`) ON `ticket_transactions` TO 'lkticket'@localhost;
