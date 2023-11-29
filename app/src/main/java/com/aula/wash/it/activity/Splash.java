package com.aula.wash.it.activity;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.aula.wash.it.R;

import java.util.Objects;


public class Splash extends AppCompatActivity {

    private static final long SPLASH_TIMEOUT = 2000; // Tempo de exibição do splash em milissegundos
    private static final long ANIMATION_DURATION = 1000; // Duração da animação em milissegundos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));

        setContentView(R.layout.activity_splash);

        // Encontrar a ImageView que contém a imagem de splash
        ImageView imageView = findViewById(R.id.splash_icon); // Usando o ID "splash_icon"

        // Configurar a animação de escala
        Animation animation = new ScaleAnimation(
                0.5f, 1.0f, // Escala inicial e final em X
                0.5f, 1.0f, // Escala inicial e final em Y
                Animation.RELATIVE_TO_SELF, 0.5f, // Ponto central em X
                Animation.RELATIVE_TO_SELF, 0.5f  // Ponto central em Y
        );
        animation.setDuration(ANIMATION_DURATION);
        imageView.startAnimation(animation);

        // Configura um temporizador para redirecionar para a MainActivity após o tempo de exibição do splash
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Splash.this, MainActivity.class);
                startActivity(intent);
                finish(); // Encerra a atividade da tela de splash para que o usuário não possa voltar para ela
            }
        }, SPLASH_TIMEOUT + ANIMATION_DURATION); // Adiciona a duração da animação ao tempo de exibição
    }
}
