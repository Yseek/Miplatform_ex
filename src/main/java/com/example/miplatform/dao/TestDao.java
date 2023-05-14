package com.example.miplatform.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import com.tobesoft.platform.PlatformRequest;
import com.tobesoft.platform.PlatformResponse;
import com.tobesoft.platform.data.ColumnInfo;
import com.tobesoft.platform.data.Dataset;
import com.tobesoft.platform.data.DatasetList;
import com.tobesoft.platform.data.VariableList;

@Repository
public class TestDao {

	private final DataSource dataSource;

	public TestDao(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void searchDao(HttpServletRequest request, HttpServletResponse response) {
		VariableList vl = new VariableList();
		DatasetList dl = new DatasetList();

		String sql = "SELECT SABUN , NAME , tp2.VALUE AS DEPT  FROM TB_PGSAWON tp JOIN TB_PGDEPT tp2 ON  tp.DEPT = tp2.CODE";
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			con = DataSourceUtils.getConnection(dataSource);
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();

			Dataset ds = new Dataset("js_sawon");
			ds.addColumn("sabun", ColumnInfo.COLTYPE_STRING, 20);
			ds.addColumn("name", ColumnInfo.COLTYPE_STRING, 20);
			ds.addColumn("dept", ColumnInfo.COLTYPE_STRING, 20);

			while (rs.next()) {
				int row = ds.appendRow();
				ds.setColumn(row, "sabun", rs.getString("sabun"));
				ds.setColumn(row, "name", rs.getString("name"));
				ds.setColumn(row, "dept", rs.getString("dept"));
			}

			dl.addDataset(ds);

			vl.addStr("ErrorCode", "0");
			vl.addStr("ErrorMsg", "SUCC");

		} catch (SQLException se) {
			vl.add("ErrorCode", "-1");
			vl.add("ErrorMsg", se.getMessage());
		} finally {
			try {
				if (pstmt != null)
					pstmt.close();
				if (con != null)
					con.close();
			} catch (Exception e) {

			}
		}
		try {
			PlatformResponse pRes = new PlatformResponse(response, PlatformRequest.XML, "utf-8");
			pRes.sendData(vl, dl);
		} catch (IOException ie) {
		}
	}

	public void saveDto(HttpServletRequest request, HttpServletResponse response) {
		VariableList in_vl = new VariableList();
		DatasetList in_dl = new DatasetList();
		VariableList out_vl = new VariableList();
		DatasetList out_dl = new DatasetList();
		Connection con = null;
		PreparedStatement pstmt = null;

		try {
			PlatformRequest pReq = new PlatformRequest(request, "utf-8");
			pReq.receiveData();

			in_vl = pReq.getVariableList();
			in_dl = pReq.getDatasetList();
			Dataset ds = in_dl.getDataset("input");
			String in_val = in_vl.getValueAsString("in_val");
			System.out.println(in_val);
			con = DataSourceUtils.getConnection(dataSource);

			for (int i = 0; i < ds.getRowCount(); i++) {
				StringBuilder sb = new StringBuilder();
				String row_status = ds.getRowStatus(i);
				if (row_status.equals("insert") == true) {
					sb.append("insert into TB_PGSAWON values(");
					sb.append("'" + ds.getColumnAsObject(i, "sabun") + "', ");
					sb.append("'" + ds.getColumnAsObject(i, "name") + "', ");
					sb.append(ds.getColumnAsObject(i, "dept") + ")");
				} else if (row_status.equals("update") == true) {
					sb.append("update TB_PGSAWON set ");
					sb.append("sabun='" + ds.getColumnAsObject(i, "sabun") + "', ");
					sb.append("name='" + ds.getColumnAsObject(i, "name") + "', ");
					sb.append("dept='" + ds.getColumnAsObject(i, "dept") + "'");
					sb.append(" where sabun='" + ds.getOrgBuffColumn(i, "sabun") + "'");
				}
				System.out.println(sb.toString());
				
				pstmt = con.prepareStatement(sb.toString());
				pstmt.executeUpdate();
			}

			for(int i=0; i<ds.getDeleteRowCount();i++){
				StringBuilder sb = new StringBuilder();
				String sabun = ds.getDeleteColumn(i, "sabun").getString();
				sb.append("delete from TB_PGSAWON where sabun='"+sabun+"'");

				System.out.println(sb.toString());
				pstmt = con.prepareStatement(sb.toString());
				pstmt.executeUpdate();
			}

			out_vl.addStr("ErrorCode", "0");
			out_vl.addStr("ErrorMsg", "SUCC");
			out_vl.addStr("out_var", "이게 갑니까?");

			PlatformResponse pRes = new PlatformResponse(response, PlatformRequest.XML, "utf-8");
			pRes.sendData(out_vl, out_dl);

		} catch (Exception e) {
		} finally{
			try {
				if(pstmt != null) pstmt.close();
				if(con != null) con.close();
			} catch (Exception e) {
			}
		}
	}
}
