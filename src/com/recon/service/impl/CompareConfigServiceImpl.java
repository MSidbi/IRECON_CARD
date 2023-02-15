package com.recon.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.recon.dao.ICompareConfigDao;
import com.recon.model.CompareSetupBean;
import com.recon.model.ManualCompareBean;
import com.recon.model.Pos_Bean;
import com.recon.service.ICompareConfigService;

@Component
public class CompareConfigServiceImpl implements ICompareConfigService {

	@Autowired ICompareConfigDao compareDao;  
	
	@Override
	public List<CompareSetupBean> getFileDetails() {
		
		return compareDao.getFileDetails();
	}

	@Override
	public boolean saveCompareDetails(CompareSetupBean setupBean) throws Exception {
		
		return compareDao.saveCompareDetails(setupBean);
	}

	@Override
	public boolean chkMain_ReconSetupDetails(CompareSetupBean setupBean) {
		
		return compareDao.chkMain_ReconSetupDetails(setupBean);
	}

	@Override
	public ArrayList<CompareSetupBean> getCompareFiles(String type, String subcat) throws Exception {
		
		return compareDao.getCompareFiles(type, subcat);
	}

	@Override
	public ArrayList<CompareSetupBean> getmatchcrtlist(int rec_set_id,String Cate) throws Exception {
		
		return compareDao.getmatchcrtlist(rec_set_id,Cate);
	}

	
	@Override
	public ArrayList<CompareSetupBean> getrecparamlist(int rec_set_id,String Cate) throws Exception {
		
		return compareDao.getrecparamlist(rec_set_id,Cate);
	}

	@Override
	public ArrayList<CompareSetupBean> getFileList() {
		
		return compareDao.getFileList();
	}

	@Override
	public boolean chkFileupload(CompareSetupBean setupBean) throws Exception {
		
		return compareDao.chkFileupload(setupBean);
		
	}

	@Override
	public HashMap<String, Object> uploadFile(CompareSetupBean setupBean,MultipartFile file) throws Exception {
		
		
		return compareDao.uploadFile(setupBean,file);
	}

	

	@Override
	public boolean validateFile(CompareSetupBean setupBean, MultipartFile file) throws Exception {
		return compareDao.validateFile(setupBean,file);
		
	}

	@Override
	public List<CompareSetupBean> getlastUploadDetails() throws Exception {
		
		return compareDao.getlastUploadDetails();
	}

	@Override
	public boolean chkCompareFiles(CompareSetupBean setupBean) throws Exception {
	
		return compareDao.chkCompareFiles( setupBean);
	}
	
	@Override
	public boolean CheckAlreadyProcessed(CompareSetupBean setupBean) throws Exception
	{
		return compareDao.CheckAlreadyProcessed(setupBean);
	}

	@Override
	public String getTableName(int Fileid) throws Exception {
		
		
		return compareDao.getTableName(Fileid);
	}

	@Override
	public String chkFlag(String flag, CompareSetupBean setupBean) throws Exception {
		
		return compareDao.chkFlag(flag,setupBean);
	}

	@Override
	public boolean updateFlag(String flag, CompareSetupBean setupBean) throws Exception {
		
		return compareDao.updateFlag(flag,setupBean);
	}

	@Override
	public boolean validate_File(String Filedate, CompareSetupBean setupBean) throws Exception {
		
		return compareDao.validate_File(Filedate, setupBean);
	}

	@Override
	public boolean saveManCompareDetails(ManualCompareBean manualCompareBean ) throws Exception {
		
		return compareDao.saveManCompareDetails(manualCompareBean);
	}

	@Override
	public List<CompareSetupBean> getFileList(String category) {
		
		return  compareDao.getFileList(category);
	}

	@Override
	public ArrayList<CompareSetupBean> getmatchcondnlist(int rec_set_id,String Cate) throws Exception {
		// TODO Auto-generated method stub
		return compareDao.getmatchcondnlist(rec_set_id,Cate);
	}

	@Override
	public String chkUploadFlag(String flag, CompareSetupBean setupBean) {
		// TODO Auto-generated method stub
		return compareDao.chkUploadFlag(flag, setupBean);
	}
	
	@Override
	public boolean chkBeforeUploadFile(String flag, CompareSetupBean setupBean ) throws Exception {
		
		return compareDao.chkBeforeUploadFile(flag, setupBean ) ;
		
	}
	
	@Override
	public boolean insertFileTranDate( CompareSetupBean setupBean ) {
		
		return compareDao.insertFileTranDate( setupBean ) ;
		
	} 

	@Override
	public int getrecordcount(CompareSetupBean setupBean) {
		// TODO Auto-generated method stub
		return compareDao.getrecordcount(setupBean);
	}
	
	
	


	@Override
	public HashMap<String, Object> uploadREV_File(CompareSetupBean setupBean, MultipartFile file) {
		// TODO Auto-generated method stub
		return  compareDao.uploadREV_File( setupBean, file) ;
	}

	@Override
	public int getREVrecordcount(CompareSetupBean setupBean) {
		// TODO Auto-generated method stub
		return compareDao.getREVrecordcount(setupBean);
	}

	@Override
	public boolean insertUploadTRAN(CompareSetupBean setupBean) {
		
		return compareDao.insertUploadTRAN(setupBean);
	}

	@Override
	public ArrayList<Pos_Bean> getFileNameList(String filedate)
			throws Exception {
		// TODO Auto-generated method stub
		return compareDao.getFileNameList(filedate);
	}

	@Override
	public HashMap<String , Object>  checkUploadedFileName(String category, String subCateg, String fileName)
	{
		return compareDao.checkUploadedFileName(category, subCateg, fileName);
	}
	

	

}
