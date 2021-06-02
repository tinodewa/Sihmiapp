package com.roma.android.sihmi.utils;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.roma.android.sihmi.model.network.ApiClient;
import com.roma.android.sihmi.model.network.MasterService;
import com.roma.android.sihmi.model.response.UploadFileResponse;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

public class UploadFile {

    public static void selectImage(Activity activity){
        Intent openGalleryIntent = new Intent(Intent.ACTION_PICK);
        openGalleryIntent.setType("image/*");
        activity.startActivityForResult(openGalleryIntent, Constant.REQUEST_GALLERY_CODE);
    }

    public static String getRealPathFromURIPath(Uri contentURI, Activity activity) {
        Cursor cursor = activity.getContentResolver().query(contentURI, null, null, null, null);
        String str;
        if (cursor == null) {
            str = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            str = cursor.getString(idx);
        }

        if (cursor != null) {
            cursor.close();
        }

        return str;
    }

    public static void startCropImageActivity(Activity activity, Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .setMultiTouchEnabled(true)
                .start(activity);
    }

    public static void selectFile(Activity activity){
        new MaterialFilePicker()
                .withActivity(activity)
                .withRequestCode(Constant.REQUEST_DOC_CODE)
                .withHiddenFiles(true)
                .withFilter(Pattern.compile(".*\\.pdf$"))
                .withTitle("Select PDF file")
                .start();
    }

    public static void uploadFileToServer(String type, String dataInput, Callback<UploadFileResponse> response){
        File file = new File(dataInput);

        MasterService masterService = ApiClient.getInstance().getApi();

        RequestBody requestBody;
        if (type.equalsIgnoreCase(Constant.IMAGE)){
            requestBody = RequestBody.create(MediaType.parse("image/*"), file);
        } else {
            requestBody = RequestBody.create(MediaType.parse("application/pdf"), file);
        }

        Map<String, RequestBody> map = new HashMap<>();
        map.put("files\"; filename=\"" + file.getName() + "\"", requestBody);


        Call<UploadFileResponse> call = masterService.uploadFile(Constant.getToken(), map);
        call.enqueue(response);
    }

}