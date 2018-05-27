/**
  * Copyright 2018 bejson.com 
  */
package com.lrxliveandreadplayer.demo.beans.jmessage;

import java.util.ArrayList;
import java.util.List;

public class Data {

    private long roomId;
    private List<Member> members;
    public void setRoomId(long roomId) {
         this.roomId = roomId;
     }
     public long getRoomId() {
         return roomId;
     }

    public void setMembers(List<Member> members) {
         this.members = members;
     }
     public List<Member> getMembers() {
        if(members == null) {
            members = new ArrayList<>();
        }
        return members;
     }

}