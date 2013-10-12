<?php
include('keycheck.php');
include('connection.php');
$name = $_POST['name'];
$pass = $_POST['password'];
$query = "SELECT uid, status FROM users WHERE name=\"" . $name . "\" AND password=MD5(\"" . $pass . "\");";
$result = mysql_query($query);
$rows = mysql_num_rows($result);
$data = array();
if($rows == 1)
{
    $row = mysql_fetch_array($result);
    $data['output'][] = 1;
    $data['output'][] = $row['uid'];
    $data['output'][] = $row['status'];
}
else
{
    $data['output'][] = 0;
}
echo json_encode($data);
mysql_close($con);
?>