$(function(){
	
	//alert("HELLOOOOO");
	//$( "input[type=submit], input[type=button], button" ).button();
	
	$('#next1').click(function(){
		
		var filename = document.getElementById("stFileName1").value;
		var fileLocation = document.getElementById("fileLocation").value;
		var filePath = document.getElementById("filePath").value;
		var ftpPort = document.getElementById("ftpPort").value;
		var ftpUser = document.getElementById("ftpUser").value;
		var ftpPwd = document.getElementById("ftpPwd").value;
		var msg="";
		if(filename==""){

			msg = msg+"Please enter Filename.\n";
		}if(fileLocation==""){

			msg = msg+"Please enter FTP Server Location.\n";
		
		}if(fileLocation!="") {
			
			var spPort = fileLocation.split(".")
		
			if(spPort.length < 4 || spPort.length > 4) {
				
				msg = msg+"Please enter valid FTP Server address.\n";
				
			} if(spPort.length == 4){
				
				if(spPort[0]>255 ||spPort[1]>255||spPort[2]>255||spPort[3]>255 ) {
					
					msg = msg+"Please enter FTP Server address between range of 1 to 255.\n";
				}
				
			}
			
			
			
		}if(filePath==""){

			msg = msg+"Please enter File Path.\n";
		}if(ftpPort==""){

			msg = msg+"Please enter FTP Port.\n";
		}
		
		if(ftpUser==""){

			msg = msg+"Please enter FTP User.\n";
		}if(ftpPwd==""){

			msg = msg+"Please enter FTP Password.\n";
		}
		
		if(msg!=""){
			
			alert(msg);
			result=false;
		}else{
			
			$('#id1').css("display", "none");
			$('#tab1').css('background-color','Green') ;
			$('#id2').css("display", "block");
			$('#id3').css("display", "none");
			
		}
		
	});
	
	$('#next2').click(function(){
		
		if(addtblheader()){
		$('#id1').css("display", "none");
		$('#tab2').css('background-color','Green') ;
		$('#id2').css("display", "none");
		$('#id3').css("display", "block");
		
		var splhdr = document.getElementById("tblHeader").value.split(',');
		
		var toAppend = document.getElementById('selectTd');
		toAppend.removeChild(toAppend.lastChild)
		var select = document.createElement('select');
		select.id ='comp_dtl_list0.stHeader';
		select.style.size="100px";
		toAppend.appendChild(select);
		
		var seloption = document.createElement('option');
		seloption.value = 'SELECT' ;
		seloption.text = '---Select---';
		select.appendChild(seloption);
		
		for(var i=0; i<splhdr.length; i++ ){
			
			var option = document.createElement('option');
			option.value = splhdr[i] ;
			option.text = splhdr[i];
			select.appendChild(option);
			
		}
		
		
		}else {
			return false;
		}
		
	});
	$('#prev2').click(function(){
		$('#id1').css("display", "block");
		$('#id2').css("display", "none");
		$('#id3').css("display", "none");
		
		
	});
	
	$('#prev3').click(function(){
		$('#id1').css("display", "none");
		
		$('#id2').css("display", "block");
		$('#id3').css("display", "none");
	
	});

	
});

 

function clearAll(){
	$('body').find('input:checkbox').removeAttr('checked');
	$('#allDiv').find('img').prop("src","images/chk_off.png");
	$('#allDiv :checkbox').prop("class","tri").removeAttr('checked');
}
