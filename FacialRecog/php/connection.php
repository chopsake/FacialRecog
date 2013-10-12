<?php
$db_host = '127.0.0.1:8889';
$db_user = 'faceuser';
$db_pass = '2legit2quit';
$con = mysql_connect($db_host, $db_user, $db_pass);
if(!$con) {
    die('Could not connect: ' . mysql_error());
}
mysql_select_db("face", $con);
?>