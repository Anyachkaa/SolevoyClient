package viamcp.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.TextFormatting;
import viamcp.ViaMCP;
import viamcp.protocols.ProtocolCollection;

import java.io.IOException;

public class GuiProtocolSelector extends GuiScreen {
    public SlotList list;
    private GuiScreen parent;

    public GuiProtocolSelector(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.add(new GuiButton(1, width / 2 - 100, height - 25, 200, 20, "Back"));

        list = new SlotList(mc, width, height, 32, height - 32, 10);
    }

    @Override
    protected void actionPerformed(GuiButton guiButton) throws IOException {
        list.actionPerformed(guiButton);

        if (guiButton.id == 1) {
            mc.displayGuiScreen(parent);
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        list.handleMouseInput();
        super.handleMouseInput();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        list.drawScreen(mouseX, mouseY, partialTicks);
        GlStateManager.pushMatrix();
        GlStateManager.scale(2.0, 2.0, 2.0);
        String title = TextFormatting.BOLD + "ViaMCP Reborn";
        drawString(this.fontRenderer, title, (this.width - (this.fontRenderer.getStringWidth(title) * 2)) / 4, 5, -1);
        GlStateManager.popMatrix();

        String versionName = ProtocolCollection.getProtocolById(ViaMCP.getInstance().getVersion()).getName();
        String versionCodeName = ProtocolCollection.getProtocolInfoById(ViaMCP.getInstance().getVersion()).getName();
        String versionReleaseDate = ProtocolCollection.getProtocolInfoById(ViaMCP.getInstance().getVersion()).getReleaseDate();
        String versionTitle = "Version: " + versionName + " - " + versionCodeName;
        String versionReleased = "Released: " + versionReleaseDate;

        int fixedHeight = ((5 + this.fontRenderer.FONT_HEIGHT) * 2) + 2;

        drawString(this.fontRenderer, TextFormatting.GRAY + (TextFormatting.BOLD + "Version Information"), (width - this.fontRenderer.getStringWidth("Version Information")) / 2, fixedHeight, -1);
        drawString(this.fontRenderer, versionTitle, (width - this.fontRenderer.getStringWidth(versionTitle)) / 2, fixedHeight + this.fontRenderer.FONT_HEIGHT, -1);
        drawString(this.fontRenderer, versionReleased, (width - this.fontRenderer.getStringWidth(versionReleased)) / 2, fixedHeight + this.fontRenderer.FONT_HEIGHT * 2, -1);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    class SlotList extends GuiSlot {
        public SlotList(Minecraft mc, int width, int height, int top, int bottom, int slotHeight) {
            super(mc, width, height, top + 30, bottom, 18);
        }

        @Override
        protected int getSize() {
            return ProtocolCollection.values().length;
        }

        @Override
        protected void elementClicked(int i, boolean b, int i1, int i2) {
            int protocolVersion = ProtocolCollection.values()[i].getVersion().getVersion();
            ViaMCP.getInstance().setVersion(protocolVersion);
            ViaMCP.getInstance().asyncSlider.setVersion(protocolVersion);
        }

        @Override
        protected boolean isSelected(int i) {
            return false;
        }

        @Override
        protected void drawBackground() {
            drawDefaultBackground();
        }

        @Override
        protected void drawSlot(int i, int i1, int i2, int i3, int i4, int i5, float i6) {
            drawCenteredString(mc.fontRenderer, (ViaMCP.getInstance().getVersion() == ProtocolCollection.values()[i].getVersion().getVersion() ? TextFormatting.GREEN.toString() + TextFormatting.BOLD : TextFormatting.GRAY.toString()) + ProtocolCollection.getProtocolById(ProtocolCollection.values()[i].getVersion().getVersion()).getName() + TextFormatting.WHITE + " (PVN: " + ProtocolCollection.getProtocolById(ProtocolCollection.values()[i].getVersion().getVersion()).getVersion() + ")", width / 2, i2 + 2, -1);
            GlStateManager.pushMatrix();
            GlStateManager.popMatrix();
        }
    }
}
