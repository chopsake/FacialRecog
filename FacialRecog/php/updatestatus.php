<?php
include('keycheck.php');
include('connection.php');
$uid = $_POST['uid'];
$status = $_POST['status'];
$query = "UPDATE users SET status = \"" . $status . "\" WHERE uid = " . $uid . ";";
mysql_query($query);
mysql_close($con);
?>