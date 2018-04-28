/**
  * Copyright 2018 bejson.com 
  */
package com.lrxliveandreadplayer.demo.beans.jmessage;
import java.util.List;

/**
 * Auto-generated: 2018-04-28 20:52:5
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class Data {

    private long roomId;
    private List<Members> members;
    public void setRoomId(long roomId) {
         this.roomId = roomId;
     }
     public long getRoomId() {
         return roomId;
     }

    public void setMembers(List<Members> members) {
         this.members = members;
     }
     public List<Members> getMembers() {
         return members;
     }

}