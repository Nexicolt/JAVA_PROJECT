create database wms_java;
create or replace table assortment
(
    assortmentID int auto_increment
        primary key,
    name varchar(100) not null,
    constraint name
        unique (name)
);

create or replace table location
(
    locationID int auto_increment
        primary key,
    name varchar(100) not null,
    constraint name
        unique (name)
);

create or replace table stockitem
(
    assortmentID int not null,
    locationID int not null,
    stockLevel float not null,
    constraint FK_assortmentID
        foreign key (assortmentID) references assortment (assortmentID),
    constraint FK_locationID
        foreign key (locationID) references location (locationID)
);

create or replace table users
(
    userID int auto_increment
        primary key,
    login varchar(50) not null,
    password varchar(255) not null,
    constraint Login
        unique (login)
);

create or replace definer = root@localhost procedure AddNewAssortment(IN _assortmentName varchar(50))
BEGIN
    #     Sprawdź, czy taki użytownik już nie istnieje
    SET @assortmentExists = (SELECT COUNT(name) FROM assortment WHERE name=_assortmentName);
    if(@assortmentExists) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT =  'Asortyment o podanej nazwie już istnieje';
    END IF;
    INSERT INTO assortment SET name=_assortmentName;
end;

create or replace definer = root@localhost procedure AddNewLocation(IN _locationName varchar(50))
BEGIN
    #     Sprawdź, czy taki użytownik już nie istnieje
    SET @locationExists = (SELECT COUNT(name) FROM location WHERE name=_locationName);
    if(@locationExists) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT =  'Lokalizacja o podanej nazwie już istnieje';
    END IF;
    INSERT INTO location SET name=_locationName;
end;

create or replace definer = root@localhost procedure AddNewUser(IN _login varchar(100), IN _password varchar(100))
BEGIN
    #     Sprawdź, czy taki użytownik już nie istnieje
    SET @userExsists = (SELECT COUNT(login) FROM users WHERE login=_login);
    if(@userExsists) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Użytkownik o podanym loginie już istnieje';
    END IF;
    INSERT INTO users SET login = _login, password = _password;
end;

create or replace definer = root@localhost function VerifyLoginData(_login varchar(50), _password varchar(255)) returns tinyint(1)
BEGIN
    SET @findedROws = (SELECT COUNT(login) FROm users WHERE login = _login AND password = _password );
    RETURN @findedROws;
end;

create procedure inputToWarehouse(IN _locationName varchar(50), IN _assortentName varchar(50), in _total float)
BEGIN
    #     Sprawdź, czy taki użytownik już nie istnieje
    set @locationID = (select locationID from location where name = _locationName limit 1);
    set @assortmentID = (select assortmentID from assortment where name = _assortentName limit 1);
    INSERT INTO stockitem SET assortmentID = @assortmentID, locationID = @locationID, stockLevel = _total;
end;
