package com.example.dk.onthidh;

        import android.content.Intent;
        import android.os.Bundle;
        import android.support.v7.app.AppCompatActivity;
        import android.view.View;
        import android.widget.Button;

public class ChooseActivity extends AppCompatActivity {
    String uid;
    private Button btnmath,btnenglish,btnbiology,btnchemistry,btnphysic,btnedu,btnhistoty,btngeography;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);
        uid = getIntent().getExtras().getString("Uid");
        anhxa();
        Click();
    }
    private  void anhxa(){
        btnenglish = (Button) findViewById(R.id.btn_english);
    }
    public void Click(){
        btnenglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ChooseActivity.this, MainActivity.class).putExtra("Uid",uid));
            }
        });
    }
}
