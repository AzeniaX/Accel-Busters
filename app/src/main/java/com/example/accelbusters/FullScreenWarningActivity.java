package com.example.accelbusters;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
public class FullScreenWarningActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_warning);

        // 获取传递的数据
        String title = getIntent().getStringExtra("title");
        String content = getIntent().getStringExtra("content");

        // 设置标题和内容
        TextView titleView = findViewById(R.id.warning_title);
        TextView contentView = findViewById(R.id.warning_content);

        if (title != null) titleView.setText(title);
        if (content != null) contentView.setText(content);

        // 点击界面可退出（可选）
        findViewById(R.id.full_screen_root).setOnClickListener(v -> finish());
    }
}
