package com.kai.unogame.utils;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.Continuation;
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
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;
import com.kai.unogame.listener.CreateGameListener;
import com.kai.unogame.listener.DeckCardsListener;
import com.kai.unogame.listener.ExitGameListener;
import com.kai.unogame.listener.GameListListener;
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
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FirebaseHelper {

    static FirebaseAuth firebaseAuth;
    static FirebaseFirestore firebaseFirestore;
    static FirebaseFirestore db;
    private static FirebaseFunctions mFunctions;
    private static String gameId;

    public static void initFirebase(){
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        db = FirebaseFirestore.getInstance();
        mFunctions = FirebaseFunctions.getInstance();
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
                                DocumentReference dr = db.collection("users").document(firebaseAuth.getCurrentUser().getUid());
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
        DocumentReference dr = db.collection("users").document(getUser().getUid());
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
        DocumentReference dr = db.collection("users").document(user.getUserid());
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
        Map<String, Object> createData = new HashMap<>();
        createData.put("uid", firebaseAuth.getCurrentUser().getUid());

        mFunctions.getHttpsCallable("createGame")
                .call(createData)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) {
                        if (task.isSuccessful()) {
                            HashMap<String, Object> result = (HashMap<String, Object>) task.getResult().getData();
                            gameId = (String) result.get("docId");
                            createGameListener.gameCreatedSuccessfully();
                            Log.d("FirebaseHelper createGame", "gameID is: " + gameId);
                        } else {
                            createGameListener.gameCreationFailure(task.getException().getMessage());
                        }
                        return "";
                    }
                });
    }

    public static void joinGame(JoinGameListener joinGameListener, Game game){
        Map<String, Object> joinData = new HashMap<>();
        joinData.put("uid", firebaseAuth.getUid());
        joinData.put("gameId", game.getGameID());

        mFunctions.getHttpsCallable("joinGame")
                .call(joinData)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) {
                        if (task.isSuccessful()) {
                            HashMap<String, Object> result = (HashMap<String, Object>) task.getResult().getData();
                            gameId = game.getGameID();
                            joinGameListener.joinGameSuccess();
                        } else {
                            joinGameListener.gamedJoinedFailure(task.getException().getMessage());
                        }
                        return "";
                    }
                });
    }

    public static void getGamesList(GameListListener gameListListener){
        firebaseFirestore.collection("unoGames").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    ArrayList<Game> gameArrayList = new ArrayList<>();
                    List<DocumentSnapshot> documentSnapshotList = task.getResult().getDocuments();
                    for(DocumentSnapshot documentSnapshot : documentSnapshotList){
                        Game game = new Game((String)documentSnapshot.get("docId"),
                                (String)documentSnapshot.get("user1Id"),
                                (Boolean)documentSnapshot.get("status"),
                                (String)documentSnapshot.get("turn"));
                        if( !firebaseAuth.getUid().contains((String)documentSnapshot.get("user1Id"))) {
                            gameArrayList.add(game);
                        }
                    }
                    gameListListener.onGameListSuccess(gameArrayList);
                }
                else{
                    gameListListener.onGameListFailure(task.getException().getMessage());
                }
            }
        });
    }

    public static void getTurnStatus(TurnListener turnListener){
        firebaseFirestore.collection("unoGames").document(gameId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
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

    public static void gameStartedListener(StartGameListener startGameListener){
        firebaseFirestore.collection("unoGames").document(gameId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
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
        firebaseFirestore.collection("unoGames").document(gameId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if( error == null && Objects.requireNonNull(value).contains("user1Id")){
                    String user1 = (String) value.get("user1Id");
                    if(firebaseAuth.getUid().contains(user1)){
                    }
                    else{

                    }
                }
                else {
                    if(error!=null && error.getMessage()!=null)
                        userCardsListener.userFailure(error.getMessage());
                }
            }
        });
    }

    private static Task<String> callDrawCards(String uid) {
        Map<String, Object> drawData = new HashMap<>();
        drawData.put("uid", uid);

        return mFunctions.getHttpsCallable("drawCards")
                .call(drawData)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        HashMap<String, Object> result = (HashMap<String, Object>) task.getResult().getData();
                        return (String) result.get("result");
                    }
                });
    }

    public static void playCard(Card card, UpdateTopCardListener updateTopCardListener){
        //FUNCTION CALL//
        callPlayCard(gameId, card).addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()) {
                    updateTopCardListener.onTopCardSuccess();
                } else {
                    //task.getException().printStackTrace();
                    updateTopCardListener.onTopCardFailure(task.getException().getMessage());

                    Exception e = task.getException();
                    if(e instanceof FirebaseFunctionsException) {
                        FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                        FirebaseFunctionsException.Code code = ffe.getCode();
                        Object details = ffe.getDetails();
                        Log.d("FirebaseHelper playCard", "callPlayCard onComplete error: " + ffe);
                    }
                }
            }
        });
    }

    private static Task<String> callPlayCard(String gameId, Card playedCard) {
        Map<String, Object> playData = new HashMap<>();
        playData.put("uid", gameId);
        Map<String, Object> card = new HashMap<>();
        card.put("id", playedCard.getId());
        card.put("color", playedCard.getColor());
        card.put("type", playedCard.getType());
        card.put("value", playedCard.getValue());
        playData.put("card", card);

        return mFunctions.getHttpsCallable("playCard")
                .call(playData)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        HashMap<String, Object> result = (HashMap<String, Object>) task.getResult().getData();
                        return (String) result.get("result");
                    }
                });
    }

    public static void getTopCard(TopCardListener topCardListener){
        firebaseFirestore.collection("unoGames").document(gameId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
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

    public static void leaveGame(UpdateExitStatusListener updateExitStatusListener){
        //FUNCTION CALL//
        callLeaveGame(gameId).addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()) {
                    updateExitStatusListener.onExitStatusChanged();
                } else {
                    //task.getException().printStackTrace();
                    updateExitStatusListener.onExitFailure(task.getException().getMessage());

                    Exception e = task.getException();
                    if(e instanceof FirebaseFunctionsException) {
                        FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                        FirebaseFunctionsException.Code code = ffe.getCode();
                        Object details = ffe.getDetails();
                        Log.d("FirebaseHelper createGame", "callCreateGame onComplete error: " + ffe);
                    }

                }
            }
        });
    }

    private static Task<String> callLeaveGame(String gameId) {
        Map<String, Object> joinData = new HashMap<>();
        joinData.put("gameId", gameId);

        return mFunctions.getHttpsCallable("leaveGame")
                .call(joinData)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        HashMap<String, Object> result = (HashMap<String, Object>) task.getResult().getData();
                        return (String) result.get("result");
                    }
                });
    }

    public static void exitGameListener(ExitGameListener exitGameListener){
        firebaseFirestore.collection("unoGames").document(gameId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
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
