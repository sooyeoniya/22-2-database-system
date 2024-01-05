
--Dirty update  T4
use AdventureWorks2019
go

SELECT 
    COUNT(DISTINCT LastName) SecondSessionResults
FROM Person.Person
WHERE FirstName = 'Aaron';