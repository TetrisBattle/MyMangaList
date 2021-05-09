package com.tetrisbattle.mymangalist;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;

public class SettingsActivity extends AppCompatActivity {

    CheckBox publich;
    RadioGroup publishMode;

    Animation animationPublish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        publich = findViewById(R.id.publish);
        publishMode = findViewById(R.id.publishMode);

        animationPublish = AnimationUtils.loadAnimation(this, R.anim.animation_publish);
        publishMode.setAnimation(animationPublish);

        publich.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                publishMode.setVisibility(View.VISIBLE);
                publishMode.startAnimation(animationPublish);
            } else {
                publishMode.setVisibility(View.GONE);
            }
        });
    }
}