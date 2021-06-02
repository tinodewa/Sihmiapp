package com.roma.android.sihmi.view.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;
import com.roma.android.sihmi.R;
import com.shockwave.pdfium.PdfDocument;

import java.io.File;
import java.util.List;
import java.util.regex.Pattern;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressLint("NonConstantResourceId")
public class FileFormActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.et_title)
    EditText etTitle;
    @BindView(R.id.et_desc)
    EditText etDesc;
    @BindView(R.id.btn_upload)
    Button btnUpload;
    @BindView(R.id.tv_nama_file)
    TextView tvNamaFile;
    @BindView(R.id.pdfView)
    PDFView pdfView;
    @BindView(R.id.btn_batal)
    Button btnBatal;
    @BindView(R.id.btn_simpan)
    Button btnSimpan;

    private String pdfFileName;
    private int pageNumber = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_form);
        ButterKnife.bind(this);

        pdfView = (PDFView) findViewById(R.id.pdfView);

        toolbar.setTitle("Upload File".toUpperCase());
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    @OnClick(R.id.btn_upload)
    public void goUpload(){
        new MaterialFilePicker()
                .withActivity(this)
                .withRequestCode(1212)
                .withHiddenFiles(true)
                .withFilter(Pattern.compile(".*\\.pdf$"))
                .withTitle("Select PDF file")
                .start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (data != null) {
            if (requestCode == 1212) {
                if (resultCode == RESULT_OK) {
                    String path = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);

                    if (path != null) {
                        File file = new File(path);
                        displayFromFile(file);
                    }

                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void displayFromFile(File file) {

        Uri uri = Uri.fromFile(new File(file.getAbsolutePath()));
        pdfFileName = getFileName(uri);

        tvNamaFile.setText(pdfFileName);

        pdfView.fromFile(file)
                .defaultPage(pageNumber)
                .onPageChange((page, pageCount) -> {
                    pageNumber = page;
                    setTitle(String.format("%s %s / %s", pdfFileName, page + 1, pageCount));
                })
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

            //Log.e(TAG, String.format("%s %s, p %d", sep, b.getTitle(), b.getPageIdx()));

            if (b.hasChildren()) {
                printBookmarksTree(b.getChildren(), sep + "-");
            }
        }
    }

    public String getFileName(Uri uri) {
        String result = null;
        String uriScheme = uri.getScheme();
        if (uriScheme != null) {
            if (uriScheme.equals("content")) {
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
