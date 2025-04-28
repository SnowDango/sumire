package com.snowdango.sumire

import com.airbnb.android.showkase.models.Showkase
import com.airbnb.android.showkase.models.ShowkaseBrowserComponent
import com.github.takahirom.roborazzi.DEFAULT_ROBORAZZI_OUTPUT_DIR_PATH
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(ParameterizedRobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel6)
class PreviewTest(
    private val showkaseBrowserComponent: ShowkaseBrowserComponent,
) {

    @Test
    fun previewScreenshot() {
        val componentName = showkaseBrowserComponent.componentName.replace(" ", "")
        val filePath =
            DEFAULT_ROBORAZZI_OUTPUT_DIR_PATH +
                "/" + showkaseBrowserComponent.group +
                "_" + componentName +
                "_" + showkaseBrowserComponent.componentKey +
                ".png"
        captureRoboImage(filePath) {
            showkaseBrowserComponent.component()
        }
    }

    companion object {
        @ParameterizedRobolectricTestRunner.Parameters
        @JvmStatic
        fun components(): Iterable<Array<Any?>> {
            return Showkase.getMetadata().componentList.map {
                arrayOf(it)
            }
        }
    }
}
