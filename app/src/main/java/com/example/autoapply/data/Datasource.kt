package com.example.autoapply.data

import com.example.autoapply.R
import com.example.autoapply.model.JobDetails

class Datasource {
    fun loadJobs(): List<JobDetails> {
        return listOf(
            JobDetails(
                R.string.software_engineer,
                R.string.san_francisco,
                R.string.pay_120k_150k,
                R.string.google,
                R.drawable.google_logo,
                R.array.google_questions,
            ),
            JobDetails(
                R.string.backend_engineer,
                R.string.seattle,
                R.string.pay_110k_140k,
                R.string.amazon,
                R.drawable.amazon_logo,
                R.array.amazon_questions
            ),
            JobDetails(
                R.string.mobile_developer,
                R.string.new_york,
                R.string.pay_100k_130k,
                R.string.spotify,
                R.drawable.spotify_logo,
                R.array.spotify_questions
            ),
            JobDetails(
                R.string.data_scientist,
                R.string.austin,
                R.string.pay_115k_145k,
                R.string.ibm,
                R.drawable.ibm_logo,
                R.array.ibm_questions
            ),
            JobDetails(
                R.string.frontend_engineer,
                R.string.remote,
                R.string.pay_105k_135k,
                R.string.shopify,
                R.drawable.shopify_logo,
                R.array.shopify_questions
            )
        )
    }
}
