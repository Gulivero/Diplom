DROP DATABASE QuestDb;

CREATE DATABASE QuestDb;

USE QuestDb;

DROP TABLE "Users", "Quests", "Requests", "Images";

CREATE TABLE "Users" (
	[id] INT PRIMARY KEY IDENTITY(1,1),
	[login]	nvarchar(50) NOT NULL UNIQUE,
	[password]	nvarchar(50) NOT NULL,
	[name]	nvarchar(50) NOT NULL,
	[communication] nvarchar(250) NOT NULL,
	[role] nvarchar(50) NOT NULL DEFAULT 'user',
	[count_quests] INT NOT NULL DEFAULT 1,
	[salt] nvarchar(250) NOT NULL
);

CREATE TABLE "Quests" (
	[id] INT PRIMARY KEY IDENTITY(1,1),
	[name]	nvarchar(50) NOT NULL,
	[description]	nvarchar(500) NOT NULL,
	[reward]	nvarchar(50) NOT NULL,
	[author] nvarchar(50) NOT NULL,
	[contractor] nvarchar (50)
);

CREATE TABLE "Requests" (
	[id] INT PRIMARY KEY IDENTITY(1,1),
	[user_id] INT foreign key references dbo.Users(id) NOT NULL,
	[quest_id] INT foreign key references dbo.Quests(id) NOT NULL,
);

CREATE TABLE "Images" (
	[id] INT PRIMARY KEY IDENTITY(1,1),
	[user_id] INT foreign key references dbo.Users(id),
	[quest_id] INT foreign key references dbo.Quests(id),
	[file_name] nvarchar(50),
	[image_data] varbinary(MAX)
);