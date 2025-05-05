To test pessimistic lock I set timeout on mysql db. By default is set to 50, on test purposes we want to change to 1.<br/>
Here is how to check current lock timeout:<br/>
SHOW GLOBAL VARIABLES LIKE 'innodb_lock_wait_timeout';<br/>
Here is how to update lock timeout:<br/>
SET GLOBAL innodb_lock_wait_timeout = 1;<br/>
