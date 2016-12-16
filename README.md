# Fish_Game

This game fuses the values from the gyroscope, compass, and accelerometer sensors on your device to move a fishing hook based on sensor changes(tilting device).

Database Implementation

--MYSQL--
# Use the following mysql queries to create the database
# Created by Josh and Dean
 
CREATE DATABASE fishinTime;
USE fishinTime;

Drop table if exists highscores;

CREATE TABLE highscores(
	name CHAR(10) NOT NULL;
	score INT(3) NOT NULL;,
	
);

# Preset values for testing reasons

INSERT INTO highscores (name, score) VALUES ('Spongebob’,'10’);
INSERT INTO highscores (name, score) VALUES (‘Patrick’,’20’);
INSERT INTO highscores (name, score) VALUES (‘Sandy’,’30’);
INSERT INTO highscores (name, score) VALUES (’Squidward’,’99’);
INSERT INTO highscores (name, score) VALUES (‘Garry’,’1’);
INSERT INTO highscores (name, score) VALUES (‘MR. Krabs’,’100’);


# How we will be grabbing the values out of the table

SELECT * FROM highscores ORDER BY score DESC LIMIT 5;

--PHP--
DATABASE INSERT
<?php
$uname="root";
$pwd="Summer2015";
$db="fish_app";

$con = mysqli_connect(localhost,$uname,$pwd, $db);
if (mysqli_connect_errno()){
echo "Failed to connect to MYSQL " . mysqli_connect_error();
}
mysqli_select_db($con, $db);;
$user_name=$_REQUEST['user_name'];
$user_score=$_REQUEST['user_score'];
$sql = "INSERT INTO highscores (name, score) VALUES ('$user_name', '$user_score')";
//$user_name = isset($_POST['user_name']) ? $_POST['user_name'] : ”;
//$user_score = isset($_POST['user_score']) ? $_POST['user_score'] : ”;
$flag['code']=0;

if($result = mysqli_query($con, $sql)){
    $flag['code']=1;
}

print(json_encode($flag));
mysqli_close($con);
?>

DATABASE INFO USED FOR RETRIEVAL

<?php

$myfile = fopen("scoreline.txt", "w") or die("Unable to open file!");
$username="root";
$password="Summer2015";
$database="fish_app";


$link = mysqli_connect(localhost,$username,$password, $database);

if (mysqli_connect_errno())
  {
  echo "Failed to connect to MySQL: " . mysqli_connect_error();
  }



$sql = "SELECT * FROM highscores ORDER BY score DESC LIMIT 10";
if (!$result = $link->query($sql)){
die("there was an error");
}

while($row = $result->fetch_assoc()){
echo $row['name'] . " ";
echo $row['score'] . '<br />';
//print(json_encode($row['name'] . $row['score'] . '<br />'));
fwrite($myfile, $row['name'] . " " . $row['score'] . "\n");
}
mysql_close($link);
fclose($myfile);

?>

