package keystrokesmod.client.clickgui.raven.components;

import java.awt.Color;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import keystrokesmod.client.clickgui.raven.ClickGui;
import keystrokesmod.client.clickgui.raven.Component;
import keystrokesmod.client.main.Raven;
import keystrokesmod.client.module.modules.client.GuiModule;
import net.minecraft.client.Minecraft;

public class BindComponent implements Component {
    private boolean isBinding;
    private final ModuleComponent p;
    private int o;
    private int x;
    private int y;

    public BindComponent(ModuleComponent b, int o) {
        this.p = b;
        this.x = b.category.getX() + b.category.getWidth();
        this.y = b.category.getY() + b.o;
        this.o = o;
    }

    public void draw() {
        GL11.glPushMatrix();
        // if(!GuiModule.useCustomFont.isToggled())
        GL11.glScaled(0.5D, 0.5D, 0.5D);

        if (/* this.p.mod instanceof AutoConfig */1 > 2) {
            this.dr("Auto-configure modules.");
            GL11.glPopMatrix();
        } else {
            this.dr(this.isBinding ? BindStage.binding
                    : BindStage.bind + ": " + Keyboard.getKeyName(this.p.mod.getKeycode()));
            GL11.glPopMatrix();
        }
    }

    @Override
    public void update(int mousePosX, int mousePosY) {
        boolean h = this.i(mousePosX, mousePosY);
        this.y = this.p.category.getY() + this.o;
        this.x = this.p.category.getX();
    }

    public void mouseDown(int x, int y, int b) {
        if (this.i(x, y) && b == 0 && this.p.po) {
            this.isBinding = !this.isBinding;
            Raven.mc.thePlayer.playSound("gui.button.press", 1, 1);
            if (isBinding) {
                ClickGui.binding++;
            } else {
                ClickGui.binding--;
            }
        }

    }

    @Override
    public void mouseReleased(int x, int y, int m) {

    }

    @Override
    public void keyTyped(char t, int k) {
        if (!this.p.mod.getName().equalsIgnoreCase("AutoConfig")) {
            if (this.isBinding) {
                if (k == Keyboard.KEY_0 || k == Keyboard.KEY_ESCAPE) {
                    if (this.p.mod instanceof GuiModule) {
                        this.p.mod.setbind(54);
                    } else {
                        this.p.mod.setbind(0);
                    }
                } else {
                    this.p.mod.setbind(k);
                }

                ClickGui.binding--;
                this.isBinding = false;
            }

        }
    }

    @Override
    public void setComponentStartAt(int n) {
        this.o = n;
    }

    public boolean i(int x, int y) {
        return x > this.x && x < this.x + this.p.category.getWidth() && y > this.y - 1 && y < this.y + 12;
    }

    public int getHeight() {
        return 16;
    }
    
    @Override
    public int getY() {
        return y;
    }

    private void dr(String s) {
        // if(GuiModule.useCustomFont.isToggled()) {
        // FontUtil.small.drawSmoothString(s, (float) ((this.p.category.getX() + 4)),
        // (float) ((this.p.category.getY() + this.o + 2)), Color.HSBtoRGB((float)
        // (System.currentTimeMillis() % 3750L) / 3750.0F, 0.3F, 0.8F));
        // } else {
        Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(s, (float) ((this.p.category.getX() + 4) * 2),
                (float) ((this.p.category.getY() + this.o + 3) * 2),
                Color.HSBtoRGB((float) (System.currentTimeMillis() % 3750L) / 3750.0F, 0.8F, 0.8F));
        // }
    }
}
