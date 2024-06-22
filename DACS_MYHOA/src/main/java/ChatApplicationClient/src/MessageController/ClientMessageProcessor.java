package ChatApplicationClient.src.MessageController;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.ConversationModel;
import model.MessageModel;
import org.example.dacs_myhoa.Room;
import org.example.dacs_myhoa.loginController;


import java.io.*;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

public class ClientMessageProcessor {
    @FXML
    private ListView<ConversationModel> listviewUser;

    @FXML
    VBox messId;
    public Room rm;
    public final PrintWriter writer;
    public final BufferedReader reader;
    public final Gson gson;

    public ClientMessageProcessor(Socket socket) throws IOException {
        this.writer = new PrintWriter(socket.getOutputStream(), true);
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.gson = new Gson();
    }

    public void connect(int userId, String username) {
        Request connectRequest = new Request("connect", new UserInfoRequest(userId, username));
        String jsonConnectRequest = gson.toJson(connectRequest);
        writer.println(jsonConnectRequest);
    }

    public void sendMessage(MessageModel chatMessage) {
        Request messageRequest = new Request("sendMessage", chatMessage);
        String jsonMessageRequest = gson.toJson(messageRequest);
        writer.println(jsonMessageRequest);
        receiveMessagessend();
    }

    public void receiveMessagessend() {
        new Thread(() -> {
            try {
                String message;
                while ((message = reader.readLine()) != null) {
                    handleMessagesend(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void handleMessagesend(String message) {
        // Xử lý tin nhắn nhận được từ server
        MessageModel MessageModel = gson.fromJson(message, MessageModel.class);
        List<MessageModel> messageModel = gson.fromJson(message, new TypeToken<List<MessageModel>>() {}.getType());
        for (MessageModel mess : messageModel) {
            HBox anc = rm.renderMessage(mess, true);
            messId.getChildren().add(anc);
        }

    }

    public void closeConnection() throws IOException {
        writer.close();
        reader.close();
    }
//
//    public void sendMessage() {
//        Gson gson = new Gson();
//        MessageModel messageModel = new MessageModel(1, 1, 1, "Hello from Client", null);
//        Request request = new Request("sendMessage", messageModel);
//        String jsonRequest = gson.toJson(request);
//        writer.println(jsonRequest);
//    }

    public void getMessage(int conversationId) {
        Gson gson = new Gson();
        Request request = new Request("getMessage", conversationId);

        String jsonRequest = gson.toJson(request);
        writer.println(jsonRequest);
        new Thread(() -> {
            try {
                String message;
                while ((message = reader.readLine()) != null) {
                    handleMessageGet(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

    }
    public void handleMessageGet(String message){
        Gson gson = new Gson();
        List<MessageModel> messageModel = gson.fromJson(message, new TypeToken<List<MessageModel>>() {}.getType());
        for (MessageModel mess : messageModel) {
            var isMe = loginController.userId == mess.getSenderId();
            HBox anc = rm.renderMessage(mess, isMe);
            messId.getChildren().add(anc);
        }
    }

    private ObservableList<ConversationModel> conversationModels = FXCollections.observableArrayList();

    public void getConversation(int userId) {
        Gson gson = new Gson();
        Request request = new Request("getConversation", userId);

        String jsonRequest = gson.toJson(request);
        writer.println(jsonRequest);

        new Thread(() -> {
            try {
                String message;
                while ((message = reader.readLine()) != null) {
                    handleConversationGet(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void handleConversationGet(String message) {
        System.out.println(message); // In ra nội dung JSON nhận được từ server

        try {
            Gson gson = new Gson();
            JsonElement jsonElement = gson.fromJson(message, JsonElement.class);

            if (jsonElement.isJsonArray()) {
                ConversationModel[] chatMessage = gson.fromJson(jsonElement, ConversationModel[].class);
                Platform.runLater(() -> {
                    conversationModels.clear();
                    conversationModels.addAll(Arrays.asList(chatMessage));
                    listviewUser.setItems(conversationModels);
                });
            } else if (jsonElement.isJsonObject()) {
                // Nếu JSON trả về là một đối tượng, hãy thử lấy dữ liệu từ đó
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                if (jsonObject.has(message)) {
                    ConversationModel[] chatMessage = gson.fromJson(jsonObject.getAsJsonArray(message), ConversationModel[].class);
                    Platform.runLater(() -> {
                        conversationModels.clear();
                        conversationModels.addAll(Arrays.asList(chatMessage));
                        listviewUser.setItems(conversationModels);
                    });
                } else {
                    // Xử lý trường hợp JSON trả về không có dữ liệu như mong đợi
                    System.out.println("Không tìm thấy dữ liệu message trong JSON");
                }
            } else {
                // Xử lý trường hợp JSON trả về không phải là array hoặc object
                System.out.println("Định dạng JSON không hợp lệ");
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            System.err.println("Lỗi khi giải nén JSON: " + e.getMessage());
        }
    }

    public void createConversation(ConversationModel conversationModel) {
        Gson gson = new Gson();
        // Create the Request
        Request request = new Request("createConversation", conversationModel);

        // Convert the Request to JSON and send it to the server
        String jsonRequest = gson.toJson(request);
        writer.println(jsonRequest);

    }


//    public void joinConversation() {
//        Gson gson = new Gson();
//        ConversationModel conversationModel = new ConversationModel(13, "Test Conversation");
//        Request request = new Request("joinConversation", conversationModel);
//
//        String jsonRequest = gson.toJson(request);
//        writer.println(jsonRequest);
//    }

    public void searchConversation(String keyword) {
        Gson gson = new Gson();

        Request request = new Request("searchConversation", keyword);

        String jsonRequest = gson.toJson(request);
        writer.println(jsonRequest);
        new Thread(() -> {
            try {
                String message;
                while ((message = reader.readLine()) != null) {
                    handleSearchConversationSGet(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }


    private void handleSearchConversationSGet(String message) {
        Gson gson = new Gson();
        ConversationModel[] conversations = gson.fromJson(message, ConversationModel[].class);
        ObservableList<ConversationModel> conversationsList = FXCollections.observableArrayList(Arrays.asList(conversations));

        // Cập nhật dữ liệu cho ListView
        listviewUser.setItems(conversationsList);
    }
}
