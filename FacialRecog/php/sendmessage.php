<?php
include('keycheck.php');
include('connection.php');
$fromid = $_POST['uid'];
$toid = $_POST['id'];
$msg = $_POST['msg'];
$date = $_POST['date'];
$query = "INSERT INTO wall (uid, text, date, poster_uid) VALUES (" . $toid . ", \"" . $msg . "\", \"" . $date . "\", " . $fromid . ");";
mysql_query($query);
mysql_close($con);
?>