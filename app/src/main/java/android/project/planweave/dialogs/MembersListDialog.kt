package android.project.planweave.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.project.planweave.R
import android.project.planweave.adapters.MemberListItemsAdapter
import android.project.planweave.models.User
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class MembersListDialog(
    context: Context,
    private var list: ArrayList<User>,
    private val title: String = ""
) : Dialog(context) {

    private var adapter: MemberListItemsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState ?: Bundle())

        val view = LayoutInflater.from(context).inflate(R.layout.dialog_list, null)

        setContentView(view)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        setUpRecyclerView(view)
    }

    private fun setUpRecyclerView(view: View) {
        view.findViewById<TextView>(R.id.tv_title).text = title

        if (list.size > 0) {

            view.findViewById<RecyclerView>(R.id.rv_list).layoutManager = LinearLayoutManager(context)
            adapter = MemberListItemsAdapter(context, list)
            view.findViewById<RecyclerView>(R.id.rv_list).adapter = adapter

            adapter!!.setOnClickListener(object :
                MemberListItemsAdapter.OnClickListener {
                override fun onClick(position: Int, user: User, action:String) {
                    dismiss()
                    onItemSelected(user, action)
                }
            })
        }
    }

    protected abstract fun onItemSelected(user: User, action:String)
}