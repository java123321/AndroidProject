package com.example.ourprojecttest.StuDiagnosis;

import com.example.ourprojecttest.StuDiagnosis.Msg;

import java.io.Serializable;
import java.util.ArrayList;

public class MessageBean implements Serializable {
     byte[] icon;
     String name;
     String time;
     ArrayList<Msg> msgList;

     public String getTime() {
          return time;
     }

     public void setTime(String time) {
          this.time = time;
     }

     public byte[] getIcon() {
          return icon;
     }

     public void setIcon(byte[] icon) {
          this.icon = icon;
     }

     public String getName() {
          return name;
     }

     public void setName(String name) {
          this.name = name;
     }

     public ArrayList<Msg> getMsgList() {
          return msgList;
     }

     public void setMsgList(ArrayList<Msg> msgList) {
          this.msgList = msgList;
     }
}
