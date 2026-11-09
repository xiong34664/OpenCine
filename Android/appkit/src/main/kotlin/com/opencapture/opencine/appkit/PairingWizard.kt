package com.opencapture.opencine.appkit

import androidx.compose.runtime.Composable
import com.opencapture.monitorui.MonitorPairingCheck
import com.opencapture.monitorui.MonitorPairingCheckState
import com.opencapture.monitorui.MonitorPairingDevice
import com.opencapture.monitorui.MonitorPairingInstruction
import com.opencapture.monitorui.MonitorPairingInstructionIcon
import com.opencapture.monitorui.MonitorPairingPresentation
import com.opencapture.monitorui.MonitorPairingStep
import com.opencapture.opencine.core.ConnectTarget
import com.opencapture.opencine.core.ConnectionPhase
import com.opencapture.opencine.core.LocalVPNFilter

/**
 * The four-page pair wizard the camera apps showed before the shared pair page (Swift
 * `PairingWizard`): find, approve, join camera Wi-Fi, open the video link, with a body and cards per
 * page, a check list from the Wi-Fi page on, the connection's live label and a failure as the page's
 * error. The app gives its copy and what only it knows. On Android, [blocker] is the primary action
 * while discovery cannot run (Nearby devices, Settings or Bluetooth), and the app's callouts are the
 * flow's content.
 */
public data class PairingWizard(
    /** One body per page. */
    val bodies: List<String>,
    /** "On the camera" lines on the approve page and on the Wi-Fi page. */
    val approveLines: List<String>,
    val joinLines: List<String>,
    /** The card while choosing; null shows none. */
    val choosing: MonitorPairingInstruction? = null,
    /** The connection's live label, shown while it scans or connects. */
    val label: String? = null,
    val joinedNetwork: String? = null,
    val vpnActive: Boolean = false,
    val blocker: String? = null,
) {
    public companion object {
        internal val titles get() = listOf(
            AppkitContext.context.getString(R.string.appkit_find_camera),
            AppkitContext.context.getString(R.string.appkit_approve_on_camera),
            AppkitContext.context.getString(R.string.appkit_join_camera_wifi),
            AppkitContext.context.getString(R.string.appkit_open_video_link),
        )
        internal val rail get() = listOf(
            AppkitContext.context.getString(R.string.appkit_bluetooth_scan),
            AppkitContext.context.getString(R.string.appkit_camera_prompt),
            AppkitContext.context.getString(R.string.appkit_camera_network),
            AppkitContext.context.getString(R.string.appkit_video_link),
        )

        /**
         * The apps' live label for a connection phase (Swift `ConnectionPhase.label`). Reads each
         * integration's phase enum by its constant names, which match by design.
         */
        public fun phaseLabel(phase: Enum<*>): String = when (phase.name) {
            "SCANNING" -> AppkitContext.context.getString(R.string.appkit_scanning_for_camera)
            "CONNECTING_GATT" -> AppkitContext.context.getString(R.string.appkit_connecting_bluetooth)
            "PAIRING" -> AppkitContext.context.getString(R.string.appkit_pairing)
            "AWAITING_APPROVAL" -> AppkitContext.context.getString(R.string.appkit_approve_on_camera_screen)
            "READING_WIFI_CREDS" -> AppkitContext.context.getString(R.string.appkit_reading_wifi_credentials)
            "JOINING_WIFI" -> AppkitContext.context.getString(R.string.appkit_joining_camera_wifi)
            "OPENING_DATALINK" -> AppkitContext.context.getString(R.string.appkit_opening_datalink)
            "LIVE" -> AppkitContext.context.getString(R.string.appkit_connected)
            else -> AppkitContext.context.getString(R.string.appkit_idle)
        }

        /** A connect's scan (a first step with a live note, as both integrations report it) is still page 0. */
        internal fun page(attempt: CamerasModel.Attempt?): Int {
            attempt ?: return 0
            val progress = attempt.progress ?: return 1
            return when (progress.current) {
                0 -> if (progress.phase == ConnectionPhase.LINK && progress.note != null) 0 else 1
                1 -> 2
                else -> 3
            }
        }
    }

    /** The wizard with its actions: Continue connects the selection; [blocker] calls [onBlocker]; a failure's Try again goes back to choosing and calls [rescan]; Cancel stops the connect and calls [onDone]. */
    public fun flow(
        model: CamerasModel,
        onDone: () -> Unit,
        rescan: () -> Unit,
        onBlocker: () -> Unit = {},
        content: @Composable () -> Unit = {},
    ): IntegrationPairingFlow = IntegrationPairingFlow(
        presentation = CamerasMapping.wizard(model, this),
        onSelect = { id -> if (model.state.value.attempts.isEmpty()) model.select(id) },
        onPrimary = primary@{
            val state = model.state.value
            if (state.attempts.isNotEmpty()) return@primary
            val sighting = state.nearby.firstOrNull { it.id == state.selection }
            val failure = model.failureFor(state.selection)
            when {
                failure != null -> {
                    model.dismissFailure(failure.target.cameraID)
                    rescan()
                }
                blocker != null -> onBlocker()
                sighting != null -> model.connect(sighting)
            }
        },
        onBack = {
            model.cancelAll()
            onDone()
        },
        content = content,
    )
}

/** The selected camera's connect as the wizard shows it; any running attempt makes it busy (Swift `CamerasMapping.wizard`). */
public fun CamerasMapping.wizard(model: CamerasModel, wizard: PairingWizard): MonitorPairingPresentation {
    val state = model.state.value
    val selected = state.nearby.firstOrNull { it.id == state.selection }
    val cameraID = selected?.let { ConnectTarget.Nearby(it.camera).cameraID }
    val busy = state.attempts.isNotEmpty()
    val failed = if (busy) null else model.failureFor(state.selection)
    val failure = failed?.failure
    val page = if (failure != null) 0 else PairingWizard.page(cameraID?.let { state.attempts[it] })
    val choosing = page == 0
    val instructions = when (page) {
        0 -> listOfNotNull(wizard.choosing.takeIf { failure == null })
        1 -> listOf(
            MonitorPairingInstruction(AppkitContext.context.getString(R.string.appkit_on_the_camera), MonitorPairingInstructionIcon.CAMERA, wizard.approveLines),
            MonitorPairingInstruction(
                AppkitContext.context.getString(R.string.appkit_on_this_phone), MonitorPairingInstructionIcon.PHONE,
                listOf(
                    AppkitContext.context.getString(R.string.appkit_wait_here_bluetooth),
                    AppkitContext.context.getString(R.string.appkit_dont_force_quit),
                ),
            ),
        )
        2 -> listOf(
            MonitorPairingInstruction(AppkitContext.context.getString(R.string.appkit_on_the_camera), MonitorPairingInstructionIcon.CAMERA, wizard.joinLines),
            MonitorPairingInstruction(
                AppkitContext.context.getString(R.string.appkit_on_this_phone), MonitorPairingInstructionIcon.PHONE,
                listOf(AppkitContext.context.getString(R.string.appkit_tap_join), LocalVPNFilter.JOIN_WIFI_PHONE_STEP),
            ),
        )
        else -> if (wizard.vpnActive) {
            listOf(MonitorPairingInstruction(AppkitContext.context.getString(R.string.appkit_check_the_connection), MonitorPairingInstructionIcon.PHONE, listOf(LocalVPNFilter.WIZARD_BANNER)))
        } else {
            emptyList()
        }
    }
    val failedName = when (val target = failed?.target) {
        is ConnectTarget.Nearby -> target.camera.name
        is ConnectTarget.Saved -> target.camera.displayName
        is ConnectTarget.AddRoute -> target.camera.displayName
        null -> null
    }
    val name = selected?.let { device(it, state, model).name } ?: failedName
    val checks = if (page >= 2) {
        listOf(
            MonitorPairingCheck(
                AppkitContext.context.getString(R.string.appkit_bluetooth_link),
                name ?: AppkitContext.context.getString(R.string.appkit_camera_connected),
                MonitorPairingCheckState.COMPLETE,
                "OK",
            ),
            MonitorPairingCheck(
                AppkitContext.context.getString(R.string.appkit_camera_wifi),
                wizard.joinedNetwork ?: AppkitContext.context.getString(R.string.appkit_waiting_for_camera_network),
                if (page > 2) MonitorPairingCheckState.COMPLETE else MonitorPairingCheckState.ACTIVE,
                if (page > 2) "OK" else "JOINING",
            ),
            MonitorPairingCheck(
                AppkitContext.context.getString(R.string.appkit_live_picture),
                if (page > 2) AppkitContext.context.getString(R.string.appkit_opening_the_video_link) else AppkitContext.context.getString(R.string.appkit_starts_after_wifi_joins),
                if (page > 2) MonitorPairingCheckState.ACTIVE else MonitorPairingCheckState.WAITING,
                "WAITING",
            ),
        )
    } else {
        emptyList()
    }
    val hint = when (page) {
        0 -> name?.let { AppkitContext.context.getString(R.string.appkit_selected_camera, it) }
            ?: AppkitContext.context.getString(R.string.appkit_choose_camera_matches_screen)
        1 -> AppkitContext.context.getString(R.string.appkit_nothing_to_type)
        2 -> AppkitContext.context.getString(R.string.appkit_android_asks_join)
        else -> AppkitContext.context.getString(R.string.appkit_monitoring_opens)
    }
    val (primary, enabled) = when {
        failure != null -> AppkitContext.context.getString(R.string.appkit_try_again) to true
        wizard.blocker != null -> wizard.blocker to true
        choosing -> AppkitContext.context.getString(R.string.appkit_continue) to (selected != null && !busy)
        else -> null to false
    }
    return MonitorPairingPresentation(
        steps = PairingWizard.titles.zip(PairingWizard.rail) { title, rail -> MonitorPairingStep(title, rail) },
        currentStep = page,
        title = PairingWizard.titles[page],
        body = wizard.bodies[page],
        target = name ?: AppkitContext.context.getString(R.string.appkit_nothing_selected_yet),
        hint = hint,
        // A blocker means discovery is not running (main's scan never started).
        progress = wizard.label.takeIf { failure == null && (busy || state.scanning && wizard.blocker == null) },
        error = failure?.message,
        devices = if (choosing) {
            state.nearby.map {
                val item = device(it, state, model)
                MonitorPairingDevice(it.id, item.name, item.subtitle, selected = item.isPrimary, busy = busy, signalBars = item.signalBars)
            }
        } else {
            emptyList()
        },
        instructions = instructions,
        checks = checks,
        emptyTitle = if (choosing && state.nearby.isEmpty() && wizard.blocker == null) {
            if (state.scanning) AppkitContext.context.getString(R.string.appkit_looking_for_cameras)
            else AppkitContext.context.getString(R.string.appkit_no_cameras_yet)
        } else {
            null
        },
        primaryAction = primary,
        primaryActionEnabled = enabled,
        backAction = if (busy) AppkitContext.context.getString(R.string.appkit_cancel) else null,
    )
}
