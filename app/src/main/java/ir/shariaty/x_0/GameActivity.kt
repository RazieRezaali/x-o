package ir.shariaty.x_0

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import ir.shariaty.x_0.databinding.ActivityGameBinding

class GameActivity : AppCompatActivity() , View.OnClickListener {
    lateinit var binding: ActivityGameBinding

    private  var  gameModel: GameModel ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        GameData.fetchGameModel()

        binding.btn0.setOnClickListener(this)
        binding.btn1.setOnClickListener(this)
        binding.btn2.setOnClickListener(this)
        binding.btn3.setOnClickListener(this)
        binding.btn4.setOnClickListener(this)
        binding.btn5.setOnClickListener(this)
        binding.btn6.setOnClickListener(this)
        binding.btn7.setOnClickListener(this)
        binding.btn8.setOnClickListener(this)

        binding.backToMainPageBtn.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))
        }

        binding.startGameBtn.setOnClickListener{
            startGame()

        }

        GameData.gameModel.observe(this){
            gameModel = it
            setUI()
        }
    }



     fun setUI() {
        gameModel?.apply {
            binding.btn0.text = filedPos[0]
            binding.btn1.text = filedPos[1]
            binding.btn2.text = filedPos[2]
            binding.btn3.text = filedPos[3]
            binding.btn4.text = filedPos[4]
            binding.btn5.text = filedPos[5]
            binding.btn6.text = filedPos[6]
            binding.btn7.text = filedPos[7]
            binding.btn8.text = filedPos[8]

            binding.startGameBtn.visibility = View.VISIBLE

            binding.backToMainPageBtn.visibility = View.VISIBLE

            binding.gameStatusText.text = when(gameStatus){
                GameStatus.CREATED ->{
                    binding.startGameBtn.visibility = View.GONE
                    "game ID : " + gameId
                }
                GameStatus.JOINED ->{
                    "Click On Start Game"
                }
                GameStatus.INPROGRESS ->{
                    binding.startGameBtn.visibility = View.GONE
                    binding.backToMainPageBtn.visibility = View.GONE
                    when(GameData.myID){
                        currentPlayer -> "your turn"
                        else -> currentPlayer+" turn"
                    }
                }
                GameStatus.FINISHED ->{
                    binding.startGameBtn.setText("Play Again")
                    if (winner.isNotEmpty()) {
                        when(GameData.myID){
                            winner -> "you win"
                            else -> winner + " won!"
                        }
                    }
                    else "Draw"
                }
            }
        }
    }

    fun startGame(){
        gameModel?.apply{
            updateGameData(
                GameModel(
                    gameId = gameId,
                    gameStatus = GameStatus.INPROGRESS
                )
            )
        }
    }

    fun updateGameData(model : GameModel) {
        GameData.saveGameModel(model)
    }

    fun checkForWinner(){
        val winningPos = arrayOf(
            intArrayOf(0,1,2),
            intArrayOf(3,4,5),
            intArrayOf(6,7,8),
            intArrayOf(0,3,6),
            intArrayOf(1,4,7),
            intArrayOf(2,5,8),
            intArrayOf(0,4,8),
            intArrayOf(2,4,6)
        )

        gameModel?.apply {
            for (i in winningPos){
                if (filedPos[i[0]] == filedPos[i[1]] && filedPos[i[1]] == filedPos[i[2]] && filedPos[i[0]].isNotEmpty()){
                    gameStatus = GameStatus.FINISHED
                    winner = filedPos[i[0]]
                }
            }

            if (filedPos.none(){ it.isEmpty() }) {
                gameStatus = GameStatus.FINISHED
            }

            updateGameData(this)
        }
    }

    override fun onClick(v: View?) {
        gameModel?.apply {
            if(gameStatus!=GameStatus.INPROGRESS) {
                Toast.makeText(applicationContext, "game not started", Toast.LENGTH_SHORT)
                    .show()
                return
            }
            //Game is in progress
            if( gameId != "-1" && currentPlayer != GameData.myID ){
                Toast.makeText(applicationContext,"it is not your turn",Toast.LENGTH_SHORT).show()
                return
            }
            val clickPos= (v?.tag as String).toInt()
            if (filedPos[clickPos].isEmpty()){
                filedPos[clickPos]=currentPlayer
                currentPlayer = if (currentPlayer=="X") "O" else "X"
                updateGameData(this)
                checkForWinner()
            }
        }
    }
}