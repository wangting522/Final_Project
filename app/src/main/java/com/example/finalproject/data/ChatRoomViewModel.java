package com.example.finalproject.data;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.ChatMessage;

import java.util.ArrayList;

public class ChatRoomViewModel extends ViewModel {
    //public MutableLiveData<ArrayList<ChatRoom.ChatMessage>> messages = new MutableLiveData< >(null);
    public MutableLiveData<ArrayList<ChatMessage>> messages = new MutableLiveData<>(new ArrayList<>());
    public MutableLiveData<ChatMessage> selectedMessage = new MutableLiveData< >();
}
