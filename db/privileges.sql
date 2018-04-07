USE `lkticket`;

GRANT SELECT ON `profiles` TO 'lkticket';

GRANT SELECT ON `locations` TO 'lkticket';

GRANT SELECT, UPDATE(`alive`) ON `printers` TO 'lkticket';

GRANT SELECT, INSERT(`name`), UPDATE(`name`) ON `shows` TO 'lkticket';

GRANT SELECT, INSERT(`email`), UPDATE(`name`) ON `users` TO 'lkticket';

GRANT SELECT, INSERT, DELETE ON `user_profiles` TO 'lkticket';

GRANT SELECT, INSERT(`token`,`user_id`) ON `user_tokens` TO 'lkticket';

GRANT SELECT, INSERT(`show_id`, `start`) ON `performances` TO 'lkticket';

GRANT SELECT, INSERT(`show_id`, `name`, `ticketCount`), UPDATE(`ticketCount`) ON `categories` TO 'lkticket';

GRANT SELECT, INSERT(`show_id`, `name`) ON `rates` TO 'lkticket';

GRANT SELECT, INSERT, UPDATE(`active_ticket_id`, `profile_id`) ON `seats` TO 'lkticket';

GRANT SELECT, INSERT(`category_id`, `rate_id`, `price`), DELETE ON `prices` TO 'lkticket';

GRANT SELECT, INSERT(`expires`, `identifier`), UPDATE(`customer_id`) ON `orders` TO 'lkticket';

GRANT SELECT, INSERT (`order_id`, `seat_id`, `rate_id`, `price`), UPDATE (`order_id`, `printed`, `paid`) ON `tickets` TO 'lkticket';

GRANT SELECT, INSERT (`email`, `phone`, `name`) ON `customers` TO 'lkticket';

GRANT SELECT, INSERT ON `customer_profiles` TO 'lkticket';

GRANT SELECT, INSERT (`transaction_id`, `order_id`, `amount`, `method`, `reference`) ON `payments` TO 'lkticket';

GRANT SELECT, INSERT (`user_id`, `order_id`, `profile_id`, `customer_id`, `printer_id`, `location_id`) ON `transactions` TO 'lkticket';

GRANT SELECT, INSERT (`ticket_id`, `transaction_id`, `activity`) ON `ticket_transactions` TO 'lkticket';
