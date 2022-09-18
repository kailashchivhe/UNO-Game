package com.kai.unogame.utils;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.kai.unogame.listener.CreateGameListener;
import com.kai.unogame.listener.CreateStatusListener;
import com.kai.unogame.listener.DeckCardsListener;
import com.kai.unogame.listener.ExitGameListener;
import com.kai.unogame.listener.GameExitListener;
import com.kai.unogame.listener.GameRequestListener;
import com.kai.unogame.listener.JoinGameListener;
import com.kai.unogame.listener.LoginListener;
import com.kai.unogame.listener.ProfileListener;
import com.kai.unogame.listener.ProfileRetrieveListener;
import com.kai.unogame.listener.RegistrationListener;
import com.kai.unogame.listener.StartGameListener;
import com.kai.unogame.listener.TopCardListener;
import com.kai.unogame.listener.TurnListener;
import com.kai.unogame.listener.UpdateExitStatusListener;
import com.kai.unogame.listener.UpdateTopCardListener;
import com.kai.unogame.listener.UserCardsListener;
import com.kai.unogame.model.Card;
import com.kai.unogame.model.Game;
import com.kai.unogame.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

public class FirebaseHelper {

    static FirebaseAuth firebaseAuth;
    static FirebaseFirestore firebaseFirestore;
    static FirebaseFirestore db;

    public static void initFirebase(){
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public static FirebaseUser getUser(){
        return firebaseAuth.getCurrentUser();
    }

    public static void login(String email, String password, LoginListener loginListener ){
        firebaseAuth.signInWithEmailAndPassword( email, password ).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                loginListener.loggedIn();
            } else {
                loginListener.loggedInFailure( task.getException().getMessage());
            }
        });
    }

    public static void register(String email, String password, String firstName, String lastName, String city, String gender, RegistrationListener registerListener){
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(firstName+ " " + lastName).build();
                    user.updateProfile(userProfileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                DocumentReference dr = db.collection("unogame").document("Users").collection("Users").document(firebaseAuth.getCurrentUser().getUid());
                                HashMap<String,Object> map = new HashMap<>();
                                map.put("firstname",firstName);
                                map.put("lastname",lastName);
                                map.put("userid", firebaseAuth.getCurrentUser().getUid());
                                map.put("city",city);
                                map.put("gender",gender);
                                dr.set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            registerListener.registered();
                                        }
                                        else{
                                            registerListener.registeredFailure(task.getException().getMessage());
                                        }
                                    }
                                });
                            }
                            else{
                                // Profile name not set properly
                                registerListener.registeredFailure(task.getException().getMessage());
                            }
                        }
                    });
                }
                else{
                    // User not created properly
                    registerListener.registeredFailure(task.getException().getMessage());
                }
            }
        });
    }

    public static void logout(){
        firebaseAuth.signOut();
    }

    public static void userDetails(ProfileRetrieveListener profileRetrieveListener){
        DocumentReference dr = db.collection("unogame").document("Users").collection("Users").document(getUser().getUid());
        dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot ds = task.getResult();
                    String firstname = ds.get("firstname").toString();
                    String lastname = ds.get("lastname").toString();
                    String city = ds.get("city").toString();
                    String gender = ds.get("gender").toString();
                    profileRetrieveListener.profileRetrieved(new User(firstname,lastname,FirebaseHelper.getUser().getUid(),gender,city));
                }
                else{
                    profileRetrieveListener.profileRetrievedFailure(task.getException().getMessage());
                }
            }
        });

    }

    public static void profileUpdate(User user, ProfileListener profileListener){
        DocumentReference dr = db.collection("unogame").document("Users").collection("Users").document(user.getUserid());
        HashMap<String,Object> map = new HashMap<>();
        map.put("firstname", user.getFirstname());
        map.put("lastname", user.getLastname());
        map.put("gender",user.getGender());
        map.put("city" , user.getCity());
        dr.update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    profileListener.profileUpdate();
                }
                else{
                    profileListener.profileUpdateFailure(task.getException().getMessage());
                }
            }
        });
    }

    public static void createGame(CreateGameListener createGameListener){
        HashMap<String, Object> map = new HashMap<>();
        map.put("status", false);
        map.put("createdStatus", true);
        map.put("user1", firebaseAuth.getCurrentUser().getUid() );
        map.put("turn", firebaseAuth.getCurrentUser().getUid() );
        map.put("name", "UNO Game");
        firebaseFirestore.collection("unogame").document("game").set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    createGameListener.gameCreatedSuccessfully();
                }
                else{
                    createGameListener.gameCreationFailure(task.getException().getMessage());
                }
            }
        });
    }

    public static void getCreatedStatus(CreateStatusListener createStatusListener){
        firebaseFirestore.collection("unogame").document("game").addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if( error == null && value.get("createdStatus") != null){
                    boolean status = (boolean) value.get("createdStatus");
                    String userId = (String) value.get("user1");
                    if(status && !(userId.contains(firebaseAuth.getUid()))){
                        createStatusListener.createStatusSuccessfully();
                    }
                }
                else if(error != null){
                    createStatusListener.createStatusFailure(error.getMessage());
                }
            }
        });
    }

    public static void getTurnStatus(TurnListener turnListener){
        firebaseFirestore.collection("unogame").document("game").addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if( error == null && value.get("turn") != null){
                    String uid = (String) value.get("turn");
                    turnListener.onTurnSuccess(uid);
                }
                else if(error != null){
                    turnListener.onTurnFailure(error.getMessage());
                }
            }
        });
    }

    public static void joinGame(JoinGameListener joinGameListener){
        HashMap<String, Object> map = new HashMap<>();
        map.put("user2", firebaseAuth.getCurrentUser().getUid() );
        firebaseFirestore.collection("unogame").document("game").update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    //getCards and update
                    HashSet<Long> userSet = UnoGameHelper.getUsersCards();
                    ArrayList<Long> deckList = UnoGameHelper.getDeck( userSet );
                    HashMap<String, Object> gameMap = new HashMap<>();
                    gameMap.put("topCard", deckList.remove(0));
                    gameMap.put("deck", deckList);
                    ArrayList<Long> user1List = new ArrayList<>();
                    ArrayList<Long> user2List = new ArrayList<>();
                    int cnt = 0;
                    for(Long data: userSet){
                        if( cnt < 7){
                            user1List.add(data);
                        }
                        else{
                            user2List.add(data);
                        }
                        cnt++;
                    }
                    gameMap.put("user1Set", user1List);
                    gameMap.put("user2Set", user2List);
                    gameMap.put("status", true);
                    gameMap.put("exitStatus", false );

                    firebaseFirestore.collection("unogame").document("game").update(gameMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                joinGameListener.gamedJoined();
                            }
                            else{
                                joinGameListener.gamedJoinedFailure(task.getException().getMessage());
                            }
                        }
                    });
                }
                else{
                    joinGameListener.gamedJoinedFailure(task.getException().getMessage());
                }
            }
        });
    }


    public static void gameStartedListener(StartGameListener startGameListener){
        firebaseFirestore.collection("unogame").document("game").addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if( error == null && value.get("status") != null){
                    boolean status = (boolean) value.get("status");
                    if(status){
                        startGameListener.gameStarted();
                    }
                }
                else if(error != null){
                    startGameListener.gameStartedFailure( error.getMessage() );
                }
            }
        });
    }

    public static void getUserCards(UserCardsListener userCardsListener){
        firebaseFirestore.collection("unogame").document("game").addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if( error == null && Objects.requireNonNull(value).contains("user1") && value.contains("deck")){
                    String user1 = (String) value.get("user1");
                    if(firebaseAuth.getUid().contains(user1)){
                        ArrayList<Long> data = (ArrayList<Long>) value.get("user1Set");
                        userCardsListener.userCardsSuccess(data);
                    }
                    else{
                        ArrayList<Long> data = (ArrayList<Long>) value.get("user2Set");
                        userCardsListener.userCardsSuccess(data);
                    }
                }
                else {
                    if(error!=null && error.getMessage()!=null)
                        userCardsListener.userFailure(error.getMessage());
                }
            }
        });
    }

    public static void updateDeckCards(ArrayList<Card> deckList){
        ArrayList<Integer> list = new ArrayList<>();
        for(Card card: deckList){
            list.add(card.getId());
        }
        HashMap<String, Object> gameMap = new HashMap<>();
        gameMap.put("deck", list);
        firebaseFirestore.collection("unogame").document("game").update(gameMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                }
                else{
                }
            }
        });
    }

    public static void updateUserCards(ArrayList<Card> userList){
        ArrayList<Long> dataList = UnoGameHelper.getLongList(userList);
        firebaseFirestore.collection("unogame").document("game").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    String uid = (String) task.getResult().get("user1");
                    HashMap<String, Object> gameMap = new HashMap<>();
                    if(firebaseAuth.getUid().contains(uid)){
                        gameMap.put("user1Set", dataList);
                    }
                    else{
                        gameMap.put("user2Set", dataList);
                    }
                    firebaseFirestore.collection("unogame").document("game").update(gameMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Log.d("FirebaseHelper", "Success");
                            }
                            else{
                                Log.d("FirebaseHelper", "Failure");
                            }
                        }
                    });
                }
                else{
                    Log.d("FirebaseHelper", "Failure");
                }
            }
        });
    }

    public static void addDrawFour(ArrayList<Card> userList){
        ArrayList<Long> dataList = UnoGameHelper.getLongList(userList);
        firebaseFirestore.collection("unogame").document("game").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    String uid = (String) task.getResult().get("user1");
                    String path = "";
                    HashMap<String, Object> gameMap = new HashMap<>();
                    if(firebaseAuth.getUid().contains(uid)){
                        path = "user2Set";
                    }
                    else{
                        path = "user1Set";
                    }
                    gameMap.put(path, FieldValue.arrayUnion(dataList) );
                    firebaseFirestore.collection("unogame").document("game").update(gameMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Log.d("FirebaseHelper", "Success");
                            }
                            else{
                                Log.d("FirebaseHelper", "Failure");
                            }
                        }
                    });
                }
                else{
                    Log.d("FirebaseHelper", "Failure");
                }
            }
        });
    }

    public static void updateTopCard(Card card, UpdateTopCardListener updateTopCardListener){
        HashMap<String, Object> gameMap = new HashMap<>();
        gameMap.put("topCard", card.getId());
        firebaseFirestore.collection("unogame").document("game").update(gameMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    updateTopCardListener.onTopCardSuccess();
                }
                else{
                    updateTopCardListener.onTopCardFailure(task.getException().getMessage());
                }
            }
        });
    }

    public static void updateTurn(){
        HashMap<String, Object> gameMap = new HashMap<>();
        firebaseFirestore.collection("unogame").document("game").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    String uid = (String) task.getResult().get("turn");
                    String user = (String) task.getResult().get("user1");
                    String user2 = (String) task.getResult().get("user2");
                    if(uid.contains(user)){
                        gameMap.put("turn", user2);
                    }
                    else{
                        gameMap.put("turn", user);
                    }
                    firebaseFirestore.collection("unogame").document("game").update(gameMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task1) {
                            if(task1.isSuccessful()){
                                Log.d("FirebaseHelper", "onComplete: ");
                            }
                            else{
                                Log.d("FirebaseHelper", "onComplete: "+ task1.getException().getMessage() );
                            }
                        }
                    });
                }
                else{
                    Log.d("FirebaseHelper", "onComplete: "+ task.getException().getMessage() );
                }
            }
        });

    }

    public static void getTopCard(TopCardListener topCardListener){
        firebaseFirestore.collection("unogame").document("game").addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if( error == null && value.contains("topCard")){
                    Long topCard = (Long) value.get("topCard");
                    Card card = UnoGameHelper.getCardObject(topCard);
                    topCardListener.onTopCardSuccess(card);
                }
                else if(error != null){
                    topCardListener.onTopFailure( error.getMessage() );
                }
            }
        });
    }

    public static void getDeckCards(DeckCardsListener deckCardsListener){
        firebaseFirestore.collection("unogame").document("game").addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if( error == null && value.contains("deck")){
                    ArrayList<Long> deckList = (ArrayList<Long>) value.get("deck");
                    deckCardsListener.deckCardsSuccess(deckList);
                }
                else if(error != null){
                    deckCardsListener.deckFailure( error.getMessage() );
                }
            }
        });
    }

    public static void clearGame(){
        firebaseFirestore.collection("unogame").document("game").delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.d("FirebaseHelper", "onComplete: ");
                }
                else{
                    Log.d( "FirebaseHelper", "onComplete: "+ task.getException().getMessage());
                }
            }
        });
    }

    public static void updateExitStatus(UpdateExitStatusListener updateExitStatusListener){
        HashMap<String, Object> gameMap = new HashMap<>();
        gameMap.put("exitStatus", true);
        firebaseFirestore.collection("unogame").document("game").update(gameMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    updateExitStatusListener.onExitStatusChanged();
                }
                else{
                    updateExitStatusListener.onExitFailure(task.getException().getMessage());
                }
            }
        });
    }

    public static void exitGameListener(ExitGameListener exitGameListener){
        firebaseFirestore.collection("unogame").document("game").addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if( error == null && value != null && value.contains("exitStatus")){
                    Boolean exitStatus = (Boolean) value.get("exitStatus");
                    if(exitStatus){
                        exitGameListener.onExitSuccess();
                    }
                }
                else if(error != null){
                    exitGameListener.onExitFailure( error.getMessage() );
                }
            }
        });
    }
}
