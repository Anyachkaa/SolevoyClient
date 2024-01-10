package ru.itskekoff.client.module;

import ru.itskekoff.client.module.impl.render.ClickGui;
import ru.itskekoff.utils.ReflectionUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ModuleManager {

    public List<Module> modules = new ArrayList<>();

    public ModuleManager() {
        modules.addAll(ReflectionUtil.getClasses("ru.itskekoff.client.module.impl", Module.class));
    }

    public Module getModule(String name) {
        Module mod;
        for (Module m : this.modules) {
            if (m.getName().equalsIgnoreCase(name)) {
                return m;
            }
        }
        return null;
    }

    public List<Module> getModuleList() {
        return this.modules;
    }

    public List<Module> getModulesInCategory(Category c) {
        List<Module> mods = new CopyOnWriteArrayList<>();
        for (Module m : this.modules) {
            if (m.getCategory().name().equalsIgnoreCase(c.name())) {
                mods.add(m);
            }
        }
        return mods;
    }

    public Module getModuleByClass(Class<? extends Module> classModule) {
        for (Module module : modules) {
            if (module != null) {
                if (module.getClass() == classModule) {
                    return module;
                }
            }
        }
        return null;
    }
}
