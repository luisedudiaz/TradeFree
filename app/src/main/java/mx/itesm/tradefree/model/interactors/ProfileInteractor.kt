package mx.itesm.tradefree.model.interactors

import android.app.Activity
import android.util.Log
import com.google.firebase.database.ValueEventListener
import mx.itesm.tradefree.model.models.User
import mx.itesm.tradefree.model.utils.classes.FirebaseManager
import mx.itesm.tradefree.model.utils.enums.Message
import mx.itesm.tradefree.model.utils.enums.UserType
import mx.itesm.tradefree.presenter.contracts.IProfileContract
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import mx.itesm.tradefree.model.utils.classes.Email


class ProfileInteractor(private val profileInteractor: IProfileContract.onProfileListener): FirebaseManager(), IProfileContract.Interactor {

    /**
     * This method update the user type.
     * @param activity  the current activity
     * @param userType  the user selection
     *
     */
    override fun performUpdateTypeUser(activity: Activity, userType: UserType) {
        val uid = auth.currentUser?.uid
        db.reference.child("/users/$uid").child("type").setValue(userType).addOnCompleteListener {
            if (it.isSuccessful) {
                profileInteractor.onSuccess("${Message.PROFILE_TYPE.getMessageSuccess()}${if (userType == UserType.BUYER) "comprador." else "vendedor."}")
            } else {
                profileInteractor.onFailure(Message.PROFILE_TYPE.getMessageError())
            }
        }
    }

    /**
     * This method update the user type.
     * @param activity  the current activity
     * @param user      the user model to update
     *
     */
    override fun performUpdateUserInfo(activity: Activity, user: User) {
        val uid = auth.currentUser?.uid
        if (user.name.isNotBlank()) {
            db.reference.child("/users/$uid").child("name").setValue(user.name).addOnCompleteListener {
                if (it.isSuccessful) {
                    profileInteractor.onProfileUpdateSuccess("${Message.PROFILE_DATA.getMessageSuccess()}nombre por ${user.name}")
                } else {
                    profileInteractor.onProfileUpdateFailure(Message.PROFILE_DATA.getMessageError())
                }
            }
        }
        if (user.email.isNotBlank()) {
            if (Email().isEmailValid(user.email)) {
                db.reference.child("/users/$uid").child("email").setValue(user.email)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            profileInteractor.onProfileUpdateSuccess("${Message.PROFILE_DATA.getMessageSuccess()}correo por ${user.email}")
                        } else {
                            profileInteractor.onProfileUpdateFailure(Message.PROFILE_DATA.getMessageError())
                        }
                    }
            } else {
                profileInteractor.onProfileUpdateFailure(Message.PROFILE_DATA.getMessageError())
            }
        }
    }

    /**
     * This method return the user information.
     */
    override fun getUserInfo() {
        val uid = auth.currentUser?.uid
        db.reference.child("users/$uid").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(response: DataSnapshot) {
                if (response.exists()) {
                     profileInteractor.onDataSuccess(response.getValue(User::class.java)!!)
                }
            }
        })
    }


}