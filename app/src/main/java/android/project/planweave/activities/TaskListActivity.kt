package android.project.planweave.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.project.planweave.R
import android.project.planweave.adapters.TaskListItemsAdapter
import android.project.planweave.databinding.ActivityTaskListBinding
import android.project.planweave.firebase.FireStoreClass
import android.project.planweave.models.Board
import android.project.planweave.models.Card
import android.project.planweave.models.Task
import android.project.planweave.models.User
import android.project.planweave.utils.Constants
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager

class TaskListActivity : BaseActivity() {

    private var binding: ActivityTaskListBinding? = null

    private lateinit var mBoardDetails: Board
    private lateinit var mBoardDocumentID: String
    lateinit var mAssignedMemberDetailList: ArrayList<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskListBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        if(intent.hasExtra(Constants.DOCUMENT_ID)) {
            mBoardDocumentID = intent.getStringExtra(Constants.DOCUMENT_ID).toString()
        }

        showProgressDialog("Please wait...")
        FireStoreClass().getBoardDetails(this@TaskListActivity, mBoardDocumentID)
    }
    fun boardDetails(board: Board) {
        mBoardDetails = board
        hideProgressDialog()
        setupActionBar()



        showProgressDialog("Please wait...")
        FireStoreClass().getAssignedMembersListDetails(this@TaskListActivity, mBoardDetails.assignedTo)
    }

    private fun setupActionBar() {
        setSupportActionBar(binding?.tbTaskListActivity)
        if(supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_back_white)
            supportActionBar!!.title = mBoardDetails.name
        }

        binding?.tbTaskListActivity?.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    fun addUpdateTaskListSuccess() {
        hideProgressDialog()
        showProgressDialog("Please wait...")
        FireStoreClass().getBoardDetails(this@TaskListActivity, mBoardDetails.documentId)
    }

    fun updateTaskList(position: Int, listName: String, model: Task) {
        val task = Task(listName, model.createdBy)

        mBoardDetails.taskList[position] = task
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)

        showProgressDialog("Please wait...")

        FireStoreClass().addUpdateTaskList(this, mBoardDetails)
    }

    fun deleteTaskList(position: Int) {
        mBoardDetails.taskList.removeAt(position)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)

        showProgressDialog("Please wait...")

        FireStoreClass().addUpdateTaskList(this, mBoardDetails)
    }
    fun createTaskList(taskListName: String) {
        val task = Task(taskListName, FireStoreClass().getCurrentUserId())
        mBoardDetails.taskList.add(0, task)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)

        showProgressDialog("Please wait...")

        FireStoreClass().addUpdateTaskList(this, mBoardDetails)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_members, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_members -> {
                val intent = Intent(this@TaskListActivity, MembersActivity::class.java)
                intent.putExtra(Constants.BOARD_DETAIL, mBoardDetails)
                startActivityForResult(intent, MEMBER_REQUEST_CODE)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun addCardToTaskList(position: Int, cardName: String) {
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)

        val cardAssignedUsersList: ArrayList<String> = ArrayList()
        cardAssignedUsersList.add(FireStoreClass().getCurrentUserId())

        val card = Card(cardName, FireStoreClass().getCurrentUserId(), cardAssignedUsersList)

        val cardsList = mBoardDetails.taskList[position].cards
        cardsList.add(card)

        val task = Task(
            mBoardDetails.taskList[position].title,
            mBoardDetails.taskList[position].createdBy,
            cardsList
        )

        mBoardDetails.taskList[position] = task
        showProgressDialog("Please wait...")

        FireStoreClass().addUpdateTaskList(this, mBoardDetails)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && (requestCode == MEMBER_REQUEST_CODE || requestCode == CARD_DETAILS_REQUEST_CODE)) {
            showProgressDialog("Please wait...")
            FireStoreClass().getBoardDetails(this, mBoardDocumentID)
        }else {
            Log.e("Cancelled", "Cancelled")
        }
    }

    fun cardDetails(taskListPosition: Int, cardPosition: Int) {
        val intent = Intent(this, CardDetailsActivity::class.java)
        intent.putExtra(Constants.BOARD_DETAIL, mBoardDetails)
        intent.putExtra(Constants.TASK_LIST_ITEM_POSITION, taskListPosition)
        intent.putExtra(Constants.CARD_LIST_ITEM_POSITION, cardPosition)
        intent.putExtra(Constants.BOARD_MEMBERS_LIST, mAssignedMemberDetailList)

        startActivityForResult(intent, CARD_DETAILS_REQUEST_CODE)
    }

    fun boardMembersDetailsList(list: ArrayList<User>) {
        mAssignedMemberDetailList = list
        hideProgressDialog()

        val addTaskList = Task("add_list")
        mBoardDetails.taskList.add(addTaskList)

        binding?.rvTaskList?.layoutManager = LinearLayoutManager(
            this@TaskListActivity,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding?.rvTaskList?.setHasFixedSize(true)

        val adapter = TaskListItemsAdapter(this, mBoardDetails.taskList)

        binding?.rvTaskList?.adapter = adapter
    }

    fun updateCardsInTaskList(taskListPosition: Int, cards: ArrayList<Card>) {
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)
        mBoardDetails.taskList[taskListPosition].cards = cards

        showProgressDialog("Please wait...")

        FireStoreClass().addUpdateTaskList(this, mBoardDetails)
    }

    companion object {
        const val MEMBER_REQUEST_CODE: Int = 135
        const val CARD_DETAILS_REQUEST_CODE: Int = 149
    }
    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }
}