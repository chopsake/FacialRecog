<?php
include('keycheck.php');
include('connection.php');
$uid = $_POST['uid'];
$query = "SELECT u.name, date_format(w.date, '%M %d %l:%i %p') as date, w.text FROM users u, wall w WHERE u.uid = w.poster_uid AND w.uid = " . $uid . " ORDER BY w.date DESC;";
$result = mysql_query($query);
$num_rows = mysql_num_rows($result);
$data = array();

if ($num_rows > 0) {
    $data['output'][] = $num_rows;
    while ($row = mysql_fetch_assoc($result)) {
        $data['output'][] = $row['name'];
        $data['output'][] = $row['date'];
        $data['output'][] = $row['text'];
    }
}
else {
    $data['output'][] = 0;
}

echo json_encode($data);
mysql_close($con);
?>