use AdventureWorks2019
go
DBCC LOGINFO

use AdventureWorks2019
go
select * from sys.fn_dblog(null, null)


--- Using an explicit transaction
use AdventureWorks2019
go

use AdventureWorks2019
go
BEGIN TRANSACTION;  
DELETE FROM HumanResources.JobCandidate  
    WHERE JobCandidateID = 77713;  
COMMIT ; 

-- Rolling back a transaction
use DreamHome
go
CREATE TABLE ValueTable (id INT);
select * from ValueTable
BEGIN TRANSACTION;  
       INSERT INTO ValueTable VALUES(1);  
       INSERT INTO ValueTable VALUES(2); 
	   select * from ValueTable
ROLLBACK;  
select * from ValueTable

drop table ValueTable


--- Naming a transaction
USE AdventureWorks2019;
go

DECLARE @TranName VARCHAR(20);  
SELECT @TranName = 'MyTransaction';  


BEGIN TRANSACTION @TranName;  
DELETE FROM HumanResources.JobCandidate  
    WHERE JobCandidateID = 13;  
  
COMMIT TRANSACTION @TranName;  
GO  

---Marking a transaction
BEGIN TRANSACTION CandidateDelete  
    WITH MARK N'Deleting a Job Candidate';  
GO  
USE AdventureWorks2019;  
GO  
DELETE FROM HumanResources.JobCandidate  
    WHERE JobCandidateID = 13;  
GO  
COMMIT TRANSACTION CandidateDelete;  
GO  

---Examples 

USE tempdb;  
GO  
CREATE TABLE ValueTable ([value] INT);  
GO  
  
DECLARE @TransactionName VARCHAR(20) = 'Transaction1';  
  
BEGIN TRAN   
       INSERT INTO ValueTable VALUES(1), (2);  

ROLLBACK TRAN ;  
  
INSERT INTO ValueTable VALUES(3),(4);  
  
SELECT [value] FROM ValueTable;  
  
DROP TABLE ValueTable;  

--- Committing a nested transaction
use DreamHome 
go
IF OBJECT_ID(N'TestTran',N'U') IS NOT NULL  
    DROP TABLE TestTran;  
GO  
CREATE TABLE TestTran (Cola INT PRIMARY KEY, Colb CHAR(3));  
GO  
-- This statement sets @@TRANCOUNT to 1.  
BEGIN TRANSACTION OuterTran;  
  
PRINT N'Transaction count after BEGIN OuterTran = '  
    + CAST(@@TRANCOUNT AS NVARCHAR(10));  
 
INSERT INTO TestTran VALUES (1, 'aaa');  
 
-- This statement sets @@TRANCOUNT to 2.  
BEGIN TRANSACTION Inner1;  
 
PRINT N'Transaction count after BEGIN Inner1 = '  
    + CAST(@@TRANCOUNT AS NVARCHAR(10));  
  
INSERT INTO TestTran VALUES (2, 'bbb');  
  
-- This statement sets @@TRANCOUNT to 3.  
BEGIN TRANSACTION Inner2;  
  
PRINT N'Transaction count after BEGIN Inner2 = '  
    + CAST(@@TRANCOUNT AS NVARCHAR(10));  
  
INSERT INTO TestTran VALUES (3, 'ccc');  
  
-- This statement decrements @@TRANCOUNT to 2.  Nothing is committed.  
COMMIT TRANSACTION Inner2;  
 
PRINT N'Transaction count after COMMIT Inner2 = '  
    + CAST(@@TRANCOUNT AS NVARCHAR(10));  
 
-- This statement decrements @@TRANCOUNT to 1. Nothing is committed.  
COMMIT TRANSACTION Inner1;  
 
PRINT N'Transaction count after COMMIT Inner1 = '  
    + CAST(@@TRANCOUNT AS NVARCHAR(10));  
  
-- This statement decrements @@TRANCOUNT to 0 and commits outer transaction OuterTran.  
COMMIT TRANSACTION OuterTran;  
  
PRINT N'Transaction count after COMMIT OuterTran = '  
    + CAST(@@TRANCOUNT AS NVARCHAR(10)); 


