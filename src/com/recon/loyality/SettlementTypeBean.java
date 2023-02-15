package com.recon.loyality;

import java.util.List;

public class SettlementTypeBean {
	
	public String getVisa_card_no() {
		return visa_card_no;
	}
	public void setVisa_card_no(String visa_card_no) {
		this.visa_card_no = visa_card_no;
	}
	public String getMobile_no() {
		return mobile_no;
	}
	public void setMobile_no(String mobile_no) {
		this.mobile_no = mobile_no;
	}
	public String getSol_id() {
		return sol_id;
	}
	public void setSol_id(String sol_id) {
		this.sol_id = sol_id;
	}
	public String getDebit_acc() {
		return debit_acc;
	}
	public void setDebit_acc(String debit_acc) {
		this.debit_acc = debit_acc;
	}
	public String getAcc_name() {
		return acc_name;
	}
	public void setAcc_name(String acc_name) {
		this.acc_name = acc_name;
	}
	public String getPayment_id() {
		return payment_id;
	}
	public void setPayment_id(String payment_id) {
		this.payment_id = payment_id;
	}
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public String getDate_time() {
		return date_time;
	}
	public void setDate_time(String date_time) {
		this.date_time = date_time;
	}
	public String getCreatedon() {
		return createdon;
	}
	public void setCreatedon(String createdon) {
		this.createdon = createdon;
	}
	private List<String> excel_bean;
	public List<String> getExcel_bean() {
		return excel_bean;
	}
	public void setExcel_bean(List<String> excel_bean) {
		this.excel_bean = excel_bean;
	}
	private String inr;
	public String getInr() {
		return inr;
	}
	public void setInr(String inr) {
		this.inr = inr;
	}
	private String cREATEDDATE;	
	private String cREATEDBY;	
	private String  dCR_TRAN_ID	;
	private String rEMARKS;
	private String mAN_CONTRA_ACCOUNT;
	private String aCCOUNT_NUMBER;
	private String tRANDATE;
	private String vALUEDATE;
	private String tRAN_ID;
	private String tRAN_PARTICULAR;
	private String tRAN_RMKS;
	private String pART_TRAN_TYPE;
	private String tRAN_PARTICULAR1;
	private String tRAN_AMT;
	private String bALANCE;
	private String pSTD_USER_ID;
	private String cONTRA_ACCOUNT;
	private String eNTRY_DATE;
	private String vFD_DATE;
	private String rEF_NUM;
	private String e;
	private String PARTICULARALS2;
	private String swicth_serial_num ;     
	private String processor_a_i ;         
	private String processor_id ;          
	private String tran_date   ;           
	private String tran_time   ;           
	private String pan_length ;            
	private String pan_num      ;          
	private String proccessing_code  ;     
	private String trace_num   ;           
	private String mercahnt_type  ;        
	private String pos_entry  ;            
	private String ref_no   ;              
	private String aquirer_i_id  ;         
	private String terminal_id  ;          
	private String brand  ;                
	private String advaice_reg_code  ;     
	private String intra_curr_aggrmt_code ;
	private String auth_id    ;            
	private String currency_code ;         
	private String implied_dec_tran   ;    
	private String compltd_amnt_tran    ;  
	private String compltd_amnt_tran_d_c  ;
	private String cash_back_amnt_l   ;    
	private String cash_back_amnt_d_c_c;   
	private String access_fee_l  ;         
	private String access_fee_l_d_c   ;    
	private String currency_settlment   ;  
	private String implied_dec_settlment ; 
	private String conversion_rate  ;      
	private String compltd_amt_settmnt  ;  
	private String compltd_amnt_d_c  ;     
	private String inter_change_fee   ;    
	private String inter_change_fee_d_c  ; 
	private String service_lev_ind ;       
	private String resp_code1   ;          
	private String filer    ;              
	private String positive_id_ind ;       
	private String atm_surcharge_free ;    
	private String cross_bord_ind   ;      
	private String cross_bord_currency_ind;
	private String visa_ias  ;             
	private String req_amnt_tran  ;        
	private String filer1   ;              
	private String trace_num_adj ;         
	private String filer2 ;                
	private String type;                   
	private String filedate_1 ;            
	private String part_id;                
	private String processing_code ; 
	private String type_p ; 
	private String dcrs_tran_no;
	private String next_tran_date;
	private String cbs_amount;
	private String cbs_contra    ;      
	private String settlement_amount ;  
	private String settlement_curr_code;
	private String currency_amount ;    
	private String variation;   
	private String file_name;
	
	private String cardNumb;
	private String arn_Numb;
	private String amount_val;
	private String visa_card_no;
	private String mobile_no;     
	//private String amount;        
	private String sol_id ;
	private String debit_acc;     
	private String acc_name;      
	private String payment_id;    
	private String channel ;      
	private String date_time;     
	//private String filedate ;     
	private String createdon;     
	//private String dcrs_remarks ; 
	public String getCardNumb() {
		return cardNumb;
	}
	public void setCardNumb(String cardNumb) {
		this.cardNumb = cardNumb;
	}
	
	public String getArn_Numb() {
		return arn_Numb;
	}
	public void setArn_Numb(String arn_Numb) {
		this.arn_Numb = arn_Numb;
	}
	public String getAmount_val() {
		return amount_val;
	}
	public void setAmount_val(String amount_val) {
		this.amount_val = amount_val;
	}
	public String getFile_name() {
		return file_name;
	}
	public void setFile_name(String file_name) {
		this.file_name = file_name;
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
	public String getSettlement_curr_code() {
		return settlement_curr_code;
	}
	public void setSettlement_curr_code(String settlement_curr_code) {
		this.settlement_curr_code = settlement_curr_code;
	}
	public String getCurrency_amount() {
		return currency_amount;
	}
	public void setCurrency_amount(String currency_amount) {
		this.currency_amount = currency_amount;
	}
	public String getVariation() {
		return variation;
	}
	public void setVariation(String variation) {
		this.variation = variation;
	}
	public String getNext_tran_date() {
		return next_tran_date;
	}
	public void setNext_tran_date(String next_tran_date) {
		this.next_tran_date = next_tran_date;
	}
	public String getDcrs_tran_no() {
		return dcrs_tran_no;
	}
	public void setDcrs_tran_no(String dcrs_tran_no) {
		this.dcrs_tran_no = dcrs_tran_no;
	}
	public String getType_p() {
		return type_p;
	}
	public void setType_p(String type_p) {
		this.type_p = type_p;
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
	private String amount_recon  ;      
	private String conv_rate_recon;     
	private String date_val    ;        
	private String expire_date ;        
	private String data_code   ;        
	private String card_seq_num ;       
	private String funcation_code;      
	private String msg_res_code  ;      
	private String card_acc_code ;      
	private String amount_org   ;       
	private String aquierer_ref_no;     
	private String fi_id_code    ;      
	private String retrv_ref_no   ;     
	private String approval_code  ;     
	private String service_code    ;    
	private String card_acc_term_id ;   
	private String card_acc_id_code ;   
	private String additional_data ;    
	private String currency_code_tran ; 
	private String currency_code_recon ;
	private String tran_lifecycle_id ;  
	private String msg_num ;            
	private String date_action ;        
	private String tran_dest_id_code;   
	private String tran_org_id_code ;   
	private String card_iss_ref_data;   
	private String recv_inst_idcode;    
	private String terminal_type;       
	private String elec_com_indic ;     
	private String processing_mode ;    
	private String currency_exponent;   
	private String business_act ;       
	private String settlement_ind ;     
	private String card_accp_name_loc ; 
	private String header_type; 
	
	
	
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
	public String getTran_date() {
		return tran_date;
	}
	public void setTran_date(String tran_date) {
		this.tran_date = tran_date;
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
	public String getRef_no() {
		return ref_no;
	}
	public void setRef_no(String ref_no) {
		this.ref_no = ref_no;
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
	public String getCurrency_code() {
		return currency_code;
	}
	public void setCurrency_code(String currency_code) {
		this.currency_code = currency_code;
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
	public String getCross_bord_currency_ind() {
		return cross_bord_currency_ind;
	}
	public void setCross_bord_currency_ind(String cross_bord_currency_ind) {
		this.cross_bord_currency_ind = cross_bord_currency_ind;
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
	public String getPARTICULARALS2() {
		return PARTICULARALS2;
	}
	public void setPARTICULARALS2(String pARTICULARALS2) {
		PARTICULARALS2 = pARTICULARALS2;
	}
	public String getCreateddate() {
		return createddate;
	}
	public void setCreateddate(String createddate) {
		this.createddate = createddate;
	}
	public String getCreatedby() {
		return createdby;
	}
	public void setCreatedby(String createdby) {
		this.createdby = createdby;
	}
	private String tRAN_PARTICULAR_2;
	private String oRG_ACCT;
	private String columns;
	private String pan;
	private String datepicker;
	private String dataType;
	private String setltbl;
	private String searchValue;
	private String fileDate;
	private String category;
	private String foracid;
	private String tran_Date;
	private String particulars;
	private String category_new;
	private String   createddate   ;   
	private String	createdby ;       
	
	public String getFiledate() {
		return filedate;
	}
	public void setFiledate(String filedate) {
		this.filedate = filedate;
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
	private String particularals; 
	private String stSubCategory;
	private String	filedate  ;       
	private String	dcrs_remarks ;    
	private String	seg_tran_id  ;    
	private String	msgtype;          
	private String	termid ;          
	private String	local_date ;      
	private String	local_time ;      
	private String	pcode ;           
	private String	trace  ;          
	private String	amount  ;         
	private String	acceptorname ;    
	private String	respcode;         
	private String	termloc ;         
	private String	new_amount   ;    
	private String txnsrc ;          
	private String txndest  ;        
	private String revcode   ;       
	private String amount_equiv  ;   
	private String ch_amount    ;    
	private String settlement_date;  
	private String iss_currency_code;
	private String acq_currency_code;
	private String merchant_type ;   
	private String authnum  ;        
	private String acctnum  ;        
	private String trans_id  ;       
	private String acquirer  ;       
	private String pan2     ;        
	private String issuer  ;         
	private String refnum  ;
	//private String setltbl;
	
	public String getParticularals() {
		return particularals;
	}
	public void setParticularals(String particularals) {
		this.particularals = particularals;
	}
	public String getCategory_new() {
		return category_new;
	}
	public void setCategory_new(String category_new) {
		this.category_new = category_new;
	}
	public String getForacid() {
		return foracid;
	}
	public void setForacid(String foracid) {
		this.foracid = foracid;
	}
	public String getTran_Date() {
		return tran_Date;
	}
	public void setTran_Date(String tran_Date) {
		this.tran_Date = tran_Date;
	}
	public String getDcrs_remarks() {
		return dcrs_remarks;
	}
	public void setDcrs_remarks(String dcrs_remarks) {
		this.dcrs_remarks = dcrs_remarks;
	}
	public String getParticulars() {
		return particulars;
	}
	public void setParticulars(String particulars) {
		this.particulars = particulars;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getPan() {
		return pan;
	}
	public void setPan(String pan) {
		this.pan = pan;
	}
	public String gettERMID() {
		return tERMID;
	}
	public void settERMID(String tERMID) {
		this.tERMID = tERMID;
	}
	public String gettRACE() {
		return tRACE;
	}
	public void settRACE(String tRACE) {
		this.tRACE = tRACE;
	}
	private String tERMID,tRACE;
	
	
	
	public String getcREATEDDATE() {
		return cREATEDDATE;
	}
	public void setcREATEDDATE(String cREATEDDATE) {
		this.cREATEDDATE = cREATEDDATE;
	}
	public String getcREATEDBY() {
		return cREATEDBY;
	}
	public void setcREATEDBY(String cREATEDBY) {
		this.cREATEDBY = cREATEDBY;
	}
	public String getdCR_TRAN_ID() {
		return dCR_TRAN_ID;
	}
	public void setdCR_TRAN_ID(String dCR_TRAN_ID) {
		this.dCR_TRAN_ID = dCR_TRAN_ID;
	}
	public String getrEMARKS() {
		return rEMARKS;
	}
	public void setrEMARKS(String rEMARKS) {
		this.rEMARKS = rEMARKS;
	}
	public String getmAN_CONTRA_ACCOUNT() {
		return mAN_CONTRA_ACCOUNT;
	}
	public void setmAN_CONTRA_ACCOUNT(String mAN_CONTRA_ACCOUNT) {
		this.mAN_CONTRA_ACCOUNT = mAN_CONTRA_ACCOUNT;
	}
	public String getaCCOUNT_NUMBER() {
		return aCCOUNT_NUMBER;
	}
	public void setaCCOUNT_NUMBER(String aCCOUNT_NUMBER) {
		this.aCCOUNT_NUMBER = aCCOUNT_NUMBER;
	}
	public String gettRANDATE() {
		return tRANDATE;
	}
	public void settRANDATE(String tRANDATE) {
		this.tRANDATE = tRANDATE;
	}
	public String getvALUEDATE() {
		return vALUEDATE;
	}
	public void setvALUEDATE(String vALUEDATE) {
		this.vALUEDATE = vALUEDATE;
	}
	public String gettRAN_ID() {
		return tRAN_ID;
	}
	public void settRAN_ID(String tRAN_ID) {
		this.tRAN_ID = tRAN_ID;
	}
	public String gettRAN_PARTICULAR() {
		return tRAN_PARTICULAR;
	}
	public void settRAN_PARTICULAR(String tRAN_PARTICULAR) {
		this.tRAN_PARTICULAR = tRAN_PARTICULAR;
	}
	public String gettRAN_RMKS() {
		return tRAN_RMKS;
	}
	public void settRAN_RMKS(String tRAN_RMKS) {
		this.tRAN_RMKS = tRAN_RMKS;
	}
	public String getpART_TRAN_TYPE() {
		return pART_TRAN_TYPE;
	}
	public void setpART_TRAN_TYPE(String pART_TRAN_TYPE) {
		this.pART_TRAN_TYPE = pART_TRAN_TYPE;
	}
	public String gettRAN_PARTICULAR1() {
		return tRAN_PARTICULAR1;
	}
	public void settRAN_PARTICULAR1(String tRAN_PARTICULAR1) {
		this.tRAN_PARTICULAR1 = tRAN_PARTICULAR1;
	}
	public String gettRAN_AMT() {
		return tRAN_AMT;
	}
	public void settRAN_AMT(String tRAN_AMT) {
		this.tRAN_AMT = tRAN_AMT;
	}
	public String getbALANCE() {
		return bALANCE;
	}
	public void setbALANCE(String bALANCE) {
		this.bALANCE = bALANCE;
	}
	public String getpSTD_USER_ID() {
		return pSTD_USER_ID;
	}
	public void setpSTD_USER_ID(String pSTD_USER_ID) {
		this.pSTD_USER_ID = pSTD_USER_ID;
	}
	public String getcONTRA_ACCOUNT() {
		return cONTRA_ACCOUNT;
	}
	public void setcONTRA_ACCOUNT(String cONTRA_ACCOUNT) {
		this.cONTRA_ACCOUNT = cONTRA_ACCOUNT;
	}
	public String geteNTRY_DATE() {
		return eNTRY_DATE;
	}
	public void seteNTRY_DATE(String eNTRY_DATE) {
		this.eNTRY_DATE = eNTRY_DATE;
	}
	public String getE() {
		return e;
	}
	public void setE(String e) {
		this.e = e;
	}
	public String getvFD_DATE() {
		return vFD_DATE;
	}
	public void setvFD_DATE(String vFD_DATE) {
		this.vFD_DATE = vFD_DATE;
	}
	public String getrEF_NUM() {
		return rEF_NUM;
	}
	public void setrEF_NUM(String rEF_NUM) {
		this.rEF_NUM = rEF_NUM;
	}
	public String gettRAN_PARTICULAR_2() {
		return tRAN_PARTICULAR_2;
	}
	public void settRAN_PARTICULAR_2(String tRAN_PARTICULAR_2) {
		this.tRAN_PARTICULAR_2 = tRAN_PARTICULAR_2;
	}
	public String getoRG_ACCT() {
		return oRG_ACCT;
	}
	public void setoRG_ACCT(String oRG_ACCT) {
		this.oRG_ACCT = oRG_ACCT;
	}
	public String getColumns() {
		return columns;
	}
	public void setColumns(String columns) {
		this.columns = columns;
	}
	public String getDatepicker() {
		return datepicker;
	}
	public void setDatepicker(String datepicker) {
		this.datepicker = datepicker;
	}
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public String getSetltbl() {
		return setltbl;
	}
	public void setSetltbl(String setltbl) {
		this.setltbl = setltbl;
	}
	public String getSearchValue() {
		return searchValue;
	}
	public void setSearchValue(String searchValue) {
		this.searchValue = searchValue;
	}
	public String getFileDate() {
		return fileDate;
	}
	public void setFileDate(String fileDate) {
		this.fileDate = fileDate;
	}
	public String getStSubCategory() {
		return stSubCategory;
	}
	public void setStSubCategory(String stSubCategory) {
		this.stSubCategory = stSubCategory;
	}
	
	
	
	
	

}
