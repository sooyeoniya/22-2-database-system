--Non-repeatable reads T6

-- ensure we use SQL Server default isolation level
SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
 
use AdventureWorks2019
go

BEGIN TRANSACTION;
 
UPDATE Person.Person
SET 
    Suffix = 'Clothes'
WHERE 
    LastName = 'Abercrombie'
AND FirstName = 'Kim';
 
COMMIT TRANSACTION;