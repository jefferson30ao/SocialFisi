package com.example.mnu92.socialapp.view.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.mnu92.socialapp.GlideApp;
import com.example.mnu92.socialapp.MediaFiles;
import com.example.mnu92.socialapp.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;


public class EditPostActivity extends AppCompatActivity {

    public DatabaseReference mReference;
    public FirebaseUser mUser;

    static final int RC_IMAGE_TAKE = 8000;
    static final int RC_VIDEO_TAKE = 8001;
    static final int RC_IMAGE_PICK = 9000;
    static final int RC_VIDEO_PICK = 9001;
    static final int RC_AUDIO_PICK = 9002;




    EditText postText;
    ImageView imagePost;
    Button mPublishButton;
    Button mCameraImageButton;
    Button mCameraVideoButton;
    Button mImageButton;
    Button mVideoButton;
    Button mAudioButton;

    Uri mFileUri;

    Uri mediaUri;
    String mediaType;

    private MediaRecorder mRecorder = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);

        Intent intent = getIntent();
        mReference = FirebaseDatabase.getInstance().getReference();
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        final String id = intent.getStringExtra("ID");
        postText = findViewById(R.id.postText);
        imagePost = findViewById(R.id.image);

        mPublishButton = findViewById(R.id.publish);
        mImageButton = findViewById(R.id.btnImage);
        mVideoButton = findViewById(R.id.btnVideo);
        mAudioButton = findViewById(R.id.btnAudio);
        mCameraImageButton = findViewById(R.id.btnCameraImage);
        mCameraVideoButton = findViewById(R.id.btnCameraVideo);

        LinearLayout ll = findViewById(R.id.linearLayoutButtons);

        mPublishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitPost(id);
            }
        });

        mCameraImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

        mCameraVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakeVideoIntent();
            }
        });

        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI), RC_IMAGE_PICK);
            }
        });

        mVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI), RC_VIDEO_PICK);
            }
        });

        mAudioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI), RC_AUDIO_PICK);
            }
        });

        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String post = dataSnapshot.child("/posts/data").child(id).child("content").getValue().toString();
                //String mediaType = dataSnapshot.child("/posts/data").child(id).child("mediaType").getValue().toString();
                if(dataSnapshot.child("/posts/data").child(id).child("mediaUrl").getValue() != null) {
                    String mediaUrl = dataSnapshot.child("/posts/data").child(id).child("mediaUrl").getValue().toString() ;
                    try {
                        GlideApp.with(EditPostActivity.this).load(mediaUrl).into(imagePost);
                    }catch (Exception ex) {
                        //A veces falla por que la actividad se cierra mientras el listener está activo.
                    }
                }
                postText.setText(post);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        imagePost.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                android.app.AlertDialog.Builder dialogo = new AlertDialog.Builder(EditPostActivity.this);
                dialogo.setTitle(R.string.warning);
                dialogo.setMessage(R.string.dete_img);
                dialogo.setPositiveButton("si", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int idd) {
                        GlideApp.with(EditPostActivity.this).load("").into(imagePost);
                        mReference.child("/posts/data").child(id).child("mediaUrl").setValue(null);
                        mReference.child("/posts/data").child(id).child("mediaType").setValue("");
                    }
                });
                dialogo.setNegativeButton("no", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) { }
                });

                dialogo.create();
                dialogo.show();
                return false;
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_IMAGE_TAKE && resultCode == RESULT_OK) {
            mediaUri = mFileUri;
            mediaType = "image";
            GlideApp.with(this).load(mediaUri).into(imagePost);
        } else if (requestCode == RC_VIDEO_TAKE && resultCode == RESULT_OK) {
            mediaUri = mFileUri;
            mediaType = "video";
            GlideApp.with(this).load(mediaUri).into(imagePost);
        }

        else if(data != null) {
            if (requestCode == RC_IMAGE_PICK) {
                mediaUri = data.getData();
                mediaType = "image";
                GlideApp.with(this).load(mediaUri).into(imagePost);
            } else if (requestCode == RC_VIDEO_PICK) {
                mediaUri = data.getData();
                mediaType = "video";
                GlideApp.with(this).load(mediaUri).into(imagePost);
            } else if (requestCode == RC_AUDIO_PICK) {
                mediaUri = data.getData();
                mediaType = "audio";
                GlideApp.with(this).load(mediaUri).into(imagePost);
            }
        }
    }

    void submitPost(String id){
        final String postText2 = postText.getText().toString();

        if(postText2.isEmpty()){
            postText.setError("Required");
            return;
        }

        mPublishButton.setEnabled(false);

        if (mediaType == null) {
            updatePost(postText2, null,id);
        } else {
            uploadAndUpdatePost(postText2,id);
        }

    }

    private void updatePost(String postText, String mediaUrl,String id) {
        DatabaseReference post = mReference.child("/posts/data").child(id);
        post.child("content").setValue(postText);
        if(post.child("mediaType").getKey() != null) {
            post.child("mediaType").setValue(mediaType);
        }
        if(post.child("mediaUrl").getKey() != null) {
            post.child("mediaUrl").setValue(mediaUrl);
        }
        finish();
    }

    private void uploadAndUpdatePost(final String postText,final String id){
        if(mediaType != null) {
            FirebaseStorage.getInstance().getReference(mediaType + "/" + UUID.randomUUID().toString() + mediaUri.getLastPathSegment()).putFile(mediaUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    return task.getResult().getStorage().getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        String downloadUri = task.getResult().toString();
                        updatePost(postText, downloadUri,id);
                    }
                }
            });
        }
    }

    private void dispatchTakePictureIntent() {

        Uri fileUri = null;
        try {
            fileUri = MediaFiles.createFile(this, MediaFiles.Type.IMAGE).uri;
        } catch (IOException ex) {
            // No se pudo crear el fichero
        }

        if (fileUri != null) {
            mFileUri = fileUri;

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            startActivityForResult(intent, RC_IMAGE_TAKE);
        }
    }

    private void dispatchTakeVideoIntent() {

        Uri fileUri = null;
        try {
            fileUri = MediaFiles.createFile(this, MediaFiles.Type.VIDEO).uri;
        } catch (IOException ex) {
            // No se pudo crear el fichero
        }

        if (fileUri != null) {
            mFileUri = fileUri;

            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            startActivityForResult(intent, RC_VIDEO_TAKE);
        }
    }

    void startRecording(){
        MediaFiles.UriPathFile file = null;
        try {
            file = MediaFiles.createFile(this, MediaFiles.Type.AUDIO);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(file != null) {
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setOutputFile(file.path);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            try {
                mRecorder.prepare();
            } catch (IOException e) {

            }

            mediaType = "audio";
            mediaUri = file.uri;
            mRecorder.start();
        }
    }

    void stopRecording(){
        if(mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
    }

}
