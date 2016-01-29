create table category(categoryId long primary key,name text)
create table channel(channelId long primary key,name text,channelNumber integer,favCount integer,commentCount integer,isFav integer,isChange integer,packageId long REFERENCES package(packageId),logoUrl text)
create table cat_chn(id_ integer primary key autoincrement,cat_id integer not null REFERENCES category(categoryId),chn_id integer not null REFERENCES channel(channelId))
create table program(programId long primary key,name text,channelId long not null REFERENCES channel(channelId),favCount integer,commentCount integer,startDate integer,endDate integer,isFav integer,description text,isChange integer,isOutline integer)
create table package(packageId long primary key,name text,code text,logoUrl text)