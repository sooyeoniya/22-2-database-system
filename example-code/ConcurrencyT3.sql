
SET TRANSACTION ISOLATION LEVEL READ UNCOMMITTED;
--Dirty update  T3
use AdventureWorks2019
go


SELECT 
    COUNT(DISTINCT LastName) DistinctLastNameBeforeBeginTran
FROM Person.Person
WHERE FirstName = 'Aaron';
 
BEGIN TRANSACTION;
 
UPDATE Person.Person
SET LastName = 'Hotchner'
WHERE FirstName = 'Aaron'
;
 
SELECT 
    COUNT(DISTINCT LastName) DistinctLastNameInTransaction
FROM Person.Person
WHERE FirstName = 'Aaron';
 
WAITFOR DELAY '00:00:10.000';
 
ROLLBACK TRANSACTION;
 
SELECT 
    COUNT(DISTINCT LastName) DistinctLastNameAfterRollback
FROM Person.Person
WHERE FirstName = 'Aaron';