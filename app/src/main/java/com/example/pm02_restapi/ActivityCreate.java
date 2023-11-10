package com.example.pm02_restapi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ActivityCreate extends AppCompatActivity {

    static final int request_image = 101;
    static final int access_camera = 201;
    ImageView imageView;
    Button btntakefoto, btncreate;
    String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        imageView = (ImageView) findViewById(R.id.foto);
        btntakefoto = (Button) findViewById(R.id.btntakeFoto);
        btncreate = (Button) findViewById(R.id.btnCreate);

        btntakefoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permisosCamara();

            }
        });

        btncreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarDatos();
                
            }
        });
    }

    private void enviarDatos() {
        convertirImageBase64(currentPhotoPath);
    }

    private void permisosCamara() {
        if(ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            //Request a la API del sistema operativo, esta es la peticion del permiso a la API del sistema operativo
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},access_camera);
        }
        else{
            //si ya tenemos el permiso y esta otorgado, entonces vamos a tener la fotografia.
            //TomarFoto();
            dispatchTakePictureIntent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == access_camera)
        {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                dispatchTakePictureIntent();
                //TomarFoto();
            }
            else
            {
                Toast.makeText(getApplicationContext(), "se necesita el permiso de la camara",Toast.LENGTH_LONG).show();
            }
        }
    }
    private File createImageFile() throws IOException {

        // Create an image file name

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        String imageFileName = "JPEG_" + timeStamp + "_";

        //Environment es para obtener variables de entorno del sistema//con esto podemos acceder a los directorios del celular
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES); //obtener directorio de las imagenes

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        //currentPhotoPath permite obtener la url donde está ubicada nuestra imagen
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //para tomar la foto

        // Ensure that there's a camera activity to handle the intent

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile(); //cargamos la imagen directamente de de la url
            } catch (IOException ex) {

                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {

                //Obtener URL de nuestra imagen
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.pm02_restapi.fileprovider", photoFile);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                startActivityForResult(takePictureIntent, request_image);
            }
        }
    }
    //Capturar lo que viene desde el ActivityForResult.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Obtener toda la informacion de la data, pueden ser imagenes, texto, vide etc.
        //viene como respuesta al callback de la respuesta de la API del sistema operativo.
        if(requestCode == request_image){
            /*Obtener la información que viene de la data
            Bundle extras = data.getExtras();
            Bitmap image = (Bitmap) extras.get("data");
            imageView.setImageBitmap(image);*/
            try {
                File foto = new File(currentPhotoPath); //trae toda la url
                //mandar el objeto
                imageView.setImageURI(Uri.fromFile(foto));
            }
            catch (Exception ex){
                ex.toString();
            }
        }
    }
    private String convertirImageBase64(String path){
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        //Aqui va la calidad de la imagen
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        //Comprimimos la imagen en un arreglo de bytes
        byte[] imagearray = byteArrayOutputStream.toByteArray();
        //Comprimimos la imagen a un base64
        return Base64.encodeToString(imagearray, Base64.DEFAULT);
    }
}