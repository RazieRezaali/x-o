package ir.shariaty.x_0


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import ir.shariaty.x_0.databinding.ActivityMainBinding
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.playOfflineBtn.setOnClickListener{
            createOfflineGame()
        }

        binding.createOnlineBtn.setOnClickListener{
            createOnlineGame()
        }

        binding.joinOnlineBtn.setOnClickListener{
            joinOnlineGame()
        }
    }

    fun createOfflineGame() {
        GameData.saveGameModel(
            GameModel(
                gameStatus = GameStatus.JOINED
            )
        )
        startGame()
    }

    fun createOnlineGame(){
        GameData.myID="X"
        GameData.saveGameModel(
            GameModel(
                gameStatus = GameStatus.CREATED ,
                gameId = Random.nextInt(1000,9999).toString()
            )
        )
        startGame()
    }
    fun joinOnlineGame() {
        var gameId = binding.gameIdInput.text.toString()
        if (gameId.isEmpty()){
            binding.gameIdInput.setError("please enter the game Id")
            return
        }
        GameData.myID = "O"
        Firebase.firestore.collection("games").document(gameId).get().addOnSuccessListener {
            val model = it?.toObject(GameModel::class.java)
            if (model == null){
                binding.gameIdInput.setError("please enter the correct game Id")
            }
            else{
                model.gameStatus = GameStatus.JOINED
                GameData.saveGameModel(model)
                startGame()
            }
        }
    }

    fun startGame(){
        startActivity(Intent(this, GameActivity::class.java))
    }

}