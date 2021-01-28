package com.example.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.security.KeyStore;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //Oyuncu skorlarını ve oyun durumunu belirten textview'ları tanımlamak üzere değişken oluşturuyoruz.
    private TextView playerOneScore, playerTwoScore,playerStatus;

    //Oyunun oynanmasını sağlayacak butonlar için 9 elemanlı bir buton dizisi...
    private Button[] buttons = new Button[9];

    //Oyunu resetlemek için ayrıca bir button daha
    private Button resetGame;

    //Oyuncu skorlarını saymak için 2 değişken.
    //rountCount oyundaki hamle sayısını tutar ve 9 hamle olunca oyun sıfırlanır.
    private int playerOneScoreCount, playerTwoScoreCount, rountCount;

    // 1. ve 2. Oyuncu arasında geçiş yapmak için bir boolean değişken...
    boolean activePlayer;

    //birinci oyuncu => 0
    //ikinci oyuncu => 1
    // boş => 2

    //9 butonu temsilen 9 elemanlı Oyun Durumu dizisini oluşturup, tüm indekslere 2 atıyoruz.(yani boş)
    int[] gameState = {2,2,2,2,2,2,2,2,2};

    //Oyunun kazanan pozisyonlarını tanımlıyoruz..
    int[][] winningPositions = {
            {0,1,2}, {3,4,5}, {6,7,8}, //sıralar
            {0,3,6}, {1,4,7}, {2,5,8}, //sütunlar
            {0,4,8}, {2,4,6} //çaprazlar
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //değişkenlerimize, ilgili görünümleri tanımlıyoruz.
        playerOneScore = (TextView) findViewById(R.id.playerOneScore);
        playerTwoScore = (TextView) findViewById(R.id.playerTwoScore);
        playerStatus = (TextView) findViewById(R.id.playerStatus);
        resetGame = (Button) findViewById(R.id.resetGame);

        //buton dizimizin uzunluğunda bir döngü oluşturup butonlarımızı tanımlıyoruz.
        //tüm düğmelere setonclicklistener fonksiyonunu tanımlıyoruz, böylece butonlara basıldıkça kontrol işlemlerimizi yapıcaz
        for(int i=0; i<buttons.length; i++){
            String buttonID = "btn_" + i;
            int resourceID = getResources().getIdentifier(buttonID,"id",getPackageName());
            buttons[i] = (Button) findViewById(resourceID);
            buttons[i].setOnClickListener(this);
        }

        //değişkenleri 0 olarak başlatıyoruz.
        rountCount = 0;
        playerOneScoreCount = 0;
        playerTwoScoreCount = 0;

        //1. oyuncu için boolean değerimiz true, 2.Oyuncu için false..
        activePlayer = true;

    }

    @Override //Oyun butonlarına basıldıkça onclick methodumuz çalışacak
    public void onClick(View v) {

        //butonun boş olup olmadığı kontrol edilir, boş değil ise methodtan çıkılır.
       if(!((Button)v).getText().toString().equals("")){
         return;
       }
       //basılan butonun id numarasını alıyoruz
       String buttonID = v.getResources().getResourceEntryName(v.getId());

       //oyun durumu dizisinde değişiklik yapmak için bir işaretçi tanımlıyoruz..
       int gameStatePointer = Integer.parseInt(buttonID.substring(buttonID.length()-1, buttonID.length()));

       //active player true ise 1.oyuncu için işlem yapılır.
       if(activePlayer){

           //buton yazı ve renkleri ayarlanıp, gameState dizimizde gerekli güncelleme yapılır.
           ((Button) v).setText("x");
           ((Button) v).setTextColor(Color.parseColor("#FFC34A"));
           gameState[gameStatePointer] = 0;
       }else{
           ((Button) v).setText("O");
           ((Button) v).setTextColor(Color.parseColor("#70FFEA"));
           gameState[gameStatePointer] = 1;
       }
       //hamle sayısı arttırılır.
       rountCount++;

       //kazanan var mı kontrol edilir. activePlayer 1. ve 2. oyuncuları temsil etmektedir.
        //kazanan olduğunda skor değişkeni arttırılır -> Ekrandaki textview güncellenir, Toast mesaj gösterilir.
       if(checkWinner()){
           if(activePlayer){
              playerOneScoreCount++;
              updatePlayerScore();
               Toast.makeText(this,"Player One Won!",Toast.LENGTH_SHORT).show();
               playAgain();
           }else{
               playerTwoScoreCount++;
               updatePlayerScore();
               Toast.makeText(this,"Player two Won!",Toast.LENGTH_SHORT).show();
               playAgain();
           }

           //hamle sayısı 9 olmuş ise oyun tekrar başlatılır ve Toast mesaj gösterilir.
       }else if(rountCount == 9){
           playAgain();
           Toast.makeText(this,"No Winner!",Toast.LENGTH_SHORT).show();

           //kazanan yok ise ve hamle sayısıda 9'dan küçük ise hamle sırası karşı oyuncuya verilir.
       }else{
           activePlayer = !activePlayer;
       }

       //Oyunculardan önde olan ekranda belirtilir.
       if(playerOneScoreCount > playerTwoScoreCount){
           playerStatus.setText("Player One is Winning!");
       }else if(playerTwoScoreCount > playerOneScoreCount){
           playerStatus.setText("Player Two is Winning!");
        }else{
           playerStatus.setText("");
        }

       //Reset game buton ile oyun resetlenir, tüm değişkenler sıfırlanır.
        resetGame.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
             playAgain();
             playerOneScoreCount = 0;
             playerTwoScoreCount = 0;
             playerStatus.setText("");
             updatePlayerScore();
            }
         });

    }

     //checkWinner methodunda kazanan var mı kontrol edilir.
    public boolean checkWinner(){
        boolean winnerResult = false;

        //kazanan pozisyonlar döngü ile kontrol edilir.
        for(int[] winningPosion:winningPositions){
            if(gameState[winningPosion[0]]==gameState[winningPosion[1]] &&
                    gameState[winningPosion[1]]==gameState[winningPosion[2]] &&
                         gameState[winningPosion[0]] !=2){
                winnerResult = true;
            }
        }
        return winnerResult;
    }

    //Ekrandaki Oyuncu skorunu güncelliyoruz.
   public void updatePlayerScore(){
      playerOneScore.setText(Integer.toString(playerOneScoreCount));
      playerTwoScore.setText(Integer.toString(playerTwoScoreCount));
   }

   //oyun yenilendiğinde hamle sayısı sıfırlanır ve hamle sırası 1. oyuncuya verilir.
   public void playAgain(){
        rountCount = 0;
        activePlayer = true;

        //Oyun durumu dizisindeki tüm indekslere 2 değeri atanır, (2=>boş) Butonların yazıları temizlenir.
        for(int i=0; i<buttons.length;i++){
            gameState[i] = 2;
            buttons[i].setText("");
        }
   }


}