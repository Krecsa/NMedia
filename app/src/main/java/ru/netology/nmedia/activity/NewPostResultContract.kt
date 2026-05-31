package ru.netology.nmedia.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract

class NewPostResultContract : ActivityResultContract<Long, Pair<Long, String>?>() {

    override fun createIntent(context: Context, input: Long): Intent =
        Intent(context, NewPostActivity::class.java).putExtra(EXTRA_POST_ID, input)

    override fun parseResult(resultCode: Int, intent: Intent?): Pair<Long, String>? =
        if (resultCode == Activity.RESULT_OK) {
            val id = intent?.getLongExtra(EXTRA_POST_ID, 0L) ?: 0L
            val text = intent?.getStringExtra(Intent.EXTRA_TEXT) ?: return null
            Pair(id, text)
        } else null

    companion object {
        const val EXTRA_POST_ID = "post_id"
    }
}