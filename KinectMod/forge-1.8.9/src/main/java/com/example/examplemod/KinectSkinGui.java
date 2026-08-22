package com.example.examplemod;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

import java.io.IOException;

public class KinectSkinGui extends GuiScreen {

    private GuiTextField nicknamePlayer1;
    private GuiTextField nicknamePlayer2;

    private int selectedPlayer = 1;

    @Override
    public void initGui() {

        int centerX = this.width / 2;

        /*
         * Campo do Jogador 1
         */
        nicknamePlayer1 = new GuiTextField(
                0,
                this.fontRendererObj,
                centerX - 100,
                115,
                200,
                20
        );

        nicknamePlayer1.setMaxStringLength(16);

        nicknamePlayer1.setText(
                ModKinect.playerManager
                        .getPlayer1()
                        .getNickname()
        );

        /*
         * Campo do Jogador 2
         */
        nicknamePlayer2 = new GuiTextField(
                1,
                this.fontRendererObj,
                centerX - 100,
                160,
                200,
                20
        );

        nicknamePlayer2.setMaxStringLength(16);

        nicknamePlayer2.setText(
                ModKinect.playerManager
                        .getPlayer2()
                        .getNickname()
        );

        /*
         * Jogador 1
         */
        this.buttonList.add(
                new GuiButton(
                        10,
                        centerX - 105,
                        30,
                        100,
                        20,
                        "Jogador 1"
                )
        );

        /*
         * Jogador 2
         */
        this.buttonList.add(
                new GuiButton(
                        11,
                        centerX + 5,
                        30,
                        100,
                        20,
                        "Jogador 2"
                )
        );

        /*
         * Steve
         */
        this.buttonList.add(
                new GuiButton(
                        20,
                        centerX - 105,
                        60,
                        100,
                        20,
                        "Steve"
                )
        );

        /*
         * Alex
         */
        this.buttonList.add(
                new GuiButton(
                        21,
                        centerX + 5,
                        60,
                        100,
                        20,
                        "Alex"
                )
        );

        /*
         * Salvar
         */
        this.buttonList.add(
                new GuiButton(
                        30,
                        centerX - 100,
                        200,
                        200,
                        20,
                        "Salvar"
                )
        );
    }

    @Override
    protected void actionPerformed(GuiButton button)
            throws IOException {

        KinectPlayerManager manager =
                ModKinect.playerManager;


        if (button.id == 10) {

            selectedPlayer = 1;

        } else if (button.id == 11) {

            selectedPlayer = 2;
        }

        /*
         * Steve.
         */
        else if (button.id == 20) {

            manager
                    .getPlayer(selectedPlayer)
                    .setSkin(SkinType.STEVE);
        }

        /*
         * Alex.
         */
        else if (button.id == 21) {

            manager
                    .getPlayer(selectedPlayer)
                    .setSkin(SkinType.ALEX);
        }

        /*
         * Salvar.
         */
        else if (button.id == 30) {

            String nickname1 = nicknamePlayer1.getText();
            String nickname2 = nicknamePlayer2.getText();

            manager
                    .getPlayer1()
                    .setNickname(
                            nicknamePlayer1.getText()
                    );

            manager
                    .getPlayer2()
                    .setNickname(
                            nicknamePlayer2.getText()
                    );

            if (!nickname1.trim().isEmpty()) {

                KinectSkinManager.testNickname(
                        nickname1
                );
            }

            if (!nickname2.trim().isEmpty()) {

                KinectSkinManager.testNickname(
                        nickname2
                );
            }

            this.mc.displayGuiScreen(null);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode)
            throws IOException {

        nicknamePlayer1.textboxKeyTyped(
                typedChar,
                keyCode
        );

        nicknamePlayer2.textboxKeyTyped(
                typedChar,
                keyCode
        );

        super.keyTyped(
                typedChar,
                keyCode
        );
    }

    @Override
    protected void mouseClicked(
            int mouseX,
            int mouseY,
            int mouseButton)
            throws IOException {

        nicknamePlayer1.mouseClicked(
                mouseX,
                mouseY,
                mouseButton
        );

        nicknamePlayer2.mouseClicked(
                mouseX,
                mouseY,
                mouseButton
        );

        super.mouseClicked(
                mouseX,
                mouseY,
                mouseButton
        );
    }

    @Override
    public void drawScreen(
            int mouseX,
            int mouseY,
            float partialTicks) {


        this.drawDefaultBackground();

        int centerX = this.width / 2;


        this.drawCenteredString(
                this.fontRendererObj,
                "KinectCraft",
                centerX,
                10,
                0xFFFFFF
        );

        this.drawCenteredString(
                this.fontRendererObj,
                "Jogador 1",
                centerX,
                100,
                0xFFFFFF
        );

        this.drawCenteredString(
                this.fontRendererObj,
                "Jogador 2",
                centerX,
                145,
                0xFFFFFF
        );

        this.drawCenteredString(
                this.fontRendererObj,
                "",
                centerX,
                90,
                0xFFFFFF
        );

        nicknamePlayer1.drawTextBox();
        nicknamePlayer2.drawTextBox();

        super.drawScreen(
                mouseX,
                mouseY,
                partialTicks
        );
    }
}