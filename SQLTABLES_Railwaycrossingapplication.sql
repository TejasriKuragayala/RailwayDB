create database Railwaycrossingapplication;
use Railwaycrossingapplication;

create table Users 
(userid int not null primary key,
name nvarchar(100) not null,
email nvarchar(50) not null unique,
password nvarchar(30),
usertype nvarchar(10)
);

create table Railwaycrossing
(crossingid int primary key,
name nvarchar(100) not null,
address nvarchar(200),
status nvarchar(10),
personinchargeid int foreign key(personinchargeid) references Users (userid) on delete cascade,
scheduleid int foreign key(scheduleid) references Schedule (scheduleid) on delete no action
);

create table Schedule
(scheduleid int not null primary key,
starttime nvarchar(100) unique,
endtime  nvarchar(100),
personinchargeid int unique foreign key (personinchargeid) references Users (userid) on delete no action
);

insert into Users ('name','email','password','userType')
values ('tejasri','tejasri123@railway.com','tejasri123','admin')

insert into Users ('name','email','password','userType')
values ('kuragayala','kuragayala123@railway.com','kuragayala123','admin')

insert into Users ('name','email','password','userType')
values ('Amazon','Amazon123@railway.com','Amazon123','admin')

insert into Users ('name','email','password','userType')
values ('Atlas','Atlas123@amazon.com','Atlas123','admin')

insert into Users ('name','email','password','userType')
values ('Java','Java123@amazon.com','Java123','admins')

