<?php
//include('keycheck.php');
include('connection.php');

// set server paths
$uploaddir = './uploads/';
$convert = '/opt/local/bin/convert';
$recog = './recog';

$file = basename($_FILES['input0']['name']);
$filenoext = basename($file, ".jpg");
$uploadfile = $uploaddir . $file;
$convertfile = $uploaddir . $filenoext . ".pgm";

// move file from tmp location to upload dir
if (move_uploaded_file($_FILES['input0']['tmp_name'], $uploadfile)) {
    exec("$convert -resize 256x256 $uploadfile $convertfile");
    $result0 = exec("$recog $convertfile");
} else {
    die('Upload error.');
}

$file = basename($_FILES['input1']['name']);
$filenoext = basename($file, ".jpg");
$uploadfile = $uploaddir . $file;
$convertfile = $uploaddir . $filenoext . ".pgm";

if (move_uploaded_file($_FILES['input1']['tmp_name'], $uploadfile)) {
    exec("$convert -resize 256x256 $uploadfile $convertfile");
    $result1 = exec("$recog $convertfile");
} else {
    die('Upload error.');
}

$file = basename($_FILES['input2']['name']);
$filenoext = basename($file, ".jpg");
$uploadfile = $uploaddir . $file;
$convertfile = $uploaddir . $filenoext . ".pgm";

if (move_uploaded_file($_FILES['input2']['tmp_name'], $uploadfile)) {
    exec("$convert -resize 256x256 $uploadfile $convertfile");
    $result2 = exec("$recog $convertfile");
} else {
    die('Upload error.');
}

// best 2 out of 3 non-blank gets the name
if ($result0 != "" && ($result0 == $result1 || $result0 == $result2)) {
    $userid = $result0;
}
else if ($result1 == $result2 && $result1 != "") {
    $userid = $result1;
}
else if ($result2 != "") {
    $userid = $result2;
} else {
    die('Cannot identify.');
}

$query = "SELECT name FROM users WHERE uid=" . $userid . ";";
$result = mysql_query($query);
$row = mysql_fetch_array($result);

echo $row['name'];

mysql_close($con);
?>