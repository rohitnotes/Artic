package com.aliumujib.artic.ui.main.settings.editprofile

import com.aliumujib.artic.models.user.User

sealed class EditProfileViewEvent {
    data class FullNameChange(val fullname: String) : EditProfileViewEvent()
    data class EmailChange(val email: String) : EditProfileViewEvent()
    object SaveProfileClick : EditProfileViewEvent()
}