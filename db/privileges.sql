USE `lkticket`;

GRANT SELECT ON `profiles` TO 'lkticket'@'localhost';

GRANT SELECT, INSERT(`name`) ON `shows` TO 'lkticket'@'localhost';

GRANT SELECT, UPDATE(`name`) ON `users` TO 'lkticket'@'localhost';

GRANT SELECT ON `user_profiles` TO 'lkticket'@'localhost';

GRANT SELECT, INSERT(`token`,`user_id`) ON `user_tokens` TO 'lkticket'@'localhost';

GRANT SELECT, INSERT(`show_id`, `start_date`) ON `performances` TO 'lkticket'@'localhost';

GRANT SELECT, INSERT(`show_id`, `name`, `ticketCount`) ON `categories` TO 'lkticket'@'localhost';

GRANT SELECT, INSERT(`show_id`, `name`) ON `rates` TO 'lkticket'@'localhost';

GRANT SELECT, INSERT(`category_id`, `rate_id`, `price`), DELETE ON `prices` TO 'lkticket'@'localhost';
