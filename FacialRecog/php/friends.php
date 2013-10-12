<?php
include('keycheck.php');
include('connection.php');
$uid = $_POST['uid'];
$query = "SELECT u.name, u.uid, u.status FROM users u, friends f WHERE u.uid = f.friend_id AND f.uid = " . $uid . ";";
$result = mysql_query($query);
$num_rows = mysql_num_rows($result);
$data = array();

if ($num_rows > 0) {
    $data['output'][] = $num_rows;
    while ($row = mysql_fetch_assoc($result)) {
        $data['output'][] = $row['uid'];
        $data['output'][] = $row['name'];
        $data['output'][] = $row['status'];
    }
}
else {
    $data['output'][] = 0;
}

echo json_encode($data);
mysql_close($con);
?>