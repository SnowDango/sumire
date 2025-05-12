package com.snowdango.sumire

import android.content.Context
import com.airbnb.android.showkase.annotation.ShowkaseRoot
import com.airbnb.android.showkase.annotation.ShowkaseRootModule
import com.airbnb.android.showkase.models.Showkase

@ShowkaseRoot
class ShowkaseModule : ShowkaseRootModule


fun startShowkase(context: Context) {
    val intent = Showkase.getBrowserIntent(context)
    context.startActivity(intent)
}
