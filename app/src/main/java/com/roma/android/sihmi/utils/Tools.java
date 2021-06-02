package com.roma.android.sihmi.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.roma.android.sihmi.ListenerHelper;
import com.roma.android.sihmi.R;
import com.roma.android.sihmi.core.CoreApplication;
import com.roma.android.sihmi.helper.AgendaScheduler;
import com.roma.android.sihmi.model.database.database.AppDb;
import com.roma.android.sihmi.model.database.entity.Agenda;
import com.roma.android.sihmi.model.database.entity.Contact;
import com.roma.android.sihmi.view.adapter.ChatAdapter;

import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Tools {

    static ProgressDialog dialog;

    public static long getStartCurrentDayMillis() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis();
    }

    public static String getTimeFromMillis(long currentDateTime) {
        Date currentDate = new Date(currentDateTime);
        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("HH:mm");
        return df.format(currentDate);
    }

    public static Long getMillisFromTimeStr(String dateStr, String format) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        Date date = sdf.parse(dateStr);
        return date != null ? date.getTime() : 0;
    }

    public static String getDateFromMillis(long currentDateTime) {
        Date currentDate = new Date(currentDateTime);
        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("dd MMM yyyy");
        return df.format(currentDate);
    }

    public static String getDateLaporanFromMillis(long currentDateTime) {
        Date currentDate = new Date(currentDateTime);
        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        return df.format(currentDate);
    }

    public static String getDateTimeLaporanFromMillis(long currentDateTime) {
        Date currentDate = new Date(currentDateTime);
        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("dd/MM/yyyy, HH:mm");
        return df.format(currentDate);
    }

    public static String getYearFromMillis(long currentDateTime) {
        Date currentDate = new Date(currentDateTime);
        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("yyyy");
        return df.format(currentDate);
    }

    public static String getFullDateFromMillis(long currentDateTime) {
        Date currentDate = new Date(currentDateTime);
        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("EEEE, dd MMM yyyy");
        return df.format(currentDate);
    }

    public static String dateNow(){
        SimpleDateFormat format = new SimpleDateFormat("MM-yyyy", Locale.getDefault());
        return format.format(new Date());
    }

    public static String convertUTF8ToString(String s) {
        String out;
        out = new String(s.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        return out;
    }

    // convert internal Java String format to UTF-8
    public static String convertStringToUTF8(String s) {
        String out;
        out = new String(s.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
        return out;
    }

    public static void showProgressDialog(Context context, String message) {
        dialog = new ProgressDialog(context);
        dialog.setMessage(message);
        dialog.setIndeterminate(true);
        dialog.show();
    }

    public static void dissmissProgressDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static void showToast(Context context, String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 100);
        View view = toast.getView();

        //Gets the actual oval background of the Toast then sets the colour filter
        if (view != null) {
            view.getBackground().setColorFilter(context.getResources().getColor(R.color.colorTextDark), PorterDuff.Mode.SRC_IN);
            TextView text = view.findViewById(android.R.id.message);
            text.setTextColor(context.getResources().getColor(R.color.colorBgWhite));

            toast.show();
        }
    }

    public static void showDialogAlert(Context context, String message){
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton("OK", (dialog1, which) -> dialog1.dismiss())
                .create();
        dialog.show();
    }

    public static void showDialogRb(Context context, String user_id, ChatAdapter adapter){
        CharSequence[] grpName = context.getResources().getStringArray(R.array.notfikasi_chat_array);
        Contact contact = CoreApplication.get().getConstant().getContactDao().getContactById(user_id);
        int pos = contact.isBisukan() ? 1 : 0;

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setSingleChoiceItems(grpName, pos, (dialog1, which) -> {
                    boolean bisu = which != 0;
                    contact.setBisukan(bisu);
                    CoreApplication.get().getConstant().getContactDao().insertContact(contact);
                    adapter.notifyDataSetChanged();
                    dialog1.dismiss();
                })
                .create();
        dialog.show();
    }

    public static void showDialogAgendaRb(Context context, Agenda agenda){
        AppDb appDb = CoreApplication.get().getConstant().getAppDb();

        appDb.agendaDao().updateReminderAgenda(agenda.get_id(), !agenda.isReminder());

        AgendaScheduler.setupUpcomingAgendaNotifier(context);

        if (!agenda.isReminder()) {
            Tools.showToast(context, context.getString(R.string.pengingat_diaktifkan));
        }
    }

    public static void showDialogGender(Context context, ListenerHelper listenerHelper){
        String[] grpName = context.getResources().getStringArray(R.array.gender);

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setSingleChoiceItems(grpName, -1, (dialog1, which) -> {
                    listenerHelper.dialogYes(String.valueOf(which));
                    dialog1.dismiss();
                })
                .setCancelable(true)
                .create();
        dialog.show();
    }

    public static void showDialogAdmin(Context context, ListenerHelper listenerHelper){
        String[] grpName = {"Admin Komisariat", "Admin BPL", "Admin Alumni", "Admin Cabang", "Admin PBHMI"};
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setSingleChoiceItems(grpName, -1, (dialog1, which) -> {
                    listenerHelper.dialogYes(grpName[which]);
                    dialog1.dismiss();
                })
                .setCancelable(true)
                .create();
        dialog.show();
    }

    public static void showDialogStatus(Context context, ListenerHelper listenerHelper){
        String[] grpName = context.getResources().getStringArray(R.array.status);

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setSingleChoiceItems(grpName, -1, (dialog1, which) -> {
                    listenerHelper.dialogYes(grpName[which]);
                    dialog1.dismiss();
                })
                .setCancelable(true)
                .create();
        dialog.show();
    }

    public static void showDialogLK1(Context context, String[] list, ListenerHelper listenerHelper){
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setSingleChoiceItems(list, -1, (dialog1, which) -> {
                    listenerHelper.dialogYes(list[which]);
                    dialog1.dismiss();
                })
                .setCancelable(true)
                .create();
        dialog.show();
    }

    public interface ListenerSelect {
        void dialogSelect(String res, int index);
    }

    public static void showDialogLK1(Context context, String[] list, ListenerSelect listenerSelect){
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setSingleChoiceItems(list, -1, (dialog1, which) -> {
                    listenerSelect.dialogSelect(list[which], which);
                    dialog1.dismiss();
                })
                .setCancelable(true)
                .create();
        dialog.show();
    }

    // konfirmasi delete data dialog
    public static void deleteDialog(Context context, String message, ListenerHelper listenerHelper){
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.komfirmasi_title))
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton(context.getString(R.string.ya), (dialog, which) ->
                        listenerHelper.dialogYes(Constant.HAPUS))
                            .setNegativeButton(context.getString(R.string.tidak), (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    // konfirmasi delete data dialog
    public static void reseteDialog(Context context, ListenerHelper listenerHelper){
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.komfirmasi_title))
                .setMessage(R.string.konfirmasi_reset)
                .setCancelable(true)
                .setPositiveButton(context.getString(R.string.ya), (dialog, which) ->
                        listenerHelper.dialogYes("reset"))
                            .setNegativeButton(context.getString(R.string.tidak), (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    public static void showDialogCustom(Context context, String title, String message, String ket, ListenerHelper listenerHelper){
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton(context.getString(R.string.ya), (dialog, which) -> {
                    listenerHelper.dialogYes(ket);
                    dialog.dismiss();
                }).setNegativeButton(context.getString(R.string.tidak), (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    public static void showDialogCustom(Context context, String title, String message, String positiveButton, String ket, ListenerHelper listenerHelper){
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(positiveButton, (dialog, which) -> {
                    listenerHelper.dialogYes(ket);
                    dialog.dismiss();
                });
        builder.create().show();
    }

    // konfirmasi delete account dialog
    public static void deleteAccountDialog(Context context, ListenerHelper listenerHelper){
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.komfirmasi_title))
                .setMessage(R.string.konfirmasi_akun)
                .setCancelable(true)
                .setPositiveButton(context.getString(R.string.hapus), (dialog, which) -> {
                    listenerHelper.dialogYes(Constant.HAPUS);
                    dialog.dismiss();
                }).setNegativeButton(context.getString(R.string.batal), (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    // dialog untuk super admin dan second admin
    public static void showDialogType(Context context, ListenerHelper listenerHelper){
        String[] list = {"Cabang", "Komisariat", "Nasional"};
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setSingleChoiceItems(list, -1, (dialog1, which) -> {
                    listenerHelper.dialogYes(list[which]);
                    dialog1.dismiss();
                })
                .setCancelable(true)
                .create();
        dialog.show();
    }

    // dialog tindakan
    public static void showDialogTindakan(Context context, ListenerHelper listenerHelper) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_tindakan, null);
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(dialogView)
                .create();
        dialog.show();
        dialogView.findViewById(R.id.tv_lihat).setOnClickListener(v -> dialog.dismiss());
        dialogView.findViewById(R.id.tv_ubah).setOnClickListener(v -> {
            dialog.dismiss();
            listenerHelper.dialogYes(Constant.UBAH);
        });
        dialogView.findViewById(R.id.tv_hapus).setOnClickListener(v -> {
            dialog.dismiss();
            listenerHelper.dialogYes(Constant.HAPUS);
        });
    }

    public static void showDialogTindakan(Context context, ListenerHelper listenerHelper, boolean allowDelete) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_tindakan, null);
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(dialogView)
                .create();
        dialog.show();
        dialogView.findViewById(R.id.tv_lihat).setOnClickListener(v -> dialog.dismiss());
        dialogView.findViewById(R.id.tv_ubah).setOnClickListener(v -> {
            dialog.dismiss();
            listenerHelper.dialogYes(Constant.UBAH);
        });
        dialogView.findViewById(R.id.tv_hapus).setOnClickListener(v -> {
            dialog.dismiss();
            listenerHelper.dialogYes(Constant.HAPUS);
        });
        if (allowDelete){
            dialogView.findViewById(R.id.tv_hapus).setVisibility(View.VISIBLE);
        } else {
            dialogView.findViewById(R.id.tv_hapus).setVisibility(View.GONE);
        }
    }

    public static void confirmDelete(Context context, ListenerHelper listenerHelper) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.komfirmasi_title));
        builder.setMessage(R.string.konfirmasi);
        builder.setPositiveButton(context.getString(R.string.ya), (dialog, which) -> {
            dialog.dismiss();
            listenerHelper.dialogYes(Constant.HAPUS);
        });
        builder.setNegativeButton(context.getString(R.string.tidak), (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void showDialogNamaType(Context context, String[] list, ListenerHelper listenerHelper) {
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setSingleChoiceItems(list, -1, (dialog1, which) -> {
                    listenerHelper.dialogYes(list[which]);
                    dialog1.dismiss();
                })
                .setCancelable(true)
                .create();
        dialog.show();
    }

    public static int getType(String text){
        int angka;
        if (text.toLowerCase().contains("nasional")){
            angka = 0;
        } else if (text.toLowerCase().contains("cabang")){
            angka = 1;
        } else if (text.toLowerCase().contains("komisariat")){
            angka = 2;
        } else {
            angka = 0;
        }
        return angka;
    }

    public static String getStringType(String text){
        String nama;
        if (text.toLowerCase().contains("0")){
            nama = "Nasional";
        } else if (text.toLowerCase().contains("2")){
            nama = "Cabang";
        } else if (text.toLowerCase().contains("4")){
            nama = "Komisariat";
        } else {
            nama = "Nasional";
        }
        return nama;
    }

//    public static List<String> cabang(){
//        List<String> list = new ArrayList<>();
//        list.add("Cabang Malang");
//        list.add("Cabang Surabaya");
//        return list;
//    }
//
//    public static List<String> komisariat(){
//        List<String> list = new ArrayList<>();
//        list.add("Komisariat UB");
//        list.add("Komisariat UM");
//        return list;
//    }

//    private void showDialog(String id) {
//        LayoutInflater inflater = LayoutInflater.from(this);
//        View dialogView = inflater.inflate(R.layout.dialog_tindakan, null);
//        AlertDialog dialog = new AlertDialog.Builder(this)
//                .setView(dialogView)
//                .create();
//        dialog.show();
//        dialogView.findViewById(R.id.tv_ubah).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//                startActivityForResult(new Intent(HelpActivity.this, HelpFormActivity.class).putExtra(HelpFormActivity.IS_NEW, false).putExtra(HelpFormActivity.ID_HELP, id), Constant.REQUEST_BANTUAN);
//            }
//        });
//        dialogView.findViewById(R.id.tv_hapus).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//                confirmDelete(id);
//            }
//        });
//    }
//
//
//    private void deleteData(String id) {
////        Tools.showProgressDialog(this, "Menghapus...");
//    }

    public static boolean isNonLK(){
        return Constant.getIdRoles().equals(Constant.NON_LK);
    }

    public static boolean isLK(){
        return Constant.getIdRoles().equals(Constant.LK_1);
    }

    public static boolean isAdmin1(){
        return Constant.getIdRoles().equals(Constant.ADMIN_1);
    }

    public static boolean isAdmin2(){
        return Constant.getIdRoles().equals(Constant.ADMIN_2);
    }

    public static boolean isAdmin3(){
        return Constant.getIdRoles().equals(Constant.ADMIN_3);
    }

    public static boolean isLA1(){
        return Constant.getIdRoles().equals(Constant.LOW_ADMIN_1);
    }

    public static boolean isLA2(){
        return Constant.getIdRoles().equals(Constant.LOW_ADMIN_2);
    }

    public static boolean isSecondAdmin(){
        return Constant.getIdRoles().equals(Constant.SECOND_ADMIN);
    }

    public static boolean isSuperAdmin(){
        return Constant.getIdRoles().equals(Constant.SUPER_ADMIN);
    }

    public static void visibilityFab(FloatingActionButton fab){
        if (isSuperAdmin() || isSecondAdmin()){
            fab.setVisibility(View.VISIBLE);
        } else {
            fab.setVisibility(View.GONE);
        }
    }

    public static void initial(ImageView imageView, String initial){
        String first = String.valueOf(initial.charAt(0));
        ColorGenerator generator = ColorGenerator.MATERIAL;
        int color = generator.getColor(first);
        TextDrawable drawable = TextDrawable.builder()
                .buildRound(first, color);
        imageView.setImageDrawable(drawable);
    }

    public static void showDateDialog(Activity activity, EditText editText){
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        Calendar newCalendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(activity, (view, year, monthOfYear, dayOfMonth) -> {
            Calendar newDate = Calendar.getInstance();
            newDate.set(year, monthOfYear, dayOfMonth);
            editText.setText(dateFormatter.format(newDate.getTime()));
        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    public static void setText(EditText editText, String text){
        if (text != null && !text.trim().isEmpty()){
            editText.setText(text);
        }
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

}
