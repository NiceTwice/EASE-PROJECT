DELIMITER //

CREATE PROCEDURE addEmail(IN p_user_id INT(10) UNSIGNED, IN p_email VARCHAR(100))
BEGIN
  SELECT count(id) INTO @var FROM usersEmails WHERE user_id = p_user_id AND email = p_email;
  IF @var = 0 THEN
    INSERT INTO usersEmails values (NULL, p_user_id, p_email, 0, 0);
  END IF;
END //

DELIMITER //

CREATE PROCEDURE deleteUser(IN p_user_id INT(10) UNSIGNED)
BEGIN
  START TRANSACTION;
  DROP TEMPORARY TABLE IF EXISTS accountsIds;
  DROP TEMPORARY TABLE IF EXISTS profilesIds;
  CREATE TEMPORARY TABLE profilesIds AS
    SELECT profile_id FROM profiles WHERE user_id = p_user_id;
  CREATE TEMPORARY TABLE appsIds AS
    SELECT app_id FROM apps JOIN profilesIds ON apps.profile_id = profilesIds.profile_id;
  CREATE TEMPORARY TABLE accountsIds AS
    SELECT account_id FROM apps JOIN profilesIds ON apps.profile_id = profilesIds.profile_id;
  DELETE FROM savedSessions where user_id = p_user_id;
  DELETE FROM usersemails where user_id = p_user_id;
  DELETE logWithAccounts FROM logWithAccounts JOIN accountsIds ON logWithAccounts.account_id = accountsIds.account_id;
  DELETE ClassicAccountsInformations FROM ClassicAccountsInformations JOIN accountsIds ON ClassicAccountsInformations.account_id = accountsIds.account_id;
  DELETE classicAccounts FROM classicAccounts JOIN accountsIds ON classicAccounts.account_id = accountsIds.account_id;
  DELETE linkAccounts FROM linkAccounts JOIN accountsIds ON linkAccounts.account_id = accountsIds.account_id;
  DELETE apps FROM apps JOIN profilesIds ON apps.profile_id = profilesIds.profile_id;
  DELETE accounts FROM accounts JOIN accountsIds ON accounts.account_id = accountsIds.account_id;
  DELETE FROM profiles where user_id = p_user_id;
  DELETE FROM GroupAndUserMap where user_id = p_user_id;
  DELETE FROM usersEmails where user_id = p_user_id;
  DELETE FROM users where user_id = p_user_id;
  COMMIT;
END //

DELIMITER ;

DELIMITER //

CREATE PROCEDURE increaseRatio(IN p_website_id INT(10) UNSIGNED)
BEGIN
  UPDATE websites SET ratio = ratio + 1 WHERE website_id = p_website_id;
END //

CREATE PROCEDURE decreaseRatio(IN p_website_id INT(10) UNSIGNED)
BEGIN
  UPDATE websites SET ratio = IF(ratio>0,ratio - 1,ratio) WHERE website_id = p_website_id;
END //

CREATE PROCEDURE setRatio(IN p_website_id INT(10) UNSIGNED, IN new_ratio INT(10) UNSIGNED)
BEGIN
  UPDATE websites SET ratio = new_ratio WHERE website_id = p_website_id;
END //

CREATE PROCEDURE lockWebsite(IN p_website_id INT(10) UNSIGNED)
BEGIN
  START TRANSACTION;
  SELECT position INTO @var FROM websites WHERE website_id = p_website_id;
  SET @i=2;
  UPDATE websites SET position = (@i:=@i+1) WHERE position < @var ORDER BY position;
  UPDATE websites SET locked = 1, position = 2, lockedExpiration = CURDATE() + INTERVAL 3 DAY WHERE website_id = p_website_id;
  COMMIT;
END //

CREATE PROCEDURE unlockWebsites()
BEGIN
  UPDATE websites SET locked = 0 WHERE lockedExpiration >= CURDATE();
END //

CREATE PROCEDURE updateWebsitesPositions()
BEGIN
  START TRANSACTION;
  CALL unlockWebsites();
  UPDATE websites SET position = 1 WHERE insertDate >= CURDATE() - INTERVAL 3 DAY ORDER BY ratio DESC;
  UPDATE websites SET position = 2 WHERE locked = 1;
  SET @i=2;
  UPDATE websites SET position = (@i:=@i+1) WHERE insertDate < CURDATE() - INTERVAL 3 AND locked = 0 DAY ORDER BY ratio DESC;
  COMMIT;
END //

DELIMITER ;

