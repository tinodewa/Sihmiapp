package com.roma.android.sihmi.view.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.roma.android.sihmi.R;
import com.roma.android.sihmi.helper.FileDownloader;
import com.roma.android.sihmi.utils.Tools;
import com.shockwave.pdfium.PdfDocument;

import java.io.File;
import java.io.IOException;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressLint("NonConstantResourceId")
public class ChatFileDetailActivity extends AppCompatActivity {
    public static final String NAMA_FILE = "nama_file";
    public static final String TYPE_FILE = "type_file";
    public static final String TIME_FILE = "time_file";
    @BindView(R.id.img_image)
    ImageView img_Image;
    @BindView(R.id.pdf_view)
    PDFView pdfView;

    String nama, type, timeFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_file_detail);
        ButterKnife.bind(this);

        pdfView = (PDFView) findViewById(R.id.pdf_view);

        nama = getIntent().getStringExtra(NAMA_FILE);
        type = getIntent().getStringExtra(TYPE_FILE);
        timeFile = getIntent().getStringExtra(TIME_FILE);

        if (type.equals("Dokument")){
            img_Image.setVisibility(View.GONE);
            pdfView.setVisibility(View.VISIBLE);
//            File pdfFile = new File(Environment.getExternalStorageDirectory() + "/filepdf/" + timeFile+".pdf");  // -> filename = maven.pdf
//            if (pdfFile.exists()){
//                Toast.makeText(this, "File Ada", Toast.LENGTH_SHORT).show();
//                displayFromFile(pdfFile);
//            } else {
//                Toast.makeText(this, "Menunggu Donwload", Toast.LENGTH_SHORT).show();
                new DownloadFile(this).execute(nama, timeFile);
//            }
        } else if (type.equals("Gambar")){
            Glide.with(this).load(nama).into(img_Image);
            img_Image.setVisibility(View.VISIBLE);
            pdfView.setVisibility(View.GONE);
        } else {
            finish();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class DownloadFile extends AsyncTask<String, Void, Void> {
        File pdfFile;
        public Activity activity;

        @SuppressWarnings("deprecation")
        public DownloadFile(Activity a) {
            this.activity = a;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Tools.showProgressDialog(activity, "Tunggu Sebentar...");
        }

        @Override
        protected Void doInBackground(String... strings) {
            String fileUrl = strings[0];   // -> http://maven.apache.org/maven-1.x/maven.pdf
            String fileName = strings[1];  // -> maven.pdf
            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
            File folder = new File(extStorageDirectory, "filepdf");
            //noinspection ResultOfMethodCallIgnored
            folder.mkdir();

            pdfFile = new File(folder, fileName);

            try{
                //noinspection ResultOfMethodCallIgnored
                pdfFile.createNewFile();
            }catch (IOException e){
                e.printStackTrace();
            }
            FileDownloader.downloadFile(fileUrl, pdfFile);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            displayFromFile(pdfFile);
            Tools.dissmissProgressDialog();
        }
    }

    private void displayFromFile(File file) {

        Uri uri = Uri.fromFile(new File(file.getAbsolutePath()));
        final String pdfFileName = getFileName(uri);

        pdfView.fromFile(file)
                .onPageChange((page, pageCount) -> setTitle(String.format("%s %s / %s", pdfFileName, page + 1, pageCount)))
                .enableAnnotationRendering(true)
                .onLoad(nbPages -> printBookmarksTree(pdfView.getTableOfContents(), "-"))
                .scrollHandle(new DefaultScrollHandle(this))
                .spacing(10) // in dp
                .onPageError((page, t) -> {

                })
                .load();
    }

    public void printBookmarksTree(List<PdfDocument.Bookmark> tree, String sep) {
        for (PdfDocument.Bookmark b : tree) {
            if (b.hasChildren()) {
                printBookmarksTree(b.getChildren(), sep + "-");
            }
        }
    }

    public String getFileName(Uri uri) {
        String result = null;
        String urlScheme = uri.getScheme();
        if (urlScheme != null) {
            if (uri.getScheme().equals("content")) {
                try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                    if (cursor != null && cursor.moveToFirst()) {
                        result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }
}
