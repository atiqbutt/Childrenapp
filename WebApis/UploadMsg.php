<?php
public function uploadSms(){
    $pnoneNumber= $this->input->post('pnoneNumber',true);
    $messageDate= $this->input->post('messageDate',true);
    $messageType= $this->input->post('messageType',true);
    $messageBody= $this->input->post('messageBody',true);
    $name= $this->input->post('name',true);
    $chidId= $this->input->post('chidId',true);
    $query = $this->db->query("INSERT INTO `SmsTable`(`phone_number`, `message_type`, `message_date`, `message_body`, `name`, `child_id`) 
    	VALUES ('$pnoneNumber','$messageType','$messageDate','$messageBody','$name','$chidId')");
    
}

public function uploadCaalLog(){
    $pnoneNumber= $this->input->post('pnoneNumber',true);
    $callDate= $this->input->post('callDate',true);
    $callType= $this->input->post('callType',true);
    $callDuration= $this->input->post('callDuration',true);
    $name= $this->input->post('name',true);
    $chidId= $this->input->post('chidId',true);
    $query = $this->db->query("INSERT INTO `Call_log`(`phone_number`, `call_type`, `call_date`, `call_duration`, `name`, `child_id`) 
    	VALUES ('$pnoneNumber','$callType','$callDate','$callDuration','$name','$chidId')");
    
}
public function changePassword(){
    $password= $this->input->post('password',true);
    $id= $this->input->post('id',true);
   
    $query = $this->db->query("UPDATE `user` SET `password`='$password' WHERE `id` ='$id'");
    
}
public function changePassword(){
    $location= $this->input->post('location',true);
    $id= $this->input->post('id',true);
   
    $query = $this->db->query("UPDATE `user` SET `current_location_coordinates`='$location' WHERE `id` ='$id'");
    
}
?>