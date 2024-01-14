package android.project.planweave.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.project.planweave.R
import android.project.planweave.databinding.ActivityIntroBinding
import android.view.View

class IntroActivity : AppCompatActivity(), View.OnClickListener {

    private var binding: ActivityIntroBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.btnSignup?.setOnClickListener(this@IntroActivity)
        binding?.btnSignin?.setOnClickListener(this@IntroActivity)
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }

    override fun onClick(view: View?) {
        when(view?.id) {
            R.id.btn_signup -> {
                startActivity(Intent(this@IntroActivity, SignUpActivity::class.java))
            }
            R.id.btn_signin -> {
                startActivity(Intent(this@IntroActivity, SignInActivity::class.java))
            }
        }

    }
}