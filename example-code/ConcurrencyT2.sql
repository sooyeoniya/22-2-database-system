-- last update T2
-- Session 2: Web reseller
 use DreamHome
 go

DECLARE @CustomerBalance	INT ;
DECLARE @BalanceDifference	INT ;
 
SET @BalanceDifference = 40 ;
 
BEGIN TRANSACTION ;
	-- Getting back current balance value
	SELECT @CustomerBalance = BalanceAmount
	FROM BankAccounts
	WHERE AccountId = 1 ;
 
	PRINT 'Read Balance value: ' + CONVERT(VARCHAR(32),@CustomerBalance);
 
	-- adding salary amount
	SET @CustomerBalance = @CustomerBalance + @BalanceDifference ;
 
PRINT 'New Balance value: ' + CONVERT(VARCHAR(32),@CustomerBalance);
 
	-- updating in table
	UPDATE BankAccounts
	SET BalanceAmount = @CustomerBalance 
	WHERE AccountId = 1 ;
 
	-- display results for user
	SELECT BalanceAmount as BalanceAmountSession2
	FROM BankAccounts
	WHERE AccountId = 1 ;
COMMIT ;