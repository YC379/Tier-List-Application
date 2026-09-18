package com.application.sae201;

import java.util.ArrayList;
import java.util.List;

public class TierJson {
    public String labelTier;
    public String couleurLabelTier;
    public String couleurTier;
    public List<ItemJson> items;

    public TierJson(String labelTier, String couleurLabelTier, String couleurTier) {
        this.labelTier = labelTier;
        this.couleurLabelTier = couleurLabelTier;
        this.couleurTier = couleurTier;
        this.items = new ArrayList<>();
    }
}