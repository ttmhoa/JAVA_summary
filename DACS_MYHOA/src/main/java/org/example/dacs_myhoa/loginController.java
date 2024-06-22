package org.example.dacs_myhoa;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


public class loginController implements Initializable {
    @FXML
    private Button btlogin;

    @FXML
    private Button btsignin;

    @FXML
    private Button btsignup;

    @FXML
    private Button btsignupplus;

    @FXML
    private Button close;

    @FXML
    private TextField email;

    @FXML
    private AnchorPane mainform;

    @FXML
    private PasswordField password;

    @FXML
    private PasswordField passwordplus;

    @FXML
    private TextField username;

    @FXML
    private TextField usernameplus;
    @FXML
    private AnchorPane sub_form;
    @FXML
    private Label lablesilde;
    @FXML
    private AnchorPane formmain2;
    @FXML
    private AnchorPane mainform3;
    private PreparedStatement preparedStatement;
    private ResultSet result;
    public static String userNameLogin;
    public static int userId;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public  void login(){
        String sql= "Select * from users where username=? and password=?";
        INDEXJDBC connection = new INDEXJDBC();
         Connection con= connection.getConnection();
         try {
             preparedStatement =con.prepareStatement(sql);
             preparedStatement.setString(1 ,username.getText());
             preparedStatement.setString(2,password.getText());
             result= preparedStatement.executeQuery();
             Alert alert;
             MessageDigest md = null;
             try {
                 md = MessageDigest.getInstance("SHA-256");
             } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
                 noSuchAlgorithmException.printStackTrace();
             }
             md.update(password.getBytes());
             byte[] bytes = md.digest();
             StringBuilder sb = new StringBuilder();
             for (int i = 0; i < bytes.length; i++) {
                 sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
             }
             String hashedPassword = sb.toString();



             if(username.getText().isEmpty() || password.getText().isEmpty()){
                 alert= new Alert(Alert.AlertType.ERROR);
                 alert.setTitle("Error Message");
                 alert.setHeaderText(null);
                 alert.setContentText("Please fill all blank fields");
                 alert.showAndWait();
             }else {
                 if(result.next()){
                     DataUser.username = username.getText();
                     alert= new Alert(Alert.AlertType.INFORMATION);
                     alert.setTitle("Information Message");
                     alert.setHeaderText(null);
                     alert.setContentText("Successfully Login!");

                     userId = result.getInt("id");
                     alert.showAndWait();
                     sliderloginaction ();
                     btlogin.getScene().getWindow().hide();

                 }
                 else {
                     alert= new Alert(Alert.AlertType.ERROR);
                     alert.setTitle("Error Login");
                     alert.setHeaderText(null);
                     alert.setContentText("Wrong Username/Password");
                     alert.showAndWait();
                 }
             }

         }catch (Exception e){
             //noinspection CallToPrintStackTrace
             e.printStackTrace();
         }

    }
    public void signup(){
        String sql ="insert into dbo.users (email,username,password,created_at) values(?,?,?,?)";
        INDEXJDBC connection = new INDEXJDBC();
        Connection con= connection.getConnection();
        Alert alert;
       try {
           if(email.getText().isEmpty() || usernameplus.getText().isEmpty()|| passwordplus.getText().isEmpty()){
               alert= new Alert(Alert.AlertType.ERROR);
               alert.setTitle("Error Message");
               alert.setHeaderText(null);
               alert.setContentText("Please fill all blank fields");
               alert.showAndWait();
           }else {
               if (passwordplus.getText().length()<8){
                   alert= new Alert(Alert.AlertType.ERROR);
                   alert.setTitle("Error Message");
                   alert.setHeaderText(null);
                   alert.setContentText("Invalid password :3");
                   alert.showAndWait();
               }else {
                   Timestamp createdAt = new Timestamp(System.currentTimeMillis());
                   preparedStatement =con.prepareStatement(sql);
                   preparedStatement.setString(1,email.getText());
                   preparedStatement.setString(2,usernameplus.getText());
                   preparedStatement.setString(3,passwordplus.getText());
                   preparedStatement.setTimestamp(4, createdAt);
                   alert= new Alert(Alert.AlertType.INFORMATION);
                   alert.setTitle("Information Message");
                   alert.setHeaderText(null);
                   alert.setContentText(" Successfully Create new account ");

                   alert.showAndWait();
                   preparedStatement.executeUpdate();
                   email.setText("");
                   usernameplus.setText("");
                   passwordplus.setText("");
               }

           }


       }catch (Exception e){
           e.printStackTrace();
       }

    }
    public void signupSlider(){
        TranslateTransition slider1= new TranslateTransition();
        slider1.setNode(sub_form);
        slider1.setToX(341);
        slider1.setDuration(Duration.seconds(.5));
        slider1.play();
        slider1.setOnFinished((ActionEvent event) ->{
            lablesilde.setText("Login Account");
            btsignup.setVisible(false);
            btsignin.setVisible(true);
            formmain2.setVisible(false);
            mainform3.setVisible(true);


        });


    }
    public  void loginSilder(){
        TranslateTransition slider1= new TranslateTransition();
        slider1.setNode(sub_form);
        slider1.setToX(0);
        slider1.setDuration(Duration.seconds(.5));
        slider1.play();
        slider1.setOnFinished((ActionEvent event) ->{
            lablesilde.setText("Create Account");
            btsignup.setVisible(true);
            btsignin.setVisible( false);
            formmain2.setVisible(true);
            mainform3.setVisible(false);

        });
    }
    public  void closebtaction(){
        Stage stage= (Stage) close.getScene().getWindow();
        stage.close();
    }
    private double x = 0;
    private double y = 0;
    public void sliderloginaction (){
        try {
            Stage stage=  new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("Dashboard.fxml"));
            Scene scene = new Scene(root);
            root.setOnMousePressed((MouseEvent event) ->{
                x= event.getScreenX();
                y= event.getScreenY();
            });
            root.setOnMouseDragged((MouseEvent event) ->{
                stage.setX(event.getScreenX() - x);
                stage.setY(event.getScreenY() -y);
                stage.setOpacity(.8);

            });
            root.setOnMouseReleased((MouseEvent event) ->{
                stage.setOpacity(1);
            });
            stage.initStyle(StageStyle.UNDECORATED);
            scene.getStylesheets().add(getClass().getResource("DashBoard.Css").toExternalForm());
            stage.setTitle("Gym Management Application");
            stage.setScene(scene);
            stage.show();



        }catch (Exception e){
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
            e.getCause();
        }
    }




}
