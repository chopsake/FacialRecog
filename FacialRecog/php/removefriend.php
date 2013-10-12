<?php
include('keycheck.php');
include('connection.php');

$uid = $_POST['uid'];
$friendid = $_POST['friendid'];

$query = "DELETE FROM friends WHERE (uid = " . $uid . " AND friend_id = " . $friendid . ") OR (uid = " . $friendid . " AND friend_id =  " . $uid . ");";
mysql_query($query);
?>