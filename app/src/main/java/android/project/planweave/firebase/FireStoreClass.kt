package android.project.planweave.firebase

import android.project.planweave.activities.SignInActivity
import android.project.planweave.activities.SignUpActivity
import android.project.planweave.models.User
import android.project.planweave.utils.Constants
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FireStoreClass {
    private val mFireStore = FirebaseFirestore.getInstance()

    fun registerUser(activity: SignUpActivity, userInfo: User) {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId()).set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegisteredSuccess()
            }.addOnFailureListener {
                e->
                Log.e(activity.javaClass.simpleName, "Error writing document", e)
            }
    }

    fun signInUser(activity: SignInActivity) {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener {document ->
                val loggedInUser = document.toObject(User::class.java)
                if(loggedInUser != null)
                    activity.signInSuccess(loggedInUser)
            }.addOnFailureListener {
                    e->
                Log.e(activity.javaClass.simpleName, "Error writing document", e)
            }
    }

    fun getCurrentUserId(): String {
        var currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID = ""
        if(currentUser != null)
                currentUserID = currentUser.uid
        return currentUserID
    }
}