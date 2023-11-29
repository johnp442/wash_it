package com.aula.wash.it.activity;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import com.aula.wash.it.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Cadastrar extends AppCompatActivity {

    private Button voltarLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        getSupportActionBar().hide();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.azul_do_wash_azul));
        }

        voltarLogin = findViewById(R.id.voltarLogin);

        voltarLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navegue para a tela de login (MainActivity) quando o botão for clicado
                Intent intent = new Intent(Cadastrar.this, MainActivity.class);
                startActivity(intent);

                // Finalize a atividade atual (CadastrarActivity)
                finish();
            }
        });
        Button buttonRegister = findViewById(R.id.buttonRegister);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Obtenha a entrada do usuário.
                TextInputEditText editTextName = findViewById(R.id.editTextName);
                TextInputEditText editTextEmail = findViewById(R.id.editTextEmail);
                TextInputEditText editTextPassword = findViewById(R.id.editTextPassword);
                TextInputEditText editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
                TextInputEditText editTextCpf = findViewById(R.id.editTextCpf);

                String nome = editTextName.getText().toString();
                String email = editTextEmail.getText().toString();
                String senha = editTextPassword.getText().toString();
                String confirmacaoSenha = editTextConfirmPassword.getText().toString();
                String cpf = editTextCpf.getText().toString();


                // Verifique se algum campo está vazio
                if (nome.isEmpty()) {
                    editTextName.setError("Nome não pode estar vazio");
                    return;
                }
                if (email.isEmpty()) {
                    editTextEmail.setError("E-mail não pode estar vazio");
                    return;
                }
                if (senha.isEmpty()) {
                    editTextPassword.setError("Senha não pode estar vazia");
                    return;
                }
                if (confirmacaoSenha.isEmpty()) {
                    editTextConfirmPassword.setError("Confirmação de senha não pode estar vazia");
                    return;
                }
                if (cpf.isEmpty()) {
                    editTextCpf.setError("CPF não pode estar vazio");
                    return;
                }
                //verifica se o cpf tem 11 digitos
                if (cpf.length() == 11){

                }else{
                    editTextCpf.setError("CPF deve conter exatamente 11 dígitos.");
                    return;
                }

                // verifica se os campos senhas são iguais

                if (senha.equals(confirmacaoSenha)) {

                } else {
                   editTextConfirmPassword.setError("Senha e confirmação de senha não correspondem. Tente novamente.");
                   return;
                }



                // Verificar se o e-mail é válido
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    editTextEmail.setError("Por favor, insira um e-mail válido.");
                    return;
                }

                // Verifique se o e-mail é válido
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(Cadastrar.this, "Por favor, insira um endereço de e-mail válido.", Toast.LENGTH_SHORT).show();
                    return;
                }
                //Crie uma caixa de diálogo de progresso
                ProgressDialog progressDialog = new ProgressDialog(Cadastrar.this);
                progressDialog.setMessage("Registrando...");
                progressDialog.show();

                //Registre o usuário com o Firebase
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, senha)
                        .addOnCompleteListener(Cadastrar.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    
                                    Log.d(TAG, "createUserWithEmail:success");
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    String uid = user.getUid();

                                    //Crie um novo documento de usuário
                                    Map<String, Object> userDoc = new HashMap<>();
                                    userDoc.put("nome", nome);
                                    userDoc.put("cpf", cpf);
                                    userDoc.put("email", email);

                                    //Adicione o usuário ao Firestore
                                    db.collection("users").document(uid).set(userDoc)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d(TAG, "DocumentSnapshot gravado com sucesso!");
                                                    progressDialog.dismiss();
                                                    Toast.makeText(Cadastrar.this, "Registrado com sucesso!", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w(TAG, "Erro ao escrever o documento", e);
                                                    progressDialog.dismiss();
                                                    Toast.makeText(Cadastrar.this, "Falha ao registrar!", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                } else {
                                    //Se o cadastro falhar, exiba uma mensagem para o usuário
                                    Log.w(TAG, "createUserWithEmail:falha", task.getException());
                                    progressDialog.dismiss();
                                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                        Toast.makeText(Cadastrar.this, "Já existe um usuário com este e-mail", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(Cadastrar.this, "A senha deve ter 6 dígitos no mínimo" , Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
            }
        });




    }

    // Oculta o teclado virtual ao clicar fora de um campo
    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
