package com.snowdango.sumire.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun NotificationPostDialog(
    isShow: Boolean,
    onDismissListener: () -> Unit,
    onConfirmClick: () -> Unit,
) {
    if (isShow) {
        AlertDialog(
            onDismissRequest = {
                onDismissListener.invoke()
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onConfirmClick.invoke()
                        onDismissListener.invoke()
                    },
                ) {
                    Text(text = "OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onDismissListener.invoke()
                    },
                ) {
                    Text("Cancel")
                }
            },
            title = {
                Text(text = "通知権限の要求")
            },
            text = {
                Text(
                    text = """
                        このアプリでは、特にユーザーに通知を発信することはありませんが、ウィジェットのエラー表示用に権限を取得する必要があります。
                        権限を与えずにウィジェットを使用することは可能ですが、楽曲データの取得に失敗してもエラー表示がされません。
                    """.trimIndent(),
                )
            },
        )
    }
}
