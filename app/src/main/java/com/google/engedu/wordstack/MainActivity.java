package com.google.engedu.wordstack;

import android.content.res.AssetManager;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    private static final int WORD_LENGTH = 5;
    public static final int LIGHT_BLUE = Color.rgb(176, 200, 255);
    public static final int LIGHT_GREEN = Color.rgb(200, 255, 200);
    private ArrayList<String> words = new ArrayList<>();
    private Random random = new Random();
    private StackedLayout stackedLayout;
    private String word1, word2;
    String s1="";
    private Stack<LetterTile> placedTiles=new Stack<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AssetManager assetManager = getAssets();
        try {
            InputStream inputStream = assetManager.open("words.txt");
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            while((line = in.readLine()) != null) {
                String word = line.trim();
                if(word.length()==WORD_LENGTH){
                    words.add(word);
                }
            }
        } catch (IOException e) {
            Toast toast = Toast.makeText(this, "Could not load dictionary", Toast.LENGTH_LONG);
            toast.show();
        }
        LinearLayout verticalLayout = (LinearLayout) findViewById(R.id.vertical_layout);
        stackedLayout = new StackedLayout(this);
        verticalLayout.addView(stackedLayout, 3);

        View l1 = findViewById(R.id.word1);
        //l1.setOnTouchListener(new TouchListener());
        l1.setOnDragListener(new DragListener());
        View l2 = findViewById(R.id.word2);
        // l2.setOnTouchListener(new TouchListener());
        l2.setOnDragListener(new DragListener());
    }

    private class TouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN && !stackedLayout.empty()) {
                LetterTile tile = (LetterTile) stackedLayout.peek();
                tile.moveToViewGroup((ViewGroup) v);
                if (stackedLayout.empty()) {
                    TextView messageBox = (TextView) findViewById(R.id.message_box);
                    String s=word1+word2;
                    messageBox.setText(s1);
                }

                placedTiles.push(tile);
                return true;
            }
            return false;
        }
    }

    private class DragListener implements View.OnDragListener {

        public boolean onDrag(View v, DragEvent event) {
            int action = event.getAction();
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    v.setBackgroundColor(LIGHT_BLUE);
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DRAG_ENTERED:
                    v.setBackgroundColor(LIGHT_GREEN);
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DRAG_EXITED:
                    v.setBackgroundColor(LIGHT_BLUE);
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DRAG_ENDED:
                    v.setBackgroundColor(Color.WHITE);
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DROP:
                    // Dropped, reassign Tile to the target Layout
                    LetterTile tile = (LetterTile) event.getLocalState();
                    tile.moveToViewGroup((ViewGroup) v);
                    if (stackedLayout.empty()) {
                        TextView messageBox = (TextView) findViewById(R.id.message_box);

                        messageBox.setText(s1);
                    }
                    placedTiles.push(tile);

                    return true;
            }
            return false;
        }
    }

    public boolean onStartGame(View view) {
        String word1="";
        String word2="";
        LinearLayout l1= (LinearLayout) findViewById(R.id.word1);
        LinearLayout l2= (LinearLayout) findViewById(R.id.word2);

        l1.removeAllViews();
        l2.removeAllViews();
        stackedLayout.clear();
        TextView messageBox = (TextView) findViewById(R.id.message_box);
        messageBox.setText("Game started");

        word1=words.get(random.nextInt(words.size()-1)+1);
        word2=words.get(random.nextInt(words.size()-1)+1);
        s1=word1+" "+word2;
        boolean flag=false;
        int ch=1;
        int counter1=0,counter2=0;
        String value="";

        while(!(flag)){
            ch=random.nextInt(2);
            switch (ch){
                case 0:
                    value=value+word1.charAt(counter1);

                    if(word1.length()<=counter1+1)
                    {
                        while(counter2<word2.length())
                        {
                            value=value+word2.charAt(counter2);
                            counter2++;
                        }
                        flag=true;
                    }
                    else
                    {
                        counter1++;
                    }

                    break;
                case 1:
                    value=value+word2.charAt(counter2);

                    if(word1.length()<=counter2+1)
                    {
                        while(counter1<word1.length())
                        {
                            value=value+word1.charAt(counter1);
                            counter1++;
                        }
                        flag=true;
                    }
                    else
                    {
                        counter2++;
                    }

                    break;
            }


        }

        messageBox.setText(value);
        for(int i=value.length()-1;i>=0;i--)
        {
            stackedLayout.push(new LetterTile(this,value.charAt(i)));
        }
        return true;
    }

    public boolean onUndo(View view) {

        LetterTile remove;
        if(!(placedTiles.empty()))
        {
            remove=placedTiles.pop();
            remove.moveToViewGroup((ViewGroup)stackedLayout);
            return true;
        }
        return  false;

    }

}