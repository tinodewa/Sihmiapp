package com.roma.android.sihmi.view.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.stetho.Stetho;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.roma.android.sihmi.R;
import com.roma.android.sihmi.helper.AgendaScheduler;
import com.roma.android.sihmi.helper.NotificationHelper;
import com.roma.android.sihmi.model.database.database.AppDb;
import com.roma.android.sihmi.model.database.entity.Account;
import com.roma.android.sihmi.model.database.entity.GroupChat;
import com.roma.android.sihmi.model.database.entity.Notification;
import com.roma.android.sihmi.model.database.entity.User;
import com.roma.android.sihmi.model.database.interfaceDao.GroupChatDao;
import com.roma.android.sihmi.model.database.interfaceDao.LevelDao;
import com.roma.android.sihmi.model.database.interfaceDao.UserDao;
import com.roma.android.sihmi.model.network.ApiClient;
import com.roma.android.sihmi.model.network.MasterService;
import com.roma.android.sihmi.model.response.GeneralResponse;
import com.roma.android.sihmi.model.response.UploadFileResponse;
import com.roma.android.sihmi.service.MyFirebaseMessagingService;
import com.roma.android.sihmi.utils.Constant;
import com.roma.android.sihmi.utils.Tools;
import com.roma.android.sihmi.utils.UploadFile;
import com.roma.android.sihmi.view.fragment.AgendaFragment;
import com.roma.android.sihmi.view.fragment.AlamatFragment;
import com.roma.android.sihmi.view.fragment.BantuanFragment;
import com.roma.android.sihmi.view.fragment.ChatFragment;
import com.roma.android.sihmi.view.fragment.KonstitusiFragment;
import com.roma.android.sihmi.view.fragment.LaporanFragment;
import com.roma.android.sihmi.view.fragment.LeaderFragment;
import com.roma.android.sihmi.view.fragment.LeadersFragment;
import com.roma.android.sihmi.view.fragment.MasterFragment;
import com.roma.android.sihmi.view.fragment.PengaturanFragment;
import com.roma.android.sihmi.view.fragment.PersonalFragment;
import com.roma.android.sihmi.view.fragment.ReportFragment;
import com.roma.android.sihmi.view.fragment.ReportLK1Fragment;
import com.roma.android.sihmi.view.fragment.TentangFragment;
import com.roma.android.sihmi.view.fragment.TentangKamiFragment;
import com.roma.android.sihmi.view.fragment.UserFragment;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.zelory.compressor.Compressor;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("NonConstantResourceId")
public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, DrawerLayout.DrawerListener {
    public static String CHANGE_THEME = "change_theme";

    @BindView(R.id.logout)
    LinearLayout logout;
    @BindView(R.id.btn_menu_right)
    ImageButton buttonRight;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @BindView(R.id.btn_chatting)
    Button btnChat;
    @BindView(R.id.btn_konstitusi)
    Button btnKonstitusi;
    @BindView(R.id.btn_alamat)
    Button btnAlamat;
    @BindView(R.id.btn_agenda)
    Button btnAgenda;
    @BindView(R.id.btn_leader)
    Button btnLeader;
    @BindView(R.id.btn_bantuan)
    Button btnBantuan;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.nav_view2)
    NavigationView nav_view2;

    ImageView imageView;
    ImageView ivInitial;

    User user;
    boolean doubleBackToExitPressedOnce = false;

    DatabaseReference databaseReference;
    ValueEventListener seenListener;
    String language;

    AppDb appDb;
    UserDao userDao;
    LevelDao levelDao;
    GroupChatDao groupChatDao;
    MasterService service;
    private Stack<Integer> activeMenuFragmentStack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        appDb = AppDb.getInstance(this);
        userDao = appDb.userDao();
        levelDao = appDb.levelDao();
        groupChatDao = appDb.groupChatDao();

        Stetho.initializeWithDefaults(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("SIHMI");

        user = userDao.getUser();
        language = Constant.getLanguage();
        service = ApiClient.getInstance().getApi();
        activeMenuFragmentStack = new Stack<>();

        try {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameContent, new AgendaFragment()).commit();
            if (getIntent().getBooleanExtra(CHANGE_THEME, false)) {
                getSupportFragmentManager().beginTransaction().replace(R.id.frameContent, new PengaturanFragment()).addToBackStack(null).commit();
            }

            activeMenuFragmentStack.push(R.id.btn_agenda);
        } catch (NullPointerException e) {
            kesalahanData();
        }


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.setDrawerElevation(0);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();


        setNavigationView(user);


        buttonRight.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.END));

        //noinspection deprecation
        drawerLayout.setDrawerListener(this);

        getMenuUser();
        getNotif();

        requestPermission();

        userDao.getImageLiveData(user.get_id()).observe(this, imageUrl -> {
            if (imageUrl == null || imageUrl.isEmpty()) {
                Tools.initial(ivInitial, user.getNama_depan());
                ivInitial.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.GONE);
            }
            else {
                Glide.with(MainActivity.this)
                        .load(Uri.parse(imageUrl))
                        .into(imageView);
                ivInitial.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
            }
        });

        AgendaScheduler.setupUpcomingAgendaNotifier(this);
    }

    public void setToolBar(String title) {
        Objects.requireNonNull(getSupportActionBar()).setTitle(title);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!language.equals(Constant.getLanguage())) {
            finish();
            startActivity(getIntent());
        }

        if (seenListener != null) {
            databaseReference.removeEventListener(seenListener);
            databaseReference.addValueEventListener(seenListener);
        }
    }


    public boolean requestPermission() {
        final boolean[] returned = {false};
        Dexter.withActivity(this).withPermissions(listPermission)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        returned[0] = true;
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();

                        returned[0] = false;
                    }
                })
                .withErrorListener(error -> Toast.makeText(this, "Go to Settings and Grant the permission to use this feature.", Toast.LENGTH_SHORT).show())
                .onSameThread()
                .check();
        return returned[0];
    }

    public static String[] listPermission = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private void setNavigationView(User user) {
        nav_view2.setElevation(0);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView tv_name = (TextView) headerView.findViewById(R.id.tv_name);
        String nameStr = user.getNama_depan() + " " + user.getNama_belakang();
        tv_name.setText(nameStr);
        TextView tv_admin = (TextView) headerView.findViewById(R.id.tv_admin);
        TextView tv_edit_profile = (TextView) headerView.findViewById(R.id.tv_edit_profile);
        imageView = (ImageView) headerView.findViewById(R.id.image_view);
        ivInitial = (ImageView) headerView.findViewById(R.id.iv_initial);
        FloatingActionButton fabEdit = (FloatingActionButton) headerView.findViewById(R.id.fab_edit);

        if (user.getImage() != null && !user.getImage().isEmpty() && !user.getImage().equals(" ")) {
            Glide.with(MainActivity.this)
                    .load(Uri.parse(user.getImage()))
                    .into(imageView);
            ivInitial.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
        } else {
            Tools.initial(ivInitial, user.getNama_depan());
            ivInitial.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
        }

        imageView.setOnClickListener(v -> goProfile());

        ivInitial.setOnClickListener(v -> goProfile());

        tv_name.setOnClickListener(v -> goProfile());

        tv_admin.setOnClickListener(v -> goProfile());

        tv_edit_profile.setOnClickListener(v -> goProfile());

        fabEdit.setOnClickListener(v -> UploadFile.selectImage(this));

        String idroles = "5d1f7520eb463a45b63a77ea";
        if (user.getId_roles() != null) {
            idroles = user.getId_roles();
        }
        tv_admin.setText(levelDao.getNamaLevel(idroles));


        navigationView.setNavigationItemSelectedListener(this);

        logout.setOnClickListener(v -> confirmLogout());
    }

    public void setActionBarTitle(String title) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }

    private void goProfile() {
        startActivityForResult(new Intent(MainActivity.this, ProfileActivity.class), 2000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == 2000 && resultCode == Activity.RESULT_OK) {
                setNavigationView(userDao.getUser());
            }

            if (requestCode == Constant.REQUEST_GALLERY_CODE && resultCode == Activity.RESULT_OK) {
                if (EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                    Uri imageUri = CropImage.getPickImageResultUri(this, data);
                    if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
                        }
                    }
                    UploadFile.startCropImageActivity(this, imageUri);

                } else {
                    EasyPermissions.requestPermissions(this, getString(R.string.akses_penyimpanan), Constant.READ_REQUEST_CODE, Manifest.permission.READ_EXTERNAL_STORAGE);
                }
            }

            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    File compFile = null;
                    try {
                        compFile = new Compressor(this).compressToFile(new File(Objects.requireNonNull(result.getUri().getPath())));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Uri comUri = Uri.fromFile(compFile);
                    String filePath = UploadFile.getRealPathFromURIPath(comUri, MainActivity.this);

                    if (Tools.isOnline(this)) {
                        Tools.showProgressDialog(MainActivity.this, getString(R.string.mengunggah_foto));
                        UploadFile.uploadFileToServer(Constant.IMAGE, filePath, new Callback<UploadFileResponse>() {
                            @Override
                            public void onResponse(@NonNull Call<UploadFileResponse> call, @NonNull Response<UploadFileResponse> response) {
                                Tools.dissmissProgressDialog();
                                if (response.isSuccessful()) {
                                    UploadFileResponse body = response.body();
                                    if (body != null) {
                                        if (body.getStatus().equalsIgnoreCase("ok")) {
                                            if (body.getData().size() > 0) {
                                                updatePhoto(body.getData().get(0).getUrl());
                                            }
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<UploadFileResponse> call, @NonNull Throwable t) {
                                Tools.dissmissProgressDialog();
                            }
                        });
                    } else {
                        Tools.showToast(MainActivity.this, getString(R.string.tidak_ada_internet));
                    }
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Toast.makeText(this, getString(R.string.gagal_potong) + result.getError(), Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    @OnClick({R.id.btn_chatting, R.id.btn_konstitusi, R.id.btn_alamat, R.id.btn_agenda, R.id.btn_leader, R.id.btn_bantuan})
    public void menuClick(Button button) {
        FragmentManager fm = getSupportFragmentManager();
        ActionBar actionBar = getSupportActionBar();
        switch (button.getId()) {
            case R.id.btn_chatting:
                if (activeMenuFragmentStack.peek() != R.id.btn_chatting) {
                    activeMenuFragmentStack.push(R.id.btn_chatting);
                    if (Tools.isNonLK()) {
                        if (actionBar != null) {
                            actionBar.setTitle(R.string.app_name_uppercase);
                        }
                        getSupportFragmentManager().beginTransaction().replace(R.id.frameContent, new PersonalFragment()).addToBackStack(null).commit();
                    } else {
                        getSupportFragmentManager().beginTransaction().replace(R.id.frameContent, new ChatFragment()).addToBackStack(null).commit();
                    }
                }
                break;
            case R.id.btn_konstitusi:
                if (activeMenuFragmentStack.peek() != R.id.btn_konstitusi) {
                    activeMenuFragmentStack.push(R.id.btn_konstitusi);
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameContent, new KonstitusiFragment()).addToBackStack(null).commit();
                }
                break;
            case R.id.btn_alamat:
                if (activeMenuFragmentStack.peek() != R.id.btn_alamat) {
                    activeMenuFragmentStack.push(R.id.btn_alamat);
                    if (Tools.isNonLK()) {
                        Tools.showToast(this, getString(R.string.tidak_bisa_akses));
                    } else {
                        fm.beginTransaction().replace(R.id.frameContent, new AlamatFragment()).addToBackStack(null).commit();
                    }
                }
                break;
            case R.id.btn_agenda:
                if (activeMenuFragmentStack.peek() != R.id.btn_agenda) {
                    activeMenuFragmentStack.push(R.id.btn_agenda);
                    fm.beginTransaction().replace(R.id.frameContent, new AgendaFragment()).addToBackStack(null).commit();
                }
                break;
            case R.id.btn_leader:
                if (activeMenuFragmentStack.peek() != R.id.btn_leader) {
                    activeMenuFragmentStack.push(R.id.btn_leader);
                    if (Tools.isNonLK()) {
                        if (actionBar != null) {
                            actionBar.setTitle(getString(R.string.judul_ketua_umum));
                        }
                        fm.beginTransaction().replace(R.id.frameContent, new LeaderFragment(1)).addToBackStack(null).commit();
                    } else {
                        fm.beginTransaction().replace(R.id.frameContent, new LeadersFragment()).addToBackStack(null).commit();
                    }
                }
                break;
            case R.id.btn_bantuan:
                if (activeMenuFragmentStack.peek() != R.id.btn_bantuan) {
                    activeMenuFragmentStack.push(R.id.btn_bantuan);
                    fm.beginTransaction().replace(R.id.frameContent, new BantuanFragment()).addToBackStack(null).commit();
                }
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.END);
    }

    public void replaceFragment(Fragment fragment, Bundle arguments) {
        activeMenuFragmentStack.push(0);
        fragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction().replace(R.id.frameContent, fragment).addToBackStack(null).commit();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        FragmentManager fm = getSupportFragmentManager();

        switch (id) {
            case R.id.nav_laporan:
                if (activeMenuFragmentStack.peek() != R.id.nav_laporan) {
                    activeMenuFragmentStack.push(R.id.nav_laporan);
                    if (Tools.isSuperAdmin()) {
                        fm.beginTransaction().replace(R.id.frameContent, new LaporanFragment()).addToBackStack(null).commit();
                    }
                    else if (Tools.isLK()) {
                        fm.beginTransaction().replace(R.id.frameContent,
                                ReportLK1Fragment.newInstance()).addToBackStack(null).commit();
                    } else {
                        fm.beginTransaction().replace(R.id.frameContent, new ReportFragment()).addToBackStack(null).commit();
                    }
                }
                break;
            case R.id.nav_user:
                if (activeMenuFragmentStack.peek() != R.id.nav_user) {
                    activeMenuFragmentStack.push(R.id.nav_user);
                    fm.beginTransaction().replace(R.id.frameContent, new UserFragment()).addToBackStack(null).commit();
                }
                break;
            case R.id.nav_master:
                if (activeMenuFragmentStack.peek() != R.id.nav_master) {
                    activeMenuFragmentStack.push(R.id.nav_master);
                    fm.beginTransaction().replace(R.id.frameContent, new MasterFragment()).addToBackStack(null).commit();
                }
                break;
            case R.id.nav_data:
                if (activeMenuFragmentStack.peek() != R.id.nav_data) {
                    activeMenuFragmentStack.push(R.id.nav_data);
                    Toast.makeText(this, "Data", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.nav_pengaturan:
                if (activeMenuFragmentStack.peek() != R.id.nav_pengaturan) {
                    activeMenuFragmentStack.push(R.id.nav_pengaturan);
                    fm.beginTransaction().replace(R.id.frameContent, new PengaturanFragment()).addToBackStack(null).commit();
                }
                break;
            case R.id.nav_info:
                if (activeMenuFragmentStack.peek() != R.id.nav_info) {
                    activeMenuFragmentStack.push(R.id.nav_info);
                    if (Tools.isNonLK()) {
                        fm.beginTransaction().replace(R.id.frameContent, new TentangKamiFragment(1)).addToBackStack(null).commit();
                    } else {
                        fm.beginTransaction().replace(R.id.frameContent, new TentangFragment()).addToBackStack(null).commit();
                    }
                }
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack(getSupportFragmentManager()
                    .getBackStackEntryAt(0).getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE );
            setActionBarTitle(getString(R.string.app_name));
            activeMenuFragmentStack.clear();
            activeMenuFragmentStack.add(R.id.btn_agenda);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            Tools.showToast(this, getString(R.string.klik2x));

            new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
        }
    }

    public void backToPreviousFragment() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            activeMenuFragmentStack.pop();
        }
    }

    SearchView searchView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem gridItem = menu.findItem(R.id.action_list);
        gridItem.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            searchView = (SearchView) item.getActionView();
            searchView.setQueryHint(getString(R.string.cari));
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    Toast.makeText(MainActivity.this, query, Toast.LENGTH_SHORT).show();
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDrawerSlide(@NonNull View view, float v) {

    }

    @Override
    public void onDrawerOpened(@NonNull View view) {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            buttonRight.setVisibility(View.VISIBLE);
        } else {
            if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                buttonRight.setVisibility(View.INVISIBLE);
            } else {
                buttonRight.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onDrawerClosed(@NonNull View view) {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            buttonRight.setVisibility(View.VISIBLE);
        } else {
            if (!drawerLayout.isDrawerOpen(GravityCompat.END)) {
                buttonRight.setVisibility(View.VISIBLE);
            } else {
                buttonRight.setVisibility(View.INVISIBLE);
            }
        }

    }

    @Override
    public void onDrawerStateChanged(int i) {

    }

    private void getMenuUser() {
        if (Tools.isNonLK()) {
            navigationView.getMenu().findItem(R.id.nav_user).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_master).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_laporan).setVisible(false);
        } else if (Tools.isLK()) {
            navigationView.getMenu().findItem(R.id.nav_user).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_master).setVisible(false);
        } else if (Tools.isAdmin1() || Tools.isAdmin2() || Tools.isLA1() || Tools.isLA2()) {
            navigationView.getMenu().findItem(R.id.nav_master).setVisible(false);
        } else if (Tools.isAdmin3()) {
            navigationView.getMenu().findItem(R.id.nav_user).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_master).setVisible(false);
        }
    }

    private void logout() {
        ApiClient.getInstance().getApi().logout(Constant.getToken())
                .enqueue(new Callback<GeneralResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<GeneralResponse> call, @NonNull Response<GeneralResponse> response) {
                        if (response.isSuccessful()) {
                            GeneralResponse body = response.body();
                            if (body != null) {
                                if (body.getStatus().equalsIgnoreCase("success")) {
                                    Toast.makeText(MainActivity.this, getString(R.string.logout_berhasil), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        Constant.logout();
                        clearData();
                    }

                    @Override
                    public void onFailure(@NonNull Call<GeneralResponse> call, @NonNull Throwable t) {
                        Constant.logout();
                        clearData();
                    }
                });
    }

    private void confirmLogout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.komfirmasi_title));
        builder.setMessage(R.string.konfirmasi_logout);
        builder.setPositiveButton(getString(R.string.ya), (dialog, which) -> logout());
        builder.setNegativeButton(getString(R.string.tidak), (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void kesalahanData() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.peringatan));
        builder.setMessage(getString(R.string.kesalahan_data));
        builder.setCancelable(false);
        builder.setPositiveButton("Oke", (dialog, which) -> {
            startActivity(new Intent(MainActivity.this, SwitchAccountActivity.class));
            finish();
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void unsubscribeFromGroupChat() {
        List<GroupChat> groupChats;
        User user = userDao.getUser();
        if (user != null) {
            if (Tools.isSuperAdmin()) {
                groupChats = groupChatDao.getAllGroupList();
            }
            else {
                groupChats = groupChatDao.getAllGroupListNotSuperAdmin(user.getCabang(), user.getKomisariat(), user.getDomisili_cabang());
            }

            for(GroupChat groupChat : groupChats) {
                String[] groupNameSplit = groupChat.getNama().split(" ");
                String groupName = TextUtils.join("_", groupNameSplit);
                final String topic = MyFirebaseMessagingService.GROUP_TOPIC_PREFIX + groupName;
                FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
                        .addOnCompleteListener(task -> Log.d("Fabric", "Successfully unsubscribed from topic " + topic));
            }
        }
    }

    private void unsubscribeFromAgenda() {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(MyFirebaseMessagingService.AGENDA_TOPIC)
                .addOnCompleteListener(task -> Log.d("Fabric", "Successfully unsubscribed from topic " + MyFirebaseMessagingService.AGENDA_TOPIC));
    }

    private void clearData() {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

        NotificationHelper.removeAllHistoryMessage();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        reference.child(userDao.getUser().get_id()).removeValue();

        unsubscribeFromGroupChat();
        unsubscribeFromAgenda();

        AgendaScheduler.cancelAgenda(this);

        updatePhoto();

        Constant.logout();
        new Handler().postDelayed(() -> appDb.clearAllTables(), 1000);
        if (Constant.getSizeAccount() > 0) {
            startActivity(new Intent(MainActivity.this, SwitchAccountActivity.class));
        } else {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
        finish();
    }

    private void updatePhoto() {
        List<Account> list = Constant.getLisAccount();
        if (akunAvailable(list)) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getUsername().equalsIgnoreCase(userDao.getUser().getUsername())) {
                    list.get(i).setImage(userDao.getUser().getImage());
                }
            }
        }

        Constant.setListAccount(list);
    }


    private boolean akunAvailable(List<Account> list) {
        boolean available = false;
        int size = list.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                if (list.get(i).getUsername().equalsIgnoreCase(userDao.getUser().getUsername())) {
                    available = true;
                }
            }
        }
        return available;
    }


    private void getNotif() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Notification");
        seenListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e("roma", "onDataChange: mainactivity 762");
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Notification notification = snapshot.getValue(Notification.class);
                    if (notification != null) {
                        if (notification.getTo().equals(user.get_id())) {
                            if (!notification.isIsshow()) {
                                int level = levelDao.getPengajuanLevel(user.getId_roles());
                                String dialogTitle = "";
                                String dialogDesc = "";
                                String dialogPositive = "";

                                switch (notification.getStatus()) {
                                    case "1":
                                        dialogTitle = getString(R.string.approve_admin_title);
                                        dialogDesc = getString(R.string.approve_admin_desc);
                                        dialogPositive = getString(R.string.bismillah);
                                        break;
                                    case "2":
                                        dialogTitle = getString(R.string.admin_berakhir);
                                        dialogDesc = getString(R.string.admin_berakhir_desc);
                                        dialogPositive = getString(R.string.alhamdulillah);
                                        break;
                                    case "3":
                                        dialogTitle = getString(R.string.anggota_berakhir);
                                        dialogDesc = getString(R.string.anggota_berakhir_desc);
                                        dialogPositive = getString(R.string.alhamdulillah);
                                        break;
                                    case "4":
                                        // Approve LK 1
                                        dialogTitle = getString(R.string.selamat_berproses);
                                        dialogDesc = getString(R.string.selamat_berproses_desc);
                                        dialogPositive = getString(R.string.yakusa);
                                        break;
                                    case "-1":
                                        dialogTitle = getString(R.string.pengajuan_ditolak);
                                        dialogDesc = getString(R.string.pengajuan_ditolak_desc);
                                        dialogPositive = getString(R.string.tutup);
                                        break;
                                    default:
                                        break;
                                }

                                Tools.showDialogCustom(MainActivity.this, dialogTitle, dialogDesc, dialogPositive, getString(R.string.ya), ket -> {
                                    if (level != notification.getNewLevel()) {
                                        logout();
                                    }
                                });

                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("isshow", true);
                                snapshot.getRef().updateChildren(hashMap);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updatePhoto(String url) {
        if (Tools.isOnline(this)) {
            Tools.showProgressDialog(MainActivity.this, getString(R.string.mengganti_foto_profile));

            User user = userDao.getUser();

            ApiClient.getInstance().getApi().updateProfile(Constant.getToken(), user.getBadko(), user.getCabang(), user.getKorkom(),
                    user.getKomisariat(), user.getId_roles(), url, user.getNama_depan(), user.getNama_belakang(),
                    user.getNama_panggilan(), user.getJenis_kelamin(), user.getNomor_hp(), user.getAlamat(), user.getUsername(),
                    user.getTempat_lahir(), user.getTanggal_lahir(), user.getStatus_perkawinan(), "", user.getEmail(),
                    user.getAkun_sosmed(), user.getDomisili_cabang(), user.getPekerjaan(), user.getJabatan(), user.getAlamat_kerja(), user.getKontribusi())
                    .enqueue(new Callback<GeneralResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<GeneralResponse> call, @NonNull Response<GeneralResponse> response) {
                            Tools.dissmissProgressDialog();
                            if (response.isSuccessful()) {
                                GeneralResponse body = response.body();
                                if (body != null) {
                                    if (body.getStatus().equalsIgnoreCase("ok")) {
                                        userDao.updatePhoto(user.get_id(), url);
                                        Glide.with(MainActivity.this)
                                                .load(Uri.parse(url))
                                                .into(imageView);
                                        setResult(Activity.RESULT_OK);

                                    } else {
                                        Toast.makeText(MainActivity.this, "" + body.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } else {
                                Toast.makeText(MainActivity.this, "" + response.message(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<GeneralResponse> call, @NonNull Throwable t) {
                            Tools.dissmissProgressDialog();
                            Toast.makeText(MainActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, getString(R.string.tidak_ada_internet), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        databaseReference.removeEventListener(seenListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseReference.removeEventListener(seenListener);
    }

}
