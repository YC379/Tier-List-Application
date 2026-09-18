package com.application.sae201;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;


public final class AppPaths {

    private static final String SAVES_DIR_NAME = "saves";
    private static final String PRESETS_DIR_NAME = "presets";

    private AppPaths() {
    }

    public static File getSavesDir() {
        return ensureExists(resolveAppDir(SAVES_DIR_NAME));
    }

    public static File getPresetsDir() {
        return ensureExists(resolveAppDir(PRESETS_DIR_NAME));
    }

    public static File getPresetFile(String fileName) {
        return new File(getPresetsDir(), fileName);
    }

    private static File ensureExists(File dir) {
        if (!dir.exists() && !dir.mkdirs()) {
            AppLogger.warn("Impossible de créer le dossier : " + dir.getAbsolutePath());
        }
        return dir;
    }

    private static File resolveAppDir(String name) {
        File appBase = getApplicationBaseDir();
        if (appBase != null) {
            File candidate = new File(appBase, name);
            if (candidate.isDirectory()) {
                return candidate;
            }
        }

        File cwdCandidate = new File(name);
        if (cwdCandidate.isDirectory()) {
            return cwdCandidate;
        }

        return appBase != null ? new File(appBase, name) : cwdCandidate;
    }

    private static File getApplicationBaseDir() {
        try {
            Path codeSourcePath = Paths.get(
                    AppPaths.class.getProtectionDomain().getCodeSource().getLocation().toURI());


            File location = codeSourcePath.toFile();
            File base = location.isFile() ? location.getParentFile() : location;

            if (base != null && base.getName().equals("classes")
                    && base.getParentFile() != null && base.getParentFile().getName().equals("target")) {
                base = base.getParentFile().getParentFile();
            }

            return base;
        } catch (URISyntaxException | NullPointerException | SecurityException e) {
            AppLogger.warn("Impossible de déterminer le dossier d'installation de l'application, "
                    + "utilisation du répertoire courant à la place.");
            return null;
        }
    }
}
