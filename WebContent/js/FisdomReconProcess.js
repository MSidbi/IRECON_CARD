function getupldfiledetails(){
	
	
	window.open("../DebitCard_Recon/GetUplodedFile.do" , 'window', 'width=1000,height=500,location=no,toolbar=no,menubar=no,scrollbars=yes,resizable=no');

	
	
}

function Validate(event) {
	debugger;
	//alert("jjh");
    var regex = new RegExp("^[0-9-!@#$%*?.]");
    var key = String.fromCharCode(event.charCode ? event.which : event.charCode);
    if (!regex.test(key)) {
        event.preventDefault();
        alert("Only decimal values are allowed");
        return false;
    }
}



function Process() {
	//alert("HELLO1");
	var datepicker = document.getElementById("fileDate").value;
	//alert("DONE");
	if(ValidateData())
	{
		
		$.ajax({

			type:'POST',
			url :'FisdomReconProcess.do',
			async: true,
			beforeSend : function() {
				showLoader();
			},
			complete : function(data) {

				hideLoader();

			},

			data:{fileDate:datepicker},
			success:function(response){

				alert(response);
				 
				 document.getElementById("fileDate").value="";
				

			},error: function(){

				alert("Error Occured");

			},

		});
	}
	
}


function seerule(e) {
	
	document.getElementById("fileValue").value=e;
	
	window.open("../DebitCard_Recon/SeeRule.do" , 'SeeRule', 'width=1000,height=500,location=no,toolbar=no,menubar=no,scrollbars=yes,resizable=no');
}


function showLoader(location) {
	
	$("#Loader").show();
}

function hideLoader(location) {
	
	$("#Loader").hide();
}
//ADDED BY INT5779 FOR GETTING SUB CATEGORIES DYNAMICALLY
function getSubCategory()
{
	debugger;
	
//	alert("HELLO");
	var category  = document.getElementById("rectyp").value;
	if(category != "MASTERCARD")
		{
		document.getElementById("dollar").style.display="none";
		}
	else{
		document.getElementById("dollar").style.display="";
	}
	//alert("category is "+category);
	if(category!="" && (category != "ONUS" && category != "AMEX" && category != "CARDTOCARD" && category != "WCC")) { 
		document.getElementById("trsubcat").style.display="";
		$.ajax({
			 
			 type:'POST',
			 url :'getSubCategorydetails.do',
			 data:{category:category},
			 success:function(response){
				 
				
				 var length =response.Subcategories.length;
				
				
				 var compareFile1 = document.getElementById("stSubCategory");
				
		 
				 var rowcount = compareFile1.childNodes.length;
					
					for(var j =1;j<=rowcount;j++ )
					{
						compareFile1.removeChild(compareFile1.lastChild);
						//compareFile2.removeChild(compareFile2.lastChild);
					}
				
				 var option1= document.createElement("option");
				 option1.value="-";
				 option1.text="--Select--";
				 var opt1= document.createElement("option");
				 opt1.value="-";
				 opt1.text="--Select--";
				 compareFile1.appendChild(option1);
				//compareFile2.appendChild(opt1)
				 
				 for(var i =0;i<length;i++ ) {
					
					 var option= document.createElement("option");
					  option.value = response.Subcategories[i];
					 option.text= response.Subcategories[i];
					 //alert("check this "+option.text);
					 if(option.text != "SURCHARGE")
					 compareFile1.appendChild(option);
				 }
				/* for(var i =0;i<length;i++ ) {
						
					 var option= document.createElement("option")
					  option.value = response.Records[i].inFileId;
					 option.text= response.Records[i].stFileName;
						compareFile2.appendChild(option)
				 }*/
				
				 //document.getElementById("trbtn").style.display="none";
				// document.getElementById("stCategorynew").disabled="disabled";
				 //document.getElementById("SubCat").disabled="disabled";
				 displayContent();
							 

			 },error: function(){
				
				 alert("Error Occured");
				 
			 },
			 
		 });
		}else {
			//alert("INSIDE ELSE");
			document.getElementById("trsubcat").style.display="none";
			//document.getElementById("stSubCategory").value="-";
			$('#trsubcat').val('-');
			$('#stSubCategory').val('-');
			//alert("check it "+document.getElementById("stSubCategory").value);
			/*var subcate = $("#stSubCategory").val("-");
			alert("subcate is.............. "+subcate);*/
			
			
		//	alert("document.getElementById().value "+document.getElementById("stSubCategory").value );
			//getFilesdata();
			
			//alert("Please Select Category.");
			
		}
	
}
/*function chngsubcat(e){
	
	if(e.value=="RUPAY" || e.value == "VISA" || e.value == "POS"){
			
			document.getElementById("trsubcat").style.display="";
		}else{
			
			document.getElementById("trsubcat").style.display="none";
			document.getElementById("SubCat").value="-";
			
		}
		
		
		
	}*/

function ValidateData()
{
	//alert("Inside ValidateData() ");
	var datepicker = document.getElementById("fileDate").value;
	//alert("subcategory "+subcategory);
	/*alert("done");
	alert("category "+category);
	alert("subcategory "+subcategory);
	alert("date "+datepicker);*/
	
	debugger;
	
	if(datepicker == "")
	{
		alert("Please select date for processing");
		return false;
	}
	
	return true;
	
}