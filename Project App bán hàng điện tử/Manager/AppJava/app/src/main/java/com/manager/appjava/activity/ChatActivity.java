package com.manager.appjava.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.manager.appjava.R;
import com.manager.appjava.adapter.ChatAdapter;
import com.manager.appjava.model.ChatMessage;
import com.manager.appjava.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {
    int iduser;
    String iduser_str;
    RecyclerView recyclerView;
    ImageView imgSend;
    EditText edtMess;
    FirebaseFirestore db;
    ChatAdapter adapter;
    List<ChatMessage> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        iduser = getIntent().getIntExtra("id", 0);
        iduser_str = String.valueOf(iduser);

        initView();
        initControl();
        listenMess();
    }
    private void initControl() {
        imgSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessToFire();
            }
        });
    }

    private void sendMessToFire() {
        String str_mess = edtMess.getText().toString().trim();
        if(TextUtils.isEmpty(str_mess)) {

        }else  {
            HashMap<String, Object> message = new HashMap<>();
            message.put(Utils.SENDID, String.valueOf(Utils.user_current.getId()));
            message.put(Utils.RECEIVEDID, iduser_str);
            message.put(Utils.MESS, str_mess);
            message.put(Utils.DATETIME, new Date());
            db.collection(Utils.PATH_CHAT).add(message);
            edtMess.setText("");
        }
    }
    private void listenMess() {
        db.collection(Utils.PATH_CHAT)
                .whereEqualTo(Utils.SENDID, String.valueOf(Utils.user_current.getId()))
                .whereEqualTo(Utils.RECEIVEDID, iduser_str)
                .addSnapshotListener(eventListener);

        db.collection(Utils.PATH_CHAT)
                .whereEqualTo(Utils.SENDID, iduser_str)
                .whereEqualTo(Utils.RECEIVEDID, String.valueOf(Utils.user_current.getId()))
                .addSnapshotListener(eventListener);

    }


    private final EventListener<QuerySnapshot> eventListener = (value, error)-> {
        if(error !=null) {
            return ;
        }if(value !=null) {
            int count = list.size();
            for(DocumentChange documentChange : value.getDocumentChanges()) {
                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.sendid = documentChange.getDocument().getString(Utils.SENDID);
                    chatMessage.receivedid = documentChange.getDocument().getString(Utils.RECEIVEDID);
                    chatMessage.mess = documentChange.getDocument().getString(Utils.MESS);
                    chatMessage.dateObj = documentChange.getDocument().getDate(Utils.DATETIME);
                    chatMessage.datetime = format_date(documentChange.getDocument().getDate(Utils.DATETIME));
                    list.add(chatMessage);

                }
            }
            Collections.sort(list, (obj1, obj2)-> obj1.dateObj.compareTo(obj2.dateObj));
            if(count ==0) {
                adapter.notifyDataSetChanged();
            }else {
                adapter.notifyItemRangeInserted(list.size(),list.size());
                recyclerView.smoothScrollToPosition(list.size()-1);
            }
        }
    };

    private String format_date(Date date) {
        return new SimpleDateFormat("MMMM dd, yyyy- hh:mm a", Locale.getDefault()).format(date);
    }
    private void initView() {
        list = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.recycleview_chat);
        imgSend = findViewById(R.id.imgagechat);
        edtMess = findViewById(R.id.edtinputtex);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        adapter = new ChatAdapter(getApplicationContext(),list, String.valueOf(Utils.user_current.getId()));
        recyclerView.setAdapter(adapter);
    }
}