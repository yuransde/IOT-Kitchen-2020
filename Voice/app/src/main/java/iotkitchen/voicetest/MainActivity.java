package iotkitchen.voicetest;

/* Default imports */
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/* Imports for text to speech */
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.util.Locale;

/* Import for recipe class */


public class MainActivity extends AppCompatActivity {

    TextToSpeech t1;
    Button start;
    Button next;
    Button repeat;
    Button ingredients;
    int instructionCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        /* Default create */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instructionCount = 0;

        /* Recipe Creation for Test Recipe */
        String title = "Peanut Butter Cups";
        String detail = "Makes six servings";
        String[] ingredient = new String[3];
        final String[] instruction = new String[7];
        ingredient[0] = "3 tablespoons powdered sugar, sifted";
        ingredient[1] = "1 half cup creamy peanut butter";
        ingredient[2] = "1 cup chocolate, melted";
        instruction[0] = "Prepare a cupcake tin with 6 liners.";
        instruction[1] = "Stir peanut butter and powdered sugar together until smooth.";
        instruction[2] = "Spread 1 to 2 tablespoons of chocolate in the bottom of each cupcake liner.";
        instruction[3] = "Dollop 1 to 2 teaspoons of the peanut butter mixture on top of the chocolate.";
        instruction[4] = "Cover each dollop of peanut butter with more chocolate and smooth out the top.";
        instruction[5] = "Refrigerate for 1 hour or until chocolate has hardened.";
        instruction[6] = "Remove peanut butter cups from the liners and enjoy!";

        final Recipe REESES = new Recipe(title, detail, ingredient, instruction);

        /* Text to speech */
        start = (Button)findViewById(R.id.startButton);
        ingredients = (Button)findViewById(R.id.ingredientButton);
        next = (Button)findViewById(R.id.nextButton);
        repeat = (Button)findViewById(R.id.repeatButton);

        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.US);
                }
            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toSpeak1 = REESES.getTitle();
                String toSpeak2 = REESES.getDetail();
                t1.speak(toSpeak1, TextToSpeech.QUEUE_ADD, null);
                t1.speak(toSpeak2, TextToSpeech.QUEUE_ADD, null);
            }
        });

        ingredients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < 3; i++)
                {
                    String toSpeak = REESES.getIngredient(i);
                    t1.speak(toSpeak, TextToSpeech.QUEUE_ADD, null);
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toSpeak;
                if (instructionCount < 7)
                {
                    toSpeak = REESES.getInstruction(instructionCount);
                }
                else
                {
                    toSpeak = "You are done.";
                }

                t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                Toast.makeText(getApplicationContext(), toSpeak, Toast.LENGTH_SHORT).show();
                instructionCount++;
            }
        });

        repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toSpeak;
                if (instructionCount <= 7)
                {
                    toSpeak = REESES.getInstruction(instructionCount-1);
                }
                else
                {
                    toSpeak = "You are done.";
                }

                t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                Toast.makeText(getApplicationContext(), toSpeak, Toast.LENGTH_LONG).show();
            }
        });

    }

    public void onPause(){
        if(t1 !=null){
            t1.stop();
            t1.shutdown();
        }
        super.onPause();
    }
}
