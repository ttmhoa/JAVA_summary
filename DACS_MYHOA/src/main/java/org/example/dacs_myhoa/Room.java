package org.example.dacs_myhoa;


import ChatApplicationClient.src.MessageController.ClientMessageProcessor;
import ChatApplicationClient.src.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;


import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.control.DatePicker;
import model.ConversationModel;
import model.MessageModel;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Optional;

import static org.example.dacs_myhoa.loginController.userId;

public class Room extends Thread implements Initializable {
    @FXML
    private Button btn_send;
    @FXML
    private TextArea msgRoom;
    @FXML
    private Button close;
    @FXML
    private Button profile;
    @FXML
    private AnchorPane panehome;
    @FXML
    private AnchorPane profileanchor;
    @FXML
    VBox messId;
    @FXML
    private TextField nickname;
    @FXML
    private TextField phonenumber;
    @FXML
    private ImageView imgprofile;
    @FXML
    private DatePicker date;
    @FXML
    private TextField searchhere;
    @FXML
    private ListView<ConversationModel> listviewUser;
    @FXML
    private VBox listuser;
    @FXML
    private TextField msgField;
    @FXML
    private Label emailProfile;
    @FXML
    private Label nicknameProfile;
    private PreparedStatement preparedStatement;

    private final Connection con;

    public Room() {
        INDEXJDBC obj = new INDEXJDBC();
        this.con = obj.getConnection();
    }

    BufferedReader reader;
    PrintWriter writer;
    Socket socket;
    private ResultSet rs;

    private  ClientMessageProcessor messageProcessor;

    public void connection(){
        try{
            socket = new Socket("localhost", 12345);
            messageProcessor = new ClientMessageProcessor(socket);
            messageProcessor.connect(userId,DataUser.username);
            System.out.println("Connected to Chat Server.");
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @FXML
    public void searchConversationer() {
        String keywork = searchhere.getText();
        messageProcessor.searchConversation(keywork);
    }

    public void getItemUser(MouseEvent event) {
        ConversationModel selectedConversation = listviewUser.getSelectionModel().getSelectedItem();
        if (selectedConversation != null) {
            int conversationId = selectedConversation.getId();
            messageProcessor.getMessage(conversationId);
            sendMessageRoom(conversationId);
        }
    }

    public void createConversationRoom() {

////         Get the selected conversation
//        ConversationModel selectedConversation = listviewUser.getSelectionModel().getSelectedItem();
//        if (selectedConversation != null) {
//            // Get the conversation ID
//           int  conversationId = selectedConversation.getId();
//
//            // Get the selected user names
//            List<String> selectedItemNames = listviewUser.getSelectionModel().getSelectedItems()
//                    .stream()
//                    .map(ConversationModel::getName)
//                    .collect(Collectors.toList());
//            String conversationName = String.join(", ", selectedItemNames);
//
//            // Get the selected user IDs
//            List<Integer> memberIds = listviewUser.getSelectionModel().getSelectedItems()
//                    .stream()
//                    .map(ConversationModel::getId)
//                    .collect(Collectors.toList());
//
//            // Create the conversation
//            messageProcessor.createConversation(new ConversationModel(conversationId, conversationName, memberIds));
//        }
    }
    public void sendMessageRoom(int conversationId) {
        String content = msgField.getText();
        MessageModel cm = new MessageModel(conversationId, userId, content, (String) null);
        messageProcessor.sendMessage(cm);
    }



    public HBox renderMessage(MessageModel message, Boolean isMe) {
        HBox hbox= new HBox();
        if(!message.getContent().isEmpty()){
            hbox.setAlignment(isMe ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
            hbox.setPadding(new Insets(10,5,5,10));
            Text text =new Text(message.getContent());
            Label label= new Label( message.getSenderName()+" :");
            TextFlow textFlow= new TextFlow(text);
            textFlow.setStyle("-fx-color: rgb(239,242,255);"+
                    "-fx-background-color: rgb(15,125,242);"+
                    "-fx-background-radius :20px;");
            textFlow.setPadding(new Insets(5,10,5,10));
            text.setFill(Color.color(0.934,0.945,0.996));
            if(!isMe){
                hbox.getChildren().add(label);
            }
            hbox.getChildren().add(textFlow);
        }
        return hbox;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        connection();
        if (messageProcessor != null) {
            messageProcessor.getConversation(userId);
        } else {
            System.out.println("messer null");
        }
    }

    private void loadProfileData() {
        String sql = "SELECT username, email " +
                "FROM users " +
                "WHERE id = ?";
        try (Connection conn = new INDEXJDBC().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            rs = ps.executeQuery();
            if (rs.next()) {
                String nickName = rs.getString("username");
                String email = rs.getString("email");
                nicknameProfile.setText(nickName);
                emailProfile.setText(email);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    private double x;
    private double y;

    public void closebtaction() {
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Message");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to preview?");
            Optional<ButtonType> optional = alert.showAndWait();
            if (optional.get().equals(ButtonType.OK)) {
                close.getScene().getWindow().hide();
                Parent root = FXMLLoader.load(getClass().getResource("Dashboard.fxml"));
                Stage stage = new Stage();
                Scene scene = new Scene(root);
                scene.getStylesheets().add(getClass().getResource("DashBoard.Css").toExternalForm());
                stage.initStyle(StageStyle.TRANSPARENT);
                root.setOnMousePressed((MouseEvent event) -> {
                    x = event.getScreenX();
                    y = event.getScreenY();
                });
                root.setOnMouseDragged((MouseEvent event) -> {
                    stage.setX(event.getScreenX() - x);
                    stage.setY(event.getScreenY() - y);
                    stage.setOpacity(.8);

                });
                root.setOnMouseReleased((MouseEvent event) -> {
                    stage.setOpacity(1);
                });
                stage.setScene(scene);
                stage.show();


            } else {

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void sliderProfile(){
        profileanchor.setVisible(true);
        panehome.setVisible(false);
        loadProfileData();
    }
    public void sliderHome(){
        profileanchor.setVisible(false);
        panehome.setVisible(true);
    }


}
//    public List<User> getAllUsers() {
//        List<User> users = new ArrayList<>();
//        try {
//            PreparedStatement statement = con.prepareStatement("SELECT * FROM dbo.users");
//            ResultSet resultSet = statement.executeQuery();
//                while (resultSet.next()) {
//                    int id = resultSet.getInt("id");
//                    String username = resultSet.getString("username");
//                    String password = resultSet.getString("password");
//                    String email = resultSet.getString("email");
//                    Timestamp createdAt = resultSet.getTimestamp("created_at");
//
//                    User user = new User(id, username, password, email, createdAt);
//                    users.add(user);
//                }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        return users;
//    }