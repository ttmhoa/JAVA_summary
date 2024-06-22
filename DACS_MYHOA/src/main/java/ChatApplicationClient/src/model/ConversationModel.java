package model;

import java.sql.Timestamp;
import java.util.List;

public class ConversationModel {
    private int id;
    private String name;
    private Timestamp createdAt;
    private List<Integer> memberIds;

    // Constructors, getters, and setters
    public ConversationModel(List<Integer> memberIds) {
        this.memberIds = memberIds;
    }

    public ConversationModel(String name, List<Integer> memberIds) {
        this.name = name;
        this.createdAt = createdAt;
        this.memberIds = memberIds;
    }

    public ConversationModel(int id, String name, List<Integer> memberIds) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.memberIds = memberIds;
    }

    public ConversationModel(int id, String name, Timestamp createdAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
    }

    public ConversationModel(int id, String name) {
        this.id = id;
        this.name = name;
    }



    public List<Integer> getMemberIds() {
        return memberIds;
    }

    public void setMemberIds(List<Integer> memberIds) {
        this.memberIds = memberIds;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}