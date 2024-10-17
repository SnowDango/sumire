package com.snowdango.sumire.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable


@Composable
fun NotificationListenerDialog(
    isShow: Boolean,
    onDismissRequest: () -> Unit,
    onConfirmClick: () -> Unit,
){
    if(isShow) {
        AlertDialog(
            onDismissRequest = {},
            confirmButton = {
                TextButton(
                    onClick = {
                        onConfirmClick.invoke()
                        onDismissRequest.invoke()
                    }
                ) {
                    Text("設定へ移動")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onDismissRequest.invoke()
                    }
                ) {
                    Text("終了する")
                }
            },
            title = {
                Text(text = "通知読み込みの権限要求")
            },
            text = {
                Text(
                    text = """
                    このアプリは通知を読み込むことで再生している楽曲を取得しています。
                    権限を有効にしない場合、正常に動作しません。
                    また、ネットワークをしようし楽曲情報を取得していますが、再生している楽曲の取得に関しない情報は取得、外部への公開をすることはありません。
                """.trimIndent()
                )
            }
        )
    }
}