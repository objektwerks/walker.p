create table if not exists walker (
  id long primary key auto_increment,
  name varchar(24) not null
);
create index if not exists walker_name_idx ON walker(name);

create table if not exists session (
  id long primary key auto_increment,
  walker_id long references walker(id),
  weight int not null,
  weight_unit char(2) not null,
  distance int not null,
  distance_unit char(2) not null,
  hours int not null,
  minutes int not null,
  calories int not null,
  datetime bigint not null
);
create index if not exists session_datetime_idx ON session(datetime);