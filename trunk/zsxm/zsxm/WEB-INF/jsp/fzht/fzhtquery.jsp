<%@ page pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>

<html>
	<head>
		<%@ include file="/common/meta.jsp"%>
		<link rel="stylesheet" type="text/css" href="<s:url value="/grid/gt_grid.css"/>" />
		<link rel="stylesheet" type="text/css" href="<s:url value="/grid/skin/default/skinstyle.css"/>" />
		<link rel="stylesheet" type="text/css" href="<s:url value="/grid/skin/china/skinstyle.css"/>" />
		<link rel="stylesheet" type="text/css" href="<s:url value="/grid/skin/vista/skinstyle.css"/>" />
		<link rel="stylesheet" type="text/css" href="<s:url value="/grid/skin/mac/skinstyle.css"/>" />
		<SCRIPT LANGUAGE="Javascript" SRC="js/dhtmlxTree/dhtmlxcommon.js"></SCRIPT>
       <SCRIPT LANGUAGE="Javascript" SRC="js/dhtmlxTree/dhtmlxtree.js"></SCRIPT>
       <link type="text/css" href="js/dhtmlxTree/dhtmlxtree.css"rel="stylesheet" >
        <SCRIPT LANGUAGE="Javascript" SRC="js/dhtmlxTree/dhtmlxtree_json.js"></SCRIPT>
        <script   type="text/javascript" src="js/dhtmlxTree/dhtmlXTreeExtend.js" ></script>			
		<script type="text/javascript" src="<s:url value="/grid/gt_msg_cn.js"/>"></script>
		<script type="text/javascript" src="<s:url value="/grid/gt_grid_all.js"/>"></script>
		<style>
.selectBut2 {
				WIDTH: 129px; BACKGROUND: url(images/skin0/rcda/btn8.jpg) no-repeat;TEXT-ALIGN: left; BORDER-RIGHT-WIDTH: 0px; PADDING-LEFT: 10px; BORDER-TOP-WIDTH: 0px; BORDER-BOTTOM-WIDTH: 0px; HEIGHT: 22px; BORDER-LEFT-WIDTH: 0px; CURSOR: pointer
			}
.fsg_nr{ position:absolute; display:none;top:25px; right:-3px; background:#fff; border:1px solid #98B1C8; }			
</style>	
	</head>

	<body style="margin: 0px; overflow: hidden; margin: 2px;">
		<s:form name="spForm" action="" method="post">
     		
				<table width="100%" cellpadding="0" cellspacing="0" id="conTable" >
							<tr>
								  <td class="lefttop"></td>
								  <td width="%"  class="centertop"></td>
								  <td class="righttop"></td>
						    </tr>
							<tr>
									<td height="26" class="leftcenter">&nbsp;</td>
								    <td class="center" >
								    	字段
								    	<select name="where" style="width:100px" onchange="changefield(this.value)">
								    		<option value="htbh">合同编号</option>
								    		<option value="zfdz">租房地址</option>
								    		<option value="fzmj">租房面积</option>
								    		<option value="dwid_mc">企业名称</option>
								    	</select>
								    	<span id="xmdis">
								    		<s:textfield name="name" cssStyle="width:100px"></s:textfield>
								    	</span>
								    	
								    	<input type="button" class="but" value="查   询" id="queryBtn" onclick="">
								    	
								    	<input type="button" class="but" value="高级查询" id="b_sendmessage" onclick="dofilter();">
								    </td>
							
							   	 <td class="rightcenter"></td>
							</tr>
									
						   <tr>
							    <td class="leftbot"></td>
							    <td class="centerbot"></td>
							    <td class="rightbot"></td>
						   </tr>
					</table>
				<div id="mygrid_container" style="width:100%;" ></div>
		
		</s:form>
	</body>
	<script>
	
	var APP_PATH = '<c:url value="/"/>';
	var grid_demo_id = "myGrid1";
	var dsOption = {
		fields :  [ <s:iterator id="header" value="gheades">
						{name : '<s:property id="header" value="gid"/>'},
					</s:iterator>
					{}
				]
	};

	var colsOption = [ 
	<s:iterator id="col" value="gcols">
			{
				id : '<s:property id="col" value="gid"/>',
				header : '<s:property id="col" value="gheader"/>',
				width : '<s:property id="col" value="gwidth"/>',
				headAlign:"center",
				align:'<s:property id="col" value="galign"/>',
				isCheckColumn : <s:property id="col" value="gcheckcolumn"/>,
				filterable	: <s:property id="col" value="gfilterable"/>
				<s:if test="gscript==1">
					,renderer:function (v,r){return '<a href="javascript:;" title="点击查看详细信息" onclick="querymx('+r.HTID+')">'+v+'</a>';}
				</s:if>
			},
		</s:iterator>
		{id:'',header:'',filterable:false}
	 ];
	colsOption.splice(colsOption.length-1,1);

	var gridOption = {
		id : grid_demo_id,
		skin : "vista",
		width : "100%",
		height : (getSize().h - 70) + "px",
		pageSize : 25,
		container : 'mygrid_container',
		showGridMenu : true,
		allowCustomSkin : true,
		selectRowByCheck : true, 
		allowFreeze : true,
		allowHide : true,
		allowGroup : true,
		stripeRows : true,
		toolbarContent : 'nav | goto | pagesize | reload | export xls | print | filter | state | info',
		pageSizeList : [ 25, 50, 100, 200, 500 ],
		lightOverRow : true,
		showIndexColumn : true,
		dataset : dsOption,
		columns : colsOption,
		remoteSort : true,
		remoteFilter : true,
		loadURL : APP_PATH + '/fzhtwh!getList.do',
		exportURL : APP_PATH + '/fzhtwh!exportList.do',
		exportFileName : "合同信息",
		autoLoad : true
	};

	var mygrid = new Sigma.Grid(gridOption);

	Event.observe(window, "load", function() {
		mygrid.render();
	}, true);

	Event.observe("queryBtn", "click", function() {
		mygrid.query({"name":$F("name"),"where":$F("where")});
	}, true);		
	
	function dofilter(){
		mygrid.showDialog("filter");
	}
	
	function querymx(obj){
		 var h = document.body.clientHeight-50;
		 var w = '';
		 if(getSize().w > 940){
			w = 940;
		 }else{
			w = getSize().w;
		 }
		
		 openWin("fzhtview!preFzhtU.do?htid="+obj+"&opttype=3",w,h);
	}
		
	function reprow(){
		var len = mygrid.getSelectedRecords().length;
		if(len > 1){
			alert("修改只能选择一条记录!");
			return false;
		}
		if(len < 1){
			alert('请选择需要修改的记录!');
			return false;
		}
		if(len==1){
			 var h = document.body.clientHeight-50;
			 var w = '';
			 if(getSize().w > 965){
				w = 965;
			 }else{
				w = getSize().w;
			 }
			
			 openWin("fzht!preFzhtU.do?htid="+mygrid.getSelectedRecord().HTID+"&opttype=3",w,h);
		}
	}
	
	function refresh(){
		mygrid.query({"name":$F("name"),"where":$F("where")});
	}
	
	function deldata(){
		var len = mygrid.getSelectedRecords().length;
		if(len < 1){
			alert("请选择需要删除的数据！");
			return false;
		}
		var r = new Array();
		for(var i=0;i<len;i++){
			r.push(mygrid.getSelectedRecords()[i].HTID);
		}
		if(window.confirm("请确定需要删除《"+len+"》条记录!")){
			var ajax = new AppAjax("fzhtwh!doDeletefzht.do?htid="+r,save_Back).submit();
		}
	}
	

	function save_Back(data,type){
		if(data.message.code >0){
			alert(data.message.text);
			refresh();
		}else{
			alert(data.message.text);
		}
	}
	
	function doExport(){
		window.location.href = "fzhtwh!doExportExcel.do";
		//new AppAjax("zsdwwh!doExportExcel.do",function(data){}).submit();
	}

	</script>
</html>
