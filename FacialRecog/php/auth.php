<?php
$name = $_POST['name'];
$pass = $_POST['password'];
$query = "SELECT id FROM users WHERE name=\"" . $name . "\" AND password=MD5(\"" . $pass . "\");";
$result = mysql_query($query);
$rows = mysql_num_rows($result);
if($rows == 1)
{
    $row = mysql_fetch_array($result);
    $userID = $row['id'];
}
else {
    die('User Not Found');
}
?>
