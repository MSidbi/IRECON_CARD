

function noBack()
{
	window.history.forward();
   
} 
function hideData(e){
	
		document.getElementById("filelist").style.display="none";
		document.getElementById("getDetails").style.display="none";
	return true;
	
}

function getFiles() {
	
	debugger;
	var type = document.getElementById("category").value;
	var subcat = document.getElementById("stSubCategory").value;

	if(type!="") {
		
		if(type=="ONUS") {
			
			displyfile(type, "");
			
		}
		else if(type=="AMEX") {
			
			displyfile(type, "");
			
		}
		else if(type=="RUPAY" || type == "VISA" || type == "MASTERCARD" || type == "POS") {
			
			if(subcat!="") {
				
				displyfile(type, subcat);
			}else {
				
				alert("Please Select Sub Category");
			}
		}
		
	}else {
		
		alert("Please Select Valid Category");
	}
}

function displyfile(category,subcategory){
	
	
	$.ajax({
		 
		 type:"POST",
		 url :'getCompareFiles.do',
		 data:{type:category,subcat:subcategory},
		 success:function(filelist){
			 
			 debugger;
			 if(filelist!=null){
				var selFillist = document.getElementById("selFillist"); 
				
				$('#selFillist').empty();
				
				var option = document.createElement("option");
				option.value= "";
				option.text="--SELECT--"
				selFillist.appendChild(option);
				
				for(var i= 0;i<filelist.length;i++) {
				
					
					var option = document.createElement("option");
					option.value= filelist[i].rec_set_id;
					option.text=filelist[i].compreFileName1+" AND " +filelist[i].compreFileName2;
					selFillist.appendChild(option);
					
				
				}
					
			 }
		 },error: function(){
			
			 alert("Error Occured");
			 
		 },
		 
	 });
	
	document.getElementById("filelist").style.display="";
	document.getElementById("getDetails").style.display="";
	
}

function displaydtls(e){
	
	debugger;
	 if(e.value=="RUPAY" || e.value =="VISA" || e.value =="MASTERCARD" || e.value =="POS") {
		
		document.getElementById("trsubcat").style.display="";
		document.getElementById("filelist").style.display="none";
		document.getElementById("getDetails").style.display="none";	
			
	} else {
		
		document.getElementById("trsubcat").style.display="none"
		document.getElementById("filelist").style.display="none";
		document.getElementById("getDetails").style.display="none";
	}
	
	
	//trsubcat
	
}
function getCompareDetails(){
	
	debugger;
	var recid = document.getElementById("selFillist").value;
	var categ = document.getElementById("category").value;
	
	//alert(categ);
	
	if( recid !=''){
	
		window.open("../DebitCard_Recon/ViewCompareDetls.do?recId="+recid+"&Cate="+categ, 'window', 'width=1000,height=500,location=no,toolbar=no,menubar=no,scrollbars=yes,resizable=no');
	}else {
		
		alert("Select File ");
	}
	
}