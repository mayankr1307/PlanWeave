package android.project.planweave.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.project.planweave.R
import android.project.planweave.databinding.ActivityTaskListBinding
import android.project.planweave.firebase.FireStoreClass
import android.project.planweave.models.Board
import android.project.planweave.utils.Constants

class TaskListActivity : BaseActivity() {

    private var binding: ActivityTaskListBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskListBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        var boardDocumentId = ""
        if(intent.hasExtra(Constants.DOCUMENT_ID)) {
            boardDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID).toString()
        }

        showProgressDialog("Please wait...")
        FireStoreClass().getBoardDetails(this@TaskListActivity, boardDocumentId)
    }

    fun boardDetails(board: Board) {
        hideProgressDialog()
        setupActionBar(board.name)
    }

    private fun setupActionBar(title: String) {
        setSupportActionBar(binding?.tbTaskListActivity)
        if(supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_back_white)
            supportActionBar!!.title = title
        }

        binding?.tbTaskListActivity?.setNavigationOnClickListener {
            onBackPressed()
        }
    }
    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }
}