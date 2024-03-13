package android.project.planweave.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.project.planweave.R
import android.project.planweave.databinding.ActivityCreateBoardBinding

class CreateBoardActivity : AppCompatActivity() {

    private var binding: ActivityCreateBoardBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateBoardBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setupActionBar()
    }

    private fun setupActionBar() {
        setSupportActionBar(binding?.tbCreateBoardActivity)
        if(supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_back_white)
            supportActionBar!!.title = "Create Board"
        }

        binding?.tbCreateBoardActivity?.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }
}