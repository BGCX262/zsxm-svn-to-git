package com.group.zsxm.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fins.gt.model.FilterInfo;
import com.fins.gt.model.SortInfo;
import com.group.core.common.LimitPage;
import com.group.core.common.TreeBean;
import com.group.zsxm.entity.XtDict;
import com.group.zsxm.entity.XtDlb;
import com.group.zsxm.exception.BusException;
import com.group.zsxm.service.common.BaseService;

@Service
public class DwshviewService  extends BaseService{
	
	
	public List<Map<String, String>> getZsxmWaitSelect(Map<String,String> query){
		String sql_q = " ";
		List<Map<String, String>> list = new ArrayList();
		sql_q = " select a.* from xm_info_v a  where a.xmid not in ( select xmid from dw_xm where  isNull(status,1)=1 )";
		
		if(query != null ){
			if(query.get("field")!=null && !query.get("field").equals("")){
				sql_q += " and "+query.get("field")+" like '%"+query.get("value")+"%'";
			}
		}
		sql_q += "  order by a.xmid ";
		list = this.getJdbcTemplate().queryForList(sql_q, new Object[]{});
		return list;
	}
	
	public Map<Integer, List<XtDict>> getDictListWithSelectAll(){
		Map<Integer, List<XtDict>> ms = new HashMap();
		List<XtDict> xtdicts = new ArrayList();
		int lbid = 0;
		String sql_q = " select * from xt_dlb where lbframe!=1 and lbid>=4 and lbid<=15 ";
		List<XtDlb> xtdlbs = this.getSimpleJdbcTemplate().query(sql_q, resultBeanMapper(XtDlb.class));
		for(int i=0;i<xtdlbs.size();i++){
			lbid = xtdlbs.get(i).getLbid();
			ms.put(lbid, this.getSimpleJdbcTemplate().query(" select dictbh,dictname from xt_dict where lbid="+lbid+" order by dictbh", resultBeanMapper(XtDict.class)));
			
		}
		return ms;
	}
	public Map<Integer, List<XtDict>> getDictListWithSelectAll(int slbid,int elbid){
		Map<Integer, List<XtDict>> ms = new HashMap();
		List<XtDict> xtdicts = new ArrayList();
		int lbid = 0;
		String sql_q = " select * from xt_dlb where lbframe!=1 and lbid>="+slbid+" and lbid<= "+elbid;
		List<XtDlb> xtdlbs = this.getSimpleJdbcTemplate().query(sql_q, resultBeanMapper(XtDlb.class));
		for(int i=0;i<xtdlbs.size();i++){
			lbid = xtdlbs.get(i).getLbid();
			ms.put(lbid, this.getSimpleJdbcTemplate().query(" select dictbh,dictname from xt_dict where lbid="+lbid+" order by dictbh", resultBeanMapper(XtDict.class)));
			
		}
		return ms;
	}
	
	public Integer getZsdwIsLjx(String dwid){
		String sql = " select count(*) from DW_RYXX_TR where dwid="+dwid;
		return this.getSimpleJdbcTemplate().queryForInt(sql);
	}
	
	public Integer getZsdwIsTzbl(String dwid){
		String sql  = " select count(*) from dw_gqbl where dwid="+dwid+" and isnull(tzlx,'')<>'' ";
		return this.getSimpleJdbcTemplate().queryForInt(sql);
	}
	
	public String getZsdwMaxDabh(int ws){
		String sql = "",dabh = "";
		sql = " select max(convert(int,isnull(dabh,0)))+1 from dw_info where isnull(status,1)=1 ";
		Integer dabh_ = this.getSimpleJdbcTemplate().queryForInt(sql);
		dabh = String.valueOf(dabh_);
		for(int i = String.valueOf(dabh_).length();i<ws;i++){
			dabh = "0"+dabh;
		}
		return dabh;
	}
	public String getZsdwMaxDabh(){
		return this.getZsdwMaxDabh(5);
	}
	
	/**
	 * 根据LBID 获取
	 * @param id
	 * @return
	 */
	public Map<Integer, List<XtDict>> getDictListByLbid(String id){
		Map<Integer, List<XtDict>> ms = new HashMap();
		List<XtDict> xtdicts = new ArrayList();
		int lbid = 0;
		String sql_q = " select * from xt_dlb where lbframe!=1 and lbid="+id;
		List<XtDlb> xtdlbs = this.getSimpleJdbcTemplate().query(sql_q, resultBeanMapper(XtDlb.class));
		for(int i=0;i<xtdlbs.size();i++){
			lbid = xtdlbs.get(i).getLbid();
			ms.put(lbid, this.getSimpleJdbcTemplate().query(" select dictbh,dictname from xt_dict where lbid="+lbid+" order by dictbh", resultBeanMapper(XtDict.class)));
			
		}
		return ms;
	}
	
	public List getZsdwMutSelListByDwid(String dwid,String selid){
		List list = new ArrayList();
		String sql = "";
		
		sql = " select count(*) from dw_mutsel_sb a where a.dwid="+dwid+" and a.selid="+selid;
		if(this.getSimpleJdbcTemplate().queryForInt(sql) > 0){
			if(selid.equals("4")){
				sql = " select a.dwid,a.selid,a.seldm,(select b.dictname from xt_dict b where b.lbid=13 " +
						" and a.seldm=b.dictbh ) selmc from dw_mutsel_sb a where a.dwid="+dwid+" and a.selid="+selid;
			}else{
				sql = " select * from dw_mutsel_sb where dwid="+dwid+" and selid="+selid;
			}
		}else{
			if(selid.equals("4")){
				sql = " select a.dwid,a.selid,a.seldm,(select b.dictname from xt_dict b where b.lbid=13 " +
						" and a.seldm=b.dictbh ) selmc from dw_mutsel a where a.dwid="+dwid+" and a.selid="+selid;
			}else{
				sql = " select * from dw_mutsel where dwid="+dwid+" and selid="+selid;
			}
		}
		
		list = this.getSimpleJdbcTemplate().queryForList(sql);
		return list;
	}
	
	public List getZsdwMutSelWithViewByDwid(String dwid,String selid,String lbid){
		List list = new ArrayList();
		String sql = " select a.*," +
				"(select b.dictname from xt_dict b where b.lbid="+lbid+" and b.dictbh=a.seldm) as seldm_mc " +
				" from dw_mutsel a where a.dwid="+dwid+" and a.selid="+selid;
		list = this.getSimpleJdbcTemplate().queryForList(sql);
		return list;
	}
	
	public Map getDwxmByDwid(String dwid){
		Map m = new HashMap();
		String sql = "";
		sql = " select count(*) from dw_xm_sb where dwid="+dwid;
		if(this.getSimpleJdbcTemplate().queryForInt(sql) > 0){
			sql = " select count(*) from dw_xm_sb where dwid="+dwid+ " and isnull(status,1)=1";
			if(this.getSimpleJdbcTemplate().queryForInt( sql ) > 0){
				sql = " select a.xmid,b.xmmc from dw_xm_sb a,xm_info b where a.xmid=b.xmid and a.dwid="+dwid;
				m = this.getSimpleJdbcTemplate().queryForMap(sql);	
			}
		}else{
			sql = " select count(*) from dw_xm where dwid="+dwid+ " and isnull(status,1)=1";
			if(this.getSimpleJdbcTemplate().queryForInt( sql ) > 0){
				sql = " select a.xmid,b.xmmc from dw_xm a,xm_info b where a.xmid=b.xmid and a.dwid="+dwid;
				m = this.getSimpleJdbcTemplate().queryForMap(sql);	
			}
		}
		
		return m;
	}
	
	
	/**
	 * 上报数据
	 * @param dwid
	 * @return
	 */
	public void doSbDwinfo(String dwid){
		String sql = " select count(*) from dw_info_sb where  dwid='"+dwid+"' ";
		if(this.getSimpleJdbcTemplate().queryForInt(sql) == 0){
			throw new BusException("当前信息未变更，无需上报");
		}
		sql = "update dw_info_sb set sbbz=1 where  dwid='"+dwid+"' ";
		this.getSimpleJdbcTemplate().update(sql);
	}
	
	public void doSbDwRyinfo(String dwid,String ryid){
		String sql = " select count(*) from dw_ryxx_sb where dwid='"+dwid+"' and ryid in ("+ryid+") and isnull(sbbz,0)=1";
		if(this.getSimpleJdbcTemplate().queryForInt(sql) > 0){
			throw new BusException("当前有相关信息已经上报，无需再上报");
		}
		sql = " select count(*) from dw_ryxx_sb where dwid='"+dwid+"' and ryid in ("+ryid+") and isnull(sbbz,0)=2";
		if(this.getSimpleJdbcTemplate().queryForInt(sql) > 0){
			throw new BusException("当前有相关信息已经审核通过，无需再上报");
		}
		sql = "update dw_ryxx_sb set sbbz=1 where dwid='"+dwid+"' and ryid in ("+ryid+")";
		this.getSimpleJdbcTemplate().update(sql);
	}
	
	public Integer getDwInfoSbbzByDwid(String dwid){
		String sql = " select isnull(sbbz,0) from dw_info_sb where dwid='"+dwid+"'  and isNull(status,1)=1 ";
		return this.getSimpleJdbcTemplate().queryForInt(sql);
	}
	
	public Map getZsdwinfoBydwid(String dwid){
		Map m = new HashMap();
		if(dwid != null && !dwid.equals("")){
			String sql = "";
			sql = " select count(*) from dw_info_sb where  dwid='"+dwid+"' ";
			if(this.getSimpleJdbcTemplate().queryForInt(sql) == 0){
				sql = " insert into dw_info_sb(DWID, DWDM, DWZT, DWMC, LOGINNAME, PASSWORD, FRDB," +
						" CLRQ, NWZ, ZCZB, DWJJ, BGDD1, BGDD2, BGDD3, BGDD4, JZMJ, LXRXM, SEX, ZC," +
						" ZW, TEL, PHONE, SSBM, DWLX, DWSX, STATUS, SM, HGTDPC, SWGLM, NSRSBH, QYZCBZ," +
						" SYZCBZ, FFRJGBZ, DWPASSWORD, DAH, SBBZ, ISJFH, TDPC, DWGZR, SNXSSR, SNJNSS," +
						" SNDYGS, DABH, RZYQSJ, DWZYCP, GSJSQY," +
						" GXJSQY, LXSQY, RJQY, DMQY, GJCXKJYQ, GJQDXXCY, XDFWY, FWWB, LHRH, LXQY, CMMI," +
						" DWSB, ZYCP, SSCTD, SSCPC, FW) " +
						" select DWID, DWDM, DWZT, DWMC, LOGINNAME, PASSWORD, FRDB, CLRQ, NWZ, ZCZB, DWJJ, " +
						"BGDD1, BGDD2, BGDD3, BGDD4, JZMJ, LXRXM, SEX, ZC, ZW, TEL, PHONE, SSBM, DWLX, DWSX," +
						" STATUS, SM, HGTDPC, SWGLM, NSRSBH, QYZCBZ, SYZCBZ, FFRJGBZ, DWPASSWORD, DAH, SBBZ," +
						" isnull(ISJFH,0), TDPC, DWGZR, SNXSSR, SNJNSS, SNDYGS, DABH, RZYQSJ, DWZYCP, GSJSQY, GXJSQY, LXSQY," +
						" RJQY, DMQY, GJCXKJYQ, GJQDXXCY, XDFWY, FWWB, LHRH, LXQY, CMMI, DWSB, ZYCP, SSCTD, SSCPC," +
						" FW from dw_info where dwid="+dwid+" and isNull(status,1)=1 ";
				this.getSimpleJdbcTemplate().update(sql);
				
				sql = " insert into dw_mutsel_sb select * from dw_mutsel where  dwid='"+dwid+"' and isNull(status,1)=1";
				this.getSimpleJdbcTemplate().update(sql);
				
				sql = "insert into dw_xm_sb select * from dw_xm where dwid='"+dwid+"' and isNull(status,1)=1";
				this.getSimpleJdbcTemplate().update(sql);
				
				sql = " select count(*) from dw_ryxx_sb where dwid='"+dwid+"'";
				if(this.getSimpleJdbcTemplate().queryForInt(sql) == 0){
					sql = "insert into dw_ryxx_sb select * from dw_ryxx where dwid='"+dwid+"'  and isNull(status,1)=1 ";
					this.getSimpleJdbcTemplate().update(sql);
				}
				
			}
			
			sql = " select dwid,DWDM,SBBZ,DAH, DWZT, DWMC, LOGINNAME, PASSWORD, FRDB, CONVERT(varchar(100), CLRQ, 23) AS CLRQ, NWZ, ZCZB, " +
				" DWJJ, BGDD1, BGDD2, BGDD3, BGDD4, JZMJ, LXRXM, SEX, ZC, ZW, TEL, PHONE,SSBM, DWLX,SM,SWGLM,NSRSBH," +
				"HGTDPC,dwpassword,ISJFH,TDPC,DWGZR,SNXSSR,SNJNSS,SNDYGS," +//以下单位属性新增字段2010-12-09
				" DABH, GXJSQY,RZYQSJ, DWZYCP, GSJSQY, LXSQY, RJQY, DMQY, GJCXKJYQ, " +
				"GJQDXXCY, XDFWY, FWWB, LHRH, LXQY, CMMI, DWSB, ZYCP, SSCTD, SSCPC," +
				" (SELECT     DICTNAME  FROM          dbo.XT_DICT AS b WHERE (LBID = 38) AND (dw_info_sb.GJQDXXCY = DICTBH)) AS GJQDXXCY_MC," +
				" (SELECT     DICTNAME  FROM          dbo.XT_DICT AS b WHERE (LBID = 40) AND (dw_info_sb.FWWB = DICTBH)) AS FWWB_MC  " +
				" from dw_info_sb where dwid='"+dwid+"'  and isNull(status,1)=1 ";
			 
			m = this.getSimpleJdbcTemplate().queryForMap(sql);
		}
		return m;
	}
	
	
	public String doSaveZsdw(String dwid,Map<String, String> zsdw,String[] dwsx,String[] cyfl,String[] jszy,String[] yjlb,String jszyothermc,Map<String, String> qzzxm){
		String sql = "";
		if(dwid != null && !dwid.equals("")){//
			
			/**
			sql = " delete from dw_info_sb where dwid='"+dwid+"' and isNull(status,1)=1";
			this.getSimpleJdbcTemplate().update(sql);
			sql = " delete from dw_mutsel_sb where dwid='"+dwid+"' and isNull(status,1)=1";
			this.getSimpleJdbcTemplate().update(sql);
			
			sql = " delete from dw_xm_sb where dwid='"+dwid+"' and isNull(status,1)=1";
			this.getSimpleJdbcTemplate().update(sql);
			**/
			
			sql = " update dw_info_sb set dwlx='"+zsdw.get("dwlx")+"',dwmc='"+zsdw.get("dwmc")+"',dwzt='"+(zsdw.get("dwzt"))+"'," +
					"frdb='"+zsdw.get("frdb")+"',clrq="+(zsdw.get("clrq").equals("")?"null":("'"+zsdw.get("clrq")+"'"))+"," +
					"nwz='"+zsdw.get("nwz")+"',zczb='"+(zsdw.get("zczb").trim().equals("")?"0":zsdw.get("zczb").trim())+"'," +
					"BGDD1='"+zsdw.get("bgdd1")+"', BGDD2='"+zsdw.get("bgdd2")+"', BGDD3='"+zsdw.get("bgdd3")+"', BGDD4='"+zsdw.get("bgdd4")+"'," +
					"dwjj='"+zsdw.get("dwjj")+"',SWGLM='"+zsdw.get("swglm")+"',NSRSBH='"+zsdw.get("nsrsbh")+"',sm='"+zsdw.get("sm")+"'," +
					"sscpc='"+zsdw.get("sscpc")+"',tdpc='"+zsdw.get("tdpc")+"',gxjsqy='"+zsdw.get("gxjsqy")+"'," +
					"lxsqy='"+zsdw.get("lxsqy")+"',rjqy='"+zsdw.get("rjqy")+"',dmqy='"+zsdw.get("dmqy")+"'," +
					"cmmi='"+zsdw.get("cmmi")+"',dwsb='"+zsdw.get("dwsb")+"',gjqdxxcy='"+zsdw.get("gjqdxxcy")+"'," +
					"gjcxkjyq='"+zsdw.get("gjcxkjyq")+"',xdfwy='"+zsdw.get("xdfwy")+"',fwwb='"+zsdw.get("fwwb")+"'," +
					"lhrh='"+zsdw.get("lhrh")+"' " +
					" where dwid='"+dwid+"' and isNull(status,1)=1";
			/**
			sql = " insert into dw_info_sb(dwid, dwdm,DWZT, DWMC,  FRDB, CLRQ, NWZ, ZCZB, " +
				" DWJJ, BGDD1, BGDD2, BGDD3, BGDD4, JZMJ, SM,SWGLM,NSRSBH,HGTDPC,DWLX,dwpassword,status,dah,sbbz,isjfh,tdpc) " +
				" values("+dwid+",'"+zsdw.get("dwdm")+"','"+(zsdw.get("dwzt"))+"','"+zsdw.get("dwmc")+"'," +
				"'"+zsdw.get("frdb")+"',"+(zsdw.get("clrq").equals("")?"null":("'"+zsdw.get("clrq")+"'"))+"," +
				"'"+zsdw.get("nwz")+"','"+(zsdw.get("zczb").trim().equals("")?"0":zsdw.get("zczb").trim())+"','"+zsdw.get("dwjj")+"','"+zsdw.get("bgdd1")+"','"+zsdw.get("bgdd2")+"'," +
				"'"+zsdw.get("bgdd3")+"','"+zsdw.get("bgdd4")+"','"+(zsdw.get("jzmj").trim().equals("")?"0":zsdw.get("jzmj").trim())+"','"+zsdw.get("sm")+"','"+zsdw.get("swglm")+"'," +
				"'"+zsdw.get("nsrsbh")+"','"+zsdw.get("hgtdpc")+"','"+zsdw.get("dwlx")+"'," +
				""+(zsdw.get("dwpassword").equals("")?"NULL":"'"+zsdw.get("dwpassword")+"'")+",1,'"+zsdw.get("dah")+"',0,'"+zsdw.get("isjfh")+"','"+zsdw.get("tdpc")+"')";
			**/
			this.getSimpleJdbcTemplate().update(sql);
			

			sql = "delete from DW_MUTSEL_SB where dwid="+dwid+" and isnull(status,1)=1 and selid=4 ";
			this.getSimpleJdbcTemplate().update(sql);
			if(yjlb != null && yjlb.length > 0){
				for(int i=0;i<yjlb.length;i++){
					sql = " insert into dw_mutsel_sb(dwid,selid,seldm,status) values('"+dwid+"','4','"+yjlb[i]+"',1)";
					this.getSimpleJdbcTemplate().update(sql);
				}
			}
			
			/**
			if(dwsx != null && dwsx.length > 0){
				for(int i=0;i<dwsx.length;i++){
					sql = " insert into dw_mutsel_sb(dwid,selid,seldm,status) values('"+dwid+"','1','"+dwsx[i]+"',1)";
					this.getSimpleJdbcTemplate().update(sql);
				}
			}
			
			if(cyfl != null && cyfl.length > 0){
				for(int i=0;i<cyfl.length;i++){
					sql = " insert into dw_mutsel_sb(dwid,selid,seldm,status) values('"+dwid+"','2','"+cyfl[i]+"',1)";
					this.getSimpleJdbcTemplate().update(sql);
				}
			}
			
			if(jszy != null && jszy.length > 0){
				for(int i=0;i<jszy.length;i++){
					if(jszy[i].equals("999")){
						sql = " insert into dw_mutsel_sb(dwid,selid,seldm,othermc,status) values('"+dwid+"','3','"+jszy[i]+"','"+jszyothermc+"',1)";
					}else{
						sql = " insert into dw_mutsel_sb(dwid,selid,seldm,status) values('"+dwid+"','3','"+jszy[i]+"',1)";
					}
					this.getSimpleJdbcTemplate().update(sql);
				}
			}
			
			if(yjlb != null && yjlb.length > 0){
				for(int i=0;i<yjlb.length;i++){
					sql = " insert into dw_mutsel_sb(dwid,selid,seldm,status) values('"+dwid+"','4','"+yjlb[i]+"',1)";
					this.getSimpleJdbcTemplate().update(sql);
				}
			}
			
			if(qzzxm != null && !qzzxm.get("xmid").equals("")){
				sql = " insert into dw_xm_sb(dwid,xmid,status) values('"+dwid+"','"+qzzxm.get("xmid")+"',1)";
				this.getSimpleJdbcTemplate().update(sql);
			}
			**/
		}
		return dwid;
	}
	
	/**
	 * 股权比例
	 * @param dwid
	 * @return
	 */
	public String getGqblZczb(String dwid){
		String sql = " select isnull(zczb,0) from dw_info_sb where dwid="+dwid;
		return String.valueOf(this.getJdbcTemplate().queryForObject(sql, String.class));
	}
	
	public String getGqblTzze(String dwid){
		String sql = " select isNull(sum(tzje),0) from dw_gqbl_sb where dwid="+dwid;
		return String.valueOf(this.getJdbcTemplate().queryForObject(sql, String.class));
	}
	public List getGqblList(String dwid,String tzze){
		String sql = " ";
		List<Map<String, String>> list = new ArrayList();
		if(dwid != null && !dwid.equals("")){
			if(tzze.trim().equals("")) {tzze = "0";}
			sql =" select count(*) from dw_gqbl_sb where dwid="+dwid;
			if(this.getSimpleJdbcTemplate().queryForInt(sql) == 0){
				sql = " insert into dw_gqbl_sb select * from dw_gqbl where dwid="+dwid;
				this.getSimpleJdbcTemplate().update(sql);
			}
			if(Double.parseDouble(tzze)!=0){
				sql = " select dwid,xh,xm,zjno,tzje,CAST(convert(numeric(10,2),(tzje/"+tzze+" * 100)) as varchar(20))+'%' tzbl,sm,status," +
						" (SELECT     DICTNAME FROM          dbo.XT_DICT AS b  WHERE      (LBID = 46) AND (dw_gqbl_sb.TZLX = DICTBH)) AS tzlx_mc from dw_gqbl_sb where dwid='"+dwid+"'  and isNull(status,1)=1";
			}else{
				sql = " select dwid,xh,xm,zjno,tzje,'0' tzbl,sm,status," +
						"(SELECT     DICTNAME FROM          dbo.XT_DICT AS b  WHERE      (LBID = 46) AND (dw_gqbl_sb.TZLX = DICTBH)) AS tzlx_mc from dw_gqbl_sb where dwid='"+dwid+"'  and isNull(status,1)=1";
			}
			list = this.getJdbcTemplate().queryForList(sql);
		}
		return list;
	}

	public int doGqblI(String dwid,Map<String, String > m){
		String sql = "";
		//获取 XH 最大值
		Integer maxxh = 1;
		sql = "select count(*) from dw_gqbl_sb where dwid='"+dwid+"'  and isNull(status,1)=1";
		if( this.getSimpleJdbcTemplate().queryForInt(sql) > 0){
			sql = " select max(xh) from dw_gqbl_sb where dwid='"+dwid+"'  and isNull(status,1)=1 ";
			maxxh = this.getSimpleJdbcTemplate().queryForInt(sql)+1;
		}
		sql = " insert into dw_gqbl_sb(dwid,xh,xm,zjno,tzje,tzbl,tzlx,sm,status) " +
			" values('"+dwid+"','"+maxxh+"','"+m.get("xm")+"','"+m.get("zjno")+"'," +
			"'"+(m.get("tzje").trim().equals("")?"0":m.get("tzje").trim())+"','"+m.get("tzbl")+"','"+transToS(m.get("tzlx"))+"','"+m.get("sm")+"',1)";
		this.getSimpleJdbcTemplate().update(sql);
		
		return 1;
	}
	
	public Map preGqblU(String dwid,String xh){
		Map map = new HashMap();
		String sql = " select * from dw_gqbl_sb where dwid='"+dwid+"' and xh="+xh+" and isNull(status,1)=1";
		map = this.getSimpleJdbcTemplate().queryForMap(sql);
		return map;
	}
	
	public int doGqblU(String dwid,String xh,Map<String, String > m){
		String sql = " update dw_gqbl_sb set xm='"+m.get("xm")+"',zjno='"+m.get("zjno")+"',tzje='"+(m.get("tzje").trim().equals("")?"0":m.get("tzje").trim())+"',sm='"+m.get("sm")+"'," +
				"tzbl='"+m.get("tzbl")+"',tzlx='"+transToS(m.get("tzlx"))+"' where dwid='"+dwid+"' and xh="+xh+"   and isNull(status,1)=1 ";
		this.getSimpleJdbcTemplate().update(sql);
		return 1;
	}
	
	public int doGqblD(String dwid,String[] xh){
		String sql = " delete from dw_gqbl_sb where dwid="+dwid+" and xh in("+ArrayToString(xh)+")  and isNull(status,1)=1 ";
		this.getSimpleJdbcTemplate().update(sql);
		return 1;
	}
	
	/**
	 * 承担项目
	 */
	public List getCdxmList(String dwid){
		String sql = " ";
		List<Map<String, String>> list = new ArrayList();
		if(dwid != null && !dwid.equals("")){
			sql = " select a.dwid,a.xh,a.xmmc,a.xmwh,a.xmbh,a.xmjb,a.jhlb,a.xmje,substring(CONVERT(varchar(100), a.lxrq, 23),0,5) AS lxrq,CONVERT(varchar(100), a.strdate, 23) AS strdate," +
					" CONVERT(varchar(100), a.enddate, 23) AS enddate,a.sm,a.status," +
					" (select b.dictname from xt_dict b where b.lbid=16 and b.dictbh=a.xmjb) as xmjb_mc," +
					" (select b.dictname from xt_dict b where b.lbid=20 and b.dictbh=a.jhlb) as jhlb_mc  " +
					" from dw_cdxm a where a.dwid='"+dwid+"'  and isNull(a.status,1)=1";
			list = this.getJdbcTemplate().queryForList(sql);
		}
		return list;
	}
	public String getXmjehj(String dwid){
		String sql = " select isNull(sum(xmje),0) from dw_cdxm where dwid="+dwid;
		return String.valueOf(this.getJdbcTemplate().queryForObject(sql, String.class));
	}
	public int doCdxmI(String dwid,Map<String, String > m){
		String sql = "";
		//获取 XH 最大值
		Integer maxxh = 1;
		sql = "select count(*) from dw_cdxm where dwid='"+dwid+"'  and isNull(status,1)=1";
		if( this.getSimpleJdbcTemplate().queryForInt(sql) > 0){
			sql = " select max(xh) from dw_cdxm where dwid='"+dwid+"'  and isNull(status,1)=1 ";
			maxxh = this.getSimpleJdbcTemplate().queryForInt(sql)+1;
		}
		sql = " insert into dw_cdxm(dwid,xh,xmmc,xmwh,xmbh,xmjb,jhlb,xmje,lxrq,strdate,enddate,sm,status) " +
			" values('"+dwid+"','"+maxxh+"','"+m.get("xmmc")+"','"+m.get("xmwh")+"','"+m.get("xmbh")+"'," +
			"'"+m.get("xmjb")+"','"+m.get("jhlb")+"','"+(m.get("xmje").trim().equals("")?"0":m.get("xmje").trim())+"',"+(m.get("lxrq").equals("")?"null":("'"+m.get("lxrq")+"-01-01"+"'"))+","+(m.get("strdate").equals("")?"null":"'"+m.get("strdate")+"'")+","+(m.get("enddate").equals("")?"null":"'"+m.get("enddate")+"'")+",'"+m.get("sm")+"',1)";
		this.getSimpleJdbcTemplate().update(sql);
		
		return 1;
	}
	
	public Map preCdxmU(String dwid,String xh){
		Map map = new HashMap();
		String 			sql = " select a.dwid,a.xh,a.xmmc,a.xmbh,a.xmwh,a.xmjb,a.jhlb,a.xmje,CONVERT(varchar(100), a.lxrq, 23) AS lxrq,CONVERT(varchar(100), a.strdate, 23) AS strdate," +
		" CONVERT(varchar(100), a.enddate, 23) AS enddate,a.status,a.sm " +
		" " +
		" from dw_cdxm a where a.dwid='"+dwid+"'  and isNull(a.status,1)=1 and xh="+xh;
		map = this.getSimpleJdbcTemplate().queryForMap(sql);
		return map;
	}
	
	public int doCdxmU(String dwid,String xh,Map<String, String > m){
		String sql = " update dw_cdxm set xmmc='"+m.get("xmmc")+"',xmwh='"+m.get("xmwh")+"',xmbh='"+m.get("xmbh")+"',xmjb='"+m.get("xmjb")+"'," +
				"jhlb='"+m.get("jhlb")+"',xmje='"+(m.get("xmje").trim().equals("")?"0":m.get("xmje").trim())+"',lxrq="+(m.get("lxrq").equals("")?"null":("'"+m.get("lxrq")+"-01-01"+"'"))+",strdate="+(m.get("strdate").equals("")?"null":"'"+m.get("strdate")+"'")+",enddate="+(m.get("enddate").equals("")?"null":"'"+m.get("enddate")+"'")+",sm='"+m.get("sm")+"' where dwid='"+dwid+"' and xh="+xh+"   and isNull(status,1)=1 ";
		this.getSimpleJdbcTemplate().update(sql);
		return 1;
	}
	
	public int doCdxmD(String dwid,String[] xh){
		String sql = " delete from dw_cdxm where dwid="+dwid+" and xh in("+ArrayToString(xh)+")  and isNull(status,1)=1 ";
		this.getSimpleJdbcTemplate().update(sql);
		return 1;
	}
	
	public List getDictListByDlb(String lbid,String dictbh,String bz){
		List l = new ArrayList();
		l = this.getJdbcTemplate().queryForList("select * from xt_dict where lbid="+lbid+" and dictbh like '"+dictbh+"%' and len(dictbh)=6");
		return l;
	}
	
	/**
	 * 承担横向项目
	 */
	public List getCdhxxmList(String dwid){
		String sql = " ";
		List<Map<String, String>> list = new ArrayList();
		if(dwid != null && !dwid.equals("")){
			sql =" select count(*) from dw_cdhxxm_sb where dwid="+dwid;
			if(this.getSimpleJdbcTemplate().queryForInt(sql) == 0){
				sql = " insert into dw_cdhxxm_sb select * from dw_cdhxxm where dwid="+dwid;
				this.getSimpleJdbcTemplate().update(sql);
			}
			
			sql = " select a.dwid,a.xh,a.xmmc,a.dfdwmc,a.yzbm,a.htlb,a.dq,a.htje,substring(CONVERT(varchar(100), a.htqdrq, 23),0,5) AS htqdrq,CONVERT(varchar(100), a.strdate, 23) AS strdate," +
					" CONVERT(varchar(100), a.enddate, 23) AS enddate,a.sm,a.status," +
					" (select b.dictname from xt_dict b where b.lbid=21 and b.dictbh=a.htlb) as htlb_mc," +
					" (select b.dictname from xt_dict b where b.lbid=22 and b.dictbh=a.dq) as dq_mc  " +
					" from dw_cdhxxm_sb a where a.dwid='"+dwid+"'  and isNull(a.status,1)=1";
			list = this.getJdbcTemplate().queryForList(sql);
		}
		return list;
	}
	public String getHxxmjehj(String dwid){
		String sql = " select isNull(sum(htje),0) from dw_cdhxxm_sb where dwid="+dwid;
		return String.valueOf(this.getJdbcTemplate().queryForObject(sql, String.class));
	}
	public int doCdhxxmI(String dwid,Map<String, String > m){
		String sql = "";
		//获取 XH 最大值
		Integer maxxh = 1;
		sql = "select count(*) from dw_cdhxxm_sb where dwid='"+dwid+"'  and isNull(status,1)=1";
		if( this.getSimpleJdbcTemplate().queryForInt(sql) > 0){
			sql = " select max(xh) from dw_cdhxxm_sb where dwid='"+dwid+"'  and isNull(status,1)=1 ";
			maxxh = this.getSimpleJdbcTemplate().queryForInt(sql)+1;
		}
		sql = " insert into dw_cdhxxm_sb(dwid,xh,xmmc,dfdwmc,yzbm,htlb,dq,htje,htqdrq,strdate,enddate,sm,status) " +
			" values('"+dwid+"','"+maxxh+"','"+m.get("xmmc")+"','"+m.get("dfdwmc")+"','"+m.get("yzbm")+"'," +
			"'"+m.get("htlb")+"','"+m.get("dq")+"','"+(m.get("htje").trim().equals("")?"0":m.get("htje").trim())+"',"+(m.get("htqdrq").equals("")?"null":("'"+m.get("htqdrq")+"'"))+","+(m.get("strdate").equals("")?"null":"'"+m.get("strdate")+"'")+","+(m.get("enddate").equals("")?"null":"'"+m.get("enddate")+"'")+",'"+m.get("sm")+"',1)";
		this.getSimpleJdbcTemplate().update(sql);
		
		return 1;
	}
	
	public Map preCdhxxmU(String dwid,String xh){
		Map map = new HashMap();
		String 	sql = " select a.dwid,a.xh,a.xmmc,a.dfdwmc,a.yzbm,a.htlb,a.dq,a.htje,substring(CONVERT(varchar(100), a.htqdrq, 23),0,5) AS htqdrq,CONVERT(varchar(100), a.strdate, 23) AS strdate," +
		" CONVERT(varchar(100), a.enddate, 23) AS enddate,a.sm,a.status " +
		" from dw_cdhxxm_sb a where a.dwid='"+dwid+"'  and isNull(a.status,1)=1 and xh="+xh;
		map = this.getSimpleJdbcTemplate().queryForMap(sql);
		return map;
	}
	
	public int doCdhxxmU(String dwid,String xh,Map<String, String > m){
		String sql = " update dw_cdhxxm_sb set xmmc='"+m.get("xmmc")+"',dfdwmc='"+m.get("dfdwmc")+"',yzbm='"+m.get("yzbm")+"',htlb='"+m.get("htlb")+"'," +
				"dq='"+m.get("dq")+"',htje='"+(m.get("htje").trim().equals("")?"0":m.get("htje").trim())+"',htqdrq="+(m.get("htqdrq").equals("")?"null":("'"+m.get("htqdrq")+"'"))+",strdate="+(m.get("strdate").equals("")?"null":"'"+m.get("strdate")+"'")+",enddate="+(m.get("enddate").equals("")?"null":"'"+m.get("enddate")+"'")+",sm='"+m.get("sm")+"' where dwid='"+dwid+"' and xh="+xh+"   and isNull(status,1)=1 ";
		this.getSimpleJdbcTemplate().update(sql);
		return 1;
	}
	
	public int doCdhxxmD(String dwid,String[] xh){
		String sql = " delete from dw_cdhxxm_sb where dwid="+dwid+" and xh in("+ArrayToString(xh)+")  and isNull(status,1)=1 ";
		this.getSimpleJdbcTemplate().update(sql);
		return 1;
	}
	
	/**
	 * 拥有知识产权情况
	 */

	
	public List getZscqList(String dwid){
		String sql = " ";
		List<Map<String, String>> list = new ArrayList();
		if(dwid != null && !dwid.equals("")){
			sql =" select count(*) from dw_zscq_sb where dwid="+dwid;
			if(this.getSimpleJdbcTemplate().queryForInt(sql) == 0){
				sql = " insert into dw_zscq_sb select * from dw_zscq where dwid="+dwid;
				this.getSimpleJdbcTemplate().update(sql);
			}
			
			sql = " select a.*,(select b.dictname from xt_dict b where b.lbid=18 and b.dictbh=a.zslx) as zslx_mc" +
					" from dw_zscq_sb a where a.dwid='"+dwid+"'  and isNull(a.status,1)=1";
			list = this.getJdbcTemplate().queryForList(sql);
		}
		return list;
	}
	
	public int doZscqI(String dwid,Map<String, String > m){
		String sql = "";
		//获取 XH 最大值
		Integer maxxh = 1;
		sql = "select count(*) from dw_zscq_sb where dwid='"+dwid+"'  and isNull(status,1)=1";
		if( this.getSimpleJdbcTemplate().queryForInt(sql) > 0){
			sql = " select max(xh) from dw_zscq_sb where dwid='"+dwid+"'  and isNull(status,1)=1";
			maxxh = this.getSimpleJdbcTemplate().queryForInt(sql)+1;
		}
		sql = " insert into dw_zscq_sb(dwid,xh,zsmc,zsno,zslx,sm,sqnf,sqr,status) " +
			" values('"+dwid+"','"+maxxh+"','"+m.get("zsmc")+"','"+m.get("zsno")+"'," +
			"'"+m.get("zslx")+"','"+m.get("sm")+"','"+m.get("sqnf")+"','"+m.get("sqr")+"',1)";
		this.getSimpleJdbcTemplate().update(sql);
		
		return 1;
	}
	
	public Map preZscqU(String dwid,String xh){
		Map map = new HashMap();
		String sql = " select * from dw_zscq_sb where dwid='"+dwid+"' and xh="+xh+" and isNull(status,1)=1";
		map = this.getSimpleJdbcTemplate().queryForMap(sql);
		return map;
	}
	
	public int doZscqU(String dwid,String xh,Map<String, String > m){
		String sql = " update dw_zscq_sb set zsmc='"+m.get("zsmc")+"',zsno='"+m.get("zsno")+"'," +
				" zslx='"+m.get("zslx")+"',sm='"+m.get("sm")+"', " +
				" sqnf='"+m.get("sqnf")+"',sqr='"+m.get("sqr")+"' " +
				" where dwid='"+dwid+"' and xh="+xh+"   and isNull(status,1)=1 ";
		this.getSimpleJdbcTemplate().update(sql);
		return 1;
	}
	
	public int doZscqD(String dwid,String[] xh){
		String sql = " delete from dw_zscq_sb where dwid="+dwid+" and xh in("+ArrayToString(xh)+")  and isNull(status,1)=1 ";
		this.getSimpleJdbcTemplate().update(sql);
		return 1;
	}
	/**
	 * 奖励情况
	 */


	public List getHjqkList(String dwid){
		String sql = " ";
		List<Map<String, String>> list = new ArrayList();
		if(dwid != null && !dwid.equals("")){
			sql =" select count(*) from dw_hjqk_sb where dwid="+dwid;
			if(this.getSimpleJdbcTemplate().queryForInt(sql) == 0){
				sql = " insert into dw_hjqk_sb select * from dw_hjqk where dwid="+dwid;
				this.getSimpleJdbcTemplate().update(sql);
			}
			
			sql = " select a.dwid,a.xh,a.jxmc,a.hjjb,zzje,CONVERT(varchar(100), a.hjrq, 23) AS hjrq,a.hjlb,a.sm,a.status , " +
					" (select b.dictname from xt_dict b where b.lbid=16 and b.dictbh=a.hjjb) as hjjb_mc ," +
					" (select b.dictname from xt_dict b where b.lbid=17 and b.dictbh=a.hjlb) as hjlb_mc " +
					" from dw_hjqk_sb a where a.dwid='"+dwid+"'  and isNull(a.status,1)=1";
			list = this.getJdbcTemplate().queryForList(sql);
		}
		return list;
	}
	
	public int doHjqkI(String dwid,Map<String, String > m){
		String sql = "";
		//获取 XH 最大值
		Integer maxxh = 1;
		sql = "select count(*) from dw_hjqk_sb where dwid='"+dwid+"'  and isNull(status,1)=1";
		if( this.getSimpleJdbcTemplate().queryForInt(sql) > 0){
			sql = " select max(xh) from dw_hjqk_sb where dwid='"+dwid+"'  and isNull(status,1)=1";
			maxxh = this.getSimpleJdbcTemplate().queryForInt(sql)+1;
		}
		sql = " insert into dw_hjqk_sb(dwid,xh,jxmc,hjjb,hjrq,hjlb,sm,zzje,status) " +
			" values('"+dwid+"','"+maxxh+"','"+m.get("jxmc")+"','"+m.get("hjjb")+"'," +
			"'"+m.get("hjrq")+"','"+m.get("hjlb")+"','"+m.get("sm")+"','"+(m.get("zzje").equals("")?"0":m.get("zzje"))+"',1)";
		this.getSimpleJdbcTemplate().update(sql);
		
		return 1;
	}
	
	public Map preHjqkU(String dwid,String xh){
		Map map = new HashMap();
		String sql = " select a.dwid,a.xh,a.jxmc,zzje,a.hjjb,CONVERT(varchar(100), a.hjrq, 23) AS hjrq,a.hjlb,a.sm,a.status from dw_hjqk_sb a where a.dwid='"+dwid+"' and xh="+xh+" and isNull(a.status,1)=1";
		map = this.getSimpleJdbcTemplate().queryForMap(sql);
		return map;
	}
	
	public int doHjqkU(String dwid,String xh,Map<String, String > m){
		String sql = " update dw_hjqk_sb set jxmc='"+m.get("jxmc")+"',hjjb='"+m.get("hjjb")+"',hjrq='"+m.get("hjrq")+"',sm='"+m.get("sm")+"'," +
				"hjlb='"+m.get("hjlb")+"',zzje="+(m.get("zzje").equals("")?"0":m.get("zzje"))+" where dwid='"+dwid+"' and xh="+xh+"   and isNull(status,1)=1 ";
		this.getSimpleJdbcTemplate().update(sql);
		return 1;
	}
	
	public int doHjqkD(String dwid,String[] xh){
		String sql = " delete from dw_hjqk_sb where dwid="+dwid+" and xh in("+ArrayToString(xh)+")  and isNull(status,1)=1 ";
		this.getSimpleJdbcTemplate().update(sql);
		return 1;
	}
	
	
	/**
	 * 联系人
	 */

	public List getLxrList(String dwid){
		String sql = " ";
		List<Map<String, String>> list = new ArrayList();
		if(dwid != null && !dwid.equals("")){
			sql =" select count(*) from dw_lxr_sb where dwid="+dwid;
			if(this.getSimpleJdbcTemplate().queryForInt(sql) == 0){
				sql = " insert into dw_lxr_sb select * from dw_lxr where dwid="+dwid;
				this.getSimpleJdbcTemplate().update(sql);
			}
			
			sql = " select a.dwid,a.xh,a.lxrxm,a.sex,a.zw,a.tel,a.phone,a.ssbm,isnull(dylxrbz,0) dylxrbz,a.email,a.qq,a.sm, " +
					" (select b.dictname from xt_dict b where b.lbid=14 and b.dictbh=a.sex) as sex_mc ," +
					" (select b.dictname from xt_dict b where b.lbid=15 and b.dictbh=a.zc) as zc_mc " +
					" from dw_lxr_sb a where a.dwid='"+dwid+"'  and isNull(a.status,1)=1 ";
			list = this.getJdbcTemplate().queryForList(sql);
		}
		return list;
	}
	
	public int doLxrI(String dwid,Map<String, String > m){
		String sql = "";
		//获取 XH 最大值
		Integer maxxh = 1;
		sql = "select count(*) from dw_lxr_sb where dwid='"+dwid+"'  and isNull(status,1)=1";
		if( this.getSimpleJdbcTemplate().queryForInt(sql) > 0){
			sql = " select max(xh) from dw_lxr_sb where dwid='"+dwid+"'  and isNull(status,1)=1";
			maxxh = this.getSimpleJdbcTemplate().queryForInt(sql)+1;
		}
		sql = " insert into dw_lxr_sb(dwid,xh,lxrxm,sex,zc,zw,tel,phone,ssbm,email,qq,sm,status) " +
			" values('"+dwid+"','"+maxxh+"','"+m.get("lxrxm")+"','"+m.get("sex")+"'," +
			"'"+m.get("zc")+"','"+m.get("zw")+"','"+m.get("tel")+"','"+m.get("phone")+"','"+m.get("ssbm")+"','"+m.get("email")+"','"+m.get("qq")+"','"+m.get("sm")+"',1)";
		this.getSimpleJdbcTemplate().update(sql);
		
		return 1;
	}
	
	public Map preLxrU(String dwid,String xh){
		Map map = new HashMap();
		String sql = " select dwid,xh,lxrxm,sex,zc,zw,tel,phone,ssbm,a.email,a.qq,status,sm from dw_lxr_sb a where a.dwid='"+dwid+"' and xh="+xh+" and isNull(a.status,1)=1";
		map = this.getSimpleJdbcTemplate().queryForMap(sql);
		return map;
	}
	
	public int doLxrU(String dwid,String xh,Map<String, String > m){
		String sql = " update dw_lxr_sb set lxrxm='"+m.get("lxrxm")+"',sex='"+m.get("sex")+"',zc='"+m.get("zc")+"'," +
				"zw='"+m.get("zw")+"',tel='"+m.get("tel")+"',phone='"+m.get("phone")+"',ssbm='"+m.get("ssbm")+"',email='"+m.get("email")+"'" +
						",qq='"+m.get("qq")+"',sm='"+m.get("sm")+"' " +
						" where dwid='"+dwid+"' and xh="+xh+"   and isNull(status,1)=1 ";
		this.getSimpleJdbcTemplate().update(sql);
		return 1;
	}
	
	public int doLxrD(String dwid,String[] xh){
		String sql = " delete from dw_lxr_sb where dwid="+dwid+" and xh in("+ArrayToString(xh)+")  and isNull(status,1)=1 ";
		this.getSimpleJdbcTemplate().update(sql);
		return 1;
	}
	
	public int doSetLxrForDy(String dwid,String xh){
		String sql = " update dw_lxr_sb set DYLXRBZ=0 where dwid="+dwid;
		this.getSimpleJdbcTemplate().update(sql);
		sql = " update dw_lxr_sb set DYLXRBZ=1 where dwid="+dwid+" and xh="+xh;
		this.getSimpleJdbcTemplate().update(sql); 
		return 1;
	}
	/**
	 * 预览
	 */
	public Map preView(String dwid){
		Map m = new HashMap();
		String sql = "";
		sql = "SELECT     DWID, DWDM, DWZT, DWMC, isNUll(LOGINNAME,'') LOGINNAME, isNull(PASSWORD,'') PASSWORD, isNull(FRDB,'') FRDB, isNull(CONVERT(varchar(100), CLRQ, 23),'') AS CLRQ, NWZ, isNull(ZCZB,0) ZCZB, isnull(DWJJ,'') DWJJ, BGDD1," +
				" BGDD2, BGDD3, isNull(BGDD4,'') BGDD4, isNull(JZMJ,0) JZMJ, LXRXM, SEX, ZC, ZW, TEL, PHONE, SSBM, DWLX,isnull(SM,'') SM,isnull(SWGLM,'') SWGLM,isnull(NSRSBH,'') NSRSBH,isnull(HGTDPC,'') HGTDPC," +
				" isNull((SELECT     DICTNAME  FROM          dbo.XT_DICT AS b WHERE      (LBID = 4) AND (a.DWZT = DICTBH)),'') AS DWZT_MC," +
				" isNull((SELECT     DICTNAME FROM          dbo.XT_DICT AS b WHERE      (LBID = 5) AND (a.NWZ = DICTBH)),'') AS NWZ_MC," +
				" isNUll((SELECT     DICTNAME  FROM          dbo.XT_DICT AS b WHERE      (LBID = 6) AND (a.BGDD1 = DICTBH)),'') AS BGDD1_MC," +
				" isNull((SELECT     DICTNAME FROM          dbo.XT_DICT AS b WHERE      (LBID = 7) AND (a.BGDD2 = DICTBH)),'') AS BGDD2_MC," +
				" isNull((SELECT     DICTNAME FROM          dbo.XT_DICT AS b WHERE      (LBID = 8) AND (a.BGDD3 = DICTBH)),'') AS BGDD3_MC," +
				" isNull((SELECT     DICTNAME FROM          dbo.XT_DICT AS b WHERE      (LBID = 9) AND (a.DWLX = DICTBH)),'') AS DWLX_MC," +
				" isNull((SELECT     DICTNAME FROM          dbo.XT_DICT AS b WHERE      (LBID = 14) AND (a.SEX = DICTBH)),'') AS SEX_MC," +
				" isNull((SELECT     DICTNAME  FROM          dbo.XT_DICT AS b  WHERE      (LBID = 15) AND (a.ZC = DICTBH)),'') AS ZC_MC," +
				" isNull((SELECT     XMMC FROM    dbo.XM_INFO WHERE      (XMID = (SELECT     XMID FROM dbo.DW_XM WHERE      (DWID = a.DWID)))),'') AS XM_MC" +
				" FROM         dbo.DW_INFO AS a WHERE     ISNULL(STATUS, 1) = 1 and a.dwid="+dwid;
		m.put("dwxx", this.getSimpleJdbcTemplate().queryForMap(sql));
		
		List<Map<String, String>> gqbl = new ArrayList();
		sql = " select * from dw_gqbl where dwid='"+dwid+"'  and isNull(status,1)=1";
		gqbl = this.getJdbcTemplate().queryForList(sql, new Object[]{});
		m.put("gqbl", gqbl);
		
		m.put("dwsxs", this.getZsdwMutSelWithViewByDwid(dwid, "1","10"));
		m.put("cyfls", this.getZsdwMutSelWithViewByDwid(dwid, "2","11"));
		m.put("jszys", this.getZsdwMutSelWithViewByDwid(dwid, "3","12"));
		m.put("yjlbs", this.getZsdwMutSelWithViewByDwid(dwid, "4","13"));
		
		
		List<Map<String, String>> lxr = new ArrayList();
		sql = " select a.dwid,a.xh,a.lxrxm,a.sex,a.zw,a.tel,a.phone,a.ssbm,isnull(dylxrbz,0) dylxrbz,a.email,a.qq, " +
			" (select b.dictname from xt_dict b where b.lbid=14 and b.dictbh=a.sex) as sex_mc ," +
			" (select b.dictname from xt_dict b where b.lbid=15 and b.dictbh=a.zc) as zc_mc " +
			" from dw_lxr a where a.dwid='"+dwid+"'  and isNull(a.status,1)=1 ";
		lxr = this.getJdbcTemplate().queryForList(sql, new Object[]{});
		m.put("lxr", lxr);
		
		List<Map<String, String>> cdxm = new ArrayList();
		sql = " select a.dwid,a.xh,a.xmmc,a.xmbh,a.xmjb,a.jhlb,a.xmje,substring(CONVERT(varchar(100), a.lxrq, 23),0,5) AS lxrq," +
				" CONVERT(varchar(100), a.strdate, 23) AS strdate," +
				" CONVERT(varchar(100), a.enddate, 23) AS enddate,a.status," +
				" (select b.dictname from xt_dict b where b.lbid=16 and b.dictbh=a.xmjb) as xmjb_mc," +
				" (select b.dictname from xt_dict b where b.lbid=20 and b.dictbh=a.jhlb) as jhlb_mc " +
				" from dw_cdxm a where a.dwid='"+dwid+"'  and isNull(a.status,1)=1";
		cdxm = this.getJdbcTemplate().queryForList(sql, new Object[]{});
		m.put("cdxm", cdxm);
		
		sql = " select isnull(sum(xmje),0) from dw_cdxm a where dwid='"+dwid+"'  and isNull(status,1)=1";
		m.put("cdxmhjje", this.getSimpleJdbcTemplate().queryForObject(sql, Double.class).toString());
		
		List<Map<String, String>> zscq = new ArrayList();
		sql = " select * from dw_zscq where dwid='"+dwid+"'  and isNull(status,1)=1";
		zscq = this.getJdbcTemplate().queryForList(sql, new Object[]{});
		m.put("zscq", zscq);
		
		
		List<Map<String, String>> hjqk = new ArrayList();
		sql = " select a.dwid,a.xh,a.jxmc,a.hjjb,CONVERT(varchar(100), a.hjrq, 23) AS hjrq,a.hjlb,zzje,a.status , " +
		" (select b.dictname from xt_dict b where b.lbid=16 and b.dictbh=a.hjjb) as hjjb_mc ," +
		" (select b.dictname from xt_dict b where b.lbid=17 and b.dictbh=a.hjlb) as hjlb_mc " +
		" from dw_hjqk a where a.dwid='"+dwid+"'  and isNull(a.status,1)=1";
		hjqk = this.getJdbcTemplate().queryForList(sql, new Object[]{});
		m.put("hjqk", hjqk);
		return m;
	}
	
	/*******************************************************************************************
	 * 机构人员情况处理
	 * @param dwid
	 * @return
	 *****************************************************************************************/
	public Object getRyxxList(LimitPage limit, Map<String, String> parMap, SortInfo sortInfo, List<FilterInfo> filterInfos,String dwid){
		String sql = " ";
		//List<Map<String, String>> list = new ArrayList();
		if(dwid != null && !dwid.equals("")){
			String name = parMap.get("name") == null ? "" :  parMap.get("name");
			String where = parMap.get("where") == null ? "" :  parMap.get("where");
			
			sql = " select * from dw_ryxx_sb_v a where a.dwid='"+dwid+"'  ";
			if(where != null && !where.equals("") && !name.equals("")){
				sql += " and a."+where+" like '"+name.replaceAll("'", "\'")+"%'";
			}
			String sortCond = getGridSortString(sortInfo);
			String filterCond = getGridFilterString(filterInfos);
			if (filterCond != null) {
				sql += filterCond;
			}
			if (sortCond != null) {
				sql += " order by " + sortCond.split("_")[0];
			} else {
				sql += " order by dwid,RYID";
			}
			if (limit != null) {
				return this.queryForListWithPage(sql, limit);
			} else {
				return this.simpleJdbcTemplate.queryForList(sql);
			}
		}else{
			return new ArrayList();
		}
		
	}
	
	public List getRyxxList(String dwid){
		String sql = " ";
		List<Map<String, String>> list = new ArrayList();
		if(dwid != null && !dwid.equals("")){
			
			sql = " select a.dwid,a.xh,a.xm,a.sex,convert(varchar(100),a.birthday,23) birthday," +
				" a.xl,a.xw,a.zc,a.zw,a.hwlxqk,a.yjfx,convert(varchar(100),a.qpsj,23) qpsj,a.zjz,a.sm, " +
				" (select b.dictname from xt_dict b where b.lbid=14 and b.dictbh=a.sex) as sex_mc ," +
				" (select b.dictname from xt_dict b where b.lbid=15 and b.dictbh=a.zc) as zc_mc," +
				" (select b.dictname from xt_dict b where b.lbid=25 and b.dictbh=a.xl) as xl_mc," +
				" a.ryid,a.sfz " +
				" from dw_ryxx_sb a where a.dwid='"+dwid+"'  and isNull(a.status,1)=1 ";
			list = this.getJdbcTemplate().queryForList(sql);
		}
		return list;
	}
	
	public int doRyxxI(String dwid,Map<String, String > m,String[] tt,String[] xx,String[] yy,String[] fj,String[] fjmc,String[] ttype,String fjpath,String[] xtdict28s){
		String sql = "";
		//获取 XH 最大值
		if(m.get("sfz") == null || m.get("sfz").trim().equals("")){
			throw new BusException("身份证号不能为空!");
		}
		sql = " select count(*) from dw_ryxx_sb where sfz='"+m.get("sfz")+"' and isnull(status,1) in (1,2)";
		if(this.getSimpleJdbcTemplate().queryForInt(sql) > 0){
			throw new BusException("身份证号已经存在!");
		}
		Integer maxxh = 1;
		sql = "select count(*) from dw_ryxx_sb where dwid='"+dwid+"'  and isNull(status,1)=1";
		if( this.getSimpleJdbcTemplate().queryForInt(sql) > 0){
			sql = " select max(xh) from dw_ryxx_sb where dwid='"+dwid+"'  and isNull(status,1)=1";
			maxxh = this.getSimpleJdbcTemplate().queryForInt(sql)+1;
		}
		String ryid = String.valueOf(this.getMaxKey("DW_RYXX"));
		sql = " insert into dw_ryxx_sb(ryid,dwid,xh,xm,sex,birthday, xl,xw,zc,zw," +
				"hwlxqk,yjfx,qpsj,zjz,sm,sfz,status,intime,zzmm,jg,hjd,ryjl,fjmc,fjpath,sbbz) " +
			" values("+ryid+",'"+dwid+"','"+maxxh+"','"+m.get("xm")+"','"+m.get("sex")+"'," +
			""+transToD(m.get("birthday"))+",'"+m.get("xl")+"','','"+m.get("zc")+"',''," +
			"'"+m.get("hwlxqk")+"','"+m.get("yjfx")+"',"+transToD(m.get("qpsj"))+",'"+m.get("zjz")+"','"+m.get("sm")+"','"+m.get("sfz")+"',1,getdate()," +
			"'"+m.get("zzmm")+"','"+m.get("jg")+"','"+m.get("hjd")+"','"+m.get("ryjl")+"','"+m.get("fjmc")+"'" +
					",'"+fjpath+"',0)";
		this.getSimpleJdbcTemplate().update(sql);
		
		if(fj != null && fj.length > 0){
			for(int i=0;i<fj.length;i++){
				sql = " insert into dw_ryxx_fj(dwid,ryid,tt,xx,yy,fj,fjmc,type,intime,fjpath)" +
						" values("+dwid+","+ryid+","+tt[i]+","+xx[i]+","+yy[i]+",'"+fj[i]+"','"+fjmc[i]+"','"+ttype[i]+"',getdate(),'"+fjpath+"')";
				this.getSimpleJdbcTemplate().update(sql);
			}
		}
		
		if(xtdict28s != null ){
			for(int i=0;i<xtdict28s.length;i++){
				sql = "insert into dw_ryxx_tr_sb(dwid,ryid,dictbh) values("+dwid+","+ryid+",'"+xtdict28s[i]+"')";
				this.getSimpleJdbcTemplate().update(sql);
			}
		}
		return 1;
	}
	
	public Map preRyxxU(String dwid,String ryid){
		Map map = new HashMap();
		String sql = " select a.ryid,a.sfz,dwid,xh,xm,sex,convert(varchar(100),birthday,23) birthday, xl,xw,zc,zw,hwlxqk," +
				"zzmm,fjmc,fjpath,ryjl,jg,hjd," +
				"yjfx,convert(varchar(100),qpsj,23) qpsj,zjz,sm,status,sbbz,thyy,fhyy,isnull(a.shzzbz,0) shzzbz,isnull(a.zfzzbz,0) zfzzbz,isnull(a.ajfzzbz,0) ajfzzbz from dw_ryxx_sb a where a.dwid='"+dwid+"' and ryid="+ryid+" ";
		map = this.getSimpleJdbcTemplate().queryForMap(sql);
		return map;
	}
	
	public List<XtDict> getDwryxxTrList(String dwid,String ryid){
		String sql = " select * from xt_dict where lbid=28 and dictbh in " +
				" ( select dictbh from dw_ryxx_tr_sb where dwid="+dwid+" and ryid="+ryid+")";
		return this.getSimpleJdbcTemplate().query(sql,resultBeanMapper(XtDict.class));
	}
	
	
	public int doRyxxU(String dwid,String ryid,Map<String, String > m,String[] tt,String[] xx,String[] yy,String[] fj,String[] fjmc,String[] ttype,String fjpath,String[] xtdict28s){
		String sql = "";
		if(m.get("sfz") == null || m.get("sfz").trim().equals("")){
			throw new BusException("身份证号不能为空!");
		}
		sql = " select count(*) from dw_ryxx_sb where sfz='"+m.get("sfz")+"' and ryid != "+ryid+" and isnull(status,1) in (1,2) ";
		if(this.getSimpleJdbcTemplate().queryForInt(sql) > 0){
			throw new BusException("身份证号已经存在!");
		}
		
		sql = " select count(*) from dw_ryxx_sb where ryid = "+ryid+" and isnull(sbbz,0)=1";
		if(this.getSimpleJdbcTemplate().queryForInt(sql) > 0){
			throw new BusException("当前信息在审核中，不能修改!");
		}
		
		sql = " select count(*) from dw_ryxx_sb where ryid = "+ryid+" and isnull(sbbz,0)=2";
		if(this.getSimpleJdbcTemplate().queryForInt(sql) > 0){
			throw new BusException("当前信息审核通过，不能修改!");
		}
		
		
		sql = " select count(*) from dw_ryxx_sb where ryid = "+ryid+" and isNull(status,1)=1 ";
		if(this.getSimpleJdbcTemplate().queryForInt(sql) == 0){
			throw new BusException("当前信息非正常状态，不能修改!");
		}
		
		sql = " update dw_ryxx_sb set sbbz=0,xm='"+m.get("xm")+"',sex='"+m.get("sex")+"',birthday="+transToD(m.get("birthday"))+"," +
				" xl='"+m.get("xl")+"',xw='',zc='"+m.get("zc")+"',zw='',hwlxqk='"+m.get("hwlxqk")+"'," +
						"yjfx='"+m.get("yjfx")+"',qpsj="+transToD(m.get("qpsj"))+",zjz='"+m.get("zjz")+"',sm='"+m.get("sm")+"'" +
								",sfz='"+m.get("sfz")+"',zzmm='"+m.get("zzmm")+"',jg='"+m.get("jg")+"',hjd='"+m.get("hjd")+"'" +
								",ryjl='"+m.get("ryjl")+"',fjpath='"+fjpath+"',fjmc='"+m.get("fjmc")+"' "+
						" where dwid='"+dwid+"' and ryid="+ryid+"   and isNull(status,1)=1 ";
		this.getSimpleJdbcTemplate().update(sql);
		

		if(fj != null && fj.length > 0){
			for(int i=0;i<fj.length;i++){
				sql = " insert into dw_ryxx_fj(dwid,ryid,tt,xx,yy,fj,fjmc,type,intime,fjpath)" +
						" values("+dwid+","+ryid+","+tt[i]+","+xx[i]+","+yy[i]+",'"+fj[i]+"','"+fjmc[i]+"','"+ttype[i]+"',getdate(),'"+fjpath+"')";
				this.getSimpleJdbcTemplate().update(sql);
			}
		}
		
		sql = "delete from dw_ryxx_tr_sb where dwid="+dwid+" and ryid="+ryid;
		this.getSimpleJdbcTemplate().update(sql);
		if(xtdict28s != null ){
			for(int i=0;i<xtdict28s.length;i++){
				sql = "insert into dw_ryxx_tr_sb(dwid,ryid,dictbh) values("+dwid+","+ryid+",'"+xtdict28s[i]+"')";
				this.getSimpleJdbcTemplate().update(sql);
			}
		}
		
		return 1;
	}
	
	public int doRyxxD(String dwid,String ryid){
		/**
		String sql = " delete from dw_ryxx_sb where dwid="+dwid+" and ryid in("+ArrayToString(ryid)+")  and isNull(status,1)=1 ";
		this.getSimpleJdbcTemplate().update(sql);
		sql = "delete from dw_ryxx_fj where dwid="+dwid+" and ryid in("+ArrayToString(ryid)+") ";
		this.getSimpleJdbcTemplate().update(sql);
		**/
		String sql = "";
		sql = " select count(*) from dw_ryxx where dwid='"+dwid+"' and ryid in ("+ryid+") ";
		if(this.getSimpleJdbcTemplate().queryForInt(sql) > 0){
			throw new BusException("当前有相关信息已经生效，不能删除！");
		}
		
		sql = " select count(*) from dw_ryxx_sb where dwid='"+dwid+"' and ryid in ("+ryid+") and isnull(sbbz,0)=1";
		if(this.getSimpleJdbcTemplate().queryForInt(sql) > 0){
			throw new BusException("当前有相关信息已经上报，不能删除");
		}
		sql = " delete from dw_ryxx_sb  where dwid='"+dwid+"' and ryid in ("+ryid+")";
		this.getSimpleJdbcTemplate().update(sql);
		return 1;
	}
	
	
	public void doRyxxFjD(String dwid,String ryid,String fjname){
		String sql = "delete from dw_ryxx_fj where dwid="+dwid+" and ryid="+ryid+" and fj='"+fjname+"'";
		this.getSimpleJdbcTemplate().update(sql);
	}
	

	/**
	 * 实验室、研发中心建设情况表
	 */
	public List getSysjsqkList(String dwid){
		String sql = " ";
		List<Map<String, String>> list = new ArrayList();
		if(dwid != null && !dwid.equals("")){
			sql =" select count(*) from DW_SYSYFJSQKB_SB where dwid="+dwid;
			if(this.getSimpleJdbcTemplate().queryForInt(sql) == 0){
				sql = " insert into DW_SYSYFJSQKB_SB select * from DW_SYSYFJSQKB where dwid="+dwid;
				this.getSimpleJdbcTemplate().update(sql);
			}
			sql = " select DWID, XH, STATUS, JSNF, JSMC, HJDW, BZ  " +
					" from DW_SYSYFJSQKB_SB a where a.dwid='"+dwid+"'  and isNull(a.status,1)=1";
			list = this.getJdbcTemplate().queryForList(sql);
		}
		return list;
	}
	
	public int doSysjsqkI(String dwid,Map<String, String > m){
		String sql = "";
		//获取 XH 最大值
		Integer maxxh = 1;
		sql = "select count(*) from DW_SYSYFJSQKB_SB where dwid='"+dwid+"'  and isNull(status,1)=1";
		if( this.getSimpleJdbcTemplate().queryForInt(sql) > 0){
			sql = " select max(xh) from DW_SYSYFJSQKB_SB where dwid='"+dwid+"'  and isNull(status,1)=1 ";
			maxxh = this.getSimpleJdbcTemplate().queryForInt(sql)+1;
		}
		sql = " insert into DW_SYSYFJSQKB_SB(DWID, XH, STATUS, JSNF, JSMC, HJDW, BZ) " +
			" values('"+dwid+"','"+maxxh+"',1,'"+m.get("jsnf")+"','"+m.get("jsmc")+"','"+m.get("hjdw")+"'," +
			"'"+m.get("bz")+"')";
		this.getSimpleJdbcTemplate().update(sql);
		
		return 1;
	}
	
	public Map preSysjsqkU(String dwid,String xh){
		Map map = new HashMap();
		String 	sql = " select DWID, XH, STATUS, JSNF, JSMC, HJDW, BZ " +
		" from DW_SYSYFJSQKB_SB a where a.dwid='"+dwid+"'  and isNull(a.status,1)=1 and xh="+xh;
		map = this.getSimpleJdbcTemplate().queryForMap(sql);
		return map;
	}
	
	public int doSysjsqkU(String dwid,String xh,Map<String, String > m){
		String sql = " update DW_SYSYFJSQKB_SB set JSNF='"+m.get("jsnf")+"',JSMC='"+m.get("jsmc")+"',HJDW='"+m.get("hjdw")+"',bz='"+m.get("bz")+"'" +
				" where dwid='"+dwid+"' and xh="+xh+"   and isNull(status,1)=1 ";
		this.getSimpleJdbcTemplate().update(sql);
		return 1;
	}
	
	public int doSysjsqkD(String dwid,String[] xh){
		String sql = " delete from DW_SYSYFJSQKB_SB where dwid="+dwid+" and xh in("+ArrayToString(xh)+")  and isNull(status,1)=1 ";
		this.getSimpleJdbcTemplate().update(sql);
		return 1;
	}
	
	/**
	 *研发机构孵化企业情况表
	 */
	public List getYfjgqkList(String dwid){
		String sql = " ";
		List<Map<String, String>> list = new ArrayList();
		if(dwid != null && !dwid.equals("")){
			sql =" select count(*) from DW_YFJGRHQYQKB_SB where dwid="+dwid;
			if(this.getSimpleJdbcTemplate().queryForInt(sql) == 0){
				sql = " insert into DW_YFJGRHQYQKB_SB select * from DW_YFJGRHQYQKB where dwid="+dwid;
				this.getSimpleJdbcTemplate().update(sql);
			}
			
			sql = " select DWID, XH, STATUS, RHQYMC, NWZ, ZCZB, convert(varchar(20),CLRQ,23) CLRQ, YFJGZG, QTGDZG, ZYLXR, LXTEL, BZ  " +
					" from DW_YFJGRHQYQKB_SB a where a.dwid='"+dwid+"'  and isNull(a.status,1)=1";
			list = this.getJdbcTemplate().queryForList(sql);
		}
		return list;
	}
	
	public int doYfjgqkI(String dwid,Map<String, String > m){
		String sql = "";
		//获取 XH 最大值
		Integer maxxh = 1;
		sql = "select count(*) from DW_YFJGRHQYQKB_SB where dwid='"+dwid+"'  and isNull(status,1)=1";
		if( this.getSimpleJdbcTemplate().queryForInt(sql) > 0){
			sql = " select max(xh) from DW_YFJGRHQYQKB where dwid='"+dwid+"'  and isNull(status,1)=1 ";
			maxxh = this.getSimpleJdbcTemplate().queryForInt(sql)+1;
		}
		sql = " insert into DW_YFJGRHQYQKB_SB(DWID, XH, STATUS, RHQYMC, NWZ, ZCZB, CLRQ, YFJGZG, QTGDZG, ZYLXR, LXTEL, BZ) " +
			" values('"+dwid+"','"+maxxh+"',1,'"+m.get("rhqymc")+"','"+m.get("nwz")+"',"+transToN(m.get("zczb"))+","+this.transToD(m.get("clrq"))+"," +
			""+transToN(m.get("yfjgzg")) +","+transToN(m.get("qtgdzg"))+",'"+m.get("zylxr")+"','"+m.get("lxtel")+"','"+m.get("bz")+"')";
		this.getSimpleJdbcTemplate().update(sql);
		
		return 1;
	}
	
	public Map preYfjgqkU(String dwid,String xh){
		Map map = new HashMap();
		String 	sql = " select DWID, XH, STATUS, RHQYMC, NWZ, ZCZB,convert(varchar(20),CLRQ,23) CLRQ, YFJGZG, QTGDZG, ZYLXR, LXTEL, BZ " +
		" from DW_YFJGRHQYQKB_SB a where a.dwid='"+dwid+"'  and isNull(a.status,1)=1 and xh="+xh;
		map = this.getSimpleJdbcTemplate().queryForMap(sql);
		return map;
	}
	
	public int doYfjgqkU(String dwid,String xh,Map<String, String > m){
		String sql = " update DW_YFJGRHQYQKB_SB set rhqymc='"+m.get("rhqymc")+"',nwz='"+m.get("nwz")+"',zczb="+transToN(m.get("zczb"))+"," +
				"clrq="+this.transToD(m.get("clrq"))+"," +
			"yfjgzg="+transToN(m.get("yfjgzg")) +",qtgdzg="+transToN(m.get("qtgdzg"))+",zylxr='"+m.get("zylxr")+"',lxtel='"+m.get("lxtel")+"',bz='"+m.get("bz")+"'" +
				" where dwid='"+dwid+"' and xh="+xh+"   and isNull(status,1)=1 ";
		this.getSimpleJdbcTemplate().update(sql);
		return 1;
	}
	
	public int doYfjgqkD(String dwid,String[] xh){
		String sql = " delete from DW_YFJGRHQYQKB_SB where dwid="+dwid+" and xh in("+ArrayToString(xh)+")  and isNull(status,1)=1 ";
		this.getSimpleJdbcTemplate().update(sql);
		return 1;
	}
	/***
	 * 
	 * @param dwid
	 * @param ryid
	 * @param tt //附件类型
	 * @param xx //附件类型 中 的种类
	 * yy //附件类型中的种类中的序号
	 */
	public List getRyxxFjList(String dwid,String ryid,String tt,String xx,String type){
		String sql = " select * from dw_ryxx_fj where dwid="+dwid+" and ryid="+ryid+" and tt="+tt+" and xx="+xx+" and type="+type+" order by yy";
		return this.getSimpleJdbcTemplate().queryForList(sql);
	}
	/**
	 * 获取招商单位的上报标志
	 * @param dwid
	 * @return
	 */
	public int getZsdwSbbz(String dwid){
		String sql = "";
		sql = " select count(*) from dw_info_sb where dwid="+dwid;
		if(this.getSimpleJdbcTemplate().queryForInt(sql) == 0){
			return 0;
		}
		sql = " select isnull(sbbz,0) from dw_info_sb where dwid="+dwid;
		return this.getSimpleJdbcTemplate().queryForInt(sql);
	}
	
	/**
	 * 获取自动生成编号(代码)
	 * 如： KJC-****
	 */
	public String getAutoDwdm(){
		String autodwdm = String.valueOf(this.getMaxKey("DW_INFO_AUTODWDM"));
		for(int i=autodwdm.length();i<4;i++){
			autodwdm = "0"+autodwdm;
		}
		return "KJC-"+autodwdm+"";
	}
	
	public List<TreeBean> doLoadTreeByLbid(String lbid){
		String sql = "";
		sql  = "  select dictbh dm,dictname mc,'0' isc,case when len(dictbh)=3 then '0' else substring(dictbh,1,len(dictbh)-3) end as pdm ,'0' bz,'0' id from xt_dict where lbid="+lbid+" order by dictbh ";
		List<TreeBean> ls = new ArrayList();
		ls = this.getSimpleJdbcTemplate().query(sql, resultBeanMapper(TreeBean.class));
		return ls;
	}
}
