package com.recon.model;

import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

public class SettlementTypeJson {
	
	private String Result;
    private List<SettlementTypeBean> Records;
    private int TotalRecordCount;
    private String Message;

    public void setParams(String Result, List<SettlementTypeBean> Records) {
        this.Result = Result;
        this.Records = Records;
        this.TotalRecordCount = TotalRecordCount;
    }

    public void setParams(String Result, String Message) {
        this.Result = Result;
        this.Message = Message;
    }

    @JsonProperty("Result")
    public String getResult() {
        return Result;
    }

    public void setResult(String Result) {
        this.Result = Result;
    }

    @JsonProperty("Records")
    public List<SettlementTypeBean> getRecords() {
        return Records;
    }

    public void setRecords(List<SettlementTypeBean> Records) {
        this.Records = Records;
    }

    @JsonProperty("TotalRecordCount")
    public int getTotalRecordCount() {
        return TotalRecordCount;
    }

    public void setTotalRecordCount(int TotalRecordCount) {
        this.TotalRecordCount = TotalRecordCount;
    }

    @JsonProperty("Message")
    public String getMessage() {
        return Message;
    }

    public void setMessage(String Message) {
        this.Message = Message;
    }

}
