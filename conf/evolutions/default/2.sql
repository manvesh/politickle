# --- !Ups
DROP INDEX `IDX_UNIQUE_RESPONSE` ON `RESPONSES`;
create unique index `IDX_UNIQUE_RESPONSE` on `RESPONSES` (`twitter_user_id`,`poll_id`);
ALTER TABLE `responses` MODIFY `twitter_user_id` VARCHAR(20);

# --- !Downs
