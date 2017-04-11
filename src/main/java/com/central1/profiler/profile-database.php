<?php
  $data = json_decode($_POST[0]);
  $database = mysqli_connect("127.0.0.1", "build_user", "pr0filer", "build_db");

  if (!$database) {
    echo "Error: Unable to connect to MySQL." . PHP_EOL;
    echo "Debugging errno: " . mysqli_connect_errno() . PHP_EOL;
    echo "Debugging error: " . mysqli_connect_error() . PHP_EOL;
    exit;
  }

  $file = fopen("output.out", "w+");

  fwrite($file, $data["project_name"]);
  fclose($file);

  /*
  project_name
  time
  machine_name
  developer_name
  goals
  date
  parameters
  projects
    project : project name
    time: time of project

  for each (projects as prj) {
    pjr[project]
    prj[time]
}
   */


/*
   $build_row = $database->prepare("INSERT INTO Builds VALUES (?,?,?,?,?,?,?)");

   $topProject = $data["project_name"];
   $time = $data["time"];
   $machine_name = $data["machine_name"];
   $developer_name = $data["developer_name"];
   $goals = $data["goals"];
   $date = $data["date"];
   $parameters = $data["parameters"];


   $build_row->bind_param('sssssss', $project_name, $time, $machine_name, $developer_name, $goals, $date, $parameters);
   $build_row->execute();


   $id = $database->insert_id;

   $stmt = $database->prepare("INSERT INTO BuildProjects (buildID, project, time) VALUES (?, ?, ?)");
   $stmt->bind_param('iss',$id, $project, $time);

   foreach($data["projects"] as $prj) {
     $project =$prj["project"];
     $time = $prj["time"];
     $stmt->execute();
   }
   */

?>
