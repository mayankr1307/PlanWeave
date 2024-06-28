package android.project.planweave.activities

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.project.planweave.R
import android.project.planweave.adapters.MemberListItemsAdapter
import android.project.planweave.databinding.ActivityMembersBinding
import android.project.planweave.firebase.FireStoreClass
import android.project.planweave.models.Board
import android.project.planweave.models.User
import android.project.planweave.utils.Constants
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager

class MembersActivity : BaseActivity() {

    private var binding: ActivityMembersBinding? = null
    private lateinit var mBoardDetails: Board
    private lateinit var mAssignedMembersList: ArrayList<User>
    private  var anyChangesMade: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMembersBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        showProgressDialog("Please wait...")

        if(intent.hasExtra(Constants.BOARD_DETAIL)) {
            mBoardDetails = intent.getParcelableExtra(Constants.BOARD_DETAIL)!!
            FireStoreClass().getAssignedMembersListDetails(this@MembersActivity, mBoardDetails.assignedTo)
        }

        setupActionBar()
    }

    private fun setupActionBar() {
        setSupportActionBar(binding?.toolbarMembersActivity)
        val actionBar = supportActionBar

        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_white)
            actionBar.title = resources.getString(R.string.members)
        }

        binding?.toolbarMembersActivity?.setNavigationOnClickListener { onBackPressed() }
    }

    fun setupMembersList(list: ArrayList<User>) {
        mAssignedMembersList = list
        hideProgressDialog()
        binding?.rvMembersList?.layoutManager = LinearLayoutManager(this)
        binding?.rvMembersList?.setHasFixedSize(true)

        val adapter = MemberListItemsAdapter(this, list)
        binding?.rvMembersList?.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_member, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_add_member -> {
                dialogSearchMember()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun dialogSearchMember() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_search_member)
        dialog.findViewById<TextView>(R.id.tv_add).setOnClickListener {
            val email = dialog.findViewById<EditText>(R.id.et_email_search_member).text.toString()

            if(email.isNotEmpty()) {
                dialog.dismiss()
                showProgressDialog("Please wait...")
                FireStoreClass().getMemberDetails(this, email)
            }else {
                Toast.makeText(
                    this@MembersActivity,
                    "Please enter members email address.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        dialog.findViewById<TextView>(R.id.tv_cancel).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    fun memberDetails(user: User) {
        mBoardDetails.assignedTo.add(user.id)
        FireStoreClass().assignMemberToBoard(this, mBoardDetails, user)
    }

    fun memberAssignedSuccess(user: User) {
        hideProgressDialog()
        mAssignedMembersList.add(user)
        setupMembersList(mAssignedMembersList)
        anyChangesMade = true
    }

    override fun onBackPressed() {
        if(anyChangesMade) {
            setResult(Activity.RESULT_OK)
        }
        super.onBackPressed()
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }
}