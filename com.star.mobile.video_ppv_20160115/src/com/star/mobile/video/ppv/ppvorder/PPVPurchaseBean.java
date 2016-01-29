package com.star.mobile.video.ppv.ppvorder;

/**
 * Created by Lee on 2016/1/18.
 */
public class PPVPurchaseBean {

    private String purchaseTime;
    private String productCode;
    private float purchasePrice;
    private String purchaseFavorablePrice;
    private boolean isChecked;

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public String getPurchaseTime() {
        return purchaseTime;
    }

    public void setPurchaseTime(String purchaseTime) {
        this.purchaseTime = purchaseTime;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public float getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(float purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public String getPurchaseFavorablePrice() {
        return purchaseFavorablePrice;
    }

    public void setPurchaseFavorablePrice(String purchaseFavorablePrice) {
        this.purchaseFavorablePrice = purchaseFavorablePrice;
    }
}
