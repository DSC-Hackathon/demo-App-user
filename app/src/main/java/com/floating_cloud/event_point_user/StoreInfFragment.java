package com.floating_cloud.event_point_user;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;


public class StoreInfFragment extends Fragment {
    private ApiClient apiClient;
    TextView editText1,editText2,txt2;
    RadioButton radioButton1,radioButton2;
    Button btn1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_store_inf,container, false);
        editText1 = view.findViewById(R.id.sName2);
        editText2 = view.findViewById(R.id.inf2);
        txt2 = view.findViewById(R.id.stype2);
        btn1 = view.findViewById(R.id.button);
        apiClient = new ApiClient();
        Bundle bundle = getArguments();
        if(bundle!=null){
            editText1.setText(bundle.getString("key1"));
            txt2.setText(bundle.getString("key2"));
            editText2.setText(bundle.getString("key3"));
        }
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showStoreInfoDialog(bundle.getString("key1"));
            }
        });

        return view;
    }
    public void close(){
        // 현재의 Fragment를 참조
        Fragment currentFragment = getParentFragmentManager().findFragmentById(R.id.main_frame);


        Fragment newFragment = new MapFragment();  // 새로운 프래그먼트 객체 생성

        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();


        if (currentFragment != null) {
            transaction.remove(currentFragment);
        }

        transaction.add(R.id.main_frame, newFragment);

        transaction.commit();
    }

    private void showStoreInfoDialog(String a) {
        // Custom Dialog Layout을 inflate
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_complane, null);

        EditText storeNameInput = dialogView.findViewById(R.id.editTextStoreName);
        EditText storeDescription = dialogView.findViewById(R.id.editTextStoreDescription);
        Button yesButton = dialogView.findViewById(R.id.yesButton);
        Button noButton = dialogView.findViewById(R.id.noButton);

        // 초기에는 확인 버튼을 비활성화
        yesButton.setEnabled(false);

        // TextWatcher를 사용하여 EditText의 내용이 변경될 때마다 호출
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 이름 입력과 라디오 버튼 선택 여부를 체크하여 확인 버튼 활성화 여부 결정
                yesButton.setEnabled(!storeNameInput.getText().toString().trim().isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        // EditText와 RadioGroup에 TextWatcher와 리스너를 등록
        storeNameInput.addTextChangedListener(textWatcher);

        // 다이얼로그 생성
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create();

        // 확인 버튼 클릭 리스너
        yesButton.setOnClickListener(v -> {
            // 사용자 입력 정보 가져오기
            String storeName = storeNameInput.getText().toString();
            String storedescription = storeDescription.getText().toString();
            apiClient.sendAnnouncement(a+" "+storedescription);

            // 다이얼로그 닫기
            dialog.dismiss();

        });

        // 취소 버튼 클릭 리스너
        noButton.setOnClickListener(v -> dialog.dismiss());

        // 다이얼로그 표시
        dialog.show();
    }

}

