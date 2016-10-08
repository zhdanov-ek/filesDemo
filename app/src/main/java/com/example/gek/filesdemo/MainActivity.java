package com.example.gek.filesdemo;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Button btnReadFile, btnWriteFile, btnReadSD, btnWriteSD, btnRemoveFile;
    EditText etInputText, etFileName;
    TextView tvResult, tvSDCardInfo, tvPathInternal;

    private final String DIR = "MyFiles";       // Имя под папки куда будут создаваться файлы
    private String fileName = "";
    private String textForWrite = "";

    // File - класс для работы с файлами, который позволяет выполнять с файлами операции:
    // создания, удаления, извлечения части пути, переименования, создания папки, смены прав RWX,
    // получения списка файлов в папке (втч по маске) и прочее
    File sdPathAbsolute;        // карта памяти
    File sdPathFile;            // полный путь к файлу на карте памяти (без имени файла)
    File sdFile;                // полный путь к файлу на карте памяти (уже с именем файла)
    File pathAbsolute;          // путь к рабочей папке программы, где создаются ее файлы



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnRemoveFile = (Button)findViewById(R.id.btnRemoveFile);
        btnRemoveFile.setOnClickListener(this);
        btnReadFile = (Button)findViewById(R.id.btnReadFile);
        btnReadFile.setOnClickListener(this);
        btnWriteFile = (Button)findViewById(R.id.btnWriteFile);
        btnWriteFile.setOnClickListener(this);
        btnReadSD = (Button)findViewById(R.id.btnReadSD);
        btnReadSD.setOnClickListener(this);
        btnWriteSD = (Button)findViewById(R.id.btnWriteSD);
        btnWriteSD.setOnClickListener(this);

        etFileName = (EditText)findViewById(R.id.etFileName);
        etInputText = (EditText)findViewById(R.id.etInputText);
        tvResult = (TextView)findViewById(R.id.tvResult);
        tvSDCardInfo = (TextView)findViewById(R.id.tvSDCardInfo);
        tvPathInternal = (TextView)findViewById(R.id.tvPathInternal);

        // Опредлеяем абсолютный путь нашей рабочей папки во внутренней памяти
        pathAbsolute = getFilesDir();
        tvPathInternal.setText(pathAbsolute.toString());


        // проверяем доступность SD карты и если она есть определяем путь к ней
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            tvSDCardInfo.setText("SD-карта не доступна: " + Environment.getExternalStorageState());
            btnWriteSD.setEnabled(false);
            btnReadSD.setEnabled(false);
        } else {
            // получаем путь к SD от системы в объект типа File
            sdPathAbsolute = Environment.getExternalStorageDirectory();
            tvSDCardInfo.setText("SD card in system: " + sdPathAbsolute.getAbsolutePath());
            // формируем новый объект типа File с указанием
            sdPathFile = new File(sdPathAbsolute, DIR);
            tvSDCardInfo.setText(tvSDCardInfo.getText() + "\n" +
                    "Work path " + sdPathFile.getAbsolutePath());
            // создаем каталог по указанному пути
            sdPathFile.mkdir();
        }


    }

    @Override
    public void onClick(View v) {
        textForWrite = etInputText.getText().toString() + "\n";
        fileName = etFileName.getText().toString();

        switch (v.getId()){
            case R.id.btnRemoveFile:
                removeFile();
                break;
            case R.id.btnWriteFile:
                writeFile();
                break;
            case R.id.btnReadFile:
                readFile();
                break;
            case R.id.btnReadSD:
                readSDFile();
                break;
            case R.id.btnWriteSD:
                writeSDFile();
                break;
            default:
                break;
        }
    }

    private void removeFile(){
        File removeFile = new File(pathAbsolute, fileName);
        if (removeFile.exists()) {
            removeFile.delete();
            Toast.makeText(this, fileName + " removed from memory", Toast.LENGTH_SHORT).show();
        }


        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            // Файл создаем на основании пути типа File и имени файла типа String
            // Такой способ избавляем от проблем с разделителями папок - \
            File removeSDFile = new File(sdPathFile, fileName);
            if (removeSDFile.exists()) {
                removeSDFile.delete();
                Toast.makeText(this, fileName + " removed from " + sdPathAbsolute, Toast.LENGTH_SHORT).show();
            }
        }
    }

    // При работе с внутренней памятью андрод можно использовать его методы для файлов
    private void writeFile(){
        try {
            // отрываем поток для записи
            // BufferedWriter - класс, позволяющий записывать текст в указанный поток
            // Ему на вход подаем
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                    openFileOutput(fileName, MODE_APPEND)));
            // пишем данные
            // writeFile – запись файла во внутреннюю память. Используется метод openFileOutput,
            // который на вход берет имя файла и режим записи:
            // MODE_PRIVATE – файл доступен только этому приложению,
            // MODE_WORLD_READABLE – файл доступен для чтения всем,
            // MODE_WORLD_WRITEABLE - файл доступен для записи всем,
            // MODE_APPEND – файл будет дописан, а не начат заново.
            bw.write(textForWrite);
            // закрываем поток
            bw.close();
            tvResult.setText(textForWrite + " has been writed in the " + fileName);
        } catch (FileNotFoundException e) {
            tvResult.setText("file not found");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readFile(){
        String result = "";
        try {
            // открываем поток для чтения
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    openFileInput(fileName)));
            String str = "";
            // читаем содержимое
            while ((str = br.readLine()) != null) {
                result = result + str +"\n";
            }
            tvResult.setText(result);

        } catch (FileNotFoundException e) {
            tvResult.setText("file not found");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // При работе с картой SD используются стандартные Java механизмы - FileReader, FileWriter
    private void readSDFile(){
        String result = "";
        sdFile = new File(sdPathFile, fileName);
        try {
            // открываем поток для чтения
            BufferedReader br = new BufferedReader(new FileReader(sdFile));
            String str = "";
            // читаем содержимое
            while ((str = br.readLine()) != null) {
                result = result + str +"\n";
            }
            tvResult.setText(result);
        } catch (FileNotFoundException e) {
            tvResult.setText("file not found");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // разрешение в манифест WRITE_EXTERNAL_STORAGE.
    private void writeSDFile(){
        sdFile = new File(sdPathFile, fileName);
        try {
            // открываем поток для записи
            //
            BufferedWriter bw = new BufferedWriter(new FileWriter(sdFile, true));   //true - добавление в файл

            // пишем данные
            bw.write(etInputText.getText().toString()+"\n");
            // закрываем поток
            bw.close();
            tvResult.setText(textForWrite + " has been writed in the " + sdFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
