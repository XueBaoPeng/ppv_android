create table if not exists channel_platform(fk_channel long,platform_type int(1),channel_number text)
alter table package add column platform_type int(1)