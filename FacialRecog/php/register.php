<?php
include('keycheck.php');
include('connection.php');

// set server paths
$uploaddir = './uploads/'; // upload file dir
$datadir = './data/'; // data dir for opencv
$convert = '/opt/local/bin/convert'; // imagemagick
$recog = './recog'; // recog executable
$train = './train.txt'; // training file list
$profiledir = './profile_pics/'; // profile pic dir


$name = $_POST['name'];
$sql = "SELECT * FROM users WHERE name=\"" . $name . "\";";
$result = mysql_query($sql);
$rows = mysql_num_rows($result);
$data = array();
if($rows == 0)  // name not used
{
    // add into database
    $password = $_POST['password'];
    $insert = "INSERT INTO users (name, password, status) "
    . "VALUES('$name', MD5('$password'), 'n00b');";
    mysql_query($insert); // add user
    mysql_query($sql); // query again to get uid
    $result = mysql_query($sql);
    $row = mysql_fetch_assoc($result);
    $uid = $row['uid'];

    // process photos for new user
    // file 1
    $file = basename($_FILES['input0']['name']);
    $filenoext = basename($file, ".jpg");
    $uploadfile = $uploaddir . $file;
    $convertfile = $uploaddir . $filenoext . ".pgm";
    $datafile = $datadir . $uid . "-1" . ".pgm";

    // move file from tmp location to upload dir
    if (move_uploaded_file($_FILES['input0']['tmp_name'], $uploadfile)) {
        exec("$convert -resize 256x256 $uploadfile $convertfile"); // convert to PGM
        exec("/bin/mv $convertfile $datafile"); // move to data dir
        $fh = fopen($train, 'a') or die("can't write to training file");
        $filestring = "$uid $uid $datafile\n";
        fwrite($fh, $filestring);
        fclose($fh);

        // profile pic
        mkdir("$profiledir" . "$uid");
        exec("$convert -resize 100x100 $uploadfile $profiledir" . "$uid/" . "profile.jpg");
    }
    else {
        die('Upload error.');
    }

    // file 2
    $file = basename($_FILES['input1']['name']);
    $filenoext = basename($file, ".jpg");
    $uploadfile = $uploaddir . $file;
    $convertfile = $uploaddir . $filenoext . ".pgm";
    $datafile = $datadir . $uid . "-2" . ".pgm";

    // move file from tmp location to upload dir
    if (move_uploaded_file($_FILES['input1']['tmp_name'], $uploadfile)) {
        exec("$convert -resize 256x256 $uploadfile $convertfile"); // convert to PGM
        exec("/bin/mv $convertfile $datafile"); // move to data dir
        $fh = fopen($train, 'a') or die("can't write to training file");
        $filestring = "$uid $uid $datafile\n";
        fwrite($fh, $filestring);
        fclose($fh);
    }
    else {
        die('Upload error.');
    }

    // file 3
    $file = basename($_FILES['input2']['name']);
    $filenoext = basename($file, ".jpg");
    $uploadfile = $uploaddir . $file;
    $convertfile = $uploaddir . $filenoext . ".pgm";
    $datafile = $datadir . $uid . "-3" . ".pgm";

    // move file from tmp location to upload dir
    if (move_uploaded_file($_FILES['input2']['tmp_name'], $uploadfile)) {
        exec("$convert -resize 256x256 $uploadfile $convertfile"); // convert to PGM
        exec("/bin/mv $convertfile $datafile"); // move to data dir
        $fh = fopen($train, 'a') or die("can't write to training file");
        $filestring = "$uid $uid $datafile\n";
        fwrite($fh, $filestring);
        fclose($fh);
    }
    else {
        die('Upload error.');
    }

    exec("$recog train $train"); // retrain openCV
    $data['output'][] = 0; // success
}
else {  // name taken
    $data['output'][] = 1;
}
echo json_encode($data);
mysql_close($con);
?>