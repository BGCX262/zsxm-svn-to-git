<%@ page pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
	<head>
		<%@ include file="/common/meta.jsp"%>
		<link type="text/css" href="<s:url value="/styles/cv_rc.css"/>" rel="stylesheet" />
		<script type="text/javascript" src="<c:url value="/js/czrc.js"/>" ></script>	
	<style>
		.fsg_nr{ position:absolute; display:none;top:25px; right:-3px; background:#fff; border:1px solid #98B1C8; }
	</style>
	</head>

	<body>
	<s:form name="czrcForm" action='zsdw!preLxr.do' method="post" >
	<s:hidden name="dwid"></s:hidden>
	<s:hidden name="pname"></s:hidden>
	<s:hidden name="opttype"></s:hidden>
 <!--个人信息 start-->
	<div class="title"  onclick=""><h2>联系人 &nbsp&nbsp&nbsp&nbsp&nbsp

	</h2>
	<div class="img_right" id="s1" ></div>
	</div>
	<div class="butbar" id="butbar">
		<div class="left">	
		</div>
	</div>	
	<div  class="tableContainer" id="tableContainer" style="width:100%">
		<table id="tac" style="width:1200px">
			<thead>
				<tr>
					<td width="20px"><input type="checkbox" id="allcheckbox" onclick="RC.checkboxAll('dmkey',this.checked)"></td>
					<td width="50px">序号</td>
					<td>姓名</td>
					<td>性别</td>
					<td>职称</td>
					<td>职务</td>
					<td>电话</td>
					<td>手机</td>
					<td>邮箱</td>
					<td>QQ</td>
					<td>所属部门</td>
					<td width="150px">备注</td>
					<td width="90">第一联系人</td>
				</tr>
			</thead>
			<s:iterator value="qlist">
				<tr>
					<td><input type="checkbox" id="dmkey" name="dmkey" value='<s:property value="xh"/>'></td>
						<td><s:property value="xh"/></td>
						<td><s:property value="lxrxm"/></td>
						<td>
							<s:property value="sex_mc"/>
						</td>
						<td>
							<s:property value="zc_mc"/>
						</td>
						<td>
							<s:property value="zw"/>
						</td>
						<td>
							<s:property value="tel"/>
						</td>
						<td>
							<s:property value="phone"/>
						</td>
						<td>
							<s:property value="email"/>
						</td>
						<td>
							<s:property value="qq"/>
						</td>
						<td>
							<s:property value="ssbm"/>
						</td>
						<td width="150px">
							<s:property value="sm"/>
						</td>
						<td align="center">
							<s:if test="dylxrbz==1">
								是
							</s:if>
							<s:else>
								&nbsp
							</s:else>
						</td>
				</tr>				
			</s:iterator>
		</table>		
	</div>
	<div class="footer">
		<input class="btn_submit1" type="button" value="下 一 步" onclick="javascript:parent.activeChange('rcdali3');"/>
		<s:if test="opttype==3">
				<input class="btn_submit1" type="button" value="退    出" onclick="javascript:doOut(4);"/>
			</s:if>
			<s:else>
				<input class="btn_submit1" type="button" value="填写完成" onclick="javascript:parent.setDwid('',1,true);"/>
		</s:else>
		
	</div>	
	</s:form>
	</body>

	<script type="text/javascript">
		$('tableContainer').style.height = (getSize().h - 120)+"px"; 
		
		function closeWindows(){closeWin(self.name);}
		function closeWin(sid){
			parent.getOpener().refresh();
			parent.closeWindows();
		}
		
		function doOut(type){
			closeWindows();
		}
		function addxx(){
			parent.openWin("zsdw!preLxrI.do?dwid="+$('dwid').value+"&opttype=1&pname="+$('pname').value,"500","280");
		}
		
		function repxx(){
			var rcsel = RC.checkbox('dmkey');
			if(rcsel.length == 0){
				alert("请选择需要修改的信息!");
				return false;
			}
			if(rcsel.length > 1){
				alert("修改只能选择一条记录!");
				return false;
			}
			parent.openWin("zsdw!preLxrU.do?dwid="+$('dwid').value+"&xh="+rcsel[0].v+"&opttype=3&pname="+$('pname').value,"500","280");
		}
		
		function delxx(){
			var rcsel = RC.checkbox('dmkey');
			if(rcsel.length == 0){
				alert("请选择需要删除的信息!");
				return false;
			}
			if(window.confirm("您确定要删除 "+rcsel.length+" 条信息?")){
				var ajax = new AppAjax("zsdw!doLxrD.do",sava_back).submitForm("czrcForm");
			}
		}
		
		function sava_back(data){
			if(data.message.code >0){
				alert("删除成功!");
				refresh();
			}else{
				alert(data.message.text);
			}
		}
		
		function setxx(){
			var rcsel = RC.checkbox('dmkey');
			if(rcsel.length == 0){
				alert("请选择需要设置的信息!");
				return false;
			}
			if(rcsel.length > 1){
				alert("设置第一联人只能选择一条记录!");
				return false;
			}
			var ajax = new AppAjax("zsdw!doSetLxrForDy.do?dwid="+$('dwid').value+"&xh="+rcsel[0].v,set_back).submit();
		}
		
		function set_back(data){
			if(data.message.code >0){
				alert("设置成功!");
				refresh();
			}else{
				alert(data.message.text);
			}
		}
		
		function refresh(){
			document.all.czrcForm.submit();
		}
	</script>
</html>

