package com.example.firebase3;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentProviderOperation;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class UploadActivity extends AppCompatActivity {
    ImageView imageView;
    Uri resimData;
    FirebaseStorage depo;
    StorageReference depoRef;

    FirebaseFirestore fbFireStore;
    FirebaseAuth fbAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        imageView = findViewById(R.id.imageView3);
    }

    public void resimSec(View v){
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i,2);

        depo = FirebaseStorage.getInstance();
        depoRef = depo.getReference();
        fbAuth = FirebaseAuth.getInstance();
        fbFireStore = FirebaseFirestore.getInstance();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==2 && resultCode==RESULT_OK && data!=null){
            resimData = data.getData();
            try {
                if (Build.VERSION.SDK_INT >= 28) {
                    //SDK>=28 İÇİN ÇALIŞAN GALERİDEN RESİM SEÇME KODU
                    ImageDecoder.Source kaynak = ImageDecoder.createSource(this.getContentResolver(), resimData);
                    Bitmap secilenResim = ImageDecoder.decodeBitmap(kaynak);
                    imageView.setImageBitmap(secilenResim);
                }
                else{
                    Bitmap secilenResim = MediaStore.Images.Media.getBitmap(this.getContentResolver(),resimData);
                    imageView.setImageBitmap(secilenResim);
                }
            }
            catch(IOException e){

            }
        }
    }
    String resimYolu;
    public void resmiYukle(View v){
            if(resimData!=null){
                UUID uuid = UUID.randomUUID();
                resimYolu = "resimler/" + "resim_" + uuid.toString() + ".jpg";
                depoRef.child(resimYolu).putFile(resimData).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UploadActivity.this, "HATA!! RESİM YÜKLENEMEDİ!", Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(UploadActivity.this, "RESİM YÜKLENDİ.", Toast.LENGTH_SHORT).show();
                        StorageReference resimRef = FirebaseStorage.getInstance().getReference(resimYolu);
                        resimRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                System.out.println(uri.toString());
                                String userEmail = fbAuth.getCurrentUser().getEmail();
                                Post p = new Post(userEmail, resimYolu, FieldValue.serverTimestamp().toString());
                                fbFireStore.collection("posts").add(p).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        onBackPressed();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(UploadActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });

                    }
                });
            }
    }
}
