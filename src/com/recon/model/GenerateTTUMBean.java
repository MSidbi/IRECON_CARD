package com.recon.model;

import java.util.ArrayList;
import java.util.List;

public class GenerateTTUMBean {

	private List<String> stExcelHeader;
	private String stFile_header;
	private String account_repo;
	private String stHeader_Value;
	private String stStart_charpos;
	private String stChar_Size;
	private String stPadding;
	private String stExcel_Header;
	private String stRemove_char;
	private String StSelectedFile;
//	private String stSelectedFile;
	//private String stFile_Name;
//	private String stFromDate;
//	private String stToDate;
	private String stFile_Date;
	private String pstd_user_id;
	private String tran_date;
	private String m_surch2;
	private String m_surch3;
	private String m_surch4;
	private String createddate           ;
	private String swicth_serial_num     ;
	private String processor_a_i         ;
	private String processor_id          ;
	private String tran_time             ;
	private String pan_length            ;
	private String pan_num               ;
	private String proccessing_code      ;
	private String trace_num             ;
	private String mercahnt_type         ;
	private String pos_entry             ;
	private String aquirer_i_id          ;
	private String terminal_id           ;
	private String brand                 ;
	private String advaice_reg_code      ;
	private String intra_curr_aggrmt_code;
	private String auth_id               ;
	private String implied_dec_tran      ;
	private String compltd_amnt_tran     ;
	private String compltd_amnt_tran_d_c ;
	private String cash_back_amnt_l      ;
	private String cash_back_amnt_d_c_c  ;
	private String access_fee_l          ;
	private String access_fee_l_d_c      ;
	private String currency_settlment    ;
	private String implied_dec_settlment ;
	private String conversion_rate      ; 
	private String compltd_amt_settmnt  ; 
	private String compltd_amnt_d_c     ; 
	private String inter_change_fee      ;
	private String inter_change_fee_d_c  ;
	private String service_lev_ind  ;     
	
	private String switch_remarks;
	private String cbs_remarks;
	
	private String seg_tran_id ;     
	private String msgtype ;         
	private String pan    ;          
	private String termid ;          
	private String local_date  ;     
	private String local_time;       
	private String pcode ;           
	private String trace;            
	private String amount ;          
	private String acceptorname ;    
	private String respcode ;        
	private String termloc ;         
	private String new_amount;       
	private String txnsrc ;          
	private String txndest  ;        
	private String revcode ;         
	private String amount_equiv ;    
	private String ch_amount ;       
	private String settlement_date ; 
	private String createdt ; 
	private String createdby ;       
	private String filedate     ; 
	private String tc;
	
	
	private String DIFF_AMOUNT;
	
	
	
	
	private String entry_date;
	
	private String processing_code;    
    
	private String amount_recon;       
	private String conv_rate_recon;    
	private String date_val;           
	private String expire_date;        
	private String data_code ;         
	private String card_seq_num ;     
	
	private String tran_org_id_code;   
	private String card_iss_ref_data;  
	private String recv_inst_idcode;   
	private String terminal_type;      
	private String elec_com_indic;     
	private String processing_mode ;   
	private String currency_exponent ; 
	private String business_act;       
	private String settlement_ind;     
	private String card_accp_name_loc ;
	private String header_type ;
	private String account_number         ;
	//private String currency_code          ;
	private String service_outlet         ;
	private String stFunctionCode;
	private String visaFunctionCode;
	
	
	
	public String getSwitch_remarks() {
		return switch_remarks;
	}
	public void setSwitch_remarks(String switch_remarks) {
		this.switch_remarks = switch_remarks;
	}
	public String getCbs_remarks() {
		return cbs_remarks;
	}
	public void setCbs_remarks(String cbs_remarks) {
		this.cbs_remarks = cbs_remarks;
	}
	public String getDIFF_AMOUNT() {
		return DIFF_AMOUNT;
	}
	public void setDIFF_AMOUNT(String dIFF_AMOUNT) {
		DIFF_AMOUNT = dIFF_AMOUNT;
	}
	public String getVisaFunctionCode() {
		return visaFunctionCode;
	}
	public void setVisaFunctionCode(String visaFunctionCode) {
		this.visaFunctionCode = visaFunctionCode;
	}
	public String getTc() {
		return tc;
	}
	public void setTc(String tc) {
		this.tc = tc;
	}
	public String getAccount_number() {
		return account_number;
	}
	public void setAccount_number(String account_number) {
		this.account_number = account_number;
	}
	public String getService_outlet() {
		return service_outlet;
	}
	public void setService_outlet(String service_outlet) {
		this.service_outlet = service_outlet;
	}
	public String getPart_tran_type() {
		return part_tran_type;
	}
	public void setPart_tran_type(String part_tran_type) {
		this.part_tran_type = part_tran_type;
	}
	public String getTransaction_amount() {
		return transaction_amount;
	}
	public void setTransaction_amount(String transaction_amount) {
		this.transaction_amount = transaction_amount;
	}
	public String getTransaction_particulars() {
		return transaction_particulars;
	}
	public void setTransaction_particulars(String transaction_particulars) {
		this.transaction_particulars = transaction_particulars;
	}
	public String getRef_curr_code() {
		return ref_curr_code;
	}
	public void setRef_curr_code(String ref_curr_code) {
		this.ref_curr_code = ref_curr_code;
	}
	public String getRef_tran_amount() {
		return ref_tran_amount;
	}
	public void setRef_tran_amount(String ref_tran_amount) {
		this.ref_tran_amount = ref_tran_amount;
	}
	public String getDcrs_remarks() {
		return dcrs_remarks;
	}
	public void setDcrs_remarks(String dcrs_remarks) {
		this.dcrs_remarks = dcrs_remarks;
	}
	public String getRef_num() {
		return ref_num;
	}
	public void setRef_num(String ref_num) {
		this.ref_num = ref_num;
	}
	private String part_tran_type         ;
	private String transaction_amount     ;
	private String transaction_particulars;
	private String ref_curr_code      ;    
	private String ref_tran_amount  ;      
	private String dcrs_remarks ;          
	private String ref_num   ;             

	
	
	public String getTran_org_id_code() {
		return tran_org_id_code;
	}
	public void setTran_org_id_code(String tran_org_id_code) {
		this.tran_org_id_code = tran_org_id_code;
	}
	public String getCard_iss_ref_data() {
		return card_iss_ref_data;
	}
	public void setCard_iss_ref_data(String card_iss_ref_data) {
		this.card_iss_ref_data = card_iss_ref_data;
	}
	public String getRecv_inst_idcode() {
		return recv_inst_idcode;
	}
	public void setRecv_inst_idcode(String recv_inst_idcode) {
		this.recv_inst_idcode = recv_inst_idcode;
	}
	public String getTerminal_type() {
		return terminal_type;
	}
	public void setTerminal_type(String terminal_type) {
		this.terminal_type = terminal_type;
	}
	public String getElec_com_indic() {
		return elec_com_indic;
	}
	public void setElec_com_indic(String elec_com_indic) {
		this.elec_com_indic = elec_com_indic;
	}
	public String getProcessing_mode() {
		return processing_mode;
	}
	public void setProcessing_mode(String processing_mode) {
		this.processing_mode = processing_mode;
	}
	public String getCurrency_exponent() {
		return currency_exponent;
	}
	public void setCurrency_exponent(String currency_exponent) {
		this.currency_exponent = currency_exponent;
	}
	public String getBusiness_act() {
		return business_act;
	}
	public void setBusiness_act(String business_act) {
		this.business_act = business_act;
	}
	public String getSettlement_ind() {
		return settlement_ind;
	}
	public void setSettlement_ind(String settlement_ind) {
		this.settlement_ind = settlement_ind;
	}
	public String getCard_accp_name_loc() {
		return card_accp_name_loc;
	}
	public void setCard_accp_name_loc(String card_accp_name_loc) {
		this.card_accp_name_loc = card_accp_name_loc;
	}
	public String getHeader_type() {
		return header_type;
	}
	public void setHeader_type(String header_type) {
		this.header_type = header_type;
	}
	public String getProcessing_code() {
		return processing_code;
	}
	public void setProcessing_code(String processing_code) {
		this.processing_code = processing_code;
	}
	public String getAmount_recon() {
		return amount_recon;
	}
	public void setAmount_recon(String amount_recon) {
		this.amount_recon = amount_recon;
	}
	public String getConv_rate_recon() {
		return conv_rate_recon;
	}
	public void setConv_rate_recon(String conv_rate_recon) {
		this.conv_rate_recon = conv_rate_recon;
	}
	public String getDate_val() {
		return date_val;
	}
	public void setDate_val(String date_val) {
		this.date_val = date_val;
	}
	public String getExpire_date() {
		return expire_date;
	}
	public void setExpire_date(String expire_date) {
		this.expire_date = expire_date;
	}
	public String getData_code() {
		return data_code;
	}
	public void setData_code(String data_code) {
		this.data_code = data_code;
	}
	public String getCard_seq_num() {
		return card_seq_num;
	}
	public void setCard_seq_num(String card_seq_num) {
		this.card_seq_num = card_seq_num;
	}
	public String getFuncation_code() {
		return funcation_code;
	}
	public void setFuncation_code(String funcation_code) {
		this.funcation_code = funcation_code;
	}
	public String getMsg_res_code() {
		return msg_res_code;
	}
	public void setMsg_res_code(String msg_res_code) {
		this.msg_res_code = msg_res_code;
	}
	public String getCard_acc_code() {
		return card_acc_code;
	}
	public void setCard_acc_code(String card_acc_code) {
		this.card_acc_code = card_acc_code;
	}
	public String getAmount_org() {
		return amount_org;
	}
	public void setAmount_org(String amount_org) {
		this.amount_org = amount_org;
	}
	public String getAquierer_ref_no() {
		return aquierer_ref_no;
	}
	public void setAquierer_ref_no(String aquierer_ref_no) {
		this.aquierer_ref_no = aquierer_ref_no;
	}
	public String getFi_id_code() {
		return fi_id_code;
	}
	public void setFi_id_code(String fi_id_code) {
		this.fi_id_code = fi_id_code;
	}
	public String getRetrv_ref_no() {
		return retrv_ref_no;
	}
	public void setRetrv_ref_no(String retrv_ref_no) {
		this.retrv_ref_no = retrv_ref_no;
	}
	public String getApproval_code() {
		return approval_code;
	}
	public void setApproval_code(String approval_code) {
		this.approval_code = approval_code;
	}
	public String getService_code() {
		return service_code;
	}
	public void setService_code(String service_code) {
		this.service_code = service_code;
	}
	public String getCard_acc_term_id() {
		return card_acc_term_id;
	}
	public void setCard_acc_term_id(String card_acc_term_id) {
		this.card_acc_term_id = card_acc_term_id;
	}
	public String getCard_acc_id_code() {
		return card_acc_id_code;
	}
	public void setCard_acc_id_code(String card_acc_id_code) {
		this.card_acc_id_code = card_acc_id_code;
	}
	public String getAdditional_data() {
		return additional_data;
	}
	public void setAdditional_data(String additional_data) {
		this.additional_data = additional_data;
	}
	public String getCurrency_code_tran() {
		return currency_code_tran;
	}
	public void setCurrency_code_tran(String currency_code_tran) {
		this.currency_code_tran = currency_code_tran;
	}
	public String getCurrency_code_recon() {
		return currency_code_recon;
	}
	public void setCurrency_code_recon(String currency_code_recon) {
		this.currency_code_recon = currency_code_recon;
	}
	public String getTran_lifecycle_id() {
		return tran_lifecycle_id;
	}
	public void setTran_lifecycle_id(String tran_lifecycle_id) {
		this.tran_lifecycle_id = tran_lifecycle_id;
	}
	public String getMsg_num() {
		return msg_num;
	}
	public void setMsg_num(String msg_num) {
		this.msg_num = msg_num;
	}
	public String getDate_action() {
		return date_action;
	}
	public void setDate_action(String date_action) {
		this.date_action = date_action;
	}
	public String getTran_dest_id_code() {
		return tran_dest_id_code;
	}
	public void setTran_dest_id_code(String tran_dest_id_code) {
		this.tran_dest_id_code = tran_dest_id_code;
	}
	private String funcation_code;     
	private String msg_res_code;       
	private String card_acc_code;      
	private String amount_org;         
	private String aquierer_ref_no;    
	private String fi_id_code;         
	private String retrv_ref_no;       
	private String approval_code;      
	private String service_code;       
	private String card_acc_term_id;   
	private String card_acc_id_code;   
	private String additional_data;    
	private String currency_code_tran; 
	private String currency_code_recon;
	private String tran_lifecycle_id;  
	private String msg_num;            
	private String date_action;        
	private String tran_dest_id_code; 
	public String getEntry_date() {
		return entry_date;
	}
	public void setEntry_date(String entry_date) {
		this.entry_date = entry_date;
	}
	public String getVfd_date() {
		return vfd_date;
	}
	public void setVfd_date(String vfd_date) {
		this.vfd_date = vfd_date;
	}
	public String getParticularals2() {
		return particularals2;
	}
	public void setParticularals2(String particularals2) {
		this.particularals2 = particularals2;
	}
	public String getMan_contra_account() {
		return man_contra_account;
	}
	public void setMan_contra_account(String man_contra_account) {
		this.man_contra_account = man_contra_account;
	}
	private String vfd_date;
	private String particularals2;
	private String man_contra_account;
	
	public String getForacid() {
		return foracid;
	}
	public void setForacid(String foracid) {
		this.foracid = foracid;
	}
	public String getE() {
		return e;
	}
	public void setE(String e) {
		this.e = e;
	}
	public String getBalance() {
		return balance;
	}
	public void setBalance(String balance) {
		this.balance = balance;
	}
	public String getTran_id() {
		return tran_id;
	}
	public void setTran_id(String tran_id) {
		this.tran_id = tran_id;
	}
	public String getValue_date() {
		return value_date;
	}
	public void setValue_date(String value_date) {
		this.value_date = value_date;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getRef_no() {
		return ref_no;
	}
	public void setRef_no(String ref_no) {
		this.ref_no = ref_no;
	}
	public String getParticularals() {
		return particularals;
	}
	public void setParticularals(String particularals) {
		this.particularals = particularals;
	}
	public String getContra_account() {
		return contra_account;
	}
	public void setContra_account(String contra_account) {
		this.contra_account = contra_account;
	}
	private String foracid;
	private String e;
	private String balance;
	private String tran_id;
	private String value_date;
	private String remarks;
	private String ref_no;
	private String particularals;
	private String contra_account;
	public String getCreatedby() {
		return createdby;
	}
	public void setCreatedby(String createdby) {
		this.createdby = createdby;
	}
	public String getFiledate() {
		return filedate;
	}
	public void setFiledate(String filedate) {
		this.filedate = filedate;
	}
	public String getCreatedt() {
		return createdt;
	}
	public void setCreatedt(String createdt) {
		this.createdt = createdt;
	}
	public String getSeg_tran_id() {
		return seg_tran_id;
	}
	public void setSeg_tran_id(String seg_tran_id) {
		this.seg_tran_id = seg_tran_id;
	}
	public String getMsgtype() {
		return msgtype;
	}
	public void setMsgtype(String msgtype) {
		this.msgtype = msgtype;
	}
	public String getPan() {
		return pan;
	}
	public void setPan(String pan) {
		this.pan = pan;
	}
	public String getTermid() {
		return termid;
	}
	public void setTermid(String termid) {
		this.termid = termid;
	}
	public String getLocal_date() {
		return local_date;
	}
	public void setLocal_date(String local_date) {
		this.local_date = local_date;
	}
	public String getLocal_time() {
		return local_time;
	}
	public void setLocal_time(String local_time) {
		this.local_time = local_time;
	}
	public String getPcode() {
		return pcode;
	}
	public void setPcode(String pcode) {
		this.pcode = pcode;
	}
	public String getTrace() {
		return trace;
	}
	public void setTrace(String trace) {
		this.trace = trace;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getAcceptorname() {
		return acceptorname;
	}
	public void setAcceptorname(String acceptorname) {
		this.acceptorname = acceptorname;
	}
	public String getRespcode() {
		return respcode;
	}
	public void setRespcode(String respcode) {
		this.respcode = respcode;
	}
	public String getTermloc() {
		return termloc;
	}
	public void setTermloc(String termloc) {
		this.termloc = termloc;
	}
	public String getNew_amount() {
		return new_amount;
	}
	public void setNew_amount(String new_amount) {
		this.new_amount = new_amount;
	}
	public String getTxnsrc() {
		return txnsrc;
	}
	public void setTxnsrc(String txnsrc) {
		this.txnsrc = txnsrc;
	}
	public String getTxndest() {
		return txndest;
	}
	public void setTxndest(String txndest) {
		this.txndest = txndest;
	}
	public String getRevcode() {
		return revcode;
	}
	public void setRevcode(String revcode) {
		this.revcode = revcode;
	}
	public String getAmount_equiv() {
		return amount_equiv;
	}
	public void setAmount_equiv(String amount_equiv) {
		this.amount_equiv = amount_equiv;
	}
	public String getCh_amount() {
		return ch_amount;
	}
	public void setCh_amount(String ch_amount) {
		this.ch_amount = ch_amount;
	}
	public String getSettlement_date() {
		return settlement_date;
	}
	public void setSettlement_date(String settlement_date) {
		this.settlement_date = settlement_date;
	}
	public String getIss_currency_code() {
		return iss_currency_code;
	}
	public void setIss_currency_code(String iss_currency_code) {
		this.iss_currency_code = iss_currency_code;
	}
	public String getAcq_currency_code() {
		return acq_currency_code;
	}
	public void setAcq_currency_code(String acq_currency_code) {
		this.acq_currency_code = acq_currency_code;
	}
	public String getMerchant_type() {
		return merchant_type;
	}
	public void setMerchant_type(String merchant_type) {
		this.merchant_type = merchant_type;
	}
	public String getAuthnum() {
		return authnum;
	}
	public void setAuthnum(String authnum) {
		this.authnum = authnum;
	}
	public String getAcctnum() {
		return acctnum;
	}
	public void setAcctnum(String acctnum) {
		this.acctnum = acctnum;
	}
	public String getTrans_id() {
		return trans_id;
	}
	public void setTrans_id(String trans_id) {
		this.trans_id = trans_id;
	}
	public String getAcquirer() {
		return acquirer;
	}
	public void setAcquirer(String acquirer) {
		this.acquirer = acquirer;
	}
	public String getPan2() {
		return pan2;
	}
	public void setPan2(String pan2) {
		this.pan2 = pan2;
	}
	public String getIssuer() {
		return issuer;
	}
	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}
	public String getRefnum() {
		return refnum;
	}
	public void setRefnum(String refnum) {
		this.refnum = refnum;
	}
	public String getCbs_amount() {
		return cbs_amount;
	}
	public void setCbs_amount(String cbs_amount) {
		this.cbs_amount = cbs_amount;
	}
	public String getCbs_contra() {
		return cbs_contra;
	}
	public void setCbs_contra(String cbs_contra) {
		this.cbs_contra = cbs_contra;
	}
	public String getSettlement_amount() {
		return settlement_amount;
	}
	public void setSettlement_amount(String settlement_amount) {
		this.settlement_amount = settlement_amount;
	}
	public String getSettlement_curr_c() {
		return settlement_curr_c;
	}
	public void setSettlement_curr_c(String settlement_curr_c) {
		this.settlement_curr_c = settlement_curr_c;
	}
	public String getCurrency_amount() {
		return currency_amount;
	}
	public void setCurrency_amount(String currency_amount) {
		this.currency_amount = currency_amount;
	}
	public String getCurrency_code() {
		return currency_code;
	}
	public void setCurrency_code(String currency_code) {
		this.currency_code = currency_code;
	}
	public String getVariation() {
		return variation;
	}
	public void setVariation(String variation) {
		this.variation = variation;
	}
	public String getNetwork_data() {
		return network_data;
	}
	public void setNetwork_data(String network_data) {
		this.network_data = network_data;
	}
	public String getForwarding_inst() {
		return forwarding_inst;
	}
	public void setForwarding_inst(String forwarding_inst) {
		this.forwarding_inst = forwarding_inst;
	}
	private String iss_currency_code;
	private String acq_currency_code;
	private String merchant_type ;   
	private String authnum ;         
	private String acctnum  ;        
	private String trans_id ;        
	private String acquirer ;        
	private String pan2 ;            
	private String issuer ;          
	private String refnum ;          
	private String cbs_amount   ;    
	private String cbs_contra    ;   
	private String settlement_amount;
	private String settlement_curr_c;
	private String currency_amount ; 
	private String currency_code ;   
	private String variation ; 
	private String network_data;
	private String forwarding_inst;
	public String getPstd_user_id() {
		return pstd_user_id;
	}
	public void setPstd_user_id(String pstd_user_id) {
		this.pstd_user_id = pstd_user_id;
	}
	public String getTran_date() {
		return tran_date;
	}
	public void setTran_date(String tran_date) {
		this.tran_date = tran_date;
	}
	public String getM_surch2() {
		return m_surch2;
	}
	public void setM_surch2(String m_surch2) {
		this.m_surch2 = m_surch2;
	}
	public String getM_surch3() {
		return m_surch3;
	}
	public void setM_surch3(String m_surch3) {
		this.m_surch3 = m_surch3;
	}
	public String getM_surch4() {
		return m_surch4;
	}
	public void setM_surch4(String m_surch4) {
		this.m_surch4 = m_surch4;
	}
	public String getCreateddate() {
		return createddate;
	}
	public void setCreateddate(String createddate) {
		this.createddate = createddate;
	}
	public String getSwicth_serial_num() {
		return swicth_serial_num;
	}
	public void setSwicth_serial_num(String swicth_serial_num) {
		this.swicth_serial_num = swicth_serial_num;
	}
	public String getProcessor_a_i() {
		return processor_a_i;
	}
	public void setProcessor_a_i(String processor_a_i) {
		this.processor_a_i = processor_a_i;
	}
	public String getProcessor_id() {
		return processor_id;
	}
	public void setProcessor_id(String processor_id) {
		this.processor_id = processor_id;
	}
	public String getTran_time() {
		return tran_time;
	}
	public void setTran_time(String tran_time) {
		this.tran_time = tran_time;
	}
	public String getPan_length() {
		return pan_length;
	}
	public void setPan_length(String pan_length) {
		this.pan_length = pan_length;
	}
	public String getPan_num() {
		return pan_num;
	}
	public void setPan_num(String pan_num) {
		this.pan_num = pan_num;
	}
	public String getProccessing_code() {
		return proccessing_code;
	}
	public void setProccessing_code(String proccessing_code) {
		this.proccessing_code = proccessing_code;
	}
	public String getTrace_num() {
		return trace_num;
	}
	public void setTrace_num(String trace_num) {
		this.trace_num = trace_num;
	}
	public String getMercahnt_type() {
		return mercahnt_type;
	}
	public void setMercahnt_type(String mercahnt_type) {
		this.mercahnt_type = mercahnt_type;
	}
	public String getPos_entry() {
		return pos_entry;
	}
	public void setPos_entry(String pos_entry) {
		this.pos_entry = pos_entry;
	}
	public String getAquirer_i_id() {
		return aquirer_i_id;
	}
	public void setAquirer_i_id(String aquirer_i_id) {
		this.aquirer_i_id = aquirer_i_id;
	}
	public String getTerminal_id() {
		return terminal_id;
	}
	public void setTerminal_id(String terminal_id) {
		this.terminal_id = terminal_id;
	}
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public String getAdvaice_reg_code() {
		return advaice_reg_code;
	}
	public void setAdvaice_reg_code(String advaice_reg_code) {
		this.advaice_reg_code = advaice_reg_code;
	}
	public String getIntra_curr_aggrmt_code() {
		return intra_curr_aggrmt_code;
	}
	public void setIntra_curr_aggrmt_code(String intra_curr_aggrmt_code) {
		this.intra_curr_aggrmt_code = intra_curr_aggrmt_code;
	}
	public String getAuth_id() {
		return auth_id;
	}
	public void setAuth_id(String auth_id) {
		this.auth_id = auth_id;
	}
	public String getImplied_dec_tran() {
		return implied_dec_tran;
	}
	public void setImplied_dec_tran(String implied_dec_tran) {
		this.implied_dec_tran = implied_dec_tran;
	}
	public String getCompltd_amnt_tran() {
		return compltd_amnt_tran;
	}
	public void setCompltd_amnt_tran(String compltd_amnt_tran) {
		this.compltd_amnt_tran = compltd_amnt_tran;
	}
	public String getCompltd_amnt_tran_d_c() {
		return compltd_amnt_tran_d_c;
	}
	public void setCompltd_amnt_tran_d_c(String compltd_amnt_tran_d_c) {
		this.compltd_amnt_tran_d_c = compltd_amnt_tran_d_c;
	}
	public String getCash_back_amnt_l() {
		return cash_back_amnt_l;
	}
	public void setCash_back_amnt_l(String cash_back_amnt_l) {
		this.cash_back_amnt_l = cash_back_amnt_l;
	}
	public String getCash_back_amnt_d_c_c() {
		return cash_back_amnt_d_c_c;
	}
	public void setCash_back_amnt_d_c_c(String cash_back_amnt_d_c_c) {
		this.cash_back_amnt_d_c_c = cash_back_amnt_d_c_c;
	}
	public String getAccess_fee_l() {
		return access_fee_l;
	}
	public void setAccess_fee_l(String access_fee_l) {
		this.access_fee_l = access_fee_l;
	}
	public String getAccess_fee_l_d_c() {
		return access_fee_l_d_c;
	}
	public void setAccess_fee_l_d_c(String access_fee_l_d_c) {
		this.access_fee_l_d_c = access_fee_l_d_c;
	}
	public String getCurrency_settlment() {
		return currency_settlment;
	}
	public void setCurrency_settlment(String currency_settlment) {
		this.currency_settlment = currency_settlment;
	}
	public String getImplied_dec_settlment() {
		return implied_dec_settlment;
	}
	public void setImplied_dec_settlment(String implied_dec_settlment) {
		this.implied_dec_settlment = implied_dec_settlment;
	}
	public String getConversion_rate() {
		return conversion_rate;
	}
	public void setConversion_rate(String conversion_rate) {
		this.conversion_rate = conversion_rate;
	}
	public String getCompltd_amt_settmnt() {
		return compltd_amt_settmnt;
	}
	public void setCompltd_amt_settmnt(String compltd_amt_settmnt) {
		this.compltd_amt_settmnt = compltd_amt_settmnt;
	}
	public String getCompltd_amnt_d_c() {
		return compltd_amnt_d_c;
	}
	public void setCompltd_amnt_d_c(String compltd_amnt_d_c) {
		this.compltd_amnt_d_c = compltd_amnt_d_c;
	}
	public String getInter_change_fee() {
		return inter_change_fee;
	}
	public void setInter_change_fee(String inter_change_fee) {
		this.inter_change_fee = inter_change_fee;
	}
	public String getInter_change_fee_d_c() {
		return inter_change_fee_d_c;
	}
	public void setInter_change_fee_d_c(String inter_change_fee_d_c) {
		this.inter_change_fee_d_c = inter_change_fee_d_c;
	}
	public String getService_lev_ind() {
		return service_lev_ind;
	}
	public void setService_lev_ind(String service_lev_ind) {
		this.service_lev_ind = service_lev_ind;
	}
	public String getResp_code1() {
		return resp_code1;
	}
	public void setResp_code1(String resp_code1) {
		this.resp_code1 = resp_code1;
	}
	public String getFiler() {
		return filer;
	}
	public void setFiler(String filer) {
		this.filer = filer;
	}
	public String getPositive_id_ind() {
		return positive_id_ind;
	}
	public void setPositive_id_ind(String positive_id_ind) {
		this.positive_id_ind = positive_id_ind;
	}
	public String getAtm_surcharge_free() {
		return atm_surcharge_free;
	}
	public void setAtm_surcharge_free(String atm_surcharge_free) {
		this.atm_surcharge_free = atm_surcharge_free;
	}
	public String getCross_bord_ind() {
		return cross_bord_ind;
	}
	public void setCross_bord_ind(String cross_bord_ind) {
		this.cross_bord_ind = cross_bord_ind;
	}
	public String getCross_bord_currency_in() {
		return cross_bord_currency_in;
	}
	public void setCross_bord_currency_in(String cross_bord_currency_in) {
		this.cross_bord_currency_in = cross_bord_currency_in;
	}
	public String getVisa_ias() {
		return visa_ias;
	}
	public void setVisa_ias(String visa_ias) {
		this.visa_ias = visa_ias;
	}
	public String getReq_amnt_tran() {
		return req_amnt_tran;
	}
	public void setReq_amnt_tran(String req_amnt_tran) {
		this.req_amnt_tran = req_amnt_tran;
	}
	public String getFiler1() {
		return filer1;
	}
	public void setFiler1(String filer1) {
		this.filer1 = filer1;
	}
	public String getTrace_num_adj() {
		return trace_num_adj;
	}
	public void setTrace_num_adj(String trace_num_adj) {
		this.trace_num_adj = trace_num_adj;
	}
	public String getFiler2() {
		return filer2;
	}
	public void setFiler2(String filer2) {
		this.filer2 = filer2;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getFiledate_1() {
		return filedate_1;
	}
	public void setFiledate_1(String filedate_1) {
		this.filedate_1 = filedate_1;
	}
	public String getPart_id() {
		return part_id;
	}
	public void setPart_id(String part_id) {
		this.part_id = part_id;
	}
	private String resp_code1     ;       
	private String filer           ;      
	private String positive_id_ind    ;   
	private String atm_surcharge_free   ;
	private String cross_bord_ind       ; 
	private String cross_bord_currency_in;
	private String visa_ias   ;           
	private String req_amnt_tran  ;       
	private String filer1   ;             
	private String trace_num_adj     ;    
	private String filer2  ;              
	private String type    ;              
	private String filedate_1 ;           
	private String part_id;   
	
	
	
	
	
	
	public String getStSelectedFile() {
		return StSelectedFile;
	}
	public void setStSelectedFile(String stSelectedFile) {
		StSelectedFile = stSelectedFile;
	}
	private String stCategory;
	private String stSubCategory;
		
	
	private List<List<String>> stTTUM_Records;
	private List<List<String>> stTTUM_DRecords;
	private List<String> stRemarks;
	private String stStart_Date;
	private String stEnd_Date;
	
	//Rupay setters
	private String stDate;
	private String stGLAccount;
	private String stDebitAcc;
	private String stCreditAcc;
	private String stAmount;
	private String stCard_Number;
	private String stPart_Tran_Type;
	private String stRemark;
	private String stTran_particulars;
	private int inRec_Set_Id;
	private String stEntry_by;
	
	private String stFile_Name;
	private String stMerger_Category;
	
	public List<String> getStExcelHeader() {
		return stExcelHeader;
	}
	public void setStExcelHeader(List<String> stExcelHeader) {
		this.stExcelHeader = stExcelHeader;
	}
	public String getStFile_header() {
		return stFile_header;
	}
	public void setStFile_header(String stFile_header) {
		this.stFile_header = stFile_header;
	}
	public String getStHeader_Value() {
		return stHeader_Value;
	}
	public void setStHeader_Value(String stHeader_Value) {
		this.stHeader_Value = stHeader_Value;
	}
	public String getStStart_charpos() {
		return stStart_charpos;
	}
	public void setStStart_charpos(String stStart_charpos) {
		this.stStart_charpos = stStart_charpos;
	}
	public String getStChar_Size() {
		return stChar_Size;
	}
	public void setStChar_Size(String stChar_Size) {
		this.stChar_Size = stChar_Size;
	}
	public String getStPadding() {
		return stPadding;
	}
	public void setStPadding(String stPadding) {
		this.stPadding = stPadding;
	}
	public String getStExcel_Header() {
		return stExcel_Header;
	}
	public void setStExcel_Header(String stExcel_Header) {
		this.stExcel_Header = stExcel_Header;
	}
	public String getStRemove_char() {
		return stRemove_char;
	}
	public void setStRemove_char(String stRemove_char) {
		this.stRemove_char = stRemove_char;
	}
	public List<List<String>> getStTTUM_Records() {
		return stTTUM_Records;
	}
	public void setStTTUM_Records(List<List<String>> stTTUM_Records) {
		this.stTTUM_Records = stTTUM_Records;
	}
	public List<List<String>> getStTTUM_DRecords() {
		return stTTUM_DRecords;
	}
	public void setStTTUM_DRecords(List<List<String>> stTTUM_DRecords) {
		this.stTTUM_DRecords = stTTUM_DRecords;
	}
	/*public String getStSelectedFile() {
		return stSelectedFile;
	}*/
	/*public void setStSelectedFile(String stSelectedFile) {
		this.stSelectedFile = stSelectedFile;
	}*/
	public List<String> getStRemarks() {
		return stRemarks;
	}
	public void setStRemarks(List<String> stRemarks) {
		this.stRemarks = stRemarks;
	}
	public String getStCategory() {
		return stCategory;
	}
	public void setStCategory(String stCategory) {
		this.stCategory = stCategory;
	}
	/*public String getStFromDate() {
		return stFromDate;
	}
	public void setStFromDate(String stFromDate) {
		this.stFromDate = stFromDate;
	}*/
	/*public String getStToDate() {
		return stToDate;
	}
	public void setStToDate(String stToDate) {
		this.stToDate = stToDate;
	}*/
	public String getStSubCategory() {
		return stSubCategory;
	}
	public void setStSubCategory(String stSubCategory) {
		this.stSubCategory = stSubCategory;
	}
	public String getStFile_Name() {
		return stFile_Name;
	}
	public void setStFile_Name(String stFile_Name) {
		this.stFile_Name = stFile_Name;
	}
	public String getStDate() {
		return stDate;
	}
	public void setStDate(String stDate) {
		this.stDate = stDate;
	}
	public String getStGLAccount() {
		return stGLAccount;
	}
	public void setStGLAccount(String stGLAccount) {
		this.stGLAccount = stGLAccount;
	}
	public String getStAmount() {
		return stAmount;
	}
	public void setStAmount(String stAmount) {
		this.stAmount = stAmount;
	}
	public String getStPart_Tran_Type() {
		return stPart_Tran_Type;
	}
	public void setStPart_Tran_Type(String stPart_Tran_Type) {
		this.stPart_Tran_Type = stPart_Tran_Type;
	}
	public String getStDebitAcc() {
		return stDebitAcc;
	}
	public void setStDebitAcc(String stDebitAcc) {
		this.stDebitAcc = stDebitAcc;
	}
	public String getStCreditAcc() {
		return stCreditAcc;
	}
	public void setStCreditAcc(String stCreditAcc) {
		this.stCreditAcc = stCreditAcc;
	}
	public String getStRemark() {
		return stRemark;
	}
	public void setStRemark(String stRemark) {
		this.stRemark = stRemark;
	}
	public String getStTran_particulars() {
		return stTran_particulars;
	}
	public void setStTran_particulars(String stTran_particulars) {
		this.stTran_particulars = stTran_particulars;
	}
	public String getStEntry_by() {
		return stEntry_by;
	}
	public void setStEntry_by(String stEntry_by) {
		this.stEntry_by = stEntry_by;
	}
	public int getInRec_Set_Id() {
		return inRec_Set_Id;
	}
	public void setInRec_Set_Id(int inRec_Set_Id) {
		this.inRec_Set_Id = inRec_Set_Id;
	}
	public String getStStart_Date() {
		return stStart_Date;
	}
	public void setStStart_Date(String stStart_Date) {
		this.stStart_Date = stStart_Date;
	}
	public String getStEnd_Date() {
		return stEnd_Date;
	}
	public void setStEnd_Date(String stEnd_Date) {
		this.stEnd_Date = stEnd_Date;
	}
	public String getStMerger_Category() {
		return stMerger_Category;
	}
	public void setStMerger_Category(String stMerger_Category) {
		this.stMerger_Category = stMerger_Category;
	}
	public String getStCard_Number() {
		return stCard_Number;
	}
	public void setStCard_Number(String stCard_Number) {
		this.stCard_Number = stCard_Number;
	}
	public String getStFile_Date() {
		return stFile_Date;
	}
	public void setStFile_Date(String stFile_Date) {
		this.stFile_Date = stFile_Date;
	}
	public String getStFunctionCode() {
		return stFunctionCode;
	}
	public void setStFunctionCode(String stFunctionCode) {
		this.stFunctionCode = stFunctionCode;
	}
	public String getAccount_repo() {
		return account_repo;
	}
	public void setAccount_repo(String account_repo) {
		this.account_repo = account_repo;
	}
	
	
	
	
}
