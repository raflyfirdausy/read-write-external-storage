package pmo2.kelompok4.readwriteexternal;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    private EditText et_isi;
    private EditText et_namaFile;
    private Button btn_simpanTxt;
    private Button btn_bacaTxt;
    private TextView tv_hasil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_isi          = (EditText)    findViewById(R.id.et_isi);
        et_namaFile     = (EditText)    findViewById(R.id.et_namaFile);
        btn_simpanTxt   = (Button)      findViewById(R.id.btn_simpanTxt);
        btn_bacaTxt     = (Button)      findViewById(R.id.btn_bacaTxt);
        tv_hasil        = (TextView)    findViewById(R.id.tv_hasil);

        //cek Permission
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE
            }, 1000);
        }

        //Untuk menyimpan file ke External Storage
        btn_simpanTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Cek Dulu Apakah nama filenya kosong atau engga
                if(TextUtils.isEmpty(et_namaFile.getText().toString())){
                    et_namaFile.setError("Nama File Tidak Boleh Kosong!");
                    Toast.makeText(MainActivity.this, Environment.getExternalStorageDirectory().getPath(), Toast.LENGTH_SHORT).show();
                } else{
                    //proses menyimpan file
                    String namaFile = et_namaFile.getText().toString() + ".txt";

                    File file       = new File("/storage/11E5-250A", namaFile);

                    try{
                        FileOutputStream fos = new FileOutputStream(file);
                        fos.write(et_isi.getText().toString().getBytes());
                        fos.close();
                        Toast.makeText(MainActivity.this, "Data berhasil Di Simpan!", Toast.LENGTH_SHORT).show();
                    } catch (FileNotFoundException e) {
                        Toast.makeText(MainActivity.this, "FileNotFoundException : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    } catch (IOException e) {
                        Toast.makeText(MainActivity.this, "IOException : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            }
        });

        btn_bacaTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent()
                        .setType("text/plain") //untuk membatasi hanya file txt saja yang bisa di pilih
                        .setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Pilih text yang Mau di buka"), 123);
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1000){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission Granted!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==123 && resultCode== RESULT_OK) {
            Uri uri = data.getData();
            String hasil;

            try{
                InputStream inputStream = getContentResolver().openInputStream(uri);
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String kata;
                StringBuilder stringBuilder = new StringBuilder();

                while ((kata = bufferedReader.readLine()) != null){
                    stringBuilder.append(kata + "\n");
                }
                inputStream.close();
                hasil = stringBuilder.toString();
                tv_hasil.setText(hasil);
                Toast.makeText(MainActivity.this, "File Berhasil Di Buka", Toast.LENGTH_SHORT).show();
            } catch (FileNotFoundException e) {
                Toast.makeText(MainActivity.this, "FileNotFoundException : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } catch (IOException e) {
                Toast.makeText(MainActivity.this, "IOException : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }
}
