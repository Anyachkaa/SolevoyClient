package ru.itskekoff.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class RenderUtils {

    public static void drawBorderedRect(double x, double y, double endX, double endY, double width, Color insideColor, Color outlineColor) {
        drawLine(x, y, endX, y, width, outlineColor);
        drawLine(endX, y, endX, endY, width, outlineColor);
        drawLine(x, endY, endX, endY, width, outlineColor);
        drawLine(x, endY, x, y, width, outlineColor);

        drawRect(x, y, endX, endY, insideColor);
    }

    public static void drawFixedBorderedRect(double x, double y, double endX, double endY, double width, Color insideColor, Color outlineColor) {
        drawLine(x, y, endX, y, width, outlineColor); // вверх
        drawLine(endX, y, endX, endY, width, outlineColor); // право
        drawLine(x, endY, endX, endY, width, outlineColor); // ввниз
        drawLine(x, endY, x, y, width, outlineColor); // влево

        drawRect(x, y, endX, endY, insideColor);
    }
    public static void drawBorderedImage(File file, Image image, double x, double y, double endX, double endY, double width, Color insideColor, Color outlineColor) {
        boolean imageEnabled = image != null;
        drawLine(x, y, endX, y, width, outlineColor);
        drawLine(endX, y, endX, endY, width, outlineColor);
        drawLine(x, endY, endX, endY, width, outlineColor);
        drawLine(x, endY, x, y, width, outlineColor);

        if (!imageEnabled) drawRect(x, y, endX, endY, insideColor);
        if (imageEnabled) {
            ResourceLocation location = Minecraft.getMinecraft().getRenderManager().renderEngine.getDynamicTextureLocation(file.getName(), new DynamicTexture(toBufferedImage(image)));

            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDepthMask(false);
            GL11.glColor4f(0.6f, 0.6f, 0.6f, 1);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            Minecraft.getMinecraft().getTextureManager().bindTexture(location);
            Gui.drawModalRectWithCustomSizedTexture((int) x + 3, (int) y + 3, (float) 0, (float) 0, (int) width, 64, (float) width, 64);
            GL11.glDepthMask(true);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GlStateManager.resetColor();
        }
    }

    public static void drawSmoothRect(double left, double top, double right, double bottom, Color color) {
        GlStateManager.resetColor();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        drawRect(left, top, right, bottom, color);
        GL11.glScalef(0.5f, 0.5f, 0.5f);
        drawRect(left * 2.0f - 1.0f, top * 2.0f, left * 2.0f, bottom * 2.0f - 1.0f, color);
        drawRect(left * 2.0f, top * 2.0f - 1.0f, right * 2.0f, top * 2.0f, color);
        drawRect(right * 2.0f, top * 2.0f, right * 2.0f + 1.0f, bottom * 2.0f - 1.0f, color);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glScalef(2.0f, 2.0f, 2.0f);
    }

    public static void drawLine(double x, double y, double endX, double endY, double width, Color rectColor) {
        int color = rectColor.getRGB();
        float red = (color >> 16 & 0xFF) / 255.0F;
        float green = (color >> 8 & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;
        float alpha = (color >> 24 & 0xFF) / 255.0F;
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(red, green, blue, alpha);
        GL11.glPushMatrix();
        GL11.glLineWidth((float) width);
        GL11.glBegin(GL11.GL_LINE_STRIP);
        GL11.glVertex2d(x, y);
        GL11.glVertex2d(endX, endY);
        GL11.glEnd();
        GL11.glLineWidth(1F);
        GL11.glPopMatrix();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.color(1, 1, 1, 1);
    }

    public static void drawRect(double x, double y, double endX, double endY, Color rectColor) {
        int color = rectColor.getRGB();
        float red = (color >> 16 & 0xFF) / 255.0F;
        float green = (color >> 8 & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;
        float alpha = (color >> 24 & 0xFF) / 255.0F;

        if (x < endX) {
            double i = x;
            x = endX;
            endX = i;
        }

        if (y < endY) {
            double j = y;
            y = endY;
            endY = j;
        }

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color(red, green, blue, alpha);
        bufferBuilder.begin(7, DefaultVertexFormats.POSITION);
        bufferBuilder.pos(x, endY, 0.0D).endVertex();
        bufferBuilder.pos(endX, endY, 0.0D).endVertex();
        bufferBuilder.pos(endX, y, 0.0D).endVertex();
        bufferBuilder.pos(x, y, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();

    }

    private static BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        return bimage;
    }

}
