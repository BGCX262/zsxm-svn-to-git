<%@ page pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
	<head>
       <%@ include file="/common/meta.jsp"%>
       <script language="JavaScript"src="<s:url value="/js/My97DatePicker/WdatePicker.js"/>"></script>		
		<style type="text/css">
			
			  .title {	background:#B8CDE3;	color:#003770;line-height:26px;height:26px;padding-left:15px;border:1px solid #fff;font-weight:bold;text-align:left;}
			  .fxtable{border: 1px solid #C1DAD7;width: 100%;margin-top:1px;}
			  .fxtable2{border: 1px solid #C1DAD7;width: 100%;margin-top:1px;}
			  .fxtable2 td.cls {border-right: 1px solid #C1DAD7;border-bottom: 1px solid #C1DAD7;background: #fff;font-size:12px;padding: 6px 6px 6px 12px;color: #4f6b72;height:20px;}
		      .fxtable td {border-right: 1px solid #C1DAD7;border-bottom: 1px solid #C1DAD7;background: #fff;font-size:12px;padding: 6px 6px 6px 12px;color: #4f6b72;}
		        a.button{background:url(images/button.gif);	display:block;color:#555555;font-weight:bold;height:30px;line-height:29px;margin-bottom:14px;text-decoration:none;width:191px;}
				a:hover.button{color:#0066CC;}
		      .lens{ no-repeat 10px 8px;text-indent:30px;display:block;}
		      .btn {
		         height:22px;
		      }
		    .editor {
			    margin-top: 5px;
			    margin-bottom: 5px;
		    }
		</style>
	</head>
	<s:form name="czrcForm" action="zsxmby!addBywh.do" method="post">
	<body >
		<s:component template="xtitle" theme="app" value='金凤凰填报维护'></s:component>
		<s:hidden name="dwid"></s:hidden>
		<s:hidden name="ryid"></s:hidden>
	    <div id="xwincontent">
	    	<table cellspacing="0" class="fxtable"  style="margin-bottom:10px;">
	        	<tr>
	        		<td style="text-align:center;background:#E7F2FC;font:bold 12px 'lucida Grande',Verdana;width:25%;">调离原因</td>
	        		<td>
	        		<s:select name="zsdw.djyy" id="zsdw.djyy" list="xtdict29" cssStyle="width:130" listKey="dictbh" listValue="dictname" />
	        		</td>
	        	</tr>
				<tr>
	        		<td style="text-align:center;background:#E7F2FC;font:bold 12px 'lucida Grande',Verdana;width:25%;">说明</td>
	        		<td><s:textarea name="zsdw.djsm" cols="20" rows="3"></s:textarea></td>
	        	</tr>	
	        	 	
	        </table>
	   </div>
	   
	   
	   <div class="footer">			
	       <input type="button" class="button" value="保  存" onclick="doSave();"/>
           <input type="button" name="resetBtn" class="button" value="关  闭" onclick="closeWin(self.name);"/>
       </div>
	</body>
	</s:form>
	<script type="text/javascript">
		function doSave(){
			var ajax = new AppAjax("rysh!doRydj.do",function(data){save_Back(data)}).submitForm("czrcForm");
		}
		
	function save_Back(data){
		if(data.message.code >0){
			alert("冻结成功！");
			getOpener().refresh();
			closeWin(self.name);	
		}else{
			alert(data.message.text);
		}
	}
	</script>
</html>