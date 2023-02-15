<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>ReconFileSetup</title>
</head>
<body onload="clearvalue();">



<div id="id1" style="display: block;">
	<div class="jtable-main-container">
				<div class="jtable-title">
					<div class="jtable-title-text">CONFIGURATION</div>
				</div>
	</div>
		<table align="center" >
			<tr>
				<th align="left">&nbsp;&nbsp;&nbsp;&nbsp;Category </th>
			 	<th align="right">&nbsp;:&nbsp;&nbsp;&nbsp;&nbsp;</th> 
					<td align="left">
						<form:select path="category" id="category" name="category">
						<form:option value="">--Select--</form:option>
						<form:option value="ONUS">ONUS</form:option>
						<form:option value="ACQUIRER">ACQUIRER</form:option>
						<form:option value="ISSUER">ISSUER</form:option>
					</form:select>
				</td>
			</tr>
			<tr class="evenRow">
				<th align="left">&nbsp;&nbsp;&nbsp;&nbsp;Recon Layer </th>
			 	<th align="right">&nbsp;:&nbsp;&nbsp;&nbsp;&nbsp;</th> 
					<td align="left">
						<form:select path="layerCount" id="layerCount" onchange="displayContent(this);">
							<form:option value="">--Select--</form:option>
							<form:option value="2">TWO</form:option>
							<form:option value="3">THREE</form:option>
					</form:select>
				</td>
			</tr>
			<tr class="evenRow" id="file1" style="display: none;">
				<th align="left">&nbsp;&nbsp;&nbsp;&nbsp;First File :</th>
			 	<th align="right">&nbsp;:&nbsp;&nbsp;&nbsp;&nbsp;</th> 
					<td align="left">
						<form:select path="compareFile1" id="compareFile1" onchange="setValue(this)">
							<form:option value="0">--Select--</form:option>
								<c:forEach var="file_list" items="${file_list}">
								<form:option id="${file_list.inFileId}" value="${file_list.inFileId}" >${file_list.stFileName}</form:option>
								</c:forEach>
							</form:select>
						
				</td>
			</tr>
			<tr class="evenRow" id="file2" style="display: none">
				<th align="left">&nbsp;&nbsp;&nbsp;&nbsp;Second File :</th>
			 	<th align="right">&nbsp;:&nbsp;&nbsp;&nbsp;&nbsp;</th> 
					<td align="left">
						<form:select path="compareFile2" id="compareFile2" onchange="setValue(this);">
							<form:option value="0">--Select--</form:option>
								<c:forEach var="file_list" items="${file_list}">
								<form:option id="${file_list.inFileId}" value="${file_list.inFileId}" >${file_list.stFileName}</form:option>
								</c:forEach>
					</form:select>
				</td>
			</tr>
			<tr class="evenRow" id="file3" style="display: none">
				<th align="left">&nbsp;&nbsp;&nbsp;&nbsp;Third File :</th>
			 	<th align="right">&nbsp;:&nbsp;&nbsp;&nbsp;&nbsp;</th> 
					<td align="left">
						<form:select path="compareFile3" id="compareFile3" onchange="setValue(this);">
							<form:option value="0">--Select--</form:option>
								<c:forEach var="file_list" items="${file_list}">
								<form:option id="${file_list.inFileId}" value="${file_list.inFileId}" >${file_list.stFileName}</form:option>
								</c:forEach>
					</form:select>
				</td>
			</tr>
			<!-- <tr><td><input class="form-button" type="button" value="Configure Column" onclick="displayDtl();"> </td></tr> -->
			
		</table>
		
		<!-- file_dtl_list -->
		
			
				
				<table id="coldtl" align="center" style="display: none" width="100%" border="1">
				
				
				</table>
				<input type="hidden" id="headerlist1" value="">
				<input type="hidden" id="headerlist2" value="">
				<input type="hidden" id="headerlist3" value="">
				<input class="form-button" align="left" type="button" id="nxt1" value="NEXT" onclick="displayDtl();">
				<input type="hidden" id="count" value="1"/><%-- ${fn:length(CompareSetupBean.columnDtls)} --%>
				
				<!-- <table>
				<tr>
					<td><input class="form-button" type="button" value="Add Row" onclick="addrow();"></td>
					<td><input class="form-button" type="button" id="nxt1" value="NEXT" onclick="displayDtl();"></td>
				</table> -->
				
		</div>
				
	<div id="id2" style="display: none;">
			
			
		<table align="center" id="displaytbl" style="size: 100%" border="1" rules="values">

			<tr style="size: 100%;">

				<!-- <th style="width: 50px">Matching id</th> -->
				<th style="width: 120px">File Name</th>
				<th style="width:100px">Headers</th>
				<th style="width: 80px">Value</th>
				<th>Condition </th>
				<th>Padding</th>
				<th style="width: 50px">Starting Position</th>
				<th style="width: 50px">Char Size</th>
				<th>Data Type</th>
				<th>Data Pattern</th>
				<th>Consider For Matching</th>

			</tr>

			<tr style="size: 100%;" id="First File">
				<!-- <td><input type="text" style="width: 50px;" readonly="readonly"	 id="reconcount" value="1" ></td> -->
				<td align="center" style="width: 250px;"><label  style="width: 150px;">File1 Headers</label></td>
				<td align="center"><select style="width:100px" id="fileselect1"><option	value="">--Select--</option></select></td>
				<td align="center" style="width: 80px"><input type="text"  style="width: 40px" id="value1" value=""	maxlength="6" cssClass="char_pos" onkeypress="return setValueType(this,'search')"> </td>
				<td align="center"><select id="condn1"><option value="">--Select--</option>
						<option value="=">=</option>
				 	<option value="!=">!=</option>
				 	<option value="LIKE">LIKE</option></select></td>
				<td align="center"><select id="staPdding1"><option value="">--Select--</option>
						<option value="Y">YES</option>
						<option value="N">NO</option></select></td>
				
				<td align="center" style="width: 50px" ><input type="text" style="width: 50px" id="startPos1"  value="0"	maxlength="6" cssClass="char_pos" onkeypress="return setValueType(this,'numeric')"></td>
				<td align="center" style="width: 50px"><input type="text" style="width: 50px" id="charsize1"  maxlength="6" cssClass="char_pos" value="0" onkeypress="return setValueType(this,'numeric')"></td>
				<td align="center"><select id="dataType1"><option
							value="">--Select--</option>
						<option value="DATE">DATE</option>
						<option value="TIME">TIME</option>
						<option value="NUMBER">NUMBER</option></select></td>
				<td align="center" style="width: 80px"><input style="width: 80px" type="text" id="datpattern1" value="0" onkeypress="return setValueType(this,'pattern')"></td>
				<td align="center"><select id="matchCondn1"><option
							value="">--Select--</option>
						<option value="Y">YES</option>
						<option value="N">NO</option></select></td>

			</tr>
			<tr style="size: 100%;" id="Second File">

				<!-- <td><input type="text" style="width: 50px;"	readonly="readonly"	 id="reconcount2" value="1"></td> -->
				<td align="center" style="width: 250px;"><label style="width: 150px;">File2 Headers</label></td>
				<td align="center" ><select style="width:100px" id="fileselect2"><option
							value="">--Select--</option></select></td>
				<td align="center"  style="width: 80px"><input type="text"  style="width: 40px" id="value2" value="" maxlength="6" cssClass="char_pos" onkeypress="return setValueType(this,'search')"> </td>
				<td align="center"><select id="condn2"><option value="">--Select--</option>
						<option value="=">=</option>
				 	<option value="!=">!=</option>
				 	<option value="LIKE">LIKE</option></select></td>
				<td align="center"><select id="staPdding2"><option
							value="">--Select--</option>
						<option value="Y">YES</option>
						<option value="N">NO</option></select></td>
				
				<td align="center" style="width: 50px"><input style="width: 50px" type="text" id="startPos2" onkeypress="return setValueType(this,'numeric')" value=0 maxlength="6" cssClass="char_pos"></td>
				<td align="center" style="width: 50px"><input style="width: 50px" type="text" id="charsize2"  onkeypress="return setValueType(this,'numeric')" value=0></td>
				<td align="center"><select id="dataType2"><option
							value="">--Select--</option>
						<option value="DATE">DATE</option>
						<option value="TIME">TIME</option>
						<option value="NUMBER">NUMBER</option></select></td>
				<td align="center" style="width: 80px"><input type="text" style="width: 80px" id="datpattern2"  value="0" onkeypress="return setValueType(this,'pattern')"></td>
				<td align="center"><select id="matchCondn2"><option
							value="">--Select--</option>
						<option value="Y">YES</option>
						<option value="N">NO</option></select></td>

			</tr>
			<tr style="size: 100%; display: none;" id="ThirdFile">

				<!-- <td><input type="text" style="width: 50px;" readonly="readonly" id="reconcount3" value="1"></td> -->
				<td align="center" style="width: 150px;"><label style="width: 150px;">File3 Headers</label></td>
				<td align="center"><select style="width:100px" id="fileselect3"><option value="">--Select--</option></select></td>
				<td align="center"  style="width: 80px"><input  style="width: 40px" type="text" id="value3" value=""	maxlength="6" cssClass="char_pos" onkeypress="return setValueType(this,'search')"> </td>
				<td align="center"><select id="condn3"><option value="">--Select--</option>
						<option value="=">=</option>
				 	<option value="!=">!=</option>
				 	<option value="LIKE">LIKE</option></select></td>
				<td align="center"><select id="staPdding3"><option value="">--Select--</option>
						<option value="Y">YES</option>
						<option value="N">NO</option></select></td>
				<td align="center" style="width: 50px"><input style="width: 50px" type="text" id="startPos3" value=0  onkeypress="return setValueType(this,'numeric')"></td>
				<td align="center" style="width: 50px"><input style="width: 50px" type="text" id="charsize3" value=0  onkeypress="return setValueType(this,'numeric')"></td>
				<td align="center"><select id="dataType3"><option
							value="">--Select--</option>
						<option value="DATE">DATE</option>
						<option value="TIME">TIME</option>
						<option value="NUMBER">NUMBER</option></select></td>
				<td align="center" style="width: 80px"><input type="text"  style="width: 80px" id="datpattern3"  value="0" onkeypress="return setValueType(this,'pattern')"></td>
				<td align="center"><select id="matchCondn3"><option
							value="">--Select--</option>
						<option value="Y">YES</option>
						<option value="N">NO</option></select></td>

			</tr>


		</table>
		<input type="hidden" id="datacount" value="0" > <input type="button" class="form-button" value="Add" class="" id="reconadd" onclick="addRow();" >


		<table id="detailtbl" style="display: none;">
		
				<tr>
					<th>File1 Header</th>
					<th>File2 Header</th>
					<td id="ThirdFile" style="display: none;">File3 Header</td>
				</tr>
			<c:forEach var="cnv" items="${CompareSetupBean.columnDtls}" varStatus="loop">
			
		
		<%-- <form:hidden   path="file1" value="${CompareSetupBean.columnDtls}" />	 --%>
			
			
		
	<%-- 	<form:hidden    path="file1" value="1" />
		<form:hidden   path="file1" value="2" />
		<form:hidden     path="file1" value="3"/> --%>
		
		<tr>
				<td><form:hidden   id="columnDtls[${loop.index}].matchCount"  path="columnDtls[${loop.index}].matchCount"  value="0" />
				<form:input   id="columnDtls[${loop.index}].fileColumn1"  path="columnDtls[${loop.index}].fileColumn1" value="0" readonly="true" />
				<form:hidden  id="columnDtls[${loop.index}].stPadding"   path="columnDtls[${loop.index}].stPadding" value="0"/>
				<form:hidden  id="columnDtls[${loop.index}].inStart_Char_Position"   path="columnDtls[${loop.index}].inStart_Char_Position" value="0"/>
				<form:hidden  id="columnDtls[${loop.index}].inEnd_char_position"   path="columnDtls[${loop.index}].inEnd_char_position" value="0"/>
				<form:hidden  id="columnDtls[${loop.index}].dataType"   path="columnDtls[${loop.index}].dataType" value="0"/>
				<form:hidden  id="columnDtls[${loop.index}].datpattern"  path="columnDtls[${loop.index}].datpattern" value="0"/>
				<form:hidden  id="columnDtls[${loop.index}].matchCondn1"  path="columnDtls[${loop.index}].matchCondn1" value=""/>
				<form:hidden  id="columnDtls[${loop.index}].strValue1"  path="columnDtls[${loop.index}].strValue1" value="" />
				<form:hidden  id="columnDtls[${loop.index}].condn1"  path="columnDtls[${loop.index}].condn1" value="" />
				
				</td>
				
			
				<td><form:input   id="columnDtls[${loop.index}].fileColumn2"  path="columnDtls[${loop.index}].fileColumn2" value="0" readonly="true" />
				<form:hidden  id="columnDtls[${loop.index}].stPadding2"  path="columnDtls[${loop.index}].stPadding2" value=""/>
				<form:hidden  id="columnDtls[${loop.index}].inStart_Char_Position2" path="columnDtls[${loop.index}].inStart_Char_Position2" value="0"/>
				<form:hidden  id="columnDtls[${loop.index}].inEnd_char_position2" path="columnDtls[${loop.index}].inEnd_char_position2" value="0"/>
				<form:hidden  id="columnDtls[${loop.index}].dataType2" path="columnDtls[${loop.index}].dataType2" value="0"/>
				<form:hidden  id="columnDtls[${loop.index}].datpattern2" path="columnDtls[${loop.index}].datpattern2" value="0"/>
				<form:hidden  id="columnDtls[${loop.index}].matchCondn2"  path="columnDtls[${loop.index}].matchCondn2" value="" />
				<form:hidden  id="columnDtls[${loop.index}].strValue2"  path="columnDtls[${loop.index}].strValue2" value="" />
				<form:hidden  id="columnDtls[${loop.index}].condn2"  path="columnDtls[${loop.index}].condn2" value="" /><input type="hidden" id="rowid" value=""></td>
				
				<td><form:input   id="columnDtls[${loop.index}].fileColumn3"  path="columnDtls[${loop.index}].fileColumn3" value="0" readonly="true" style="display: none;" />
				<form:hidden   id="columnDtls[${loop.index}].stPadding3" path="columnDtls[${loop.index}].stPadding3" value="0" />
				<form:hidden   id="columnDtls[${loop.index}].inStart_Char_Position3" path="columnDtls[${loop.index}].inStart_Char_Position3" value="0" />
				<form:hidden   id="columnDtls[${loop.index}].inEnd_char_position3" path="columnDtls[${loop.index}].inEnd_char_position3" value="0" />
				<form:hidden   id="columnDtls[${loop.index}].dataType3" path="columnDtls[${loop.index}].dataType3" value="0" />
				<form:hidden   id="columnDtls[${loop.index}].datpattern3" path="columnDtls[${loop.index}].datpattern3" value="0" />
				<form:hidden  id="columnDtls[${loop.index}].matchCondn3"  path="columnDtls[${loop.index}].matchCondn3" value="" />
				<form:hidden  id="columnDtls[${loop.index}].strValue3"  path="columnDtls[${loop.index}].strValue3" value="" />
				<form:hidden  id="columnDtls[${loop.index}].condn3"  path="columnDtls[${loop.index}].condn3" value="" /></td>
			</tr>
					
			</c:forEach>
			
		
		</table>
		
	<input type="submit" align="left" class="form-button" value="Save" id="reconsave" onclick="return saveData();"  >


			
			
			
			
			
			</div>
			



		


	
	

</body>
</html>