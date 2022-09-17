package com.kai.unogame.utils;

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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.kai.unogame.listener.CreateGameListener;
import com.kai.unogame.listener.CreateStatusListener;
import com.kai.unogame.listener.GameRequestListener;
import com.kai.unogame.listener.JoinGameListener;
import com.kai.unogame.listener.LoginListener;
import com.kai.unogame.listener.ProfileListener;
import com.kai.unogame.listener.ProfileRetrieveListener;
import com.kai.unogame.listener.RegistrationListener;
import com.kai.unogame.listener.StartGameListener;
import com.kai.unogame.listener.TurnListener;
import com.kai.unogame.model.Game;
import com.kai.unogame.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

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
        map.put("status", true);
        map.put("user2", firebaseAuth.getCurrentUser().getUid() );
        firebaseFirestore.collection("unogame").document("game").update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    //getCards and update
                    HashSet<Integer> userSet = UnoGameHelper.getUsersCards();
                    HashSet<Integer> deckSet = UnoGameHelper.getDeck( userSet );
                    HashMap<String, Object> gameMap = new HashMap<>();
                    gameMap.put("deck", deckSet);
                    ArrayList<Integer> user1List = new ArrayList<>();
                    ArrayList<Integer> user2List = new ArrayList<>();
                    int cnt = 0;
                    for(Integer data: userSet){
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

}
