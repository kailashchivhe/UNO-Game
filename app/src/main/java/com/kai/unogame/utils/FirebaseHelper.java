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
import com.kai.unogame.listener.DrawCardListener;
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
import com.kai.unogame.listener.PlayCardListener;
import com.kai.unogame.listener.UserCardsListener;
import com.kai.unogame.model.Card;
import com.kai.unogame.model.Game;
import com.kai.unogame.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseHelper {
    public static final String TAG = "FirebaseHelper";
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

    public static String getGameId() {
        return gameId;
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
        firebaseFirestore.collection("unoGames").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if( error == null ){
                    ArrayList<Game> gameArrayList = new ArrayList<>();
                    List<DocumentSnapshot> documentSnapshotList = value.getDocuments();
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
                else if( error != null ){
                    gameListListener.onGameListFailure(error.getMessage());
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
        if( gameId.isEmpty() ){
            return;
        }
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
        DocumentReference documentReference = firebaseFirestore.collection("unoGames").document(gameId);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if( task.isSuccessful()){
                    String user1 = (String) task.getResult().get("user1Id");
                    String userCollection = "user2";
                    if(firebaseAuth.getUid().contains(user1)){
                        userCollection = "user1";
                    }
                    getSpecificUserCards(userCollection, userCardsListener, documentReference );
                }
                else {
                    userCardsListener.userFailure(task.getException().getMessage());
                }
            }
        });
    }

    private static void getSpecificUserCards(String user, UserCardsListener userCardsListener, DocumentReference documentReference ){
        documentReference.collection(user).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if( error == null ){
                    ArrayList<Card> cardArrayList = new ArrayList<>();
                    List<DocumentSnapshot> documentSnapshotList = value.getDocuments();
                    for(DocumentSnapshot documentSnapshot : documentSnapshotList){
                        Card card = new Card( (int)documentSnapshot.get("id"),
                                (String)documentSnapshot.get("color"),
                                (String)documentSnapshot.get("type"),
                                (String)documentSnapshot.get("value"));
                        cardArrayList.add(card);
                    }
                    userCardsListener.userCardsSuccess(cardArrayList);
                }
                else if(error != null){
                    userCardsListener.userFailure(error.getMessage());
                }
            }
        });
    }

    public static void getTopCard(TopCardListener topCardListener){
        firebaseFirestore.collection("unoGames").document(gameId).collection("top").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if( error == null ){
                    ArrayList<Card> cardArrayList = new ArrayList<>();
                    List<DocumentSnapshot> documentSnapshotList = value.getDocuments();
                    for(DocumentSnapshot documentSnapshot : documentSnapshotList){
                        Card card = new Card( (int)documentSnapshot.get("id"),
                                (String)documentSnapshot.get("color"),
                                (String)documentSnapshot.get("type"),
                                (String)documentSnapshot.get("value"));
                        cardArrayList.add(card);
                    }
                    if( cardArrayList.size() > 0 ) {
                        topCardListener.onTopCardSuccess(cardArrayList.get(0));
                    }
                }
                else {
                    topCardListener.onTopFailure(error.getMessage());
                }
            }
        });
    }

    public static void getDeckCards(DeckCardsListener deckCardsListener){
        firebaseFirestore.collection("unoGames").document(gameId).collection("deck").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if( error == null ){
                    ArrayList<Card> cardArrayList = new ArrayList<>();
                    List<DocumentSnapshot> documentSnapshotList = value.getDocuments();
                    for(DocumentSnapshot documentSnapshot : documentSnapshotList){
                        Card card = new Card( (int)documentSnapshot.get("id"),
                                (String)documentSnapshot.get("color"),
                                (String)documentSnapshot.get("type"),
                                (String)documentSnapshot.get("value"));
                        cardArrayList.add(card);
                    }
                    deckCardsListener.deckCardsSuccess(cardArrayList);
                }
                else if(error != null){
                    deckCardsListener.deckFailure(error.getMessage());
                }
            }
        });
    }

    public static Task<String> drawCard(DrawCardListener drawCardListener) {
        Map<String, Object> drawData = new HashMap<>();
        drawData.put("uid", firebaseAuth.getUid());
        drawData.put("gameId", gameId);

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

    public static void playCard(Card playedCard, PlayCardListener playCardListener){
        //FUNCTION CALL//
        Map<String, Object> playData = new HashMap<>();
        playData.put("uid", firebaseAuth.getUid());
        playData.put("gameId", gameId);
        Map<String, Object> card = new HashMap<>();
        card.put("id", playedCard.getId());
        card.put("color", playedCard.getColor());
        card.put("type", playedCard.getType());
        card.put("value", playedCard.getValue());
        playData.put("card", card);

        mFunctions.getHttpsCallable("playCard")
                .call(playData)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) {
                        if(task.isSuccessful()) {
                            playCardListener.playCardSuccess();
                        }
                        else{
                            playCardListener.playCardFailure(task.getException().getMessage());
                        }
                        return "";
                    }
                });
    }

    public static void leaveGame(){
        firebaseFirestore.collection("unoGames").document(gameId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    String winnerUserId = (String) task.getResult().get("user2Id");
                    if(winnerUserId.contains(firebaseAuth.getUid())){
                        winnerUserId = (String) task.getResult().get("user1Id");
                    }
                    Map<String, Object> leaveGameData = new HashMap<>();
                    leaveGameData.put("gameId", gameId);
                    leaveGameData.put("uid", winnerUserId);
                    mFunctions.getHttpsCallable("leaveGame")
                            .call(leaveGameData)
                            .continueWith(new Continuation<HttpsCallableResult, String>() {
                                @Override
                                public String then(@NonNull Task<HttpsCallableResult> task){
                                    if( task.isSuccessful() ) {
                                        gameId = "";
                                        Log.d(TAG, "then: success");
                                    }
                                    else{
                                        Log.d(TAG, "then: failure");
                                    }
                                    return "";
                                }
                            });
                }
                else{
                    Log.d(TAG, "then: failure");
                }
            }
        });
    }

    public static void exitGameListener(ExitGameListener exitGameListener){
        firebaseFirestore.collection("unoGames").document(gameId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if( error == null && value != null && value.contains("winnerId")){
                    String winnerId = (String) value.get("winnerId");
                    if(!winnerId.isEmpty()){
                        gameId = "";
                        if(winnerId.contains(firebaseAuth.getUid())) {
                            exitGameListener.onExitSuccess("Congratulations");
                        }
                        else{
                            exitGameListener.onExitSuccess("Other player won");
                        }
                    }
                }
                else if(error != null){
                    exitGameListener.onExitFailure( error.getMessage() );
                }
            }
        });
    }

    public static void updateTurn(){
        HashMap<String, Object> gameMap = new HashMap<>();
        firebaseFirestore.collection("unoGames").document(gameId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    String uid = (String) task.getResult().get("turn");
                    String user = (String) task.getResult().get("user1Id");
                    String user2 = (String) task.getResult().get("user2Id");
                    if(uid.contains(user)){
                        gameMap.put("turn", user2);
                    }
                    else{
                        gameMap.put("turn", user);
                    }
                    firebaseFirestore.collection("unoGames").document(gameId).update(gameMap).addOnCompleteListener(new OnCompleteListener<Void>() {
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
}
