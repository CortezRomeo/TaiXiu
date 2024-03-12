package com.cortezromeo.taixiu.geyserform;

import com.cortezromeo.taixiu.TaiXiu;
import org.geysermc.cumulus.util.FormImage;

public class ButtonData {

    private String buttonName;
    private FormImage.Type butotnImageType;
    private String buttonImageData;

    public ButtonData(String buttonName, FormImage.Type buttonImageType, String buttonImageData) {
        this.buttonName = buttonName;
        this.butotnImageType = buttonImageType;
        this.buttonImageData = buttonImageData;
    }

    public String getButtonName() {
        return TaiXiu.nms.addColor(buttonName);
    }

    public FormImage.Type getButtonImageType() {
        return butotnImageType;
    }

    public String getButtonImageData() {
        return buttonImageData;
    }

}
