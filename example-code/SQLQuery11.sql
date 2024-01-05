

begin tran
print @@TRANCOUNT 
insert into Branch values('B879', 'fgsdffdfd', 'fgsdffdfd', 'dffdfd')
commit;
print @@TRANCOUNT 
select * from Branch

begin tran
insert into Branch values('B778', 'fgsdffdfd', 'fgsdffdfd', 'dffdfd')
print @@TRANCOUNT 
rollback;

select * from Branch