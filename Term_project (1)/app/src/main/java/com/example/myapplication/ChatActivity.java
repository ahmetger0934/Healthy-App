package com.example.myapplication;

import static com.google.android.gms.vision.L.TAG;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ChatActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private String recipientUserId;

    private EditText messageEditText;
    private Button sendButton;
    private RecyclerView messagesRecyclerView;

    private List<ChatMessage> messagesList;
    private ChatMessageAdapter chatMessageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        messageEditText = findViewById(R.id.editText);
        sendButton = findViewById(R.id.sendButton);
        messagesRecyclerView = findViewById(R.id.messagesRecyclerView);

        recipientUserId = getIntent().getStringExtra("recipientUserId");

        messagesList = new ArrayList<>();
        chatMessageAdapter = new ChatMessageAdapter(messagesList, currentUser.getUid());
        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        messagesRecyclerView.setAdapter(chatMessageAdapter);

        sendButton.setOnClickListener(v -> {
            db.collection("users").document(currentUser.getUid()).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null && document.exists()) {
                                String senderName = document.getString("username");
                                String senderPhotoUrl = document.getString("profileImageUrl");
                                sendMessage(senderName, senderPhotoUrl);
                            }
                        }
                    });
        });

        fetchMessages();

        chatMessageAdapter.setOnMessageLongClickListener(this::onMessageLongClick);




    }

    public void onMessageLongClick(ChatMessage message) {



        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete this message?")
                .setPositiveButton("Delete", (dialog, which) -> {

                    deleteMessage(message);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void sendMessage(String senderName, String senderPhotoUrl) {
        String messageText = messageEditText.getText().toString();
        String messageId = UUID.randomUUID().toString();
        if (!messageText.isEmpty()) {
            ChatMessage message = new ChatMessage(currentUser.getUid(), recipientUserId, messageText, new Date(), senderName, senderPhotoUrl, messageId);

            db.collection("chats")
                    .add(message)
                    .addOnSuccessListener(documentReference -> {
                        Log.d(TAG, "Message sent: " + documentReference.getId());
                        messageEditText.setText("");
                    })
                    .addOnFailureListener(e -> Log.w(TAG, "Failed to send message", e));
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fetchMessages() {
        if (recipientUserId != null) {
            db.collection("chats")
                    .orderBy("timestamp", Query.Direction.ASCENDING)
                    .addSnapshotListener((value, error) -> {
                        if (error != null) {
                            Log.w(TAG, "Listen failed.", error);
                            return;
                        }

                        messagesList.clear();
                        for (DocumentSnapshot doc : value) {
                            ChatMessage message = doc.toObject(ChatMessage.class);
                            message.setMessageId(doc.getId());
                            if ((message.getSenderId().equals(currentUser.getUid()) && message.getRecipientId().equals(recipientUserId)) ||
                                    (message.getSenderId().equals(recipientUserId) && message.getRecipientId().equals(currentUser.getUid()))) {
                                db.collection("users").document(message.getSenderId().equals(currentUser.getUid()) ? message.getRecipientId() : message.getSenderId())
                                        .get()
                                        .addOnSuccessListener(documentSnapshot -> {
                                            if (documentSnapshot.exists()) {
                                                message.setReceiverPhotoUrl(documentSnapshot.getString("profileImageUrl"));
                                                messagesList.add(message);
                                                chatMessageAdapter.notifyDataSetChanged();
                                            }
                                        });
                            }
                        }
                    });
        } else {
            Log.d(TAG, "recipientUserId is null");
        }
    }

    private void deleteMessage(ChatMessage message) {
        String messageId = message.getMessageId();
        String currentUserId = currentUser.getUid();


        if (currentUserId.equals(message.getSenderId())) {

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("chats")
                    .document(messageId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {



                        int position = messagesList.indexOf(message);
                        if (position != -1) {
                            messagesList.remove(position);
                            chatMessageAdapter.notifyItemRemoved(position);
                        }
                    })
                    .addOnFailureListener(e -> {

                        Log.e(TAG, "Error deleting message from Firestore", e);

                    });
        } else {

            Toast.makeText(this, "You can only delete your own messages", Toast.LENGTH_SHORT).show();
        }
    }

}