package me.phoenixra.atumvr.api;

import me.phoenixra.atumvr.api.devices.VRDevicesManager;
import me.phoenixra.atumvr.api.overlays.VROverlaysManager;
import org.jetbrains.annotations.NotNull;

public interface AtumVRAPI {

    @NotNull
    VRDevicesManager createVrDevicesManager(AtumVRCore vrCore);
    @NotNull
    VROverlaysManager createVrOverlaysManager(AtumVRCore vrCore);

    /**
     * Get the instance.
     *
     * @return The instance.
     */
    static AtumVRAPI getInstance() {
        return Instance.get();
    }

    final class Instance {
        private static AtumVRAPI api;
        private Instance() {
            throw new UnsupportedOperationException("This is an utility class and cannot be instantiated");
        }

        static void loadDefault(){
            try {
                Class<?> defaultClazz = Class.forName(
                        "me.phoenixra.atumvr.core.AtumVRAPIDefault"
                );
                api = (AtumVRAPI) defaultClazz.getConstructor().newInstance();
            }catch (Exception exception){
                throw new RuntimeException(exception);
            }
        }
        static void set(final AtumVRAPI api) {
            if(Instance.api != null) return;

            Instance.api = api;
        }


        static AtumVRAPI get() {
            return api;
        }


    }
}
