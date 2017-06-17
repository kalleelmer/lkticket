USE `lkticket`;

GRANT SELECT ON `profiles` TO 'lkticket'@'localhost';

GRANT SELECT ON `users` TO 'lkticket'@'localhost';

GRANT SELECT ON `user_profiles` TO 'lkticket'@'localhost';

GRANT SELECT, INSERT(`token`,`user_id`) ON `user_tokens` TO 'lkticket'@'localhost';
