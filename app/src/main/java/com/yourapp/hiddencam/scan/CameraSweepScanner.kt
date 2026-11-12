package com.yourapp.hiddencam.scan

import android.content.Context
import android.content.pm.PackageManager
import com.yourapp.hiddencam.fusion.Finding
import com.yourapp.hiddencam.fusion.SignalType

data class CameraSweepResult(
    val checked: Boolean,
    val flashAvailable: Boolean,
    val cameraAvailable: Boolean,
    val glintHint: String,
    val irHint: String,
    val findings: List<Finding>,
)

class AndroidCameraSweepScanner(
    private val context: Context,
) : CameraSweepScanner {
    override fun sweep(): CameraSweepResult {
        val packageManager = context.packageManager
        val cameraAvailable = packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
        val flashAvailable = packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)

        val findings = buildList {
            add(
                Finding(
                    id = "camera-sensor",
                    type = SignalType.GLINT,
                    title = if (cameraAvailable) "Camera hardware available" else "No rear/front camera found",
                    detail = if (cameraAvailable) {
                        "The phone has a camera sensor, so a glint/IR sweep can be attempted on-device."
                    } else {
                        "This device does not report a camera sensor, so the glint/IR check is unavailable."
                    },
                    weight = if (cameraAvailable) 12 else 0,
                ),
            )
            if (flashAvailable) {
                add(
                    Finding(
                        id = "torch-check",
                        type = SignalType.IR,
                        title = "Torch/flash present",
                        detail = "A torch is available, which helps the app check for reflective glints and IR-like bright points.",
                        weight = 10,
                    ),
                )
            }
        }

        return CameraSweepResult(
            checked = cameraAvailable || flashAvailable,
            flashAvailable = flashAvailable,
            cameraAvailable = cameraAvailable,
            glintHint = if (cameraAvailable) {
                "Camera sensor present; glint scan is allowed on this device."
            } else {
                "No camera sensor detected; glint check is not available."
            },
            irHint = if (flashAvailable) {
                "Flash/torch available; reflective and IR-like highlights can be probed."
            } else {
                "No flash hardware detected; IR-like sweep is limited."
            },
            findings = findings,
        )
    }
}
