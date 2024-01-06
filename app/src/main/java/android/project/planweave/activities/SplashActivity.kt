package android.project.planweave.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.project.planweave.R
import android.view.Window
import android.view.WindowManager
import android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.apply {
            requestFeature(Window.FEATURE_NO_TITLE)
            setFlags(
                FLAG_FULLSCREEN,
                FLAG_FULLSCREEN
            )
        }
        setContentView(R.layout.activity_splash)

        object: CountDownTimer(2000, 2000) {
            override fun onTick(millisUntilFinished: Long) {

            }
            override fun onFinish() {
                val intent = Intent(this@SplashActivity, IntroActivity::class.java)
                startActivity(intent)
                finish()
            }
        }.start()
    }
}