USE `lkticket`;

GRANT SELECT ON `profiles` TO 'lkticket';

GRANT SELECT, INSERT(`name`), UPDATE(`name`) ON `shows` TO 'lkticket';

GRANT SELECT, INSERT(`name`), UPDATE(`name`) ON `users` TO 'lkticket';

GRANT SELECT ON `user_profiles` TO 'lkticket';

GRANT SELECT, INSERT(`token`,`user_id`) ON `user_tokens` TO 'lkticket';

GRANT SELECT, INSERT(`show_id`, `start`) ON `performances` TO 'lkticket';

GRANT SELECT, INSERT(`show_id`, `name`, `ticketCount`), UPDATE(`ticketCount`) ON `categories` TO 'lkticket';

GRANT SELECT, INSERT(`show_id`, `name`) ON `rates` TO 'lkticket';

GRANT SELECT, INSERT, UPDATE(`active_ticket_id`) ON `seats` TO 'lkticket';

GRANT SELECT, INSERT(`category_id`, `rate_id`, `price`), DELETE ON `prices` TO 'lkticket';

GRANT SELECT, INSERT(`expires`, `identifier`) ON `orders` TO 'lkticket';

GRANT SELECT, INSERT (`order_id`, `seat_id`, `rate_id`, `price`) ON `tickets` TO 'lkticket';
