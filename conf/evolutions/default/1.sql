# Initial schema

# --- !Ups
create table `USERS` (`id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,`twitter_id` VARCHAR(254) NOT NULL,`twitter_name` VARCHAR(254) NOT NULL,`twitter_handle` VARCHAR(254),`twitter_avatar_url` VARCHAR(254),`created_at` TIMESTAMP NULL,`updated_at` TIMESTAMP NULL,`access_token` VARCHAR(254),`secret` VARCHAR(254));
create index `twitter_name_index` on `USERS` (`twitter_name`);
create unique index `twitter_id_index` on `USERS` (`twitter_id`);
create table `POLLS` (`id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,`owner_id` BIGINT NOT NULL,`description` VARCHAR(254) NOT NULL,`hash_tag` VARCHAR(254),`created_at` TIMESTAMP NULL,`updated_at` TIMESTAMP NULL);
alter table `POLLS` add constraint `POLL_USER_FK` foreign key(`owner_id`) references `USERS`(`id`) on update NO ACTION on delete NO ACTION;
create table `CHOICES` (`id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,`poll_id` BIGINT NOT NULL,`description` VARCHAR(254) NOT NULL,`created_at` TIMESTAMP NULL,`updated_at` TIMESTAMP NULL);
alter table `CHOICES` add constraint `CHOICES_POLL_FK` foreign key(`poll_id`) references `POLLS`(`id`) on update NO ACTION on delete NO ACTION;
create table `RESPONSES` (`id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,`twitter_user_id` BIGINT NOT NULL,`poll_id` BIGINT NOT NULL,`choice_id` BIGINT NOT NULL,`explanation` VARCHAR(2000),`created_at` TIMESTAMP NOT NULL,`updated_at` TIMESTAMP NOT NULL);
create unique index `IDX_UNIQUE_RESPONSE` on `RESPONSES` (`twitter_user_id`,`poll_id`,`choice_id`);
alter table `RESPONSES` add constraint `RESPONSES_POLL_FK` foreign key(`poll_id`) references `POLLS`(`id`) on update NO ACTION on delete NO ACTION;
alter table `RESPONSES` add constraint `RESPONSES_CHOICE_FK` foreign key(`choice_id`) references `CHOICES`(`id`) on update NO ACTION on delete NO ACTION;
create table `TARGETS` (`id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,`twitter_user_id` BIGINT NOT NULL,`poll_id` BIGINT NOT NULL,`created_at` TIMESTAMP NOT NULL,`updated_at` TIMESTAMP NOT NULL);
alter table `TARGETS` add constraint `POLLTARGETS_POLL_FK` foreign key(`poll_id`) references `POLLS`(`id`) on update NO ACTION on delete NO ACTION;

# --- !Downs
drop table targets;
drop table responses;
drop table choices;
drop table polls;
drop table users;