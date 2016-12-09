<?php
 	
 	
 	$con=mysqli_connect("example.com","dspon15","Summer2015","fishinTime");

   if (mysqli_connect_errno($con)) {
      echo "Failed to connect to MySQL: " . mysqli_connect_error();
   }

   mysql_select_db("fishinTime", $con);

   $result = mysqli_query($con,"SELECT * FROM highscore ORDER BY score DESC LIMIT 5");
   $row = mysqli_fetch_array($result);
   $data = $row[0];

   $row = mysqli_fetch_array($result);
   $data = $row[0];

   if($data){
      echo $data;
   }
   
   print (json_encode($output));

   mysqli_close($con);
?>