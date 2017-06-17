USE `lkticket`;

GRANT SELECT ON `users` TO 'lkticket'@'localhost';

GRANT SELECT, INSERT(`token`,`user_id`) ON `user_tokens` TO 'lkticket'@'localhost';
