package keystrokesmod.client.module;

import com.google.gson.JsonObject;
import keystrokesmod.client.clickgui.raven.Component;
import keystrokesmod.client.clickgui.raven.components.ModuleComponent;
import keystrokesmod.client.main.Raven;
import keystrokesmod.client.module.setting.Setting;
import keystrokesmod.client.module.setting.impl.ComboSetting;
import keystrokesmod.client.module.setting.impl.TickSetting;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;

public class Module {
    protected ArrayList<Setting> settings;
    private final String moduleName;
    private final ModuleCategory moduleCategory;
    protected boolean hasBind = true, showInHud = true, clientConfig, enabled;
    protected boolean defaultEnabled = enabled;
    protected int keycode;
    protected int defualtKeyCode = keycode;

    protected ModuleComponent component;

    protected static Minecraft mc;
    private boolean isToggled;

    private String description = "";

    public void guiUpdate() {

    }

    public Module(String name, ModuleCategory moduleCategory) {
        this.moduleName = name;
        this.moduleCategory = moduleCategory;
        this.settings = new ArrayList<>();
        mc = Minecraft.getMinecraft();
    }

    protected <E extends Module> E withKeycode(int i) {
        this.keycode = i;
        this.defualtKeyCode = i;
        return (E) this;
    }

    protected <E extends Module> E withEnabled(boolean i) {
        this.enabled = i;
        this.defaultEnabled = i;
        try {
            setToggled(i);
        } catch (Exception e) {
        }
        return (E) this;
    }

    public <E extends Module> E withDescription(String i) {
        this.description = i;
        return (E) this;
    }


    public JsonObject getConfigAsJson() {
        JsonObject settings = new JsonObject();

        for (Setting setting : this.settings) {
            if (setting != null) {
                JsonObject settingData = setting.getConfigAsJson();
                settings.add(setting.settingName, settingData);
            }
        }

        JsonObject data = new JsonObject();
        data.addProperty("enabled", enabled);
        if (hasBind) data.addProperty("keycode", keycode);
        data.addProperty("showInHud", showInHud);
        data.add("settings", settings);

        return data;
    }

    public void applyConfigFromJson(JsonObject data) {
        try {
            if (hasBind) this.keycode = data.get("keycode").getAsInt();
            setToggled(data.get("enabled").getAsBoolean());
            JsonObject settingsData = data.get("settings").getAsJsonObject();
            for (Setting setting : getSettings()) {
                if (settingsData.has(setting.getName())) {
                    setting.applyConfigFromJson(settingsData.get(setting.getName()).getAsJsonObject());
                }
            }
            this.showInHud = data.get("showInHud").getAsBoolean();
        } catch (NullPointerException ignored) {

        }
        postApplyConfig();
    }

    public void postApplyConfig() {

    }


    public void keybind() {
        if (this.keycode != 0 && this.canBeEnabled()) {
            if (!this.isToggled && Keyboard.isKeyDown(this.keycode)) {
                this.toggle();
                this.isToggled = true;
            } else if (!Keyboard.isKeyDown(this.keycode)) {
                this.isToggled = false;
            }
        }
    }


    public boolean canBeEnabled() {
        return true;
    }

    public boolean showInHud() {
        return showInHud;
    }


    public void enable() {
        this.enabled = true;
        this.onEnable();
        if (enabled) {
            Raven.eventBus.register(this);
        }
    }

    public void disable() {
        this.enabled = false;
        Raven.eventBus.unregister(this);
        this.onDisable();
    }

    public void setToggled(boolean enabled) {
        if (enabled) {
            enable();
        } else {
            disable();
        }
    }

    public boolean isBindable() {
        return hasBind;
    }


    public String getName() {
        return this.moduleName;
    }

    public ArrayList<Setting> getSettings() {
        return this.settings;
    }

    public Setting getSettingByName(String name) {
        for (Setting setting : this.settings) {
            if (setting.getName().equalsIgnoreCase(name))
                return setting;
        }
        return null;
    }

    public void registerSetting(Setting Setting) {
        this.settings.add(Setting);
    }

    public void setVisableInHud(boolean vis) {
        this.showInHud = vis;
    }


    public ModuleCategory moduleCategory() {
        return this.moduleCategory;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void onEnable() {
    }

    public void onDisable() {
    }

    public void toggle() {
        if (this.enabled) {
            this.disable();
        } else {
            this.enable();
        }
    }

    public void guiButtonToggled(TickSetting b) {
    }

    public void guiButtonToggled(ComboSetting b) {
    }

    public void guiButtonToggled(TickSetting b, Component c) {
    }

    public int getKeycode() {
        return this.keycode;
    }


    public void setbind(int keybind) {
        this.keycode = keybind;
    }

    public void resetToDefaults() {
        this.keycode = defualtKeyCode;
        this.setToggled(defaultEnabled);

        for (Setting setting : this.settings) {
            setting.resetToDefaults();
        }
    }

    public void setModuleComponent(ModuleComponent component) {
        this.component = component;
    }

    public void onGuiClose() {

    }

    public String getBindAsString() {
        return keycode == 0 ? "None" : Keyboard.getKeyName(keycode);
    }

    public void clearBinds() {
        this.keycode = 0;
    }

    public boolean isClientConfig() {
        return clientConfig;
    }


    public enum ModuleCategory {
        category(true, null, "Raven B++"),
        combat(false, category, "Combat"),
        movement(false, category, "Movement"),
        player(false, category, "Player"),
        world(false, category, "World"),
        render(false, category, "Render"),
        minigames(false, category, "Minigames"),
        other(false, category, "Other"),
        client(false, category, "Client"),
        hotkey(false, category, "Hotkey"),
        config(false, client, "Config"),
        sumo(false, minigames, "Sumo");

        private final boolean defaultShown;
        private final ModuleCategory topCatagory;
        private final String name;

        ModuleCategory(boolean defaultShown, ModuleCategory topCatagory, String name) {
            this.defaultShown = defaultShown;
            this.topCatagory = topCatagory;
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public boolean isShownByDefault() {
            return defaultShown;
        }

        public ModuleCategory getParentCategory() {
            return topCatagory;
        }
    }
}
