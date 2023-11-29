package com.aula.wash.it.activity;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aula.wash.it.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 2;
    private Button telaCadastro;
    private Button btnLogin;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Verifique se a sessão do usuário está salva no SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "");
        String senha = sharedPreferences.getString("senha", "");

        if (!email.isEmpty() && !senha.isEmpty()) {
            // Navegue até a 'TelaCentral' se a sessão do usuário estiver salva
            Intent intent = new Intent(MainActivity.this, TelaCentral.class);
            startActivity(intent);
            finish();
            return;
        }


        CheckBox lembrarSenha = findViewById(R.id.LembrarSenha);

        lembrarSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Verifique se CheckBox está marcada
                if (lembrarSenha.isChecked()) {
                    // Obtenha o e-mail e a senha do usuário
                    String email = ((EditText) findViewById(R.id.txtLogin)).getText().toString();
                    String senha = ((EditText) findViewById(R.id.txtSenha)).getText().toString();

                    // Salve o e-mail e a senha do usuário no SharedPreferences
                    SharedPreferences sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("email", email);
                    editor.putString("senha", senha);
                    editor.apply();


                } else {
                    // Limpe o e-mail e a senha do usuário do SharedPreferences
                    SharedPreferences sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.apply();


                }
            }
        });



        TextView esqueceuSenha= findViewById(R.id.EsqueceuSenha);
        esqueceuSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Obtenha o e-mail do seu EditText
                String email = ((EditText) findViewById(R.id.txtLogin)).getText().toString().trim();

                // Verifique se o campo de e-mail está vazio
                if (email.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Por favor, insira seu endereço de e-mail", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Chame a função de redefinição de senha do Firebase
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "E-mail enviado");
                                    Toast.makeText(MainActivity.this, "Link de redefinição enviado para o seu e-mail", Toast.LENGTH_SHORT).show();
                                } else {
                                    Log.d(TAG, "Falha ao enviar o e-mail de redefinição");
                                    Toast.makeText(MainActivity.this, "Falha ao enviar e-mail de redefinição!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });



        Button btnGoogle = findViewById(R.id.btnGoogle);
        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle();
            }
        });

        getSupportActionBar().hide();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.azul_do_wash_azul));
        }

        // Referências
        telaCadastro = findViewById(R.id.btnCadastrar);
        btnLogin = findViewById(R.id.btnLogin);

        // Evento de clique para o botão de cadastro
        telaCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navegue para a tela de cadastro quando o botão for clicado
                Intent intent = new Intent(MainActivity.this, Cadastrar.class);
                startActivity(intent);
            }
        });

        // Evento de clique para o botão de login
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Obtenha a entrada do usuário
                String email = ((EditText) findViewById(R.id.txtLogin)).getText().toString().trim();
                String senha = ((EditText) findViewById(R.id.txtSenha)).getText().toString().trim();

                //Verifique se o campo de e-mail ou senha está vazio
                if (email.isEmpty() || senha.isEmpty()) {
                    Toast.makeText(MainActivity.this, "E-mail ou senha não podem estar vazio", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Mostre um ProgressDialog aqui para indicar o processo de login
                ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setMessage("Entrando...");
                progressDialog.show();

                // Autentique o usuário com o Firebase
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, senha)
                        .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // Dispense o ProgressDialog
                                progressDialog.dismiss();

                                if (task.isSuccessful()) {
                                    // Login realizado com sucesso, atualize a interface do usuário com as informações do usuário autenticado
                                    Log.d(TAG, "signInWithEmail:success");
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    String uid = user.getUid();  //Este é o ID único do usuário

                                    //Navegue para a próxima atividade se o login for bem-sucedido
                                    Intent intent = new Intent(MainActivity.this, TelaCentral.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // Se o login falhar, exiba uma mensagem para o usuário
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(MainActivity.this, "Verifique se o E-mail e Senha estão corretos.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });



    }

    private void signInWithGoogle() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.w(TAG, "Login com o Google falhou", e);
            }
        }

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            saveUserToFirebaseIfNotFound(user);
                            startActivity(new Intent(MainActivity.this, TelaCentral.class));
                            Toast.makeText(MainActivity.this, "Login bem-sucedido!", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Login falhou:" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    private void saveUserToFirebaseIfNotFound(FirebaseUser user) {
        DocumentReference docRef = db.collection("users").document(user.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (!document.exists()) {
                        Map<String, Object> newUser = new HashMap<>();
                        newUser.put("nome", user.getDisplayName());
                        newUser.put("email", user.getEmail());
                        docRef.set(newUser);
                    }
                } else {
                    Log.d(TAG, "Obtenção falhou com", task.getException());
                }
            }
        });
    }

    public void resetPassword(View view) {
        // Obtenha o e-mail do usuário
        String email = ((EditText) findViewById(R.id.txtLogin)).getText().toString().trim();

        // Verifique se o campo de e-mail está vazio
        if (email.isEmpty()) {
            Toast.makeText(MainActivity.this, "Nenhum e-mail encontrado. Por favor, insira-o", Toast.LENGTH_SHORT).show();
            return;
        }

        // Envie um e-mail de redefinição de senha
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //Mostre uma mensagem ao usuário
                            Toast.makeText(MainActivity.this, "Email de redefinição de senha enviado",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            //Se a tarefa falhar, exiba uma mensagem para o usuário
                            Log.w(TAG, "sendPasswordResetEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Falha ao enviar e-mail de redefinição de senha",
                                    Toast.LENGTH_SHORT).show();
                        }
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
