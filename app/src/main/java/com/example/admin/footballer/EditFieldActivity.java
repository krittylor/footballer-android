package com.example.admin.footballer;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.admin.footballer.Models.Field;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class EditFieldActivity extends BaseActivity implements IPickResult, OnConnectionFailedListener{
    private static final String TAG = "AddFieldActivity";
    private EditText fieldNameEdit, cityNameEdit, locationNameEdit, priceEdit;
    private ImageView fieldImage;
    private List<String> photoUrls = new ArrayList<String>();
    private Uri currentPhotoUri = null;
    private double longitude = 50, latitude = 10;
    private GoogleApiClient mGoogleApiClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_field);

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();


        fieldNameEdit = (EditText)findViewById(R.id.field_name);
        cityNameEdit = (EditText)findViewById(R.id.field_city);
        locationNameEdit = (EditText)findViewById(R.id.field_location);
        priceEdit = (EditText)findViewById(R.id.field_price);
        fieldImage = (ImageView)findViewById(R.id.field_image);
        fieldImage.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                onImageViewClick();
            }
        });

        FirebaseUtil.getFieldsRef().child(FieldsActivity.fieldId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    Field field = dataSnapshot.getValue(Field.class);
                    fieldNameEdit.setText(field.fieldName);
                    cityNameEdit.setText(field.cityName);
                    locationNameEdit.setText(field.locationName);
                    priceEdit.setText(String.valueOf(field.price));
                    photoUrls = field.photoUrls;
                    if(photoUrls == null)
                        photoUrls = new ArrayList<String>();
                    longitude = field.longitude;
                    latitude = field.latitude;
                    currentPhotoUri = null;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        ImageButton mapViewButton = (ImageButton)findViewById(R.id.pick_location);
        mapViewButton.setOnClickListener(onPickLocationListener);
        Button uploadPhotoButton = (Button)findViewById(R.id.upload_field_photo);
        uploadPhotoButton.setOnClickListener(onUploadClickListener);
        Button addFieldButton = (Button)findViewById(R.id.add_field);
        addFieldButton.setOnClickListener(onSaveFieldListener);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.drawable.footballer);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

    }

    protected void onImageViewClick() {
        PickSetup setup = new PickSetup();

        PickImageDialog.build(setup).show(this);
    }

    @Override
    public void onPickResult(final PickResult r) {
        if (r.getError() == null) {
            //If you want the Uri.
            //Mandatory to refresh image from Uri.
            //getImageView().setImageURI(null);

            //Setting the real returned image.
            //getImageView().setImageURI(r.getUri());

            currentPhotoUri = r.getUri();
            fieldImage.setImageURI(currentPhotoUri);
            //If you want the Bitmap.
            //avatarView.setImageBitmap(r.getBitmap());

            //r.getPath();
        } else {
            //Handle possible errors
            //TODO: do what you have to do with r.getError();
            Toast.makeText(this, r.getError().getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_field, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == android.R.id.home){
            onBackPressed();
        }
        if(id == R.id.action_close){
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == 2){
            if(resultCode == RESULT_OK){
                Place place = PlacePicker.getPlace(this, data);
                latitude = place.getLatLng().latitude;
                longitude = place.getLatLng().longitude;
                locationNameEdit.setText(place.getName());
                cityNameEdit.setText(place.getAddress());
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                try{
                    List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                    String cityName = addresses.get(0).getLocality();
                    cityNameEdit.setText(cityName);
                } catch (Exception e){
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private View.OnClickListener onPickLocationListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

            try{
                startActivityForResult(builder.build(EditFieldActivity.this), 2);
            } catch (Exception e){
                Toast.makeText(EditFieldActivity.this, "Something went wrong with map api", Toast.LENGTH_LONG).show();
            }
        }
    };
    private View.OnClickListener onUploadClickListener = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            if(currentPhotoUri == null)
                return;
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference imageReference = storageRef.child(UUID.randomUUID().toString());

            // Create the file metadata
            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType("image/jpeg")
                    .build();
            UploadTask uploadTask = imageReference.putFile(currentPhotoUri);
            showProgressDialog("Uploading photo");
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Handle successful uploads on complete
                    dismissProgressDialog();
                    Uri downloadUrl = taskSnapshot.getMetadata().getDownloadUrl();
                    photoUrls.add(downloadUrl.toString());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    dismissProgressDialog();
                    // Handle unsuccessful uploads
                    Toast.makeText(EditFieldActivity.this, "Uploading photo failed", Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    private View.OnClickListener onSaveFieldListener = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            int price = 0;
            if(!priceEdit.getText().toString().equalsIgnoreCase(""))
                price = Integer.valueOf(priceEdit.getText().toString());
            Field field = new Field(fieldNameEdit.getText().toString(),
                    cityNameEdit.getText().toString(),
                    locationNameEdit.getText().toString(),
                    FieldsActivity.userId,
                    latitude,
                    longitude,
                    photoUrls,
                    price
            );
            FirebaseUtil.getFieldsRef().child(FieldsActivity.fieldId).setValue(field.toMap()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Intent intent = new Intent();
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EditFieldActivity.this, "Saving field failed", Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "No google map service", Toast.LENGTH_LONG).show();
    }


}