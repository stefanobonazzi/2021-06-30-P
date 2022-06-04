package it.polito.tdp.genes.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import it.polito.tdp.genes.model.Genes;

public class GenesDao {
	
	public List<Genes> getAllGenes(){
		String sql = "SELECT DISTINCT GeneID, Essential, Chromosome FROM Genes";
		List<Genes> result = new ArrayList<Genes>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Genes genes = new Genes(res.getString("GeneID"), 
						res.getString("Essential"), 
						res.getInt("Chromosome"));
				result.add(genes);
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			throw new RuntimeException("Database error", e) ;
		}
	}
	
	public List<String> getAllVertices(){
		String sql = "SELECT DISTINCT `Localization` "
				+ "FROM `classification`";
		List<String> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				result.add(res.getString("Localization"));
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			throw new RuntimeException("Database error", e) ;
		}
	}

	public double getWeight(String l1, String l2){
		String sql = "SELECT COUNT(DISTINCT i.`Type`) AS weight "
				+ "FROM `interactions` i, `classification` c1, `classification` c2 "
				+ "WHERE (c1.`Localization` = ? AND i.`GeneID1` = c1.`GeneID` AND c2.`Localization` = ? AND c2.`GeneID` = i.`GeneID2`) "
				+ "	OR (c1.`Localization` = ? AND i.`GeneID1` = c1.`GeneID` AND c2.`Localization` = ? AND c2.`GeneID` = i.`GeneID2`)";
		double result = 0;
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, l1);
			st.setString(2, l2);
			st.setString(3, l2);
			st.setString(4, l1);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				result = res.getDouble("weight");
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			throw new RuntimeException("Database error", e) ;
		}
	}
}
