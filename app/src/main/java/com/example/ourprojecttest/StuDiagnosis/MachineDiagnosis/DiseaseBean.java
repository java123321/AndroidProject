package com.example.ourprojecttest.StuDiagnosis.MachineDiagnosis;

public class DiseaseBean {
    private String recoveryRate;//恢复率
    private String infectious;//传染性
    private String treatmentDepartment;//治疗部门
    private String belongInsurance;//是否属于医保类疾病
    private String population;//疾病人群
    private String concurrentDisease;//当前疾病
    private String symptom;//症状
    private String id;
    private String diseaseName;//疾病名称
    private String  diseaseAlias;//疾病别名
    private String diseaseLocation;//疾病部位
    private String introduction;//疾病介绍
    private String treatmentDuration;//治疗周期

    public String getRecoveryRate() {
        return recoveryRate;
    }

    public void setRecoveryRate(String recoveryRate) {
        this.recoveryRate = recoveryRate;
    }

    public String getInfectious() {
        return infectious;
    }

    public void setInfectious(String infectious) {
        this.infectious = infectious;
    }

    public String getTreatmentDepartment() {
        return treatmentDepartment;
    }

    public void setTreatmentDepartment(String treatmentDepartment) {
        this.treatmentDepartment = treatmentDepartment;
    }

    public String getBelongInsurance() {
        return belongInsurance;
    }

    public void setBelongInsurance(String belongInsurance) {
        this.belongInsurance = belongInsurance;
    }

    public String getPopulation() {
        return population;
    }

    public void setPopulation(String population) {
        this.population = population;
    }

    public String getConcurrentDisease() {
        return concurrentDisease;
    }

    public void setConcurrentDisease(String concurrentDisease) {
        this.concurrentDisease = concurrentDisease;
    }

    public String getSymptom() {
        return symptom;
    }

    public void setSymptom(String symptom) {
        this.symptom = symptom;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDiseaseName() {
        return diseaseName;
    }

    public void setDiseaseName(String diseaseName) {
        this.diseaseName = diseaseName;
    }

    public String getDiseaseAlias() {
        return diseaseAlias;
    }

    public void setDiseaseAlias(String diseaseAlias) {
        this.diseaseAlias = diseaseAlias;
    }

    public String getDiseaseLocation() {
        return diseaseLocation;
    }

    public void setDiseaseLocation(String diseaseLocation) {
        this.diseaseLocation = diseaseLocation;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getTreatmentDuration() {
        return treatmentDuration;
    }

    public void setTreatmentDuration(String treatmentDuration) {
        this.treatmentDuration = treatmentDuration;
    }
}
