package me.phoenixra.atumvr.api.events;

import lombok.Getter;
import org.lwjgl.openvr.VR;

import java.util.HashMap;

public enum VREvent {
    TRACKED_DEVICE_ACTIVATED(VR.EVREventType_VREvent_TrackedDeviceActivated),
    TRACKED_DEVICE_DEACTIVATED(VR.EVREventType_VREvent_TrackedDeviceDeactivated),
    TRACKED_DEVICE_UPDATED(VR.EVREventType_VREvent_TrackedDeviceUpdated),
    DEVICE_USER_INTERACTION_STARTED(VR.EVREventType_VREvent_TrackedDeviceUserInteractionStarted),
    DEVICE_USER_INTERACTION_ENDED(VR.EVREventType_VREvent_TrackedDeviceUserInteractionEnded),
    IPD_CHANGED(VR.EVREventType_VREvent_IpdChanged),
    ENTER_STAND_BY_MODE(VR.EVREventType_VREvent_EnterStandbyMode),
    LEAVE_STAND_BY_MODE(VR.EVREventType_VREvent_LeaveStandbyMode),
    TRACKED_DEVICE_ROLE_CHANGED(VR.EVREventType_VREvent_TrackedDeviceRoleChanged),
    WATCHDOG_WAKE_UP_REQUESTED(VR.EVREventType_VREvent_WatchdogWakeUpRequested),
    LENS_DISTORTION_CHANGED(VR.EVREventType_VREvent_LensDistortionChanged),
    PROPERTY_CHANGED(VR.EVREventType_VREvent_PropertyChanged),
    WIRELESS_DISCONNECT(VR.EVREventType_VREvent_WirelessDisconnect),
    WIRELESS_RECONNECT(VR.EVREventType_VREvent_WirelessReconnect),
    BUTTON_PRESS(VR.EVREventType_VREvent_ButtonPress),
    BUTTON_UNPRESS(VR.EVREventType_VREvent_ButtonUnpress),
    BUTTON_TOUCH(VR.EVREventType_VREvent_ButtonTouch),
    BUTTON_UNTOUCH(VR.EVREventType_VREvent_ButtonUntouch),
    MODAL_CANCEL(VR.EVREventType_VREvent_Modal_Cancel),
    MOUSE_MOVE(VR.EVREventType_VREvent_MouseMove),
    MOUSE_BUTTON_DOWN(VR.EVREventType_VREvent_MouseButtonDown),
    MOUSE_BUTTON_UP(VR.EVREventType_VREvent_MouseButtonUp),
    FOCUS_ENTER(VR.EVREventType_VREvent_FocusEnter),
    FOCUS_LEAVE(VR.EVREventType_VREvent_FocusLeave),
    SCROLL_DISCRETE(VR.EVREventType_VREvent_ScrollDiscrete),
    TOUCH_PAD_MOVE(VR.EVREventType_VREvent_TouchPadMove),
    OVERLAY_FOCUS_CHANGED(VR.EVREventType_VREvent_OverlayFocusChanged),
    RELOAD_OVERLAYS(VR.EVREventType_VREvent_ReloadOverlays),
    SCROLL_SMOOTH(VR.EVREventType_VREvent_ScrollSmooth),
    LOCK_MOUSE_POSITION(VR.EVREventType_VREvent_LockMousePosition),
    UNLOCK_MOUSE_POSITION(VR.EVREventType_VREvent_UnlockMousePosition),
    INPUT_FOCUS_CAPTURED(VR.EVREventType_VREvent_InputFocusCaptured),
    INPUT_FOCUS_RELEASED(VR.EVREventType_VREvent_InputFocusReleased),
    SCENE_APPLICATION_CHANGED(VR.EVREventType_VREvent_SceneApplicationChanged),
    INPUT_FOCUS_CHANGED(VR.EVREventType_VREvent_InputFocusChanged),
    SCENE_APPLICATION_USING_WRONG_GRAPHICS_ADAPTER(VR.EVREventType_VREvent_SceneApplicationUsingWrongGraphicsAdapter),
    ACTION_BINDING_RELOADED(VR.EVREventType_VREvent_ActionBindingReloaded),
    HIDE_RENDER_MODELS(VR.EVREventType_VREvent_HideRenderModels),
    SHOW_RENDER_MODELS(VR.EVREventType_VREvent_ShowRenderModels),
    SCENE_APPLICATION_STATE_CHANGED(VR.EVREventType_VREvent_SceneApplicationStateChanged),
    SCENE_APP_PIPE_DISCONNECTED(VR.EVREventType_VREvent_SceneAppPipeDisconnected),
    CONSOLE_OPENED(VR.EVREventType_VREvent_ConsoleOpened),
    CONSOLE_CLOSED(VR.EVREventType_VREvent_ConsoleClosed),
    OVERLAY_SHOWN(VR.EVREventType_VREvent_OverlayShown),
    OVERLAY_HIDDEN(VR.EVREventType_VREvent_OverlayHidden),
    DASHBOARD_ACTIVATED(VR.EVREventType_VREvent_DashboardActivated),
    DASHBOARD_DEACTIVATED(VR.EVREventType_VREvent_DashboardDeactivated),
    DASHBOARD_REQUESTED(VR.EVREventType_VREvent_DashboardRequested),
    RESET_DASHBOARD(VR.EVREventType_VREvent_ResetDashboard),
    IMAGE_LOADED(VR.EVREventType_VREvent_ImageLoaded),
    SHOW_KEYBOARD(VR.EVREventType_VREvent_ShowKeyboard),
    HIDE_KEYBOARD(VR.EVREventType_VREvent_HideKeyboard),
    OVERLAY_GAMEPAD_FOCUS_GAINED(VR.EVREventType_VREvent_OverlayGamepadFocusGained),
    OVERLAY_GAMEPAD_FOCUS_LOST(VR.EVREventType_VREvent_OverlayGamepadFocusLost),
    OVERLAY_SHARED_TEXTURE_CHANGED(VR.EVREventType_VREvent_OverlaySharedTextureChanged),
    SCREENSHOT_TRIGGERED(VR.EVREventType_VREvent_ScreenshotTriggered),
    IMAGE_FAILED(VR.EVREventType_VREvent_ImageFailed),
    DASHBOARD_OVERLAY_CREATED(VR.EVREventType_VREvent_DashboardOverlayCreated),
    SWITCH_GAMEPAD_FOCUS(VR.EVREventType_VREvent_SwitchGamepadFocus),
    REQUEST_SCREENSHOT(VR.EVREventType_VREvent_RequestScreenshot),
    SCREENSHOT_TAKEN(VR.EVREventType_VREvent_ScreenshotTaken),
    SCREENSHOT_FAILED(VR.EVREventType_VREvent_ScreenshotFailed),
    SUBMIT_SCREENSHOT_TO_DASHBOARD(VR.EVREventType_VREvent_SubmitScreenshotToDashboard),
    SCREENSHOT_PROGRESS_TO_DASHBOARD(VR.EVREventType_VREvent_ScreenshotProgressToDashboard),
    PRIMARY_DASHBOARD_DEVICE_CHANGED(VR.EVREventType_VREvent_PrimaryDashboardDeviceChanged),
    ROOM_VIEW_SHOWN(VR.EVREventType_VREvent_RoomViewShown),
    ROOM_VIEW_HIDDEN(VR.EVREventType_VREvent_RoomViewHidden),
    SHOW_UI(VR.EVREventType_VREvent_ShowUI),
    SHOW_DEV_TOOLS(VR.EVREventType_VREvent_ShowDevTools),
    DESKTOP_VIEW_UPDATING(VR.EVREventType_VREvent_DesktopViewUpdating),
    DESKTOP_VIEW_READY(VR.EVREventType_VREvent_DesktopViewReady),
    START_DASHBOARD(VR.EVREventType_VREvent_StartDashboard),
    ELEVATE_PRISM(VR.EVREventType_VREvent_ElevatePrism),
    OVERLAY_CLOSED(VR.EVREventType_VREvent_OverlayClosed),
    DASHBOARD_THUMB_CHANGED(VR.EVREventType_VREvent_DashboardThumbChanged),
    NOTIFICATION_SHOWN(VR.EVREventType_VREvent_Notification_Shown),
    NOTIFICATION_HIDDEN(VR.EVREventType_VREvent_Notification_Hidden),
    NOTIFICATION_BEGIN_INTERACTION(VR.EVREventType_VREvent_Notification_BeginInteraction),
    NOTIFICATION_DESTROYED(VR.EVREventType_VREvent_Notification_Destroyed),
    QUIT(VR.EVREventType_VREvent_Quit),
    PROCESS_QUIT(VR.EVREventType_VREvent_ProcessQuit),
    QUIT_ACKNOWLEDGED(VR.EVREventType_VREvent_QuitAcknowledged),
    DRIVER_REQUESTED_QUIT(VR.EVREventType_VREvent_DriverRequestedQuit),
    RESTART_REQUESTED(VR.EVREventType_VREvent_RestartRequested),
    INVALIDATE_SWAP_TEXTURE_SETS(VR.EVREventType_VREvent_InvalidateSwapTextureSets),
    CHAPERONE_DATA_HAS_CHANGED(VR.EVREventType_VREvent_ChaperoneDataHasChanged),
    CHAPERONE_UNIVERSE_HAS_CHANGED(VR.EVREventType_VREvent_ChaperoneUniverseHasChanged),
    CHAPERONE_TEMP_DATA_HAS_CHANGED(VR.EVREventType_VREvent_ChaperoneTempDataHasChanged),
    CHAPERONE_SETTINGS_HAVE_CHANGED(VR.EVREventType_VREvent_ChaperoneSettingsHaveChanged),
    SEATED_ZERO_POSE_RESET(VR.EVREventType_VREvent_SeatedZeroPoseReset),
    CHAPERONE_FLUSH_CACHE(VR.EVREventType_VREvent_ChaperoneFlushCache),
    CHAPERONE_ROOM_SETUP_STARTING(VR.EVREventType_VREvent_ChaperoneRoomSetupStarting),
    CHAPERONE_ROOM_SETUP_FINISHED(VR.EVREventType_VREvent_ChaperoneRoomSetupFinished),
    STANDING_ZERO_POSE_RESET(VR.EVREventType_VREvent_StandingZeroPoseReset),
    AUDIO_SETTINGS_HAVE_CHANGED(VR.EVREventType_VREvent_AudioSettingsHaveChanged),
    BACKGROUND_SETTING_HAS_CHANGED(VR.EVREventType_VREvent_BackgroundSettingHasChanged),
    CAMERA_SETTINGS_HAVE_CHANGED(VR.EVREventType_VREvent_CameraSettingsHaveChanged),
    REPROJECTION_SETTING_HAS_CHANGED(VR.EVREventType_VREvent_ReprojectionSettingHasChanged),
    MODEL_SKIN_SETTINGS_HAVE_CHANGED(VR.EVREventType_VREvent_ModelSkinSettingsHaveChanged),
    ENVIRONMENT_SETTINGS_HAVE_CHANGED(VR.EVREventType_VREvent_EnvironmentSettingsHaveChanged),
    POWER_SETTINGS_HAVE_CHANGED(VR.EVREventType_VREvent_PowerSettingsHaveChanged),
    ENABLE_HOME_APP_SETTINGS_HAVE_CHANGED(VR.EVREventType_VREvent_EnableHomeAppSettingsHaveChanged),
    STEAMVR_SECTION_SETTING_CHANGED(VR.EVREventType_VREvent_SteamVRSectionSettingChanged),
    LIGHTHOUSE_SECTION_SETTING_CHANGED(VR.EVREventType_VREvent_LighthouseSectionSettingChanged),
    NULL_SECTION_SETTING_CHANGED(VR.EVREventType_VREvent_NullSectionSettingChanged),
    USER_INTERFACE_SECTION_SETTING_CHANGED(VR.EVREventType_VREvent_UserInterfaceSectionSettingChanged),
    NOTIFICATIONS_SECTION_SETTING_CHANGED(VR.EVREventType_VREvent_NotificationsSectionSettingChanged),
    KEYBOARD_SECTION_SETTING_CHANGED(VR.EVREventType_VREvent_KeyboardSectionSettingChanged),
    PERF_SECTION_SETTING_CHANGED(VR.EVREventType_VREvent_PerfSectionSettingChanged),
    DASHBOARD_SECTION_SETTING_CHANGED(VR.EVREventType_VREvent_DashboardSectionSettingChanged),
    WEB_INTERFACE_SECTION_SETTING_CHANGED(VR.EVREventType_VREvent_WebInterfaceSectionSettingChanged),
    TRACKERS_SECTION_SETTING_CHANGED(VR.EVREventType_VREvent_TrackersSectionSettingChanged),
    LAST_KNOWN_SECTION_SETTING_CHANGED(VR.EVREventType_VREvent_LastKnownSectionSettingChanged),
    DISMISSED_WARNINGS_SECTION_SETTING_CHANGED(VR.EVREventType_VREvent_DismissedWarningsSectionSettingChanged),
    GPU_SPEED_SECTION_SETTING_CHANGED(VR.EVREventType_VREvent_GpuSpeedSectionSettingChanged),
    WINDOWS_MR_SECTION_SETTING_CHANGED(VR.EVREventType_VREvent_WindowsMRSectionSettingChanged),
    OTHER_SECTION_SETTING_CHANGED(VR.EVREventType_VREvent_OtherSectionSettingChanged),
    ANY_DRIVER_SETTINGS_CHANGED(VR.EVREventType_VREvent_AnyDriverSettingsChanged),
    STATUS_UPDATE(VR.EVREventType_VREvent_StatusUpdate),
    WEB_INTERFACE_INSTALL_DRIVER_COMPLETED(VR.EVREventType_VREvent_WebInterface_InstallDriverCompleted),
    MC_IMAGE_UPDATED(VR.EVREventType_VREvent_MCImageUpdated),
    FIRMWARE_UPDATE_STARTED(VR.EVREventType_VREvent_FirmwareUpdateStarted),
    FIRMWARE_UPDATE_FINISHED(VR.EVREventType_VREvent_FirmwareUpdateFinished),
    KEYBOARD_CLOSED(VR.EVREventType_VREvent_KeyboardClosed),
    KEYBOARD_CHAR_INPUT(VR.EVREventType_VREvent_KeyboardCharInput),
    KEYBOARD_DONE(VR.EVREventType_VREvent_KeyboardDone),
    KEYBOARD_OPENED_GLOBAL(VR.EVREventType_VREvent_KeyboardOpened_Global),
    KEYBOARD_CLOSED_GLOBAL(VR.EVREventType_VREvent_KeyboardClosed_Global),
    APPLICATION_LIST_UPDATED(VR.EVREventType_VREvent_ApplicationListUpdated),
    APPLICATION_MIME_TYPE_LOAD(VR.EVREventType_VREvent_ApplicationMimeTypeLoad),
    PROCESS_CONNECTED(VR.EVREventType_VREvent_ProcessConnected),
    PROCESS_DISCONNECTED(VR.EVREventType_VREvent_ProcessDisconnected),
    COMPOSITOR_CHAPERONE_BOUNDS_SHOWN(VR.EVREventType_VREvent_Compositor_ChaperoneBoundsShown),
    COMPOSITOR_CHAPERONE_BOUNDS_HIDDEN(VR.EVREventType_VREvent_Compositor_ChaperoneBoundsHidden),
    COMPOSITOR_DISPLAY_DISCONNECTED(VR.EVREventType_VREvent_Compositor_DisplayDisconnected),
    COMPOSITOR_DISPLAY_RECONNECTED(VR.EVREventType_VREvent_Compositor_DisplayReconnected),
    COMPOSITOR_HDCP_ERROR(VR.EVREventType_VREvent_Compositor_HDCPError),
    COMPOSITOR_APPLICATION_NOT_RESPONDING(VR.EVREventType_VREvent_Compositor_ApplicationNotResponding),
    COMPOSITOR_APPLICATION_RESUMED(VR.EVREventType_VREvent_Compositor_ApplicationResumed),
    COMPOSITOR_OUT_OF_VIDEO_MEMORY(VR.EVREventType_VREvent_Compositor_OutOfVideoMemory),
    COMPOSITOR_DISPLAY_MODE_NOT_SUPPORTED(VR.EVREventType_VREvent_Compositor_DisplayModeNotSupported),
    COMPOSITOR_STAGE_OVERRIDE_READY(VR.EVREventType_VREvent_Compositor_StageOverrideReady),
    COMPOSITOR_REQUEST_DISCONNECT_RECONNECT(VR.EVREventType_VREvent_Compositor_RequestDisconnectReconnect),
    TRACKED_CAMERA_START_VIDEO_STREAM(VR.EVREventType_VREvent_TrackedCamera_StartVideoStream),
    TRACKED_CAMERA_STOP_VIDEO_STREAM(VR.EVREventType_VREvent_TrackedCamera_StopVideoStream),
    TRACKED_CAMERA_PAUSE_VIDEO_STREAM(VR.EVREventType_VREvent_TrackedCamera_PauseVideoStream),
    TRACKED_CAMERA_RESUME_VIDEO_STREAM(VR.EVREventType_VREvent_TrackedCamera_ResumeVideoStream),
    TRACKED_CAMERA_EDITING_SURFACE(VR.EVREventType_VREvent_TrackedCamera_EditingSurface),
    PERFORMANCE_TEST_ENABLE_CAPTURE(VR.EVREventType_VREvent_PerformanceTest_EnableCapture),
    PERFORMANCE_TEST_DISABLE_CAPTURE(VR.EVREventType_VREvent_PerformanceTest_DisableCapture),
    PERFORMANCE_TEST_FIDELITY_LEVEL(VR.EVREventType_VREvent_PerformanceTest_FidelityLevel),
    MESSAGE_OVERLAY_CLOSED(VR.EVREventType_VREvent_MessageOverlay_Closed),
    MESSAGE_OVERLAY_CLOSE_REQUESTED(VR.EVREventType_VREvent_MessageOverlayCloseRequested),
    INPUT_HAPTIC_VIBRATION(VR.EVREventType_VREvent_Input_HapticVibration),
    INPUT_BINDING_LOAD_FAILED(VR.EVREventType_VREvent_Input_BindingLoadFailed),
    INPUT_BINDING_LOAD_SUCCESSFUL(VR.EVREventType_VREvent_Input_BindingLoadSuccessful),
    INPUT_ACTION_MANIFEST_RELOADED(VR.EVREventType_VREvent_Input_ActionManifestReloaded),
    INPUT_ACTION_MANIFEST_LOAD_FAILED(VR.EVREventType_VREvent_Input_ActionManifestLoadFailed),
    INPUT_PROGRESS_UPDATE(VR.EVREventType_VREvent_Input_ProgressUpdate),
    INPUT_TRACKER_ACTIVATED(VR.EVREventType_VREvent_Input_TrackerActivated),
    INPUT_BINDINGS_UPDATED(VR.EVREventType_VREvent_Input_BindingsUpdated),
    INPUT_BINDING_SUBSCRIPTION_CHANGED(VR.EVREventType_VREvent_Input_BindingSubscriptionChanged),
    SPATIAL_ANCHORS_POSE_UPDATED(VR.EVREventType_VREvent_SpatialAnchors_PoseUpdated),
    SPATIAL_ANCHORS_DESCRIPTOR_UPDATED(VR.EVREventType_VREvent_SpatialAnchors_DescriptorUpdated),
    SPATIAL_ANCHORS_REQUEST_POSE_UPDATE(VR.EVREventType_VREvent_SpatialAnchors_RequestPoseUpdate),
    SPATIAL_ANCHORS_REQUEST_DESCRIPTOR_UPDATE(VR.EVREventType_VREvent_SpatialAnchors_RequestDescriptorUpdate),
    SYSTEM_REPORT_STARTED(VR.EVREventType_VREvent_SystemReport_Started),
    MONITOR_SHOW_HEADSET_VIEW(VR.EVREventType_VREvent_Monitor_ShowHeadsetView),
    MONITOR_HIDE_HEADSET_VIEW(VR.EVREventType_VREvent_Monitor_HideHeadsetView),
    VENDOR_SPECIFIC_RESERVED_START(VR.EVREventType_VREvent_VendorSpecific_Reserved_Start),
    VENDOR_SPECIFIC_RESERVED_END(VR.EVREventType_VREvent_VendorSpecific_Reserved_End);


    @Getter
    private final int id;

    //faster get with hashmap. (too mane enums, affects on perfomance if use stream())
    private static HashMap<Integer, VREvent> events = new HashMap<>();


    VREvent(int id){
        this.id = id;
    }

    public static VREvent fromId(int id){
        if(events.isEmpty()){
            for(VREvent event : values()){
                events.put(event.id,event);
            }
        }
        return events.get(id);
    }

}
