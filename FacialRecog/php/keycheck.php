<?php
$key = 'LbnKHqqqZLw60uctCYh1';
if (isset($_POST['key']))
{
    if ($_POST['key'] == $key) ;
    else {
        die('401 Not Authorized');
    }
}
?>