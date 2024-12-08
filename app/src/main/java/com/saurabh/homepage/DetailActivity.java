package com.saurabh.homepage;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {
    // Create a list of DiseaseInformation
    List<DiseaseInformation> diseaseList = new ArrayList<>();
    private String classId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);
        classId = getIntent().getStringExtra("Class");
        initData();
        initView();
    }
    private void initView(){
        DiseaseInformation a = null;
        if(classId != null){
            if(classId.equals("0")){
                a = diseaseList.get(0);
            }else if(classId.equals("1")){
                a = diseaseList.get(2);
            }else{
                a = diseaseList.get(1);
            }
        }else{
            finish();
        }
        if(a != null){
            CollapsingToolbarLayout view1 =  findViewById(R.id.collapsingToolbarLayout);
            view1.setTitle(a.name);
            TextView textView1 = findViewById(R.id.textview1);
            TextView textView2 = findViewById(R.id.textview2);
            TextView textView3 = findViewById(R.id.textview3);
            textView1.setText(a.conditional);
            textView2.setText(a.prevent);
            textView3.setText(a.curve);
            ImageView imageView = findViewById(R.id.imageViewg);
            imageView.setImageResource(a.image);

        }
    }
    private void initData(){
        diseaseList.add(new DiseaseInformation(
                "Bệnh thối bẹ",
                "Thường phát sinh vào mùa nắng, nhiệt độ cao, nắng gay gắt",
                "- Chọn giống sạch bệnh.\n- Cắt tỉa những cành bị bệnh, xử lý vết cắt bằng nước Clo.\n- Quản lý nước tốt (thoát hết nước mùa mưa, bảo đảm đủ ẩm mùa khô).\n- Không tưới nước lên cây vào buổi trưa hoặc lúc trời nắng nóng\n- Không bón dư đạm",
                "- Dùng một trong các loại thuốc sau: Azoxystrobin, Metalaxyl hay các hỗn hợp (Mandipropamid + Chlorothalonil)…\n- Có thể sử dụng các loại thuốc trừ bệnh gốc đồng như Cuprous Oxide; một số thuốc trừ nấm bệnh gốc Difernoconazole hoặc Azoxitrobin + Difernoconazole (UPPER 400SC); phun 1-2 lần cho đến khi vết bệnh khô",
                R.drawable.benh_thoi_be_thanh_long_fusarium_sp
        ));
        diseaseList.add(new DiseaseInformation(
                "Bệnh thán thư",
                "Thường xảy ra trong điều kiện thời tiết mưa nhiều, sương mù, vườn rậm rạp, ẩm độ và nhiệt độ không khí và đất cao",
                "- Tỉa cành cho cây thông thoáng, loại bỏ cành bị sâu bệnh, không cho cành tiếp xúc với đất.\n- Tiêu hủy các cành bị bệnh nặng.",
                "Phun thuốc phòng bệnh (Nustar, Antracol, Anvil) khi điều kiện thời tiết thuận lợi cho bệnh phát triển (mưa nhiều)."
                ,R.drawable._thanthu
        ));

        diseaseList.add(new DiseaseInformation(
                "Bệnh nám cành",
                "Thường xảy ra vào mùa nắng, nóng",
                "- Vệ sinh ruộng trồng. Cắt tỉa cành bệnh.\n- Chống úng và chống hạn cho cây.\n- Bón phân cân đối N-P-K",
                "Phun thuốc có hoạt chất Hexaconazole, Azoxystrobin, Metalaxyl hay các hỗn hợp (Mandipropamid + Chlorothalonil)"
                ,R.drawable.nam_canhg
        ));



    }
}
