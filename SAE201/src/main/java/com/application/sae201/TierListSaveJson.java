package com.application.sae201;

import java.util.List;

public class TierListSaveJson {
    public String nomTierList;
    public List<TierJson> tierJsonList;
    public List<ItemJson> reserveItems;

    public String aff() {
        return "nomTierList = " + nomTierList;
    }
}