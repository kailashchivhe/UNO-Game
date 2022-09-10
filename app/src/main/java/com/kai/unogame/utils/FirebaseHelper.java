package com.kai.unogame.utils;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.kai.unogame.listener.LoginListener;
import com.kai.unogame.listener.ProfileListener;
import com.kai.unogame.listener.RegistrationListener;
import com.kai.unogame.model.User;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

public class FirebaseHelper {

    static FirebaseAuth firebaseAuth;
    static FirebaseFirestore firebaseFirestore;
    static FirebaseFirestore db;
    private static StorageReference mStorageRef;
    private static StorageTask mUploadTask;
    public static final String TAG = "vidit";

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

    protected static void upload(Bitmap bitmap, RegistrationListener registrationListener){
        if (bitmap != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()+".JPEG");
            mUploadTask = fileReference.putBytes(data)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> downloadUri = taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    HashMap<String,Object> map = new HashMap<>();
                                    map.put("uri",uri.toString());
                                    db.collection("unogame").document("Users").collection("Users").document(firebaseAuth.getCurrentUser().getUid()).update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Log.d(TAG, "onSuccess: uploaded");
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    registrationListener.registeredFailure(e.getMessage());
                                }
                            });
                            Log.d(TAG, "onSuccess: registration done");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            registrationListener.registeredFailure(e.getMessage());
                        }
                    });
        }
        else {
            //Photo not clicked
            registrationListener.registeredFailure("Photo not Clicked");
        }
    }

    public static void register(String email, String password, String firstName, String lastName, String city, String gender, Bitmap bitmapProfile, RegistrationListener registerListener){
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
                                            upload(bitmapProfile,registerListener);
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
}
