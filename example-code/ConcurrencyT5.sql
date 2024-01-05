--Non-repeatable reads T5

-- ensure we use SQL Server default isolation level
SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
 
use AdventureWorks2019
go


BEGIN TRANSACTION;
 
-- Query 1 - first run
SELECT TOP 5
    FirstName,
    MiddleName,
    LastName,
    Suffix
FROM Person.Person
ORDER BY LastName
;
 
-- let some time for session 2
WAITFOR DELAY '00:00:10.000';
 
-- Query 1 - second run
SELECT TOP 5
    FirstName,
    MiddleName,
    LastName,
    Suffix
FROM Person.Person
ORDER BY LastName
;
 
COMMIT TRANSACTION;