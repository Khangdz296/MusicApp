package com.example.music;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Dòng này quan trọng nhất: Nó kết nối file Java này với file giao diện XML bạn đã vẽ
        // R.layout.fragment_home phải trùng tên với file XML của bạn
        return inflater.inflate(R.layout.fragment_home_hoang, container, false);
    }
}