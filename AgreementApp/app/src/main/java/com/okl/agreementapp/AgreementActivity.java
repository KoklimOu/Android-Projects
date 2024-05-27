package com.okl.agreementapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

public class AgreementActivity extends AppCompatActivity {
    /*Here is to catch boolean values and send it to server,
    user require to check all checkBox*/
    static boolean isCheckAll = true;
    private CheckBox privacy, termOfService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agreement);
    }

    public void checkBox(View view) {
        boolean checked = ((CheckBox) view).isChecked();
      if (checked){
          privacy = findViewById(R.id.PrivacyCB);
          termOfService = findViewById(R.id.termOfServiceCB);
          privacy.isSelected();
          termOfService.isSelected();
      }
    }

    public void seePrivacy(View view) {
    }

    public void seeTermOfService(View view) {
    }
}