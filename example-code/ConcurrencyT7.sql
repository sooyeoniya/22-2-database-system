
-- Phantom reads  T7
use DreamHome
go

IF (OBJECT_ID('dbo.Employee') IS NOT NULL)
BEGIN
    DROP TABLE [dbo].[Employee];
END;
 
CREATE TABLE [dbo].[Employee] (
    EmpId       int IDENTITY(1,1) NOT NULL,
    EmpName     nvarchar(32)      NOT NULL,
    CONSTRAINT pk_EmpId 
        PRIMARY KEY CLUSTERED (EmpId)
);

-- ensure we use SQL Server default isolation level
SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
 
BEGIN TRANSACTION;
 
-- Query 1 - first run
SELECT *
FROM dbo.Employee
;
 
-- let some time for session 2
WAITFOR DELAY '00:00:10.000';
 
-- Query 1 - second run
SELECT *
FROM dbo.Employee
;
 
COMMIT TRANSACTION;