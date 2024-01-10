package ru.itskekoff.bots.macro;

import lombok.Data;

import java.io.*;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public @Data class MacroManager {
    private List<Macro> macros = new CopyOnWriteArrayList<>();
    private Macro currentMacro;
    private MacroRecord currentMacroRecord;
    private boolean macro = false, macroRecord = false;
    private int macroDelay = 200;

    public MacroManager() {
    }

    public void init() throws IOException {
        if (new File("./SolevoyClient/macro").exists()) {
            for (File entry : Objects.requireNonNull(new File("./SolevoyClient/macro").listFiles())) {
                if (entry.getName().endsWith(".mcr")) {
                    Macro macro1 = (Macro) readObjectFromFile(entry.getAbsolutePath());
                    if (!macros.contains(macro1)) macros.add(macro1);
                }
            }
        }
    }

    public void saveAsFile(Macro macro) {
        macro.setName(macro.getName().replaceAll("[^a-zA-Z\\d_-]", ""));
        File macroFile = new File("./SolevoyClient/macro/" + macro.getName() + ".mcr");
        File macroDir = new File("./SolevoyClient/macro");
        if (!macroDir.exists()) {
            macroDir.mkdirs();
        }
        writeObjectToFile(macroFile.getAbsolutePath(), macro);
    }

    public void removeMacro(Macro macroToRemove) {
        File macroFile = new File("./SolevoyClient/macro/" + macroToRemove.getName() + ".mcr");
        if (macroFile.exists()) {
            macroFile.delete();
        }
        macros.remove(macroToRemove);
    }

    public Macro getMacroFromString(String macroName) {
        Macro returnMacro = null;
        for (Macro macro1 : macros) {
            if (macro1.getName().equalsIgnoreCase(macroName)) {
                returnMacro = macro1;
            }
        }
        return returnMacro;
    }

    private Object readObjectFromFile(String filepath) {
        try {
            FileInputStream fileIn = new FileInputStream(filepath);
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);
            Object obj = objectIn.readObject();
            objectIn.close();
            return obj;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private void writeObjectToFile(String filepath, Object serObj) {
        try {
            FileOutputStream fileOut = new FileOutputStream(filepath);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(serObj);
            objectOut.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
