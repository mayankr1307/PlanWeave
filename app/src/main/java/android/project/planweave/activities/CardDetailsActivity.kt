package android.project.planweave.activities

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.project.planweave.R
import android.project.planweave.adapters.CardMemberListItemsAdapter
import android.project.planweave.databinding.ActivityCardDetailsBinding
import android.project.planweave.dialogs.LabelColorListDialog
import android.project.planweave.dialogs.MembersListDialog
import android.project.planweave.firebase.FireStoreClass
import android.project.planweave.models.Board
import android.project.planweave.models.Card
import android.project.planweave.models.SelectedMembers
import android.project.planweave.models.Task
import android.project.planweave.models.User
import android.project.planweave.utils.Constants
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager

class CardDetailsActivity : BaseActivity() {

    private var binding: ActivityCardDetailsBinding? = null

    private lateinit var mBoardDetails: Board
    private lateinit var mMembersDetailList: ArrayList<User>
    private var mTaskListPosition = -1
    private var mCardPosition = -1
    private var mSelectedColor = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardDetailsBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        getIntentData()
        setupActionBar()
        binding?.etNameCardDetails?.setText(mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].name)
        binding?.etNameCardDetails?.setSelection(binding?.etNameCardDetails?.text.toString().length)

        mSelectedColor = mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].labelColor
        if(mSelectedColor.isNotEmpty()) {
            setColor()
        }

        binding?.btnUpdateCardDetails?.setOnClickListener {
            if(binding?.etNameCardDetails?.text.toString().isNotEmpty()) {
                updateCardDetails()
            }else {
                Toast.makeText(
                    this@CardDetailsActivity,
                    "Please enter the card name.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        binding?.tvSelectLabelColor?.setOnClickListener {
            labelColorsListDialog()
        }

        binding?.tvSelectMembers?.setOnClickListener {
            membersListDialog()
        }

    }

    private fun membersListDialog() {
        var cardAssignedMembersList = mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo

        if(cardAssignedMembersList.size > 0) {
            for(i in mMembersDetailList.indices) {
                for(j in cardAssignedMembersList) {
                    if(mMembersDetailList[i].id == j) {
                        mMembersDetailList[i].selected = true
                    }
                }
            }
        }else {
            for(i in mMembersDetailList.indices) {
                mMembersDetailList[i].selected = false
            }
        }

        val listDialog = object: MembersListDialog(
            this@CardDetailsActivity,
            mMembersDetailList,
            "Select Member",
        ) {
            override fun onItemSelected(user: User, action: String) {
                // TODO implement the selected Members functionality
            }
        }

        listDialog.show()
    }

    private fun setupActionBar() {
        setSupportActionBar(binding?.toolbarCardDetailsActivity)
        val actionBar = supportActionBar

        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_white)
            actionBar.title = mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].name
        }

        binding?.toolbarCardDetailsActivity?.setNavigationOnClickListener { onBackPressed() }
    }

    private fun colorsList(): ArrayList<String> {

        return arrayListOf(
            "#FF5733",
            "#33FF57",
            "#5733FF",
            "#33FFEC",
            "#F633FF",
            "#FF5733",
            "#57FF33"
        )
    }

    private fun setColor() {
        binding?.tvSelectLabelColor?.text = ""
        binding?.tvSelectLabelColor?.setBackgroundColor(Color.parseColor(mSelectedColor))
    }

    private fun labelColorsListDialog() {
        val colorsList: ArrayList<String> = colorsList()
        val listDialog = object: LabelColorListDialog(
            this,
            colorsList,
            resources.getString(R.string.str_select_label_color),
            mSelectedColor) {
            override fun onItemSelected(color: String) {
                mSelectedColor = color
                setColor()
            }
        }
        listDialog.show()
    }

    private fun getIntentData() {
        if(intent.hasExtra(Constants.BOARD_DETAIL)) {
            mBoardDetails = intent.getParcelableExtra(Constants.BOARD_DETAIL)!!
        }

        if(intent.hasExtra(Constants.TASK_LIST_ITEM_POSITION)) {
            mTaskListPosition = intent.getIntExtra(Constants.TASK_LIST_ITEM_POSITION, -1)
        }

        if(intent.hasExtra(Constants.CARD_LIST_ITEM_POSITION)) {
            mCardPosition = intent.getIntExtra(Constants.CARD_LIST_ITEM_POSITION, -1)
        }

        if(intent.hasExtra(Constants.BOARD_MEMBERS_LIST)) {
            mMembersDetailList = intent.getParcelableArrayListExtra(Constants.BOARD_MEMBERS_LIST)!!
        }
    }

    fun addUpdateTaskListSuccess() {
        hideProgressDialog()

        setResult(Activity.RESULT_OK)

        finish()
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_delete_card, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun updateCardDetails() {
        val card = Card(
            binding?.etNameCardDetails?.text.toString(),
            mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].createdBy,
            mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo,
            mSelectedColor
        )
        mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition] = card
        showProgressDialog("Please wait...")
        FireStoreClass().addUpdateTaskList(this@CardDetailsActivity, mBoardDetails)
    }

    private fun deleteCard() {
        val cardsList: ArrayList<Card> = mBoardDetails.taskList[mTaskListPosition].cards
        cardsList.removeAt(mCardPosition)

        val taskList: ArrayList<Task> = mBoardDetails.taskList
        taskList.removeAt(taskList.size - 1)

        taskList[mTaskListPosition].cards = cardsList

        showProgressDialog("Please wait...")
        FireStoreClass().addUpdateTaskList(this@CardDetailsActivity, mBoardDetails)
    }

    private fun alertDialogForDeleteCard(cardName: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(resources.getString(R.string.alert))
        builder.setMessage(
            resources.getString(R.string.confirmation_message_to_delete_card,
                cardName)
        )

        builder.setIcon(android.R.drawable.ic_dialog_alert)

        builder.setPositiveButton("Yes") { dialogInterface, which ->
            dialogInterface.dismiss()
            deleteCard()
        }

        builder.setNegativeButton("No") {dialogInterface, which ->
            dialogInterface.dismiss()
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_delete_card -> {
                alertDialogForDeleteCard(mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].name)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupSelectedMembersList() {
        val cardAssignedMemberList = mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo

        val selectedMembersList: ArrayList<SelectedMembers> = ArrayList()

        for(i in mMembersDetailList.indices) {
            for(j in cardAssignedMemberList) {
                if(mMembersDetailList[i].id == j) {
                    val selectedMember = SelectedMembers(
                        mMembersDetailList[i].id,
                        mMembersDetailList[i].image
                    )

                    selectedMembersList.add(selectedMember)
                }
            }
        }

        if(selectedMembersList.size > 0) {
            selectedMembersList.add(SelectedMembers("", ""))
            binding?.tvSelectMembers?.visibility = View.GONE
            binding?.rvSelectedMembersList?.visibility = View.VISIBLE
            binding?.rvSelectedMembersList?.layoutManager = GridLayoutManager(
                this,
                6
            )

            val adapter = CardMemberListItemsAdapter(this, selectedMembersList)
            binding?.rvSelectedMembersList?.adapter = adapter
            adapter.setOnClickListener(
                object: CardMemberListItemsAdapter.OnClickListener{
                    override fun onClick() {
                        membersListDialog()
                    }
                }
            )
        }else {
            binding?.tvSelectMembers?.visibility = View.VISIBLE
            binding?.rvSelectedMembersList?.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }
}