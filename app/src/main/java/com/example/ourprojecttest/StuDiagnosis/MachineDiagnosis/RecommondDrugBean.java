package com.example.ourprojecttest.StuDiagnosis.MachineDiagnosis;

public class RecommondDrugBean {
    private String goodsName;//商品名字
    private String pyCode;//拼音简码
    private String guiGe;//规格
    private String unit;//单位
    private String approvalNumber;//批准文号
    private String manufacture;//生产厂家
    private String barCode;//条形码
    private String cureDisease;//主治疾病
    private String explainBook;//说明书
    private String additionalExplain;//补充说明
    private String otc;//1为otc，0为普通
    private String upTime;//上架时间

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getPyCode() {
        return pyCode;
    }

    public void setPyCode(String pyCode) {
        this.pyCode = pyCode;
    }


    public String getGuiGe() {
        return guiGe;
    }

    public void setGuiGe(String guiGe) {
        this.guiGe = guiGe;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getApprovalNumber() {
        return approvalNumber;
    }

    public void setApprovalNumber(String approvalNumber) {
        this.approvalNumber = approvalNumber;
    }

    public String getManufacture() {
        return manufacture;
    }

    public void setManufacture(String manufacture) {
        this.manufacture = manufacture;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getCureDisease() {
        return cureDisease;
    }

    public void setCureDisease(String cureDisease) {
        this.cureDisease = cureDisease;
    }

    public String getExplainBook() {
        return explainBook;
    }

    public void setExplainBook(String explainBook) {
        this.explainBook = explainBook;
    }

    public String getAdditionalExplain() {
        return additionalExplain;
    }

    public void setAdditionalExplain(String additionalExplain) {
        this.additionalExplain = additionalExplain;
    }

    public String getOtc() {
        return otc;
    }

    public void setOtc(String otc) {
        this.otc = otc;
    }

    public String getUpTime() {
        return upTime;
    }

    public void setUpTime(String upTime) {
        this.upTime = upTime;
    }
}
