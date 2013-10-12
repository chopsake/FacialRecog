<?php
include('keycheck.php');
include('connection.php');

$uid = $_POST['uid'];
$friendName = $_POST['friendName'];

$query = "SELECT uid FROM users WHERE name = \"" . $friendName  . "\";";
$result = mysql_query($query);
$row = mysql_fetch_assoc($result);
$friendid = $row['uid'];

$query = "SELECT COUNT(*) AS count FROM friends WHERE uid = " . $uid . " AND friend_id = " . $friendid . ";";
$result = mysql_query($query);
$row = mysql_fetch_assoc($result);
$count = $row['count'];

$data = array();

if ($count == 0 && $uid != $friendid) {
    $query = "INSERT INTO friends (uid, friend_id) VALUES (" . $uid . ", " . $friendid . "), (" . $friendid . ", " . $uid . ");";
    mysql_query($query);
    $data['output'][] = 0;
}
else {
    $data['output'][] = 1;
}

echo json_encode($data);
mysql_close($con);
?>