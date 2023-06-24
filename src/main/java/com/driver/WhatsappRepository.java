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

    public int removeUser(User user) throws Exception{
        boolean found =false;
        Group gp=null;
        for(Group ele:groupUserMap.keySet()){
            if(groupUserMap.get(ele).contains(user)){
                found=true;
                gp=ele;
                break;
            }
        }
        if(found==false) throw new Exception("User not found");
        if(found){
            for(User ele:adminMap.values()){
                if(ele==user) throw new Exception("Cannot remove admin");
            }
        }
        //remove the user from groupUserMap
        groupUserMap.get(gp).remove(user);

        List<Message> toBeRemoved=new ArrayList<>();
        for(Message msg_ele:senderMap.keySet()){
            if(senderMap.get(msg_ele)==user){
                toBeRemoved.add(msg_ele);
            }
        }
        //first removing from senderMap
        for(Message ele:toBeRemoved){
            senderMap.remove(ele);
        }
        //removing from groupMessageMap
        groupMessageMap.get(gp).removeAll(toBeRemoved);
        //removing mobile of the user from userMobile
        userMobile.remove(user.getMobile());

        return groupUserMap.get(gp).size()+groupMessageMap.get(gp).size()+senderMap.size();
    }

    public String findMessage(Date start, Date end, int k) throws Exception{
        List<Message> list=new ArrayList<>();
        for(Message ele:senderMap.keySet()){
            if(ele.getTimestamp().after(start) && ele.getTimestamp().before(end)){
                list.add(ele);
            }
        }
        if(list.size()<k) throw new Exception("K is greater than the number of messages");
        Collections.sort(list, new Comparator<Message>() {
            @Override
            public int compare(Message o1, Message o2) {
                if(o1.getTimestamp().after(o2.getTimestamp())){
                    return -1;
                }
                return 1;
            }
        });
         return list.get(k-1).getContent();


    }
}
