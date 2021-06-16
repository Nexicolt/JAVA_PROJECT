create or replace table assortment
(
    assortmentID int auto_increment
        primary key,
    name varchar(100) not null,
    constraint name
        unique (name)
);

create or replace table contracotrs
(
    contractorID int auto_increment
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

create or replace definer = root@localhost procedure DoOutput(IN _sendTo varchar(50), IN _sendFrom varchar(50), IN _assortmentName varchar(100), IN _locationName varchar(100), IN _assortmentCount float)
BEGIN

    SET @assortmentID = (SELECT assortmentID FROm assortment WHERE name=_assortmentName);
    IF(@assortmentID IS NULL) THEN
        SEt @msg = CONCAT('Towar o nazwie ', _assortmentName, ' nie istnieje');
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = @msg ;
    end if;

    SET @locationID = (SELECT  locationID FROm location WHERE name = _locationName);
    IF(@locationID IS NULL) THEN
        SEt @msg = CONCAT('Lokalizacja o nazwie ', _locationName, ' nie istnieje');
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = @msg ;
    end if;

    SET @sendToID = (SELECT contractorID FROm contracotrs WHERE name = _sendTo);
    IF(@sendToID IS NULL) THEN
        SEt @msg = CONCAT('Klient o nazwie ', _sendTo, ' nie istnieje');
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = @msg ;
    end if;

    SET @sendFromID = (SELECT contractorID FROm contracotrs WHERE name = _sendFrom);
    IF(@sendFromID IS NULL) THEN
        SEt @msg = CONCAT('Klient o nazwie ', _sendFrom, ' nie istnieje');
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = @msg ;
    end if;

    SET @AssortmentLevel = (SELECT stockLevel FROm stockitem WHERE locationID=@locationID AND assortmentID=@assortmentID);
    if(@AssortmentLevel IS NULL) THEN
        SEt @msg = CONCAT('Towar o nazwie ', _assortmentName, ' nie istnieje na lokalizacji ', _locationName);
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = @msg ;
    end if;

    if(@AssortmentLevel < _assortmentCount)THEN
        SEt @msg = CONCAT('Towar o nazwie ', _assortmentName, ' z lokalizacji ', _locationName, ' ma zbyt małą ilośc stanu na magazynie. Konieczne przyjęcie');
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = @msg ;
    end if;

    UPDATE stockitem SET stockLevel=stockLevel-_assortmentCount WHERE locationID=@locationID AND assortmentID = @assortmentID;

    DELETE FROM stockitem WHERE stockLevel =0;

end;

create or replace definer = root@localhost procedure GetStockItem(IN _assortmentName varchar(50), IN _locationName varchar(50))
BEGIN

    set _assortmentName = if(_assortmentName = '', NULL, _assortmentName);
    set _locationName = if(_locationName = '', NULL, _locationName);

    IF (_assortmentName IS NOT NULL) THEN

        SET @assortmentID = (SELECT assortmentID
                             FROm assortment
                             WHERE name LIKE CONCAT('%', _assortmentName, '%')
                             LIMIT 1);
        if (@assortmentID IS NULL) THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Asortyment o podanej nazwie nie istnieje';
        end if;

        IF (_locationName IS NOT NULL) THEN
            SET @locationID =
                    (SELECT locationID
                     FROm location
                     WHERE name LIKE CONCAT('%', _locationName, '%')
                     LIMIT 1);
            if (@locationID IS NULL) THEN
                SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Lokalizacja o podanej nazwie nie istnieje';
            end if;

            SELECT a.name as 'Assortment', l.name as "Location", stockLevel as "Count"
            FROM stockitem
                     INNER JOIn assortment a on stockitem.assortmentID = a.assortmentID
                     INNER JOIN location l on stockitem.locationID = l.locationID
            WHERE stockitem.locationID = @locationID
              AND stockitem.assortmentID = @assortmentID;
        end if;

        SELECT a.name as 'Assortment', l.name as "Location", stockLevel as "Count"
        FROM stockitem
                 INNER JOIn assortment a on stockitem.assortmentID = a.assortmentID
                 INNER JOIN location l on stockitem.locationID = l.locationID
        WHERE stockitem.assortmentID = @assortmentID;


    end if;
    IF (_locationName IS NOT NULL AND _assortmentName IS NULL) THEn
        SET @locationID =
                (SELECT locationID
                 FROm location
                 WHERE name LIKE CONCAT('%', _locationName, '%')
                 LIMIT 1);
        if (@locationID IS NULL) THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Lokalizacja o podanej nazwie nie istnieje';
        end if;

        SELECT a.name as 'Assortment', l.name as "Location", stockLevel as "Count"
        FROM stockitem
                 INNER JOIn assortment a on stockitem.assortmentID = a.assortmentID
                 INNER JOIN location l on stockitem.locationID = l.locationID
        WHERE stockitem.locationID = @locationID;
    end if;

    IF (_locationName IS NULL AND _assortmentName IS NULL) THEn
        SELECT a.name as 'Assortment', l.name as "Location", stockLevel as "Count"
        FROM stockitem
                 INNER JOIn assortment a on stockitem.assortmentID = a.assortmentID
                 INNER JOIN location l on stockitem.locationID = l.locationID;
    end if;


end;

create or replace definer = root@localhost function VerifyLoginData(_login varchar(50), _password varchar(255)) returns tinyint(1)
BEGIN
    SET @findedROws = (SELECT COUNT(login) FROm users WHERE login = _login AND password = _password );
    RETURN @findedROws;
end;

