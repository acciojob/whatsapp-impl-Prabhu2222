package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class WhatsappRepository {

    //Assume that each user belongs to at most one group
    //You can use the below mentioned hashmaps or delete these and create your own.
    private HashMap<Group, List<User>> groupUserMap;
    private HashMap<Group, List<Message>> groupMessageMap;
    private HashMap<Message, User> senderMap;
    private HashMap<Group, User> adminMap;
    private HashSet<String> userMobile;
    private int customGroupCount;
    private int messageId;

    public WhatsappRepository(){
        this.groupMessageMap = new HashMap<Group, List<Message>>();
        this.groupUserMap = new HashMap<Group, List<User>>();
        this.senderMap = new HashMap<Message, User>();
        this.adminMap = new HashMap<Group, User>();
        this.userMobile = new HashSet<>();
        this.customGroupCount = 0;
        this.messageId = 0;
    }

    public String createUser(String name, String mobile) throws Exception {
        if(userMobile.contains(mobile))
            throw new Exception("User already exists");
        userMobile.add(mobile);
        return "SUCCESS";

    }

    public Group createGroup(List<User> users) {
        Group gp=new Group();

        if(users.size()==2){
           gp.setName(users.get(1).getName());

        }else if(users.size()>2){
            this.customGroupCount++;
             gp.setName("Group"+" "+this.customGroupCount);
             adminMap.put(gp,users.get(0));
        }
        gp.setNumberOfParticipants(users.size());
        groupUserMap.put(gp,new ArrayList<User>(users));
        return gp;
    }

    public int createMessage(String content) {
        this.messageId++;
        Message msg = new Message(this.messageId, content);
        return this.messageId;
    }

    public int sendMessage(Message message, User sender, Group group) throws Exception{
        if(groupUserMap.containsKey(group)) {
            if(groupUserMap.get(group).contains(sender)){
                //updating groupMessageMap
                groupMessageMap.putIfAbsent(group,new ArrayList<Message>());
                groupMessageMap.get(group).add(message);
                //Updating sender Map
                senderMap.put(message,sender);

            }else throw new Exception("You are not allowed to send message");

        }else throw new Exception("Group does not exist");
       return groupMessageMap.get(group).size();
    }

    public String changeAdmin(User approver, User user, Group group) throws Exception {
        if(!groupUserMap.containsKey(group)) throw new Exception("Group does not exist");
        if(adminMap.get(group)!=approver) throw new Exception("Approver does not have rights");
        if(!groupUserMap.get(group).contains(user)) throw new Exception("User is not a participant");
        adminMap.put(group,user);
        return "SUCCESS";
    }
}
