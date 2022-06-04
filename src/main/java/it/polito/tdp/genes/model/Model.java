package it.polito.tdp.genes.model;

import java.util.ArrayList;
import java.util.List;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import it.polito.tdp.genes.db.GenesDao;

public class Model {

	private GenesDao dao;
	private Graph<String, DefaultWeightedEdge> graph;
	private List<String> vertices;
	private List<String> result;
	private double max;
	
	public Model() {
		this.dao = new GenesDao();
	}
	
	public String creaGrafo() {
		this.graph = new SimpleWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		this.vertices = this.dao.getAllVertices();
		Graphs.addAllVertices(this.graph, this.vertices);
		
		for(String s: this.vertices) {
			for(String ss: this.vertices) {
				DefaultWeightedEdge edge = this.graph.getEdge(s, ss);
				if(!s.equals(ss) && edge == null) {
					double weight = this.dao.getWeight(s, ss);
					if(weight > 0) {
						edge = this.graph.addEdge(s, ss);
						this.graph.setEdgeWeight(edge, weight);
					}
				}
			}
		}
		
		return "GRAFO CREATO!\n#VERTICI: "+this.graph.vertexSet().size()+"\n#ARCHI: "+this.graph.edgeSet().size();
	}
	
	public String doStatistiche(String localization) {
		List<String> near = new ArrayList<>(Graphs.neighborListOf(this.graph, localization));
		String res = "Adiacenti a: "+localization+"\n";
		
		for(String s: near) {
			res += s + " - " + this.graph.getEdgeWeight(this.graph.getEdge(localization, s))+"\n";
		}
		
		return res;
	}
	
	public String doRicerca(String localization) {
		this.result = new ArrayList<>();
		this.max = 0;
		List<String> parziale = new ArrayList<>();
		parziale.add(localization);
		this.ricorsiva(parziale, localization, true, 0);
		
		String s = "Lunghezza cammino: "+this.max+"\n";
		
		for(String st: this.result) 
			s += st+"\n";
		
		return s;
	}
	
	public void ricorsiva(List<String> parziale, String localization, boolean more, double lenght) {
		if(!more) {
			if(lenght > this.max) {
				this.result = new ArrayList<>(parziale);
				this.max = lenght;
			}
		} else {
			List<String> near = Graphs.neighborListOf(this.graph, localization);
			List<String> possible = new ArrayList<String>();
			
			for(String s: near) {
				if(!parziale.contains(s)) {
					possible.add(s);
				}
			}
		
			if(possible.isEmpty())
				this.ricorsiva(parziale, localization, false, lenght);
			else {
				for(String st: possible) {
					parziale.add(st);
					lenght += this.graph.getEdgeWeight(this.graph.getEdge(localization, st));
					this.ricorsiva(parziale, st, true, lenght);
					lenght -= this.graph.getEdgeWeight(this.graph.getEdge(localization, st));
					parziale.remove(st);
				}
			}
		}
	}
	
	public List<String> getAllVertices() {
		if(this.vertices.isEmpty())
			return this.dao.getAllVertices();
		
		return this.vertices;
	}
}