package com.example.connect3;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.util.Arrays.asList;

public class MainActivity extends AppCompatActivity {
    private int played = 0;
    private boolean gameOn = true;
    private int[][] winningCombos = {{0, 1, 2}, {3, 4, 5}, {6, 7, 8}, {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, {0, 4, 8}, {2, 4, 6}};
    private int moves = 0;
    private int click = 0;
    private Random random;
    private GridLayout grid;
    private LinearLayout linearLayout;
    private ArrayList<Integer> remainingMoves;

    private int crossScore = 0;
    private int noughtScore = 0;

    private List<Integer> tags = new ArrayList<>();
    private ArrayList<Integer> noughts = new ArrayList<>();//system
    private ArrayList<Integer> cross = new ArrayList<>();//player
    private boolean winnerDeclared = false;

    /**
     * @param resName name of the imageView id.
     * @return this method returns the image view id according to passed string
     * returns -1 if image view by that string is not found.
     */

    private static int getResId(String resName) {

        try {
            Class c = R.id.class;
            Field field = c.getDeclaredField(resName);
            return field.getInt(field);

        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * called on creation of the Activity.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        grid = (GridLayout) findViewById(R.id.gridLayout);
        linearLayout = (LinearLayout) findViewById(R.id.linearScoreLayout);
        random = new Random();

        remainingMoves = new ArrayList<>();
        remainingMoves.add(0);
        remainingMoves.add(1);
        remainingMoves.add(2);
        remainingMoves.add(3);
        remainingMoves.add(4);
        remainingMoves.add(5);
        remainingMoves.add(6);
        remainingMoves.add(7);
        remainingMoves.add(8);
    }

    /**
     * @param view called on click of any image view on the grid.
     *             calls both methods executeMove() and systemMove() .
     */

    public void dropIn(View view) {

        if (gameOn) {
            ImageView box = (ImageView) view;

            executeMove(box, 0);

            if (moves < 9)
                systemMove();

        }
    }

    /**
     * @param s passes the name of the winner
     *          holds the play and declares the winner  that is passed through the parameter s.
     */
    private void declareWinner(String s) {

        gameOn = false;
        winnerDeclared = true;
        played++;
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();

        new CountDownTimer(2000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                grid.setVisibility(View.INVISIBLE);
                linearLayout.setVisibility(View.INVISIBLE);

                LinearLayout buttons = (LinearLayout) findViewById(R.id.buttonsCarrier);
                buttons.setVisibility(View.VISIBLE);

            }
        }.start();


    }

    private int difficulty = 0;

    /**
     * @param view the play again button.
     *             called on click of playAgainButton.
     *             this button resets the game Board.
     */
    public void playAgain(View view) {

        difficulty = Integer.parseInt(view.getTag().toString());

        LinearLayout buttons = (LinearLayout) findViewById(R.id.buttonsCarrier);
        buttons.setVisibility(View.INVISIBLE);

        click = 0;
        moves = 0;
        tags.clear();
        noughts.clear();
        cross.clear();

        grid.setVisibility(View.VISIBLE);
        linearLayout.setVisibility(View.VISIBLE);

        remainingMoves.clear();

        remainingMoves.add(0);
        remainingMoves.add(1);
        remainingMoves.add(2);
        remainingMoves.add(3);
        remainingMoves.add(4);
        remainingMoves.add(5);
        remainingMoves.add(6);
        remainingMoves.add(7);
        remainingMoves.add(8);


        for (int i = 0; i < grid.getChildCount(); i++) {

            ((ImageView) grid.getChildAt(i)).setImageResource(0);
        }

        gameOn = true;
        winnerDeclared = false;

        if (played % 2 == 1) {
            systemMove();
        }
    }

    /**
     * logic for making moves based on users moves.
     * calls the executeMove() method.
     */
    boolean gotCore = false;
    ArrayList<Integer> corners = new ArrayList<>(asList(0, 2, 6, 8));


    private void systemMove() {
        Log.v("msg", "system move called");
        int n;
        int r;
        if (moves > 3 && (n = canWin(noughts)) != -1 && difficulty > 1) {

            r = n;

        } else if (moves > 2 && (n = canWin(cross)) != -1 && difficulty > 1) {

            r = n;

        } else if (!tags.contains(4) && difficulty > 2) {

            r = 4;
            gotCore = true;

        } else if (!tags.containsAll(corners) && difficulty > 3) {

            r = corners.get(random.nextInt(4));
            while (tags.contains(r)) {
                r = corners.get(random.nextInt(4));

            }

        } else {

            r = random.nextInt(8);

            Log.v("random generated ", String.valueOf(r));

            while (tags.contains(r)) {

                r = remainingMoves.get(random.nextInt(remainingMoves.size()));

            }
            Log.v("random confirmed", String.valueOf(r));

        }


        String res = String.valueOf(r);

        Log.v("id generated ", res);

        String resId = "imageView" + res;

        if (gameOn) {


            ImageView box = (ImageView) findViewById(getResId(resId));

            executeMove(box, 1);
        }
    }

    /**
     * @param box    image view on which action needs to be performed.
     * @param passed defines who clicked passed = 0 means player and 1 means system.
     *               this method bears the complete logic for making a move and also deciding a winner.
     */

    private void executeMove(ImageView box, int passed) {

        int tag = Integer.parseInt(box.getTag().toString());

        if (!tags.contains(tag)) {
            Log.v("valid", "move");
            moves++;
            tags.add(tag);
            Integer t = tag;
            remainingMoves.remove(t);

            box.setTranslationY(-1000f);

            if (passed == 0) {

                box.setImageResource(R.drawable.cross);

                cross.add(tag);
                Log.v("player", "move");

            } else {

                box.setImageResource(R.drawable.nought);
                noughts.add(tag);
                Log.v("system", "move");

            }
            box.animate().translationYBy(1000).setDuration(300);


            if (moves >= 5) {
                Log.v("testing", "win possibilities");
                for (int[] winCombo :
                        winningCombos) {
                    if (cross.contains(winCombo[0]) && cross.contains(winCombo[1]) && cross.contains(winCombo[2])) {
                        declareWinner("Player Wins");
                        TextView crossScoreView = (TextView) findViewById(R.id.playerScore);
                        crossScore++;
                        crossScoreView.setText(String.valueOf(crossScore));
                        break;
                    } else if (noughts.contains(winCombo[0]) && noughts.contains(winCombo[1]) && noughts.contains(winCombo[2])) {
                        declareWinner("System Wins");
                        TextView noughtsScoreView = (TextView) findViewById(R.id.systemScore);
                        noughtScore++;
                        noughtsScoreView.setText(String.valueOf(noughtScore));
                        break;
                    }

                }
                Log.v("message", "no winner found");
            }


        }

        if (moves == 9) {
            if (!winnerDeclared)
                declareWinner("its a draw");
        }
    }

    /**
     * @param arr passed to perform check .
     *            The provided logic check either if the player is making a winning move .
     *            OR the system has a chance to make a winning move .
     * @return returns nextMove for the system.
     */
    private int canWin(ArrayList<Integer> arr) {
        int moveTo = -1;

        for (int[] winCombo : winningCombos) {

            if (arr.contains(winCombo[0]) && arr.contains(winCombo[1]) && !tags.contains(winCombo[2])) {

                moveTo = winCombo[2];

                return moveTo;

            } else if (arr.contains(winCombo[1]) && arr.contains(winCombo[2]) && !tags.contains(winCombo[0])) {
                moveTo = winCombo[0];

                return moveTo;


            } else if (arr.contains(winCombo[0]) && arr.contains(winCombo[2]) && !tags.contains(winCombo[1])) {

                moveTo = winCombo[1];

                return moveTo;

            }

        }

        return moveTo;
    }


}
