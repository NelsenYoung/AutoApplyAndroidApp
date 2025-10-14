package com.example.autoapply.model
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class JobDetails(
    @StringRes val jobTitleResourceId: Int,
    @StringRes val locationResourceId: Int,
    @StringRes val payDetailsResourceId: Int,
    @StringRes val companyNameResourceId: Int,
    @DrawableRes val companyIconResourceId: Int,
)
