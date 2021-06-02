package com.roma.android.sihmi.view.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.roma.android.sihmi.R;
import com.roma.android.sihmi.model.database.entity.Account;
import com.roma.android.sihmi.model.network.ApiClient;
import com.roma.android.sihmi.model.network.MasterService;
import com.roma.android.sihmi.utils.Constant;
import com.roma.android.sihmi.utils.LoginProcess;
import com.roma.android.sihmi.utils.Tools;
import com.roma.android.sihmi.view.adapter.AkunAdapter;

import java.util.Iterator;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressLint("NonConstantResourceId")
public class SwitchAccountActivity extends BaseActivity {
    @BindView(R.id.btn_alih_akun)
    Button btnAlihAkun;
    @BindView(R.id.btn_daftar)
    Button btnDaftar;
    @BindView(R.id.rv_akun)
    RecyclerView rvAkun;

    AkunAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch_account);
        ButterKnife.bind(this);
        initAdapter();
    }

    @OnClick({R.id.btn_alih_akun, R.id.btn_daftar})
    public void click(Button button){
        int id = button.getId();
        switch (id){
            case R.id.btn_alih_akun:
                startActivity(new Intent(SwitchAccountActivity.this, LoginActivity.class));
                break;
            case R.id.btn_daftar:
                startActivity(new Intent(SwitchAccountActivity.this, RegisterActivity.class));
                break;
        }
    }

    private void initAdapter(){
        adapter = new AkunAdapter(this, Constant.getLisAccount(), (type, account) -> {
            if (type.equalsIgnoreCase(Constant.MASUK)){
                MasterService service = ApiClient.getInstance().getApi();
                LoginProcess loginProcess = new LoginProcess(this, service);
                loginProcess.login(account.getUsername(), account.getPassword());
            } else {
                Tools.deleteAccountDialog(this, ket -> {
                    if (ket.equals(Constant.HAPUS)) {
                        delete(account);
                    }
                });
            }
        });
        rvAkun.setLayoutManager(new LinearLayoutManager(this));
        rvAkun.setHasFixedSize(true);
        rvAkun.setAdapter(adapter);
    }

    private void delete(Account account){
        Log.d("halloo", "delete: ");
        List<Account> list = Constant.getListAccount();
        Iterator<Account> iterator = list.iterator();
        while (iterator.hasNext()){
            Account account1 = iterator.next();
            if (account1.getUsername().equalsIgnoreCase(account.getUsername())){
                iterator.remove();
            }
        }
        Constant.setListAccount(list);
        adapter.updateData(list);
    }
}
