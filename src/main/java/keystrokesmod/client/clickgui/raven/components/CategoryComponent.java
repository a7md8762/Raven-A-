package keystrokesmod.client.clickgui.raven.components;

import keystrokesmod.client.clickgui.raven.Component;
import keystrokesmod.client.main.Raven;
import keystrokesmod.client.module.Module;
import keystrokesmod.client.module.modules.client.GuiModule;
import keystrokesmod.client.utils.font.FontUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;

public class CategoryComponent {
    public ArrayList<Component> modulesInCategory = new ArrayList<>();
    public ArrayList<Component> moduleInCategory = new ArrayList<>();
    public Module.ModuleCategory categoryName;
    public boolean moduleOpened, categoryOpened, inUse;
    private int width, x, y;
    private final int bh;
    public int xx, yy, chromaSpeed;
    public boolean n4m = false, pin = false;
    public String pvp;
    private final double marginY;
    private final double marginX;
    private boolean visable = true;

    public CategoryComponent(Module.ModuleCategory category) {
        this.categoryName = category;
        this.width = 92;
        this.x = 5;
        this.y = 5;
        this.bh = 13;
        this.xx = 0;
        this.categoryOpened = false;
        this.inUse = false;
        this.chromaSpeed = 3;
        this.marginX = 80;
        this.marginY = 4.5;
        loadAllModules();
    }

    public void loadAllModules() {
        this.modulesInCategory.clear();
        int tY = this.bh + 3;
        for (Iterator<Module> var3 = Raven.moduleManager.getModulesInCategory(this.categoryName).iterator(); var3.hasNext(); tY += 16) {
            Module mod = var3.next();
            ModuleComponent b = new ModuleComponent(mod, this, tY);
            this.modulesInCategory.add(b);
        }
        moduleOpened = false;
    }

    public void loadSpecificModule(Module module) {
        ModuleComponent component;
        ArrayList<Component> newModules = new ArrayList<>();
        for (Component c : modulesInCategory) {
            if (c instanceof ModuleComponent) {
                ModuleComponent e = (ModuleComponent) c;
                if (e.mod == module) {
                    component = (ModuleComponent) c;
                    newModules.add(component);
                    this.moduleInCategory = newModules;
                    moduleOpened = true;
                    return;
                }
            }
        }
    }

    public ArrayList<Component> getModules() {
        return moduleOpened ? moduleInCategory : this.modulesInCategory;
    }

    public void setX(int n) {
        this.x = n;
        if (Raven.clientConfig != null) {
            Raven.clientConfig.saveConfig();
        }
    }

    public void setY(int y) {
        this.y = y;
        if (Raven.clientConfig != null) {
            Raven.clientConfig.saveConfig();
        }
    }

    public void mousePressed(boolean d) {
        this.inUse = d;
    }

    public boolean p() {
        return this.pin;
    }

    public void cv(boolean on) {
        this.pin = on;
    }

    public boolean isOpened() {
        return this.categoryOpened;
    }

    public void setOpened(boolean on) {
        this.categoryOpened = on;

        if (Raven.clientConfig != null) {
            Raven.clientConfig.saveConfig();
        }
    }

    public void rf() {
        Minecraft mc = Minecraft.getMinecraft();
        if (!visable)
            return;
        this.width = 92;
        if (!this.getModules().isEmpty() && this.categoryOpened) {
            int categoryHeight = 0;

            Component moduleRenderManager;
            for (Iterator moduleInCategoryIterator = this.getModules().iterator(); moduleInCategoryIterator.hasNext(); categoryHeight += moduleRenderManager.getHeight()) {
                moduleRenderManager = (Component) moduleInCategoryIterator.next();
            }

            //drawing the background for every module in the category
            Color bgColor = moduleOpened ? (new Color(GuiModule.settingBackgroundRGB.getRed(), GuiModule.settingBackgroundRGB.getGreen(), GuiModule.settingBackgroundRGB.getBlue(),
                    (int) (GuiModule.backgroundOpacity.getInput() / 100 * 255))) : (new Color(GuiModule.backgroundRGB.getRed(), GuiModule.backgroundRGB.getGreen(),
                    GuiModule.backgroundRGB.getBlue(), (int) (GuiModule.backgroundOpacity.getInput() / 100 * 255)));

            net.minecraft.client.gui.Gui.drawRect(this.x - 1, this.y, this.x + this.width + 1, this.y + this.bh + categoryHeight + 4, bgColor.getRGB());
            // 1000000 character lines make me want to kill myself
        }

        if (GuiModule.categoryBackground.isToggled()) { // any reason for this to be gl fuckery instead of a drawrect except making code look broken?
            Gui.drawRect((this.x - 2), this.y, (this.x + this.width + 2), (this.y + this.bh + 3),
                    GuiModule.categoryBackgroundRGB.getRGB());
            GlStateManager.resetColor();
        }

        // category name
        int colorCN;

        switch ((GuiModule.CNColor) GuiModule.cnColor.getMode()) {

            case STATIC:
                colorCN = GuiModule.categoryNameRGB.getRGB();
                break;

            case RAINBOW:
                colorCN = Color.getHSBColor((float) (System.currentTimeMillis() % (7500L / (long) this.chromaSpeed)) / (7500.0F / (float) this.chromaSpeed), 1.0F, 1.0F).getRGB();
                break;

            default:
                throw new RuntimeException("if this happens, im coming to your house (you broke my code)");

        }

        if (GuiModule.useCustomFont.isToggled()) {
            FontUtil.two.drawSmoothString(this.n4m ? this.pvp : this.categoryName.getName(), (float) (this.x + 2), (float) (this.y + 4), colorCN);
        } else {
            mc.fontRendererObj.drawString(this.n4m ? this.pvp : this.categoryName.getName(), (float) (this.x + 2), (float) (this.y + 4), colorCN, false);
        }

        if (!this.n4m) {
            GL11.glPushMatrix();
            //Opened/closed unicode... :yes: :holsum: :evil:
            mc.fontRendererObj.drawString(this.categoryOpened ? "-" : "+", (float) (this.x + marginX), (float) ((double) this.y + marginY), Color.white.getRGB(), false);
            GL11.glPopMatrix();
            if (this.categoryOpened && !this.getModules().isEmpty()) {
                Iterator var5 = this.getModules().iterator();

                while (var5.hasNext()) {
                    Component c2 = (Component) var5.next();
                    c2.draw();
                }
            }

        }
    }

    public void updateModules() {
        int tY = this.bh + 3;
        this.modulesInCategory.clear();
        for (Iterator<Module> var3 = Raven.moduleManager.getModulesInCategory(this.categoryName).iterator(); var3.hasNext(); tY += 16) {
            Module mod = var3.next();
            ModuleComponent b = new ModuleComponent(mod, this, tY);
            this.modulesInCategory.add(b);
        }
    }

    public void r3nd3r() {
        int o = this.bh + 3;

        Component c;
        ModuleComponent d;
        for (Iterator var2 = this.getModules().iterator(); var2.hasNext(); o += c.getHeight()) {
            c = (Component) var2.next();
            c.setComponentStartAt(o);
        }

    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getWidth() {
        return this.width;
    }

    public void up(int x, int y) {
        if (this.inUse) {
            this.setX(x - this.xx);
            this.setY(y - this.yy);
        }

    }

    public boolean i(int x, int y) {
        return x >= this.x + 92 - 13 && x <= this.x + this.width && (float) y >= (float) this.y + 2.0F && y <= this.y + this.bh + 1;
    }

    public boolean mousePressed(int x, int y) {
        return x >= this.x + 77 && x <= this.x + this.width - 6 && (float) y >= (float) this.y + 2.0F && y <= this.y + this.bh + 1;
    }

    public boolean insideArea(int x, int y) {
        return x >= this.x && x <= this.x + this.width && y >= this.y && y <= this.y + this.bh;
    }

    public String getName() {
        return String.valueOf(getModules());
    }

    public void setLocation(int parseInt, int parseInt1) {
        this.x = parseInt;
        this.y = parseInt1;
    }

    public void setVisable(boolean vis) {
        this.visable = vis;
    }

    public boolean isVisable() {
        return visable;
    }
}