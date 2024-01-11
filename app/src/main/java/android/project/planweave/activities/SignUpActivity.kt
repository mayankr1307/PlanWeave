package android.project.planweave.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.project.planweave.R
import android.project.planweave.databinding.ActivitySignUpBinding
import android.view.Window
import android.view.WindowManager

class SignUpActivity : AppCompatActivity() {

    private var binding: ActivitySignUpBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setupToolBar()
    }

    private fun setupToolBar() {
        setSupportActionBar(binding?.tbSignup)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        binding?.tbSignup?.setNavigationOnClickListener { onBackPressed() }
        supportActionBar?.title = "Sign Up"
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }
}