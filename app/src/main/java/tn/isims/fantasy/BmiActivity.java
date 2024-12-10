package tn.isims.fantasy;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import tn.isims.fantasy.databinding.ActivityBmiBinding;
import tn.isims.fantasy.databinding.ActivityEditProfileBinding;

public class BmiActivity extends AppCompatActivity {


    private EditText hghtEdt, wghtEdt;
    private TextView rstTxv, dspTxv, ctgrTxv;
    private Button calcBtn, rstBtn;

    private ActivityBmiBinding binding;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi);

        binding = ActivityBmiBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backIcon.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());


        hghtEdt = (EditText)  findViewById(R.id.heightEditTextNumberSigned);
        wghtEdt = (EditText)  findViewById(R.id.weightEditTextNumberSigned);

        dspTxv = (TextView)  findViewById(R.id.displayTextView);
        ctgrTxv = (TextView) findViewById(R.id.categoryTextView);

        calcBtn =(Button)  findViewById(R.id.calculateButton);
        rstBtn = (Button)  findViewById(R.id.resetButton);

        calcBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick (View view) {
                float Height = Float.parseFloat(String.valueOf(hghtEdt.getText()))/100;
                float Weight = Float.parseFloat(String.valueOf(wghtEdt.getText()));

                float BMI = Weight/ (Height * Height);
                dspTxv.setText(String.format("%.2f", BMI));


                String category;
                if (BMI < 18.5) {
                    category = "Underweight";

                } else if (BMI < 25) {
                    category = "Normal Weight";

                } else if (BMI < 30) {
                    category = "Overweight";

                } else {
                    category = "obese";
                }

                ctgrTxv.setText(category);
            }
        });

        rstBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                hghtEdt.setText("");
                wghtEdt.setText("");
                dspTxv.setText("");
                ctgrTxv.setText("");
            }
        });
    }
}